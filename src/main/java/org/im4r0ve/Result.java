package org.im4r0ve;

/**
 * Packaged result of the simulation
 */
public class Result
{
    private Tile[][] map;
    private int[][] overlay;
    private int population;

    public Result(int[][] overlay, int population)
    {
        this.overlay = overlay;
        this.population = population;
    }

    public Tile[][] getMap()
    {
        return map;
    }

    public int[][] getOverlay()
    {
        return overlay;
    }

    public void setMap(Tile[][] map)
    {
        this.map = map;
    }

    public int getPopulation()
    {
        return population;
    }
}
