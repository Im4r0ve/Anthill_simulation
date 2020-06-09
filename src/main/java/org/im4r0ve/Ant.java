package org.im4r0ve;

import javafx.scene.paint.Color;

public class Ant {
    public Color getColor()
    {
        return color;
    }

    enum States{
        SEARCHING,
        CARRYING_FOOD,
        FIGHTING
    }
    private States state;
    private final Color color;
    private int x;
    private int y;
    private int health; //half of the weight
    //private int weight; //equals food at the end
    private int strength;
    private int speed;
    private double viewRange;

    private Anthill anthill;
    private boolean alive = true;

    public Ant(AntGenome genome, Anthill anthill)
    {
        state = States.SEARCHING;
        x = anthill.getX();
        y = anthill.getY();
        color = genome.getColor();
        health = genome.getHealth();
        strength = genome.getStrength();
        speed = genome.getSpeed();
        viewRange = genome.getViewRange();
        this.anthill = anthill;
    }
    public void searchFood()
    {
        /*for(int y = this.y - viewRange; y < this.y+viewRange + 1;++y)
        {
            for (int x = this.x; x < this.x+viewRange + 1; ++x)
            {
                if (Math.pow(this.y - y, 2) + Math.pow(this.x - x, 2) <= Math.pow(viewRange, 2))
                    System.out.print("x");
                else
                    System.out.print("_");
            }
            System.out.println();
        }*/
    }
    public void step()
    {
        //move up
        searchFood();
        Tile myTile = anthill.getSim().getTile(x,y);
        if(myTile.getAnts().size() == 1)
        {
            myTile.showInitMaterial();
            myTile.removeAnt(this);
        }
        Tile newTile = anthill.getSim().getTile(x+1,y + 1);
        if(!newTile.isBarrier())
        {
            newTile.addAnt(this);
            y++;
            x++;
        }
        System.out.println(x + " " + y);
        health--;
    }
}
