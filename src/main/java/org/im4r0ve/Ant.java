package org.im4r0ve;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * Implements all the decision making and movement of an ant. Each ant type has it's own specification.
 */
public class Ant {
    private Anthill anthill;
    private States state;
    enum States{
        SEARCHING,
        GOING_HOME,
        FIGHTING
    }

    private final Color color;
    private int x;
    private int y;
    private int maxHealth;
    private int health; //half of the weight
    private int weight; //equals food at the end
    private int strength;
    private int speed;
    private float viewRange;
    private int pheromoneTrailFood;
    private int pheromoneTrail;

    private int carryingFood;
    private boolean alive;

    public Ant(AntGenome genome, Anthill anthill)
    {
        alive = true;
        state = States.SEARCHING;
        x = anthill.getX();
        y = anthill.getY();
        color = anthill.getAntColor();
        health = genome.getHealth();
        maxHealth = health;
        strength = genome.getStrength();
        speed = genome.getSpeed();
        viewRange = genome.getViewRange();
        weight = genome.getWeight();
        pheromoneTrail = genome.getPheromoneTrail();
        pheromoneTrailFood = genome.getPheromoneTrailFood();
        this.anthill = anthill;
    }

    /**
     * Ant movement logic driver. Based on surroundings and current state determines what to do.
     */
    public void step()
    {
        if(checkForDeath())
        {
            return;
        }
        if(health < (Math.abs(x-anthill.getX()) + Math.abs(y - anthill.getY())))
        {
            state = States.GOING_HOME;
        }
        System.out.println();
        System.out.println(state);
        System.out.println("x:"+ x + " y:" + y);

        //format of the interests: x,y,distance
        ArrayList<Integer> interests = searchArea();
        if (interests.size() == 0)
        {
            //didnt find anything
            System.out.println("Didnt find anything");
            cleanUpBFS(x,y);
            move();
        }
        else
        {
            boolean nothingInteresting = true;
            for (int i = 0; i < interests.size(); i += 3)
            {
                //decide what to do
                Tile tile = anthill.getSim().getTile(interests.get(i), interests.get(i + 1));
                if (tile.getMaterial() == Material.FOOD && carryingFood != strength)
                {
                    //Found food and decided to get it
                    nothingInteresting = false;
                    if (interests.get(i + 2) == 1)
                    {
                        //Ant is besides food
                        System.out.println("Picking up food: Available" + tile.getFood());
                        System.out.println(carryingFood + " "+ strength);
                        pickUpFood(tile.removeFood(strength));
                        System.out.println("Food now " + carryingFood);
                        state = States.GOING_HOME; //maybe add what happens when Ant can carry more
                        cleanUpBFS(x, y);
                        break;
                    }
                    System.out.println("Getting closer: " + tile.getMaterial() + " "+ tile.getX() + " "+ tile.getY());
                    getCloser(tile, interests.get(i + 2));
                    break;
                }

                if (tile.getMaterial() == Material.ANTHILL && state == States.GOING_HOME)
                {
                    //Found anthill and decided to get to it
                    nothingInteresting = false;
                    if (interests.get(i + 2) == 1)
                    {
                        //Ant is besides Anthill
                        System.out.println("Storing food");
                        anthill.addFood(carryingFood);
                        carryingFood = 0;
                        eatFromAnthill();
                        state = States.SEARCHING;
                        cleanUpBFS(x, y);
                        break;
                    }
                    System.out.println("Getting closer: " + tile.getMaterial() + " "+ tile.getX() + " "+ tile.getY());
                    getCloser(tile, interests.get(i + 2));
                    break;
                }
                //add what happens if detecting ant fighting etc.
            }

            if(nothingInteresting)
            {
                System.out.println("Didnt find anything interesting");
                cleanUpBFS(x,y);
                move();
            }
        }
        health--;
    }
    /**
     * Checks if the ant health is less or equal 0. If ant is carrying food he eats it and searches for some more.
     */
    private boolean checkForDeath()
    {
        if(health <= 0)
        {
            if(carryingFood > 0)
            {
                health += carryingFood;
                state = States.SEARCHING;
                carryingFood = 0;
            }
            else
            {
                Tile myTile = anthill.getSim().getTile(x, y);
                myTile.setMaterial(Material.FOOD);
                myTile.addFood(weight);
                myTile.removeAnt(this);
                alive = false;
                return true;
            }
        }
        return false;
    }

    /**
     * Searches the surroundings of an ant by running BFS and storing shortest paths to all interests
     * @return ArrayList of interest points in format: x,y,distance
     */
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
            if ((myTile.getMaterial() == Material.FOOD || myTile.getMaterial() == Material.ANTHILL || myTile.getAnts().size() > 0 ) && distance != 0) //add Anthill
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

    /**
     * Moves based on compass values multiplied by pheromone values in each direction.
     * Compass is giving better probability of going straight from the anthill.
     */
    //move based on compass and pheromones around ant
    private void move()
    {
        //format:  East, North,  West, South
        int[] dHorizont = {1, 0, -1, 0};
        int[] dVertical = {0, -1, 0, 1};

        double[] pheromoneValues = new double[4];
        double[] compass = getCompass();
        double sum = 0;

        for (int j = 0; j < 4; ++j)
        {
            int neighborX = x + dHorizont[j];
            int neighborY = y + dVertical[j];
            //skips rocks
            Tile tile = anthill.getSim().getTile(neighborX, neighborY);
            if (!tile.isBarrier())
            {
                pheromoneValues[j] = anthill.getPheromone(neighborX,neighborY)*compass[j];
                sum += pheromoneValues[j];
            }
        }

        Random random = new Random();
        for (int i = 0; i < speed; ++i)
        {
            double tempSum = sum;
            double rnd = random.nextDouble()*sum;
            System.out.println("E " + pheromoneValues[0] + " N " + pheromoneValues[1] + " W " +pheromoneValues[2] + " S " +pheromoneValues[3]);

            for (int j = 0; j < 4; ++j)
            {
                tempSum -= pheromoneValues[j];
                if(tempSum <= rnd)
                {
                    moveToTile(x + dHorizont[j],y + dVertical[j]);
                    spreadPheromone(x,y);
                    break;
                }
            }
        }
    }

    /**
     * When the ant finds something interesting he gets closer to it by shortest path.
     * @param target tile to get closer to.
     * @param distance distance to the tile.
     */
    private void getCloser(Tile target, int distance)
    {
        //move closer to food or anthill by shortest path

        for (int j = 0; j < Math.max(distance - speed,1); ++j)
        {
            target = target.getPrev();
        }
        //for testing purposes only, did not happen
        if(target == null)
        {
            System.err.println("target je null______________________________________________________________________");
            return;
        }

        int oldX = x;
        int oldY = y;
        moveToTile(target.getX(), target.getY());

        //leave trail of pheromones
        while (target != null)
        {
            spreadPheromone(target.getX(), target.getY());
            target = target.getPrev();
        }
        cleanUpBFS(oldX, oldY);
    }

    /**
     * Cleans up all the shortest paths and markings on the map after running searchArea().
     * @param x coordinate of the start of searchArea().
     * @param y coordinate of the start of searchArea().
     */
    private void cleanUpBFS(int x,int y)
    {
        int offset = (int)Math.ceil(viewRange)+3;
        for (int ys = y-offset; ys < y+offset; ys++) {
            for (int xs = x-offset; xs < x+offset; xs++)
            {
                Tile tile = anthill.getSim().getTile(xs, ys);
                tile.setPrev(null);
                tile.setVisited(false);
            }
        }
    }

    /**
     * Pointing away from anthill when an ants is in SEARCHING state.
     * Points to anthill if the ant is in GOING_HOME state.
     * @return double[] of probabilities for each direction in format: East, North,  West, South
     */
    private double[] getCompass()
    {
        //format:  East, North,  West, South
        double[] compass = {0.60, 0.20, 0.00, 0.20};
        int dx,dy;
        if(state == States.GOING_HOME)
        {
            dx = anthill.getX() - this.x;
            dy = anthill.getY() - this.y;

        }
        else{
            dx = this.x - anthill.getX();
            dy = this.y - anthill.getY();
        }
        if(dx == 0 && dy == 0)
        {
            Random rnd = new Random();
            Utils.rotateArrayRight(compass,rnd.nextInt(4));
        }
        double rotation = Math.atan2(-dy,dx);

        if(rotation == 0.0)
            return compass;
        if(rotation < 0.0)
            rotation = 2*Math.PI + rotation;

        int fullRotations = (int)((rotation)/(Math.PI/2)); //rotacie doprava
        double passToRight = (rotation - fullRotations*(Math.PI/2))/(Math.PI/2);

        if(passToRight != 0.0)
        {
            double[] newCompass = compass.clone();

            for (int i = 0; i < 4; ++i)
            {
                if (i == 3)
                    newCompass[0] += passToRight * compass[i];
                else
                    newCompass[i + 1] += passToRight * compass[i];
                newCompass[i] -= passToRight * compass[i];
            }
            compass = newCompass;
        }

        Utils.rotateArrayRight(compass,fullRotations);

        System.out.println("E " + compass[0] + " N " + compass[1] + " W " +compass[2] + " S " +compass[3]);
        return compass;
    }

    /**
     * Teleports an ant to the x , y position.
     * @param x coordinate to teleport to
     * @param y coordinate to teleport to
     */
    private void moveToTile(int x, int y)
    {
        anthill.getSim().getTile(this.x, this.y).removeAnt(this);

        Tile newTile = anthill.getSim().getTile(x, y);
        if (!newTile.isBarrier())
        {
            newTile.addAnt(this);
            this.y = y;
            this.x = x;
        }
        System.out.println("x:"+ x + " y:" + y);
    }

    /**
     * Adds pheromone to the pheromoneMap of corresponding anthill with coordinates x,y.
     * Adds more if an ant is carying food.
     * @param x coordinate
     * @param y coordinate
     */
    private void spreadPheromone(int x, int y)
    {
        if(carryingFood > 0)
            anthill.addPheromone(x,y, pheromoneTrailFood);
        else
            anthill.addPheromone(x,y,pheromoneTrail);
    }

    /**
     * @param food Food amount to pick up.
     */
    private void pickUpFood(int food)
    {
        this.carryingFood += food;
    }

    /**
     * Eats food from anthill.
     * Called only if it is besides anthill.
     */
    private void eatFromAnthill()
    {
        health += anthill.eatFood(maxHealth-health);
    }

    /**
     * @return returns color of an ant
     */
    public Color getColor()
    {
        return color;
    }

    /**
     * Checks if the ant health is less or equal to 0
     * @return true if the ant is dead
     */
    public boolean isDead()
    {
        return !alive;
    }
}
