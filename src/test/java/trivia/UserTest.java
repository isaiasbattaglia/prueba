package trivia;

import trivia.User;

import org.javalite.activejdbc.Base;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;


public class UserTest{
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
   public void validateUniquenessOfUsernames(){
       User user = new User();
       user.set("username", "pepe");
       user.set("password", "hola");
       user.set("email", "hola@gmail");
       user.set("lives", 3);
       user.set("total_points",0);
       user.set("correct_questions",0);
       user.set("incorrect_questions",0);
       user.set("total_questions",0);
       user.set("level",0);
       user.saveIt();
       User user2 = new User();
       user2.set("username", "pepe");
       user2.set("password", "hola");
       user2.set("email", "hola@gmail.com");
       user2.set("lives", 3);
       user2.set("total_points",0);
       user2.set("correct_questions",0);
       user2.set("incorrect_questions",0);
       user2.set("total_questions",0);
       user2.set("level",0);
       user2.save();
       assertEquals(user2.isValid(), false);
   }

   @Test
    public void validateUniquenessOfEmail(){
       User user = new User();
       user.set("username", "hola");
       user.set("password", "hola");
       user.set("email", "hola@gmail.com");
       user.set("lives", 3);
       user.set("total_points",0);
       user.set("correct_questions",0);
       user.set("incorrect_questions",0);
       user.set("total_questions",0);
       user.set("level",0);
       user.saveIt();
       User user2 = new User();
       user2.set("username", "hola2");
       user2.set("password", "hola");
       user2.set("email", "hola@gmail.com");
       user2.set("lives", 3);
       user2.set("total_points",0);
       user2.set("correct_questions",0);
       user2.set("incorrect_questions",0);
       user2.set("total_questions",0);
       user2.set("level",0);
       user2.save();
       assertEquals(user2.isValid(), false);
   }

   @Test
   public void validatePositiveAmountOfLives(){
       User user = new User();
       user.set("username", "pepe");
       user.set("password", "hola");
       user.set("email", "hola@gmail.com");
       user.set("lives", -3);
       user.set("total_points",0);
       user.set("correct_questions",0);
       user.set("incorrect_questions",0);
       user.set("total_questions",0);
       user.set("level",0);
       user.saveIt();
       assertFalse( ((Integer)user.get("lives"))>=0);
   }
   @Test
   public void validatePositiveAmountOfPoints(){
       User user = new User();
       user.set("username", "pepe");
       user.set("password", "hola");
       user.set("email", "hola@gmail.com");
       user.set("lives", 3);
       user.set("total_points", -3);
       user.set("correct_questions",0);
       user.set("incorrect_questions",0);
       user.set("total_questions",0);
       user.set("level",0);
       user.saveIt();
       assertFalse( ((Integer)user.get("total_points"))>=0);
   }

  @Test
   public void validatePositiveAmountOfLevel(){
       User user = new User();
       user.set("username", "pepe");
       user.set("password", "hola");
       user.set("email", "hola@gmail.com");
       user.set("lives", 3);
       user.set("level", -3);
       user.set("total_points", 0);
       user.set("correct_questions",0);
       user.set("incorrect_questions",0);
       user.set("total_questions",0);
       user.saveIt();
       assertFalse( ((Integer)user.get("level"))>=0);
   }


  @Test
  public void validatePositiveAmountOfCorrectQuestion(){
    User user = new User();
    user.set("username", "pepe");
    user.set("password", "hola");
    user.set("email", "hola@gmail.com");
    user.set("lives", 3);
    user.set("level", -3);
    user.set("total_points", 0);
    user.set("correct_questions",-200);
    user.set("incorrect_questions",0);
    user.set("total_questions",0);
    user.saveIt();
    assertFalse( ((Integer)user.get("correct_questions"))>=0);
   }

  @Test
  public void validatePositiveAmountOfIncorrecctQuestion(){
    User user = new User();
    user.set("username", "pepe");
    user.set("password", "hola");
    user.set("email", "hola@gmail.com");
    user.set("lives", 3);
    user.set("level", -3);
    user.set("total_points", 0);
    user.set("correct_questions",0);
    user.set("incorrect_questions",-200);
    user.set("total_questions",0);
    user.saveIt();
    assertFalse( ((Integer)user.get("incorrect_questions"))>=0);
   }

  @Test
  public void validatePositiveAmountOfTotalQuestions(){
    User user = new User();
    user.set("username", "pepe");
    user.set("password", "hola");
    user.set("email", "hola@gmail.com");
    user.set("lives", 3);
    user.set("level", -3);
    user.set("total_points", 0);
    user.set("correct_questions",0);
    user.set("incorrect_questions",0);
    user.set("total_questions",-10);
    user.saveIt();
    assertFalse( ((Integer)user.get("total_questions"))>=0);
   }


  @Test
  public void validatePositiveAmountOfQuestionsCorrect(){
    User u = new User();
    u.set("username","matias");
    u.set("password","cabj");
    u.set("email","maty@cabj.com");
    u.set("lives", 3);
    u.set("lives", 3);
    u.set("total_points",0);
    u.set("correct_questions",0);
    u.set("incorrect_questions",0);
    u.set("total_questions",0);
    u.set("level",0);
    u.saveIt();

    Object id_O = u.getId();
    Long id = Long.parseLong(id_O.toString());
    Game game = new Game();
    game.set("user_id",id);
    game.set("questions_Correct",-1);
    game.set("questions_Incorrect",0);
    game.saveIt();
    assertEquals((((Integer)game.get("questions_Correct"))>=0),false);
  }


  private void answerQuestion(boolean question, User user){
    if (question)
      user.updateProfile(true);
    else
      user.updateProfile(false);
    user.saveIt();
  } 

  /*@Test
  public void validateSubtractLive(){
    User u = new User();
    u.set("username","matias");
    u.set("password","cabj");
    u.set("email","maty@cabj.com");
    u.set("lives", 3);
    u.set("lives", 3);
    u.set("total_points",0);
    u.set("correct_questions",0);
    u.set("incorrect_questions",0);
    u.set("total_questions",0);
    u.set("level",0);
    u.saveIt();
    Game game = Game();
    game.saveIt();
    assertEquals((((Integer)u.get("lives")).compareTo(new Integer(2)))==0,true);    
  }*/

}

