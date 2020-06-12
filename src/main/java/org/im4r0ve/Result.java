package org.im4r0ve;

/**
 * Packaged result of the simulation
 */
public class Result
{
    private Tile[][] map;
    private int population;
    private int stepsTaken;

    public Result(Tile[][] map,int population, int stepsTaken)
    {
        this.map = map;
        this.population = population;
        this.stepsTaken = stepsTaken;
    }

    public Tile[][] getMap()
    {
        return map;
    }

    public int getStepsTaken()
    {
        return stepsTaken;
    }

    public int getPopulation()
    {
        return population;
    }
}
