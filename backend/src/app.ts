import express, { type Express } from 'express';

import IPService from "./services/ipService";
import NameService from "./services/nameService";
import TimeService from "./services/timeService";

// Things completed
// 1. Server IP
// 2. Full name
//
// TODO:
//
// 1. Get the date and time of request in server time


export function createApp(): Express {
  const app = express();

  const ipService = new IPService();
  const nameService = new NameService();
  const timeService = new TimeService()

  app.get('/health', (_req, res) => {
    res.json({ status: 'ok' });
  });

  app.get('/address', (_req, res,next) => {
    const serverIP = ipService.getIPAddress();
    serverIP ? res.json({ serverIP: ipService.getIPAddress()}) : res.status(500).json({ error: 'Error when getting server IP' });
  })

  app.get('/name', (_req, res) => {
    res.json({name: nameService.getFullName()})
  })

  app.get('/time', (_req, res) => {
    res.json({time:timeService.getTime()})
  })

  app.use((_req, res) => {
    res.status(404).json({ error: 'Not Found' });
  });

  return app;
}
