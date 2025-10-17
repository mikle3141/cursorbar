#!/bin/bash
# Скрипт для запуска приложения CursorBar

cd "$(dirname "$0")"

echo "Компиляция проекта..."
mvn compile

if [ $? -eq 0 ]; then
    echo ""
    echo "Запуск приложения..."
    mvn exec:java
else
    echo "Ошибка компиляции!"
    exit 1
fi

