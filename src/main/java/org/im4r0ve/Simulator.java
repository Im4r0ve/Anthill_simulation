package org.im4r0ve;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.util.Duration;

/**
 * Simulation driver. Simulates and draws simulation.
 * Implements timeline that allows us to start and stop the simulation.
 */
public class Simulator
{
    private App app;
    private Simulation simulation;
    private Timeline timeline;

    /**
     * Constructor of the simulator.
     * @param simulation ref to simulation that we want to show.
     * @param app for drawing the map.
     * @param millisPerFrame speed of simulation.
     */
    public Simulator(Simulation simulation, App app, int millisPerFrame)
    {
        this.timeline = new Timeline(new KeyFrame(Duration.millis(millisPerFrame), this::simulate));
        this.timeline.setCycleCount(Timeline.INDEFINITE);
        this.simulation = simulation;
        this.app = app;
    }

    /**
     * Does one step of the simulation
     * @param actionEvent
     */
    private void simulate(ActionEvent actionEvent)
    {
        step();
    }

    /**
     * Updates the map by calling step on the simulation in the new thread and
     * then the main thread shows the map when it has time.
     */
    public void step()
    {
        Thread taskThread = new Thread(() ->
        {
            Result result = simulation.step();
            Platform.runLater(() -> app.updateSimulation(result));
        });
        taskThread.start();
    }

    /**
     * Starts/continues the simulation
     */
    public void start()
    {
        this.timeline.play();
    }
    /**
     * Stops the simulation
     */
    public void stop()
    {
        this.timeline.stop();
    }
}
