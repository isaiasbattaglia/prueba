CREATE TABLE categories_users_table(
  user_id  INT(11),
  question_id INT(11),
  PRIMARY KEY(user_id,question_id),
  created_at DATETIME,
  updated_at DATETIME
)ENGINE=InnoDB;