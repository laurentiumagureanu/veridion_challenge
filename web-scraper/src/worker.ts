import { createChannel } from './lib/amqp';
import { extract } from './lib/scraper';
import { Message } from 'amqplib';

const SCRAPE_EXCHANGE: string = process.env.SCRAPE_EXCHANGE || 'scrape_exchange';
const SCRAPE_REQUESTS_QUEUE: string = process.env.SCRAPE_REQUESTS_QUEUE || 'scrape_requests';
const SCRAPE_RESULT_ROUTING_KEY: string = process.env.SCRAPE_RESULT_ROUTING_KEY || 'result';

interface ScrapeRequest {
  id: string;
  domain: string;
}

interface ScrapeResultResponse {
  id: string;
  domain?: string;
  website?: string;
  status: 'COMPLETED' | 'FAILED';
  phoneNumbers?: string[];
  socialMediaLinks?: string[];
  address?: string | null;
}

const handleScrapeRequest = async (msg: Message): Promise<ScrapeResultResponse> => {
  try {
    const content = msg.content.toString();
    console.log(`Received message: ${content}`);
    const request: ScrapeRequest = JSON.parse(content);

    const { domain } = request;
    console.log(`Extracting ${domain}`);
    const result = await extract(domain);

    return {
      id: request.id,
      ...result
    };

  } catch (error) {
    console.error('Error:', error);
    
    // Try to get request data for error response
    let requestId = 'unknown';
    let requestDomain = 'unknown';
    
    try {
      const request: ScrapeRequest = JSON.parse(msg.content.toString());
      requestId = request.id;
      requestDomain = request.domain;
    } catch (parseError) {
      console.error('Failed to parse request for error response:', parseError);
    }
    
    return {
      id: requestId,
      domain: requestDomain,
      status: 'FAILED',
    };
  }
};

const work = async (): Promise<void> => {
  try {
    const channel = await createChannel();
    
    while (true) {
      const msg = await channel.get(SCRAPE_REQUESTS_QUEUE, { noAck: false });
      
      if (msg) {
        const result = await handleScrapeRequest(msg);
        console.log('result', result);

        if (result.status === 'FAILED') {
          console.log('Failed to scrape', result.domain);
          // Retry up to 3 times as configured in the queue
          console.log('Retrying', result.domain);
          // Reject the message and requeue it
          channel.reject(msg, true);
          channel.publish(SCRAPE_EXCHANGE, SCRAPE_RESULT_ROUTING_KEY, Buffer.from(JSON.stringify(result)));
        } else {
          console.log('Successfully scraped', result.domain);
          // If the result is successful, send the result and ack the message
          channel.publish(SCRAPE_EXCHANGE, SCRAPE_RESULT_ROUTING_KEY, Buffer.from(JSON.stringify(result)));
          channel.ack(msg);
        }
      } else {
        await new Promise(resolve => setTimeout(resolve, 1000));
      }
    }
  } catch (error) {
    console.error('Error:', error);
    process.exit(1);
  }
};

work(); 