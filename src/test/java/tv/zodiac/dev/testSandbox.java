package tv.zodiac.dev;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class testSandbox extends API {

    private NEWAPI_AMS AMS = new NEWAPI_AMS();

    @Test
    void testDate() {
        assertEquals("2018-05-19", reminderProgramStart());
        assertEquals("2018-05-19", get_date(1));
        assertEquals("2018-05-19 2018-05-20", get_date_several(2));
        assertEquals("2018-05-20", get_date(2));
    }

    @Test
    void test_get_date_time() throws InterruptedException {
        assertEquals("2018-05-19 00:00", get_date_time(1));
        //assertEquals("2018-05-20 00:00", get_date_time(1440));
        //assertEquals("2018-05-21 00:00", get_date_time(2880));
        //assertEquals("2018-05-21 00:00", get_date_time(2880));
    }

    @Test
    void testOperation_NewAPI_400_Bad_request() throws IOException {
        ArrayList actual = AMS.request(ams_ip, mac, Operation.blablabla, count_reminders, reminderProgramStart, reminderChannelNumber, reminderProgramId, reminderOffset, reminderScheduleId, reminderId);
        assertEquals(expected400, actual.get(0));
        assertEquals("", actual.get(1));
    }

    @Test
    void testOracleDB_Query() throws SQLException, ClassNotFoundException {
        ArrayList actual = AMS.QueryDB(ams_ip, mac);
        assertFalse(actual.isEmpty());

        assertEquals(Long.class, actual.get(0).getClass());
        assertNotEquals(0, actual.get(0));

        assertEquals(Long.class, actual.get(1).getClass());
        assertNotEquals(0, actual.get(1));

        assertEquals(Integer.class, actual.get(2).getClass());
        assertEquals(0, actual.get(2));

        assertEquals(String.class, actual.get(3).getClass());
        assertNotEquals(0, actual.get(3));

        assertEquals(String.class, actual.get(4).getClass());
        assertNotEquals(0, actual.get(4));
    }

    @Test
    void testOracleDB_Query_macaddress_empty() throws SQLException, ClassNotFoundException {
        ArrayList result = AMS.QueryDB(ams_ip, "");
        assertTrue(result.isEmpty());
    }

    @Test
    void testOracleDB_Query_macaddress_wrong() throws SQLException, ClassNotFoundException {
        ArrayList result = AMS.QueryDB(ams_ip, mac_wrong);
        assertTrue(result.isEmpty());
    }


}
