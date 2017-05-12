CREATE TABLE questions (
  id  int(11) auto_increment PRIMARY KEY,
  description  VARCHAR(300),
  answer1  VARCHAR(150),
  answer2  VARCHAR(150),
  answer3  VARCHAR(150),
  answer4  VARCHAR(150),
  category_id INT,
  created_at DATETIME,
  updated_at DATETIME
)ENGINE=InnoDB;
