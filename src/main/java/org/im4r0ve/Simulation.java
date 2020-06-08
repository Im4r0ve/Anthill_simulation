package org.im4r0ve;

import java.util.ArrayList;
import java.util.Random;

public class Simulation
{
    private int height;
    private int width;

    private boolean showMap;

    private Tile[][] map;
    private ArrayList<Anthill> anthills;

    public Simulation(Tile[][] map, boolean showMap, int initAnts, ArrayList<AntGenome> genomes) //add multiple anthills/genomes
    {
        this.showMap = showMap;
        this.map = map;
        anthills = new ArrayList<>();
        anthills.add(new Anthill(50,100,1,initAnts, genomes,this));
    }


    public void drawMap()
    {
        if(showMap)
        {

        }
    }
    public void spawnFood()
    {
        Random random = new Random();
        if(random.nextDouble() < 0.30)
        {
            while(map[random.nextInt(width)][random.nextInt(height)].isBarrier()){}


        }
    }
    public Tile getTile(int row, int col)
    {
        if (col < 0)
            col += width;
        if (col > width)
            col %= width;
        if (row < 0)
            row += height;
        if (row > height)
            row %= height;
        return map[row][col];
    }

    public void step()
    {
        for(Anthill anthill : anthills)
        {
            anthill.step();
        }
    }

    public int getHeight()
    {
        return height;
    }

    public int getWidth()
    {
        return width;
    }
}
