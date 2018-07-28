package com.example.nicho.myapplication5;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    //To store the list items in a array list
    private ArrayList<Stock> allstock = new ArrayList<>();
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swiper;

    //this varibale checks the network connectivity
    Boolean isConnect = true;
    String inputsymbol;
    private StockAdapter stockAdapter;
    AsyncLoaderTask asyncLoaderTask;

    private DatabaseHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        stockAdapter = new StockAdapter(this, allstock);
        recyclerView.setAdapter(stockAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //asyncLoaderTask = new AsyncLoaderTask(this);
        dbHandler = new DatabaseHandler(this);
        //Before this add swipe refresh swiper in main.xml
        //Then refer it by id and then set onrefresh
        swiper=(SwipeRefreshLayout)findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            //this method is used to refrsh the code and update stocks and also check the network connectivity
            public void onRefresh() {
                if(!doNetworkkCheck())
                {
                    //Alert for network display
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setMessage("Stocks cannnot be Added without A Network Connection");
                    builder1.setTitle("No Network Connection");

                    AlertDialog dialog = builder1.create();
                    dialog.show();
                }
                else
                    forrefresh();
                swiper.setRefreshing(false);
            }
        });
    }

    private void forrefresh() {

        dbHandler.dumpDbToLog();

        Log.d(TAG, "onResume: ");
        ArrayList<String[]> list = dbHandler.loadStocks();
        //Get the financial data from the internet by async task
        ArrayList<Stock> stocks_lst = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            new AsyncFinancialData(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, list.get(i)[0]);
        }
        allstock.clear();
        //if we have stock avialable then add it to the list
        allstock.addAll(stocks_lst);
        Log.d(TAG, "onResume: " + list);
        stockAdapter.notifyDataSetChanged();
    }

    //To inflate the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_stock, menu);
        return true;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public DatabaseHandler getDbHandler() {
        return dbHandler;
    }

    @Override
    protected void onResume() {
        dbHandler.dumpDbToLog();

        if(!doNetworkkCheck())
        {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
            builder1.setMessage("Stocks cannnot be Added without A Network Connection");
            builder1.setTitle("No Network Connection");

            AlertDialog dialog = builder1.create();
            dialog.show();
        }
        else {
            Log.d(TAG, "onResume: ");
            ArrayList<String[]> list = dbHandler.loadStocks();

            ArrayList<Stock> stocks_lst = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {

                //to get the financial data using Async2 methd
                new AsyncFinancialData(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, list.get(i)[0]);
            }

            allstock.clear();

            allstock.addAll(stocks_lst);
            Log.d(TAG, "onResume: " + list);
            stockAdapter.notifyDataSetChanged();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        dbHandler.shutDown();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_stock:
                //To add the stock and display the alert for entering stock symbol
                Log.d(TAG, "onOptionsItemSelected: Add Stock");
                doNetworkkCheck();
                if (isConnect == true) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    final EditText in = new EditText(this);
                    in.setInputType(InputType.TYPE_CLASS_TEXT);
                    in.setGravity(Gravity.CENTER_HORIZONTAL);
                    in.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

                    builder.setView(in);
                    //If positive button is clicked then execute Asnyc task
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.d(TAG, "onClick: P");
                            inputsymbol = in.getText().toString();
                            asyncLoaderTask = new AsyncLoaderTask(MainActivity.this);
                            asyncLoaderTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, inputsymbol.toString());

                        }
                    });

                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.d(TAG, "onClick: N");
                        }
                    });

                    builder.setMessage("Please Enter The Stock Symbol:");
                    builder.setTitle("Stock Selection");

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                    builder1.setMessage("Stocks cannnot be Added without A Network Connection");
                    builder1.setTitle("No Network Connection");

                    AlertDialog dialog = builder1.create();
                    dialog.show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //to check the network connectivity
    private boolean doNetworkkCheck() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = connectivityManager.getActiveNetworkInfo();
        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            isConnect = true;
        } else {
            isConnect = false;
        }
        return isConnect;
    }

    //If there is not stock symbol then display this alert
    public void invalidstockentry(String inputsymbol) {
        String input = inputsymbol;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Symbol Not Found :" + input.toUpperCase());
        builder.setMessage("Data for Stock Symbol");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //this is to get the data from the stock symbol asynch task
    public void datafromAsyncStockSymbol(HashMap stock_symb, String stock) {
        //if no such entry then invalid dialog
        if (stock_symb.size() == 0) {
            invalidstockentry(stock);

        }
        //If only one entry directly display
        else if (stock_symb.size() == 1) {
            getdatastockfromfinancial(stock);
        }
        //if multiple entries show a list view
        else {
            multiplestocksymbol(stock_symb);
        }
    }
    //This function dislay the list view of all the stocks that matches the input stock symbol
    public void multiplestocksymbol(HashMap stockmap) {
        HashMap<String, String> stocks = stockmap;
        Log.d(TAG, "multiplestocksymbol: " + stockmap.size());
        int k = 0;
        final CharSequence[] stockdisplay = new CharSequence[stocks.size()];
        for (Map.Entry<String, String> entry : stocks.entrySet()) {
            String line = entry.getKey() + " - " + entry.getValue();
            Log.d(TAG, "displayMultipleStockListDialog: map= " + line);
            stockdisplay[k++] = line;
        }
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Make a Selection");
        builder1.setItems(stockdisplay, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String stockName = stockdisplay[i].toString().substring(0, (stockdisplay[i].toString().indexOf("-") - 1));
                getdatastockfromfinancial(stockName);
                Log.d(TAG, "onClick: Reached");
            }
        });

        builder1.setNegativeButton("NEVERMIND", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        AlertDialog dialog = builder1.create();
        dialog.show();
    }

    //This fucntion is used fetch the financial data from the async task
    public void getdatastockfromfinancial(String stock_sym) {
        Log.d(TAG, "getStockFinancialDetails: selected stock symbol = " + stock_sym);
        boolean flag = false;
        for(int i=0; i< allstock.size() && flag == false; i++)
        {
            if(allstock.get(i).getStock_symbol().equals(stock_sym)){
            Log.d(TAG, "getdatastockfromfinancial:yesssssss ");

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Duplicate Stock");
                builder.setMessage("Stock Symbol"+ " "+stock_sym + " is already displayed.");
                builder.setIcon(R.drawable.ic_warning_black_48dp);

                AlertDialog dialog = builder.create();
                dialog.show();
                flag =true;
        }
        }
        if(flag == false)
            new AsyncFinancialData(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, stock_sym);
    }

    //This gets the fetched data like price,trade price and percent and if not display alert
    public void datafromAsyncfinancial(HashMap stock_fin) {
        if (stock_fin == null || stock_fin.size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Data Found");
            builder.setMessage("No Financial Stock Data found for the selected item!");

            AlertDialog dialog = builder.create();
            dialog.show();
            //invalidstockentry(inputsymbol);
        } else {
            Log.d(TAG, "datafromAsyncfinancial: iii");
            //List View display
            Stock stock1 = new Stock();
            stock1.setStock_symbol(stock_fin.get("symbol").toString());
            stock1.setCompany_name(stock_fin.get("company_name").toString());

            try {
                Log.d(TAG, "datafromAsyncfinancial: in try");
                Double val = Double.parseDouble(stock_fin.get("priceofmap").toString());
                stock1.setTrade_price(val);

                val = Double.parseDouble(stock_fin.get("change_price").toString());
                stock1.setChange_amount(val);

                val = Double.parseDouble(stock_fin.get("changepercent").toString());
                stock1.setPrice_percent(val);
            } catch (Exception e) {
                Log.d(TAG, "getFromAsyncTaskStockFinancialData: Number Format Exception");
            }

            allstock.add(stock1);
            stockAdapter.sortList();
            stockAdapter.notifyDataSetChanged();

            //Add the stock in database
            dbHandler.addStock(stock1);
        }
    }
}




