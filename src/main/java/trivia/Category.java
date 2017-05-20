package trivia;

import org.javalite.activejdbc.Model;
import java.util.Random;
import java.util.List;
public class Category extends Model {
  static{    
  }
  public Category(){}
  public Category(String name){
  	set("tCategory",name);
  }
  public Category randomCategory(){
  	List<Category> list = findAll(); 
  	Random r = new Random();
  	return list.get(r.nextInt(list.size()));
  }

  //public Question getQuestion(){
  	//return (getRandomQuestion());
  //}

}