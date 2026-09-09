import { createApp } from './app';
import { env } from './config/env';
import https from 'https';
import fs from "node:fs";

const app = createApp();

const server = https
    .createServer(
        {
            key: fs.readFileSync("secrets/key.pem"),
            cert: fs.readFileSync("secrets/cert.pem"),
        }
        ,app
    )
    .listen(env.port, () => {
      console.log(`Server listening on ${env.port}\nLink: https://localhost:${env.port}`);
    })


for (const signal of ['SIGINT', 'SIGTERM'] as const) {
  process.on(signal, () => {
    server.close(() => {
      process.exit(0);
    });
  });
}
