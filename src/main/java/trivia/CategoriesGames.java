package trivia;
import org.javalite.activejdbc.Model;
public class CategoriesGames extends Model {
	public CategoriesGames(Long game_Id, Long cat_id){
		set("game_id",game_Id);
		set("category_id",cat_id);
		saveIt();
	}


}