USE closerShareLocation
-- USE master
-- DROP DATABASE closerShareLocation
-- CREATE DATABASE closerShareLocation
-- DROP TABLE IF EXISTS _user
-- DROP TABLE IF EXISTS token
-- DROP TABLE IF EXISTS post
-- DROP TABLE IF EXISTS comment
-- DROP TABLE IF EXISTS user_post_likes
-- DROP TABLE IF EXISTS country
-- DROP TABLE IF EXISTS city
-- DROP TABLE IF EXISTS friend
-- DROP TABLE IF EXISTS device



SELECT TOP(1000) * FROM _user
SELECT * FROM post_images
SELECT * FROM image
SELECT * FROM token
SELECT * FROM post
SELECT * FROM comment
SELECT * FROM user_post_likes
SELECT * FROM user_post_watches
SELECT * FROM friend
SELECT * FROM friend_request
SELECT * FROM country
SELECT * FROM city
SELECT * FROM device
SELECT * FROM message

SELECT * FROM friend_request WHERE friend_request.user_id = 2

SELECT * FROM message WHERE (sender_id = 1 AND receiver_id = 314) OR (sender_id = 314 AND receiver_id = 1) ORDER BY time desc

-- DELETE FROM friend WHERE friend_id > 2 OR user_id > 200
-- DELETE FROM friend_request WHERE requestor_id > 200
-- DELETE FROM post WHERE user_id > 200
-- DELETE FROM token where user_id > 200
-- DELETE FROM device where user_id > 200
-- DELETE FROM _user WHERE id > 200

SELECT * FROM _user
WHERE name LIKE 'u%'
SELECT * FROM _user
WHERE email LIKE 'u%'
SELECT * FROM _user
WHERE address LIKE 'u%'

SELECT CAST(CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END AS BIT) as bool
FROM _user WHERE id = 1;

ALTER TABLE _user
ALTER COLUMN address NVARCHAR(255)

-- ALTER TABLE comment
-- ALTER COLUMN content ntext

-- ALTER TABLE comment
-- ADD created_time bigint

select * from post where user_id = 502

-- DELETE FROM device where user_id = 502
-- DELETE FROM token where user_id = 502
-- DELETE FROM friend where user_id = 502 or friend_id = 502
-- DELETE FROM friend_request where user_id = 502 or requestor_id = 502
-- DELETE FROM message WHERE receiver_id = 502 or sender_id = 502
-- DELETE FROM post where user_id = 502
-- DELETE FROM user_post_likes WHERE post_id >= 352
-- DELETE FROM [image] WHERE post_id >= 352
-- DELETE FROM _user where id = 502

-- DELETE FROM country
-- DELETE FROM city
-- DELETE FROM friend
-- DELETE FROM friend_request
