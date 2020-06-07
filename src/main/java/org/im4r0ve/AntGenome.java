package org.im4r0ve;

public class AntGenome
{
    private int health;
    private int strength;
    private int speed;
    private int viewRange;

    public AntGenome(int health,int strength,int speed, int viewRange)
    {
        this.health = health;
        this.strength = strength;
        this.speed = speed;
        this.viewRange = viewRange;
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

    public int getViewRange()
    {
        return viewRange;
    }
}
