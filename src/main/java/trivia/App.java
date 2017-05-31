package trivia;
import org.javalite.activejdbc.Base;
import trivia.User;
import java.util.List;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;
//import spark.template.mustache.MustacheTemplateEngine;

public class App{
  public static void main( String[] args )
  {
    Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia", "root", "root");

    /*User u = new User();
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

    System.out.println((String)((game.getRandomCategory()).get("tCategory")));*/

    //game.play();
        
        Map map = new HashMap();
        map.put("name", "Sam");
        map.put("value", 1000);
        map.put("taxed_value", 1000 - (1000 * 0.4));
        map.put("in_ca", true);

    // home
    //get("/", () -> {} );

     get("/users", (req, res) -> {
        // crear vista users
        // listar users con las etiquetas li y ul
        // para esto usar foreach dentro de mustache
       return new ModelAndView(map, "./views/users.mustache");
      }, new MustacheTemplateEngine()
      );

     get("/user/new", (req, res) -> {
        return new ModelAndView(map, "./views/users/new.mustache");
    }, new MustacheTemplateEngine());
  

    /* Esto se encarga de crear un usuario nuevo y devuelve la vista para jugar */
    post("/users", (req, res) -> {
        Map map2 = new HashMap();
        map2.put("nickname", req.queryParams("nickname"));
        // Aca guardaras el user
        return new ModelAndView(map2, "./views/users/play.mustache");
        }, new MustacheTemplateEngine()
    );

    /*get("/", function (req, res) {
      // devuelve un html con un mensaje bienvenido, un buton jugar
      System.out.println(req.question_id);
    });

    get("/play" ...)
    // vas a pedirle al usuario su nombre para despues crear un jeugo

    post("/user" ...)

    post "/game"

    put "/game"*/
    /*Game g = new Game();
    User u = User.find("username = ?", "Maradona");
    g.set("user_id", u.id);
    Question q = g.getQuestion();
    q.answer("Boca");*/

    //User u2 = new User("juan","pepe@gmail.com","1234");
    //u2.saveIt();
    Base.close();

    /*post('/user/:id/play', (req, res) => {
        query_params.get("option")
    })*/

    }
}