package Engine;

import Elements.Element;
import Engine.Saving.GameSave;
import Enums.Direction;
import Enums.FluidMovement;
import GUI.GameView;
import GUI.Popup;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.jfree.fx.FXGraphics2D;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//todo change screen size to match 5 pixel shift in element rendering
public class AlchemyEngine extends Application {
    public static void main(String[] args) {
        AlchemyEngine.launch();
    }

    //GUI
    private Stage stage;
    private FXGraphics2D graphics;
    private final GameView gameView = new GameView(this);

    //Elements
    private final ArrayList<Element> elementsToAdd = new ArrayList<>();
    private ArrayList<Element> elements = new ArrayList<>();

    //Data
    private boolean isSaved = true;

    //Engine
    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        //Stage preparations
        stage.setWidth(1920);
        stage.setHeight(1080);
        stage.setTitle("AlchemyBuilder");
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setFullScreen(true);

        graphics = gameView.init();

        //Timer
        new AnimationTimer() {
            long last = -1;
            boolean drawToggle = true;

            @Override
            public void handle(long now) {
                if (now - last > (1 / 100d) * 1e9) {
                    update();
                    if (drawToggle) draw();
                    last = now;
                    drawToggle = !drawToggle;
                }
            }
        }.start();

        //Final things
        stage.setScene(new Scene(gameView));
        stage.show();

        try (Scanner fileSearcher = new Scanner("src/Engine/lastSavePath.txt")) {
            File file = new File(fileSearcher.nextLine());
            try (Scanner fileReader = new Scanner(file)) {
                if (file.length() > 0) loadSave(true, stage, new File(fileReader.nextLine()));
            }
        }
    }

    private void update() {
        gameView.update();
        elements.addAll(elementsToAdd);
        elementsToAdd.clear();
        if (!gameView.isShiftDown()) {
            elements.removeIf(element -> !element.update());
        }
    }

    private void draw() {
        gameView.clear(false);

        for (Element element : elements) {
            element.draw(graphics);
        }
    }

    //Collision
    public ArrayList<Element> detectCollision(Point2D.Double position, List<Class<? extends Element>> filter, boolean onlyFilter, Point2D.Double blindSpot, int range) {
        ArrayList<Element> collisions = new ArrayList<>();

        for (Element element : elements) {
            if ((filter.contains(element.getClass()) && !onlyFilter) || (!filter.contains(element.getClass()) && onlyFilter)) continue;
            if (position.equals(element.getPosition()) || position.equals(blindSpot)) continue;
            System.out.println(element.getName() + " || " + position + " || " + element.getPosition());

            if (position.distance(element.getPosition()) < range) collisions.add(element);
        }

        return collisions;
    }

    public ArrayList<Element> detectCollision(Point2D.Double position, List<Class<? extends Element>> filter, boolean onlyFilter, Point2D.Double blindSpot) {
        return detectCollision(position, filter, onlyFilter, blindSpot, 10);
    }

    public ArrayList<Element> detectCollision(Point2D.Double position, List<Class<? extends Element>> filter, boolean onlyFilter) {
        return detectCollision(position, filter, onlyFilter, new Point2D.Double(-1,-1));
    }

    public ArrayList<Element> detectCollision(Point2D.Double position, List<Class<? extends Element>> filter) {
        return detectCollision(position, filter, false);
    }

    public ArrayList<Element> detectCollision(Point2D.Double position) {
        return detectCollision(position, new ArrayList<>());
    }

    //Water
    public FluidMovement moveFluid(Element fluid, int allowedDistance) {
//        double height = fluid.getPosition().y;
//        double maxLeft = 0d;
//        double maxRight = 0d;
//        double minX = allowedDistance;
//
//        for (Element element : elements) {                                              //Test if element is not itself
//            if (element == fluid) {
//                continue;
//            }
//
//            double otherHeight = element.getPosition().y;
//            if (height - otherHeight <= 5 && height - otherHeight >= 0) {               //Check deltaY
//                double deltaX = element.getPosition().x - fluid.getPosition().x;        //Check deltaX
//
//                if (Math.abs(deltaX) <= allowedDistance) {
//                    if (Math.abs(deltaX) < minX) {                                      //Check minX
//                        minX = Math.abs(deltaX);
//                    }
//
//                    if (deltaX < maxLeft) {                                             //Check maxes
//                        maxLeft = deltaX;
//                    } else if (deltaX > maxRight) {
//                        maxRight = deltaX;
//                    }
//                }
//            }
//        }
//
//        if (minX <= 10) {
//            if (maxLeft == 0 && maxRight == 0) {
//                return FluidMovement.BLOCKED;
//            } else if (Math.abs(maxLeft) < maxRight) {
//                return FluidMovement.LEFT;
//            } else if (Math.abs(maxLeft) > maxRight) {
//                return FluidMovement.RIGHT;
//            } else {
//                Random random = new Random();
//                if (random.nextBoolean()) {
//                    return FluidMovement.LEFT;
//                } else {
//                    return FluidMovement.RIGHT;
//                }
//            }
//        } else {
//            return FluidMovement.DOWN;
//        }

        allowedDistance = 20;

//        List<Class<? extends Element>> filter = Collections.singletonList(Water.class);
        List<Class<? extends Element>> filter = new ArrayList<>();
        System.out.println(horizontalDistanceCheck(fluid.getPosition(),  true, filter, fluid.getPosition()));
//        boolean leftPossible = horizontalDistanceCheck(fluid.getPosition(), true, filter, false) >= allowedDistance;
//        boolean rightPossible = horizontalDistanceCheck(fluid.getPosition(), false, filter, false) >= allowedDistance;

//        System.out.println(leftPossible + " " + rightPossible);

        return FluidMovement.BLOCKED;
    }

    public FluidMovement moveFluid(Element fluid) {
        return moveFluid(fluid, 75);
    }

    //Element
    public boolean addElement(Element element) {
        boolean tooClose = false;
        for (Element otherElement : elements) {
            if (element.equals(otherElement)) continue;

            if (element.getPosition().distance(otherElement.getPosition()) < 10) {
                tooClose = true;
                break;
            }
        }

        if (!tooClose) {
            element.setPosition(new Point2D.Double(roundToTens(element.getPosition().x), roundToTens(element.getPosition().y)));
            elementsToAdd.add(element);
            isSaved = false;
            return true;
        } else {
            return false;
        }
    }

    public void removeElement(Point2D.Double position) {
        elements.removeIf(element -> element.getPosition().distance(position) < 10);
        isSaved = false;
    }

    //BucketFill
    public void fillElement(Point2D.Double origin, Class<? extends Element> elementClass) {
        origin = new Point2D.Double(roundToTens(origin.x), roundToTens(origin.y));

        //Find distances
        ArrayList<Class<? extends Element>> empty = new ArrayList<>();
        int upDistance = verticalDistanceCheck(origin, true);
        int downDistance = verticalDistanceCheck(origin, false);
        int leftDistance = horizontalDistanceCheck(origin, true, empty);
        int rightDistance = horizontalDistanceCheck(origin, false, empty);

        upDistance *= 10;
        downDistance *= 10;
        leftDistance *= 10;
        rightDistance *= 10;

        //Trace edges for gaps
        boolean topEdge = traceEdge(Direction.UP, origin, upDistance, (int) origin.x - leftDistance, (int) origin.x + rightDistance);
        boolean bottomEdge = traceEdge(Direction.DOWN, origin, downDistance, (int) origin.x - leftDistance, (int) origin.x + rightDistance);
        boolean leftEdge = traceEdge(Direction.LEFT, origin, leftDistance, (int) origin.y + upDistance, (int) origin.y - downDistance);
        boolean rightEdge = traceEdge(Direction.RIGHT, origin, rightDistance, (int) origin.y + upDistance, (int) origin.y - downDistance);

        if (topEdge && bottomEdge && leftEdge && rightEdge) {       //Fill area
            Point2D.Double startingPosition = new Point2D.Double(origin.x - leftDistance + 10, origin.y + upDistance - 10);

            all:
            for (int verI = 0; verI < Math.abs(upDistance + downDistance) / 10 - 1; verI++) {
                for (int horI = 0; horI < Math.abs(leftDistance + rightDistance) / 10 - 1; horI++) {
                    Point2D.Double currentPosition = new Point2D.Double(startingPosition.x + 10 * horI, startingPosition.y - 10 * verI);

                    try {
                        Element element = elementClass.newInstance();
                        element.setPosition(currentPosition);
                        if (!addElement(element)) {
                            elementsToAdd.clear();
                            break all;
                        }
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private int horizontalDistanceCheck(Point2D.Double origin, boolean isLeft, List<Class<? extends Element>> filter, Point2D.Double blindSpot) {
        int amount = 10;
        if (!isLeft) {
            amount *= -1;
        }

        for (int i = 0; true; i++) {
            double width = origin.x - amount * i;
            if (width > 1520 || width < 0) break;
            ArrayList<Element> temp = detectCollision(new Point2D.Double(width, origin.y), filter, false, blindSpot);
            if (!temp.isEmpty()) {
                System.out.println(temp);
                System.out.println(origin + " | " + temp.get(0).getPosition());
                return i;
            }
        }

        return 0;
    }

    private int horizontalDistanceCheck(Point2D.Double origin, boolean isLeft, List<Class<? extends Element>> filter) {
        return horizontalDistanceCheck(origin, isLeft, filter, new Point2D.Double(-1,-1));
    }

    private int verticalDistanceCheck(Point2D.Double origin, boolean isUp) {
        int amount = 10;
        if (!isUp) {
            amount *= -1;
        }

        for (int i = 0; true; i++) {
            double height = origin.y + amount * i;
            if (height > 1080 || height < 0) break;
            if (!detectCollision(new Point2D.Double(origin.x, height)).isEmpty()) {
                return i;
            }
        }

        return 0;
    }

    private boolean traceEdge(Direction side, Point2D.Double origin, int distanceFromOrigin, int pointA, int pointB) {
        boolean toReturn = true;

        Point2D.Double currentPoint;
        Point2D.Double moveToPoint;

        switch (side) {     //Prime variables
            case UP:
                currentPoint = new Point2D.Double(pointA, origin.y + distanceFromOrigin);
                moveToPoint = new Point2D.Double(10, 0);
                break;
            case DOWN:
                currentPoint = new Point2D.Double(pointA, origin.y - distanceFromOrigin);
                moveToPoint = new Point2D.Double(10, 0);
                break;
            case LEFT:
                currentPoint = new Point2D.Double(origin.x - distanceFromOrigin, pointA);
                moveToPoint = new Point2D.Double(0, -10);
                break;
            default:
                currentPoint = new Point2D.Double(origin.x + distanceFromOrigin, pointA);
                moveToPoint = new Point2D.Double(0, -10);
                break;
        }

        //Prime starting point for loop
        currentPoint = new Point2D.Double(currentPoint.x - moveToPoint.x, currentPoint.y - moveToPoint.y);

        for (int i = 0; i < Math.abs(pointA - pointB) / 10 + 1; i++) {      //Check edge
            currentPoint = new Point2D.Double(currentPoint.x + moveToPoint.x, currentPoint.y + moveToPoint.y);

            if (detectCollision(currentPoint).isEmpty()) {
                toReturn = false;
                break;
            }
        }

        return toReturn;
    }

    //Game
    public boolean loadSave(boolean isLoad, Window window) {
        return loadSave(isLoad, window, null);
    }

    public boolean loadSave(boolean isLoad, Window window, File file) {
        if (isLoad) {
            ObjectReader objectReader = new ObjectMapper().reader();
            if (file == null) {
                file = buildFileChooser().showOpenDialog(window);
            }

            try (Scanner scanner = new Scanner(file)) {
                elements = objectReader.readValue(scanner.nextLine(), GameSave.class).getSavedElements(this);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                return false;
            }
        } else {
            ObjectWriter objectWriter = new ObjectMapper().writer();
            GameSave gameSave = new GameSave(elements);
            if (file == null) {
                file = buildFileChooser().showSaveDialog(window);
            }

            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(objectWriter.writeValueAsString(gameSave));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                return false;
            }
        }

        isSaved = true;
        stage.requestFocus();

        try (FileWriter fileWriter = new FileWriter("src/Engine/lastSavePath.txt")) {
            fileWriter.write(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public void reset() {
        if (!isSaved && !elements.isEmpty()) {
            Popup popup = new Popup(this, "reset your alchemy", "You have not yet saved!", "reset", "cancel") {
                @Override
                public void yesButton() {
                    elements.clear();
                    elementsToAdd.clear();
                    isSaved = false;
                    this.close();
                }

                @Override
                public void noButton() {
                    this.close();
                }
            };
            popup.start();
        } else {
            elements.clear();
            elementsToAdd.clear();
            isSaved = false;
        }
    }

    public void attemptQuit() {
        if (!isSaved && !elements.isEmpty()) {
            Popup popup = new Popup(this, "quit without saving", "Your alchemy is not saved!", "save", "quit") {
                @Override
                public void yesButton() {
                    if (loadSave(false, this)) {
                        quit();
                        this.close();
                    }
                }

                @Override
                public void noButton() {
                    quit();
                    this.close();
                }
            };
            popup.start();
        } else {
            quit();
        }
    }

    public void quit() {
        if (elements.isEmpty()) {
            try (FileWriter fileWriter = new FileWriter("src/Engine/lastSavePath.txt")) {
                fileWriter.write("");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        stage.close();
    }

    //Helper
    private static FileChooser buildFileChooser() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
        fileChooser.setInitialFileName("myAlchemy");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("alchemy", "*.alchemy"));

        return fileChooser;
    }

    public static double roundToTens(double num) {
        return Math.round(num / 10d) * 10;
    }

    //Getters / Setters
    public Stage getStage() {
        return stage;
    }
}
