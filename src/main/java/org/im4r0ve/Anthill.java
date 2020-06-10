package org.im4r0ve;

import java.util.ArrayList;
import java.util.Arrays;

public class Anthill
{
    private ArrayList<Ant> ants;

    private int x;
    private int y;
    private int food;
    private int reproductionRate;
    private Simulation sim;
    private int[][] pheromoneMap;

    public Anthill(int x,int y, int reproductionRate,int initAnts, ArrayList<AntGenome> antGenomes, Simulation sim)
    {
        ants = new ArrayList<>();
        pheromoneMap = new int[sim.getWidth()][sim.getHeight()];
        for (int[] row: pheromoneMap)
            Arrays.fill(row, 1);
        this.x = x;
        this.y = y;
        this.sim = sim;
        Tile myTile = sim.getTile(x,y);
        myTile.setMaterial(Material.ANTHILL);

        this.reproductionRate = reproductionRate;
        for(int i = 0; i< initAnts;++i)
        {
            Ant newAnt = new Ant(antGenomes.get(0),this);
            ants.add(newAnt);//add dynamic genome ratios
            myTile.getAnts().add(newAnt);
        }
        food = 0;
    }

    public void spawnAnt(AntGenome antGenome)
    {
        if (food >= antGenome.getHealth())
            ants.add(new Ant(antGenome,this));
    }
    public void removeAnt(Ant ant)
    {
        ants.remove(ant);
    }
    public void step()
    {
        for(Ant ant : ants)
        {
            ant.step();
        }
        //decrease pheromones
        //spawn new ant
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
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getReproductionRate()
    {
        return reproductionRate;
    }

    public Simulation getSim()
    {
        return sim;
    }

    public int getPheromone(int x, int y)
    {
        int width = sim.getWidth();
        int height = sim.getHeight();

        if (x < 0)
            x += width;
        if (x >= width)
            x %= width;

        if (y < 0)
            y += height;
        if (y >= height)
            y %= height;

        return pheromoneMap[x][y];
    }
    public void addPheromone(int x, int y, int value)
    {
        pheromoneMap[x][y] += value;
    }
    public void removePheromone(int x, int y, int value)
    {
        if(value > pheromoneMap[x][y])
            pheromoneMap[x][y] = 0;
        else
            pheromoneMap[x][y] -= value;
    }

}
