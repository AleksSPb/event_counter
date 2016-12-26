Задача: Реализовать объект для учета однотипных событий. События поступают в произвольный момент времени. Возможно как 10К событий в секунду, так и 2 в час. Интерфейс:
1. Учесть событие.
2. Выдать число событий за последнюю минуту
3. Выдать число событий за последний час
4. Выдать число событий за последние сутки

Вариант 1(EventCounterImpArray):
Сделанные мною допущения:
    Не требуется обеспечивать отказоустойчивость(при сбое происходит полная потеря данных).
    Подсчет не должен быть абсолютно точным, допускаются отклонения в размере количества событий произошедших за 1 секунду.

Выбранная реализация:
    При сделанных допущениях достаточно вести подсчет событий произошедших в 1 секунду. Для минимизации требований к ресурсам сделаем массив содержащий
    количество произошедших событий для каждой секунды в сутках. После прохождения суток запись опять начинается с начала массива.
    Данное решение не является точным подсчетом, т.к. расчет выполняется по секундно
    Значение текущей секунды обнуляется и производится его инкремент при получении события
    Т.е. получение статистики за минуту будет включать события за 59 предыдущих секунд + произошедшие до момента запроса статистики события текущей секунды
    Данный вариант выбран в связи с его устойчивостью к большим нагрузкам.

Вариант 2(EventCounterImpH2DB):
Сделанные мною допущения:
    Требуется обеспечивать отказоустойчивость(при сбое все данные сохраняются).
    Подсчет должен быть абсолютно точным за указанный период, подсчет может быть длительной операцией.

Выбранная реализация:
    При сделанных допущениях был выбран вариант реализации записи о каждом событии в БД. Для подсчета произошедших событий за период используются
    SQL запросы. Данный вариант выбран с учетом отказоустойчивости и отсутствием жестких ограничений на выполнение подсчета


Сборка и запуск тестового класса:
    Необходимо наличие maven
    В корневой папке проекта выполнить mvn clean package
    Созданы unit тесты для различных реализаций

    Для эмуляции нагрузочного теста сделан отдельный класс LongCounterTest, регистрирующий события сразу в 2х подсчитывающих классах
    Перейти в папку target
    Произвести запуск с помощью команды java -cp event_counter-1.0-SNAPSHOT.jar aparkhomenko.eventcounter.LongCounterTest
    Образец вывода тестовой программы:
Start test - 2016-12-26 03:07:49
Fired events - 20000
End test - 2016-12-26 03:09:29
Last minute events: array - 11994; DB - 12068
Last hour events: array - 20000; DB - 20002
Last day events: array - 20000; DB - 20003

Process finished with exit code 0

Значения за последнюю минуту между вариантами отличаются из-за разницы в подсчитываемом диапазоне.
Разница в часовых и суточных показателях обусловлена наличием в базе данных сохраненных сведений

