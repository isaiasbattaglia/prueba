package trivia;

import org.javalite.activejdbc.Model;

public class Game extends Model {
  static{
    validatePresenceOf("user_id").message("Please, provide a user id");

  }
  public Game(){}
  public Game(Long user_id){
  	set("round",0);
  	set("user_id",user_id);
  }

  private Category getCategory(){
  	return (new Category()).randomCategory();
  }

  public Question getQuestion(){
  	Category c = getCategory();
  	return c.getQuestion();
  }
}