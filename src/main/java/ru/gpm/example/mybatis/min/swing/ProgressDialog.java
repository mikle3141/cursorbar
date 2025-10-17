package ru.gpm.example.mybatis.min.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Модальное окно с круговым ProgressBar
 */
public class ProgressDialog extends JDialog {
    private CircularProgressBar progressBar;
    private Timer animationTimer;
    private Timer timeoutTimer;
    private AtomicBoolean shouldClose = new AtomicBoolean(false);
    private int animationStep = 0;
    private static final int ANIMATION_STEPS = 100;
    private static final int TIMEOUT_SECONDS = 30;
    private LocalizationManager localization;

    public ProgressDialog(Frame parent, String title) {
        super(parent, title, true);
        this.localization = LocalizationManager.getInstance();
        initializeComponents();
        setupLayout();
        startAnimation();
        startTimeoutTimer();
    }

    private void initializeComponents() {
        // Создаем круговой ProgressBar
        progressBar = new CircularProgressBar();
        progressBar.setMaximum(100);
        progressBar.setText(localization.getString("progress.loading"));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setSize(350, 200);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel(localization.getString("progress.title"), JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Центрируем круговой прогресс-бар
        JPanel progressPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        progressPanel.add(progressBar);
        
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(progressPanel, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
    }

    private void startAnimation() {
        // Изменяем интервал так, чтобы один цикл занимал 30 секунд
        // 30 секунд = 30000ms, делим на 100 шагов = 300ms на шаг
        animationTimer = new Timer(300, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (shouldClose.get()) {
                    closeDialog();
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
                closeDialog();
            }
        });
        timeoutTimer.setRepeats(false);
        timeoutTimer.start();
    }

    /**
     * Сигнал для закрытия диалога
     */
    public void signalClose() {
        shouldClose.set(true);
    }

    private void closeDialog() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
        if (timeoutTimer != null) {
            timeoutTimer.stop();
        }
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
        super.dispose();
    }
}
