package trivia;
import org.javalite.activejdbc.Model;

public class User extends Model {
  static{
    validatePresenceOf("username").message("Please, provide your username");
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

}
