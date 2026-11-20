# Практическая работа №9

Коллекции Java и складской учёт оборудования.
Резвухин Георгий, ЭФБО-17-25. Вариант 21.
Пакет ru.edu.pr06 сохранён по заданию. Требуется JDK 21 или новее.

## Запуск

Из папки проекта на macOS/Linux:
- sh run_selfcheck.sh
- sh run_selfcheck.sh --interactive

На Windows: run_selfcheck.bat или run_selfcheck.bat --interactive.
Отдельный CSV: sh run_selfcheck.sh path/to/file.csv.
Любой FAIL завершает SelfCheck с кодом 1. Сборка использует --release 21.

## Демонстрация

Вводите по строке:
- add NB-01 Notebook COMPUTER 12
- receive NB-01 5
- issue NB-01 3
- find COMPUTER
- totals
- categories
- dates 2026-11-19
- exit

Имя товара в add задаётся одним словом. После команды печатаются размеры каталога,
истории и сводка. В dates вводите нужную дату.
В интерактивном режиме используется дата компьютера.
В SelfCheck фиксировано 19.11.2026 для воспроизводимости.

## CSV

Основной формат code;name;category;quantity;expected. expected опционален.
Успех: OK:qty=N, ошибка: SKIP:причина. Пробелы по краям полей запрещены.
Комментарии начинаются с #. В пяти строках варианта добавлено расширение
action;date;expectedMovements. RECEIVE/ISSUE меняют существующую позицию,
QUERY проверяет количество движений за дату.

## Состав

src/ru/edu/pr06 содержит шесть классов и перечислений.
diagrams содержит две диаграммы в DOT, PNG и SVG.
REPORT.docx — отчёт, DECISIONS.md — решения и ответы.
GUIDE.md — демонстрация 20 показателей таблицы оценивания.
test-results.txt и interactive-results.txt — фактические журналы запуска.

## Публикация

Репозиторий проекта — https://github.com/SanTW3/practice-09.
Имя из задания: pr9-rezvukhin-g. Префикс коммита: PR9:.
