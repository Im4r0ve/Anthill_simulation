package org.im4r0ve;

public class Utils
{
    /**
     * recalculates coordinates so all of the requested coordinates are within the bounds of the map.
     */
    public static int wrapAroundCoordinate(int coordinate, int size)
    {
        if (coordinate < 0)
        {
            coordinate += size;
        }
        if (coordinate >= size)
        {
            coordinate %= size;
        }

        return coordinate;
    }

    /**
     * Calculates if coordinates are within the circle with some radius.
     */
    public static boolean inside_circle(int centerX, int centerY, int tileX, int tileY, double radius) {
        double dx = centerX - tileX;
        double dy = centerY - tileY;
        return dx*dx + dy*dy <= radius*radius;
    }

    /**
     * Used for rotating the compass by multiples of 90 degree.
     */
    public static void rotateArrayRight(double[] compass, int n)
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
}
