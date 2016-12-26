/**
 * Created by aleks on 24.12.2016.
 */
import aparkhomenko.eventcounter.EventCounterImpArray;
import aparkhomenko.eventcounter.IEventCounter;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArrayCounterTest {
    private static IEventCounter counter = EventCounterImpArray.getInstance();

    @Test
    public void testCounterMinute() {
        long curRegisteredEvents = counter.getLastMinuteCounter();
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