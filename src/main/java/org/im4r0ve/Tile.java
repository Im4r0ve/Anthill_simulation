package org.im4r0ve;

import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Tile
{
    private Material initMaterial;
    private Material material;
    private int food;
    private ArrayList<Ant> ants;

    public Tile(Material initMaterial, int food)
    {
        if(initMaterial == Material.FOOD)
        {
            this.initMaterial = Material.GRASS;
        }
        else
        {
            this.initMaterial = initMaterial;
        }
        this.material = initMaterial;
        this.food = food;
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
        return material.isBarrier();
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
    public void setMaterial(Material material)
    {
        this.material = material;
    }
    public Material getMaterial()
    {
        return material;
    }
}
