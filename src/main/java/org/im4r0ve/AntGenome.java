package org.im4r0ve;

import javafx.scene.paint.Color;

public class AntGenome
{
    private int health;
    private int strength;
    private int speed;
    private float viewRange;
    private Color color;

    public AntGenome(int health, int strength, int speed, int viewRange, Color color)
    {
        this.health = health;
        this.strength = strength;
        this.speed = speed;
        this.viewRange = viewRange;
        this.color = color;
    }

    public int getHealth()
    {
        return health;
    }

    public int getStrength()
    {
        return strength;
    }

    public int getSpeed()
    {
        return speed;
    }

    public float getViewRange()
    {
        return viewRange;
    }

    public Color getColor()
    {
        return color;
    }
}
