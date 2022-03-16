package GUI;

import Elements.Block.Coal;
import Elements.Block.Stone;
import Elements.Block.Wood;
import Elements.Element;
import Elements.Fluid.Sand;
import Elements.Fluid.Water;
import Elements.Gas.Fire;
import Engine.AlchemyEngine;
import Enums.Direction;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import org.jfree.fx.FXGraphics2D;

import java.awt.*;
import java.awt.geom.Point2D;

public class GameView extends BorderPane {
    //Engine and other
    private final AlchemyEngine engine;
    private final ToggleGroup toggleGroup = new ToggleGroup();

    //GUI
    private final Canvas canvas = new Canvas();
    private FXGraphics2D graphics;
    private final javafx.scene.text.Font font = new Font("Arial Black", 20);
    private final javafx.scene.text.Font bigFont = new Font("Arial Black", 30);

    //Mouse
    private MouseEvent lastMouseEvent = null;
    private boolean isMousePressed = false;
    private double hor = 0d;
    private double ver = 0d;

    //Shift
    private boolean isShiftDown = false;
    private boolean firstShift = true;
    private double shiftHor = 0d;
    private double shiftVer = 0d;
    private Direction direction = Direction.UP;
    private Direction lastDirection = direction;
    private double width = 0d;
    private double height = 0d;

    //Construction
    public GameView(AlchemyEngine engine) {
        this.engine = engine;
    }

    public FXGraphics2D init() {
        //Alchemy
        Label elementsLabel = new Label("Elements");
        elementsLabel.setFont(bigFont);
        VBox alchemyButtons = new VBox(elementsLabel, makeParticleButtons(), makeBlockButtons());

        alchemyButtons.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.gray(0.6), CornerRadii.EMPTY, Insets.EMPTY)));
        alchemyButtons.setAlignment(Pos.CENTER);
        alchemyButtons.setMinWidth(200);
        alchemyButtons.setMaxHeight(400);
        alchemyButtons.setTranslateY(340);
        alchemyButtons.setSpacing(20);

        //Game
        VBox gameButtons = new VBox(this.makeGameButtons());
        gameButtons.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.gray(0.6), CornerRadii.EMPTY, Insets.EMPTY)));
        gameButtons.setMinWidth(200);
        gameButtons.setMinHeight(200);
        gameButtons.setAlignment(Pos.CENTER);

        //Credits
        Label name1 = new Label("Alchemy");
        Label name2 = new Label("Builder");
        Label name3 = new Label("by LarsingDash");

        name1.setFont(bigFont);
        name2.setFont(bigFont);
        name3.setFont(font);

        VBox credits = new VBox(name1, name2, name3);
        credits.setMinWidth(200);
        credits.setMinHeight(200);
        credits.setAlignment(Pos.CENTER);
        credits.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.gray(0.6), CornerRadii.EMPTY, Insets.EMPTY)));


        //Right side
        VBox rightSide = new VBox(credits, gameButtons);
        rightSide.setMinHeight(500);
        rightSide.setSpacing(100);
        rightSide.setTranslateY(290);

        //Other
        this.setLeft(alchemyButtons);
        this.setRight(rightSide);

        //Canvas
        canvas.setWidth(1520);
        canvas.setHeight(1080);
        canvas.setScaleY(-1);
        graphics = new FXGraphics2D(canvas.getGraphicsContext2D());
        this.setCenter(canvas);

        setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.gray(0.7), CornerRadii.EMPTY, Insets.EMPTY)));

        //Input
        setOnKeyPressed(this::shiftListener);
        setOnKeyReleased(this::shiftListener);

        canvas.setOnMousePressed(event -> mouseUpdate(true, event));
        canvas.setOnMouseReleased(event -> mouseUpdate(false, event));
        canvas.setOnMouseDragged(event -> lastMouseEvent = event);

        return graphics;
    }

    //Mouse
    private void mouseUpdate(boolean mousePressed, MouseEvent mouseEvent) {
        isMousePressed = mousePressed;
        lastMouseEvent = mouseEvent;

        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            if (mousePressed) {
                shiftHor = AlchemyEngine.roundToTens(mouseEvent.getX());
                shiftVer = AlchemyEngine.roundToTens(mouseEvent.getY());
            }

            if (!mousePressed && mouseEvent.isShiftDown() && !firstShift) {
                double horStep = (hor - shiftHor) / 10;
                double verStep = (ver - shiftVer) / 10;
                int amount = 10;

                try {
                    ElementButton selectedButton = (ElementButton) toggleGroup.getSelectedToggle();

                    int startI = 0;
                    if (Math.abs(horStep) > Math.abs(verStep)) {
                        if (horStep < 0) {
                            amount *= -1;
                            startI = 1;
                            horStep--;
                        }

                        for (int i = startI; i < Math.abs(horStep); i++) {
                            Element element = selectedButton.getElement().newInstance();

                            element.setEngine(engine);
                            element.setPosition(new Point2D.Double(shiftHor + amount * i, shiftVer));

                            engine.addElement(element);
                        }
                    } else {
                        double verStartingPoint = shiftVer;
                        if (verStep < 0) {
                            startI = 1;
                            verStep--;
                            verStartingPoint = shiftVer - amount * Math.abs(verStep);
                        }

                        for (int i = startI; i < Math.abs(verStep); i++) {
                            Element element = selectedButton.getElement().newInstance();

                            element.setEngine(engine);
                            element.setPosition(new Point2D.Double(shiftHor, verStartingPoint + amount * i));
                            engine.addElement(element);
                        }
                    }
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }

                clear(false);
            }

            hor = AlchemyEngine.roundToTens(lastMouseEvent.getX());
            ver = AlchemyEngine.roundToTens(lastMouseEvent.getY());
        } else if (mousePressed && isShiftDown) {        //right button
            ElementButton selectedElement = (ElementButton) toggleGroup.getSelectedToggle();
            if (selectedElement != null) {
                engine.fillElement(new Point2D.Double(mouseEvent.getX(), mouseEvent.getY()), selectedElement.getElement());
            }
        }
    }

    public void update() {
        if (lastMouseEvent != null) {
            if (isMousePressed && lastMouseEvent.getButton() == MouseButton.PRIMARY) {
                ElementButton selectedButton = (ElementButton) toggleGroup.getSelectedToggle();
                if (selectedButton != null) {
                    hor = AlchemyEngine.roundToTens(lastMouseEvent.getX());
                    ver = AlchemyEngine.roundToTens(lastMouseEvent.getY());

                    if (!isShiftDown) {
                        try {
                            //Place element
                            Element element = selectedButton.getElement().newInstance();
                            element.setEngine(engine);
                            firstShift = true;

                            element.setPosition(new Point2D.Double(hor, ver));
                            element.init();

                            engine.addElement(element);
                        } catch (InstantiationException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } else {
                        //Remember begin of shift
                        if (firstShift) {
                            doFirstShift(lastMouseEvent.getX(), lastMouseEvent.getY());
                            firstShift = false;
                        }

                        decideShiftLineDirection();

                        //Update direction
                        if (direction != lastDirection) {
                            lastDirection = direction;
                            clear(true);
                        }

                        makeShiftLine();
                    }
                }
            } else if (lastMouseEvent.isSecondaryButtonDown() && !isShiftDown) {
                engine.removeAtPoint(new Point2D.Double(lastMouseEvent.getX(), lastMouseEvent.getY()));
            }
        }
    }

    private void decideShiftLineDirection() {
        if (Math.abs(shiftHor - hor) <= Math.abs(shiftVer - ver)) {         //Up shift
            if (shiftVer - ver < 0) {
                direction = Direction.UP;
            } else {                                                        //Down shift
                direction = Direction.DOWN;
            }
        } else {                                                            //Left shift
            if (shiftHor - hor > 0) {
                direction = Direction.LEFT;
            } else {                                                        //Right shift
                direction = Direction.RIGHT;
            }
        }
    }

    private void makeShiftLine() {
        graphics.setColor(Color.BLACK);
        int tempHor = (int) hor - 5;
        int tempVer = (int) ver - 5;
        int tempShiftHor = (int) shiftHor - 5;
        int tempShiftVer = (int) shiftVer - 5;
        int tempWidth = Math.abs(tempHor - tempShiftHor);
        int tempHeight = Math.abs(tempVer - tempShiftVer);

        if (tempWidth < width || tempHeight < height) clear(true);
        width = tempWidth;
        height = tempHeight;

        switch (direction) {
            case UP:
                graphics.fillRect(tempShiftHor, tempShiftVer, 10, tempHeight);
                break;
            case DOWN:
                graphics.fillRect(tempShiftHor, tempVer, 10, tempHeight);
                break;
            case LEFT:
                graphics.fillRect(tempHor, tempShiftVer, tempWidth, 10);
                break;
            case RIGHT:
                graphics.fillRect(tempShiftHor, tempShiftVer, tempWidth, 10);
                break;
        }
    }

    private void doFirstShift(double x, double y) {
        firstShift = false;

        hor = AlchemyEngine.roundToTens(x);
        ver = AlchemyEngine.roundToTens(y);

        shiftHor = hor;
        shiftVer = ver;
    }

    private void shiftListener(KeyEvent keyEvent) {
        boolean shift = keyEvent.isShiftDown();
        isShiftDown = shift;

        if (shift && lastMouseEvent != null && !lastMouseEvent.isPrimaryButtonDown()) {
            Point point = MouseInfo.getPointerInfo().getLocation();
            hor = (int) AlchemyEngine.roundToTens(point.x - 200);
            ver = (int) AlchemyEngine.roundToTens(1080 - point.y);

            shiftHor = hor;
            shiftVer = ver;
        }
    }

    //Making buttons
    private VBox makeParticleButtons() {
        //Elements
        Label particleLabel = new Label("particles");
        particleLabel.setFont(font);

        ElementButton fire = new ElementButton(toggleGroup, Fire.class, "fire");
        ElementButton water = new ElementButton(toggleGroup, Water.class, "water");
        ElementButton sand = new ElementButton(toggleGroup, Sand.class, "sand");

        HBox buttons = new HBox(fire, water, sand);
        buttons.setSpacing(10);
        buttons.setAlignment(Pos.CENTER);

        VBox full = new VBox(particleLabel, buttons);
        full.setAlignment(Pos.CENTER);
        full.setSpacing(10);
        return full;
    }

    private VBox makeBlockButtons() {
        //Elements
        Label blocksLabel = new Label("blocks");
        blocksLabel.setFont(font);

        ElementButton coal = new ElementButton(toggleGroup, Coal.class, "coal");
        ElementButton wood = new ElementButton(toggleGroup, Wood.class, "wood");
        ElementButton stone = new ElementButton(toggleGroup, Stone.class, "stone");

        HBox buttons = new HBox(coal, wood, stone);
        buttons.setSpacing(10);
        buttons.setAlignment(Pos.CENTER);

        VBox full = new VBox(blocksLabel, buttons);
        full.setAlignment(Pos.CENTER);
        full.setSpacing(10);
        return full;
    }

    private VBox makeGameButtons() {
        //Elements
        GUIButton load = new GUIButton("load");
        GUIButton save = new GUIButton("save");
        GUIButton reset = new GUIButton("reset");
        GUIButton quit = new GUIButton("quit");

        HBox top = new HBox(load, save);
        HBox bottom = new HBox(reset, quit);
        VBox full = new VBox(top, bottom);

        top.setSpacing(15);
        bottom.setSpacing(15);
        full.setSpacing(15);

        top.setAlignment(Pos.CENTER);
        bottom.setAlignment(Pos.CENTER);
        full.setAlignment(Pos.CENTER);

        //Actions
        load.setOnAction(event -> engine.loadSave(true, engine.getStage()));
        save.setOnAction(event -> engine.loadSave(false, engine.getStage()));
        reset.setOnAction(event -> engine.reset());
        quit.setOnAction(event -> engine.attemptQuit());

        return full;
    }

    //Screen wiping
    public void clear(boolean force) {
        if (!isShiftDown || force) {
            graphics.setBackground(Color.decode("#d6d6d6"));
            graphics.clearRect(0, 0, (int) canvas.getWidth(), (int) canvas.getHeight());
        }
    }

    public boolean isShiftDown() {
        return isShiftDown;
    }
}
