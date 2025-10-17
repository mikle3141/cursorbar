package ru.gpm.example.mybatis.min.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CompletableFuture;

/**
 * Главное окно приложения для проверки доступности веб-сайтов
 */
public class MainWindow extends JFrame {
    private WebsiteCheckDialog websiteCheckDialog;
    private JButton checkWebsiteButton;
    private JButton showProgressButton;
    private JButton signalCloseButton;
    private JLabel statusLabel;

    public MainWindow() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    private void initializeComponents() {
        setTitle("Проверка доступности веб-сайтов");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 350);
        setLocationRelativeTo(null);

        checkWebsiteButton = new JButton("Проверить доступность сайта");
        showProgressButton = new JButton("Показать демо ProgressBar");
        signalCloseButton = new JButton("Закрыть по сигналу");
        signalCloseButton.setEnabled(false);
        
        statusLabel = new JLabel("Введите URL сайта для проверки", JLabel.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(checkWebsiteButton);
        buttonPanel.add(showProgressButton);
        buttonPanel.add(signalCloseButton);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.add(buttonPanel, BorderLayout.CENTER);
        contentPanel.add(statusLabel, BorderLayout.SOUTH);
        
        add(contentPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        checkWebsiteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showWebsiteCheckDialog();
            }
        });

        showProgressButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showProgressDialog();
            }
        });

        signalCloseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signalCloseProgressDialog();
            }
        });
    }

    private void showWebsiteCheckDialog() {
        // Запрашиваем URL у пользователя
        String websiteUrl = JOptionPane.showInputDialog(
            this,
            "Введите URL сайта для проверки:",
            "Проверка доступности сайта",
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (websiteUrl != null && !websiteUrl.trim().isEmpty()) {
            checkWebsiteButton.setEnabled(false);
            statusLabel.setText("Проверяем доступность сайта: " + websiteUrl);
            
            // Создаем и показываем диалог проверки сайта
            SwingUtilities.invokeLater(() -> {
                websiteCheckDialog = new WebsiteCheckDialog(this, websiteUrl.trim());
                websiteCheckDialog.setVisible(true);
                
                // После закрытия диалога обновляем UI
                SwingUtilities.invokeLater(() -> {
                    checkWebsiteButton.setEnabled(true);
                    statusLabel.setText("Проверка завершена");
                });
            });
        } else if (websiteUrl != null) {
            statusLabel.setText("URL не может быть пустым");
        }
    }

    private void showProgressDialog() {
        showProgressButton.setEnabled(false);
        signalCloseButton.setEnabled(true);
        statusLabel.setText("Модальное окно открыто...");
        
        // Создаем и показываем модальный диалог в отдельном потоке
        CompletableFuture.runAsync(() -> {
            SwingUtilities.invokeLater(() -> {
                ProgressDialog progressDialog = new ProgressDialog(this, "Информационное окно");
                progressDialog.setVisible(true);
                
                // После закрытия диалога обновляем UI
                SwingUtilities.invokeLater(() -> {
                    showProgressButton.setEnabled(true);
                    signalCloseButton.setEnabled(false);
                    statusLabel.setText("Модальное окно закрыто");
                });
            });
        });
    }

    private void signalCloseProgressDialog() {
        // Эта функция теперь не используется для проверки сайтов
        // так как проверка завершается автоматически
        statusLabel.setText("Функция недоступна для проверки сайтов");
    }

    public static void main(String[] args) {
        // Устанавливаем Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new MainWindow().setVisible(true);
        });
    }
}
