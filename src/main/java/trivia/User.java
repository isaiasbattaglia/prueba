package trivia;
import java.util.List;
import org.javalite.activejdbc.Model;
import java.util.Map;
import java.util.HashMap;
import org.javalite.activejdbc.validation.UniquenessValidator;

public class User extends Model {
  private static Map<Integer,Integer> cache =new HashMap<Integer,Integer> ();
  static{
    validatePresenceOf("username").message("Please, provide your username");
    validatePresenceOf("password").message("Please, provide your password");
    validatePresenceOf("email").message("please, provide your email");
    validatePresenceOf("lives").message("Initialize lives");
    validatePresenceOf("total_points").message("Initialize total points");
    validatePresenceOf("correct_questions").message("Initialize correct_questions");
    validatePresenceOf("incorrect_questions").message("Initialize incorrect_questions");
    validatePresenceOf("total_questions").message("Initialize total_questions");
    validatePresenceOf("level").message("Initialize a level");
    validateWith(new UniquenessValidator("username")).message("This username is already taken.");
    validateWith(new UniquenessValidator("email")).message("This username is already taken.");
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
    validatePresenceOf("password").message("Please, provide your username");
    validatePresenceOf("email").message("please, provide your email");
    validatePresenceOf("lives").message("Initialize lives");
    validatePresenceOf("total_points").message("Initialize total points");
    validatePresenceOf("correct_questions").message("Initialize correct_questions");
    validatePresenceOf("incorrect_questions").message("Initialize incorrect_questions");
    validatePresenceOf("total_questions").message("Initialize total_questions");
    validatePresenceOf("level").message("Initialize a level");
    set("username", username);
    set("email",email);
    set("password",password);
    set("lives",3);
    set("total_points",0);
    set("level",1);
    set("correct_questions",0);
    set("incorrect_questions",0);
    set("total_questions",0);
    saveIt();
  }
  /**
  *Metodo que permite modificar las vidas de un usuario
  *@Param newLives nuevas vidas del usuario
  **/
  public void setLives(Integer newLives)
  {set("lives",newLives).saveIt();}

  /**
  *Metodo que crea un juego para un determinado usuario
  *@Return a Game
  **/
  public Game createGame()
  {//set("lives",((Integer)get("lives"))-1).saveIt();
  return new Game((Long)this.get("id"));

  }
  
  /**
  *Metodo que retorna todos los juegos que inicio (this) un jugador.
  *@Return lista de juegos de this.
  **/
  public List<Game> getGame()
  {return this.getAll(Game.class); }


  public void updateProfile(boolean correctAnswer){
    this.set("total_questions",(Integer)this.get("total_questions")+1).saveIt();
    if (correctAnswer) {
      this.set("correct_questions", (Integer) this.get("correct_questions")+1).saveIt();
      this.set("total_points",(Integer) this.get("total_points")+10).saveIt();
      Integer points= (Integer)this.get("total_points");
      Integer pointsToNextLevel=pointsToNextLevel();
      if (pointsToNextLevel.compareTo(points)<=0)
        updateLevel(points,pointsToNextLevel);    
    }
    else
      this.set("incorrect_questions", (Integer) this.get("incorrect_questions")+1).saveIt();
  }

  public Integer pointsToNextLevel(){
    return (memoFib((Integer)this.get("level")))*10;
  }

  private Integer memoFib(Integer n){
    if (!cache.containsKey(n)) {
      if(n<=1)
        cache.put(n,1);
      else
        cache.put(n,memoFib(n-1)+memoFib(n-2));
    }
    return cache.get(n);
  }

  private void updateLevel(Integer actualPoints, Integer totalPoints){
    Integer dif=actualPoints-totalPoints;
    if (dif>=0) {
      this.set("level", (Integer)this.get("level")+1).saveIt();
      this.set("total_points",dif).saveIt();
    }
    return;
  }

  public static boolean validUser(String name, String password){
    List<User> user = User.where("username = ? and password = ? ", name, password);
    return user.size()==1;
  }

  public static User getUser(String name, String password){
    List<User> ls = User.where("username = ? and password = ? ",name,password);
    if (ls.size()==0)
      throw new IllegalArgumentException("NO valid user");
    return ls.get(0);
  }

  public String username(){
    return (String)this.get("username");
  }

  public Integer level(){
    return (Integer)this.get("level");
  }
}
