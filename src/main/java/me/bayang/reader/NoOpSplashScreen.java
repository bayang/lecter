package me.bayang.reader;

import de.felixroske.jfxsupport.SplashScreen;
import javafx.scene.Parent;

public class NoOpSplashScreen extends SplashScreen {

    @Override
    public Parent getParent() {
        return super.getParent();
    }

    @Override
    public boolean visible() {
        return false;
    }

    @Override
    public String getImagePath() {
        return super.getImagePath();
    }

}
