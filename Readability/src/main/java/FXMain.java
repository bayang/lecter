import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by Zhang on 3/14.
 */
public class FXMain extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("UI.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
