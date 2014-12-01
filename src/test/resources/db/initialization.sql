CREATE DATABASE IF NOT EXISTS cococaca;

USE cococaca;

CREATE TABLE IF NOT EXISTS T_SUBSCRIBER (ID INT AUTO_INCREMENT PRIMARY KEY, NAME VARCHAR (128), PASSWORD VARCHAR (128), GENDER VARCHAR (32), AVATAR_ID VARCHAR (128));

CREATE TABLE IF NOT EXISTS TC_FOLLOWSHIP (FOLLOWEE_ID VARCHAR (128), FOLLOWER_ID VARCHAR (128));

CREATE TABLE IF NOT EXISTS T_POST (ID VARCHAR (128), AUTHOR_ID VARCHAR (128), CONTENT_ID VARCHAR (128), DESCRIPTION VARCHAR (128), DATESTAMP VARCHAR (128));

CREATE TABLE IF NOT EXISTS T_DANMUKU (ID INT AUTO_INCREMENT PRIMARY KEY, AUTHOR_ID VARCHAR (128), CONTENT VARCHAR (128), DATESTAMP VARCHAR (128), POST_ID VARCHAR(128));

CREATE TABLE IF NOT EXISTS T_IMG (ID VARCHAR (128), FETCH_KEY VARCHAR (128));

CREATE TABLE IF NOT EXISTS T_FILE_STORE (ID INT AUTO_INCREMENT PRIMARY KEY, BIN_DATA MediumBlob);


CREATE USER 'quantumlabs'@'localhost' IDENTIFIED BY 'root';

GRANT ALL ON COCOCACA.* TO 'quantumlabs'@'localhost';

FLUSH PRIVILEGES;

