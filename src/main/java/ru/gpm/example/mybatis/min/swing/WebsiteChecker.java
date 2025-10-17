package ru.gpm.example.mybatis.min.swing;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Класс для проверки доступности веб-сайта
 */
public class WebsiteChecker {
    
    public static class CheckResult {
        private final boolean isAvailable;
        private final String message;
        private final long responseTime;
        
        public CheckResult(boolean isAvailable, String message, long responseTime) {
            this.isAvailable = isAvailable;
            this.message = message;
            this.responseTime = responseTime;
        }
        
        public boolean isAvailable() {
            return isAvailable;
        }
        
        public String getMessage() {
            return message;
        }
        
        public long getResponseTime() {
            return responseTime;
        }
    }
    
    /**
     * Проверяет доступность сайта асинхронно
     * @param urlString URL для проверки
     * @return CompletableFuture с результатом проверки
     */
    public static CompletableFuture<CheckResult> checkWebsiteAsync(String urlString) {
        return CompletableFuture.supplyAsync(() -> {
            return checkWebsite(urlString);
        });
    }
    
    /**
     * Проверяет доступность сайта синхронно
     * @param urlString URL для проверки
     * @return результат проверки
     */
    public static CheckResult checkWebsite(String urlString) {
        LocalizationManager localization = LocalizationManager.getInstance();
        long startTime = System.currentTimeMillis();
        
        try {
            // Добавляем протокол если его нет
            if (!urlString.startsWith("http://") && !urlString.startsWith("https://")) {
                urlString = "http://" + urlString;
            }
            
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            // Устанавливаем таймаут
            connection.setConnectTimeout(5000); // 5 секунд на подключение
            connection.setReadTimeout(10000);   // 10 секунд на чтение
            
            // Устанавливаем User-Agent
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            
            int responseCode = connection.getResponseCode();
            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;
            
            connection.disconnect();
            
            if (responseCode >= 200 && responseCode < 400) {
                return new CheckResult(true, 
                    localization.getString("result.available.message", responseCode, responseTime), 
                    responseTime);
            } else {
                return new CheckResult(false, 
                    localization.getString("result.unavailable.message", responseCode, responseTime), 
                    responseTime);
            }
            
        } catch (UnknownHostException e) {
            long endTime = System.currentTimeMillis();
            return new CheckResult(false, 
                localization.getString("error.unknown.host.with.time", urlString, endTime - startTime), 
                endTime - startTime);
        } catch (ConnectException e) {
            long endTime = System.currentTimeMillis();
            return new CheckResult(false, 
                localization.getString("error.connection.with.time", endTime - startTime), 
                endTime - startTime);
        } catch (SocketTimeoutException e) {
            long endTime = System.currentTimeMillis();
            return new CheckResult(false, 
                localization.getString("error.timeout.with.time", endTime - startTime), 
                endTime - startTime);
        } catch (IOException e) {
            long endTime = System.currentTimeMillis();
            return new CheckResult(false, 
                localization.getString("error.io.with.time", e.getMessage(), endTime - startTime), 
                endTime - startTime);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            return new CheckResult(false, 
                localization.getString("error.unexpected.with.time", e.getMessage(), endTime - startTime), 
                endTime - startTime);
        }
    }
}
