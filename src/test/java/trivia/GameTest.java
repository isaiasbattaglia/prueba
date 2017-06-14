package trivia;

import trivia.User;

import org.javalite.activejdbc.Base;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GameTest{
  @Before
  public void before(){
    Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia_test", "root", "root");
    System.out.println("UserTest setup");
    Base.openTransaction();
  }

  @After
  public void after(){
    System.out.println("UserTest tearDown");
    Base.rollbackTransaction();
    Base.close();
  }

	@Test
	public void validateSubtractLive(){
		User u = new User();
		u.set("username","matias");
		u.set("password","cabj");
		u.set("email","maty@cabj.com");
    u.set("lives", 3);
    u.saveIt();
		Object id_O = u.getId();
		Long id = Long.parseLong(id_O.toString());
    Game game = new Game(id);
    System.out.println(u.get("lives"));
    game.saveIt();
		assertEquals((((Integer)u.get("lives")).compareTo(new Integer(2)))==0,true);		
	}  
}

  /**
  *Metodo que permite jugar en una partida hasta que se responda mal, o se alcance el maximo de rondas permitidas
  **/
 /* public void play(Category c, Question q){
    User user = this.parent(User.class);
    if(!(this.get("state").equals("finalizado")) && (Integer)user.get("lives")>0){
      user.set("lives",(Integer)user.get("lives")-1).saveIt();
      boolean valid = true;
      while(valid && (Integer)this.get("round")<5){ 
        this.add(category);
        valid = answerQuestion(true);
        this.set("round",(Integer)this.get("round")+1).saveIt();
      }
      if((Integer)this.get("round")>=20)
        this.set("state","finalizada").saveIt();
    }

  }

  public boolean answerQuestion(boolean question){
    if (question){
      updateProfile(true);
      return true;
    }
    updateProfile(false);
    return false;
  }*/

 		/*Category c = new Category("Deportes");
 		Object id_O_c = c.getId();
 		String id_S_C = id_S_C.toString();
 		Long id_C = Long.parseLong(id_S_C);
 		Question q = new Question("Pregunta","ans_1","ans_2","ans_3","ans_4",id_C);
 		play();*

}*/