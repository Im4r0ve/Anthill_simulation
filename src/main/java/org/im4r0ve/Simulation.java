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
        height = map[0].length;
        width = map.length;
        this.maxFoodPerTile = maxFoodPerTile;
        anthills = new ArrayList<>();
        anthills.add(new Anthill(20,20,0.8,initAnts, genomes,this));
    }

    public Tile[][] step()
    {
        spawnFood(200,0.40);
        for(Anthill anthill : anthills)
        {
            anthill.step();
        }
        return map;
    }

    private void spawnFood(int foodSpawnAmount, double probability)
    {
        Random random = new Random();
        if(random.nextDouble() <= probability)
        {
            int centerX;
            int centerY;
            double radius = Math.sqrt(((float)foodSpawnAmount / maxFoodPerTile)/ Math.PI);
            int offset = (int)Math.ceil(radius);
            //skips already filled positions
            do
            {
                centerX = random.nextInt(width);
                centerY = random.nextInt(height);
            }while(getTile(centerX,centerY).getMaterial() != Material.GRASS);

            for (int y = centerY-offset; y < centerY+offset; y++) {
                for (int x = centerX-offset; x < centerX+offset; x++) {
                    if (    GUI_utils.inside_circle(centerX, centerY, x,y, radius) &&
                            getTile(x,y).getMaterial() == Material.GRASS &&
                            foodSpawnAmount > 0)
                    {
                        foodSpawnAmount -= maxFoodPerTile;
                        getTile(x,y).addFood(maxFoodPerTile);
                        getTile(x,y).setMaterial(Material.FOOD);
                    }
                }
            }
        }
    }

    public Tile getTile(int x, int y)
    {
        x = GUI_utils.wrapAroundCoordinate(x,width);
        y = GUI_utils.wrapAroundCoordinate(y,height);

        return map[x][y];
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
