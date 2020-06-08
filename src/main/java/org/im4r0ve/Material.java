package org.im4r0ve;

import javafx.scene.paint.Color;

public enum Material
{
    GRASS,
    ANTHILL,
    FOOD,
    ROCK,
    ;

    public boolean isBarrier()
    {
        switch(this)
        {
            case ROCK:
                return true;
            default:
                return false;
        }
    }
    public Color getColor()
    {
        switch(this)
        {
            case GRASS:
                return Color.GREEN;
            case ANTHILL:
                return Color.BROWN;
            case FOOD:
                return Color.SANDYBROWN;
            case ROCK:
                return Color.GRAY;
            default:
                return Color.RED;
        }
    }
    public void setColor(Color color)
    {
        switch(color)
        {
            case Color.GREEN:
                return ;
            case Color.BROWN:
                Material = ANTHILL;
            case FOOD:
                return Color.SANDYBROWN;
            case ROCK:
                return Color.GRAY;
            default:
                return Color.RED;
        }
    }
}
