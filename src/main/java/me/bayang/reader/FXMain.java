package me.bayang.reader;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import me.bayang.reader.view.NoOpSplashScreen;
import me.bayang.reader.view.RssView;

@SpringBootApplication
public class FXMain extends AbstractJavaFxApplicationSupport {
    
    public static void main(String[] args) {
        launch(FXMain.class, RssView.class, new NoOpSplashScreen() ,args);
    }

}
