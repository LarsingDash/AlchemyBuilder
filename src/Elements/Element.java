package Elements;


import Engine.AlchemyEngine;
import org.jfree.fx.FXGraphics2D;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Random;

public abstract class Element implements Cloneable {
    //Engine
    private AlchemyEngine engine;

    //Properties
    private Point2D.Double position;
    private final String name;
    private Color color;

    //Lifespan
    private int lifespan;
    private int age = 0;

    //Fire
    private final boolean isFlammable;
    private boolean isLit = false;
    private int burnCount = 0;

    //Constructors
    public Element(AlchemyEngine engine, Point2D.Double position, String name, Color color, boolean isFlammable, int lifespan) {
        //Engine
        this.engine = engine;

        //Properties
        this.position = position;
        this.name = name;
        this.color = color;

        //Lifespan
        this.lifespan = lifespan;

        //Fire
        this.isFlammable = isFlammable;

        init();
    }

    //Behavioral
    public boolean update() {
        if (position.x <= 0 || position.x >= 1520 || position.y <= 0 || position.y >= 1080) return false;   //WorldBorder
        if (isFlammable && isLit) {                                                                         //Burn
            burnCount++;
            color = updateColorFade(Color.RED);

            //Kill
            if (burnCount == 30) {
                return false;
            }
        }

        if (lifespan != 0) {
            age++;

            if (age == lifespan) {
                return false;
            }
        }

        return behave();
    }

    abstract public boolean behave();

    private Color updateColorFade(Color toFadeTo) {
        float fraction = (float) burnCount / 30;
        fraction = Math.min(1.0f, fraction);

        int red = (int) (fraction * toFadeTo.getRed() + (1 - fraction) * color.getRed());
        int green = (int) (fraction * toFadeTo.getGreen() + (1 - fraction) * color.getGreen());
        int blue = (int) (fraction * toFadeTo.getBlue() + (1 - fraction) * color.getBlue());
        return new Color(red, green, blue);
    }

    public void init() {
        //Lifespan
        if (lifespan != 0) {
            Random random = new Random();
            lifespan = Math.max(1, lifespan + (random.nextInt(10) - 5));
        }
    }

    //Getters and Setters
    public AlchemyEngine getEngine() {
        return engine;
    }

    public void setEngine(AlchemyEngine engine) {
        this.engine = engine;
    }

    public Point2D.Double getPosition() {
        return position;
    }

    public void setPosition(Point2D.Double position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isLit() {
        return isLit;
    }

    public int getBurnCount() {
        return burnCount;
    }

    public void setLit(boolean lit) {
        isLit = lit;
    }

    //Other
    public void draw(FXGraphics2D graphics) {
        graphics.setColor(color);
        graphics.fillRect((int) position.x - 5,(int) position.y - 5,10,10);
    }

    @Override
    public String toString() {
        return name;
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
