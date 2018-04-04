package me.bayang.reader.view;

import org.springframework.beans.factory.annotation.Autowired;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.scene.Parent;
import me.bayang.reader.storage.IStorageService;

@FXMLView(bundle="i18n.translations",value="/fxml/PopupWebView.fxml", encoding = "UTF-8")
public class PopupWebView extends AbstractFxmlView {
    
    @Autowired
    IStorageService config;

    @Override
    public Parent getView() {
        Parent p =  super.getView();
        p.getStylesheets().add(getClass().getResource(config.getAppTheme().getPath()).toExternalForm());
        
        return p;
        
    }

}
