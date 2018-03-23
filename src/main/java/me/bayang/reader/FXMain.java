package me.bayang.reader;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.stage.Stage;
import me.bayang.reader.controllers.AddSubscriptionController;
import me.bayang.reader.controllers.RssView;

@SpringBootApplication
public class FXMain extends AbstractJavaFxApplicationSupport {
    
    private static Stage addSubscriptionStage;
    
    private static AddSubscriptionController addSubscriptionController;
    
    public static void main(String[] args) {
        launch(FXMain.class, RssView.class, new NoOpSplashScreen() ,args);
    }

    public static Stage getAddSubscriptionStage() {
        return addSubscriptionStage;
    }
}
