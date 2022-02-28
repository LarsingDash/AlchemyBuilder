package Engine.Saving;

import Elements.Element;
import Engine.AlchemyEngine;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;

@JsonDeserialize(as = GameSave.class)
@SuppressWarnings("unused")
public class GameSave implements Serializable {
    public GameSave() {
    }
    private ArrayList<SavablePoint> savedPoints = new ArrayList<>();

    private ArrayList<String> savedElements  = new ArrayList<>();

    public GameSave(ArrayList<Element> elements) {
        for (Element element : elements) {
            Point2D.Double position = element.getPosition();
            savedPoints.add(new SavablePoint(position.x, position.y));
            savedElements.add(element.getClass().getName());
        }
    }

    public ArrayList<Element> getSavedElements(AlchemyEngine engine) {
        ArrayList<Element> arrayToReturn = new ArrayList<>();

        for (SavablePoint position : savedPoints) {
            try {
                Class<?> c = Class.forName(savedElements.get(savedPoints.indexOf(position)));
                Element newInstance = (Element) c.newInstance();
                newInstance.setEngine(engine);
                newInstance.setPosition(new Point2D.Double(position.getX(), position.getY()));
                arrayToReturn.add(newInstance);
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return arrayToReturn;
    }

    public ArrayList<SavablePoint> getSavedPoints() {
        return savedPoints;
    }

    public void setSavedPoints(ArrayList<SavablePoint> savedPoints) {
        this.savedPoints = savedPoints;
    }

    public ArrayList<String> getSavedElements() {
        return savedElements;
    }

    public void setSavedElements(ArrayList<String> savedElements) {
        this.savedElements = savedElements;
    }
}