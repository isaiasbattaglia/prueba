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
    staticFileLocation("/public");

    before((req, res)->{
        Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia", "root", "root");
    });

    after((req, res) -> {
      Base.close();
    });

    //Home del juego
    get("/", (req,res) -> {
      Map map = new HashMap();
      return new ModelAndView(map, "./views/home.mustache");
    },new MustacheTemplateEngine());


    /*Se muestra la vista cuando alguien se desea registrar en el juego*/
    get("/registrar", (req,res) -> {
      Map map = new HashMap();
      return new ModelAndView(map, "./views/users/new.mustache");
    },new MustacheTemplateEngine());

    /* Esto se encarga de crear un usuario nuevo y vuelve la vista al logIn(Home)
    *Es un post dado a que se crea un nuevo usuario en la base de datos */
    post("/users", (req, res) -> {
      Map map2 = new HashMap();
      //Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia", "root", "root");
      map2.put("nickname", req.queryParams("nickname"));
      User u = new User(req.queryParams("nickname"),req.queryParams("Email"),req.queryParams("password"));    //Se crea un nuevo usuario.
      map2.put("lives",u.get("lives"));
      //Base.close();
      return new ModelAndView(map2, "./views/home.mustache");
      }, new MustacheTemplateEngine());


    /*Busca en la base de datos si el usuario es correcto, en caso de ser valido, crea una sesion para ese usuario
    * y muestra la vista de new game, en el caso de no ser correcto el usuario se redirecciona a la ventana login*/
    get("/games", (req,res) ->{
      Map map =new HashMap();
      //Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia", "root", "root");
      boolean valid = User.validUser(req.queryParams("nickname"),req.queryParams("password"));  //Se valida que el usuario sea correcto.
      if (valid){
          User currentUser = User.getUser(req.queryParams("nickname"),req.queryParams("password"));
          /*Se obtiene el ID del usuario*/
          Object id_O = currentUser.getId();
          String s = id_O.toString();
          Long id= Long.parseLong(s);
          /*Fin de la obtencion del ID del usuario*/
          req.session(true);    //Se crea la sesion para el usuario.
          req.session().attribute("user", id);  //Se asocia esa sesion con el id del usuario en cuestion.
          List<Game> games = Game.where("user_id = ?",id);  //Se obtiene la lista de juegos que posee ese usuario.
          games.size();       
          map.put("games", games);  
          map.put("lives",currentUser.get("lives"));
          //Base.close();
          return new ModelAndView(map,"./views/games/home.mustache");   //Retorna la vista del hombe de game
      }
      else{
        //Base.close();
        return new ModelAndView(map,"./views/home.mustache");
      }}, new MustacheTemplateEngine());
    
    /*Vista cuando se comienza un nuevo juego*/
    post("/newGame", (req,res)->{
      Map map = new HashMap();
      //Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia", "root", "root");
      Object id_O = (Object) req.session().attribute("user");
      String s = id_O.toString();
      Long id = Long.parseLong(s);
      User actualUser = User.findFirst("id = ?",id);
      System.out.println(id);
      if ((Integer)actualUser.get("lives")>0){
        Game game = new Game(id);
        actualUser.setLives((Integer)actualUser.get("lives")-1);
        Category c = game.getRandomCategory();
        map.put("category", c.get("tCategory"));
        String idc = (c.getId()).toString();
        Long id_Cat = Long.parseLong(idc);
        map.put("category_id",id_Cat);
        Base.close();
        return new ModelAndView(map,"./views/category/randomCategory.mustache");
      }
      else{
        List<Game> games = Game.where("user_id = ?",id);
        games.size();
        map.put("games",games);
        map.put("lives",actualUser.get("lives"));
        Base.close();
        return new ModelAndView(map,"./views/games/home.mustache");
      } 
    },new MustacheTemplateEngine());

    /*Debe listar los usuarios registrados*/
    get("/users", (req, res) -> {
      //Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia", "root", "root");
      List<User> users= User.findAll();
      users.get(0);
      Map map = new HashMap();
      map.put("users",users);
      //Base.close();
      return new ModelAndView(map, "./views/users/users.mustache");
    }, new MustacheTemplateEngine());

     /*Vista para crear nuevo usuario*/
    get("/user/new", (req, res) -> {
      Map map = new HashMap();
      return new ModelAndView(map, "./views/users/new.mustache");
    }, new MustacheTemplateEngine());

    post("/answer/:id", (req,res)->{
      Map map = new HashMap();
      String q = req.params("id");
      Category c = Category.findFirst("id = ?",q); 
      map.put("lives",(String)c.get("tCategory"));
      System.out.println(c.get("tCategory"));
      return new ModelAndView(map,"./views/games/home.mustache");
    },new MustacheTemplateEngine());
  
    /*Vista del juego cuando se pide una pregunta*/
    get("/questions", (req,res)->{
      //Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia", "root", "root");
      String id_c = req.queryParams("category_id");
      Long id = Long.parseLong(id_c);
      Category c = Category.findFirst("id = ?", id);
      Question q = c.getQuestion();
      List<String> ls = q.randomAnswers();
      Map map = new HashMap();
      map.put("category",req.queryParams("category"));
      map.put("Question",q.get("description"));
      map.put("answer1",ls.get(0));
      map.put("answer2",ls.get(1));
      map.put("answer3",ls.get(2));
      map.put("answer4",ls.get(3));
      map.put("qs",q);
      //Base.close();   
      return new ModelAndView(map, "./views/questions/show.mustache");
    },new MustacheTemplateEngine());

    /*post("/answer", (req, res) -> {
      Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia", "root", "root");
      User user =  User.findFirst("id = ?",req.session().attribute("user"));    //Se obtiene el usuario que esta jugando en este momento, se lo hace por su id.
      if(req.queryParams("answer").equals(RESPUESTA_CORRECTA_DE_LA_PREGUNTA_QUE_VIENE_DE_ALGUN_LADO))
        user.updateProfile(true);
      else
        user.updateProfile(false);
      c = c.randomCategory();
      Map map = new HashMap();
      return new ModelAndView(map, "./views/users.mustache");
    }, new MustacheTemplateEngine()); */
  }
}