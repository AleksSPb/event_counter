/**
 * Created by aleks on 24.12.2016.
 */
import aparkhomenko.eventcounter.EventCounterImpLowMemory;
import aparkhomenko.eventcounter.IEventCounter;
import org.junit.Test;
import static org.junit.Assert.*;

public class SimpleCounterTest {
    private static IEventCounter counter = EventCounterImpLowMemory.getInstance();

    @Test
    public void testCounterSecond() {
        long curRegisteredEvents = counter.getLastHourCounter();
        for (int i = 0; i < 10; i++) {
            counter.registerEvent();
        }
        assertEquals(curRegisteredEvents + 10, counter.getLastMinuteCounter());


    }
    @Test
    public void testCounterHour() {
        long curRegisteredEvents = counter.getLastHourCounter();
        for (int i = 0; i < 10; i++) {
            counter.registerEvent();
        }
        assertEquals(curRegisteredEvents+10, counter.getLastHourCounter());
    }
    @Test
    public void testCounterDay() {
        long curRegisteredEvents = counter.getLast24HoursCounter();
        for (int i = 0; i < 10; i++) {
            counter.registerEvent();
        }
        assertEquals(curRegisteredEvents+10, counter.getLast24HoursCounter());
    }

}