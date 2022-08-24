CREATE USER campsite_user WITH ENCRYPTED PASSWORD 'campsite_pwd';
CREATE DATABASE "campsite"
    WITH OWNER "campsite_user"
    ENCODING 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8';
