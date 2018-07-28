package com.example.nicho.myapplication5;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.TimeoutException;

/**
 * Created by Shalini on 2/27/2018.
 */

public class StockViewHolder extends RecyclerView.ViewHolder{

    public TextView stock_symbol;
    public TextView company_name;
    public TextView trade_price;
    public ImageView price_changesymbol;
    public TextView change_amount;
    public TextView price_percent;

    public StockViewHolder(View itemView) {

        //This funtion displays the recycler view contents by setting them
        super(itemView);
        stock_symbol = (TextView) itemView.findViewById(R.id.stock_symbol);
        company_name = (TextView) itemView.findViewById(R.id.company_name);
        trade_price=(TextView)itemView.findViewById(R.id.trade_price);
        //price_changesymbol=(ImageView)itemView.findViewById(R.id.);

        change_amount=(TextView)itemView.findViewById(R.id.change_amount);
        price_percent=(TextView)itemView.findViewById(R.id.price_percent);

    }
}
