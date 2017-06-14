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
         user.saveIt();
         User user2 = new User();
         user2.set("username", "pepe");
         user2.set("password", "hola");
         user2.set("email", "hola@gmail.com");
         user2.set("lives", 3);
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
         user.saveIt();
         User user2 = new User();
         user2.set("username", "hola2");
         user2.set("password", "hola");
         user2.set("email", "hola@gmail.com");
         user2.set("lives", 3);
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
         user.saveIt();
         assertFalse( ((Integer)user.get("level"))>=0);
     }
}

