package org.im4r0ve;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

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
    private int weight; //equals food at the end
    private int strength;
    private int speed;
    private float viewRange;
    private int food;

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
        weight = genome.getWeight();
        this.anthill = anthill;
    }

    public void step()
    {
        if(checkForDeath())
        {
            return;
        }

        System.out.println(state);
        //format of the interests: x,y,distance
        ArrayList<Integer> interests = searchArea();
        if (interests.size() == 0)
        {
            //didnt find anything
            cleanUpBFS(x,y);
            move();
        }
        else
        {
            boolean noInterest = true;
            for (int i = 0; i < interests.size(); i += 3)
            {
                //decide what to do
                Tile tile = anthill.getSim().getTile(interests.get(i), interests.get(i + 1));
                if(tile.getAnts().size() == 0)
                {
                    //no interest in ants
                    if (tile.getMaterial() == Material.FOOD && food != strength)
                    {
                        noInterest = false;
                        //Ant is besides food
                        if (interests.get(i + 2) == 1)
                        {
                            pickUpFood(tile.removeFood(strength));
                            state = States.GOING_HOME; //maybe add what happens when Ant can carry more
                            cleanUpBFS(x, y);
                            break;
                        }
                        getCloser(tile,interests.get(i + 2));
                        break;
                    }

                    if(tile.getMaterial() == Material.ANTHILL && state == States.GOING_HOME)
                    {
                        noInterest = false;
                        //Ant is besides Anthill
                        if (interests.get(i + 2) == 1)
                        {
                            anthill.addFood(food);
                            food = 0;
                            //eat
                            state = States.SEARCHING;
                            cleanUpBFS(x, y);
                            break;
                        }
                        getCloser(tile,interests.get(i + 2));
                        break;
                    }
                }
                //add what happens if detecting ant fighting etc.
            }
            if(noInterest)
            {
                cleanUpBFS(x,y);
                move();
            }
        }
        health--;
    }

    private boolean checkForDeath()
    {
        if(health <= 0)
        {
            if(food > 0)
            {
                health += food;
                state = States.SEARCHING;
                food = 0;
            }
            else
            {
                Tile myTile = anthill.getSim().getTile(x, y);
                myTile.setMaterial(Material.FOOD);
                myTile.addFood(weight);
                myTile.removeAnt(this);
                anthill.removeAnt(this);
                return true;
            }
        }
        return false;
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

    private void cleanUpBFS(int x,int y)
    {
        int offset = (int)Math.ceil(viewRange)+1;
        for (int ys = y-offset; ys < y+offset; ys++) {
            for (int xs = x-offset; xs < x+offset; xs++) {
                {
                    Tile tile = anthill.getSim().getTile(xs,ys);
                    tile.setPrev(null);
                    tile.setVisited(false);
                }
            }
        }
    }
    //inside of viewrange
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
        System.out.println(x + " " + y);
    }

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
        double rotation = Math.atan2(-dy,dx);
        System.out.println(rotation);
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

        rotateRight(compass,fullRotations);
        System.out.println("E " + compass[0] + " N " + compass[1] + " W " +compass[2] + " S " +compass[3]);
        return compass;
    }

    private void rotateRight(double[] compass,int n)
    {
        for(int i = 0; i < n; ++i)
        {
            double last = compass[compass.length-1];
            for (int j = compass.length-1; j > 0; --j)
            {
                compass[j] = compass[j - 1];
            }
            compass[0] = last;
        }
    }

    //move based on compass and pheromones around ant
    private void move()
    {
        for (int i = 0; i < speed; ++i)
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
            double rnd = random.nextDouble()*sum;
            System.out.println("E " + pheromoneValues[0] + " N " + pheromoneValues[1] + " W " +pheromoneValues[2] + " S " +pheromoneValues[3]);

            for (int j = 0; j < 4; ++j)
            {
                sum -= pheromoneValues[j];
                if(sum <= rnd)
                {
                    moveToTile(x + dHorizont[j],y + dVertical[j]);
                    spreadPheromone(x,y);
                    break;
                }
            }
        }
    }
    private void spreadPheromone(int x, int y)
    {
        if(food > 0)
            anthill.addPheromone(x,y,200);
        else
            anthill.addPheromone(x,y,100);
    }
    public void getCloser(Tile target, int distance)
    {
        //move closer to food or anthill by shortest path
        for (int j = 0; j < distance - speed; ++j)
        {
            target = target.getPrev();
        }
        int oldX = x;
        int oldY = y;
        moveToTile(target.getX(), target.getY());

        //leave trail of pheromones
        while (target.getPrev() != null)
        {
            spreadPheromone(target.getX(), target.getY());
            target = target.getPrev();
        }
        cleanUpBFS(oldX, oldY);
    }

    public void pickUpFood(int food)
    {
        this.food += food;
    }
}
