package com.example.android.eq;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.eq.Utils.QueryUtils;

import java.util.List;

/**
 * Created by G on 3/13/2018.
 */

public class EarthquakeLoader extends AsyncTaskLoader {
    private static final String LOG_TAG = EarthquakeLoader.class.getName();
    private String mUrl;
    public EarthquakeLoader(Context context,String url) {
        super(context);
        mUrl = url;
    }
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Earthquake> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        List<Earthquake> earthquakes = QueryUtils.fetchEarthquakeData(mUrl);
        return earthquakes;
    }

}
