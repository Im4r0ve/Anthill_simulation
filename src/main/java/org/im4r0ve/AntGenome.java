package org.im4r0ve;

/**
 * Stores the whole ant genome that is needed for the creation of an ant.
 */
public class AntGenome
{
    private int health;
    private int strength;
    private int speed;
    private float viewRange;
    private int weight;
    private int pheromoneTrailFood;
    private int pheromoneTrail;

    public AntGenome(int health, int weight, int speed, int strength, float viewRange, int pheromoneTrail, int pheromoneTrailFood)
    {
        this.health = health;
        this.strength = strength;
        this.speed = speed;
        this.viewRange = viewRange;
        this.weight = weight;
        this.pheromoneTrailFood = pheromoneTrailFood;
        this.pheromoneTrail = pheromoneTrail;
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

    public int getPheromoneTrailFood()
    {
        return pheromoneTrailFood;
    }

    public int getPheromoneTrail()
    {
        return pheromoneTrail;
    }
}
