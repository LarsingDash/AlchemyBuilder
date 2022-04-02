package GUI;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.io.File;

public class GUIButton extends Button {
    public GUIButton(String name) {
        setMinSize(50,50);
        setBackground(new Background(new BackgroundImage(new Image(new File("src/Resources/Other/" + name + ".png").toURI().toString()), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        setTooltip(new Tooltip(name));
    }
}