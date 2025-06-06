import * as amqp from 'amqplib';

const RABBITMQ_HOST: string = process.env.RABBITMQ_HOST || 'localhost';
const RABBITMQ_PORT: number = parseInt(process.env.RABBITMQ_PORT || '5672', 10);
const RABBITMQ_USERNAME: string = process.env.RABBITMQ_USERNAME || 'guest';
const RABBITMQ_PASSWORD: string = process.env.RABBITMQ_PASSWORD || 'guest';

export const createChannel = async (): Promise<amqp.Channel> => {
    const connection = await amqp.connect({
        hostname: RABBITMQ_HOST,
        port: RABBITMQ_PORT,
        username: RABBITMQ_USERNAME,
        password: RABBITMQ_PASSWORD
    });
    return await connection.createChannel();
}; 