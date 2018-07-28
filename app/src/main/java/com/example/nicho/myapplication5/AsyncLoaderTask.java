package com.example.nicho.myapplication5;

import android.app.AlertDialog;
import android.content.AsyncTaskLoader;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by Shalini on 2/27/2018.
 */

public class AsyncLoaderTask extends AsyncTask<String, Integer,String>{

    private MainActivity mainActivity;
    private  int count;

    //private final String dataURL=" https://api.iextrading.com";
    private static final String TAG = "AsyncLoaderTask";
    private HashMap stock_map=new HashMap();
    private String webURL="http://d.yimg.com/aq/autoc";
    String input;
    public AsyncLoaderTask(MainActivity ma)
    {
        mainActivity=ma;
    }

    @Override
    protected String doInBackground(String... strings) {

        input=strings[0];
        Uri.Builder buildURL = Uri.parse(webURL).buildUpon();
        buildURL.appendQueryParameter("region","US");
        buildURL.appendQueryParameter("lang","en-US");
        buildURL.appendQueryParameter("query",strings[0]);
        String urltouse=buildURL.build().toString();

        StringBuilder stringbuilder=new StringBuilder();

        try{
            URL url=new URL(urltouse);
            Log.d(TAG, "doInBackground: "+  urltouse);
            HttpURLConnection connection=(HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");

            InputStream inputsteam=connection.getInputStream();
            BufferedReader reader=new BufferedReader(new InputStreamReader(inputsteam));

            String line;
            while((line = reader.readLine())!= null)
            {
                stringbuilder.append(line).append("\n");
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return stringbuilder.toString();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        String stringdata=s;
        Log.d(TAG, "onPostExecute: "+ stringdata);
        try{
            JSONObject jsonObject=new JSONObject(stringdata);
            JSONObject resultset=(JSONObject)jsonObject.getJSONObject("ResultSet");

            JSONArray resultArray=resultset.getJSONArray("Result");

            if(resultArray.length()==0)
            {
               mainActivity.invalidstockentry(input);
            }

            for(int k=0;k<resultArray.length();k++) {
                JSONObject jsonObject1 = (JSONObject) resultArray.get(k);
                String type=jsonObject1.getString("type");
                String stock_symbol = jsonObject1.getString("symbol");
                String company_name = jsonObject1.getString("name");
                if(type.equals("S") && !stock_symbol.contains(".")) {
                    stock_map.put(stock_symbol,company_name);
                    Log.d(TAG, "onPostExecute: "+stock_symbol+ company_name);
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Log.d(TAG, "onPostExecute: Excption thrown");
        }

        mainActivity.datafromAsyncStockSymbol(stock_map,input);
    }
}
