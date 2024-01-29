USE closerShareLocation
USE master
DROP DATABASE closerShareLocation
CREATE DATABASE closerShareLocation
DROP TABLE IF EXISTS _user
DROP TABLE IF EXISTS token
DROP TABLE IF EXISTS post
DROP TABLE IF EXISTS comment
DROP TABLE IF EXISTS user_post_likes
DROP TABLE IF EXISTS country
DROP TABLE IF EXISTS city
DROP TABLE IF EXISTS friend



SELECT * FROM _user
SELECT * FROM post_images
SELECT * FROM image
SELECT token FROM token
SELECT * FROM post
SELECT * FROM comment
SELECT * FROM user_post_likes
SELECT * FROM user_post_watches
SELECT * FROM friend
SELECT * FROM friend_request
SELECT * FROM country
SELECT * FROM city


SELECT * FROM friend_request WHERE friend_request.user_id = 2

DELETE FROM friend_request

ALTER TABLE comment
ALTER COLUMN content ntext

ALTER TABLE comment
ADD created_time bigint


DELETE FROM token
DELETE FROM _user where id = 2
DELETE FROM country
DELETE FROM city