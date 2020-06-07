package org.im4r0ve;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Simulation
{
    private int height;
    private int width;

    private boolean showMap;

    private ArrayList<ArrayList<Tile>> map;
    private ArrayList<Anthill> anthills;

    public Simulation(ArrayList<ArrayList<Tile>> map, boolean showMap, int initAnts, ArrayList<AntGenome> genomes) //add multiple anthills/genomes
    {
        this.showMap = showMap;
        this.map = map;
        anthills = new ArrayList<>();
        anthills.add(new Anthill(50,100,1,initAnts, genomes,this));
    }


    public void changeColor()
    {

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
        return map.get(row).get(col);
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
