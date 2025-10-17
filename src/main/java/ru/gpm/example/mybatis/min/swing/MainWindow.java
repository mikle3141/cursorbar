package ru.gpm.example.mybatis.min.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CompletableFuture;

/**
 * Главное окно приложения для демонстрации модального диалога с ProgressBar
 */
public class MainWindow extends JFrame {
    private ProgressDialog progressDialog;
    private JButton showProgressButton;
    private JButton signalCloseButton;
    private JLabel statusLabel;

    public MainWindow() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    private void initializeComponents() {
        setTitle("Демонстрация модального окна с ProgressBar");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        showProgressButton = new JButton("Показать модальное окно");
        signalCloseButton = new JButton("Закрыть по сигналу");
        signalCloseButton.setEnabled(false);
        
        statusLabel = new JLabel("Готов к работе", JLabel.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(showProgressButton);
        buttonPanel.add(signalCloseButton);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.add(buttonPanel, BorderLayout.CENTER);
        contentPanel.add(statusLabel, BorderLayout.SOUTH);
        
        add(contentPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
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

    private void showProgressDialog() {
        showProgressButton.setEnabled(false);
        signalCloseButton.setEnabled(true);
        statusLabel.setText("Модальное окно открыто...");
        
        // Создаем и показываем модальный диалог в отдельном потоке
        CompletableFuture.runAsync(() -> {
            SwingUtilities.invokeLater(() -> {
                progressDialog = new ProgressDialog(this, "Информационное окно");
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
        if (progressDialog != null) {
            progressDialog.signalClose();
            statusLabel.setText("Отправлен сигнал закрытия...");
        }
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
