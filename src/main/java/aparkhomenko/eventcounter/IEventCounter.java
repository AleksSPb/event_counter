package aparkhomenko.eventcounter;

/**
 * Created by aleks on 24.12.2016.
 */
public interface IEventCounter {

    void registerEvent();

    long getLastMinuteCounter();

    long getLastHourCounter();

    long getLast24HoursCounter();
}
