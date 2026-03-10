CREATE TABLE IF NOT EXISTS users (
  id INTEGER AUTO_INCREMENT  PRIMARY KEY,
  name VARCHAR(250) NOT NULL,
  email VARCHAR(250) NOT NULL,
  login VARCHAR(250) NOT NULL UNIQUE,
  birthday DATE
);

CREATE TABLE IF NOT EXISTS films (
  id INTEGER AUTO_INCREMENT  PRIMARY KEY,
  name VARCHAR(250) NOT NULL,
  description VARCHAR(500) NOT NULL,
  duration INTEGER NOT NULL,
  releaseDate DATE,
  mpa_id INTEGER
);

CREATE TABLE IF NOT EXISTS genres (
  genre_id INTEGER AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(250) NOT NULL
);

CREATE TABLE IF NOT EXISTS genre_films (
  genre_id INTEGER,
  film_id INTEGER
);

CREATE TABLE IF NOT EXISTS film_likes (
  user_id INTEGER,
  film_id INTEGER
);

CREATE TABLE IF NOT EXISTS friendship (
  user_id INTEGER,
  friend_id INTEGER,
  status BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS mpa (
  mpa_id INTEGER AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(250) NOT NULL,
  description VARCHAR(500) NOT NULL
);

ALTER TABLE friendship
    ADD FOREIGN KEY (user_id)
    REFERENCES users(id);

ALTER TABLE friendship
    ADD FOREIGN KEY (friend_id)
    REFERENCES users(id);

ALTER TABLE genre_films
    ADD FOREIGN KEY (genre_id)
    REFERENCES genres(genre_id) ON DELETE CASCADE;

ALTER TABLE genre_films
    ADD FOREIGN KEY (film_id)
    REFERENCES films(id) ON DELETE CASCADE;

ALTER TABLE film_likes
    ADD FOREIGN KEY (user_id)
    REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE film_likes
    ADD FOREIGN KEY (film_id)
    REFERENCES films(id) ON DELETE CASCADE;

ALTER TABLE films
    ADD FOREIGN KEY (mpa_id)
    REFERENCES mpa(mpa_id) ON DELETE SET NULL;

ALTER TABLE films
    ADD FOREIGN KEY (mpa_id)
    REFERENCES mpa(mpa_id) ON DELETE SET NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uq_film_release ON films
(
    name, releaseDate
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_film_likes ON film_likes
(
    user_id, film_id
);

