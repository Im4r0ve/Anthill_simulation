package org.im4r0ve;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

public class GUI_utils
{
    public static HBox createTextField(String label, String defaultText)
    {
        Label myLabel = new Label(label);
        myLabel.setFont(new Font("Arial", 21));
        TextField textField = new TextField(defaultText);
        return (new HBox(myLabel, textField));
    }

    public static int wrapAroundCoordinate(int coordinate, int size)
    {
        if (coordinate < 0)
            coordinate += size;
        if (coordinate >= size)
            coordinate %= size;

        return coordinate;
    }

    public static boolean inside_circle(int centerX, int centerY, int tileX, int tileY, double radius) {
        double dx = centerX - tileX;
        double dy = centerY - tileY;
        return dx*dx + dy*dy <= radius*radius;
    }
}
