package com.cmsys.linebacker.util;

import android.annotation.SuppressLint;

import com.cmsys.linebacker.bean.RestMessageBean;
import com.cmsys.linebacker.bean.RestResultAsteriskData;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by @CarlosJesusGH on 14/03/16.
 */
public class RestfulUtils {

    //Always remember replacing void spaces so it doesn't return an error. Use replace(" ", "%20")
    public static InputStream RestWsGETInputStream(String pUrl) throws Exception {
        InputStream in = null;
        try {
            URL url = new URL(pUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(10000);
            in = urlConnection.getInputStream();
        } catch (Exception e) {
            throw e;
        }
        return in;
    }

    //Always remember replacing void spaces so it doesn't return an error. Use replace(" ", "%20")
    @SuppressLint("NewApi")
    public static String RestWsGETString(String pUrl) throws Exception {
        String strResponse = null;
        try {
            URL url = new URL(pUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();
            strResponse = getStringFromInputStream(in);
        } catch (Exception e) {
            throw e;
        }
        return strResponse;
    }

    private static String performPostCall(String requestURL,
                                          HashMap<String, String> postDataParams) {

        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    private static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public static String getStringFromInputStream(InputStream pIs) throws Exception {
        BufferedReader r = new BufferedReader(new InputStreamReader(pIs));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
        }
        r.close();
        pIs.close();
        //
        return total.toString();
    }

    public static <T> T readRestfulAndParseToObject(String url, Class<T> classOfT) throws Exception {
        String data = null;
        T restResponse = null;
        try {
            data = RestWsGETString(url);
            restResponse = new Gson().fromJson(data, classOfT);
        } catch (Exception e) {
            ExceptionUtils.printExceptionToFile(e);
            throw e;
        }
        return restResponse;
    }

    public static <T> T readRestfulPostAndParseToObject(String url, Class<T> classOfT, HashMap<String, String> postDataParams) throws Exception {
        String data = null;
        T restResponse = null;
        try {
            data = performPostCall(url, postDataParams);
            restResponse = new Gson().fromJson(data, classOfT);
        } catch (Exception e) {
            ExceptionUtils.printExceptionToFile(e);
            throw e;
        }
        return restResponse;
    }

    public interface RestValueEventListener {
        void onDataChange(String string);

        void onCancelled(Exception e);
    }

}
