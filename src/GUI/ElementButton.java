package GUI;

import Elements.Element;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.io.File;

public class ElementButton extends ToggleButton {
    private final Class<? extends Element> element;

    public ElementButton(ToggleGroup toggleGroup, Class<? extends Element> element, String imageURL) {
        this.setToggleGroup(toggleGroup);
        this.element = element;

        this.setMinSize(50,50);
        setBackground(new Background(new BackgroundImage(new Image(new File("src/resources/" + imageURL + ".png").toURI().toString()), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
    }

    public Class<? extends Element> getElement() {
        return element;
    }
}
