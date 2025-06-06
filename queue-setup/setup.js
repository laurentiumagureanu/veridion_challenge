const amqp = require('amqplib');

const RETRY_LIMIT = process.env.RETRY_LIMIT || 1;

const SCRAPE_EXCHANGE = process.env.SCRAPE_EXCHANGE || 'scrape_exchange';
const SCRAPE_DEAD_LETTER_QUEUE = process.env.SCRAPE_DEAD_LETTER_QUEUE || 'scrape_dead_letter';
const SCRAPE_REQUESTS_QUEUE = process.env.SCRAPE_REQUESTS_QUEUE || 'scrape_requests';
const SCRAPE_RESULTS_QUEUE = process.env.SCRAPE_RESULTS_QUEUE || 'scrape_results';

const SCRAPE_REQUEST_ROUTING_KEY = process.env.SCRAPE_REQUEST_ROUTING_KEY || 'request';
const SCRAPE_DEAD_LETTER_ROUTING_KEY = process.env.SCRAPE_DEAD_LETTER_ROUTING_KEY || 'dead_letter';
const SCRAPE_RESULT_ROUTING_KEY = process.env.SCRAPE_RESULT_ROUTING_KEY || 'result';

async function setupQueues() {
  try {
    const rabbitmqHost = process.env.RABBITMQ_HOST || 'localhost';
    const rabbitmqPort = process.env.RABBITMQ_PORT || 5672;
    const rabbitmqUsername = process.env.RABBITMQ_USERNAME || 'guest';
    const rabbitmqPassword = process.env.RABBITMQ_PASSWORD || 'guest';
    const connection = await amqp.connect({
        hostname: rabbitmqHost,
        port: rabbitmqPort,
        username: rabbitmqUsername,
        password: rabbitmqPassword
    });
    const channel = await connection.createChannel();

    await channel.assertQueue(SCRAPE_DEAD_LETTER_QUEUE, {
      durable: true
    });

    await channel.assertQueue(SCRAPE_REQUESTS_QUEUE, {
      durable: true,
      deadLetterExchange: SCRAPE_EXCHANGE,
      deadLetterRoutingKey: SCRAPE_DEAD_LETTER_ROUTING_KEY,
      arguments: {
        'x-queue-type': 'quorum',
        'x-dead-letter-exchange': SCRAPE_EXCHANGE,
        'x-dead-letter-routing-key': SCRAPE_DEAD_LETTER_ROUTING_KEY,
        'x-delivery-limit': RETRY_LIMIT
      }
    });

    await channel.assertQueue(SCRAPE_RESULTS_QUEUE, {
        durable: true
    });

    await channel.assertExchange(SCRAPE_EXCHANGE, 'direct', {
        durable: true
    });

    await channel.bindQueue(SCRAPE_REQUESTS_QUEUE, SCRAPE_EXCHANGE, SCRAPE_REQUEST_ROUTING_KEY);
    await channel.bindQueue(SCRAPE_RESULTS_QUEUE, SCRAPE_EXCHANGE, SCRAPE_RESULT_ROUTING_KEY);
    await channel.bindQueue(SCRAPE_DEAD_LETTER_QUEUE, SCRAPE_EXCHANGE, SCRAPE_DEAD_LETTER_ROUTING_KEY);

    await connection.close();
  } catch (error) {
    console.error('Error setting up queues:', error);
    process.exit(1);
  }
}

setupQueues(); 