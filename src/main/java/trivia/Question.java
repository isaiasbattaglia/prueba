package trivia;

import org.javalite.activejdbc.Model;

public class Question extends Model {
  static{
		validatePresenceOf("answer1").message("Please, provide at least one answer"); 
  }
  /**
  *Constructo de la clase Question
  **/
  public Question(){}
  /**
  *Constructor de la clase Question
  *@Param description descripcion pregunta, ans1,ans2,ans2,ans4 respuestas a la pregunta
  **/
  public Question(String description,String ans1, String ans2, String ans3, String ans4){
		validatePresenceOf("description").message("Please, provide your question description");
	  set("description", description);
    set("answer1",ans1);
    set("answer2",ans2);
    set("answer3",ans3);
    set("answer4",ans4);
	}
  /**
  *Metodo que muestra las respuesta a una pregunta de forma aleatoria
  **/
  public void showRandomAnswer(){
    System.out.println(this.get("answer1"));
    System.out.println(this.get("answer2"));
    System.out.println(this.get("answer3"));
    System.out.println(this.get("answer4"));
  }


  public void showQuestion(){
    Category cat = this.parent(Category.class);
    System.out.println((String)cat.get("tCategory"));
    System.out.println((String)this.get("description"));
    showRandomAnswer();
  }

  public boolean validateAnswer(Integer numberOfAnswer){
    return numberOfAnswer==1;
  }
}