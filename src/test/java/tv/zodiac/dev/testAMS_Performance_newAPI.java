package tv.zodiac.dev;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * We are localhost (Charter Headend). Full chain of requests: localhost -> AMS -> STB -> AMS -> localhost
 */
class testAMS_Performance_newAPI extends API_common {
    private NewAPI_AMS AMS = new NewAPI_AMS();
    private int timeout = 20000;
    private int sleep_after_iteration = 1000;

    private ArrayList add_list,
            modify_list = new ArrayList(),
            delete_list = new ArrayList(),
            purge_list = new ArrayList();
    private int a_avg = 0, a_med = 0, a_min = 0, a_min_iteration = 0, a_max = 0, a_max_iteration = 0, a_total_i = 0,
            m_avg = 0, m_med = 0, m_min = 0, m_min_iteration = 0, m_max = 0, m_max_iteration = 0, m_total_i = 0,
            d_avg = 0, d_med = 0, d_min = 0, d_min_iteration = 0, d_max = 0, d_max_iteration = 0, d_total_i = 0,
            p_avg = 0, p_med = 0, p_min = 0, p_min_iteration = 0, p_max = 0, p_max_iteration = 0, p_total_i = 0;

    private ArrayList a_current = new ArrayList(),
            m_current = new ArrayList(),
            d_current = new ArrayList(),
            p_current = new ArrayList();

    private boolean use_random = false;

    private void before(String ams_ip, String mac, String boxname, int count_reminders, int count_iterations, int reminderChannelNumber) throws IOException {
        check_csv(ams_ip, mac, boxname, count_reminders, count_iterations, reminderChannelNumber);

        print_start_header(ams_ip, mac, boxname, count_reminders, reminderChannelNumber);

        if(reminderChannelNumber == -1){
            use_random = true;
        }
    }

    private void print_start_header(String ams_ip, String mac, String boxname, int count_reminders, int reminderChannelNumber) throws IOException {
        logger(INFO_LEVEL, "[INF] " + new Date() + ": New start for mac=" + mac + "(" + boxname + ") to ams=" + ams_ip + ", "
                + "count_reminders=" + count_reminders + ", "
                + "reminderProgramStart=" + reminderProgramStart + ", "
                + "reminderChannelNumber=" + reminderChannelNumber + ", "
                + "reminderProgramId=" + reminderProgramId + ", "
                + "reminderOffset=" + reminderOffset + ", "
                + "reminderScheduleId=, "
                + "reminderId=");
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/reminders.csv", numLinesToSkip = 1)
    void test0_Add(String ams_ip, String mac, String boxname, int count_reminders, int count_iterations, int reminderChannelNumber) throws InterruptedException, IOException {
        before(ams_ip, mac, boxname, count_reminders, count_iterations, reminderChannelNumber);

        for (int i = 1; i <= count_iterations; i++) {
            if(use_random){                reminderChannelNumber = reminderChannelNumber(1000);            }
            print_iteration_header(ams_ip, mac, count_reminders, i, count_iterations, reminderChannelNumber);

            long reminderScheduleId = reminderScheduleId(Generation.random);
            long reminderId = reminderId(Generation.random);

            add_list = AMS.request_perf(ams_ip, mac, Operation.add, i, count_reminders, reminderProgramStart, reminderChannelNumber, reminderProgramId, reminderOffset, reminderScheduleId, reminderId);
            print_preliminary_results(add_list);
            if (add_list.get(0).equals(expected200) && add_list.get(1).equals("")) {
                a_current.add(add_list.get(2));
                a_avg = (int) add_list.get(3);
                a_med = (int) add_list.get(4);
                a_min = (int) add_list.get(5);
                a_min_iteration = (int) add_list.get(6);
                a_max = (int) add_list.get(7);
                a_max_iteration = (int) add_list.get(8);
                a_total_i = (int) add_list.get(9);
            }
            reminderScheduleId_list.clear();
            reminderId_list.clear();
            logger(DEBUG_LEVEL, "[DBG] reminderX_list-s are CLEARED !!!");
            add_list.clear();
            Thread.sleep(sleep_after_iteration);
        }

        prepare_total_results(mac, boxname, count_reminders, count_iterations,
                a_avg, a_med, a_min, a_min_iteration, a_max, a_max_iteration, a_total_i, a_current,
                m_avg, m_med, m_min, m_min_iteration, m_max, m_max_iteration, m_total_i, m_current,
                d_avg, d_med, d_min, d_min_iteration, d_max, d_max_iteration, d_total_i, d_current,
                p_avg, p_med, p_min, p_min_iteration, p_max, p_max_iteration, p_total_i, p_current);
        assertNotEquals(0, a_avg, "a_avg");
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/reminders.csv", numLinesToSkip = 1)
    void test1_Add_Purge(String ams_ip, String mac, String boxname, int count_reminders, int count_iterations, int reminderChannelNumber) throws InterruptedException, IOException {
        before(ams_ip, mac, boxname, count_reminders, count_iterations, reminderChannelNumber);

        for (int i = 1; i <= count_iterations; i++) {
            if(use_random){                reminderChannelNumber = reminderChannelNumber(1000);            }
            print_iteration_header(ams_ip, mac, count_reminders, i, count_iterations, reminderChannelNumber);

            long reminderScheduleId = reminderScheduleId(Generation.random);
            long reminderId = reminderId(Generation.random);

            add_list = AMS.request_perf(ams_ip, mac, Operation.add, i, count_reminders, reminderProgramStart, reminderChannelNumber, reminderProgramId, reminderOffset, reminderScheduleId, reminderId);
            print_preliminary_results(add_list);
            if (add_list.get(0).equals(expected200) && add_list.get(1).equals("")) {
                a_current.add(add_list.get(2));
                a_avg = (int) add_list.get(3);
                a_med = (int) add_list.get(4);
                a_min = (int) add_list.get(5);
                a_min_iteration = (int) add_list.get(6);
                a_max = (int) add_list.get(7);
                a_max_iteration = (int) add_list.get(8);
                a_total_i = (int) add_list.get(9);

                purge_list = AMS.request_perf(ams_ip, mac, Operation.purge, i);
                print_preliminary_results(purge_list);
                if(purge_list.get(1).equals("")) {
                    p_avg = (int) purge_list.get(3);
                    p_med = (int) purge_list.get(4);
                    p_min = (int) purge_list.get(5);
                    p_min_iteration = (int) purge_list.get(6);
                    p_max = (int) purge_list.get(7);
                    p_max_iteration = (int) purge_list.get(8);
                    p_total_i = (int) purge_list.get(9);
                }
            }
            reminderScheduleId_list.clear();
            reminderId_list.clear();
            logger(DEBUG_LEVEL, "[DBG] reminderX_list-s are CLEARED !!!");
            add_list.clear();
            purge_list.clear();
            Thread.sleep(sleep_after_iteration);
        }

        prepare_total_results(mac, boxname, count_reminders, count_iterations,
                a_avg, a_med, a_min, a_min_iteration, a_max, a_max_iteration, a_total_i, a_current,
                m_avg, m_med, m_min, m_min_iteration, m_max, m_max_iteration, m_total_i, m_current,
                d_avg, d_med, d_min, d_min_iteration, d_max, d_max_iteration, d_total_i, d_current,
                p_avg, p_med, p_min, p_min_iteration, p_max, p_max_iteration, p_total_i, p_current);
        assertNotEquals(0, a_avg, "a_avg");
        assertNotEquals(0, p_avg, "p_avg");
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/reminders.csv", numLinesToSkip = 1)
    void test2_Add_Delete_Purge(String ams_ip, String mac, String boxname, int count_reminders, int count_iterations, int reminderChannelNumber) throws IOException, InterruptedException {
        before(ams_ip, mac, boxname, count_reminders, count_iterations, reminderChannelNumber);

        for (int i = 1; i <= count_iterations; i++) {
            if(use_random){                reminderChannelNumber = reminderChannelNumber(1000);            }
            print_iteration_header(ams_ip, mac, count_reminders, i, count_iterations, reminderChannelNumber);

            long reminderScheduleId = reminderScheduleId(Generation.random);
            long reminderId = reminderId(Generation.random);

            add_list = AMS.request_perf(ams_ip, mac, Operation.add, i, count_reminders, reminderProgramStart, reminderChannelNumber, reminderProgramId, reminderOffset, reminderScheduleId, reminderId);
            print_preliminary_results(add_list);
            if (add_list.get(0).equals(expected200) && add_list.get(1).equals("")) {
                a_current.add(add_list.get(2));
                a_avg = (int) add_list.get(3);
                a_med = (int) add_list.get(4);
                a_min = (int) add_list.get(5);
                a_min_iteration = (int) add_list.get(6);
                a_max = (int) add_list.get(7);
                a_max_iteration = (int) add_list.get(8);
                a_total_i = (int) add_list.get(9);

                delete_list = AMS.request_perf(ams_ip, mac, Operation.delete, i, count_reminders, reminderScheduleId, reminderId);
                print_preliminary_results(delete_list);
                if (delete_list.get(1).equals("")) {
                    d_current.add(delete_list.get(2));
                    d_avg = (int) delete_list.get(3);
                    d_med = (int) delete_list.get(4);
                    d_min = (int) delete_list.get(5);
                    d_min_iteration = (int) delete_list.get(6);
                    d_max = (int) delete_list.get(7);
                    d_max_iteration = (int) delete_list.get(8);
                    d_total_i = (int) delete_list.get(9);
                }

                purge_list = AMS.request_perf(ams_ip, mac, Operation.purge, i);
                print_preliminary_results(purge_list);
                if (purge_list.get(1).equals("")) {
                    p_avg = (int) purge_list.get(3);
                    p_med = (int) purge_list.get(4);
                    p_min = (int) purge_list.get(5);
                    p_min_iteration = (int) purge_list.get(6);
                    p_max = (int) purge_list.get(7);
                    p_max_iteration = (int) purge_list.get(8);
                    p_total_i = (int) purge_list.get(9);
                }
            }

            reminderScheduleId_list.clear();
            reminderId_list.clear();
            logger(DEBUG_LEVEL, "[DBG] reminderX_list-s are CLEARED !!!");
            add_list.clear();
            delete_list.clear();
            purge_list.clear();
            Thread.sleep(sleep_after_iteration);
        }

        prepare_total_results(mac, boxname, count_reminders, count_iterations,
                a_avg, a_med, a_min, a_min_iteration, a_max, a_max_iteration, a_total_i, a_current,
                m_avg, m_med, m_min, m_min_iteration, m_max, m_max_iteration, m_total_i, m_current,
                d_avg, d_med, d_min, d_min_iteration, d_max, d_max_iteration, d_total_i, d_current,
                p_avg, p_med, p_min, p_min_iteration, p_max, p_max_iteration, p_total_i, p_current);
        assertNotEquals(0, a_avg, "a_avg");
        assertNotEquals(0, d_avg, "d_avg");
        assertNotEquals(0, p_avg, "p_avg");
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/reminders.csv", numLinesToSkip = 1)
    void test3_Add_Modify_Delete_Purge(String ams_ip, String mac, String boxname, int count_reminders, int count_iterations, int reminderChannelNumber) throws IOException, InterruptedException {
        before(ams_ip, mac, boxname, count_reminders, count_iterations, reminderChannelNumber);

        for (int i = 1; i <= count_iterations; i++) {
            if(use_random){                reminderChannelNumber = reminderChannelNumber(1000);            }
            print_iteration_header(ams_ip, mac, count_reminders, i, count_iterations, reminderChannelNumber);

            long reminderScheduleId = reminderScheduleId(Generation.random);
            long reminderId = reminderId(Generation.random);
            //long reminderScheduleId = reminderScheduleId(Generation.increment);
            //long reminderId = reminderId(Generation.increment);

            add_list = AMS.request_perf(ams_ip, mac, Operation.add, i, count_reminders, reminderProgramStart, reminderChannelNumber, reminderProgramId, reminderOffset, reminderScheduleId, reminderId);
            print_preliminary_results(add_list);
            if (add_list.get(0).equals(expected200) && add_list.get(1).equals("")) {
                a_current.add(add_list.get(2));
                a_avg = (int) add_list.get(3);
                a_med = (int) add_list.get(4);
                a_min = (int) add_list.get(5);
                a_min_iteration = (int) add_list.get(6);
                a_max = (int) add_list.get(7);
                a_max_iteration = (int) add_list.get(8);
                a_total_i = (int) add_list.get(9);

                modify_list = AMS.request_perf(ams_ip, mac, Operation.modify, i, count_reminders, reminderProgramStart, reminderChannelNumber, reminderProgramId, reminderOffset, reminderScheduleId, reminderId);
                print_preliminary_results(modify_list);
                if (modify_list.get(1).equals("")) {
                    m_current.add(modify_list.get(2));
                    m_avg = (int) modify_list.get(3);
                    m_med = (int) modify_list.get(4);
                    m_min = (int) modify_list.get(5);
                    m_min_iteration = (int) modify_list.get(6);
                    m_max = (int) modify_list.get(7);
                    m_max_iteration = (int) modify_list.get(8);
                    m_total_i = (int) modify_list.get(9);
                }

                delete_list = AMS.request_perf(ams_ip, mac, Operation.delete, i, count_reminders, reminderScheduleId, reminderId);
                print_preliminary_results(delete_list);
                if (delete_list.get(1).equals("")) {
                    d_current.add(delete_list.get(2));
                    d_avg = (int) delete_list.get(3);
                    d_med = (int) delete_list.get(4);
                    d_min = (int) delete_list.get(5);
                    d_min_iteration = (int) delete_list.get(6);
                    d_max = (int) delete_list.get(7);
                    d_max_iteration = (int) delete_list.get(8);
                    d_total_i = (int) delete_list.get(9);
                }

                purge_list = AMS.request_perf(ams_ip, mac, Operation.purge, i);
                print_preliminary_results(purge_list);
                if (purge_list.get(1).equals("")) {
                    p_avg = (int) purge_list.get(3);
                    p_med = (int) purge_list.get(4);
                    p_min = (int) purge_list.get(5);
                    p_min_iteration = (int) purge_list.get(6);
                    p_max = (int) purge_list.get(7);
                    p_max_iteration = (int) purge_list.get(8);
                    p_total_i = (int) purge_list.get(9);
                }
            }
            reminderScheduleId_list.clear();
            reminderId_list.clear();
            logger(DEBUG_LEVEL, "[DBG] reminderX_list-s are CLEARED !!!");
            add_list.clear();
            modify_list.clear();
            delete_list.clear();
            purge_list.clear();
            Thread.sleep(sleep_after_iteration);
        }

        prepare_total_results(mac, boxname, count_reminders, count_iterations,
                a_avg, a_med, a_min, a_min_iteration, a_max, a_max_iteration, a_total_i, a_current,
                m_avg, m_med, m_min, m_min_iteration, m_max, m_max_iteration, m_total_i, m_current,
                d_avg, d_med, d_min, d_min_iteration, d_max, d_max_iteration, d_total_i, d_current,
                p_avg, p_med, p_min, p_min_iteration, p_max, p_max_iteration, p_total_i, p_current);
        assertNotEquals(0, a_avg, "a_avg");
        assertNotEquals(0, m_avg, "m_avg");
        assertNotEquals(0, d_avg, "d_avg");
        assertNotEquals(0, p_avg, "p_avg");
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/reminders.csv", numLinesToSkip = 1)
    void test9_Purge(String ams_ip, String mac, String boxname, int count_reminders, int count_iterations, int reminderChannelNumber) throws IOException, InterruptedException {
        before(ams_ip, mac, boxname, count_reminders, count_iterations, reminderChannelNumber);

        for (int i = 1; i <= count_iterations; i++) {
            print_iteration_header(ams_ip, mac, count_reminders, i, count_iterations, reminderChannelNumber);

            purge_list = AMS.request_perf(ams_ip, mac, Operation.purge, i);
            print_preliminary_results(purge_list);
            if (purge_list.get(1).equals("")) {
                p_avg = (int) purge_list.get(3);
                p_med = (int) purge_list.get(4);
                p_min = (int) purge_list.get(5);
                p_min_iteration = (int) purge_list.get(6);
                p_max = (int) purge_list.get(7);
                p_max_iteration = (int) purge_list.get(8);
                p_total_i = (int) purge_list.get(9);
            }

            purge_list.clear();
            Thread.sleep(sleep_after_iteration);
        }

        prepare_total_results(mac, boxname, count_reminders, count_iterations,
                -1, a_med, a_min, a_min_iteration, a_max, a_max_iteration, a_total_i, a_current,
                m_avg, m_med, m_min, m_min_iteration, m_max, m_max_iteration, m_total_i, m_current,
                d_avg, d_med, d_min, d_min_iteration, d_max, d_max_iteration, d_total_i, d_current,
                p_avg, p_med, p_min, p_min_iteration, p_max, p_max_iteration, p_total_i, p_current);
        assertNotEquals(0, p_avg, "p_avg");
    }

}