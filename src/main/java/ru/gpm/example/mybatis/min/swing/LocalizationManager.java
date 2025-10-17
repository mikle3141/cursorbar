package ru.gpm.example.mybatis.min.swing;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Менеджер локализации для поддержки многоязычности
 */
public class LocalizationManager {
    private static LocalizationManager instance;
    private Properties properties;
    private String currentLanguage;
    
    private LocalizationManager() {
        loadConfiguration();
        loadLanguageResources();
    }
    
    public static LocalizationManager getInstance() {
        if (instance == null) {
            instance = new LocalizationManager();
        }
        return instance;
    }
    
    private void loadConfiguration() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("plugin.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            System.err.println("Error loading plugin.properties: " + e.getMessage());
        }
        
        currentLanguage = properties.getProperty("app.language", "en");
    }
    
    private void loadLanguageResources() {
        String languageFile = "messages_" + currentLanguage + ".properties";
        Properties languageProps = new Properties();
        
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(languageFile)) {
            if (input != null) {
                languageProps.load(input);
                // Объединяем с основными настройками
                properties.putAll(languageProps);
            } else {
                System.err.println("Language file not found: " + languageFile + ", using English");
                loadDefaultEnglish();
            }
        } catch (IOException e) {
            System.err.println("Error loading language file: " + e.getMessage());
            loadDefaultEnglish();
        }
    }
    
    private void loadDefaultEnglish() {
        // Загружаем английские переводы по умолчанию
        properties.setProperty("app.title", "Website Availability Checker");
        properties.setProperty("button.check.website", "Check Website Availability");
        properties.setProperty("button.show.demo", "Show Demo ProgressBar");
        properties.setProperty("button.close.signal", "Close by Signal");
        properties.setProperty("status.ready", "Enter website URL to check");
        properties.setProperty("status.checking", "Checking website availability");
        properties.setProperty("status.completed", "Check completed");
        properties.setProperty("status.empty.url", "URL cannot be empty");
        properties.setProperty("status.modal.open", "Modal window opened");
        properties.setProperty("status.modal.closed", "Modal window closed");
        properties.setProperty("status.signal.sent", "Close signal sent");
        properties.setProperty("status.signal.unavailable", "Function unavailable for website checks");
        
        // Диалог ввода URL
        properties.setProperty("dialog.url.title", "Website Availability Check");
        properties.setProperty("dialog.url.message", "Enter website URL to check:");
        
        // Диалог проверки сайта
        properties.setProperty("dialog.check.title", "Website Availability Check");
        properties.setProperty("dialog.check.checking", "Checking...");
        properties.setProperty("dialog.check.status", "Checking website availability");
        
        // Результаты проверки
        properties.setProperty("result.available.title", "Website Available");
        properties.setProperty("result.unavailable.title", "Website Unavailable");
        properties.setProperty("result.url", "URL");
        properties.setProperty("result.status", "Result");
        properties.setProperty("result.response.time", "Response Time");
        properties.setProperty("result.timeout", "Check interrupted: timeout exceeded (30 seconds)");
        
        // Прогресс диалог
        properties.setProperty("progress.title", "Please wait...");
        properties.setProperty("progress.loading", "Loading...");
        
        // Ошибки
        properties.setProperty("error.unknown.host", "Website unavailable: cannot find host");
        properties.setProperty("error.connection", "Website unavailable: connection error");
        properties.setProperty("error.timeout", "Website unavailable: timeout exceeded");
        properties.setProperty("error.io", "Website unavailable: I/O error");
        properties.setProperty("error.unexpected", "Website unavailable: unexpected error");
    }
    
    public String getString(String key) {
        return properties.getProperty(key, key);
    }
    
    public String getString(String key, Object... args) {
        String template = getString(key);
        return String.format(template, args);
    }
    
    public String getCurrentLanguage() {
        return currentLanguage;
    }
    
    public boolean isRussian() {
        return "ru".equals(currentLanguage);
    }
    
    public boolean isEnglish() {
        return "en".equals(currentLanguage);
    }
}
