package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import model.RobotModel;

public class CoordinateWindow extends JInternalFrame implements PropertyChangeListener {
    private final RobotModel model;
    private final JLabel labelX;
    private final JLabel labelY;
    private final JLabel labelDir;

    public CoordinateWindow(RobotModel model) {
        super("Координаты робота", true, true, true, true);
        this.model = model;
        this.model.addPropertyChangeListener(this);

        labelX = new JLabel("X: 100");
        labelY = new JLabel("Y: 100");
        labelDir = new JLabel("Dir: 0.00");

        JPanel panel = new JPanel(new BorderLayout());
        JPanel infoPanel = new JPanel();
        infoPanel.add(labelX);
        infoPanel.add(labelY);
        infoPanel.add(labelDir);
        panel.add(infoPanel, BorderLayout.CENTER);

        getContentPane().add(panel);
        pack();
        setLocation(200, 100);
        setVisible(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        EventQueue.invokeLater(() -> {
            labelX.setText(String.format("X: %.2f", model.getX()));
            labelY.setText(String.format("Y: %.2f", model.getY()));
            labelDir.setText(String.format("Dir: %.2f", model.getDirection()));
        });
    }
}