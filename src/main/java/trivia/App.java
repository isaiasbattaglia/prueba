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

    post("/login", (req,res)->{
      Map map = new HashMap();
      if (req.session().attribute("user")==null) {
        User currentUser = User.getUser(req.queryParams("nickname"),req.queryParams("password"));
        /*Se obtiene el ID del usuario*/
        Object id_O = currentUser.getId();
        String s = id_O.toString();
        Long id= Long.parseLong(s);
        /*Fin de la obtencion del ID del usuario*/
        req.session(true);    //Se crea la sesion para el usuario.
        req.session().attribute("user", id);  //Se asocia esa sesion con el id del usuario en cuestion.
        req.session().attribute("correct_answer",0);
        List<Game> games = Game.where("user_id = ? and state=?",id,"En_proceso");  //Se obtiene la lista de juegos que posee ese usuario.
        games.size();       
        map.put("games", games);  
        map.put("lives",currentUser.get("lives"));
      }
      return new ModelAndView(map,"./views/games/home.mustache");
    },new MustacheTemplateEngine());


    /*Se muestra la vista cuando alguien se desea registrar en el juego*/
    get("/registrar", (req,res) -> {
      Map map = new HashMap();
      return new ModelAndView(map, "./views/users/new.mustache");
    },new MustacheTemplateEngine());
   
    get("/logout", (req,res)->{
      Map map = new HashMap();
      if (req.session().attribute("user")!=null)
        req.session(false);
      return new ModelAndView(map, "/.views/home.mustache");
    },new MustacheTemplateEngine());

    /* Esto se encarga de crear un usuario nuevo y vuelve la vista al logIn(Home)
    *Es un post dado a que se crea un nuevo usuario en la base de datos */
    post("/users", (req, res) -> {
      Map map2 = new HashMap();
      map2.put("nickname", req.queryParams("nickname"));
      User u = new User(req.queryParams("nickname"),req.queryParams("Email"),req.queryParams("password"));    //Se crea un nuevo usuario.
      return new ModelAndView(map2, "./views/home.mustache");
      }, new MustacheTemplateEngine());

    /*Busca en la base de datos si el usuario es correcto, en caso de ser valido, crea una sesion para ese usuario
    * y muestra la vista de new game, en el caso de no ser correcto el usuario se redirecciona a la ventana login*/
    get("/games", (req,res) ->{
      Map map =new HashMap();
      Object id_O = (Object) req.session().attribute("user");
      String s = id_O.toString();
      Long id = Long.parseLong(s);
      List<Game> games = Game.where("user_id = ? and state=?",id,"En_proceso");  //Se obtiene la lista de juegos que posee ese usuario.
      User actualUser = User.findFirst("id = ?",id);
      games.size();       
      map.put("games", games);  
      map.put("lives",actualUser.get("lives"));
      return new ModelAndView(map,"./views/games/home.mustache");   //Retorna la vista del hombe de game      
      }, new MustacheTemplateEngine());
    
    /*Meotod que permite crear un juego
    *Se crea un nuevo juego, si el usuario tiene vidas para hacerlo, caso contrario no se lo permite*/
    post("/newGame", (req,res)->{
      Map map = new HashMap();
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
        String idg = (game.getId()).toString();
        map.put("game_id",idg);
        return new ModelAndView(map,"./views/category/randomCategory.mustache");
      }
      else{
        List<Game> games = Game.where("user_id = ? and state=?",id,"En_proceso");
        games.size();
        map.put("games",games);
        map.put("lives",actualUser.get("lives"));
        return new ModelAndView(map,"./views/games/home.mustache");
      } 
    },new MustacheTemplateEngine());

    /*Debe listar los usuarios registrados*/
    get("/users", (req, res) -> {
      List<User> users= User.findAll();
      users.get(0);
      Map map = new HashMap();
      map.put("users",users);
      return new ModelAndView(map, "./views/users/users.mustache");
    }, new MustacheTemplateEngine());

    /*Vista del juego cuando se pide una pregunta*/
    get("/questions", (req,res)->{
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
      map.put("quest",q);
      map.put("game_id",req.queryParams("game_id"));
      return new ModelAndView(map, "./views/questions/show.mustache");
    },new MustacheTemplateEngine());


    /**
    *Metodo que verifica si una respuesta dada por un usuario es correcta o no
    *En ambos casos actualiza el perfil del usuario, nivel,puntos,etc.
    **/
    post("/answer", (req,res)->{
      Map map = new HashMap();
      String user_Answer = req.queryParams("question");   //Se obtiene la respuesta proporcionada por el usuario.
      Object q = req.queryParams("quest");
      Question question = Question.findFirst("id = ?", Long.parseLong(q.toString()));
      
      Object id_O = (Object) req.session().attribute("user");
      String s = id_O.toString();
      Long id = Long.parseLong(s);
      User actualUser = User.findFirst("id =?",id);
      
      String id_G = req.queryParams("game_id");
      Long id_game = Long.parseLong(id_G);
      Game game = Game.findFirst("id = ?", id_game);
      System.out.println("ID DEL JUEGO!!"+id_game);
      game.incrementRound();
      
      if(user_Answer.equals((String)question.get("answer1"))){        
        actualUser.updateProfile(true);
        Object correct_ans = req.session().attribute("correct_answer");
        Integer cant_ans = Integer.parseInt(correct_ans.toString());
        if(cant_ans>=3){
          actualUser.setLives((Integer)actualUser.get("lives")+1);
          req.session().removeAttribute("correct_answer");
          req.session().attribute("correct_answer",0);
        }
        else{
          req.session().removeAttribute("correct_answer");
          req.session().attribute("correct_answer",cant_ans+1);
        }
        map.put("correct","Respuesta correcta");
        map.put("game_id",req.queryParams("game_id"));
        return new ModelAndView(map,"./views/games/correct.mustache");
      }
      else{
        actualUser.updateProfile(false);
        map.put("incorrect","Respuesta incorrecta");
        return new ModelAndView(map,"./views/games/incorrect.mustache");
      }
    },new MustacheTemplateEngine());
  

    /*Metodo que permite continuar una partida pendiente
    *Siempre y cuando la misma no este suspendida, o finalizada*/
    post("/play", (req,res)->{
      Map map = new HashMap();
      //Object o = req.session().attribute("game_id");
      //String id_G = o.toString();
      String id_G = req.queryParams("game_id");
      Long id_game = Long.parseLong(id_G);
      Game game = Game.findFirst("id = ?", id_game);
      if (((String)game.get("state")).equals("En_proceso") && (Integer)game.get("round")<5){
        Category c = game.getRandomCategory();
        map.put("category", c.get("tCategory"));
        String idc = (c.getId()).toString();
        Long id_Cat = Long.parseLong(idc);
        map.put("category_id",id_Cat);
        map.put("game_id",id_G);
        return new ModelAndView(map,"./views/category/randomCategory.mustache");
      }   
      else{
        game.finalized();
        /*List<Game> games = Game.where("user_id = ? and state=?",id_game,"En_proceso");
        map.put("games",games);*/
        Object id_O = (Object) req.session().attribute("user");
        Long id = Long.parseLong(id_O.toString());
        User actualUser = User.findFirst("id =?",id);
        map.put("user",actualUser.get("username"));
        return new ModelAndView(map, "./views/games/win.mustache");

      }
    }, new MustacheTemplateEngine());  
  }
}