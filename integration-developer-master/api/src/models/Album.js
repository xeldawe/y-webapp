module.exports = (sequelize, DataTypes) => {
  const Album = sequelize.define('Album', {
    AlbumId: {
      type: DataTypes.INTEGER,
      allowNull: false,
      primaryKey: true,
      autoIncrement: true,
    },
    AlbumName: {
      type: DataTypes.STRING,
      maxLength: 255,
      allowNull: false,
      validate: {
        notEmpty: true,
      },
    },
    DateReleased: {
      type: DataTypes.STRING,
      maxLength: 24,
      allowNull: false,
      validate: {
        notEmpty: true,
      },
    },
    ArtistId: {
      type: DataTypes.INTEGER,
      allowNull: false,
      validate: {
        notEmpty: true,
      },
    },
    GenreId: {
      type: DataTypes.INTEGER,
      allowNull: false,
      validate: {
        notEmpty: true,
      },
    },
  }, {
    timestamps: false,
  });

  Album.associate = (models) => {
    Album.hasOne(models.Artist, {
      sourceKey: 'ArtistId', foreignKey: 'ArtistId', as: 'Album_Artist_1', constraints: false,
    });
    Album.belongsTo(models.Artist, {
      sourceKey: 'ArtistId', foreignKey: 'ArtistId', as: 'Album_Artist_2', constraints: false
    });
  };

  return Album;
};
