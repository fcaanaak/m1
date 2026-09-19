import express from "express";
import IPService from "../services/ipService";

const ipController = express.Router()

const ipService = new IPService();

ipController.get('/', (req, res) => {

    ipService.getPublicIpAddress()
        .then(respJ => res.json({serverIp: respJ}))
        .catch(err => res.status(404).json({error: err}));

});

export default ipController;