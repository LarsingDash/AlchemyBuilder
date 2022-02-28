package GUI;

import Elements.*;
import Elements.Block.Stone;
import Elements.Block.Wood;
import Elements.Fluid.Sand;
import Elements.Fluid.Water;
import Elements.Gas.Fire;
import Engine.AlchemyEngine;
import Enums.Direction;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
        //Buttons
        HBox alchemyButtons = new HBox(this.makeParticleButtons(), this.makeBlockButtons());
        alchemyButtons.setSpacing(20);
        Label elementsLabel = new Label("Elements");
        VBox alchemyButtonsVBox = new VBox(elementsLabel, alchemyButtons);
        alchemyButtonsVBox.setAlignment(Pos.CENTER_LEFT);
        alchemyButtonsVBox.setMinWidth(200);
        this.setLeft(alchemyButtonsVBox);

        VBox gameButtons = new VBox(this.makeGameButtons());
        gameButtons.setAlignment(Pos.CENTER_RIGHT);
        this.setRight(gameButtons);

        //Canvas
        canvas.setWidth(1520);
        canvas.setHeight(1080);
        canvas.setScaleY(-1);
        graphics = new FXGraphics2D(canvas.getGraphicsContext2D());
        VBox canvasBox = new VBox(canvas);
        this.setCenter(canvasBox);

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
        Label particleLabel = new Label("Particles");
        ElementButton fire = new ElementButton(toggleGroup, Fire.class, "fire");
        ElementButton water = new ElementButton(toggleGroup, Water.class, "water");
        ElementButton sand = new ElementButton(toggleGroup, Sand.class, "sand");

        VBox vBox = new VBox(particleLabel, fire, water, sand);
        vBox.setSpacing(20);
        return vBox;
    }

    private VBox makeBlockButtons() {
        //Elements
        Label blocksLabel = new Label("Blocks");
        ElementButton wood = new ElementButton(toggleGroup, Wood.class, "wood");
        ElementButton stone = new ElementButton(toggleGroup, Stone.class, "stone");

        VBox vBox = new VBox(blocksLabel, wood, stone);
        vBox.setSpacing(20);
        return vBox;
    }

    private VBox makeGameButtons() {
        //Elements
        Button load = new Button("load");
        Button save = new Button("save");
        Button reset = new Button("reset");
        Button quit = new Button("quit");

        //Actions
        load.setOnAction(event -> engine.loadSave(true, engine.getStage()));
        save.setOnAction(event -> engine.loadSave(false, engine.getStage()));
        reset.setOnAction(event -> engine.reset());
        quit.setOnAction(event -> engine.attemptQuit());

        return new VBox(load, save, reset, quit);
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
