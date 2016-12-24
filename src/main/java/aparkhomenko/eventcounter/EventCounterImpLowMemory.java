package aparkhomenko.eventcounter;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * Created by aleks on 24.12.2016.
 * Данное решение не является точным подсчетом, т.к. расчет выполняется по секундно
 * Значение текущей секунды обнуляется и производится его инкремент при получении события
 * Т.е. получение статистики за минуту будет включать события за 59 предыдущих секунд + произошедшие до момента запроса статистики
 * Данный вариант выбран в связи с его устойчивостью к большим нагрузкам
 */
public class EventCounterImpLowMemory implements IEventCounter {
    private static final int SECONDS_IN_MINUTE = 60;
    private static final int SECONDS_IN_HOUR = 60 * SECONDS_IN_MINUTE;
    private static final int SECONDS_IN_DAY = 24 * SECONDS_IN_HOUR;
    private final AtomicIntegerArray circularCounterArray = new AtomicIntegerArray(SECONDS_IN_DAY);
    private volatile int currentSecond;
    private static volatile boolean canIncrement = true;

    private static volatile EventCounterImpLowMemory INSTANCE;

    private EventCounterImpLowMemory() {
        currentSecond = 0;
        SecondUpdater secondUpdater = new SecondUpdater(this);
        secondUpdater.start();
    }

    public static IEventCounter getInstance() {
        if (INSTANCE == null) {
            synchronized (EventCounterImpLowMemory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new EventCounterImpLowMemory();

                }
            }
        }
        return INSTANCE;
    }

    public void registerEvent() {

        circularCounterArray.incrementAndGet(currentSecond);
    }

    synchronized void incrementTime() {
        int newCurrent = (currentSecond + 1) % SECONDS_IN_DAY;
        circularCounterArray.set(newCurrent, 0);
        currentSecond = newCurrent;
    }

    public long getLastMinuteCounter() {
        long minuteCounter = 0;
        int currentSecond = this.currentSecond;
        for (int i = currentSecond + SECONDS_IN_DAY - SECONDS_IN_MINUTE + 1; i <= currentSecond + SECONDS_IN_DAY; i++) {
            minuteCounter += circularCounterArray.get(i % SECONDS_IN_DAY);
        }
        return minuteCounter;
    }

    public long getLastHourCounter() {
        long hourCounter = 0;
        int currentSecond = this.currentSecond;
        for (int i = currentSecond + SECONDS_IN_DAY - SECONDS_IN_HOUR + 1; i <= currentSecond + SECONDS_IN_DAY; i++) {
            hourCounter += circularCounterArray.get(i % SECONDS_IN_DAY);
        }
        return hourCounter;
    }

    public long getLast24HoursCounter() {
        int dayCounter = 0;
        for (int i = 0; i < SECONDS_IN_DAY; i++) {
            dayCounter += circularCounterArray.get(i);
        }
        return dayCounter;
    }
}

class SecondUpdater extends TimerTask {

    private final EventCounterImpLowMemory eventCounter;
    private final Timer timer = new Timer(true);
    private static final int DELAY = 1000;

    SecondUpdater(EventCounterImpLowMemory eventCounter) {
        this.eventCounter = eventCounter;
    }

    void start() {
        timer.schedule(this, 0, DELAY);
    }

    @Override
    public void run() {
        eventCounter.incrementTime();
    }
}


