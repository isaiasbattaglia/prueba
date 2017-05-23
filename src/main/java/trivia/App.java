package trivia;
import org.javalite.activejdbc.Base;
import trivia.User;
import java.util.List;
/**
 * Hello world!
 *
 */
public class App{
  public static void main( String[] args )
  {
    Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia", "root", "root");

    User u = new User();
    u.set("username", "Maradona");
    u.set("password", "messi");
    u.saveIt();

    Category c = new Category();
    c.set("tCategory","deportes");
    c.set("tCategory","deportes");
    c.set("tCategory","Historia");
    c.saveIt();

    Question q = new Question();
    q.set("description", "quien es el mejor?");
    q.set("answer1", "River");
    q.set("answer2", "Boca");
    q.set("category_id",c.get("id"));
    q.saveIt();

    User user = new User("Isaias","Isaias@gmail.com","123");
    user.saveIt();
    Game game = user.createGame();
    Game game2 = user.createGame();
    Game game3 = u.createGame();
    game.saveIt();
    game2.saveIt();
    game3.saveIt();
    //user.play();
    List<Game> juegos = user.getGame();
    for(Game g: juegos)
      System.out.println((Integer)g.get("id"));

    System.out.println((String)((game.getRandomCategory()).get("tCategory")));

    game.play();
    /*Game g = new Game();
    User u = User.find("username = ?", "Maradona");
    g.set("user_id", u.id);
    Question q = g.getQuestion();
    q.answer("Boca");*/

    //User u2 = new User("juan","pepe@gmail.com","1234");
    //u2.saveIt();
    //Base.close();



    }
}