package trivia;

import org.javalite.activejdbc.Model;
import java.util.Random;
import java.util.List;
public class Category extends Model {
  static{    
  }
  /**
  *Constructor de la clase Category
  **/
  public Category(){}
  /**
  *Constructor de la clase Category
  *@Param name nombre de la categoria
  **/
  public Category(String name){
  	set("tCategory",name);
  }
  /**
  *Metodo que obtiene una categoria aleatoria
  *@Return una categoria aleatoria
  **/
  public Category randomCategory(){
  	List<Category> list = findAll(); 
  	Random r = new Random();
  	return list.get(r.nextInt(list.size()));
  }
  /**
  *Metodo que obtiene una pregunta aleatoria correspondiente a esta(this) categoria
  *@Return Question aleatoria
  **/
  public Question getQuestion(){
  	List<Question> lst = this.getAll(Question.class);
    System.out.println(lst.size());
    Random r = new Random();
    Integer i = r.nextInt(lst.size());
    return lst.get(i);
  }

}