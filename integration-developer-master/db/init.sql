-- ---------------
-- create database
-- ---------------

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE="ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION";

CREATE SCHEMA IF NOT EXISTS `music` DEFAULT CHARACTER SET utf8;

CREATE TABLE IF NOT EXISTS `music`.`Genres` (
  `GenreId` INT(11) NOT NULL AUTO_INCREMENT,
  `GenreName` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`GenreId`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS `music`.`Artists` (
  `ArtistId` INT(11) NOT NULL AUTO_INCREMENT,
  `ArtistName` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`ArtistId`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS `music`.`Albums` (
  `AlbumId` INT(11) NOT NULL AUTO_INCREMENT,
  `AlbumName` VARCHAR(255) NOT NULL,
  `DateReleased` DATETIME NOT NULL,
  `ArtistId` INT(11) NOT NULL,
  `GenreId` INT(11) NOT NULL,
  PRIMARY KEY (`AlbumId`),
  INDEX `fk_Albums_Genre_idx` (`GenreId` ASC) VISIBLE,
  INDEX `fk_Albums_Artist1_idx` (`ArtistId` ASC) VISIBLE,
  CONSTRAINT `fk_Albums_Genre`
    FOREIGN KEY (`GenreId`)
    REFERENCES `music`.`Genres` (`GenreId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Albums_Artist1`
    FOREIGN KEY (`ArtistId`)
    REFERENCES `music`.`Artists` (`ArtistId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- -------------
-- seed database
-- -------------

-- seed Artists table
INSERT INTO `music`.`Artists` (`ArtistName`) VALUES
    ("Tears for Fears"),
    ("The Rolling Stones"),
    ("Billy Idol"),
    ("Depeche Mode"),
    ("U2"),
    ("David Bowie"),
    ("New Order"),
    ("Culture Club"),
    ("R.E.M"),
    ("Tom Waits"),
    ("Pink Floyd"),
    ("UB40"),
    ("Metallica"),
    ("Bob Dylan"),
    ("Herbie Hancock"),
    ("The Police");

-- seed Genres table
INSERT INTO `music`.`Genres` (`GenreName`) VALUES
    ("new wave"),
    ("rock"),
    ("hard rock"),
    ("synth-pop"),
    ("alternative rock"),
    ("experimental rock"),
    ("art rock"),
    ("reggae"),
    ("thrash metal"),
    ("heartland rock"),
    ("electro-funk"); 

-- seed Albums table
INSERT INTO `music`.`Albums` (`AlbumName`, `DateReleased`, `ArtistId`, `GenreId`) VALUES
    ("The Hurting", "1983-03-07", 1, 1),
    ("Undercover", "1983-11-07", 2, 2),
    ("Rebel Yell", "1983-11-10", 3, 3),
    ("Construction Time Again", "1983-08-22", 4, 4),
    ("Under a Blood Red Sky", "1983-11-21", 5, 2),
    ("Let's Dance", "1983-04-14", 6, 1),
    ("Power, Corruption & Lies", "1983-05-02", 7, 4),
    ("Colour by Numbers", "1983-10-10", 8, 1),
    ("Murmur", "1983-04-12", 9, 5),
    ("Swordfishtrombones", "1983-09-01", 10, 6),
    ("War", "1983-02-28", 5, 2),
    ("The Final Cut", "1983-03-21", 11, 7),
    ("Labour of Love", "1983-09-12", 12, 8),
    ("Kill 'Em All", "1983-07-25", 13, 9),
    ("Infidels", "1983-10-27", 14, 10),
    ("Future Shock", "1983-08-01", 15, 11),
    ("Synchronicity", "1983-06-17", 16, 2);
