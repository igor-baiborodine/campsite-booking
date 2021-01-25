CREATE DATABASE campsite CHARACTER SET utf8;

CREATE USER 'campsite'@'%' IDENTIFIED BY 'campsite';
GRANT ALL PRIVILEGES ON campsite.* TO 'campsite'@'%';
FLUSH PRIVILEGES;

