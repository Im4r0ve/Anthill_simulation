package org.im4r0ve;

import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * Tile holds all the variables that we need to store in the map. Each pixel has corresponding Tile.
 */
public class Tile
{
    private int x;
    private int y;
    private Material initMaterial;
    private Material material;
    private int food;
    private ArrayList<Ant> ants;
    //for BFS
    private Tile prev;
    private boolean visited;

    public Tile(Color color, int food, int x, int y)
    {
        this.x = x;
        this.y = y;
        if(Material.FOOD.getColor().equals(color))
        {
            this.initMaterial = Material.GRASS;
            this.material = getMaterial(color);
        }
        else
        {
            this.initMaterial = getMaterial(color);
            this.material = initMaterial;
        }

        this.food = food;
        this.ants = new ArrayList<>();
        this.prev = null;
        this.visited = false;
    }

    public Tile(Tile other)
    {
        this.x = other.x;
        this.y = other.y;
        this.initMaterial = other.initMaterial;
        this.material = other.material;
        this.food = other.food;
        this.ants = new ArrayList<>();
        this.prev = null;
        this.visited = false;
    }

    //__________________________________________________________________________________________________________________
    //                                              GETTERS/SETTERS
    //__________________________________________________________________________________________________________________

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

    public void addFood(int food)
    {
        this.food += food;
    }

    public int removeFood(int food)
    {
        int result;
        if(food > this.food)
        {
            result = this.food;
            this.food = 0;
            showInitMaterial();
            return result;
        }
        this.food -= food;
        return food;
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

    public int getY()
    {
        return y;
    }

    public int getX()
    {
        return x;
    }
}
