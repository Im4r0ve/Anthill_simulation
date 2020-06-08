package org.im4r0ve;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    private Canvas map;
    private ArrayList<Simulation> simulations;
    private ArrayList<AntGenome> antGenomes;
    private int height;
    private int width;
    private boolean showMap;
    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        //starts menu
        simulations = new ArrayList<>();
        antGenomes = new ArrayList<>();
        showMap = true;

        initialize(primaryStage);
        primaryStage.show();
        for (int i = 0; i < 1; ++i) //generations pool
        {
            antGenomes.add(new AntGenome(100,25,1,5, Color.RED));
            simulations.add(new Simulation(loadMap(),1,antGenomes));
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

    public static boolean isEmpty(ImageView imageView)
    {
        return imageView.getImage().isError();
    }
    public Tile[][] loadMap()
    {
        /*if(map.getImage().isError())
        {
            System.out.println("Error: empty image");
            return null;
        }*/

        Image image = map.getImage();
        height = (int)image.getHeight();
        width = (int)image.getWidth();
        Tile[][] newMap = new Tile[width][height];

        var reader = image.getPixelReader();
        for(int y = 0; y < height;++y)
        {
            for(int x = 0; x < width;++x)
            {
                Color color = reader.getColor(x,y);
                newMap[x][y] = new Tile(color, 0);
            }
        }
        return newMap;
    }
    private void draw(Tile[][] newMap)
    {
        Image image = map.getImage();

    }
    private void initialize(Stage primaryStage)
    {
        primaryStage.setTitle("Anthill simulator");

        Button step = new Button("Step");
        step.setOnAction(e ->
        {
            for (Simulation simulation : simulations)
            {
                Tile[][] newMap = simulation.step();
                if (showMap)
                {
                    draw(newMap);
                }
            }
        });
        VBox vbox = new VBox();
        vbox.getChildren().addAll(
                step,
                GUI_utils.createTextField("Strength: ", "5"));

        Image defaultImage = new Image("file:resources/map2.png");
        map = new ImageView(defaultImage);
        width = (int)defaultImage.getWidth();
        height = (int)defaultImage.getHeight();

        HBox hbox = new HBox();
        hbox.setSpacing(50);
        hbox.getChildren().addAll(vbox,map);
        primaryStage.setScene(new Scene (hbox, width + 400, 600));
    }
}