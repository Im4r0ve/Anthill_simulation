package org.im4r0ve;

import java.util.ArrayList;
import java.util.Random;

public class Simulation
{
    private int height;
    private int width;

    private Tile[][] map;
    private ArrayList<Anthill> anthills;
    private int maxFoodPerTile;

    public Simulation(Tile[][] map, int initAnts, ArrayList<AntGenome> genomes, int maxFoodPerTile) //add multiple anthills/genomes
    {
        this.map = map;
        height = map.length;
        width = map[0].length;
        this.maxFoodPerTile = maxFoodPerTile;
        anthills = new ArrayList<>();
        anthills.add(new Anthill(20,20,1,initAnts, genomes,this));
    }
    boolean inside_circle(int centerX, int centerY, int tileX, int tileY, double radius) {
        double dx = centerX - tileX;
        double dy = centerY - tileY;
        return dx*dx + dy*dy <= radius*radius;
    }


    public void spawnFood(int foodSpawnAmount, double probability)
    {
        Random random = new Random();
        if(random.nextDouble() <= probability)
        {
            int centerX;
            int centerY;
            double radius = Math.ceil(Math.sqrt(((float)foodSpawnAmount / maxFoodPerTile)/ Math.PI));
            //skips already filled positions
            do
            {
                centerX = random.nextInt(width);
                centerY = random.nextInt(height);
            }while(getTile(centerX,centerY).getMaterial() != Material.GRASS);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (inside_circle(centerX, centerY, x,y, radius) && getTile(x,y).getMaterial() == Material.GRASS)
                    {
                        foodSpawnAmount-= maxFoodPerTile;
                        getTile(x,y).addFood(maxFoodPerTile);
                        getTile(x,y).setMaterial(Material.FOOD);
                    }
                }
            }
        }
    }
    public Tile getTile(int row, int col)
    {
        if (col < 0)
            col += width;
        if (col >= width)
            col %= width;

        if (row < 0)
            row += height;
        if (row >= height)
            row %= height;

        return map[row][col];
    }

    public Tile[][] step()
    {
        spawnFood(200,1);
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
