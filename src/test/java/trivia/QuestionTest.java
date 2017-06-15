package trivia;

import trivia.User;

import org.javalite.activejdbc.Base;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class QuestionTest{
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
	public void validateNoEmptyDescription(){
	  Question q = new Question();
	  q.set("description","");
	  q.set("answer1","hola");
	  q.set("answer2","prueba");
	  q.set("answer3","pruena");
	  q.set("answer4","jaja");
	  q.save();
	  assertEquals(q.isValid(),false);
	}


	@Test
	public void validatePresenceOfAnswers(){
	  Question q = new Question();
		q.set("description","hola");
	  q.set("answer1","hola");
	  q.set("answer2","");
	  q.set("answer3","pruena");
	  q.set("answer4","jaja");
	  q.save();
	  assertEquals(q.isValid(),false);
	}	
}