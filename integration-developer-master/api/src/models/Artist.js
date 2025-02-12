module.exports = (sequelize, DataTypes) => {
  const Artist = sequelize.define('Artist', {
    ArtistId: {
      type: DataTypes.INTEGER,
      allowNull: false,
      validate: {
        notEmpty: true,
      },
      primaryKey: true,
    },
    ArtistName: {
      type: DataTypes.STRING,
      maxLength: 255,
      allowNull: false,
      validate: {
        notEmpty: true,
      },
    },
  }, {
    timestamps: false,
  });

  Artist.associate = (models) => {
    Artist.hasMany(models.Album, {
      sourceKey: 'ArtistId', foreignKey: 'ArtistId', as: 'Artist_Album_1', constraints: false,
    });
    Artist.belongsTo(models.Album, {
      sourceKey: 'ArtistId', foreignKey: 'ArtistId', as: 'Artist_Album_2', constraints: false,
    });
    Artist.belongsToMany(models.Genre, {
      through: 'Album', foreignKey: 'ArtistId', constraints: false,
    });
  };

  return Artist;
};
