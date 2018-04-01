package me.bayang.reader.components;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import me.bayang.reader.FXMain;

public class DeletableLabel extends HBox {
    
    private String content;
    
    private FontAwesomeIconView deleteCross;

    public DeletableLabel(String content) {
        this.content = content;
        Label l = new Label(content);
        l.getStyleClass().add("deletable-content");
        l.setPadding(new Insets(0, 5, 0, 3));
        this.getChildren().add(l);
        FontAwesomeIconView fv = new FontAwesomeIconView(FontAwesomeIcon.TIMES, "18");
        fv.getStyleClass().add("deletable-cross");
//        fv.setFill(FXMain.invertedColor);
//        fv.setOnMouseEntered(event -> fv.setFill(FXMain.primaryColor));
//        fv.setOnMouseExited(event -> fv.setFill(FXMain.invertedColor));
        this.getChildren().add(fv);
        this.deleteCross = fv;
        this.setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
        this.setAlignment(Pos.CENTER);
        this.getStyleClass().add("deletable-box");
    }

    public FontAwesomeIconView getDeleteCross() {
        return deleteCross;
    }

    public void setDeleteCross(FontAwesomeIconView deleteCross) {
        this.deleteCross = deleteCross;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
}
