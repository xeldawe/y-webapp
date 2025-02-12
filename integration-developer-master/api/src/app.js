const express = require('express');
const albumsRouter = require('./routes/albums');
const artistsRouter = require('./routes/artists');
const genresRouter = require('./routes/genres');
const orm = require('./models');

const app = express();

/**
 * ORM config
 */
orm.sequelize.sync();

/**
 * Routes
 */
app.use(express.json());
app.use('/albums', albumsRouter);
app.use('/artists', artistsRouter);
app.use('/genres', genresRouter);
app.get('/ping', (req, res) => {
  res.status(200).send('pong');
});

app.listen(3000, () => console.log(`Listening on port 3000`));
