package org.im4r0ve;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Anthill maintains all the ants that belong to one anthill. It is also maintaining the pheromone map.
 */
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
    public int step()
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
        redrawAnthill();
        return ants.size();
    }

    /**
     * Spawns new ants if there is enough food in the anthill
     */
    private void spawnAnt()
    {
        if (food >= antGenomes.get(0).getWeight())
        {
            ants.add(new Ant(antGenomes.get(0),this));
            food -= antGenomes.get(0).getWeight();
        }
    }

    /**
     * Redraws anthill based on amount of food in it.
     */
    private void redrawAnthill()
    {
        double radius = Math.sqrt(((float)this.food / sim.getFoodPerTile())/ Math.PI);
        int offset = 100;
        int tempFood = this.food;
        for (int y = this.y-offset; y < this.y+offset; y++) {
            for (int x = this.x-offset; x < this.x+offset; x++) {
                if (    Utils.inside_circle(this.x, this.y, x,y, radius) &&
                        (sim.getTile(x,y).getMaterial() == Material.GRASS ||
                                sim.getTile(x,y).getMaterial() == Material.ANTHILL) &&
                        tempFood > 0)
                {
                    tempFood -= sim.getFoodPerTile();
                    sim.getTile(x,y).setMaterial(Material.ANTHILL);
                }
                else
                {
                    if(sim.getTile(x,y).getMaterial() == Material.ANTHILL && (x != this.x || y != this.y))
                    {
                        sim.getTile(x,y).showInitMaterial();
                    }
                }
            }
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
        if(x < 0 || y < 0 || x >= width || y >= height)
        {
            return 0;
        }
        //x = Utils.wrapAroundCoordinate(x,width);
        //y = Utils.wrapAroundCoordinate(y,height);

        return pheromoneMap[x][y];
    }

    /**
     * Adds pheromone to the map
     * @param x coordinate
     * @param y coordinate
     * @param value how much pheromone to add
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
                    pheromoneMap[j][i] -= ((pheromoneMap[j][i]-basePheromoneLevel)/50);
            }
        }
    }

    /**
     * Adds food to the anthill
     * @param food food to be added to anthill.
     */
    public void addFood(int food)
    {
        this.food += food;
    }

    /**
     * A function that serves an ant that wants to eat.
     * @param food food that ant wants to eat from the anthill.
     * @return returns how much food the ant actually gets.
     */
    public int eatFood(int food)
    {
        if(food < this.food)
        {
            this.food -= food;
            return food;
        }
        int result = this.food;
        this.food = 0;
        return result;
    }

    /**
     * Gets X coordinate of the anthill.
     * @return X coordinate of the anthill.
     */
    public int getX()
    {
        return x;
    }
    /**
     * Gets Y coordinate of the anthill.
     * @return Y coordinate of the anthill.
     */
    public int getY()
    {
        return y;
    }
    /**
     * Gets a reference to parent simulation.
     * @return parent simulation.
     */
    public Simulation getSim()
    {
        return sim;
    }
    /**
     * Gets a color of ants belonging to the anthill.
     * @return color of ants belonging to the anthill.
     */
    public Color getAntColor()
    {
        return antColor;
    }
}
