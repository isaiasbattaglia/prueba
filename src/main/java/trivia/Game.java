package trivia;

import org.javalite.activejdbc.Model;

public class Game extends Model {
  static{
    validatePresenceOf("user_id").message("Please, provide a user id");
  }
  
  /**
  *Constructor por defecto de la clase Game
  **/
  public Game(){}
  /**
  *Constructor de la clase Game
  *@Param user_id usuario que crea el juego
  **/
  public Game(Long user_id){
  	set("round",0);
  	set("user_id",user_id);
    set("state","en_proceso");
    //User user = this.parent(User.class);
    saveIt();
  }

  /**
  *Metodo que obtiene una categoria aleatoria
  *@Return categoria aleatoria
  **/
  public Category getRandomCategory()
  {return (new Category()).randomCategory();}

  /**
  *Metodo que permite jugar en una partida hasta que se responda mal, o se alcance el maximo de rondas permitidas
  **/
  public void play(){
    User user = this.parent(User.class);
    if(!(this.get("state").equals("finalizado")) && (Integer)user.get("lives")>0){
      user.set("lives",(Integer)user.get("lives")-1).saveIt();
      boolean valid = true;
      while(valid && (Integer)this.get("round")<20){ 
        Category category = getRandomCategory();
        this.add(category);
        Question question = category.getQuestion();
        question.add(user);
        valid = user.answerQuestion(question);
        this.set("round",(Integer)this.get("round")+1).saveIt();
      }
      if((Integer)this.get("round")>=20)
        this.set("state","finalizada").saveIt();
    }

  }

  /**
  *Metodo que permite rendirse durante una partida
  **/
  public void whiteFlag()
    {set("state","finalizada");}
  

  public String state(){
    return (String) this.get("state");
  }
}