module.exports = (sequelize, DataTypes) => {
  const Genre = sequelize.define('Genre', {
    GenreId: {
      type: DataTypes.INTEGER,
      allowNull: false,
      validate: {
        notEmpty: true,
      },
      primaryKey: true,
    },
    GenreName: {
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

  Genre.associate = (models) => {
    Genre.belongsToMany(models.Artist, {
      through: 'Album', foreignKey: 'GenreId', constraints: false,
    });
  };

  return Genre;
};
