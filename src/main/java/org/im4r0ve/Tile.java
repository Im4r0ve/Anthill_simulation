package org.im4r0ve;

import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Tile
{
    private Material initMaterial;
    private Material material;
    private int food;
    private ArrayList<Ant> ants;
    //for BFS
    private Tile prev;
    private boolean visited;

    public Tile(Color color, int food)
    {
        if(color == Material.FOOD.getColor())
        {
            this.initMaterial = Material.GRASS;
        }
        else
        {
            this.initMaterial = getMaterial(color);
        }
        this.material = initMaterial;
        this.food = food;
        ants = new ArrayList<>();
    }

    public Material getMaterial(Color color)
    {
        if(color.equals(Color.GREEN))
            return Material.GRASS;
        if(color.equals(Color.BROWN))
            return Material.ANTHILL;
        if(color.equals(Color.SANDYBROWN))
            return Material.FOOD;
        if(color.equals(Color.GRAY))
            return Material.ROCK;
        return null;
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
    public void showInitMaterial()
    {
        material = initMaterial;
    }

    public Tile getPrev()
    {
        return prev;
    }

    public void setPrev(Tile prev)
    {
        this.prev = prev;
    }

    public boolean isVisited()
    {
        return visited;
    }

    public void setVisited(boolean visited)
    {
        this.visited = visited;
    }
}
