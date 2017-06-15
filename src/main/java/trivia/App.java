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
        
        /*Se obtiene el ID del usuario*/
        Object id_O = currentUser.getId();
        String s = id_O.toString();
        Long id= Long.parseLong(s);
        /*Fin de la obtencion del ID del usuario*/
       if (req.session().attribute("user")!=null){    //Ya hay una sesion iniciada...
        Object id_O_S = req.session().attribute("user");
        String id_Se = id_O_S.toString();
        Long id_sesion= Long.parseLong(id_Se);
        //SI la sesion del usuario no es la misma del que quiere volver a ingresar
        if(id_sesion.compareTo(id)!=0){
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
      Object id_O = (Object) req.session().attribute("user");
      String s = id_O.toString();
      //String s = req.session().attribute("user");
      Long id = Long.parseLong(s);
      List<Game> games = Game.where("user_id = ? and state=?",id,"En_proceso");  //Se obtiene la lista de juegos que posee ese usuario.
      User actualUser = User.findById(id);    //Se busca el usuarii
      games.size();       //Esto se lo hace para traer la lazylist
      
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
      Object id_O = (Object) req.session().attribute("user");
      String s = id_O.toString();
      Long id = Long.parseLong(s);
      User actualUser = User.findById(id);  
      //Si el usuario posee vidas para seguir jugando
      if ((Integer)actualUser.get("lives")>0){
        Game game = new Game(id); //Se crea un nuevo juego para ese usuario.
        //actualUser.setLives((Integer)actualUser.get("lives")-1);
        Category c = (new Category()).randomCategory();
        String idc = (c.getId()).toString();      //Se obtiene el id de la categoria
        Long id_Cat = Long.parseLong(idc);        //Se obtiene el id de la categoria
        String idg = (game.getId()).toString();   //Se obtiene el id del juego en el cual se esta jugando
        map.put("category", c.get("tCategory"));
        map.put("category_id",id_Cat);
        map.put("game_id",idg);

        //Se colocan en un mapa la categoria que toco, para luego mostrar una vista personalizada con esa categoria.
        String cat =(String) c.get("tCategory");
        if (cat.equals("Historia")){
          map.put("hist_art",true);
          map.put("historia",true);
        }
        if (cat.equals("Geografia")){
          map.put("dep_geo",true);
          map.put("geografia",true);
        }
        if (cat.equals("Ciencia")){
          map.put("cie_ent",true);
          map.put("ciencia",true);
        }
        if (cat.equals("Entretenimiento")){
          map.put("cie_ent",true);
          map.put("entretenimiento",true);
        }
        if (cat.equals("Deportes")){
          map.put("dep_geo",true);
          map.put("deportes",true);
        }
        if (cat.equals("Arte")){
          map.put("hist_art",true);
          map.put("arte",true);
        }
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
      Long id = Long.parseLong(id_c);
      Category c = Category.findById(id);
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
      //Se ovtiene la question realizada al usuario
      Object q = req.queryParams("quest");
      Long id_q = Long.parseLong(q.toString());
      //Fin de la obtencion de la question
      Question question = Question.findById(id_q);

      Object id_O = (Object) req.session().attribute("user");
      String s = id_O.toString();
      Long id = Long.parseLong(s);
      User actualUser = User.findById(id);

      //Se registra que el usuario actual respondio esa pregunta.
      QuestionsUsers qu = new QuestionsUsers(id,id_q);

      //Se obtiene el id del juego en cuestion.
      String id_G = req.queryParams("game_id");
      Long id_game = Long.parseLong(id_G);
      //FIn de la obtenecion del id del juego
      Game game = Game.findById(id_game);
      game.incrementRound();  //Se incrementan las rondas del juego
      
      GamesQuestions gq = new GamesQuestions(id_q,id_game);   //Se vincula el game y la question.

      //Se obtiene la categoria de la pregunta y su correspondiente ID
      Category c = question.getCategory();
      Object id_Cat_o = c.getId();
      Long id_cat = Long.parseLong((id_Cat_o.toString()));


      //CategoriesGames cg = new CategoriesGames(id_game,id_cat);
      //Si respondio correctamente...
      if(user_Answer.equals((String)question.get("answer1"))){
        actualUser.updateProfile(true);   //Se actuliza el perfil del usuario con una respuesta correcta.
        game.updateGame(true);            //Se actualiza el game con una respuesta correcta.

        //Se obtiene la cantidad de preguntas que respondio correctamente
        Object correct_ans = req.session().attribute("correct_answer");
        Integer cant_ans = Integer.parseInt(correct_ans.toString());
        if(cant_ans>=3){    //Si son mayor o igual a 3
          //Le incrementamos una vida al usuario y reiniciamos las preguntas respondidas correctamente en esa sesion.
          actualUser.setLives((Integer)actualUser.get("lives")+1);
          req.session().removeAttribute("correct_answer");
          req.session().attribute("correct_answer",0);
        }
        else{
          //Incrementamos las respuestas correctas.
          req.session().removeAttribute("correct_answer");
          req.session().attribute("correct_answer",cant_ans+1);
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
      Long id_game = Long.parseLong(id_G);
      Game game = Game.findById(id_game);
      Category c = (new Category()).randomCategory();
      map.put("category", c.get("tCategory"));
      String idc = (c.getId()).toString();
      Long id_Cat = Long.parseLong(idc);
      map.put("category_id",id_Cat);
      map.put("game_id",id_G);
      String cat =(String) c.get("tCategory");

      //Se coloca en un mapa la categoria que toco para mostrar una vista personalizada de la misma.
      if (cat.equals("Historia")){
        map.put("hist_art",true);
        map.put("historia",true);
      }
      if (cat.equals("Geografia")){
        map.put("dep_geo",true);
        map.put("geografia",true);
      }
      if (cat.equals("Ciencia")){
        map.put("cie_ent",true);
        map.put("ciencia",true);
      }
      if (cat.equals("Entretenimiento")){
        map.put("cie_ent",true);
        map.put("entretenimiento",true);
      }
      if (cat.equals("Deportes")){
        map.put("dep_geo",true);
        map.put("deportes",true);
      }
      if (cat.equals("Arte")){
        map.put("hist_art",true);
        map.put("arte",true);
      }
        return new ModelAndView(map,"./views/category/randomCategory.mustache");
    }, new MustacheTemplateEngine());  

    //Metodo que devuelve la vista cuando se finaliza un juego
    get("/finalizedGame", (req,res)->{
      Map map = new HashMap();
      Long id = getUserId(req.session().attribute("user"));
      String id_g = req.queryParams("game_id");
      Long game_id = Long.parseLong(id_g);
      Game game = Game.findById(game_id);
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
      Long id = getUserId(req.session().attribute("user"));
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

  private static Long getUserId(Object id_u){
    Object id_O = id_u;
    Long id = Long.parseLong(id_O.toString());
    return id;
  }
}