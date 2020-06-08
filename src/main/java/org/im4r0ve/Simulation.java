package org.im4r0ve;

import java.util.ArrayList;
import java.util.Random;

public class Simulation
{
    private int height;
    private int width;

    private Tile[][] map;
    private ArrayList<Anthill> anthills;

    public Simulation(Tile[][] map, int initAnts, ArrayList<AntGenome> genomes) //add multiple anthills/genomes
    {
        this.map = map;
        height = map.length;
        width = map[0].length;
        anthills = new ArrayList<>();
        anthills.add(new Anthill(20,20,1,initAnts, genomes,this));
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

    public Tile[][] step()
    {
        for(Anthill anthill : anthills)
        {
            anthill.step();
        }
        return map;
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
