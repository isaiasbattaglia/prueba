CREATE TABLE categories_games(
  id  int(11) auto_increment PRIMARY KEY,
  game_id  INT(11),
  category_id INT(11),
  created_at DATETIME,
  updated_at DATETIME
)ENGINE=InnoDB;