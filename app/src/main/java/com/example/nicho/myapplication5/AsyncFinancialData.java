package com.example.nicho.myapplication5;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by Admin on 03-03-2018.
 */

public class AsyncFinancialData  extends AsyncTask<String, Void, String> {

    private static final String TAG = "AsyncTaskLoadFinancialD";
    private MainActivity mainActivity;

    private final String stockDataURL = "https://api.iextrading.com";
    private String inputSymbol;

    public AsyncFinancialData(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(String... strings) {

        inputSymbol = strings[0];

        Uri.Builder buildUri = Uri.parse(stockDataURL).buildUpon();

        buildUri.appendPath("1.0");
        buildUri.appendPath("stock");
        buildUri.appendPath(inputSymbol);
        buildUri.appendPath("quote");

        //This is the url to use appending the data and the input symbol
        String urlToUse = buildUri.build().toString();
        Log.d(TAG, "doInBackground: the url = " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //To get request method and request code use following
            conn.setRequestMethod("GET");
            conn.getRequestMethod();
            conn.getResponseCode();
            InputStream inputStream = conn.getInputStream();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = buffer.readLine()) != null) {
                sb.append(line).append('\n');
            }
            Log.d(TAG, "doInBackground: The string got = " + sb.toString());
        } catch (Exception e) {
            //if no such financial data then exception like for the first time
            Log.d(TAG, "doInBackground: Exception in financial data");
            e.printStackTrace();
            return null;
        }

        String jsonFinData = sb.toString();
        return jsonFinData;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        Log.d(TAG, "onPostExecute: ");
        HashMap stock_display = fetchJsonfinancialData(s);
        Log.d(TAG, "onPostExecute: jj");
        mainActivity.datafromAsyncfinancial(stock_display);
    }

    private HashMap fetchJsonfinancialData(String str) {
        //No financial data found
        if (str == null)
            return null;

        //use the fetched financial data using json obejct and tore in HashMap for modification
        HashMap stockdata_financial = new HashMap();
        try {
            JSONObject jObj = new JSONObject(str);
            String company_name = jObj.getString("companyName");
            String price = jObj.getString("latestPrice");
            String price_change = jObj.getString("change");
            String change_percent = jObj.getString("changePercent");

            stockdata_financial.put("symbol", inputSymbol);
            stockdata_financial.put("company_name", company_name);
            stockdata_financial.put("priceofmap", price);
            stockdata_financial.put("change_price", price_change);
            stockdata_financial.put("changepercent", change_percent);

            Log.d(TAG, "parseFinJSONData: price= " + price + " change = " + price_change + " per = " + change_percent);
        } catch (Exception e) {
            Log.d(TAG, "parseFinJSONData: Error while parsing JSON Financial data");
            e.printStackTrace();
            return null;
        }
        return stockdata_financial;
    }
}

