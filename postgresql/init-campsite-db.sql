CREATE USER campsiteapi WITH ENCRYPTED PASSWORD 'campsitepwd';
CREATE DATABASE "campsite"
    WITH OWNER "campsiteapi"
    ENCODING 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8';
