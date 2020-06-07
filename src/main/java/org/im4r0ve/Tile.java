package org.im4r0ve;

import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Tile
{
    private Color color;
    private int food;
    private boolean barrier;
    private ArrayList<Ant> ants;

    public Tile(int food,boolean barrier, Color color)
    {
        this.color = color;
        this.food = food;
        this.barrier = barrier;
        ants = new ArrayList<>();
    }

    public int getFood()
    {
        return food;
    }

    public void setFood(int food)
    {
        this.food = food;
    }

    public void addFood(int food)
    {
        this.food += food;
    }

    public void removeFood(int food)
    {
        this.food -= food;
    }

    public boolean isBarrier()
    {
        return barrier;
    }

    public void setBarrier(boolean barrier)
    {
        this.barrier = barrier;
    }

    public ArrayList<Ant> getAnts()
    {
        return ants;
    }

    public void addAnt(Ant ant)
    {
        ants.add(ant);
    }
    public void removeAnt(Ant ant)
    {
        ants.remove(ant);
    }
}
