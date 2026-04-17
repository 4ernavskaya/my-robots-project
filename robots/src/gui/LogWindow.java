package gui;

import java.awt.BorderLayout; // BorderLayout делит область на 5 частей: NORTH, SOUTH, EAST, WEST, CENTER
import java.awt.EventQueue;// Позволяет выполнять код в потоке обработки событий
import java.awt.TextArea;// Импорт класса TextArea - многострочное текстовое поле (устаревшее, но простое)

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import log.LogChangeListener; // Импорт интерфейса LogChangeListener - для получения уведомлений об изменениях в логе
import log.LogEntry;// Импорт класса LogEntry - представляет одну запись в логе (одно сообщение)
import log.LogEntry;
import log.LogWindowSource;// Импорт класса LogWindowSource - источник логов (где хранятся сообщения)

public class LogWindow extends JInternalFrame implements LogChangeListener
{
    private LogWindowSource m_logSource;
    private TextArea m_logContent;

    public LogWindow(LogWindowSource logSource)
    {
        super("Протокол работы", true, true, true, true);
        m_logSource = logSource;
        m_logSource.registerListener(this);
        m_logContent = new TextArea("");
        m_logContent.setSize(200, 500);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateLogContent();
    }

    private void updateLogContent()
    {
        StringBuilder content = new StringBuilder();
        for (LogEntry entry : m_logSource.all())

        {
            content.append(entry.getMessage()).append("\n");
        }
        m_logContent.setText(content.toString());
        m_logContent.invalidate();
    }

    @Override
    public void onLogChanged()
    {
        EventQueue.invokeLater(this::updateLogContent);
    }
}