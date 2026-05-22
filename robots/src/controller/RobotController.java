package controller;

import model.RobotModel;
import java.util.Timer;
import java.util.TimerTask;

public class RobotController {
    private final RobotModel model;
    private final Timer timer;

    public RobotController(RobotModel model) {
        this.model = model;
        this.timer = new Timer("robot-controller", true);
    }

    public void start() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                model.update();
            }
        }, 0, 10);
    }
}