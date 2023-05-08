CREATE TABLE productions (
  id int(5) unsigned not null auto_increment,
  title nvarchar(30) not null,
  content text not null,
  thumbnail nvarchar(2083) not null,
  primary key (id)
);
CREATE TABLE images (
  id int(5) unsigned not null auto_increment,
  url nvarchar(2083) not null,
  alt nvarchar(30) not null,
  primary key (id)
);
CREATE TABLE production_images (
  production_id int(5) unsigned not null,
  image_id int(5) unsigned not null
);
CREATE TABLE users (
  id int(5) unsigned not null auto_increment,
  name nvarchar(15) not null,
  password nvarchar(30) not null,
  primary key (id)
);
CREATE TABLE pages (
  id int(5) unsigned not null auto_increment,
  content_type nvarchar(10) not null,
  content text not null,
  primary key (id),
  unique(content_type)
);