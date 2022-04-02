package GUI;

import Engine.AlchemyEngine;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;

abstract public class Popup extends Stage {
    private final AlchemyEngine engine;
    private final String mainText;
    private final String extraText;

    private final String leftText;
    private final String rightText;

    public Popup(AlchemyEngine engine, String mainText, String extraText, String leftText, String rightText) {
        this.engine = engine;
        this.mainText = mainText;
        this.extraText = extraText;

        this.leftText = leftText;
        this.rightText = rightText;
    }

    public void start() {
        this.setWidth(500);
        this.setHeight(500);
        this.setResizable(false);
        this.initOwner(engine.getStage());
        this.initModality(Modality.APPLICATION_MODAL);

        Label mainText = new Label("Are you sure you want to " + this.mainText + "?");
        Label extraLabel = new Label(this.extraText);
        Button left = new Button(leftText);
        Button right = new Button(rightText);

        HBox buttons = new HBox(left, right);
        VBox content = new VBox(mainText, extraLabel, buttons);
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(50);
        content.setAlignment(Pos.CENTER);
        this.setScene(new Scene(content));

        left.setOnAction(event -> yesButton());
        right.setOnAction(event -> noButton());
        Runnable sound = (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.exclamation");
        sound.run();
        show();
    }

    abstract public void yesButton();

    abstract public void noButton();
}
