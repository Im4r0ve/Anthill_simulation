package org.im4r0ve;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
 * Main class that generates the whole user interface and handles all of the user input
 */
public class App extends Application {
    enum States {
        EDITING,
        SIMULATING
    }
    private Image defaultImage;
    private States state;
    private Accordion settings;
    private Canvas displayedMap;
    private Canvas overlay;
    private Simulator simulator;
    private Button apply;
    private Button stop;
    private Button reset;
    private CheckBox showPheromones;
    private Label currentPopulation;
    private Label showStepsTaken;
    private int stepsTaken;

    private Map<String,TextField> textFields;

    private Tile[][] map;
    private ArrayList<Simulation> simulations;
    private ArrayList<AntGenome> antGenomes;

    private int height;
    private int width;

    //Simulation variables
    private int foodPerTile;
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

    /**
     * Launches the app
     */
    public static void main(String[] args)
    {
        launch(args);
    }

    /**
     * Entry method for the app. Initializes everything.
     */
    @Override
    public void start(Stage primaryStage)
    {
        initMainView(primaryStage);
        primaryStage.show();
        initialize();
    }
    //__________________________________________________________________________________________________________________
    //                                       Initialization
    //__________________________________________________________________________________________________________________
    /**
     * Function initialize() gets values of variables from corresponding text fields and initializes
     * all of the data structures based on variables
     */
    private void initialize()
    {
        foodPerTile = Integer.parseInt(textFields.get("Max food on tile:").getText());
        foodSpawnAmount = Integer.parseInt(textFields.get("Food spawn amount:").getText());
        foodSpawnProbability = Double.parseDouble(textFields.get("Food spawn prob:").getText());
        x = Integer.parseInt(textFields.get("x:").getText());
        y = Integer.parseInt(textFields.get("y:").getText());
        initAnts = Integer.parseInt(textFields.get("Init number of ants:").getText());
        reproductionRate = Double.parseDouble(textFields.get("Reproduction rate:").getText());
        basePheromoneLevel = Integer.parseInt(textFields.get("Base pheromone level:").getText());
        antColor = Color.web(textFields.get("Ant color:").getText());
        millisPerFrame = Integer.parseInt(textFields.get("millis/frame:").getText());

        processImage(defaultImage);
        initSimulation();
        setApplicationState(States.EDITING);
    }

    /**
     * InitMainView generates all of the visible UI
     * @param primaryStage - Main window of the application
     */
    private void initMainView(Stage primaryStage)
    {
        primaryStage.setTitle("Anthill simulator");
        textFields = new HashMap<>();
        antGenomes = new ArrayList<>();

        HBox toolbar = generateToolbar();

        generateSettings();
        VBox vboxSettings = new VBox(settings);

        loadImage();
        Group map = new Group(displayedMap,overlay);

        HBox hbox = new HBox(vboxSettings,map);
        hbox.setSpacing(5);

        VBox vbox = new VBox(toolbar, hbox);
        vbox.setSpacing(5);

        primaryStage.setScene(new Scene (vbox));
    }

    /**
     * initialization of all the simulations
     */
    private void initSimulation()
    {
        simulations = new ArrayList<>();

        for (int i = 0; i < 1; ++i) //multiple simulations will be needed for faster work with genetic algorithms
        {
            simulations.add(new Simulation(clone(map), foodPerTile, foodSpawnAmount, foodSpawnProbability,
                    x, y, antGenomes, initAnts, reproductionRate, basePheromoneLevel, antColor));
        }
        simulator = new Simulator(simulations.get(0),this,millisPerFrame);
    }
    //__________________________________________________________________________________________________________________
    //                                      Updates elements in the UI
    //__________________________________________________________________________________________________________________
    /**
     * Updates all AntGenomes after pressing Apply changes
     */
    private void updateAntGenomes()
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
                    Float.parseFloat(textFields.get(i+" View range:").getText()),
                    Integer.parseInt(textFields.get(i+" Pheromone trail:").getText()),
                    Integer.parseInt(textFields.get(i+" Pheromone trail with food:").getText()))
            );
        }
    }

    /**
     * Updates map and statistics after each step of the simulation
     * @param result result of the simulation.
     */
    public void updateSimulation(Result result)
    {
        drawMap(result.getMap());
        if(this.showPheromones.isSelected())
            drawOverlay(result.getOverlay());
        updateStatistics(result.getPopulation());
    }

    /**
     * Draws map by iterating through 2D array of object Tile. While iterating it calculates the color
     * of each tile and displays it.
     */
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
    private double translateRange(double oldMin,double oldMax, double newMin, double newMax, double oldValue)
    {
        return (((oldValue - oldMin) * (newMax - newMin)) / (oldMax - oldMin)) + newMin;
    }

    private void drawOverlay(int[][] overlay)
    {
        var writer = this.overlay.getGraphicsContext2D().getPixelWriter();
        for(int y = 0; y < height;++y)
        {
            for(int x = 0; x < width;++x)
            {
                if(overlay[x][y]>2000)
                    System.out.println("WTF");
                double translatedValue = translateRange(basePheromoneLevel,basePheromoneLevel*2,0.0,1.0, overlay[x][y]);
                writer.setColor(x,y, Color.rgb(255,255,0, translatedValue));
            }
        }
    }

    /**
     * Displays Current population and how many steps were taken by the ants in the simulation.
     * Labels are located int the toolbar.
     * @param population current population
     */
    private void updateStatistics( int population)
    {
        currentPopulation.setText("Current population: " + population);
        this.stepsTaken += population;
        this.showStepsTaken.setText("Steps taken: " + this.stepsTaken);
    }
    //__________________________________________________________________________________________________________________
    //                                       Generating UI elements
    //__________________________________________________________________________________________________________________
    /**
     * Generates the top toolbar of the UI
     * @return Returns an object of horizontally stacked buttons
     */
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

        this.showPheromones = new CheckBox("Show pheromones");
        this.showPheromones.setSelected(true);
        this.showPheromones.setMaxHeight(Double.MAX_VALUE);
        this.showPheromones.setAlignment(Pos.CENTER);


        this.currentPopulation = new Label("Current population: 0");
        this.showStepsTaken = new Label("Steps taken: 0");
        this.stepsTaken = 0;

        this.showStepsTaken.setMaxHeight(Double.MAX_VALUE);
        this.showStepsTaken.setAlignment(Pos.CENTER);

        this.currentPopulation.setMaxHeight(Double.MAX_VALUE);
        this.currentPopulation.setAlignment(Pos.CENTER);

        HBox toolbar = new HBox();
        toolbar.getChildren().addAll(step, start, stop,  reset, apply,showPheromones, currentPopulation,showStepsTaken);
        toolbar.setSpacing(5);
        return toolbar;
    }
    //__________________________________________________________________________________________________________________
    //                                       Setup of default values

    /**
     * Generates accordion style settings on the left side of the UI.
     * For now it is also responsible for setting up default values of Simulation and AntGenomes
     */
    private void generateSettings()
    {
        settings = new Accordion();
        VBox vBox = new VBox();
        TitledPane simulation = new TitledPane("Simulation"  , vBox);
        vBox.getChildren().addAll(
                createTextField("Max food on tile:","50"),
                createTextField("Food spawn amount:","250"),
                createTextField("Food spawn prob:","0.3"),
                createTextField("millis/frame:","200")
                );
        vBox.setSpacing(5);

        settings.getPanes().addAll(simulation);
        addAnthill("#DC143C",50,50,20,0.5,1000);
        addAntGenome(100,50,3,50,8,20,200);
        addAntGenome(200,100,3,200,5,20,200);
    }
    //__________________________________________________________________________________________________________________
    /**
     * Adds anthill Titled Pane to the settings panel.
     */
    private void addAnthill(String color, int x, int y, int initAnts, double reproductionRate, int basePheromoneLevel)
    {
        VBox vBox = new VBox();
        vBox.getChildren().addAll(
                createTextField("Ant color:",color),
                createTextField("x:",String.valueOf(x)),
                createTextField("y:",String.valueOf(y)),
                createTextField("Init number of ants:",String.valueOf(initAnts)),
                createTextField("Reproduction rate:",String.valueOf(reproductionRate)),
                createTextField("Base pheromone level:",String.valueOf(basePheromoneLevel)));
        vBox.setSpacing(5);

        TitledPane newAnthill = new TitledPane("Anthill", vBox);
        settings.getPanes().add(newAnthill);
    }

    /**
     * Adds AntGenome Titled Pane to the settings panel.
     */
    private void addAntGenome(int health,int weight, int speed,int strength, float viewRange, int pheromoneTrail,int pheromoneTrailFood)
    {
        VBox vBox = new VBox();
        vBox.getChildren().addAll(
                createTextField(antGenomes.size() + " Health:",String.valueOf(health)),
                createTextField(antGenomes.size() + " Weight:",String.valueOf(weight)),
                createTextField(antGenomes.size() + " Speed:",String.valueOf(speed)),
                createTextField(antGenomes.size() + " Strength:",String.valueOf(strength)),
                createTextField(antGenomes.size() + " View range:",String.valueOf(viewRange)),
                createTextField(antGenomes.size() + " Pheromone trail:",String.valueOf(pheromoneTrail)),
                createTextField(antGenomes.size() + " Pheromone trail with food:",String.valueOf(pheromoneTrailFood))
                );
        vBox.setSpacing(5);

        TitledPane newAntGenome = new TitledPane("Ant genome " + antGenomes.size(), vBox);
        antGenomes.add(new AntGenome(health,weight,speed,strength,viewRange, pheromoneTrail, pheromoneTrailFood));
        settings.getPanes().add(newAntGenome);
    }

    //__________________________________________________________________________________________________________________
    //                                              HANDLERS
    //__________________________________________________________________________________________________________________

    /**
     * Handles the press of step button in the toolbar.
     * Does one step in the simulation.
     */
    private void handleStep(ActionEvent actionEvent)
    {
        setApplicationState(States.SIMULATING);
        simulator.step();
    }

    /**
     * Handles start button. Starts the simulator.
     */
    private void handleStart(ActionEvent actionEvent)
    {
        setApplicationState(States.SIMULATING);
        simulator.start();
    }
    /**
     * Handles stop button. Stops the simulator.
     */
    private void handleStop(ActionEvent actionEvent)
    {
        simulator.stop();
    }
    /**
     * Resets the map to default and allows apply button to be pressed.
     */
    private void handleReset(ActionEvent actionEvent)
    {
        simulator.stop();
        setApplicationState(States.EDITING);
        initSimulation();
        drawMap(map);
    }
    /**
     * Applies all the changed settings. Only available in the EDITING state.
     */
    private void handleApply(ActionEvent actionEvent)
    {
        initialize();
        updateAntGenomes();
    }

    //__________________________________________________________________________________________________________________
    //                                              UTILS
    //__________________________________________________________________________________________________________________

    /**
     * Loads image of a map from predefined path and creates a map that is displayed in the window.
     */
    private void loadImage()
    {
        defaultImage = new Image("file:resources/map2.png");
        height = (int)defaultImage.getHeight();
        width = (int)defaultImage.getWidth();

        displayedMap = new Canvas(width,height);
        double minScale =  Math.min(600 / width,600 / height);
        displayedMap.setScaleX(minScale);
        displayedMap.setScaleY(minScale);

        overlay = new Canvas(width,height);
        overlay.setScaleX(minScale);
        overlay.setScaleY(minScale);
    }
    /**
     * Processes defaultImage by reading it's colors and creating internal Tile[][] map based on material that they represent.
     */
    private void processImage(Image image)
    {
        if(image.isError())
        {
            System.err.println("Error: empty image");
        }

        map = new Tile[width][height];
        var reader = image.getPixelReader();
        for(int y = 0; y < height;++y)
        {
            for(int x = 0; x < width;++x)
            {
                map[x][y] = new Tile(reader.getColor(x,y), foodPerTile, x, y);
            }
        }
        drawMap(map);
    }
    /**
     * Copies one Tile[][] to another.
     * It is needed for resetting the map.
     */
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

    /**
     * Changes application states and blocks buttons that should not be used,
     * @param state new state of application
     */
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

    /**
     * Creates formatted label and TextField next to it that is responsible for changing variables.
     * @param label for better clarity and also serves as identifier for accessing textFields.
     * @param defaultText of the textField.
     * @return returns one HBox, which consist of one Label and TextField next to it.
     */
    public HBox createTextField(String label, String defaultText)
    {
        Label myLabel = new Label(label);
        myLabel.setFont(new Font("Arial", 13));
        myLabel.setPadding(new Insets(5, 5, 5, 5));

        TextField textField = new TextField(defaultText);
        textField.setPadding(new Insets(5, 5, 5, 5));

        textFields.put(label,textField);
        return (new HBox(myLabel, textField));
    }
}