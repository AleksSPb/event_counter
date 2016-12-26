import aparkhomenko.eventcounter.EventCounterImpH2DB;
import aparkhomenko.eventcounter.IEventCounter;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertEquals;

/**
 * Created by aleks on 26.12.2016.
 */
public class DBCounterTest {
    private static IEventCounter counter = EventCounterImpH2DB.getInstance();
    private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_CONNECTION = "jdbc:h2:~/test";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";
    private static Connection connection = null;
    private static AtomicLong eventId = new AtomicLong();

    @BeforeClass
    public static void InitializeTest() {
        connection = getDBConnection();
        try {
            connection.setAutoCommit(true);
            Statement stmt = connection.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS EVENTS(id int primary key, time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            stmt.execute("CREATE INDEX IF NOT EXISTS IDXTIME ON EVENTS(time)");

            ResultSet rs = stmt.executeQuery("select MAX(ID) from EVENTS");
            if (rs.next()) {
                Long value = rs.getLong(1);
                if (value != null)
                    eventId.set(value);
                else eventId.set(1);
            }
            stmt.close();
        } catch (SQLException sqlEx) {
            throw new RuntimeException("SQL error: " + sqlEx);
        }


    }

    @Test
    public void testHalfMinute() {
        long curMinuteEvents = counter.getLastMinuteCounter();
        long curHourEvents = counter.getLastHourCounter();
        long curDayEvents = counter.getLast24HoursCounter();

        long now = System.currentTimeMillis();
        long halfMinuteBefore = now - TimeUnit.SECONDS.toMillis(30);
        java.sql.Timestamp halfMinuteBeforeTimestamp = new Timestamp(halfMinuteBefore);
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO EVENTS (ID, time) VALUES(?,?)");
            ps.setLong(1, eventId.incrementAndGet());
            ps.setTimestamp(2, halfMinuteBeforeTimestamp);
            int result = ps.executeUpdate();
            ps.close();
        } catch (SQLException sqlEx) {
            System.out.println("Can't Minute Counter value");
        }
        assertEquals(curMinuteEvents + 1, counter.getLastMinuteCounter());
        assertEquals(curHourEvents + 1, counter.getLastHourCounter());
        assertEquals(curDayEvents + 1, counter.getLast24HoursCounter());
    }


    @Test
    public void testHalfHour() {
        long curMinuteEvents = counter.getLastMinuteCounter();
        long curHourEvents = counter.getLastHourCounter();
        long curDayEvents = counter.getLast24HoursCounter();

        long now = System.currentTimeMillis();
        long halfHourBefore = now - TimeUnit.MINUTES.toMillis(30);
        java.sql.Timestamp halfHourBeforeTimestamp = new Timestamp(halfHourBefore);
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO EVENTS (ID, time) VALUES(?,?)");
            ps.setLong(1, eventId.incrementAndGet());
            ps.setTimestamp(2, halfHourBeforeTimestamp);
            int result = ps.executeUpdate();
            ps.close();
        } catch (SQLException sqlEx) {
            System.out.println("Can't Minute Counter value");
        }
        assertEquals(curMinuteEvents, counter.getLastMinuteCounter());
        assertEquals(curHourEvents + 1, counter.getLastHourCounter());
        assertEquals(curDayEvents + 1, counter.getLast24HoursCounter());
    }

    @Test
    public void testHalfDay() {
        long curMinuteEvents = counter.getLastMinuteCounter();
        long curHourEvents = counter.getLastHourCounter();
        long curDayEvents = counter.getLast24HoursCounter();

        long now = System.currentTimeMillis();
        long halfDayBefore = now - TimeUnit.HOURS.toMillis(12);
        java.sql.Timestamp halfDayBeforeTimestamp = new Timestamp(halfDayBefore);
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO EVENTS (ID, time) VALUES(?,?)");
            ps.setLong(1, eventId.incrementAndGet());
            ps.setTimestamp(2, halfDayBeforeTimestamp);
            int result = ps.executeUpdate();
            ps.close();
        } catch (SQLException sqlEx) {
            System.out.println("Can't Minute Counter value");
        }
        assertEquals(curMinuteEvents, counter.getLastMinuteCounter());
        assertEquals(curHourEvents, counter.getLastHourCounter());
        assertEquals(curDayEvents + 1, counter.getLast24HoursCounter());
    }

    @Test
    public void test2Days() {
        long curMinuteEvents = counter.getLastMinuteCounter();
        long curHourEvents = counter.getLastHourCounter();
        long curDayEvents = counter.getLast24HoursCounter();

        long now = System.currentTimeMillis();
        long twoDaysBefore = now - TimeUnit.HOURS.toMillis(48);
        java.sql.Timestamp twoDaysBeforeTimestamp = new Timestamp(twoDaysBefore);
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO EVENTS (ID, time) VALUES(?,?)");
            ps.setLong(1, eventId.incrementAndGet());
            ps.setTimestamp(2, twoDaysBeforeTimestamp);
            int result = ps.executeUpdate();
            ps.close();
        } catch (SQLException sqlEx) {
            System.out.println("Can't Minute Counter value");
        }
        assertEquals(curMinuteEvents, counter.getLastMinuteCounter());
        assertEquals(curHourEvents, counter.getLastHourCounter());
        assertEquals(curDayEvents, counter.getLast24HoursCounter());
    }

    private static Connection getDBConnection() {
        Connection dbConnection = null;
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER,
                    DB_PASSWORD);
            return dbConnection;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return dbConnection;
    }
}
