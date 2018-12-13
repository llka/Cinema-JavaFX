-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema cinema
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema cinema
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `cinema` DEFAULT CHARACTER SET utf8 ;
USE `cinema` ;

-- -----------------------------------------------------
-- Table `cinema`.`contact`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cinema`.`contact` (
  `contact_id` INT(11) NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(45) NOT NULL,
  `last_name` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  `password` VARCHAR(45) NOT NULL,
  `role` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`contact_id`),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 4
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cinema`.`film`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cinema`.`film` (
  `film_id` INT(11) NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(45) NOT NULL,
  `duration` INT(11) NULL DEFAULT NULL,
  `tickets_left_count` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`film_id`),
  UNIQUE INDEX `title_UNIQUE` (`title` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cinema`.`shedule`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cinema`.`shedule` (
  `shedule_id` INT(11) NOT NULL AUTO_INCREMENT,
  `film_id` INT(11) NOT NULL,
  `date` DATETIME NOT NULL,
  `ticket_cost` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`shedule_id`),
  INDEX `film_key_idx` (`film_id` ASC),
  CONSTRAINT `film_key`
    FOREIGN KEY (`film_id`)
    REFERENCES `cinema`.`film` (`film_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cinema`.`ticket`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cinema`.`ticket` (
  `ticket_id` INT(11) NOT NULL AUTO_INCREMENT,
  `place_number` INT(11) NULL DEFAULT NULL,
  `cost` DECIMAL(10,2) NULL DEFAULT NULL,
  `shedule_id` INT(11) NOT NULL,
  PRIMARY KEY (`ticket_id`),
  INDEX `fk_ticket_shedule1_idx` (`shedule_id` ASC),
  CONSTRAINT `fk_ticket_shedule1`
    FOREIGN KEY (`shedule_id`)
    REFERENCES `cinema`.`shedule` (`shedule_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 10
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cinema`.`contact_has_ticket`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cinema`.`contact_has_ticket` (
  `contact_contact_id` INT(11) NOT NULL,
  `ticket_ticket_id` INT(11) NOT NULL,
  PRIMARY KEY (`contact_contact_id`, `ticket_ticket_id`),
  INDEX `fk_contact_has_ticket_ticket1_idx` (`ticket_ticket_id` ASC),
  INDEX `fk_contact_has_ticket_contact1_idx` (`contact_contact_id` ASC),
  CONSTRAINT `fk_contact_has_ticket_contact1`
    FOREIGN KEY (`contact_contact_id`)
    REFERENCES `cinema`.`contact` (`contact_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_contact_has_ticket_ticket1`
    FOREIGN KEY (`ticket_ticket_id`)
    REFERENCES `cinema`.`ticket` (`ticket_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
