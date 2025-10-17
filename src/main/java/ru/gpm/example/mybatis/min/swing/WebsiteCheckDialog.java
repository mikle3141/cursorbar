package ru.gpm.example.mybatis.min.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Диалог для проверки доступности веб-сайта с круговым ProgressBar
 */
public class WebsiteCheckDialog extends JDialog {
    private CircularProgressBar progressBar;
    private Timer animationTimer;
    private Timer timeoutTimer;
    private AtomicBoolean shouldClose = new AtomicBoolean(false);
    private int animationStep = 0;
    private static final int ANIMATION_STEPS = 100;
    private static final int TIMEOUT_SECONDS = 30;
    private String websiteUrl;
    private CompletableFuture<WebsiteChecker.CheckResult> checkFuture;
    private JLabel statusLabel;
    private LocalizationManager localization;

    public WebsiteCheckDialog(Frame parent, String websiteUrl) {
        super(parent, "", true);
        this.websiteUrl = websiteUrl;
        this.localization = LocalizationManager.getInstance();
        setTitle(localization.getString("dialog.check.title"));
        initializeComponents();
        setupLayout();
        startWebsiteCheck();
        startAnimation();
        startTimeoutTimer();
    }

    private void initializeComponents() {
        // Создаем круговой ProgressBar
        progressBar = new CircularProgressBar();
        progressBar.setMaximum(100);
        progressBar.setText(localization.getString("dialog.check.checking"));
        
        statusLabel = new JLabel(localization.getString("dialog.check.status") + ": " + websiteUrl, JLabel.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setSize(400, 250);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel(localization.getString("dialog.check.title"), JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Центрируем круговой прогресс-бар
        JPanel progressPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        progressPanel.add(progressBar);
        
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(progressPanel, BorderLayout.CENTER);
        contentPanel.add(statusLabel, BorderLayout.SOUTH);
        
        add(contentPanel, BorderLayout.CENTER);
    }

    private void startWebsiteCheck() {
        // Запускаем проверку сайта асинхронно
        checkFuture = WebsiteChecker.checkWebsiteAsync(websiteUrl);
        
        // Обрабатываем результат
        checkFuture.thenAccept(result -> {
            SwingUtilities.invokeLater(() -> {
                closeDialogWithResult(result);
            });
        });
    }

    private void startAnimation() {
        // Анимация с интервалом 300ms для одного цикла за 30 секунд
        animationTimer = new Timer(300, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (shouldClose.get()) {
                    return;
                }
                
                animationStep = (animationStep + 1) % ANIMATION_STEPS;
                int progress = (animationStep * 100) / ANIMATION_STEPS;
                progressBar.setProgress(progress);
                progressBar.setText(String.format("%d%%", progress));
            }
        });
        animationTimer.start();
    }

    private void startTimeoutTimer() {
        timeoutTimer = new Timer(TIMEOUT_SECONDS * 1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!checkFuture.isDone()) {
                    // Если проверка не завершилась за 30 секунд, прерываем её
                    checkFuture.cancel(true);
                    WebsiteChecker.CheckResult timeoutResult = new WebsiteChecker.CheckResult(
                        false, localization.getString("result.timeout"), 30000);
                    closeDialogWithResult(timeoutResult);
                }
            }
        });
        timeoutTimer.setRepeats(false);
        timeoutTimer.start();
    }

    private void closeDialogWithResult(WebsiteChecker.CheckResult result) {
        shouldClose.set(true);
        
        // Останавливаем таймеры
        if (animationTimer != null) {
            animationTimer.stop();
        }
        if (timeoutTimer != null) {
            timeoutTimer.stop();
        }
        
        // Показываем результат
        showResult(result);
    }

    private void showResult(WebsiteChecker.CheckResult result) {
        // Создаем диалог с результатом
        String title = result.isAvailable() ? 
            localization.getString("result.available.title") : 
            localization.getString("result.unavailable.title");
        String message = String.format(
            "<html><body style='width: 300px; text-align: center;'>" +
            "<h3>%s</h3>" +
            "<p><b>%s:</b> %s</p>" +
            "<p><b>%s:</b> %s</p>" +
            "<p><b>%s:</b> %d мс</p>" +
            "</body></html>",
            title, 
            localization.getString("result.url"), websiteUrl, 
            localization.getString("result.status"), result.getMessage(), 
            localization.getString("result.response.time"), result.getResponseTime()
        );
        
        JOptionPane.showMessageDialog(
            this, 
            message, 
            title, 
            result.isAvailable() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE
        );
        
        dispose();
    }

    @Override
    public void dispose() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
        if (timeoutTimer != null) {
            timeoutTimer.stop();
        }
        if (checkFuture != null && !checkFuture.isDone()) {
            checkFuture.cancel(true);
        }
        super.dispose();
    }
}
