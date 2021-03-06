package test.perf.reminders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import test.perf.common.CommonAPI;

import static java.lang.System.currentTimeMillis;

public class NewAPI extends CommonAPI {

    /** Purge method
     * @param mac - TBD
     * @param operation - TBD
     * @return list
     * @throws IOException - TBD
     */
    ArrayList request(String server, String mac, Operation operation) throws IOException {
        logger(INFO_LEVEL, "[INF] " + new Date() + ": " + operation.toString().toUpperCase() + ":");

        HttpPost request = new HttpPost(prepareUrl(server, operation, true));
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");
        request.setEntity(new StringEntity(generateJsonReminderPurge(mac)));
        logger(DEBUG_LEVEL, "[DBG] request string: " + request);

        long start = currentTimeMillis();
        HttpResponse response = HttpClients.createDefault().execute(request);
        long finish = System.currentTimeMillis();
        int current = (int)(finish-start);
        logger(DEBUG_LEVEL, "[DBG] " + current + "ms request");

        ArrayList list = new ArrayList();
        //list.ADD(0, response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
        list.add(0, response.getStatusLine().getStatusCode());
        list.add(1, checkResponseBody(readResponse(new StringBuilder(),response)));
        logger(INFO_LEVEL, "[INF] return data: [" + list.get(0) + ", " + list.get(1) + "]");
        return list;
    }

    /** Delete method
     * @param mac      - mac of the box
     * @param reminderScheduleId - TBD
     * @param reminderId - TBD
     * @return list
     * @throws IOException -TBD
     */
    ArrayList request(String server, String mac, Operation operation, int count_reminders, long reminderScheduleId, long reminderId) throws IOException {
        logger(INFO_LEVEL, "[INF] " + new Date() + ": " + operation.toString().toUpperCase() + ":");

        HttpPost request = new HttpPost(prepareUrl(server, operation, true));
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");
        request.setEntity(new StringEntity(generateJsonReminderDelete(mac, count_reminders, reminderScheduleId, reminderId)));
        logger(DEBUG_LEVEL, "[DBG] request string: " + request);

        long start = System.currentTimeMillis();
        HttpResponse response = HttpClients.createDefault().execute(request);
        long finish = System.currentTimeMillis();
        int current = (int)(finish-start);
        logger(DEBUG_LEVEL, "[DBG] " + current + "ms request");

        ArrayList list = new ArrayList();
        //list.ADD(0, response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
        list.add(0, response.getStatusLine().getStatusCode());
        list.add(1, checkResponseBody(readResponse(new StringBuilder(),response)));
        logger(INFO_LEVEL, "[INF] return data: [" + list.get(0) + ", " + list.get(1) + "]");
        return list;
    }

    /** Add / Modify method
     * @param mac      - mac of the box
     * @param operation       - can be Add / Modify / Delete / Purge
     * @param count_reminders - count_reminders of reminders to generate in json {..}
     * @param reminderProgramStart - TBD
     * @param reminderChannelNumber - TBD
     * @param reminderProgramId - TBD
     * @param reminderOffset - TBD
     * @param reminderScheduleId - TBD
     * @param reminderId - TBD
     * @return list
     * @throws IOException -TBD
     */
    public ArrayList request(String server, String mac, Operation operation, int count_reminders, String reminderProgramStart, long reminderChannelNumber, String reminderProgramId, long reminderOffset, long reminderScheduleId, long reminderId) throws IOException {
        logger(INFO_LEVEL, "[INF] " + new Date() + ": " + operation.toString().toUpperCase() + ":");

        HttpPost request = new HttpPost(prepareUrl(server, operation, true));
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");
        request.setEntity(new StringEntity(generateJsonReminder(mac, count_reminders, operation, reminderProgramStart, reminderChannelNumber, reminderProgramId, reminderOffset, reminderScheduleId, reminderId)));
        logger(DEBUG_LEVEL, "[DBG] request string: " + request);

        long start = System.currentTimeMillis();
        HttpResponse response = HttpClients.createDefault().execute(request);
        long finish = System.currentTimeMillis();
        int current = (int)(finish-start);
        logger(DEBUG_LEVEL, "[DBG] " + current + "ms request");

        ArrayList list = new ArrayList();
        //list.ADD(0, response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
        list.add(0, response.getStatusLine().getStatusCode());
        list.add(1, checkResponseBody(readResponse(new StringBuilder(),response)));
        logger(INFO_LEVEL, "[INF] return data: [" + list.get(0) + ", " + list.get(1) + "]");

        return list;
    }


    private String generateJsonSetting(String mac, String option, String value) throws IOException {
        //String json = "{\"settings\":{\"groups\":[{\"id\":\"STBmac\",\"type\":\"device-stb\",\"options\":[{\"name\":\"Audio Output\",\"value\":\"HDMI\"}]}]}}";
        JSONObject json = new JSONObject();
        JSONObject object_in_settings = new JSONObject();
        JSONArray array_groups = new JSONArray();

        json.put("settings", object_in_settings);
        object_in_settings.put("groups", array_groups);

        JSONObject object_in_groups = new JSONObject();
        array_groups.add(object_in_groups);
        object_in_groups.put("id", "STB" + mac);
        object_in_groups.put("type", "device-stb");
        JSONArray array_options = new JSONArray();
        object_in_groups.put("options", array_options);

        JSONObject object_in_options = new JSONObject();
        array_options.add(object_in_options);
        object_in_options.put("name", option);
        object_in_options.put("value", value);

        if(SHOW_GENERATED_JSON) {
            logger(INFO_LEVEL, "[JSON] generated json: " + json);
        }
        return json.toString();
    }

    private String generateJsonReminder(String mac, int count_reminders, Enum<Operation> operation, String reminderProgramStart, long reminderChannelNumber, String reminderProgramId, long reminderOffset, long reminderScheduleId, long reminderId) throws IOException {
        boolean first_clean_reminderScheduleId_list = true;
        boolean first_clean_reminderId_list = true;

        if (count_reminders < 0) { count_reminders = 0; }

        JSONObject json = new JSONObject();
        json.put("deviceId", mac);
        JSONArray array_reminders = new JSONArray();
        json.put("reminders", array_reminders);
        for (int i = 0; i < count_reminders; i++) {
            JSONObject object_in_reminders = new JSONObject();

            //generation reminderProgramStart
            if (Objects.equals(reminderProgramStart, "-1")) {
                object_in_reminders.put("reminderProgramStart", "");
            } else {
                object_in_reminders.put("reminderProgramStart", getDateTime(i));
            }

            //generation reminderChannelNumber
            if (reminderChannelNumber == -1) {
                object_in_reminders.put("reminderChannelNumber", "");
            } else if(reminderChannelNumber == -2) {
                object_in_reminders.put("reminderOffset", null);
            } else {
                object_in_reminders.put("reminderChannelNumber", reminderChannelNumber);
            }

            //reminderProgramId
            object_in_reminders.put("reminderProgramId", reminderProgramId);

            //generation reminderOffset
            if (reminderOffset == -1) {
                object_in_reminders.put("reminderOffset", "");
            } else if (reminderOffset == -2) {
                object_in_reminders.put("reminderOffset", null);
            } else {
                object_in_reminders.put("reminderOffset", reminderOffset);
            }

            //MAIN WORKING VARIANT
            //generation reminderScheduleId
            if (reminderScheduleId == 0) {
                object_in_reminders.put("reminderScheduleId", 0);
            } else if (reminderScheduleId == -1) {
                object_in_reminders.put("reminderScheduleId", "");
            } else if (reminderScheduleId == -2) {
                object_in_reminders.put("reminderScheduleId", null);
            } else if (reminderScheduleId == 1) {
                if (operation.name().equals("ADD")) {
                    object_in_reminders.put("reminderScheduleId", reminderScheduleId(Generation.INCREMENT));
                } else if (operation.name().equals("MODIFY")){
                    object_in_reminders.put("reminderScheduleId", reminderScheduleIdList.get(i));
                }
            } else if (reminderScheduleId == Long.MAX_VALUE) {
                object_in_reminders.put("reminderScheduleId", Long.MAX_VALUE);
            } else if (reminderScheduleId == Long.MIN_VALUE) {
                object_in_reminders.put("reminderScheduleId", Long.MIN_VALUE);
            } else if (count_reminders>1 && operation.name().equals("ADD")) {
                //todo//todo//todo FIXME
                if(first_clean_reminderScheduleId_list) {
                    reminderScheduleIdList.clear();
                    logger(DEBUG_LEVEL, "[DBG] preliminary clean reminderScheduleIdList !!!");
                    first_clean_reminderScheduleId_list = false;
                }
                object_in_reminders.put("reminderScheduleId", reminderScheduleId(Generation.RANDOM));
            } else if (count_reminders>1 && operation.name().equals("MODIFY")) {
                //todo//todo//todo FIXME
                object_in_reminders.put("reminderScheduleId", reminderScheduleIdList.get(i));
            } else {
                object_in_reminders.put("reminderScheduleId", reminderScheduleId);
            }


            //generation reminderId
            if (reminderId == 0) {
                object_in_reminders.put("reminderId", 0);
            } else if (reminderId == -1) {
                object_in_reminders.put("reminderId", "");
            } else if (reminderId == -2) {
                object_in_reminders.put("reminderId", null);
            } else if (reminderId == 1) {
                if (operation.name().equals("ADD")) {
                    object_in_reminders.put("reminderId", reminderId(Generation.INCREMENT));
                } else if (operation.name().equals("MODIFY")){
                    object_in_reminders.put("reminderId", reminderIdList.get(i));
                }
            } else if (reminderId == Long.MAX_VALUE) {
                object_in_reminders.put("reminderId", Long.MAX_VALUE);
            } else if (reminderId == Long.MIN_VALUE) {
                object_in_reminders.put("reminderId", Long.MIN_VALUE);
            } else if (count_reminders>1 && operation.name().equals("ADD")) {
                if(first_clean_reminderId_list) {
                    //todo//todo//todo FIXME
                    reminderIdList.clear();
                    logger(DEBUG_LEVEL,"[DBG] preliminary clean reminderIdList !!!");
                    first_clean_reminderId_list = false;
                }
                object_in_reminders.put("reminderId", reminderId(Generation.RANDOM));
            } else if (count_reminders>1 && operation.name().equals("MODIFY")) {
                //todo//todo//todo FIXME
                object_in_reminders.put("reminderId", reminderIdList.get(i));
            } else {
                object_in_reminders.put("reminderId", reminderId);
            }

            array_reminders.add(object_in_reminders);
        }


        if(reminderScheduleIdList.size()<=10) {
            logger(DEBUG_LEVEL,"reminderScheduleIdList : size=" + reminderScheduleIdList.size() + " : " + reminderScheduleIdList);
        }
        if(reminderIdList.size()<=10) {
            logger(DEBUG_LEVEL,"reminderIdList         : size=" + reminderIdList.size() + " : " + reminderIdList);
        }

        if(SHOW_GENERATED_JSON) {
            logger(INFO_LEVEL, "[JSON] generated json: " + json);
        }
        return json.toString();
    }

    private String generateJsonReminderDelete(String mac, int count_reminders, long reminderScheduleId, long reminderId) throws IOException {
        JSONObject json = new JSONObject();
        json.put("deviceId", mac);
        JSONArray array_reminders = new JSONArray();
        json.put("reminders", array_reminders);
        for (int i = 0; i < count_reminders; i++) {
            JSONObject object_in_reminders = new JSONObject();

            if (reminderScheduleId == 0) {
                object_in_reminders.put("reminderScheduleId", 0);
            } else if (reminderScheduleId == -1) {
                object_in_reminders.put("reminderScheduleId", "");
            } else if (reminderScheduleId == Long.MAX_VALUE) {
                object_in_reminders.put("reminderScheduleId", Long.MAX_VALUE);
            } else if (reminderScheduleId == Long.MIN_VALUE) {
                object_in_reminders.put("reminderScheduleId", Long.MIN_VALUE);
            } else if (count_reminders > 1) {
                object_in_reminders.put("reminderScheduleId", reminderScheduleIdList.get(i));
            } else {
                object_in_reminders.put("reminderScheduleId", reminderScheduleId);
            }

            if (reminderId == 0) {
                object_in_reminders.put("reminderId", 0);
            } else if (reminderId == -1) {
                object_in_reminders.put("reminderId", "");
            } else if (reminderId == Long.MAX_VALUE) {
                object_in_reminders.put("reminderId", Long.MAX_VALUE);
            } else if (reminderId == Long.MIN_VALUE) {
                object_in_reminders.put("reminderId", Long.MIN_VALUE);
            } else if (count_reminders > 1) {
                object_in_reminders.put("reminderId", reminderIdList.get(i));
            } else {
                object_in_reminders.put("reminderId", reminderId);
            }
            array_reminders.add(object_in_reminders);
        }

        if(SHOW_DEBUG_LEVEL) {
            if(reminderScheduleIdList.size()<=10) {
                logger(DEBUG_LEVEL, "reminderScheduleIdList : size=" + reminderScheduleIdList.size() + " : " + reminderScheduleIdList);
            }
            if(reminderIdList.size()<=10) {
                logger(DEBUG_LEVEL, "reminderIdList         : size=" + reminderIdList.size() + " : " + reminderIdList);
            }
        }

        if(SHOW_GENERATED_JSON && SHOW_INFO_LEVEL) {
            logger(INFO_LEVEL, "[JSON] generated json: " + json);
        }
        return json.toString();
    }

    private String generateJsonReminderPurge(String mac) throws IOException {
        JSONObject json = new JSONObject();
        json.put("deviceId", mac);
        JSONArray array_reminders = new JSONArray();
        json.put("reminders", array_reminders);

        if(SHOW_GENERATED_JSON && SHOW_INFO_LEVEL) {
            logger(INFO_LEVEL, "[JSON] generated json: " + json);
        }
        return json.toString();
    }

    public ArrayList changeSettings(String mac, String option, String value) throws IOException {
        logger(INFO_LEVEL, "Change settings for macaddress=" + mac + ", server=" + amsIp + " option=" + option + ", value=" + value);
        HttpPost request = new HttpPost("http://" + amsIp + ":" + amsPort + "/ams/settings");
        request.setHeader("Content-type", "application/json");

        request.setEntity(new StringEntity(generateJsonSetting(mac, option, value)));
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");
        logger(DEBUG_LEVEL, "[DBG] request string: " + request);
        //+ "\n[DBG] Request entity: " + request.getEntity());

        long start = currentTimeMillis();
        HttpResponse response = HttpClients.createDefault().execute(request);
        long finish = System.currentTimeMillis();
        int current = (int)(finish-start);
        logger(DEBUG_LEVEL, "[DBG] " + current + "ms request");

        ArrayList list = new ArrayList();
        //list.ADD(0, response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
        list.add(0, response.getStatusLine().getStatusCode());
        list.add(1, checkResponseBody(readResponse(new StringBuilder(),response)));
        logger(INFO_LEVEL, "[INF] return data: " + list + "\n");
        return list;
    }

    /** Purge method
     * @param mac - TBD
     * @param operation - TBD
     * @return list
     * @throws IOException - TBD
     */
    ArrayList requestPerformance(String ams_ip, String mac, Operation operation, int i) throws IOException {
        logger(INFO_LEVEL, "[INF] " + new Date() + ": " + operation.toString().toUpperCase() + ":");

        HttpPost request = new HttpPost(prepareUrl(ams_ip, operation, true));
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");
        request.setEntity(new StringEntity(generateJsonReminderPurge(mac)));
        logger(DEBUG_LEVEL, "[DBG] request string: " + request);

        long start = currentTimeMillis();
        HttpResponse response = HttpClients.createDefault().execute(request);
        long finish = System.currentTimeMillis();
        int current = (int)(finish-start);
        logger(DEBUG_LEVEL, "[DBG] " + current + "ms request");

        ArrayList list = new ArrayList();
        //list.ADD(0, response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
        list.add(0, response.getStatusLine().getStatusCode());
        list.add(1, checkResponseBody(readResponse(new StringBuilder(),response)));
        if (list.get(0).equals(HttpStatus.SC_OK)) {
            purgeList.add(current);
            list.add(2, current);
            list.add(3, getAverage(purgeList));
            list.add(4, searchMedian(purgeList, sorting));
            int[] min = getMin(Operation.PURGE, current, i);
            list.add(5, min[0]);
            list.add(6, min[1]);
            int[] max = getMax(Operation.PURGE, current, i);
            list.add(7, max[0]);javascript:;
            list.add(8, max[1]);
            list.add(9, purgeList.size());
            //logger(DEBUG_LEVEL,"[DBG] " + new Date() + ": PURGE avg = " + getAverage(purgeList) + "ms/" + totalI + ": purgeList:" + purgeList);
        }

        return list;
    }

    /** Delete method
     * @param mac      - mac of the box
     * @param reminderScheduleId - TBD
     * @param reminderId - TBD
     * @return list
     * @throws IOException -TBD
     */
    ArrayList requestPerformance(String ams_ip, String mac, Operation operation, int i, int count_reminders, long reminderScheduleId, long reminderId) throws IOException {
        logger(INFO_LEVEL, "[INF] " + new Date() + ": " + operation.toString().toUpperCase() + ":");

        HttpPost request = new HttpPost(prepareUrl(ams_ip, operation, true));
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");
        request.setEntity(new StringEntity(generateJsonReminderDelete(mac, count_reminders, reminderScheduleId, reminderId)));
        logger(DEBUG_LEVEL, "[DBG] request string: " + request);

        long start = System.currentTimeMillis();
        HttpResponse response = HttpClients.createDefault().execute(request);
        long finish = System.currentTimeMillis();
        int current = (int)(finish-start);
        logger(DEBUG_LEVEL, "[DBG] " + current + "ms request");

        ArrayList list = new ArrayList();
        //list.ADD(0, response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
        list.add(0, response.getStatusLine().getStatusCode());
        list.add(1, checkResponseBody(readResponse(new StringBuilder(),response)));
        if (list.get(0).equals(HttpStatus.SC_OK)) {
            deleteList.add(current);
            list.add(2, current);
            list.add(3, getAverage(deleteList));
            list.add(4, searchMedian(deleteList, sorting));
            int[] min = getMin(Operation.DELETE, current, i);
            list.add(5, min[0]);
            list.add(6, min[1]);
            int[] max = getMax(Operation.DELETE, current, i);
            list.add(7, max[0]);
            list.add(8, max[1]);
            list.add(9, deleteList.size());
            //logger(DEBUG_LEVEL, "[DBG] " + new Date() + ": DELETE avg = " + getAverage(deleteList) + "ms/" + deleteList.size() + ": deleteList:" + deleteList);
        }

        return list;
    }

    /** Add / Modify method
     * @param mac      - mac of the box
     * @param operation       - can be Add / Modify / Delete / Purge
     * @param count_reminders - count_reminders of reminders to generate in json {..}
     * @param reminderProgramStart - TBD
     * @param reminderChannelNumber - TBD
     * @param reminderProgramId - TBD
     * @param reminderOffset - TBD
     * @param reminderScheduleId - TBD
     * @param reminderId - TBD
     * @return list
     * @throws IOException -TBD
     */
    ArrayList requestPerformance(String ams_ip, String mac, Operation operation, int i, int count_reminders, String reminderProgramStart, long reminderChannelNumber, String reminderProgramId, long reminderOffset, long reminderScheduleId, long reminderId) throws IOException {
        logger(INFO_LEVEL, "[INF] " + new Date() + ": " + operation.toString().toUpperCase() + ":");

        HttpPost request = new HttpPost(prepareUrl(ams_ip, operation, true));
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");
        request.setEntity(new StringEntity(generateJsonReminder(mac, count_reminders, operation, reminderProgramStart, reminderChannelNumber, reminderProgramId, reminderOffset, reminderScheduleId, reminderId)));
        logger(DEBUG_LEVEL, "[DBG] request string: " + request);

        long start = System.currentTimeMillis();
        HttpResponse response = HttpClients.createDefault().execute(request);
        long finish = System.currentTimeMillis();
        int current = (int)(finish-start);
        logger(DEBUG_LEVEL, "[DBG] " + current + "ms request");

        ArrayList list = new ArrayList();
        //list.ADD(0, response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
        list.add(0, response.getStatusLine().getStatusCode());
        list.add(1, checkResponseBody(readResponse(new StringBuilder(),response)));
        if (list.get(0).equals(HttpStatus.SC_OK)) {
            if (operation.name().equals("ADD")) {
                addList.add(current);
                list.add(2, current);
                list.add(3, getAverage(addList));
                list.add(4, searchMedian(addList, sorting));
                int[] min = getMin(Operation.ADD, current, i);
                list.add(5, min[0]);
                list.add(6, min[1]);
                int[] max = getMax(Operation.ADD, current, i);
                list.add(7, max[0]);
                list.add(8, max[1]);
                list.add(9, addList.size());
                //logger(DEBUG_LEVEL, "[DBG] " + new Date() + ": ADD avg = " + getAverage(addList) + "ms/" + totalI + ": addList:" + addList);

            } else if (operation.name().equals("MODIFY")) {
                modifyList.add(current);
                list.add(2, current);
                list.add(3, getAverage(modifyList));
                list.add(4, searchMedian(modifyList, sorting));
                int[] min = getMin(Operation.MODIFY, current, i);
                list.add(5, min[0]);
                list.add(6, min[1]);
                int[] max = getMax(Operation.MODIFY, current, i);
                list.add(7, max[0]);
                list.add(8, max[1]);
                list.add(9, modifyList.size());
                //logger(DEBUG_LEVEL, "[DBG] " + new Date() + ": MODIFY avg = " + getAverage(modifyList) + "ms/" + totalI + ": modifyList:" + modifyList);
            }
        }

        return list;
    }


}
