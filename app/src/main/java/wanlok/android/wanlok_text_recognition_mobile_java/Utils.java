package wanlok.android.wanlok_text_recognition_mobile_java;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

public class Utils {
    public static final SimpleDateFormat FILE_NAME_DATE_TIME_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);

    public static String getString(JSONObject jsonObject, String key) {
        String value;
        try {
            value = jsonObject.getString(key);
        } catch (JSONException e) {
            value = null;
        }
        return value;
    }

    public static JSONArray getJSONArray(JSONObject jsonObject, String key) {
        JSONArray value;
        try {
            value = jsonObject.getJSONArray(key);
        } catch (JSONException e) {
            value = null;
        }
        return value;
    }

    public static Object get(JSONObject jsonObject, String key, int i) {
        Object value;
        JSONArray jsonArray;
        try {
            jsonArray = jsonObject.getJSONArray(key);
        } catch (JSONException e) {
            jsonArray = null;
        }
        if (jsonArray != null && jsonArray.length() > i) {
            try {
                value = jsonArray.get(i);
            } catch (JSONException e) {
                value = null;
            }
        } else {
            value = null;
        }
        return value;
    }

    public static void set(JSONObject jsonObject, String key, String value) {
        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void set(JSONObject jsonObject, String key, Integer value) {
        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void set(JSONObject jsonObject, String key, Double value) {
        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void set(JSONObject jsonObject, String key, JSONArray value) {
        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getHostAddress() {
        List<String> hostAddresses = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    hostAddresses.add(inetAddress.getHostAddress());
                }
            }
        } catch (SocketException ex) {

        }
        return hostAddresses;
    }

    public static void copy(File from, File to) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(from);
            fileOutputStream = new FileOutputStream(to);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = fileInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, read);
                fileOutputStream.flush();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try { fileInputStream.close(); } catch (IOException e) {}
            }
            if (fileInputStream != null) {
                try { fileOutputStream.close(); } catch (IOException e) {}
            }
        }
    }
}
