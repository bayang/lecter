package me.bayang.reader.components;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class DeletableLabel extends HBox {
    
    private String content;
    
    private FontAwesomeIconView deleteCross;

    public DeletableLabel(String content) {
        this.content = content;
        Label l = new Label(content);
        l.setPadding(new Insets(0, 2, 0, 2));
        this.getChildren().add(l);
        FontAwesomeIconView fv = new FontAwesomeIconView(FontAwesomeIcon.TIMES, "15");
        fv.setOnMouseEntered(event -> fv.setFill(Color.CORNFLOWERBLUE));
        fv.setOnMouseExited(event -> fv.setFill(Color.BLACK));
        this.getChildren().add(fv);
        this.deleteCross = fv;
        this.setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
//        this.getStyleClass().add("contact-card");
        this.setAlignment(Pos.CENTER);
        
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
