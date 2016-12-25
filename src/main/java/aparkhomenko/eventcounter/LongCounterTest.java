package aparkhomenko.eventcounter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by aleks on 24.12.2016.
 * Класс содержит в методе main тест для класса EventCounterImpLowMemory
 * Тест вынесен из тестовых классов из-за длительного выполнения (более 1 минуты)
 * Цель данного теста, чтобы проверить общее значение произошедших событий и значение за последнюю минуту отличалось от общего
 */
public class LongCounterTest {
    public static void main(String args[]) {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ExecutorService executor = Executors.newFixedThreadPool(10);
        IEventCounter realTimeCounter = EventCounterImpLowMemory.getInstance();
        final Random random = new Random();
        final int TOTAL_EVENTS = 50000;
        CountDownLatch countDownLatch = new CountDownLatch(TOTAL_EVENTS);
        System.out.println("Start test - " + sdf.format(new Date()));
        for (int i = 0; i < TOTAL_EVENTS; i++) {
            executor.execute(() -> {
                        realTimeCounter.registerEvent();
                        try {
                            Thread.sleep(random.nextInt(50));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        countDownLatch.countDown();
                    }
            );
        }
        try {
            countDownLatch.await();
        } catch (Exception e) {

        }
        System.out.println("Fired events - " + TOTAL_EVENTS);
        System.out.println("End test - " + sdf.format(new Date()));
        System.out.println("Last minute events - " + realTimeCounter.getLastMinuteCounter());
        System.out.println("Last hour events - " + realTimeCounter.getLastHourCounter());
        System.out.println("Last day events - " + realTimeCounter.getLast24HoursCounter());
        executor.shutdownNow();
    }
}

class EveryMinuteGetStatistic extends Thread {
    private final EventCounterImpLowMemory eventCounter;

    public EveryMinuteGetStatistic(EventCounterImpLowMemory counter) {
        this.eventCounter = counter;
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("\nLast minute counter value - " + eventCounter.getLastMinuteCounter());
            try {
                Thread.sleep(600);
            } catch (InterruptedException iEx) {
                System.out.println(iEx);
            }
        }
    }
}
