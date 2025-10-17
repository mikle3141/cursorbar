package ru.gpm.example.mybatis.min.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;

/**
 * Кастомный круговой ProgressBar
 */
public class CircularProgressBar extends JComponent {
    private int progress = 0;
    private int maximum = 100;
    private Color progressColor = new Color(0, 120, 215);
    private Color backgroundColor = new Color(240, 240, 240);
    private int strokeWidth = 8;
    private String text = "";

    public CircularProgressBar() {
        setPreferredSize(new Dimension(120, 120));
    }

    public void setProgress(int progress) {
        this.progress = Math.max(0, Math.min(progress, maximum));
        repaint();
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
        repaint();
    }

    public void setText(String text) {
        this.text = text;
        repaint();
    }

    public void setProgressColor(Color color) {
        this.progressColor = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int size = Math.min(width, height) - strokeWidth;
        int x = (width - size) / 2;
        int y = (height - size) / 2;

        // Рисуем фон
        g2d.setColor(backgroundColor);
        g2d.fillOval(x, y, size, size);

        // Рисуем прогресс
        if (progress > 0) {
            g2d.setColor(progressColor);
            g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            double angle = 360.0 * progress / maximum;
            g2d.draw(new Arc2D.Double(x, y, size, size, 90, -angle, Arc2D.OPEN));
        }

        // Рисуем текст
        if (!text.isEmpty()) {
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getHeight();
            g2d.drawString(text, (width - textWidth) / 2, (height + textHeight / 2) / 2);
        }

        g2d.dispose();
    }
}
