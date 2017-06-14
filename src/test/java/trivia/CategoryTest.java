package trivia;

import trivia.User;

import org.javalite.activejdbc.Base;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CategoryTest{

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
  	public void validateUniquenessOfCategories(){
  		Category c = new Category();
  		c.set("tCategory","Historia");
  		c.saveIt();
  		Category c2 = new Category();
  		c2.set("tCategory","Historia");
  		c2.save();
  		assertEquals(c2.isValid(),false);
  	}
}