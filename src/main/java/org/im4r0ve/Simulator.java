package org.im4r0ve;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.util.Duration;

public class Simulator
{
    private App app;
    private Simulation simulation;
    private Timeline timeline;

    public Simulator(Simulation simulation, App app)
    {
        this.timeline = new Timeline(new KeyFrame(Duration.millis(200), this::step));
        this.timeline.setCycleCount(Timeline.INDEFINITE);
        this.simulation = simulation;
        this.app = app;
    }

    private void step(ActionEvent actionEvent)
    {
        Thread taskThread = new Thread(() ->
        {
            Tile[][] finalNewMap = simulation.step();
            Platform.runLater(() -> app.drawMap(finalNewMap));
        });
        taskThread.start();
    }

    public void start()
    {
        this.timeline.play();
    }

    public void stop()
    {
        this.timeline.stop();
    }
}
