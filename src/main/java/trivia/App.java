package trivia;
import org.javalite.activejdbc.Base;
import trivia.User;

/**
 * Hello world!
 *
 */
public class App{
    public static void main( String[] args )
    {
      Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia", "root", "root");

      // User u = new User();
      // u.set("username", "Maradona");
      // u.set("password", "messi");
      // u.saveIt();

      Question q = new Question();
      q.set("description", "quien es el mejor?");
      q.set("answer1", "River no");
      q.set("answer2", "Boca");
      q.set("category_id",5);
      q.saveIt();

      Category c = new Category();
      c.set("tCategory","deportes");
      c.set("tCategory","deportes");
      c.set("tCategory","Historia");
      c.saveIt();

      User user = new User("Isaias","Isaias@gmail.com","123");
      user.saveIt();
      Game game = user.createGame((Long)user.get("id"));
      game.saveIt();
      //user.play();

      System.out.println((String)((game.getCategory()).get("tCategory")));
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