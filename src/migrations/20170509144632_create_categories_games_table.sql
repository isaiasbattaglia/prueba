CREATE TABLE categories_games(
  game_id  INT(11),
  category_id INT(11),
  PRIMARY KEY(game_id,category_id),
  created_at DATETIME,
  updated_at DATETIME
)ENGINE=InnoDB;