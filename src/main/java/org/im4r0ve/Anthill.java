package org.im4r0ve;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Anthill
{
    private Simulation sim;

    private ArrayList<Ant> ants;
    private Color antColor;
    private ArrayList<AntGenome> antGenomes;
    private double reproductionRate;

    private int x;
    private int y;
    private int food;

    private int[][] pheromoneMap;
    private int basePheromoneLevel;

    /**
     * Initializes Anthill and spawn initial number of ants
     */
    public Anthill(int x, int y,Simulation sim,ArrayList<AntGenome> antGenomes, int initAnts, double reproductionRate, int basePheromoneLevel, Color antColor)
    {
        this.x = x;
        this.y = y;
        this.sim = sim;
        this.antGenomes = antGenomes;
        this.reproductionRate = reproductionRate;
        this.basePheromoneLevel = basePheromoneLevel;
        this.antColor = antColor;

        food = 0;
        ants = new ArrayList<>();
        pheromoneMap = new int[sim.getWidth()][sim.getHeight()];
        for (int[] row: pheromoneMap)
            Arrays.fill(row, basePheromoneLevel);

        Tile myTile = sim.getTile(x,y);
        myTile.setMaterial(Material.ANTHILL);
        for(int i = 0; i < initAnts;++i)
        {
            Ant newAnt = new Ant(antGenomes.get(0),this);
            ants.add(newAnt);
            myTile.getAnts().add(newAnt);
        }
    }

    /**
     * Removes all dead ants that belong to this anthill.
     * Moves all ants.
     * Decreases pheromone values on the map.
     * Spawns new ants if there is enough food in the anthill
     */
    public void step()
    {
        ants.removeIf(Ant::isDead);
        for (Ant ant : ants)
        {
            ant.step();
        }

        removePheromone();

        Random rnd = new Random();
        if (rnd.nextDouble() <= reproductionRate)
            spawnAnt();
    }

    /**
     * Spawns new ants if there is enough food in the anthill
     */
    private void spawnAnt()
    {
        if (food >= antGenomes.get(0).getHealth())
        {
            ants.add(new Ant(antGenomes.get(0),this));
            food -= antGenomes.get(0).getHealth();
        }
    }

    //__________________________________________________________________________________________________________________
    //                                              GETTERS/SETTERS
    //__________________________________________________________________________________________________________________

    /**
     * Gets pheromone value from the map
     * @param x coordinate
     * @param y coordinate
     * @return pheromone value
     */
    public int getPheromone(int x, int y)
    {
        int width = sim.getWidth();
        int height = sim.getHeight();

        x = Utils.wrapAroundCoordinate(x,width);
        y = Utils.wrapAroundCoordinate(y,height);

        return pheromoneMap[x][y];
    }

    /**
     * Adds pheromone to the map
     * @param x coordinate
     * @param y coordinate
     * @param value
     */
    public void addPheromone(int x, int y, int value)
    {
        int width = sim.getWidth();
        int height = sim.getHeight();

        x = Utils.wrapAroundCoordinate(x,width);
        y = Utils.wrapAroundCoordinate(y,height);
        if(pheromoneMap[x][y] < 2000)
            pheromoneMap[x][y] += value;
    }

    /**
     * Decreases pheromone value for the whole map each step.
     */
    private void removePheromone()
    {
        for(int i = 0; i < sim.getHeight(); ++i)
        {
            for (int j = 0; j < sim.getWidth(); ++j)
            {
                if(pheromoneMap[j][i] > basePheromoneLevel)
                    pheromoneMap[j][i] -= (pheromoneMap[j][i]-basePheromoneLevel/50);
            }
        }
    }

    public int getFood()
    {
        return food;
    }

    public void addFood(int food)
    {
        this.food += food;
        //builds the anthill
    }

    public void removeFood(int food)
    {
        this.food -= food;
        //destroys the anthill
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public Simulation getSim()
    {
        return sim;
    }

    public Color getAntColor()
    {
        return antColor;
    }
}
