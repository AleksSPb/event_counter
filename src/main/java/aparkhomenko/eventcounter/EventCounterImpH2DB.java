package aparkhomenko.eventcounter;

import java.sql.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Created by aleks on 25.12.2016.
 * Данное решение является точным подсчетом. Информация о событиях сохраняется в БД.
 * Рассчет произошедших событий производится с помощью SQL запроса
 */

public class EventCounterImpH2DB implements IEventCounter {

    private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_CONNECTION = "jdbc:h2:~/test";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";
    private static Connection connection = null;
    private static AtomicLong eventId = new AtomicLong();
    private static volatile EventCounterImpH2DB INSTANCE;

    private EventCounterImpH2DB() {
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

    /**
     * @return Single instance of class
     */
    public static IEventCounter getInstance() {
        if (INSTANCE == null) {
            synchronized (EventCounterImpH2DB.class) {
                if (INSTANCE == null) {
                    INSTANCE = new EventCounterImpH2DB();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Increment counter for the current second in array
     */
    public void registerEvent() {
        try {
            Statement statement = connection.createStatement();
            statement.execute("INSERT INTO EVENTS (id) VALUES(" + eventId.incrementAndGet() + ")");
            statement.close();
        } catch (SQLException sqlEx) {
            System.out.println("Can't insert into events");
        }
    }


    /**
     * Calculate sum of registered events for current and last 59 seconds
     *
     * @return Return calculated sum
     */
    public long getLastMinuteCounter() {
        long now = System.currentTimeMillis();
        long oneMinuteBefore = now - TimeUnit.MINUTES.toMillis(1);
        java.sql.Timestamp nowTimestamp = new java.sql.Timestamp(now);
        java.sql.Timestamp minuteBeforeTimestamp = new Timestamp(oneMinuteBefore);
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT COUNT(Id) FROM EVENTS WHERE time BETWEEN ? AND ?");
            ps.setTimestamp(1, minuteBeforeTimestamp);
            ps.setTimestamp(2, nowTimestamp);
            ResultSet rs = ps.executeQuery();
            long counter = 0;
            if (rs.next()) {
                counter = rs.getLong(1);
            }
            ps.close();
            return counter;
        } catch (SQLException sqlEx) {
            System.out.println("Can't Minute Counter value");
        }
        return 0;
    }

    /**
     * Calculate sum of registered events for current and last 3599 seconds
     *
     * @return Return calculated sum
     */
    public long getLastHourCounter() {
        long now = System.currentTimeMillis();
        long oneHourBefore = now - TimeUnit.HOURS.toMillis(1);
        java.sql.Timestamp nowTimestamp = new java.sql.Timestamp(now);
        java.sql.Timestamp hourBeforeTimestamp = new Timestamp(oneHourBefore);
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT COUNT(Id) FROM EVENTS WHERE time BETWEEN ? AND ?");
            ps.setTimestamp(1, hourBeforeTimestamp);
            ps.setTimestamp(2, nowTimestamp);
            ResultSet rs = ps.executeQuery();
            long counter = 0;
            if (rs.next()) {
                counter = rs.getLong(1);
            }
            ps.close();
            return counter;
        } catch (SQLException sqlEx) {
            System.out.println("Can't Hour Counter value");
        }
        return 0;
    }

    /**
     * Calculate sum of registered events for current and last 86399 seconds
     *
     * @return Return calculated sum
     */
    public long getLast24HoursCounter() {
        long now = System.currentTimeMillis();
        long oneDayrBefore = now - TimeUnit.DAYS.toMillis(1);
        java.sql.Timestamp nowTimestamp = new java.sql.Timestamp(now);
        java.sql.Timestamp dayBeforeTimestamp = new Timestamp(oneDayrBefore);
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT COUNT(Id) FROM EVENTS WHERE time BETWEEN ? AND ?");
            ps.setTimestamp(1, dayBeforeTimestamp);
            ps.setTimestamp(2, nowTimestamp);
            ResultSet rs = ps.executeQuery();
            long counter = 0;
            if (rs.next()) {
                counter = rs.getLong(1);
            }
            ps.close();
            return counter;
        } catch (SQLException sqlEx) {
            System.out.println("Can't Day Counter value");
        }
        return 0;
    }

    /**
     * Internal support class for every second increment time in main class
     */

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




