# CursorBar - Java Swing приложение с модальным окном

Это Java Swing приложение демонстрирует работу с модальным окном, содержащим круговой ProgressBar.

## Возможности

- **Модальное окно** с круговым ProgressBar
- **Анимация прогресса** с плавным вращением
- **Автоматическое закрытие** через 30 секунд
- **Закрытие по сигналу** от программы
- **Красивый UI** с современным дизайном

## Структура проекта

```
src/main/java/ru/gpm/example/mybatis/min/swing/
├── SwingApplication.java      # Главный класс для запуска
├── MainWindow.java            # Главное окно приложения
├── ProgressDialog.java        # Модальное окно с ProgressBar
└── CircularProgressBar.java   # Кастомный круговой ProgressBar
```

## Запуск

### Через Maven:
```bash
mvn compile exec:java
```

### Через Java:
```bash
mvn compile
java -cp target/classes ru.gpm.example.mybatis.min.swing.SwingApplication
```

## Использование

1. Запустите приложение
2. Нажмите кнопку "Показать модальное окно"
3. Наблюдайте анимацию кругового ProgressBar
4. Используйте кнопку "Закрыть по сигналу" для досрочного закрытия
5. Окно автоматически закроется через 30 секунд

## Технические детали

- **Java 17+**
- **Swing** для GUI
- **Timer** для анимации и таймаута
- **AtomicBoolean** для thread-safe сигнализации
- **Custom painting** для кругового ProgressBar
