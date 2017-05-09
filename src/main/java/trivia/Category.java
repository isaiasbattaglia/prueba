package trivia;

import org.javalite.activejdbc.Model;

public class Category extends Model {
  static{
    validatePresenceOf("username").message("Please, provide your username");
  }
}