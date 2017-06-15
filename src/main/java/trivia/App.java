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


    //-----------------------------------------------------------------------------------------------------------------------
    //Manejo del registro,login y logout del usuario
    //-----------------------------------------------------------------------------------------------------------------------

    //Home del juego
    get("/", (req,res) -> {
      Map map = new HashMap();
      //res.header("cache-control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
      //res.header("pragma", "no-cache"); // HTTP 1.0
      //res.header("expires", "0"); // HTTP 1.0 proxies
      //Si el usuario ya se encuentra logueado, se lo redirecciona directamente al home del game.
      //if (req.session().attribute("user")!=null) {
        //res.redirect("/games");
        //return null;
      //}
      return new ModelAndView(map, "./views/home.mustache");
    },new MustacheTemplateEngine());


    /*Metodo que permite hacer el login de un usuario*/
    get("/login", (req,res)->{
      Map map = new HashMap();
      //Si el usuario ingresado es valido
      if(User.validUser(req.queryParams("nickname"),req.queryParams("password"))){
        User currentUser = User.getUser(req.queryParams("nickname"),req.queryParams("password"));   //Buscamos el usuario de la persona que se acaba de loguear
        
        Long id = Long.parseLong((currentUser.getId()).toString());

       if (req.session().attribute("user")!=null){    //Ya hay una sesion iniciada...
        Long id_sesion = req.session().attribute("user");   //Obtenemos el id del usuario que estaba jugando
        //SI la sesion del usuario no es la misma del que quiere ingresar
        if(id_sesion.compareTo((Long)id)!=0){
          req.session(true);    //Se crea la sesion para el usuario.
          req.session().attribute("user", id);  //Se asocia esa sesion con el id del usuario en cuestion.
          req.session().attribute("correct_answer",0);
        }
      }
      else{
        req.session(true);    //Se crea la sesion para el usuario.
        req.session().attribute("user", id);  //Se asocia esa sesion con el id del usuario en cuestion.
        req.session().attribute("correct_answer",0);
      }
        List<Game> games = Game.where("user_id = ? and state=?",id,"En_proceso");  //Se obtiene la lista de juegos que posee ese usuario.
        games.size();       
        map.put("games", games);  
        map.put("lives",currentUser.get("lives"));
        map.put("level",currentUser.get("level"));
        return new ModelAndView(map,"./views/games/home.mustache");
      }
      else{
        map.put("error","Usuario o contraseña incorrecto");
        return new ModelAndView(map,"./views/home.mustache");
      }
    },new MustacheTemplateEngine());


    /*Se muestra la vista cuando alguien se desea registrar en el juego*/
    get("/registrar", (req,res) -> {
      Map map = new HashMap();
      return new ModelAndView(map, "./views/users/new.mustache");
    },new MustacheTemplateEngine());

    //Metdo que vrifica si los datos ingresados por un usuario, son correctos
    post("/verificar", (req, res) -> {
      Map map = new HashMap();
      String pass1 = req.queryParams("password");
      String pass2 = req.queryParams("password2");
      if(!(pass1.equals(pass2))){
        map.put("error","Contraseñas no coinciden");
        return new ModelAndView(map,"./views/users/new.mustache");
      }
      List<User> lst = User.where("username = ? or email = ? ", req.queryParams("nickname"), req.queryParams("Email"));
      User u;
      if(lst.size()==0)
        u = new User(req.queryParams("nickname"),req.queryParams("Email"),req.queryParams("password"));
      else{
        User user = lst.get(0);
        String username = (String)user.get("username");
        if(username.equals(req.queryParams("username")))
          map.put("error","El nombre de usuario ya esta registrado.");
        else
          map.put("error","Ya existe un usuario con ese email.");
        return new ModelAndView(map,"./views/users/new.mustache");
      }
      return new ModelAndView(map, "./views/home.mustache");    //Se registro con exito, se redirecciona al home, para loguarse.
    }, new MustacheTemplateEngine());

    //Metodo que permite realizar el logout de una sesion.
    get("/logout", (req,res)->{
      Map map = new HashMap();
      if (req.session().attribute("user")!=null){ 
        req.session().removeAttribute("user");
        req.session().removeAttribute("correct_answer");
      }
      return new ModelAndView(map, "./views/home.mustache");
    },new MustacheTemplateEngine());


  //-----------------------------------------------------------------------------------------------------------------------
  //Manejo de la logica del juego, preguntas y respuestas, vistas de las mismas
  //-----------------------------------------------------------------------------------------------------------------------


    /*Busca en la base de datos si el usuario es correcto, en caso de ser valido, crea una sesion para ese usuario
    * y muestra la vista de new game, en el caso de no ser correcto el usuario se redirecciona a la ventana login*/
    get("/games", (req,res) ->{
      Map map =new HashMap();
      Long id = req.session().attribute("user");
      List<Game> games = Game.where("user_id = ? and state=?",id,"En_proceso");  //Se obtiene la lista de juegos que posee ese usuario.
      User actualUser = User.findById(id);    //Se busca el usuarii
      games.size(); //Esto se lo hace para traer la lazylist
      
      //Se colocan los atributos necesarios en el map.
      map.put("games", games);  
      map.put("lives",actualUser.get("lives"));
      map.put("level",actualUser.get("level"));
      return new ModelAndView(map,"./views/games/home.mustache");   //Retorna la vista del hombe de game      
      }, new MustacheTemplateEngine());
    
    /*Meotod que permite crear un juego
    *Se crea un nuevo juego, si el usuario tiene vidas para hacerlo, caso contrario no se lo permite*/
    post("/newGame", (req,res)->{
      Map map = new HashMap();
      Long id = req.session().attribute("user");
      User actualUser = User.findById(id);  

      //Si el usuario posee vidas para seguir jugando
      if ((Integer)actualUser.get("lives")>0){
        Game game = new Game(id); //Se crea un nuevo juego para ese usuario.
        //actualUser.setLives((Integer)actualUser.get("lives")-1);
        Category c = (new Category()).randomCategory();
        Long idc = Long.parseLong((c.getId()).toString());      //Se obtiene el id de la categoria
        Long idg = Long.parseLong((game.getId()).toString());   //Se obtiene el id del juego en el cual se esta jugando
        map.put("category", c.get("tCategory"));
        map.put("category_id",idc);
        map.put("game_id",idg);

        //Se colocan en un mapa la categoria que toco, para luego mostrar una vista personalizada con esa categoria.
        String cat =(String) c.get("tCategory");
        if (cat.equals("Historia"))
          map.put("historia",true);
        if (cat.equals("Geografia"))
          map.put("geografia",true);
        if (cat.equals("Ciencia"))
          map.put("ciencia",true);
        if (cat.equals("Entretenimiento"))
          map.put("entretenimiento",true);
        if (cat.equals("Deportes"))
          map.put("deportes",true);
        if (cat.equals("Arte"))
          map.put("arte",true);
        return new ModelAndView(map,"./views/category/randomCategory.mustache");
      }
      else{ //El usuario no posee mas vidas para continuar jugando.
        List<Game> games = Game.where("user_id = ? and state=?",id,"En_proceso");
        games.size();     //Para traer la lazy list.
        map.put("games",games);
        map.put("lives",actualUser.get("lives"));
        map.put("level",actualUser.get("level"));
        map.put("error","No posee mas vidas para seguir jugando");
        return new ModelAndView(map,"./views/games/home.mustache");
      } 
    },new MustacheTemplateEngine());

    /*Vista del juego cuando se pide una pregunta*/
    get("/questions", (req,res)->{
      String id_c = req.queryParams("category_id");
      Category c = Category.findById(id_c);
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
      //Se obtiene el ID de la question realizada al usuario
      String id_qq = req.queryParams("quest_id");
      Long id_q = Long.parseLong(id_qq);
      Question question = Question.findById(id_q);

      Long id_u = req.session().attribute("user");
      User actualUser = User.findById(id_u);

      //Se registra que el usuario actual respondio esa pregunta.
      QuestionsUsers qu = new QuestionsUsers(id_u,id_q);

      //Se obtiene el id del juego en cuestion.
      String id_G = req.queryParams("game_id");
      Long id_game = Long.parseLong(id_G);
      Game game = Game.findById(id_game);
      game.incrementRound();  //Se incrementan las rondas del juego
      
      GamesQuestions gq = new GamesQuestions(id_q,id_game);   //Se vincula el game y la question.

      //Se obtiene la categoria de la pregunta y su correspondiente ID
      Category c = question.getCategory();
      //Si respondio correctamente...
      if(user_Answer.equals((String)question.get("answer1"))){
        actualUser.updateProfile(true);   //Se actuliza el perfil del usuario con una respuesta correcta.
        game.updateGame(true);            //Se actualiza el game con una respuesta correcta.
        //Se obtiene la cantidad de preguntas que respondio correctamente
        Integer correct_ans = req.session().attribute("correct_answer");
        if(correct_ans>=3){    //Si son mayor o igual a 3
          //Le incrementamos una vida al usuario y reiniciamos las preguntas respondidas correctamente en esa sesion.
          actualUser.setLives((Integer)actualUser.get("lives")+1);
          req.session().removeAttribute("correct_answer");
          req.session().attribute("correct_answer",0);
        }
        else{
          //Incrementamos las respuestas correctas.
          req.session().removeAttribute("correct_answer");
          req.session().attribute("correct_answer",correct_ans+1);
        }
        //Si estamos en la ultima ronda
        if(game.getActualRound().compareTo(game.getTotalRounds())==0){          
          game.finalized();   //Se finaliza el juego
          map.put("final",true);
          map.put("no_final",false);
        }
        else{        
          map.put("final",false);  
          map.put("no_final",true);
        }
        map.put("correct","Respuesta correcta");
        map.put("game_id",req.queryParams("game_id"));
        return new ModelAndView(map,"./views/games/correct.mustache");
      }
      else{
        actualUser.updateProfile(false);    //Se actualiza el perfil del usuario con respuesta incorrecta
        game.updateGame(false);             //Se actualiza el game con una respuesta incorrecta
        if(game.getActualRound().compareTo(game.getTotalRounds())==0){          //SI estabamos en la ultima ronda
          game.finalized();   //Finalizamos el juego
          map.put("final",true);
          map.put("no_final",false);
        }
        else{        
          map.put("final",false);  
          map.put("no_final",true);
        }
        map.put("incorrect","Respuesta incorrecta");
        map.put("game_id",req.queryParams("game_id"));
        map.put ("correct_answer",(String)question.get("answer1"));
        return new ModelAndView(map,"./views/games/incorrect.mustache");
      }
    },new MustacheTemplateEngine());
  

    /*Metodo que permite continuar una partida pendiente
    *Siempre y cuando la misma no este suspendida, o finalizada*/
    get("/play", (req,res)->{
      Map map = new HashMap();
      String id_G = req.queryParams("game_id");
      Game game = Game.findById(id_G);
      Category c = (new Category()).randomCategory();
      Long idc = Long.parseLong((c.getId()).toString());
      //Long id_Cat = Long.parseLong(idc);
      map.put("category", c.get("tCategory"));
      map.put("category_id",idc);
      map.put("game_id",id_G);
      String cat =(String) c.get("tCategory");

      //Se coloca en un mapa la categoria que toco para mostrar una vista personalizada de la misma.
      if (cat.equals("Historia"))
        map.put("historia",true);
      if (cat.equals("Geografia"))
        map.put("geografia",true);
      if (cat.equals("Ciencia"))
        map.put("ciencia",true);
      if (cat.equals("Entretenimiento"))
        map.put("entretenimiento",true);
      if (cat.equals("Deportes"))
        map.put("deportes",true);
      if (cat.equals("Arte"))
        map.put("arte",true);
      return new ModelAndView(map,"./views/category/randomCategory.mustache");
    }, new MustacheTemplateEngine());  

    //Metodo que devuelve la vista cuando se finaliza un juego
    get("/finalizedGame", (req,res)->{
      Map map = new HashMap();
      Long id = (req.session().attribute("user"));
      String id_g = req.queryParams("game_id");
      Game game = Game.findById(id_g);
      User actualUser = User.findById(id);
      map.put("user",actualUser.get("username"));
      map.put("Co_ans",game.get("questions_Correct"));
      map.put("In_ans",game.get("questions_Incorrect"));
      return new ModelAndView(map, "./views/games/finalizedGame.mustache");
    }, new MustacheTemplateEngine());



  //---------------------------------------------------------------------------------------------------------
  //Manejo de vistas de usuario.
  //---------------------------------------------------------------------------------------------------------

    get("/profile", (req,res)->{
      Map data = new HashMap();
      Long id = (req.session().attribute("user"));
      User actualUser = User.findById(id);
      data.put("lifes",actualUser.get("lives"));
      data.put("Total_Points",actualUser.get("total_points"));
      data.put("correct_questions",actualUser.get("correct_questions"));
      data.put("incorrect_questions",actualUser.get("incorrect_questions"));  
      data.put("total_questions",actualUser.get("total_questions"));
      data.put("level",actualUser.get("level"));
      data.put("points_to_next_level",actualUser.pointsToNextLevel());
      data.put("user",actualUser.get("username"));
      return new ModelAndView(data,"./views/users/profile.mustache");
    },new MustacheTemplateEngine());


    //Metodo que muestra el ranking de usuarios.
    get("/ranking",(req,res)->{
      Map info = new HashMap();
      List<User> top_10 = User.findBySQL("select username, level from users order by level desc limit 10");
      info.put("ranking",top_10);
      return new ModelAndView(info,"./views/ranking.mustache");
    },new MustacheTemplateEngine());
  }
}