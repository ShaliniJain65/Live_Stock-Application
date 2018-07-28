package com.example.nicho.myapplication5;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLClientInfoException;
import java.util.ArrayList;

/**
 * Created by Admin on 03-03-2018.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHandler";

    //If DB schema is changed, the database version must be incremented
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "StockAppDB";
    private static final String TABLE_NAME = "StockWatchTable";
    private static final String SYMBOL = "StockSymbol";
    private static final String COMPANY = "CompanyName";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    SYMBOL + " TEXT not null unique," +
                    COMPANY + " TEXT not null)";

    private SQLiteDatabase database;

   public DatabaseHandler(Context context){
       super(context,DATABASE_NAME,null,DATABASE_VERSION);
       database=getWritableDatabase();
   }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //onCreate() is called only if the DB does not exist
        Log.d(TAG, "onCreate: Create new DB");
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    public ArrayList<String[]> loadStocks()
    {
        Log.d(TAG, "loadStocks: ");
        ArrayList<String[]> stocks_list = new ArrayList<>();

        Cursor cursor = database.query(
                TABLE_NAME, //table query
                new String[]{SYMBOL, COMPANY}, //Columns to return
                null, // columns for the WHERE clause
                null, // values for WHERE clause
                null, // no grouping of rows
                null, // no filtering by roe groups
                null); //sorting order

        if(cursor != null) {
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                String symb = cursor.getString(0);
                String company = cursor.getString(1);

                stocks_list.add(new String[] {symb, company});
                cursor.moveToNext();
            }
            cursor.close();
        }
        return stocks_list;
    }

    //Adding the stock in database
    public void addStock(Stock stock) {
        Log.d(TAG, "addStock: Adding stock " + stock.getStock_symbol());

        ContentValues values = new ContentValues();
        values.put(SYMBOL, stock.getStock_symbol());
        values.put(COMPANY, stock.getCompany_name());
        database.insert(TABLE_NAME, null, values);

   }

    //Deleting the stock from database
    public void deleteStock(String symb)
    {
        Log.d(TAG, "deleteStock: Deleting stock "+ symb);
        if(database == null)
            Log.d(TAG, "deleteStock: nulllllllllll");
        int cnt = database.delete(TABLE_NAME, SYMBOL + " = ?", new String[] {symb});
        Log.d(TAG, "deleteStock: deleted "+ cnt);
    }

    public void shutDown() {
        database.close();
    }

    //to display the data in the console in the form as stored in the database
    public void dumpDbToLog() {
        Cursor cursor = database.rawQuery("select * from " + TABLE_NAME, null);
        if (cursor != null) {
            cursor.moveToFirst();

            Log.d(TAG, "dumpDbToLog: vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
            for (int i = 0; i < cursor.getCount(); i++) {
                String symbl = cursor.getString(0);
                String name = cursor.getString(1);

                Log.d(TAG, "dumpDbToLog: " +
                        String.format("%s %-18s", SYMBOL + ":", symbl) +
                        String.format("%s %-18s", COMPANY + ":", name));
                cursor.moveToNext();
            }
            cursor.close();
        }
        Log.d(TAG, "dumpDbToLog: ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
    }
}
