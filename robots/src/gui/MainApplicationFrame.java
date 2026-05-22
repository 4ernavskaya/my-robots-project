package gui;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JInternalFrame;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import log.Logger;
import model.RobotModel;
import controller.RobotController;

public class MainApplicationFrame extends JFrame {
    private final javax.swing.JDesktopPane desktopPane = new javax.swing.JDesktopPane();
    private final Map<String, Rectangle> normalBounds = new HashMap<>();

    public MainApplicationFrame() {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width  - inset * 2,
                screenSize.height - inset * 2);

        setContentPane(desktopPane);

        // === MVC ИНИЦИАЛИЗАЦИЯ ===
        RobotModel model = new RobotModel();
        RobotController controller = new RobotController(model);
        controller.start();

        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);
        saveNormalBounds(logWindow);

        // === GAME WINDOW ===
        GameVisualizer visualizer = new GameVisualizer();
        visualizer.setModel(model);

        GameWindow gameWindow = new GameWindow(visualizer);
        gameWindow.setSize(400, 400);
        addWindow(gameWindow);
        saveNormalBounds(gameWindow);

        // === COORDINATE WINDOW ===
        CoordinateWindow coordWindow = new CoordinateWindow(model);
        addWindow(coordWindow);
        saveNormalBounds(coordWindow);

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        SwingUtilities.invokeLater(this::restoreWindowStates);
    }

    private void saveNormalBounds(JInternalFrame frame) {
        normalBounds.put(frame.getTitle(), frame.getBounds());
    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    public void exitApplication() {
        saveWindowStates();

        UIManager.put("OptionPane.yesButtonText", "Да");
        UIManager.put("OptionPane.noButtonText", "Нет");
        int result = JOptionPane.showConfirmDialog(
                this,
                "Вы действительно хотите выйти?",
                "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION
        );
        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private javax.swing.JMenuBar generateMenuBar() {
        return new MenuBarBuilder(this).buildMenuBar();
    }

    public void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            javax.swing.SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException
                 | IllegalAccessException | UnsupportedLookAndFeelException e) {
            Logger.debug("Ошибка смены темы: " + e.getMessage());
        }
    }

    private void saveWindowStates() {
        List<WindowConfig> configs = new ArrayList<>();
        for (java.awt.Component comp : desktopPane.getComponents()) {
            if (comp instanceof JInternalFrame frame) {
                Rectangle bounds;
                int state;

                if (frame.isIcon()) {
                    state = 1;
                    bounds = normalBounds.getOrDefault(frame.getTitle(), frame.getBounds());
                } else if (frame.isMaximum()) {
                    state = 2;
                    bounds = normalBounds.getOrDefault(frame.getTitle(), frame.getBounds());
                } else {
                    state = 0;
                    bounds = frame.getBounds();
                    normalBounds.put(frame.getTitle(), bounds);
                }

                configs.add(new WindowConfig(
                        frame.getTitle(),
                        bounds.x, bounds.y, bounds.width, bounds.height,
                        state
                ));
            }
        }
        try {
            WindowConfigStorage.saveConfig(configs);
            Logger.debug("Конфигурация окон успешно сохранена.");
        } catch (IOException e) {
            Logger.debug("Ошибка сохранения конфигурации: " + e.getMessage());
        }
    }

    private void restoreWindowStates() {
        try {
            List<WindowConfig> savedConfigs = WindowConfigStorage.loadConfig();
            if (savedConfigs.isEmpty()) {
                Logger.debug("Нет сохранённой конфигурации окон.");
                return;
            }

            for (WindowConfig cfg : savedConfigs) {
                JInternalFrame target = findFrameByTitle(cfg.getTitle());
                if (target != null) {
                    try {
                        switch (cfg.getState()) {
                            case 1:
                                if (cfg.getWidth() > 0 && cfg.getHeight() > 0) {
                                    target.setBounds(cfg.getX(), cfg.getY(), cfg.getWidth(), cfg.getHeight());
                                }
                                normalBounds.put(cfg.getTitle(), target.getBounds());
                                target.setIcon(true);
                                break;
                            case 2:
                                target.setMaximum(true);
                                break;
                            default:
                                target.setBounds(cfg.getX(), cfg.getY(), cfg.getWidth(), cfg.getHeight());
                                target.setIcon(false);
                                target.setMaximum(false);
                                normalBounds.put(cfg.getTitle(), target.getBounds());
                                break;
                        }

                        if (cfg.getState() != 1) {
                            normalBounds.put(cfg.getTitle(), target.getBounds());
                        }
                    } catch (PropertyVetoException e) {
                        Logger.debug("Ошибка восстановления состояния окна: " + e.getMessage());
                    }
                }
            }
            Logger.debug("Конфигурация окон восстановлена.");
        } catch (IOException | ClassNotFoundException e) {
            Logger.debug("Файл конфигурации не найден, используются значения по умолчанию.");
        }
    }

    private JInternalFrame findFrameByTitle(String title) {
        for (java.awt.Component comp : desktopPane.getComponents()) {
            if (comp instanceof JInternalFrame frame && frame.getTitle().equals(title)) {
                return frame;
            }
        }
        return null;
    }
}