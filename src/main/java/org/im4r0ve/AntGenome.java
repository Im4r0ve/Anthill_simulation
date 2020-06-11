package org.im4r0ve;

public class AntGenome
{
    private int health;
    private int strength;
    private int speed;
    private float viewRange;
    private int weight;

    public AntGenome(){}
    public AntGenome(int health, int weight,  int speed,int strength, float viewRange)
    {
        this.health = health;
        this.strength = strength;
        this.speed = speed;
        this.viewRange = viewRange;
        this.weight = weight;
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

    public int getWeight()
    {
        return weight;
    }
}
