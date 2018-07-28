package com.example.nicho.myapplication5;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by Shalini on 2/27/2018.
 */

public class StockAdapter extends RecyclerView.Adapter<StockViewHolder>{

    private static final String TAG = "StockAdapter";
    //private static String marketwatchurl=  "http://www.marketwatch.com/investing/stock";

    private ArrayList<Stock> allstock;
    private DatabaseHandler dbHandler;
    private RecyclerView recyclerView;
    private static String marketwatchurl=  "http://www.marketwatch.com/investing/stock/";

    private static DecimalFormat df = new DecimalFormat(".##");
    private MainActivity mainActivity;

    public StockAdapter(MainActivity ma,ArrayList<Stock> stocks){
        mainActivity=ma;
        allstock=stocks;
    }
    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");

        //Create a view of the template with the filled data
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_list_entry, parent, false);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: ");
                final int position=mainActivity.getRecyclerView().getChildLayoutPosition(view);
                String url=marketwatchurl + allstock.get(position).getStock_symbol();
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                mainActivity.startActivity(intent);
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            //on long click deletes the particular data clicked from list and database
            public boolean onLongClick(View view) {
                Log.d(TAG, "onLongClick: IN");
                final int position=mainActivity.getRecyclerView().getChildLayoutPosition(view);
                AlertDialog.Builder builder=new AlertDialog.Builder(mainActivity);
                builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mainActivity.getDbHandler().deleteStock(allstock.get(position).getStock_symbol());
                        allstock.remove(position);
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

                //Confirm with the user do you want to delete the particular stock
                builder.setMessage("Delete Stock Symbol "+allstock.get(position).getStock_symbol()+ "?");
                builder.setTitle("Delete Stock");
                builder.setIcon(R.drawable.ic_delete_black_48dp);
                AlertDialog dialog=builder.create();
                dialog.show();
                Log.d(TAG, "onLongClick: ");
                return true;

            }
        });

        //-- The data is filled now create object and display
        return new StockViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {

        Stock stocklistview=allstock.get(position);
        //It displays the stock which are up in green and the one which are down in red
        if(stocklistview.getChange_amount() < 0)
        {
            holder.stock_symbol.setTextColor(Color.RED);
            holder.company_name.setTextColor(Color.RED);
            holder.trade_price.setTextColor(Color.RED);
            holder.change_amount.setTextColor(Color.RED);
            holder.price_percent.setTextColor(Color.RED);

            holder.stock_symbol.setText(stocklistview.getStock_symbol());
            holder.company_name.setText(stocklistview.getCompany_name());
            holder.trade_price.setText(""+stocklistview.getTrade_price());

            holder.change_amount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_down_24,0,0,0);
            holder.change_amount.setText(""+stocklistview.getChange_amount());
            holder.price_percent.setText("("+ String.format("%.2f",(stocklistview.getPrice_percent()*100))+ "%)");

        }
        else {
            holder.stock_symbol.setTextColor(Color.GREEN);
            holder.company_name.setTextColor(Color.GREEN);
            holder.trade_price.setTextColor(Color.GREEN);
            holder.change_amount.setTextColor(Color.GREEN);
            holder.price_percent.setTextColor(Color.GREEN);
            holder.stock_symbol.setText(stocklistview.getStock_symbol());
            holder.company_name.setText(stocklistview.getCompany_name());
            holder.change_amount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_up_24,0,0,0);
            holder.trade_price.setText("" + stocklistview.getTrade_price());
            holder.change_amount.setText("" + stocklistview.getChange_amount());
            holder.price_percent.setText("(" + String.format("%.2f", (stocklistview.getPrice_percent() * 100)) + "%)");
        }
    }

    @Override
    public int getItemCount() {
        return allstock.size();
    }

    //It sorts the list alphabaticallly
    public ArrayList<Stock> sortList()
    {
        Collections.sort(allstock, new Comparator<Stock>() {
            @Override
            public int compare(Stock stock, Stock stock1) {
                return stock.compare(stock.getStock_symbol(), stock1.getStock_symbol());
            }
        });
        return allstock;
    }
}
