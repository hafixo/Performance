import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

class testAMS_Change_registration extends testAMS {

    //@Test(timeout = 20000)
    @Test
    void testChange_registration_via_charterapi_() throws IOException, InterruptedException {
        long start = System.currentTimeMillis();
        ArrayList actual = api.Change_registration(macaddress, charterapi_, ams_ip);
        long finish = System.currentTimeMillis();
        System.out.println("[DBG] " + (finish-start) + "ms test, return code: " + actual);
        assertEquals(expected200, actual.get(0));
        assertEquals(expected200t, actual.get(1));
    }

    //@Test(timeout = 20000)
    @Test
    void testChange_registration_via_charterapi_b() throws IOException, InterruptedException {
        long start = System.currentTimeMillis();
        ArrayList actual = api.Change_registration(macaddress, charterapi_b, ams_ip);
        long finish = System.currentTimeMillis();
        System.out.println("[DBG] " + (finish-start) + "ms test, return code: " + actual);
        assertEquals(expected200, actual.get(0));
        assertEquals(expected200t, actual.get(1));
        assertEquals("SUCCESS", actual.get(2));
    }

    //@Test(timeout = 20000)
    @Test
    void testChange_registration_via_charterapi_c() throws IOException, InterruptedException {
        long start = System.currentTimeMillis();
        ArrayList actual = api.Change_registration(macaddress, charterapi_c, ams_ip);
        long finish = System.currentTimeMillis();
        System.out.println("[DBG] " + (finish-start) + "ms test, return code: " + actual);
        assertEquals(expected200, actual.get(0));
        assertEquals(expected200t, actual.get(1));
        assertEquals("SUCCESS", actual.get(2));
    }

    //@Test(timeout = 20000)
    @Test
    void testChange_registration_via_charterapi_d() throws IOException, InterruptedException {
        long start = System.currentTimeMillis();
        ArrayList actual = api.Change_registration(macaddress, charterapi_d, ams_ip);
        long finish = System.currentTimeMillis();
        System.out.println("[DBG] " + (finish-start) + "ms test, return code: " + actual);
        assertEquals(expected200, actual.get(0));
        assertEquals(expected200t, actual.get(1));
        assertEquals("SUCCESS", actual.get(2));
    }

    @Test
    @Disabled
    void testChange_registration_to_invalid_ams127_0_0_1() throws IOException, InterruptedException {
        long start = System.currentTimeMillis();
        ArrayList actual = api.Change_registration(macaddress, charterapi_by_default, "127.0.0.1");
        long finish = System.currentTimeMillis();
        System.out.println("[DBG] " + (finish-start) + "ms test, return code: " + actual);
        assertEquals(expected200, actual.get(0));
        assertEquals(expected200t, actual.get(1));
        assertEquals("SUCCESS", actual.get(2));
    }

}