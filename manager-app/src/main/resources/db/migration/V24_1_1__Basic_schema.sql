CREATE SCHEMA IF NOT EXISTS user_management;

CREATE TABLE user_management.t_user(
  id SERIAL PRIMARY KEY ,
  c_username varchar NOT NULL CHECK ( length(trim(c_username)) > 0 ) unique,
  c_password varchar
);

CREATE TABLE user_management.t_authority(
  id SERIAL PRIMARY KEY ,
  c_authority varchar NOT NULL CHECK ( length(trim(c_authority)) > 0) UNIQUE
);

CREATE TABLE  user_management.t_user_authority(
    id SERIAL PRIMARY KEY ,
    id_user INT NOT NULL REFERENCES user_management.t_user(id),
    id_authority INT NOT NULL REFERENCES user_management.t_authority(id),
    CONSTRAINT uk_user_authority UNIQUE (id_user, id_authority)
);
