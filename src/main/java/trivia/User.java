package trivia;
import org.javalite.activejdbc.Model;

public class User extends Model {
  static{
    validatePresenceOf("username").message("Please, provide your username");
    //validatePresenceOf("password").message("Please, provide your password");
    //validateRange("lives", 0, 999).message("lives cannot be less than " + 0 + " or more than " + 999);
  }
  public User(){}
  public User(String username, String email, String password){
    validatePresenceOf("username").message("Please, provide your username");
    set("username", username);
    set("email",email);
    set("password",password);
    set("lives",3);
    set("total_points",0);
    set("level",1);
    set("correct_questions",0);
    set("incorrect_questions",0);
  }
  public void setLives(int newLives){
    set("lives",newLives);
  }
  public Game createGame(Long user_id){
    return new Game(user_id);
  }
}
