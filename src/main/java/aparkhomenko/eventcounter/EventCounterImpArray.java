package aparkhomenko.eventcounter;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * Created by aleks on 24.12.2016.
 * Данное решение не является точным подсчетом, т.к. расчет выполняется по секундно
 * Значение текущей секунды обнуляется и производится его инкремент при получении события
 * Т.е. получение статистики за минуту будет включать события за 59 предыдущих секунд + произошедшие до момента запроса статистики события текущей секунды
 * Данный вариант выбран в связи с его устойчивостью к большим нагрузкам.
 */
public class EventCounterImpArray implements IEventCounter {
    private static final int SECONDS_IN_MINUTE = 60;
    private static final int SECONDS_IN_HOUR = 60 * SECONDS_IN_MINUTE;
    private static final int SECONDS_IN_DAY = 24 * SECONDS_IN_HOUR;
    private final AtomicIntegerArray circularCounterArray = new AtomicIntegerArray(SECONDS_IN_DAY);
    private volatile int currentSecond;
    private static volatile EventCounterImpArray INSTANCE;

    private EventCounterImpArray() {
        currentSecond = 0;
        SecondUpdater secondUpdater = new SecondUpdater(this);
        secondUpdater.start();
    }

    /**
     *
     * @return Single instance of class
     */
    public static IEventCounter getInstance() {
        if (INSTANCE == null) {
            synchronized (EventCounterImpArray.class) {
                if (INSTANCE == null) {
                    INSTANCE = new EventCounterImpArray();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Increment counter for the current second in array
     */
    public void registerEvent() {
        circularCounterArray.incrementAndGet(currentSecond);
    }

    /**
     * Change current second and set it value to 0
     */
    synchronized void incrementTime() {
        int newCurrent = (currentSecond + 1) % SECONDS_IN_DAY;
        circularCounterArray.set(newCurrent, 0);
        currentSecond = newCurrent;
    }

    /**
     * Calculate sum of registered events for current and last 59 seconds
     * @return Return calculated sum
     */
    public long getLastMinuteCounter() {
        long minuteCounter = 0;
        int currentSecond = this.currentSecond;
        for (int i = currentSecond + SECONDS_IN_DAY - SECONDS_IN_MINUTE + 1; i <= currentSecond + SECONDS_IN_DAY; i++) {
            minuteCounter += circularCounterArray.get(i % SECONDS_IN_DAY);
        }
        return minuteCounter;
    }
    /**
     * Calculate sum of registered events for current and last 3599 seconds
     * @return Return calculated sum
     */
    public long getLastHourCounter() {
        long hourCounter = 0;
        int currentSecond = this.currentSecond;
        for (int i = currentSecond + SECONDS_IN_DAY - SECONDS_IN_HOUR + 1; i <= currentSecond + SECONDS_IN_DAY; i++) {
            hourCounter += circularCounterArray.get(i % SECONDS_IN_DAY);
        }
        return hourCounter;
    }
    /**
     * Calculate sum of registered events for current and last 86399 seconds
     * @return Return calculated sum
     */
    public long getLast24HoursCounter() {
        int dayCounter = 0;
        for (int i = 0; i < SECONDS_IN_DAY; i++) {
            dayCounter += circularCounterArray.get(i);
        }
        return dayCounter;
    }

    /**
     * Internal support class for every second increment time in main class
     */
    class SecondUpdater extends TimerTask {

        private final EventCounterImpArray eventCounter;
        private final Timer timer = new Timer(true);
        private static final int DELAY = 1000;// 1 second

        SecondUpdater(EventCounterImpArray eventCounter) {
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
}




