package Engine;

import Elements.Block.Stone;
import Elements.Block.Wood;
import Elements.Element;
import Elements.Fluid.Fluid;
import Elements.Fluid.Sand;
import Elements.Fluid.Water;
import Elements.Gas.Fire;
import Engine.Saving.GameSave;
import Enums.CollisionStyle;
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
import java.util.*;

public class AlchemyEngine extends Application {
    public static void main(String[] args) {
        AlchemyEngine.launch();
    }

    //GUI
    private Stage stage;
    private FXGraphics2D graphics;
    private final GameView gameView = new GameView(this);

    //Elements
    private ArrayList<Element> elements = new ArrayList<>();
    private final ArrayList<Element> elementsToAdd = new ArrayList<>();
    private final ArrayList<Element> elementsToRemove = new ArrayList<>();

    public static ArrayList<Class<? extends Fluid>> buoyancyList = new ArrayList<>(Arrays.asList(Water.class, Sand.class));       //Lightest to heaviest

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

        try (Scanner fileSearcher = new Scanner("src/Engine/saving/lastSavePath.txt")) {
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

        if (!gameView.isShiftDown()) {                                          //If game is not paused
            for (Element element : elements) {
                boolean isFluid = false;
                LinkedList<Element> collided;
                if (getElementsUnder(Fluid.class).contains(element.getClass())) {       //Make collided -   FLUID
                    Fluid castedElement = (Fluid) element;
                    collided = collisionCheck(castedElement, element.getFilter(), element.getCollisionCheckStyle());
                    isFluid = true;
                } else {                                                                //ELSE
                    collided = collisionCheck(element.getPosition(), element.getFilter(), element.getCollisionCheckStyle());
                }

                if (!collided.isEmpty()) {                                              //If collision
                    if (element.getCollisionCheckStyle() == CollisionStyle.NONE) continue;

                    if (element.collide(collided)) {                                            //Collide
                        if (element.update()) {                                                         //Update
                            removeElement(element);
                        }
                    } else {
                        removeElement(element);
                    }
                } else {                                                                //No collision
                    if (isFluid) {                                                              //If Fluid, set to going down
                        Fluid castedElement = (Fluid) element;
                        castedElement.setMovement(FluidMovement.DOWN);
                    }

                    if (element.update()) {                                             //Update
                        removeElement(element);
                    }
                }
            }
        }
    }

    private void draw() {
        gameView.clear(false);

        for (Element element : elements) {
            element.draw(graphics);
        }

        for (Element element : elementsToRemove) {
            element.draw(graphics);
        }
        elements.removeAll(elementsToRemove);
        elementsToRemove.clear();
    }

    //Collision
    public LinkedList<Element> collisionCheck(Point2D.Double origin, Fluid fluid, List<Class<? extends Element>> filter, CollisionStyle style) {
        LinkedList<Element> collided = new LinkedList<>();

        //NONE
        if (style == CollisionStyle.NONE) {
            return collided;
        }

        //Decision tree
        switch (style) {
            default:
                break;
            case POINT:
                detectCollision(filter, collided, new ArrayList<>(Collections.singletonList(origin)));
                break;
            case UP:
                detectCollision(origin, filter, collided, Direction.UP);
                break;
            case DOWN:
                detectCollision(origin, filter, collided, Direction.DOWN);
                break;
            case LEFT:
                detectCollision(origin, filter, collided, Direction.LEFT);
                break;
            case RIGHT:
                detectCollision(origin, filter, collided, Direction.RIGHT);
                break;
            case FULL:
                detectCollision(origin, filter, collided, Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT);
                break;
            case ROUND:
                detectCollision(origin, filter, collided, new ArrayList<>(Arrays.asList(new Point2D.Double(origin.x + 10, origin.y + 10), new Point2D.Double(origin.x + 10, origin.y - 10), new Point2D.Double(origin.x - 10, origin.y + 10), new Point2D.Double(origin.x - 10, origin.y - 10))), Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT);
                break;
            case FLUID_SIMPLE:
                Point2D.Double position = fluid.getPosition();
                detectCollision(position, filter, collided, new ArrayList<>(Collections.singletonList(new Point2D.Double(position.x, position.y - 10))));

                if (collided.size() == 1) {
                    Element otherElement = collided.get(0);
                    if (buoyancyList.contains(otherElement.getClass())) buoyancyTest(fluid, (Fluid) otherElement);
                }

                break;
            case FLUID_FULL:
                //Make points
                ArrayList<Point2D.Double> allPointsLeft = new ArrayList<>();
                ArrayList<Point2D.Double> allPointsRight = new ArrayList<>();
                origin = fluid.getPosition();

                for (int i = 1; i <= 7; i++) {
                    allPointsLeft.add(new Point2D.Double(origin.x - 10 * i, origin.y));
                    allPointsLeft.add(new Point2D.Double(origin.x - 10 * i, origin.y - 10));
                    allPointsRight.add(new Point2D.Double(origin.x + 10 * i, origin.y));
                    allPointsRight.add(new Point2D.Double(origin.x + 10 * i, origin.y - 10));
                }

                //Fetch collisions from points
                ArrayList<Point2D.Double> emptyPointsLeft = new ArrayList<>(allPointsLeft);
                ArrayList<Point2D.Double> emptyPointsRight = new ArrayList<>(allPointsRight);
                detectCollision(filter, collided, emptyPointsLeft);
                detectCollision(filter, collided, emptyPointsRight);

                //Invert testPoints to only contain points that DID collide
                ArrayList<Point2D.Double> collisionsLeft = new ArrayList<>(allPointsLeft);
                ArrayList<Point2D.Double> collisionsRight = new ArrayList<>(allPointsRight);
                collisionsLeft.removeAll(emptyPointsLeft);
                collisionsRight.removeAll(emptyPointsRight);

                //Check directions
                double wallDistLeft = 100;
                double gapDistLeft = 100;
                double distLeft = 100;
                double distRight = 100;
                double wallDistRight = 100;
                double gapDistRight = 100;

                //WallDist
                //Left
                if (!collisionsLeft.isEmpty()) {
                    for (Point2D.Double collision : collisionsLeft) {
                        double currentDist = origin.x - collision.x;

                        if (collision.y == origin.y && currentDist < wallDistLeft) {
                            wallDistLeft = currentDist;
                        }
                    }
                }
                //Right
                if (!collisionsRight.isEmpty()) {
                    for (Point2D.Double collision : collisionsRight) {
                        double currentDist = collision.x - origin.x;

                        if (collision.y == origin.y && currentDist < wallDistRight) {
                            wallDistRight = currentDist;
                        }
                    }
                }

                //GapDist
                //Left
                if (emptyPointsLeft.size() > 1) {
                    for (Point2D.Double gap : emptyPointsLeft) {
                        double currentDist = origin.x - gap.x;

                        if (emptyPointsLeft.contains(new Point2D.Double(gap.x, gap.y - 10)) && currentDist < gapDistLeft) {
                            gapDistLeft = currentDist;
                        }
                    }
                }
                //Right
                if (emptyPointsRight.size() > 1) {
                    for (Point2D.Double gap : emptyPointsRight) {
                        double currentDist = gap.x - origin.x;

                        if (emptyPointsRight.contains(new Point2D.Double(gap.x, gap.y - 10)) && currentDist < gapDistRight) {
                            gapDistRight = currentDist;
                        }
                    }
                }

                //Check distances
                //Left
                if (wallDistLeft == 100) {
                    distLeft = gapDistLeft;
                } else {
                    if (gapDistLeft < wallDistLeft) {
                        distLeft = wallDistLeft - gapDistLeft;
                    }
                }
                //Right
                if (wallDistRight == 100) {
                    distRight = gapDistRight;
                } else {
                    if (gapDistRight < wallDistRight) {
                        distRight = wallDistRight - gapDistRight;
                    }
                }

                //Make decision
                if (distLeft == 100 && distRight == 100) {
                    fluid.setMovement(FluidMovement.BLOCKED);
                } else if (distLeft == distRight) {
                    Random random = new Random();
                    if (random.nextBoolean()) {
                        fluid.setMovement(FluidMovement.LEFT);
                    } else {
                        fluid.setMovement(FluidMovement.RIGHT);
                    }
                } else if (distLeft < distRight) {
                    fluid.setMovement(FluidMovement.LEFT);
                } else {
                    fluid.setMovement(FluidMovement.RIGHT);
                }

                break;
        }

        return collided;
    }

    public LinkedList<Element> collisionCheck(Point2D.Double origin, List<Class<? extends Element>> filter, CollisionStyle style) {
        return collisionCheck(origin, null, filter, style);
    }

    public LinkedList<Element> collisionCheck(Fluid fluid, List<Class<? extends Element>> filter, CollisionStyle style) {
        return collisionCheck(null, fluid, filter, style);
    }

    private void detectCollision(List<Class<? extends Element>> filter, LinkedList<Element> collisions, ArrayList<Point2D.Double> points) {
        for (Element element : elements) {
            if (filter.contains(element.getClass())) continue;

            if (points.contains(element.getPosition())) {
                collisions.add(element);
                points.removeIf(point -> point.equals(element.getPosition()));
            }

            if (points.size() == 0) {
                break;
            }
        }
    }

    private void detectCollision(Point2D.Double origin, List<Class<? extends Element>> filter, LinkedList<Element> collisions, Direction... directions) {
        detectCollision(origin, filter, collisions, new ArrayList<>(), directions);
    }

    private void detectCollision(Point2D.Double origin, List<Class<? extends Element>> filter, LinkedList<Element> collisions, ArrayList<Point2D.Double> points, Direction... directions) {
        for (Direction direction : directions) {
            switch (direction) {
                case UP:
                    points.add(new Point2D.Double(origin.x, origin.y + 10));
                    break;
                case DOWN:
                    points.add(new Point2D.Double(origin.x, origin.y - 10));
                    break;
                case LEFT:
                    points.add(new Point2D.Double(origin.x - 10, origin.y));
                    break;
                case RIGHT:
                    points.add(new Point2D.Double(origin.x + 10, origin.y));
                    break;
            }
        }

        detectCollision(filter, collisions, points);
    }

    private void buoyancyTest(Fluid fluid, Fluid otherFluid) {
        if (fluid.getClass() == otherFluid.getClass()) return;

        if (buoyancyList.indexOf(fluid.getClass()) > buoyancyList.indexOf(otherFluid.getClass())) {
            Point2D.Double tempPosition = fluid.getPosition();

            fluid.setPosition(otherFluid.getPosition());

            while (true) {
                LinkedList<Element> collisions = new LinkedList<>();
                detectCollision(new ArrayList<>(), collisions, new ArrayList<>(Collections.singletonList(tempPosition)));

                if (!collisions.isEmpty()) {
                    tempPosition.setLocation(tempPosition.x, tempPosition.y + 10);
                } else {
                    otherFluid.setPosition(tempPosition);
                    break;
                }
            }
        }
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

    public void removeElement(Element element) {
        element.draw(graphics);
        elementsToRemove.add(element);
    }

    public void removeAtPoint(Point2D.Double position) {
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
            for (int verI = Math.abs(upDistance + downDistance) / 10 - 2; verI >= 0; verI--) {
                for (int horI = 0; horI < Math.abs(leftDistance + rightDistance) / 10 - 1; horI++) {
                    Point2D.Double currentPosition = new Point2D.Double(startingPosition.x + 10 * horI, startingPosition.y - 10 * verI);

                    try {
                        Element element = elementClass.newInstance();

                        element.setEngine(this);
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

    private int horizontalDistanceCheck(Point2D.Double origin, boolean isLeft, List<Class<? extends Element>> filter) {
        int amount = 10;
        if (!isLeft) {
            amount *= -1;
        }

        for (int i = 0; true; i++) {
            double width = origin.x - amount * i;
            if (width > 1520 || width < 0) break;
            LinkedList<Element> temp = collisionCheck(new Point2D.Double(width, origin.y), filter, CollisionStyle.POINT);
            if (!temp.isEmpty()) {
                return i;
            }
        }

        return 0;
    }

    private int verticalDistanceCheck(Point2D.Double origin, boolean isUp) {
        int amount = 10;
        if (!isUp) {
            amount *= -1;
        }

        for (int i = 0; true; i++) {
            double height = origin.y + amount * i;
            if (height > 1080 || height < 0) break;
            if (!collisionCheck(new Point2D.Double(origin.x, height), new ArrayList<>(), CollisionStyle.POINT).isEmpty()) {
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

            if (collisionCheck(currentPoint, new ArrayList<>(), CollisionStyle.POINT).isEmpty()) {
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

        try (FileWriter fileWriter = new FileWriter("src/Engine/saving/lastSavePath.txt")) {
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
            try (FileWriter fileWriter = new FileWriter("src/Engine/Saving/lastSavePath.txt")) {
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

    public List<Class<? extends Element>> invertFilter(List<Class<? extends Element>> filter) {
        ArrayList<Class<? extends Element>> toReturn = getElementsUnder(Element.class);
        toReturn.removeAll(filter);

        return toReturn;
    }

    public ArrayList<Class<? extends Element>> getElementsUnder(Class<? extends Element> type) {
        ArrayList<Class<? extends Element>> allElements = new ArrayList<>(Arrays.asList(Stone.class, Wood.class, Sand.class, Water.class, Fire.class));
        ArrayList<Class<? extends Element>> remainingElements = new ArrayList<>();

        if (type == Element.class) {
            remainingElements.addAll(allElements);
            return remainingElements;
        }

        for (Class<? extends Element> element : allElements) {
            if (element.getSuperclass() == type) {
                remainingElements.add(element);
            }
        }

        return remainingElements;
    }

    //Getters / Setters
    public Stage getStage() {
        return stage;
    }
}
