package trivia;
import org.javalite.activejdbc.Base;
import trivia.User;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args )
    {
      /*Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia", "root", "root");
      User u = new User();
      u.set("username", "Maradona");
      u.set("password", "messi");
      u.saveIt();

      User user2 = new User();
      user2.set("username", "oas");
      user2.set("password", "oass");
      user2.saveIt();
      Base.close();
      Base.deleteAll();
      Base.close();*/
      addUser();
    }

    public static void addUser(){
    	Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia", "root", "root");
    	String username, password;
    	Scanner a =new Scanner();
    	System.out.println("Ingrese su email");
    	User u = new User();
    	u.set("username",  a.Scanner());
    	u.saveIt();
    	System.out.println("Ungrese su contraseña:");
    	u.set("password", a.Scanner());
    	u.saveIt();
    	Base.close();
    }
}
