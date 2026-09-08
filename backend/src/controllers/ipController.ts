import express from "express";
import IPService from "../services/ipService";

const ipController = express.Router()

const ipService = new IPService();

ipController.get('/', (req, res) => {
    res.json({serverIP: ipService.getIPAddress()})
});

export default ipController;