const express = require('express');
const Joi = require('joi');

const router = express.Router();
const { Album, Artist, Sequelize } = require('../models');

router.get('/', (req, res) => {
  // Handle case without query parameters
  if (req.query === undefined
    || (!req.query.hasOwnProperty('artistId') && !req.query.hasOwnProperty('artistName'))
  ) {
    return res.status(400).send('You must define a query parameter. E.g. artistId or artistName');
  };

  // Validation
  const schema = Joi.object({
    artistId: Joi.number().allow(''),
    artistName: Joi.string().allow(''),
  });
  const validation = schema.validate(req.query);
  if (validation.error) {
    return res.status(400).send(validation.error.details[0].message);
  }

  // Handle case with 'artistId' and/or 'artistName' query parameters.
  let where = {}; // Defaults to 'without query parameters'
  if (req.query.hasOwnProperty('artistId')) where.ArtistId = req.query.artistId;
  if (req.query.hasOwnProperty('artistName')) where.ArtistName = req.query.artistName;
  const options = {
    attributes: [
      'AlbumId',
      'AlbumName',
      [Sequelize.literal('LEFT(DateReleased, 10)'), 'DateReleased'],
      'ArtistId',
      'GenreId',
    ],
    include: [{
      model: Artist,
      as: 'Album_Artist_2',
      foreignKey: 'ArtistId',
      required: true,
      where: where,
      attributes: [],
    }],
  };
  Album.findAll(options).then((albums) => {
    res.status(200).send(albums);
  });
});

router.post('/', (req, res) => {
  // Validation
  const schema = Joi.object({
    AlbumName: Joi.string().required(),
    DateReleased: Joi.string().pattern(/([12]\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01]))/).required(),
    ArtistId: Joi.number().required(),
    GenreId: Joi.number().required(),
  });
  const validation = schema.validate(req.body);
  if (validation.error) {
    res.status(400).send(validation.error.details[0].message);
    return;
  }

  Album.create(req.body).then((album) => {
    res.status(201).send(album.toJSON());
  }).catch((err) => {
    console.log(err);
    res.status(500).send('Internal Server Error. Stuff like this happens during demos...');
  });
});

module.exports = router;
