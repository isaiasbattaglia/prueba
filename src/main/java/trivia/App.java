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

public class App{
  public static void main( String[] args ){
    Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia", "root", "root");

    //Home del juego
    get("/", (req,res) -> {
      Map map = new HashMap();
			return new ModelAndView(map, "./views/home.html");
    },new MustacheTemplateEngine());

    /*Se muestra la vista cuando alguien se desea registrar en el juego*/
    post("/registrar", (req,res) -> {
      Map map = new HashMap();
      return new ModelAndView(map, "./views/users/new.mustache");
    },new MustacheTemplateEngine());

    /*Se muestra la vista cuando alguien se desea loguear,
    *Este metodo hace un post en /games*/
    post("/login", (req,res) ->{
    	Map map = new HashMap();
      return new ModelAndView(map, "./views/users/LogIn.mustache");
    },new MustacheTemplateEngine());

    /*Busca en la base de datos si el usuario es correcto, en caso de ser valido, crea una sesion para ese usuario
    * y muestra la vista new game, en el caso de no ser correcto el usuario se redirecciona a la ventana login*/
    post("/games", (req,res) ->{
      Map map =new HashMap();
      Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia", "root", "root");
      boolean valid = User.validUser(req.queryParams("nickname"),req.queryParams("password"));  //Se valida que el usuario sea correcto.
      if (valid){
          User currentUser = User.getUser(req.queryParams("nickname"),req.queryParams("password"));
          Object id_O = currentUser.getId();
          String s = id_O.toString();
          Long id= Long.parseLong(s);
          req.session(true);
          req.session().attribute("user", id);
          Base.close();
          return new ModelAndView(map,"./views/games/home.mustache");
      }
      else{
        Base.close();
        return new ModelAndView(map,"./views/users/LogIn.mustache");
      }
      }, new MustacheTemplateEngine());
    
    /*Vista cuando se comienza un nuevo juego*/
    post("/newGame", (req,res)->{
      Map map = new HashMap();
      Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia", "root", "root");
      Game game = new Game(req.session().attribute("user"));
      Base.close();
      return new ModelAndView(map,"./views/users/new.mustache");
    },new MustacheTemplateEngine());


    /*Debe listar los usuarios registrados*/
    get("/users", (req, res) -> {
      // crear vista users
      // listar users con las etiquetas li y ul
      // para esto usar foreach dentro de mustache
      Map map = new HashMap();
      return new ModelAndView(map, "./views/users.mustache");
    }, new MustacheTemplateEngine());

     /*Vista para crear nuevo usuario*/
    get("/user/new", (req, res) -> {
      Map map = new HashMap();
      return new ModelAndView(map, "./views/users/new.mustache");
    }, new MustacheTemplateEngine());
  
    /* Esto se encarga de crear un usuario nuevo y devuelve la vista para jugar */
    post("/users", (req, res) -> {
      Map map2 = new HashMap();
      Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia", "root", "root");
      map2.put("nickname", req.queryParams("nickname"));
      User u=new User();
      u.set("username", req.queryParams("nickname")).saveIt();
      u.set("email", req.queryParams("Email")).saveIt();
      u.set("password", req.queryParams("password")).saveIt();
      Base.close();
      return new ModelAndView(map2, "./views/users/play.mustache");
      }, new MustacheTemplateEngine());

    /*Vista del juego cuando se pide una pregunta*/
    get("/games", (req,res)->{
    	Base.open();
    	Category c = new Category();
    	c = c.randomCategory();
    	Question q = c.getQuestion();
    	Map map = new HashMap();
    	map.put("Question",q.get("description"));
    	map.put("answer1",q.get("answer1"));
    	map.put("answer2",q.get("answer2"));
    	map.put("answer3",q.get("answer3"));
    	map.put("answer4",q.get("answer4"));
    	Base.close();  	
    	return new ModelAndView(map, "./views/questions/show.mustache");
    },new MustacheTemplateEngine());
  }
}