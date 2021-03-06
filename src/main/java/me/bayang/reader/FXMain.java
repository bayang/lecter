package me.bayang.reader;

import java.util.ResourceBundle;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.PropertySource;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import de.felixroske.jfxsupport.GUIState;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import me.bayang.reader.controllers.ShareLinkController;
import me.bayang.reader.utils.Theme;
import me.bayang.reader.view.RssView;
import me.bayang.reader.view.SpinnerSplashScreen;

@SpringBootApplication
@PropertySource(value="file:${home.dir.config}/.config/lecter/config.properties", ignoreResourceNotFound=true)
public class FXMain extends AbstractJavaFxApplicationSupport {
    
    public static Stage shareLinkStage = null;
    public static ShareLinkController shareLinkController = null;
    
    public static ResourceBundle bundle = ResourceBundle.getBundle("i18n.translations");
    
    public static String startupCss;
    
    public static void main(String[] args) {
//        launch(FXMain.class, RssView.class, new NoOpSplashScreen() ,args);
        launch(FXMain.class, RssView.class, new SpinnerSplashScreen(), args);
    }
    
    public static void createShareLinkStage() {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(FXMain.getStage());
        dialogStage.getIcons().add(new Image("icon.png"));
        dialogStage.setResizable(true);
        FXMain.shareLinkStage = dialogStage;
    }

    @Override
    public void beforeInitialView(Stage stage,
            ConfigurableApplicationContext ctx) {
        super.beforeInitialView(stage, ctx);
        final AbstractFxmlView view = ctx.getBean(RssView.class);
        if (GUIState.getScene() == null) {
            GUIState.setScene(new Scene(view.getView()));
        }
        Theme appTheme = Theme.valueOf(ctx.getEnvironment().getProperty("app.css", Theme.LIGHT.name()));
        startupCss = appTheme.getPath();
        GUIState.getScene().getStylesheets().add(FXMain.class.getResource(startupCss).toExternalForm());
        stage.setMinWidth(700);
        stage.setMinHeight(650);
    }

    
}
