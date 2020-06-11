package org.im4r0ve;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
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
        EDITING,
        EVOLVING,
        SIMULATING
    }
    private States state;
    private HBox toolbar;
    private Accordion settings;
    private Canvas displayedMap;
    private Simulator simulator;

    private Tile[][] map;
    private ArrayList<Simulation> simulations;
    private ArrayList<AntGenome> antGenomes;
    private int height;
    private int width;
    private int maxFoodPerTile;

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)
    {
        maxFoodPerTile = 50;

        initMainView(primaryStage);
        primaryStage.show();

        initSimulation();

        state = States.EDITING;
    }

    private void initMainView(Stage primaryStage)
    {
        primaryStage.setTitle("Anthill simulator");

        toolbar = generateToolbar();

        settings = generateSettings();
        VBox vboxSettings = new VBox(settings);

        processImage();
        Group canvas = new Group(displayedMap);

        HBox hbox = new HBox(vboxSettings,canvas);
        hbox.setSpacing(5);

        VBox vbox = new VBox(toolbar, hbox);
        vbox.setSpacing(5);

        primaryStage.setScene(new Scene (vbox));
    }

    private void initSimulation()
    {
        simulations = new ArrayList<>();
        antGenomes = new ArrayList<>();
        for (int i = 0; i < 1; ++i) //generations pool
        {
            antGenomes.add(new AntGenome(150,50,3,8, 50, Color.RED));
            simulations.add(new Simulation(clone(map),50,antGenomes,maxFoodPerTile));
        }
        simulator = new Simulator(simulations.get(0),this);
    }

    public void drawMap(Tile[][] map)
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

    private HBox generateToolbar()
    {
        Button step = new Button("Step");
        step.setOnAction(this::handleStep);

        Button start = new Button("Start");
        start.setOnAction(this::handleStart);

        Button stop = new Button("Stop");
        stop.setOnAction(this::handleStop);

        Button reset = new Button("reset");
        reset.setOnAction(this::handleReset);

        Button apply = new Button("apply");
        apply.setOnAction(this::handleApply);

        HBox toolbar = new HBox();
        toolbar.getChildren().addAll(start, stop, step, reset, apply);
        toolbar.setSpacing(5);
        return toolbar;
    }

    private Accordion generateSettings()
    {
        Accordion accordion = new Accordion();
        TitledPane general = new TitledPane("General" , new Label("General"));
        TitledPane simulation = new TitledPane("Simulation"  , new Label("Simulation"));
        TitledPane ga = new TitledPane("GA", new Label("GA"));
        TitledPane genome = new TitledPane("Genome", Utils.createTextField("Strength","42"));

        accordion.getPanes().addAll(general, simulation,ga,genome);
        return accordion;
    }

    //__________________________________________________________________________________________________________________
    //                                              HANDLERS
    //__________________________________________________________________________________________________________________

    private void handleStep(ActionEvent actionEvent)
    {
        setApplicationState(States.SIMULATING);
        Thread taskThread = new Thread(() ->
        {
            Tile[][] newMap = new Tile[width][height];
            for (Simulation simulation : simulations)
            {
                newMap = simulation.step();
            }

            Tile[][] finalNewMap = newMap;
            Platform.runLater(() -> drawMap(finalNewMap));
        });
        taskThread.start();
    }

    private void handleStart(ActionEvent actionEvent)
    {
        setApplicationState(States.SIMULATING);
        simulator.start();
    }
    private void handleStop(ActionEvent actionEvent)
    {
        simulator.stop();
    }
    private void handleReset(ActionEvent actionEvent)
    {
        setApplicationState(States.EDITING);
        initSimulation();
        drawMap(map);
    }

    private void handleApply(ActionEvent actionEvent)
    {
        TitledPane anthill = new TitledPane("Anthill", Utils.createTextField("Strength","42"));

        settings.getPanes().addAll(anthill);
    }

    //__________________________________________________________________________________________________________________
    //                                              UTILS
    //__________________________________________________________________________________________________________________

    private void processImage()
    {
        Image defaultImage = new Image("file:resources/map2.png");
        height = (int)defaultImage.getHeight();
        width = (int)defaultImage.getWidth();

        displayedMap = new Canvas(width,height);
        double minScale =  Math.min(600 / width,600 / height);
        displayedMap.setScaleX(minScale);
        displayedMap.setScaleY(minScale);

        map = loadImage(defaultImage);
        drawMap(map);
    }

    private Tile[][] loadImage(Image image)
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

    private Tile[][] clone(Tile[][] toCopy)
    {
        Tile[][] copy = new Tile[width][height];
        for(int y=0; y<height; ++y)
            for(int x=0; x<width; ++x)
            {
                copy[x][y] = new Tile(toCopy[x][y]);
            }
        return copy;
    }

    private void setApplicationState(States state)
    {
        if (state == this.state)
        {
            return;
        }
        this.state = state;
    }
}