CREATE DATABASE campsite CHARACTER SET utf8;

CREATE USER 'campsite_user'@'%' IDENTIFIED BY 'campsite_pwd';
GRANT ALL PRIVILEGES ON campsite.* TO 'campsite_user'@'%';
FLUSH PRIVILEGES;
