import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

class testAMS_Check_registration extends testAMS {

    //@Test(timeout = 20000)
    @Test
    void testCheck_registration_via_charterapi_() throws IOException, InterruptedException {
        long start = System.currentTimeMillis();
        ArrayList actual = api.Check_registration(macaddress, charterapi_);
        long finish = System.currentTimeMillis();
        System.out.println("[DBG] " + (finish-start) + "ms test, return code: " + actual);
        assertEquals(expected200, actual.get(0));
        assertEquals(expected200t, actual.get(1));
    }

    //@Test(timeout = 20000)
    @Test
    void testCheck_registration_via_charterapi_b() throws IOException, InterruptedException {
        long start = System.currentTimeMillis();
        ArrayList actual = api.Check_registration(macaddress, charterapi_b);
        long finish = System.currentTimeMillis();
        System.out.println("[DBG] " + (finish-start) + "ms test, return code: " + actual);
        assertEquals(expected200, actual.get(0));
        assertEquals(expected200t, actual.get(1));
    }

    //@Test(timeout = 20000)
    @Test
    void testCheck_registration_via_charterapi_c() throws IOException, InterruptedException {
        long start = System.currentTimeMillis();
        ArrayList actual = api.Check_registration(macaddress, charterapi_c);
        long finish = System.currentTimeMillis();
        System.out.println("[DBG] " + (finish-start) + "ms test, return code: " + actual);
        assertEquals(expected200, actual.get(0));
        assertEquals(expected200t, actual.get(1));
    }

    //@Test(timeout = 20000)
    @Test
    void testCheck_registration_via_charterapi_d() throws IOException, InterruptedException {
        long start = System.currentTimeMillis();
        ArrayList actual = api.Check_registration(macaddress, charterapi_d);
        long finish = System.currentTimeMillis();
        System.out.println("[DBG] " + (finish-start) + "ms test, return code: " + actual);
        assertEquals(expected200, actual.get(0));
        assertEquals(expected200t, actual.get(1));
    }

    @Test
    void testCheck_registration___No_amsIp_found_for_macAddress() throws IOException, InterruptedException {
        long start = System.currentTimeMillis();
        ArrayList actual = api.Check_registration("343834383438", charterapi_);
        long finish = System.currentTimeMillis();
        System.out.println("[DBG] " + (finish-start) + "ms test, return code: " + actual);
        assertEquals(expected500, actual.get(0));
        assertEquals(expected500t, actual.get(1));
        assertEquals("No amsIp found for macAddress", actual.get(2));
    }

    @Test
    void testCheck_registration_b_No_amsIp_found_for_macAddress() throws IOException, InterruptedException {
        long start = System.currentTimeMillis();
        ArrayList actual = api.Check_registration("343834383438", charterapi_b);
        long finish = System.currentTimeMillis();
        System.out.println("[DBG] " + (finish-start) + "ms test, return code: " + actual);
        assertEquals(expected500, actual.get(0));
        assertEquals(expected500t, actual.get(1));
        assertEquals("No amsIp found for macAddress", actual.get(2));
    }

    @Test
    void testCheck_registration_c_No_amsIp_found_for_macAddress() throws IOException, InterruptedException {
        long start = System.currentTimeMillis();
        ArrayList actual = api.Check_registration("343834383438", charterapi_c);
        long finish = System.currentTimeMillis();
        System.out.println("[DBG] " + (finish-start) + "ms test, return code: " + actual);
        assertEquals(expected500, actual.get(0));
        assertEquals(expected500t, actual.get(1));
        assertEquals("No amsIp found for macAddress", actual.get(2));
    }

    @Test
    void testCheck_registration_d_No_amsIp_found_for_macAddress() throws IOException, InterruptedException {
        long start = System.currentTimeMillis();
        ArrayList actual = api.Check_registration("343834383438", charterapi_d);
        long finish = System.currentTimeMillis();
        System.out.println("[DBG] " + (finish-start) + "ms test, return code: " + actual);
        assertEquals(expected500, actual.get(0));
        assertEquals(expected500t, actual.get(1));
        assertEquals("No amsIp found for macAddress", actual.get(2));
    }

}