package org.im4r0ve;

import javafx.scene.paint.Color;

public class Ant {
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
    public void step()
    {
        //move up
        Tile myTile = anthill.getSim().getTile(x,y);
        if(myTile.getAnts().size() == 1)
        {
            myTile.showInitMaterial();
        }
        Tile newTile = anthill.getSim().getTile(x,y + 1);
        if(!newTile.isBarrier())
        {
            newTile.addAnt(this);
            y++;
        }
        System.out.println(x + " " + y);

    }
}
