package org.im4r0ve;

import java.util.ArrayList;

public class Anthill
{
    private ArrayList<Ant> ants;

    private int x;
    private int y;
    private int food;
    private int reproductionRate;
    private Simulation sim;

    public Anthill(int x,int y, int reproductionRate,int initAnts, ArrayList<AntGenome> antGenomes, Simulation sim)
    {
        ants = new ArrayList<>();
        this.x = x;
        this.y = y;
        this.sim = sim;
        sim.getTile(x,y).setMaterial(Material.ANTHILL);

        this.reproductionRate = reproductionRate;
        for(int i = 0; i< initAnts;++i)
        {
            ants.add(new Ant(antGenomes.get(0),this));//add dynamic genome ratios
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
}
