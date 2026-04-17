package gui;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JInternalFrame;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import log.Logger;

public class MainApplicationFrame extends JFrame {
    private final javax.swing.JDesktopPane desktopPane = new javax.swing.JDesktopPane();

    public MainApplicationFrame() {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        setContentPane(desktopPane);

        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400, 400);
        addWindow(gameWindow);

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        restoreWindowStates();
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
                Rectangle bounds = frame.getBounds();
                int state = frame.isIcon() ? 1 : (frame.isMaximum() ? 2 : 0);

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
            for (WindowConfig cfg : savedConfigs) {
                JInternalFrame target = findFrameByTitle(cfg.getTitle());
                if (target != null) {
                    target.setBounds(cfg.getX(), cfg.getY(), cfg.getWidth(), cfg.getHeight());
                    try {
                        if (cfg.getState() == 1) {
                            target.setIcon(true);
                        } else if (cfg.getState() == 2) {
                            target.setMaximum(true);
                        }
                    } catch (PropertyVetoException e) {
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