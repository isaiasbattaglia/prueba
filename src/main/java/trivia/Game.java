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
    if(!(this.get("state").equals("finalizado"))){
      boolean valid = true;
      while(valid && (Integer)this.get("round")<20){ 
        Category category = getRandomCategory();
        Question question = category.getQuestion();
        waitAnswer(question,category);
        //valid = question.answer(v);
        this.set("round",(Integer)this.get("round")+10).saveIt();
      }
      if((Integer)this.get("round")>=20)
        this.set("state","finalizada").saveIt();
    }
  }

  private void waitAnswer(Question question,Category category){
    System.out.println((String)category.get("tCategory"));
    System.out.println((String)question.get("description"));
    question.showRandomAnswer();
  }
  /**
  *Metodo que permite rendirse durante una partida
  **/
  public void whiteFlag()
    {set("state","finalizada");}
}