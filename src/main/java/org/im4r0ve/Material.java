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
       if(this == ROCK)
       {
           return true;
       }
       return false;
    }
    public Color getColor()
    {
        switch(this)
        {
            case GRASS:
                return Color.GREEN; //#008000, 0, 128, 0
            case ANTHILL:
                return Color.SADDLEBROWN; //#8B4513, 139, 69, 19
            case FOOD:
                return Color.SANDYBROWN; //#F4A460, 244, 164, 96
            case ROCK:
                return Color.GRAY; //#808080, 128, 128, 128
            default:
                return Color.PURPLE; //#800080, 128, 0, 128
        }
    }
}
