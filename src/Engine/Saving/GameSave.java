package Engine.Saving;

import Elements.Element;
import Engine.AlchemyEngine;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.LinkedList;

@JsonDeserialize(as = GameSave.class)
@SuppressWarnings("unused")
public class GameSave implements Serializable {
    public GameSave() {
    }
    private LinkedList<SavablePoint> savedPoints = new LinkedList<>();

    private LinkedList<String> savedElements  = new LinkedList<>();

    public GameSave(LinkedList<Element> elements) {
        for (Element element : elements) {
            Point2D.Double position = element.getPosition();
            savedPoints.add(new SavablePoint(position.x, position.y));
            savedElements.add(element.getClass().getName());
        }
    }

    public LinkedList<Element> getSavedElements(AlchemyEngine engine) {
        LinkedList<Element> arrayToReturn = new LinkedList<>();

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

    public LinkedList<SavablePoint> getSavedPoints() {
        return savedPoints;
    }

    public void setSavedPoints(LinkedList<SavablePoint> savedPoints) {
        this.savedPoints = savedPoints;
    }

    public LinkedList<String> getSavedElements() {
        return savedElements;
    }

    public void setSavedElements(LinkedList<String> savedElements) {
        this.savedElements = savedElements;
    }
}