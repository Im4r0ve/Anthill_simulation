package org.im4r0ve;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Anthill
{
    private ArrayList<Ant> ants;

    private int x;
    private int y;
    private int food;
    private double reproductionRate;
    private Simulation sim;
    private int[][] pheromoneMap;
    private ArrayList<AntGenome> antGenomes;

    public Anthill(int x,int y, double reproductionRate,int initAnts, ArrayList<AntGenome> antGenomes, Simulation sim)
    {
        ants = new ArrayList<>();
        pheromoneMap = new int[sim.getWidth()][sim.getHeight()];
        for (int[] row: pheromoneMap)
            Arrays.fill(row, 1000);
        this.x = x;
        this.y = y;
        this.sim = sim;
        this.antGenomes = antGenomes;

        Tile myTile = sim.getTile(x,y);
        myTile.setMaterial(Material.ANTHILL);

        this.reproductionRate = reproductionRate;
        for(int i = 0; i < initAnts;++i)
        {
            Ant newAnt = new Ant(antGenomes.get(0),this);
            ants.add(newAnt);//add dynamic genome ratios
            myTile.getAnts().add(newAnt);
        }
        food = 0;
    }

    public void spawnAnt()
    {
        if (food >= antGenomes.get(0).getHealth())
        {
            ants.add(new Ant(antGenomes.get(0),this));
            food -= antGenomes.get(0).getHealth();
        }
    }
    public void removeAnt(Ant ant)
    {
        ants.remove(ant);
    }
    public void step()
    {
        for(int i = 0; i < ants.size(); i++)
        {
            ants.get(i).step();
        }

        removePheromone(1000);

        Random rnd = new Random();
        if (rnd.nextDouble() <= reproductionRate)
            spawnAnt();
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

    public double getReproductionRate()
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

        x = GUI_utils.wrapAroundCoordinate(x,width);
        y = GUI_utils.wrapAroundCoordinate(y,height);

        return pheromoneMap[x][y];
    }

    public void addPheromone(int x, int y, int value)
    {
        int width = sim.getWidth();
        int height = sim.getHeight();

        x = GUI_utils.wrapAroundCoordinate(x,width);
        y = GUI_utils.wrapAroundCoordinate(y,height);

        pheromoneMap[x][y] += value;
    }

    public void removePheromone(int minValue)
    {
        for(int i = 0; i < sim.getHeight(); ++i)
        {
            for (int j = 0; j < sim.getWidth(); ++j)
            {
                //System.out.print(pheromoneMap[j][i]);
                if(pheromoneMap[j][i] > minValue)
                    pheromoneMap[j][i]--;
            }
            //System.out.println();
        }
    }
}
