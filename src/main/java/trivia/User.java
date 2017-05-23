package trivia;
import java.util.List;
import org.javalite.activejdbc.Model;

public class User extends Model {
  static{
    validatePresenceOf("username").message("Please, provide your username");
    //validatePresenceOf("password").message("Please, provide your password");
    //validateRange("lives", 0, 999).message("lives cannot be less than " + 0 + " or more than " + 999);
  }
  /**
  *Constructor de la clase User
  **/
  public User(){}
  /**
  *Constructor de la clase User
  *@Param username=nombre de usuario, email=Correo electronico, password=Contraseña
  **/
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
  /**
  *Metodo que permite modificar las vidas de un usuario
  *@Param newLives nuevas vidas del usuario
  **/
  public void setLives(int newLives)
  {set("lives",newLives);}

  /**
  *Metodo que crea un juego para un determinado usuario
  *@Return a Game
  **/
  public Game createGame()
  {return new Game((Long)this.get("id"));}
  
  /**
  *Metodo que retorna todos los juegos que inicio (this) un jugador.
  *@Return lista de juegos de this.
  **/
  public List<Game> getGame()
  {return this.getAll(Game.class); }

}
