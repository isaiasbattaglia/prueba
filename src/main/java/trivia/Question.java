package trivia;

import org.javalite.activejdbc.Model;
import java.util.*;

public class Question extends Model {
  static{
    validatePresenceOf("description").message("Please, provide a description"); 
    validatePresenceOf("answer1").message("Please, provide all answers"); 
    validatePresenceOf("answer2").message("Please, provide all answers"); 
    validatePresenceOf("answer3").message("Please, provide all answers"); 
    validatePresenceOf("answer4").message("Please, provide all answers"); 
  }
  /**
  *Constructo de la clase Question
  **/
  public Question(){}
  /**
  *Constructor de la clase Question
  *@Param description descripcion pregunta, ans1,ans2,ans2,ans4 respuestas a la pregunta
  **/
  public Question(String description,String ans1, String ans2, String ans3, String ans4, Long id_C){
    validatePresenceOf("description").message("Please, provide a description"); 
    validatePresenceOf("answer1").message("Please, provide all answers"); 
    validatePresenceOf("answer2").message("Please, provide all answers"); 
    validatePresenceOf("answer3").message("Please, provide all answers"); 
    validatePresenceOf("answer4").message("Please, provide all answers"); 
	  set("description", description);
    set("answer1",ans1);
    set("answer2",ans2);
    set("answer3",ans3);
    set("answer4",ans4);
    set("category_id",id_C);
    saveIt();
	}

  //Metodo que realiza una permutacion aleatoria de las respuestas de una pregunta.
  public List<String> randomAnswers(){
    List<String> arr = new ArrayList<String>();
    arr.add(0,(String)this.get("answer1"));
    arr.add(1,(String)this.get("answer2"));
    arr.add(2,(String)this.get("answer3"));
    arr.add(3,(String)this.get("answer4"));
    Collections.shuffle(arr);
    return arr;
  }

  //Obtiene la categoria(padre) de la pregunta.
  public Category getCategory(){
    Category c = this.parent(Category.class);
    return c;
  }
  
}