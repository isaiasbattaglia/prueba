CREATE TABLE games_questions(
  id  int(11) auto_increment PRIMARY KEY,
  game_id  INT(11),
  question_id INT(11),
  created_at DATETIME,
  updated_at DATETIME
)ENGINE=InnoDB;