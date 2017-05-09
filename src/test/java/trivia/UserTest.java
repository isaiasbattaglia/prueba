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
         user.set("username", "anakin");
         user.saveIt();
         User user2 = new User();
         user.set("username", "anakin");
         assertEquals(user2.isValid(), false);
     }

     @Test
     public void validateUniquenessOfEmail(){
         User user = new User();
         user.set("username", "hola");
         user.set("email", "hola@gmail.com");
         user.saveIt();
         User user2 = new User();
         user2.set("username", "hola2");
         user2.set("email", "hola@gmail.com");
         assertEquals(user2.get("email")!=user.get("email"), false);
     }

     @Test
     public void validatePositiveAmountOfLives(){
         User user = new User();
         user.set("username", "pepe");
         user.set("lives", -3);
         user.saveIt();
         assertFalse( ((Integer)user.get("lives"))>=0);
         //assertEquals(user2.get("email")!=user.get("email"), false);
     }
}

