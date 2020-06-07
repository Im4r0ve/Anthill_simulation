package org.im4r0ve;

import javafx.geometry.Point2D;
import javafx.scene.Node;

public class Ant {
    private int x;
    private int y;
    private int health; //half of the weight
    //private int weight; //equals food at the end
    private int strength;
    private int speed;
    private int viewRange;

    private Anthill anthill;
    private boolean alive = true;

    public Ant(AntGenome genome, Anthill anthill)
    {
        x = anthill.getX();
        y = anthill.getY();
        health = genome.getHealth();
        strength = genome.getStrength();
        speed = genome.getSpeed();
        viewRange = genome.getViewRange();
    }
    public void step()
    {
        //move up
        Tile tile = anthill.getSim().getTile(x,y + 1);
        if(!tile.isBarrier())
        {
            tile.addAnt(this);

        };
    }

}
