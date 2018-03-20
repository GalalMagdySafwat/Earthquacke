package com.example.android.eq.Utils;

import android.text.TextUtils;
import android.util.Log;

import com.example.android.eq.Earthquake;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by G on 3/1/2018.
 */

public class QueryUtils {
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private static URL createUrl (String stringUrl){
        URL url =null;
        try {
            url = new URL(stringUrl);
        }catch (MalformedURLException e){
        Log.e(LOG_TAG, "Problem building the URL ", e);
    }
    return url;
    }

    private static String makeHttpRequest(URL url)throws IOException{
        String jsonResponse = "";
        if (url==null){
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream=null;
        try {
            urlConnection =(HttpURLConnection)url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode()==200){
                inputStream=urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        }catch (IOException e){
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        }finally {
            if (urlConnection !=null){
                urlConnection.disconnect();
            }if (inputStream!=null){
                inputStream.close();
            }
        }
        return jsonResponse;
    }
    private static String readFromStream(InputStream inputStream)throws IOException{
        StringBuilder outPut = new StringBuilder();
        if (inputStream!=null){
            InputStreamReader inputStreamReader= new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader=new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line!=null){
                outPut.append(line);
                line=reader.readLine();
            }
        }
        return outPut.toString();
    }
    private static List<Earthquake> extractFeatureFromJson(String earthquakeJSON){
        if (TextUtils.isEmpty(earthquakeJSON)){
            return null;
        }
        List<Earthquake> earthquakes=new ArrayList<>();
        try {
            JSONObject baseJsonResponse =new JSONObject(earthquakeJSON);
            JSONArray earthquakeArray = baseJsonResponse.getJSONArray("features");
            for (int i = 0; i < earthquakeArray.length(); i++) {

                // Get a single earthquake at position i within the list of earthquakes
                JSONObject currentEarthquake = earthquakeArray.getJSONObject(i);

                // For a given earthquake, extract the JSONObject associated with the
                // key called "properties", which represents a list of all properties
                // for that earthquake.
                JSONObject properties = currentEarthquake.getJSONObject("properties");

                // Extract the value for the key called "mag"
                double magnitude = properties.getDouble("mag");

                // Extract the value for the key called "place"
                String location = properties.getString("place");

                // Extract the value for the key called "time"
                long time = properties.getLong("time");

                // Extract the value for the key called "url"
                String url = properties.getString("url");

                // Create a new {@link Earthquake} object with the magnitude, location, time,
                // and url from the JSON response.
                Earthquake earthquake = new Earthquake(magnitude, location, time, url);

                // Add the new {@link Earthquake} to the list of earthquakes.
                earthquakes.add(earthquake);
            }
        }catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }
        return earthquakes;
    }
    public static List<Earthquake> fetchEarthquakeData(String requestUrl) {
        URL url=createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse=makeHttpRequest(url);
        }catch (IOException e){
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        List<Earthquake> earthquakes =extractFeatureFromJson(jsonResponse);
        return earthquakes;
    }

    }

