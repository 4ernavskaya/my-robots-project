package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;
import model.RobotModel;

public class GameVisualizer extends JPanel implements PropertyChangeListener {
    private RobotModel model;

    public GameVisualizer() {
        setDoubleBuffered(true);
        setPreferredSize(new Dimension(400, 400));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (model != null) {
                    model.setTarget(e.getX(), e.getY());
                }
            }
        });
    }

    public void setModel(RobotModel model) {
        if (this.model != null) {
            this.model.removePropertyChangeListener(this);
        }
        this.model = model;
        model.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        EventQueue.invokeLater(this::repaint);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (model == null) return;

        Graphics2D g2d = (Graphics2D) g;
        drawRobot(g2d, round(model.getX()), round(model.getY()), model.getDirection());
        drawTarget(g2d, model.getTargetX(), model.getTargetY());
    }

    private void drawRobot(Graphics2D g, int x, int y, double direction) {
        int robotCenterX = x;
        int robotCenterY = y;

        java.awt.geom.AffineTransform originalTransform = g.getTransform();
        g.rotate(direction, robotCenterX, robotCenterY);

        g.setColor(Color.MAGENTA);
        fillOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, robotCenterX + 10, robotCenterY, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX + 10, robotCenterY, 5, 5);

        g.setTransform(originalTransform);
    }

    private void drawTarget(Graphics2D g, int x, int y) {
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }

    private static void fillOval(Graphics g, int cx, int cy, int w, int h) {
        g.fillOval(cx - w/2, cy - h/2, w, h);
    }

    private static void drawOval(Graphics g, int cx, int cy, int w, int h) {
        g.drawOval(cx - w/2, cy - h/2, w, h);
    }

    private static int round(double v) {
        return (int)(v + 0.5);
    }
}