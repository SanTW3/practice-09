# ПР9 — гайд по таблице оценивания

Фактически в таблице 20 показателей, хотя вводный текст говорит о 40.
Вариант 21. Откройте Java-классы, sample.csv, DECISIONS.md и диаграммы.
Запуск: sh run_selfcheck.sh. Ввод команд: sh run_selfcheck.sh --interactive.

## 1. Создание StockItem

**Показать:** StockItem constructor; unit/1–2.

**Сказать:** «Корректное создание даёт qty=12. Отрицательное количество вызывает IllegalArgumentException.»

## 2. increase и decrease

**Показать:** StockItem.increase/decrease; unit/3–6.

**Сказать:** «Приход 5 к остатку 12 даёт 17. Для отказа decrease(13) используйте отдельную позицию с остатком 12.»

## 3. Добавление и дубликат

**Показать:** Warehouse.addItem; unit/7–9.

**Сказать:** «containsKey отклоняет повторный code. Новая запись не заменяет исходную.»

## 4. Приход

**Показать:** Warehouse.receive; unit/10–11.

**Сказать:** «receive NB-01 5 даёт 17 и одну запись. Неизвестный код вызывает IAE.»

## 5. Выдача

**Показать:** Warehouse.issue; unit/12–14.

**Сказать:** «Выдача 3 из 12 оставляет 9. Выдача 100 отклоняется до изменения остатка и истории.»

## 6. Поиск по категории

**Показать:** Warehouse.findByCategory; unit/15–17.

**Сказать:** «Новый ArrayList и копии StockItem. Изменение результата не меняет склад.»

## 7. Сводка

**Показать:** Warehouse.totalsByCategory; unit/18.

**Сказать:** «Цикл по items.values суммирует quantity через getOrDefault.»

## 8. equals и hashCode

**Показать:** StockItem.equals/hashCode; unit/26.

**Сказать:** «Методы используют code. HashSet считает одинаковые коды одним элементом. Ключ items — String, Map использует методы строки.»

## 9. Map для каталога

**Показать:** Warehouse.items и requireItem.

**Сказать:** «Map даёт ожидаемое O(1) обращения по коду. Перебор List потребовал бы O(n).»

## 10. Инкапсуляция

**Показать:** private final items и movements; unit/27, 30, 32.

**Сказать:** «Прямых getter коллекций нет. На входе и выходе копируются изменяемые карточки.»

## 11. record StockMovement

**Показать:** StockMovement.java; unit/19–20.

**Сказать:** «Компоненты itemCode, type, quantity и date неизменяемы.»

## 12. Category

**Показать:** Category.java; unit/24.

**Сказать:** «Доступны COMPUTER, DISPLAY, PERIPHERAL, NETWORK. UNKNOWN вызывает IAE в valueOf.»

## 13. Изменение требования

**Показать:** StockItem.decrease.

**Сказать:** «В варианте 21 резерва нет. По запросу добавьте MIN_RESERVE=5 и проверку quantity-q < MIN_RESERVE, затем измените 5 на 10. Обновите тест выдачи до нуля, проверьте остатки 10 и 9. После показа восстановите вариант 21.»

## 14. Data-driven

**Показать:** SelfCheck.dataChecks/readRow; sample.csv.

**Сказать:** «Заголовок и # пропускаются. OK сравнивает остаток, SKIP — причину. Пять расширенных строк проверяют дату истории.»

## 15. Интерактивный режим

**Показать:** SelfCheck.interactive.

**Сказать:** «Запустите sh run_selfcheck.sh --interactive. add NB-01 Notebook COMPUTER 12, receive NB-01 5, issue NB-01 3. Остаток 14, движений 2.»

## 16. Анализ кода

**Показать:** DECISIONS.md.

**Сказать:** «Линейный поиск заменён items.get(code). Проверка null обеспечивает R9, private поле — R6.»

## 17. Вариант 21

**Показать:** Warehouse.receive/issue/addMovementsByDate; unit/19–22.

**Сказать:** «Дата через LocalDate.now(clock), поиск по date.equals. Возвращается новый список.»

## 18. Граница decrease

**Показать:** StockItem.decrease; unit/25.

**Сказать:** «Из 12 выдача 12 разрешена, остаток ноль. Запрет q > quantity строгий.»

## 19. Самопроверка

**Показать:** SelfCheck.main/check.

**Сказать:** «33 unit и 16 CSV проверок дали PASS. Ожидаемое исключение тоже является успешной проверкой.»

## 20. Разделение ответственности

**Показать:** StockItem.decrease и Warehouse.issue.

**Сказать:** «StockItem контролирует количество, Warehouse — каталог и историю, SelfCheck — ввод и проверки.»

