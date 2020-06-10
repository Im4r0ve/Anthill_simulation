package org.im4r0ve;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;


/**
 * JavaFX App
 */
public class App extends Application {
    enum States {
        Settings,
        Evolving,
        Simulating,
    }
    private States state;
    private Canvas displayedMap;
    private Tile[][] map;
    private ArrayList<Simulation> simulations;
    private ArrayList<AntGenome> antGenomes;
    private int height;
    private int width;
    private boolean showMap;
    private int maxFoodPerTile;
    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)
    {
        //starts menu
        simulations = new ArrayList<>();
        antGenomes = new ArrayList<>();
        showMap = true;
        maxFoodPerTile = 50;

        initialize(primaryStage);
        primaryStage.show();
        for (int i = 0; i < 1; ++i) //generations pool
        {
            antGenomes.add(new AntGenome(100,25,1,5, Color.RED));
            simulations.add(new Simulation(copyMap(map),1,antGenomes,maxFoodPerTile));
        }

        state = States.Settings;

        //thread = new Thread(this);
        //thread.run();
        //setup of playground
        //waits for start button
        //starts x simulation threads for g generations
        //waits for all threads
        //displays results
        //start game with the best one
    }

    public Tile[][] loadImage(Image image)
    {
        if(image.isError())
        {
            System.out.println("Error: empty image");
            return null;
        }

        Tile[][] newMap = new Tile[width][height];
        var reader = image.getPixelReader();
        for(int y = 0; y < height;++y)
        {
            for(int x = 0; x < width;++x)
            {
                newMap[x][y] = new Tile(reader.getColor(x,y), maxFoodPerTile, x, y);
            }
        }
        return newMap;
    }

    private Tile[][] copyMap(Tile[][] toCopy)
    {
        Tile[][] copy = new Tile[width][height];
        for(int y=0; y<height; ++y)
            for(int x=0; x<width; ++x)
            {
                copy[x][y] = toCopy[x][y];
            }
        return copy;
    }

    private void drawMap(Tile[][] map)
    {
        var writer = displayedMap.getGraphicsContext2D().getPixelWriter();
        for(int y = 0; y < height;++y)
        {
            for(int x = 0; x < width;++x)
            {
                if (map[x][y].getAnts().size() != 0)
                    writer.setColor(x,y,map[x][y].getAnts().get(0).getColor());
                else
                    writer.setColor(x,y,map[x][y].getMaterial().getColor());
            }
        }
    }
    private void initialize(Stage primaryStage)
    {
        primaryStage.setTitle("Anthill simulator");

        Button step = new Button("Step");
        step.setOnAction(e ->
        {
            //while(true)
            //{
                for (Simulation simulation : simulations)
                {
                    Tile[][] newMap = simulation.step();
                    if (showMap)
                    {
                        drawMap(newMap);
                    }
                }
            //}
        });
        VBox vbox = new VBox();
        vbox.getChildren().addAll(
                step,
                GUI_utils.createTextField("Strength: ", "5"));

        Image defaultImage = new Image("file:resources/map2.png");
        height = (int)defaultImage.getHeight();
        width = (int)defaultImage.getWidth();
        displayedMap = new Canvas(width,height);
        map = loadImage(defaultImage);
        drawMap(map);

        double minScale =  Math.min(600 / width,600 / height);

        displayedMap.setScaleX(minScale);
        displayedMap.setScaleY(minScale);

        Group canvas = new Group();
        canvas.getChildren().add(displayedMap);

        HBox hbox = new HBox();
        hbox.setSpacing(50);
        hbox.getChildren().addAll(vbox,canvas);
        primaryStage.setScene(new Scene (hbox, 600 + 400, 600));
    }
}