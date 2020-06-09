package org.im4r0ve;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Ant {
    public Color getColor()
    {
        return color;
    }

    enum States{
        SEARCHING,
        GOING_HOME,
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
    private float viewRange;

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

    private ArrayList<Integer> searchArea()
    {
        //format of the result: x,y,distance
        ArrayList<Integer> result = new ArrayList<>();
        Queue<Integer> xQueue = new LinkedList<>();
        Queue<Integer> yQueue = new LinkedList<>();
        int nodesInNextLayer = 0;
        int nodesLeftInLayer = 1;
        int distance = 0;

        //format: North, South, East, West
        int[] dHorizont = {0,0,1,-1};
        int[] dVertical = {-1,1,0,0};

        xQueue.add(x);
        yQueue.add(y);

        Tile myTile = anthill.getSim().getTile(x,y);
        myTile.setVisited(true);
        while(xQueue.size()>0 && distance <= viewRange)
        {
            int newX = xQueue.remove();
            int newY = yQueue.remove();
            myTile = anthill.getSim().getTile(newX,newY);
            //if there is something interesting add it to result
            if ((myTile.getMaterial() == Material.FOOD || myTile.getAnts().size() > 0) && distance != 0) //add Anthill
            {
                result.add(newX);
                result.add(newY);
                result.add(distance);
            }

            //add neighbors to queue
            for (int i = 0; i < 4; ++i)
            {
                int neighborX = newX + dHorizont[i];
                int neighborY = newY + dVertical[i];
                //skips rocks
                Tile tile = anthill.getSim().getTile(neighborX, neighborY);
                if (!tile.isBarrier() && !tile.isVisited())
                {
                    xQueue.add(neighborX);
                    yQueue.add(neighborY);

                    tile.setVisited(true);
                    tile.setPrev(myTile);
                    nodesInNextLayer++;
                }
            }
            nodesLeftInLayer--;
            if(nodesLeftInLayer == 0)
            {
                nodesLeftInLayer = nodesInNextLayer;
                nodesInNextLayer = 0;
                distance++;
            }

        }
        return result;
    }
    public void step()
    {
        ArrayList<Integer> interests = searchArea();
        //clean up
        int offset = (int)Math.ceil(viewRange);
        for (int y = this.y-offset-1; y < this.y+offset+1; y++) {
            for (int x = this.x-offset-1; x < this.x+offset+1; x++) {
                {
                    Tile tile = anthill.getSim().getTile(x,y);
                    tile.setPrev(null);
                    tile.setVisited(false);
                }
            }
        }
        //choose what to do
            //if interests distance == 1 do magic
            //or move
        //clean up mess
        //move based on speed

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
