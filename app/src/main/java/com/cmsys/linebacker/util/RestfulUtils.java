package com.cmsys.linebacker.util;

import android.annotation.SuppressLint;

import com.cmsys.linebacker.bean.RestMessageBean;
import com.cmsys.linebacker.bean.RestResultAsteriskData;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

    public interface RestValueEventListener {
        void onDataChange(String string);

        void onCancelled(Exception e);
    }

}
