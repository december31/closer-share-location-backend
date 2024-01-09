USE closerShareLocation
USE master
DROP DATABASE closerShareLocation
CREATE DATABASE closerShareLocation
CREATE DATABASE screencall
DROP TABLE IF EXISTS _user
DROP TABLE IF EXISTS token
DROP TABLE IF EXISTS post
DROP TABLE IF EXISTS comment
DROP TABLE IF EXISTS user_post_likes




SELECT * FROM _user
SELECT * FROM post_images
SELECT * FROM image
SELECT token FROM token

DELETE FROM token
DELETE FROM _user where id = 2
