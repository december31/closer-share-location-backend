USE closerShareLocation
USE master
DROP DATABASE closerShareLocation
CREATE DATABASE closerShareLocation
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
SELECT token FROM token
SELECT * FROM post
SELECT * FROM comment
SELECT * FROM user_post_likes
SELECT * FROM user_post_watches
SELECT * FROM friend
SELECT * FROM friend_request
SELECT * FROM country
SELECT * FROM city
SELECT * FROM device


SELECT * FROM friend_request WHERE friend_request.user_id = 2

INSERT INTO [dbo].[_user]
           ([id]
           ,[avatar]
           ,[created_time]
           ,[description]
           ,[email]
           ,[gender]
           ,[last_modified]
           ,[latitude]
           ,[longitude]
           ,[name]
           ,[otp]
           ,[otp_requested_time]
           ,[password]
           ,[role])
     VALUES
           (3, avatar/avatar7.png, )
GO



-- ALTER TABLE comment
-- ALTER COLUMN content ntext

-- ALTER TABLE comment
-- ADD created_time bigint


-- DELETE FROM token
-- DELETE FROM _user where id = 2
-- DELETE FROM country
-- DELETE FROM city
-- DELETE FROM friend
-- DELETE FROM friend_request
