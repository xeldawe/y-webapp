const express = require('express');
const { Genre } = require('../models');

const router = express.Router();

router.get('/', (req, res) => {
  Genre.findAll().then((genres) => {
    res.status(200).send(genres);
  });
});

module.exports = router;
