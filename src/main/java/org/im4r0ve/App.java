package org.im4r0ve;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private Accordion settings;
    private Canvas displayedMap;
    private Simulator simulator;
    private Button apply;
    private Button stop;
    private Button reset;

    private Map<String,TextField> textFields;

    private Tile[][] map;
    private ArrayList<Simulation> simulations;
    private ArrayList<AntGenome> antGenomes;

    private int height;
    private int width;

    //Simulation variables
    private int maxFoodPerTile;
    private double foodSpawnProbability;
    private int foodSpawnAmount;
    private int millisPerFrame;

    //Anthill variables
    private int x;
    private int y;
    private int basePheromoneLevel;
    private int initAnts;
    private double reproductionRate;
    private Color antColor;

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)
    {
        initMainView(primaryStage);
        primaryStage.show();
        initialize();
    }

    private void initialize()
    {
        maxFoodPerTile = Integer.parseInt(textFields.get("Max food on tile:").getText());
        foodSpawnAmount = Integer.parseInt(textFields.get("Food spawn amount:").getText());
        foodSpawnProbability = Double.parseDouble(textFields.get("Food spawn prob:").getText());
        x = Integer.parseInt(textFields.get("x:").getText());
        y = Integer.parseInt(textFields.get("y:").getText());
        initAnts = Integer.parseInt(textFields.get("Init number of ants:").getText());
        reproductionRate = Double.parseDouble(textFields.get("Reproduction rate:").getText());
        basePheromoneLevel = Integer.parseInt(textFields.get("Base pheromone level:").getText());
        antColor = Color.web(textFields.get("Ant color:").getText());
        millisPerFrame = Integer.parseInt(textFields.get("millis/frame:").getText());
        
        initSimulation();
        setApplicationState(States.EDITING);
    }

    private void initMainView(Stage primaryStage)
    {
        primaryStage.setTitle("Anthill simulator");
        textFields = new HashMap<>();
        antGenomes = new ArrayList<>();

        HBox toolbar = generateToolbar();

        generateSettings();
        VBox vboxSettings = new VBox(settings);

        processImage();
        Group canvas = new Group(displayedMap);

        HBox hbox = new HBox(vboxSettings,canvas);
        hbox.setSpacing(5);

        VBox vbox = new VBox(toolbar, hbox);
        vbox.setSpacing(5);

        primaryStage.setScene(new Scene (vbox));
    }

    private void initAntGenomes()
    {
        int size = antGenomes.size();
        antGenomes = new ArrayList<>();
        for(int i = 0; i < size;++i)
        {
            antGenomes.add(new AntGenome(
                    Integer.parseInt(textFields.get(i+" Health:").getText()),
                    Integer.parseInt(textFields.get(i+" Weight:").getText()),
                    Integer.parseInt(textFields.get(i+" Speed:").getText()),
                    Integer.parseInt(textFields.get(i+" Strength:").getText()),
                    Float.parseFloat(textFields.get(i+" View range:").getText())
                    ));
        }
    }

    private void initSimulation()
    {
        simulations = new ArrayList<>();

        for (int i = 0; i < 1; ++i) //generations pool
        {
            simulations.add(new Simulation(clone(map), maxFoodPerTile, foodSpawnAmount, foodSpawnProbability,
            x, y, antGenomes, initAnts, reproductionRate, basePheromoneLevel, antColor));
        }
        simulator = new Simulator(simulations.get(0),this,millisPerFrame);
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

        this.stop = new Button("Stop");
        this.stop.setOnAction(this::handleStop);

        this.reset = new Button("Reset");
        this.reset.setOnAction(this::handleReset);

        this.apply = new Button("Apply changes");
        this.apply.setOnAction(this::handleApply);

        HBox toolbar = new HBox();
        toolbar.getChildren().addAll(step, start, stop,  reset, apply);
        toolbar.setSpacing(5);
        return toolbar;
    }

    private void generateSettings()
    {
        settings = new Accordion();
        VBox vBox = new VBox();
        TitledPane simulation = new TitledPane("Simulation"  , vBox);
        vBox.getChildren().addAll(
                createTextField("Max food on tile:","100"),
                createTextField("Food spawn amount:","200"),
                createTextField("Food spawn prob:","0.3"),
                createTextField("millis/frame:","200")
                );

        settings.getPanes().addAll(simulation);
        addAnthill();
        addAntGenome(200,100,5,50,10);
        addAntGenome(200,100,3,200,5);
    }

    private void addAnthill()
    {
        VBox vBox = new VBox();
        vBox.getChildren().addAll(
                createTextField("Ant color:","#DC143C"),
                createTextField("x:","50"),
                createTextField("y:","50"),
                createTextField("Init number of ants:","50"),
                createTextField("Reproduction rate:","0.5"),
                createTextField("Base pheromone level:","1000"));

        TitledPane newAnthill = new TitledPane("Anthill", vBox);
        //anthills.put("Anthill " + anthills.size(), new Anthill());
        settings.getPanes().add(newAnthill);
    }

    private void addAntGenome(int health,int weight, int speed,int strength, float viewRange)
    {
        VBox vBox = new VBox();
        vBox.getChildren().addAll(
                createTextField(antGenomes.size() + " Health:",String.valueOf(health)),
                createTextField(antGenomes.size() + " Weight:",String.valueOf(weight)),
                createTextField(antGenomes.size() + " Speed:",String.valueOf(speed)),
                createTextField(antGenomes.size() + " Strength:",String.valueOf(strength)),
                createTextField(antGenomes.size() + " View range:",String.valueOf(viewRange))
                );

        TitledPane newAntGenome = new TitledPane("Ant genome " + antGenomes.size(), vBox);
        antGenomes.add(new AntGenome(health,weight,speed,strength,viewRange));
        settings.getPanes().add(newAntGenome);
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
        initialize();
        initAntGenomes();
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
        if(state == States.SIMULATING)
        {
            apply.setDisable(true);
            stop.setDisable(false);
            reset.setDisable(false);
        }
        if(state == States.EDITING)
        {
            stop.setDisable(true);
            reset.setDisable(true);
            apply.setDisable(false);
        }
        this.state = state;
    }

    public HBox createTextField(String label, String defaultText)
    {
        Label myLabel = new Label(label);
        myLabel.setFont(new Font("Arial", 13));

        TextField textField = new TextField(defaultText);
        textFields.put(label,textField);
        return (new HBox(myLabel, textField));
    }
}