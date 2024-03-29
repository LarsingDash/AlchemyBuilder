package Elements;


import Engine.AlchemyEngine;
import Enums.CollisionStyle;
import org.jfree.fx.FXGraphics2D;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public abstract class Element implements Cloneable {
    //Engine
    private AlchemyEngine engine;

    //Properties
    private Point2D.Double position;
    private final CollisionStyle collisionStyle;
    private Color color;
    private List<Class<? extends Element>> filter;

    //Lifespan
    private int lifespan;
    private int age = 0;

    //Fire
    private final boolean isFlammable;
    private final boolean isBurnable;
    private boolean isLit = false;
    private int burnCount = 0;

    //Constructors
    public Element(CollisionStyle collisionStyle, Color color, boolean isFlammable, boolean isBurnable, int lifespan) {
        //Properties
        this.position = new Point2D.Double(0,0);
        this.collisionStyle = collisionStyle;
        this.color = color;

        //Lifespan
        this.lifespan = lifespan;

        //Fire
        this.isFlammable = isFlammable;
        this.isBurnable = isBurnable;
    }

    //Behavioral
    public boolean update() {       //Return true if killed
        //Check for worldBorder
        if (position.x <= 0 || position.x >= 1520 || position.y <= 0 || position.y >= 1080) return true;   //WorldBorder

        //Burn
        if (isFlammable && isLit) {
            burnCount++;

            if (isBurnable) {
                color = updateColorFade();

                //Kill
                if (burnCount == 30) {
                    return true;
                }
            }
        }

        //Lifespan
        if (lifespan != 0) {
            age++;

            if (age == lifespan) {
                return true;
            }
        }

        //Behavior
        behave();

        return position.x >= 1520 || position.x <= 0 || position.y >= 1080 || position.y <= 0;
    }

    abstract public void behave();

    abstract public boolean collide(LinkedList<Element> collided);

    abstract public void initFilter();

    private Color updateColorFade() {
        float fraction = (float) burnCount / 30;
        fraction = Math.min(1.0f, fraction);

        int red = (int) (fraction * Color.RED.getRed() + (1 - fraction) * color.getRed());
        int green = (int) (fraction * Color.RED.getGreen() + (1 - fraction) * color.getGreen());
        int blue = (int) (fraction * Color.RED.getBlue() + (1 - fraction) * color.getBlue());
        return new Color(red, green, blue);
    }

    public void init() {
        //Lifespan
        if (lifespan != 0) {
            Random random = new Random();
            lifespan = Math.max(1, lifespan + (random.nextInt(10) - 5));
        }

        //Filter
        initFilter();
    }

    //Getters and Setters
    public AlchemyEngine getEngine() {
        return engine;
    }

    public void setEngine(AlchemyEngine engine) {
        this.engine = engine;
        init();
    }

    public Point2D.Double getPosition() {
        return position;
    }

    public void setPosition(Point2D.Double position) {
        this.position = position;
    }

    public CollisionStyle getCollisionCheckStyle() {
        return collisionStyle;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public List<Class<? extends Element>> getFilter () {
        return filter;
    }

    public void setFilter(List<Class<? extends Element>> filter) {
        this.filter = filter;
    }

    public boolean isLit() {
        return isLit;
    }

    public void setLit(boolean lit) {
        isLit = lit;
    }

    public boolean isFlammable() {
        return isFlammable;
    }

    public int getBurnCount() {
        return burnCount;
    }

    //Other
    public void draw(FXGraphics2D graphics) {
        graphics.setColor(color);
        graphics.fillRect((int) position.x - 5,(int) position.y - 5,10,10);
    }

    @Override
    public String toString() {
        return this.getClass().getName();
    }

    public Element clone() {
        try {
            return (Element) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return null;
    }
}
