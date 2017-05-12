CREATE TABLE categories (
  id  INT(11) auto_increment PRIMARY KEY,
  tCategory ENUM('Historia','Geografia','Ciencia','Deportes','Arte','Entretenimiento','Comodin'),
  created_at DATETIME,
  updated_at DATETIME
)ENGINE=InnoDB;