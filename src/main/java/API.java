import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.junit.Rule;
import org.junit.rules.Timeout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

import static java.lang.System.currentTimeMillis;

/**
 * we are as Middle: send requests to AMS and got responses
 * Middle -> AMS -> box -> AMS -> Middle
 */
public class API {

    //API(){
    //    reminderScheduleId_arraylist = new ArrayList<>();
    //    reminderId_arraylist = new ArrayList<>();
    //}
    //private ArrayList<Long> reminderScheduleId_arraylist;
    //private ArrayList<Long> reminderId_arraylist;

    //long reminderScheduleId_arraylist() {
    //    return reminderScheduleId_arraylist.get(0);
    //}

    //long reminderId_arraylist() {
    //    return reminderId_arraylist.get(0);
    //}

    @Rule
    final public Timeout globalTimeout = Timeout.seconds(20);

    //private final static Logger log = Logger.getLogger(API.class.getName());

    enum Operation { add, modify, delete, purge, blablabla, blablablablablablablablablablablablablablabla }

    Boolean show_generated_json = true;

    Boolean show_extra_info = false;
    //enum Http { Get, Post, Delete };

    //static Logger log = Logger.getLogger(testAMS.class.getName());
    //FileHandler txtFile = new FileHandler ("log.log", true);
    //private FileHandler fh = new FileHandler("test_reminder.log");

    //public void testMiddle_Request(){
    //curl -vk -X POST -H "Content-Type: application/json"
    //String charterapiX = "http://specd.partnerapi.engprod-charter.net/api/pub/remindersmiddle/v1/reminders" +
    //"?deviceId=000007444C77&lineupId=CA11-1"
    //-d '{"reminderType":"Individual","deliveryId":"49767-MV000209150000-1488390180000","channelId":"49767","programId":"MV000209150000","channelNumber":662,"startTime":1488390180000,"reminderPresetTime":0}'

    final String charterapi_a = "http://spec.partnerapi.engprod-charter.net/api/pub";
    final String charterapi_b = "http://specb.partnerapi.engprod-charter.net/api/pub";
    final String charterapi_c = "http://specc.partnerapi.engprod-charter.net/api/pub";
    final String charterapi_d = "http://specd.partnerapi.engprod-charter.net/api/pub";
    final String postfix_settings = "/networksettingsmiddle/ns/settings";
    final String charterapi = charterapi_b;

    final String expected200 = "200 OK";
    final String expected201 = "201 Created";
    final String expected400 = "400 Bad Request";
    final String expected404 = "404 Not Found";
    final String expected405 = "405 Method Not Allowed";
    final String expected500 = "500 Internal Server Error";
    final String expected504 = "504 Server data timeout";

    final String macaddress_wrong = "123456789012";
    final String boxD101 = "A0722CEEC970"; //WB20 D101 ???
    final String boxD102 = "3438B7EB2E24"; //WB20 D102
    final String boxD103 = "3438B7EB2E28"; //WB20 D103
    final String boxD104 = "3438B7EB2E34"; //WB20 D104
    final String boxD105 = "3438B7EB2E30"; //WB20 D105
    final String boxD106 = "3438B7EB2EC4"; //WB20 D106
    final String box_Kirmoto = "0000007F8214"; //Kirmoto
    final String box_Tanya = "000005FE680A"; //Tanya
    final String box_Vitya = "A0722CEEC934"; //Vitya
    final String box_Katya_V = "0000048D4EB4"; //Katya_V
    final String boxD111 = "2c7e81ee2530";
    final String boxX = "1";
    String macaddress = boxD102;


    //DATES
    String reminderProgramStart_past = "2000-01-01";
    String reminderProgramStart_wrong = "0000-00-00";
    String reminderProgramStart_text = "yyyy-mm-dd";
    //String[] rack_date = {"2018-03-15"};

    //CHANNELS
    @Deprecated
    int reminderChannelNumber = 2;
    int reminderChannelNumber_empty;
    int reminderChannelNumber_for_statuscode3 = 9999;
    int reminderChannelNumber_for_statuscode4 = 1000;
    /*private Integer[] rack_channel30 = { 2, 3, 4, 5, 6, 7, 8, 9, 12, 13,
            14, 16, 18, 19, 22, 23, 25, 28, 30, 31,
            32, 33, 37, 38, 41, 44, 46, 48, 49, 50 };*/
    Integer[] rack_channel = {2, 3, 4, 5, 6, 7, 8, 9, 10, 11};

    //String reminderProgramId = ""; //"reminderProgramId": "EP002960010113"
    String reminderProgramId = "EP002960010113";
    //String reminderProgramId = "0";
    String reminderProgramId_empty;

    int reminderOffset = 0;
    int reminderOffset_null;
    int reminderOffset_new = 10;
    int reminderOffset_new_null;

    long reminderScheduleId = 1;
    long reminderScheduleId_null;
    long reminderScheduleId_random = -1;

    long reminderId = 1;
    long reminderId_random = -1;

    int count_reminders = 1;

    private static String[] statuscode = {
            "0 - requested operation with the reminder was accomplished successfully. Always returned for \"Reminders Purge\" request (Request ID=3)",
            "1 - the number of reminders for the STB exceeded the limitation Applies to \"Reminders Add\" request (Request ID=0)",
            "2 - reminder is set for time in the past. Applies to \"Reminders Add\" request (Request ID=0)",
            "3 - reminder is set for unknown channel. \"Reminders Add\" request (Request ID=0)",
            "4 - reminder is unknown. Applies to \"Reminders Delete\" request (Request ID=1) and \"Reminders Modify\" request (Request ID=2)",
            "5 - reminder with provided pair of identifiers (reminderScheduleId and reminderId) is already set \"Reminders Add\" request (Request ID=0)"};

    String ams_ip = "172.30.81.4";
    //String ams_ip = "172.30.112.19";
    //String ams_ip = "172.30.82.132";
    int ams_port = 8080;

    @Deprecated
    String generate_json_test(String date, int count_remindres, String operation, int reminderOffset) {
        System.out.println("[DBG] [date] Generate_json: with date=" + date + ", " +
                "count_reminders=" + count_remindres + ", " +
                "operation=" + operation + ", " +
                "reminderOffset=" + reminderOffset);
         /*
        //WORKING parsing from json_string to Class:
        Gson g = new Gson();
        Reminder reminder = g.fromJson(json_add2, Reminder.class);
        System.out.println("[DBG] parsing from json_string to Class: \nmacaddress: " + reminder.deviceId);
        System.out.println("[DBG] count of reminders in class: " + reminder.reminders.size());
        for(Reminders rems : reminder.reminders){System.out.println(
                    "operation: " + rems.operation + ", " +
                    "reminderChannelNumber: " + rems.reminderChannelNumber + ", " +
                    "reminderProgramStart: " + rems.reminderProgramStart + ", " +
                    "reminderProgramId: " + rems.reminderProgramId + ", " +
                    "reminderOffset: " + rems.reminderOffset);
            }

        //parsing from Class to json_string
        System.out.println("[DBG] parsing from Class to json_string: \n" + g.toJson(reminder));
*/
/*        //WORKING variant for one class Reminder + one class Reminders
        //==============================================================
        final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

        //from class -> to string json
        //class with fields:
        Reminders rs = new Reminders(operation, reminderChannelNumber, reminderProgramStart, reminderProgramId, reminderOffset);
        Reminder r = new Reminder(macaddress, rs);
        //create json structure:
        String json = GSON.toJson(r);
        System.out.println("[DBG] from class -> to string json:\n" + json);

        //from json string -> to class
        Reminder to_class = GSON.fromJson(json, Reminder.class);
        System.out.println("[DBG] from json string -> to class:\n" + to_class.getDeviceId()+ " " + to_class.getClass());
*/
/*
        //from class -> to string json
        Reminder from_class = new Reminder(macaddress, Arrays.asList("operation", "reminderChannelNumber", "reminderProgramStart", "reminderProgramId", "reminderOffset"));
        String json = GSON.toJson(from_class);
        System.out.println("[DBG] from class -> to string json:\n" + json);

        //from json string -> to class
        Reminder to_class = GSON.fromJson(json, Reminder.class);
        System.out.println("[DBG] from json string -> to class:\n" + to_class.getDeviceId()+ " " + to_class.getReminders_list());
*/
/*
        //WORKING
        JsonObject jo = new JsonParser().parse(json_add2).getAsJsonObject();
        System.out.println("1 show full jsonobject: " + jo);
        show only jsonarray:
        String ja = jo.get("reminders").getAsJsonArray().toString();
        System.out.println("2 only jsonarray: " + ja);
*/
    return "";
    }

    String check_body_response(String body, String macaddress) {
        String result = "";
        if(body.contains("\"statusCode\":1")){
            //log.warning("one or more statusCode's = " + statuscode[1]);
            System.out.println("! one or more statusCode's = " + statuscode[1]);
            result += "1";
        }
        if(body.contains("\"statusCode\":2")){
            //log.warning("one or more statusCode's = " + statuscode[2]);
            System.out.println("! one or more statusCode's = " + statuscode[2]);
            result += "2";
        }
        if(body.contains("\"statusCode\":3")){
            //log.warning("one or more statusCode's = " + statuscode[3]);
            System.out.println("! one or more statusCode's = " + statuscode[3]);
            result += "3";
        }
        if(body.contains("\"statusCode\":4")){
            //log.warning("one or more statusCode's = " + statuscode[4]);
            System.out.println("! one or more statusCode's = " + statuscode[4]);
            result += "4";
        }
        if(body.contains("\"statusCode\":5")){
            //log.warning("one or more statusCode's = " + statuscode[5]);
            System.out.println("! one or more statusCode's = " + statuscode[5]);
            result += "5";
        }
        if(body.contains("\"status\":\"Failed\"") && body.contains("\"errorMessage\":\"REM-ST-001 Box is not registered\"")){
            result += "REM-ST-001 Box is not registered";
        }
        if(body.contains("\"status\":\"Failed\"") && body.contains("\"errorMessage\":\"REM-002 Reminders Service error: REM-112\"")){
            result += "REM-002 Reminders Service error: REM-012 [" + macaddress + "] Request not accomplished";
        }
        if(body.contains("\"status\":\"Failed\"") && body.contains("\"errorMessage\":\"REM-002 Reminders Service error: Timeout detected by BoxResponseTracker\"")){
            result += "REM-002 Reminders Service error: Timeout detected by BoxResponseTracker";
        }
        if(body.contains("\"status\":\"Failed\"") && body.contains("\"errorMessage\":\"REM-008 Reminders parsing error: missing program start\"")){
            result += "REM-008 Reminders parsing error: missing program start";
        }
        if(body.contains("\"status\":\"Failed\"") && body.contains("\"errorMessage\":\"REM-008 Reminders parsing error: missing channel number\"")){
            result += "REM-008 Reminders parsing error: missing channel number";
        }
        if(body.contains("\"status\":\"Failed\"") && body.contains("\"errorMessage\":\"REM-008 Reminders parsing error: missing offset\"")){
            result += "REM-008 Reminders parsing error: missing offset";
        }
        if(body.contains("\"status\":\"Failed\"") && body.contains("\"errorMessage\":\"REM-008 Reminders parsing error: incorrect reminderScheduleId\"")){
            result += "REM-008 Reminders parsing error: incorrect reminderScheduleId";
        }
        if(body.contains("\"status\":\"Failed\"") && body.contains("\"errorMessage\":\"REM-008 Reminders parsing error: wrong number of reminders\"")){
            result += "REM-008 Reminders parsing error: wrong number of reminders";
        }
        if(body.contains("\"status\":\"Failed\"") && body.contains("\"errorMessage\":\"Incorrect request: ChangeReminders\"")){
            result += "Incorrect request: ChangeReminders";
        }
        if(body.contains("\"status\":\"Failed\"") && body.contains("\"errorMessage\":\"Incorrect request: blablabla\"")){
            result += "Incorrect request: blablabla";
        }
        if(body.contains("\"status\":\"Failed\"") && body.contains("\"errorMessage\":\"name cannot be null\"")){
            result += "name cannot be null";
        }
        if(body.contains("REM-002 Reminders Service error: Can not connect to STB with stbId=" + macaddress)){
            result += "REM-002 Reminders Service error: Can not connect to STB with stbId=" + macaddress;
        }
        if(body.contains("REM-008 Reminders parsing error: wrong deviceId")){
            result += "REM-008 Reminders parsing error: wrong deviceId";
        }
        if(body.contains("REM-008 Reminders parsing error: wrong operation")){
            result += "REM-008 Reminders parsing error: wrong operation";
        }
        if(body.contains("REM-008 Reminders parsing error: incorrect message format")){
            result += "REM-008 Reminders parsing error: incorrect message format";
        }
        if(body.contains("REM-008 Reminders parsing error: incorrect reminderId")){
            result += "REM-008 Reminders parsing error: incorrect reminderId";
        }
        if(body.contains("Failed to getAmsIpByMacAddress for : " + macaddress + ", with error: No amsIp found for macAddress: STB" +macaddress)){
            result += "No amsIp found for macAddress";
        }
        if(body.contains("STB MAC not found: " + macaddress)){
            result += "STB MAC not found: " + macaddress;
        }
        if(body.contains("incorrect value")){
            result += "incorrect value";
        }
        if(body.contains("SET-025 Unsupported data type: Not a JSON Object:")){
            result += "SET-025 Unsupported data type: Not a JSON Object";
        }
        if(body.contains("responseCode\":\"ERROR_SCHEDULING_REMINDER")){
            result += "ERROR_SCHEDULING_REMINDER";
        }

        //System.out.println("[DBG] check_body_for_statuscode: result: " + result);
        return result;
    }

    StringBuilder read_response(StringBuilder body, HttpResponse response) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
        //StringBuilder body = new StringBuilder();
        for (String line; (line = reader.readLine()) != null; ) {
            System.out.print(", response body: " + body.append(line));
            if(reader.readLine() == null){
                System.out.println();
            }
        }
        return body;
    }

    HttpGet prepare_get_request(String uri) {
        HttpGet request = new HttpGet(uri);
        request.setHeader("Content-type", "application/json");
        request.setHeader("Cache-Control", "no-cache");
        System.out.println("[DBG] Request string: " + request);
        return request;
    }

    HttpPost prepare_post_request(String uri){
        HttpPost request = new HttpPost(uri);
        System.out.println("[DBG] Request string: " + request);
        return request;
    }

    /*@Deprecated
    ArrayList Purge(String ams_ip, String macaddress) throws IOException {
        System.out.println("Purge for ams_ip=" + ams_ip + " and macaddress=" + macaddress);
        //log.info("Purge for ams_ip=" + ams_ip + " and macaddress=" + macaddress);

        String url = "http://" + ams_ip + ":" + ams_port + postfix_change;
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost request = new HttpPost(url);

        String json_purge = "{\"deviceId\":" + macaddress + ",\"reminders\":[{\"operation\":\"Purge\"}]}";
        request.setEntity(new StringEntity(json_purge));
        //request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        System.out.println("[DBG] Request string: " + request);
        //+ "\n[DBG] Request entity: " + request.getEntity());

        long start = currentTimeMillis();
        HttpResponse response = client.execute(request);
        long finish = currentTimeMillis();
        System.out.println("[DBG] " + (finish - start) + "ms request, " +
                "Response getStatusLine: " + response.getStatusLine());

        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
        StringBuilder body = new StringBuilder();
        for (String line = null; (line = reader.readLine()) != null; ) {
            body.append(line);
            //System.out.println("[DBG] Response body: " + body.append(line).append("\n"));
        }

   /*     // Считываем json
        Object obj = new JSONParser().parse(json_purge); // Object obj = new JSONParser().parse(new FileReader("JSONExample.json"));
        // Кастим obj в JSONObject
        JSONObject jo = (JSONObject) obj;
        // Достаём firstName and lastName
        String firstName = (String) jo.get("firstName");
        String lastName = (String) jo.get("lastName");
        System.out.println("fio: " + firstName + " " + lastName);
        // Достаем массив номеров
        JSONArray phoneNumbersArr = (JSONArray) jo.get("phoneNumbers");
        Iterator phonesItr = phoneNumbersArr.iterator();
        System.out.println("phoneNumbers:");
        // Выводим в цикле данные массива
        while (phonesItr.hasNext()) {
            JSONObject test = (JSONObject) phonesItr.next();
            System.out.println("- type: " + test.get("type") + ", phone: " + test.get("number"));
        }*/

   /*     ArrayList arrayList = new ArrayList();
        arrayList.add(0, response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase());
        arrayList.add(1, check_body_for_statuscode(body.toString()));
        client.close();
        return arrayList;
    }*/

    ArrayList QueryDB(String ams_ip, String macaddress) throws ClassNotFoundException, SQLException {
        //ResultSet QueryDB(String macaddress) throws ClassNotFoundException, SQLException {
        System.out.println("QueryDB for macaddress=" + macaddress + " to DB on AMS=" + ams_ip);

        String url = "jdbc:oracle:thin:@//ams-db01.enwd.co.sa.charterlab.com:1521/zdev02";
        String username = "ams_ipv6_e591";
        String password = "ams_ipv6_e591";

        Class.forName("oracle.jdbc.driver.OracleDriver");
        Connection connection = DriverManager.getConnection(url, username, password);
        Statement statement = connection.createStatement();

        long start = currentTimeMillis();
        ResultSet result = statement.executeQuery("select * from MAC_IP where MAC_STR = '" + macaddress + "\'");
        long finish = currentTimeMillis();
        System.out.println("[DBG] " + (finish - start) + "ms query");

        ArrayList actual = new ArrayList();
        while (result.next()) {
            actual.add(result.getLong(1));
            actual.add(result.getLong(2));
            actual.add(result.getInt(3));
            actual.add(result.getString(4));
            actual.add(result.getString(5));
        }
        if(!actual.isEmpty()) {
            System.out.println("[DBG] return result: "
                    + actual.get(0) + "  "
                    + actual.get(1) + "  "
                    + actual.get(2) + "  "
                    + actual.get(3) + "  "
                    + actual.get(4));
        }
        connection.close();
        return actual;
    }

    String reminderProgramStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat pattern = new SimpleDateFormat("yyyy-MM-dd");
        calendar.add(Calendar.DAY_OF_YEAR, +1);
        return pattern.format(calendar.getTime());
    }

    int reminderChannelNumber() {
        Random random = new Random();
        return Math.abs(random.nextInt(1000));
    }

    long reminderScheduleId(){
        Random random = new Random();
        long reminderScheduleId = Math.abs(random.nextLong());
        //reminderScheduleId_arraylist.add(reminderScheduleId);
        return reminderScheduleId;
    }

    long reminderId(){
        Random random = new Random();
        long reminderId = Math.abs(random.nextLong());
        //reminderId_arraylist.add(reminderId);
        return reminderId;
    }

    String get_date(int count, Boolean several) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat pattern = new SimpleDateFormat("yyyy-MM-dd");
        StringBuilder result = new StringBuilder();
        if (several) {
            for (int i = 1; i <= count; i++) {
                calendar.add(Calendar.DAY_OF_YEAR, +1);
                result.append(pattern.format(calendar.getTime()));
                if (i != count) {
                    result.append(" ");
                }
            }
        } else {
            calendar.add(Calendar.DAY_OF_YEAR, +count);
            result = result.append(pattern.format(calendar.getTime()));
        }
        System.out.println("generated result: " + result);
        return result.toString();
    }

    @Deprecated
    String get_time(int count_reminders) {
        int interval_in_minutes;
        if (count_reminders<=48){ interval_in_minutes = 30; }
        else if (count_reminders<=288){ interval_in_minutes = 5; }
        else if (count_reminders<=720){ interval_in_minutes = 2; }
        else interval_in_minutes = 1;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat pattern = new SimpleDateFormat("HH:mm");
        calendar.setTime(new java.util.Date(0, 0, 0, 0, 0));

        StringBuilder result = new StringBuilder();
        for (int i=1; i<=count_reminders; i++){
            result.append(pattern.format(calendar.getTime()));
            calendar.add(Calendar.MINUTE, interval_in_minutes);
            if(i!=count_reminders){
                result.append(" ");
            }
        }
        System.out.println("generated times: " + result);
        return result.toString();
    }

    String get_time(int count_reminders, int number) {
        int interval_in_minutes;
        if (count_reminders<=48){ interval_in_minutes = 30; }
        else if (count_reminders<=288){ interval_in_minutes = 5; }
        else if (count_reminders<=720){ interval_in_minutes = 2; }
        else interval_in_minutes = 1;

        if (number < 1) { number = 1; }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat pattern = new SimpleDateFormat("HH:mm");
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.setTime(new java.util.Date(0, 0, 0, 0, 0));
        calendar.add(Calendar.MINUTE, interval_in_minutes*(number-1));
        return pattern.format(calendar.getTime());
    }

}