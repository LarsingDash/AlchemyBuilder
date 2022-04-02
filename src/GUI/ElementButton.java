package GUI;

import Elements.Element;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.io.File;

public class ElementButton extends ToggleButton {
    private final Class<? extends Element> element;
    private final BackgroundImage normal;
    private final BackgroundImage selected;

    public ElementButton(ToggleGroup toggleGroup, Class<? extends Element> element, String imageURL) {
        this.setToggleGroup(toggleGroup);
        this.element = element;

        setMinSize(50, 50);
        normal = new BackgroundImage(new Image(new File("src/Resources/Elements/" + imageURL + ".png").toURI().toString()), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        selected = new BackgroundImage(new Image(new File("src/Resources/ElementsSelected/" + imageURL + "Selected.png").toURI().toString()), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);

        setBackground(new Background(normal));
        setTooltip(new Tooltip(imageURL));

        setOnAction(event -> {
            for (Toggle toggle : toggleGroup.getToggles()) {
                ElementButton button = (ElementButton) toggle;
                button.select(button.isSelected());
            }
        });
    }

    public Class<? extends Element> getElement() {
        return element;
    }

    public void select(boolean isSelected) {
        if (isSelected) {
            setBackground(new Background(selected));
        } else {
            setBackground(new Background(normal));
        }
    }
}
