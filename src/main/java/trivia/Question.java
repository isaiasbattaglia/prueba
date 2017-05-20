package trivia;

import org.javalite.activejdbc.Model;

public class Question extends Model {
  static{
		validatePresenceOf("answer1").message("Please, provide at least one answer"); 
  }

  public Question(){}
  public Question(String description,String ans1, String ans2, String ans3, String ans4){
		validatePresenceOf("description").message("Please, provide your question description");
	  set("description", description);
    set("answer1",ans1);
    set("answer2",ans2);
    set("answer3",ans3);
    set("answer4",ans4);
	}
}