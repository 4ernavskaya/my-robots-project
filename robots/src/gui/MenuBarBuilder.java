package gui;

import java.awt.event.KeyEvent;
import javax.swing.*;

import log.Logger;


public class MenuBarBuilder {

    private final MainApplicationFrame frame;

    public MenuBarBuilder(MainApplicationFrame frame) {
        this.frame = frame;
    }

    public JMenuBar buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        menuBar.add(createFileMenu());
        menuBar.add(createLookAndFeelMenu());
        menuBar.add(createTestMenu());

        return menuBar;
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("Файл");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.add(createExitMenuItem());
        return fileMenu;
    }

    private JMenuItem createExitMenuItem() {
        JMenuItem exitMenuItem = new JMenuItem("Выход");
        exitMenuItem.setMnemonic(KeyEvent.VK_X);
        exitMenuItem.addActionListener((event)-> {
            System.out.println("Выход из приложения");
            frame.exitApplication();
        });
        return exitMenuItem;
    }

    private JMenu createLookAndFeelMenu() {
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        lookAndFeelMenu.add(createSystemLookAndFeelItem());
        lookAndFeelMenu.add(createCrossplatformLookAndFeelItem());

        return lookAndFeelMenu;
    }

    private JMenuItem createSystemLookAndFeelItem() {
        JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
        systemLookAndFeel.addActionListener((event) -> {
            frame.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            frame.invalidate();
        });
        return systemLookAndFeel;
    }

    private JMenuItem createCrossplatformLookAndFeelItem() {
        JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
        crossplatformLookAndFeel.addActionListener((event) -> {
            frame.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            frame.invalidate();
        });
        return crossplatformLookAndFeel;
    }

    private JMenu createTestMenu() {
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        testMenu.add(createAddLogMessageItem());

        return testMenu;
    }

    private JMenuItem createAddLogMessageItem() {
        JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) -> {
            Logger.debug("Новая строка");
        });
        return addLogMessageItem;
    }
}