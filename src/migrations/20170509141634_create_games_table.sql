CREATE TABLE games (
  id INT(11) auto_increment PRIMARY KEY,
  round INT,
  user_id INT(11),
  state ENUM('En_proceso','Finalizada'),
  created_at DATETIME,
  updated_at DATETIME
)ENGINE=InnoDB;
