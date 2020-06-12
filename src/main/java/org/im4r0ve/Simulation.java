package org.im4r0ve;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Random;

/**
 * Responsible for maintaining the map and anthills in it.
 */
public class Simulation
{
    private int height;
    private int width;

    private Tile[][] map;
    private Anthill anthill;

    private int maxFoodPerTile;
    private int foodSpawnAmount;
    private double foodSpawnProbability;

    public Simulation(Tile[][] map, int maxFoodPerTile, int foodSpawnAmount, double foodSpawnProbability,
                      int x, int y, ArrayList<AntGenome> antGenomes, int initAnts, double reproductionRate, int basePheromoneLevel, Color antColor)
    {
        this.map = map;
        height = map[0].length;
        width = map.length;
        this.maxFoodPerTile = maxFoodPerTile;
        this.foodSpawnAmount = foodSpawnAmount;
        this.foodSpawnProbability = foodSpawnProbability;
        this.anthill = new Anthill(x, y,this, antGenomes, initAnts, reproductionRate, basePheromoneLevel, antColor);
    }

    /**
     * Spawns food and calls step on all anthills on the map.
     * @return returns Map, so Application can draw it.
     */
    public Tile[][] step()
    {
        System.out.println("Step_____________________________________________________________________________________");
        spawnFood();
        anthill.step();

        return map;
    }

    /**
     * Spawns randomly food every step of the simulation. Spawns it only on GRASS and based on foodSpawnAmount and maxFoodPerTile
     * creates a circle of food on the map.
     */
    private void spawnFood()
    {
        Random random = new Random();
        if(random.nextDouble() <= foodSpawnProbability)
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
                    if (    Utils.inside_circle(centerX, centerY, x,y, radius) &&
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
    /**
     * Gets Tile at coordinates x,y from the map.
     * You can use indexes from -width to infinity and it returns x and y from within the bounds of the map.
     */
    public Tile getTile(int x, int y)
    {
        x = Utils.wrapAroundCoordinate(x,width);
        y = Utils.wrapAroundCoordinate(y,height);

        return map[x][y];
    }
    /**
     * Gets height of the map
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Gets width of the map
     */
    public int getWidth()
    {
        return width;
    }
}
