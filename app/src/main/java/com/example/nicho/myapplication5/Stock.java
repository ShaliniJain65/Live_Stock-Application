package com.example.nicho.myapplication5;

import android.support.annotation.NonNull;

import java.util.Comparator;

/**
 * Created by Shalini on 2/27/2018.
 */
//Contains getters and setters for all the Stock variables
public class Stock implements Comparator<String> {

    private String stock_symbol;
    private String company_name;
    private double trade_price;
    //private double price_changesymbol;
    private double change_amount;
    private double price_percent;
    private int counter=1;

    public String getStock_symbol() {
        return stock_symbol;
    }

    public void setStock_symbol(String stock_symbol) {
        this.stock_symbol = stock_symbol;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public double getTrade_price() {
        return trade_price;
    }

    public void setTrade_price(double trade_price) {
        this.trade_price = trade_price;
    }

    public double getChange_amount() {
        return change_amount;
    }

    public void setChange_amount(double change_amount) {
        this.change_amount = change_amount;
    }

    public double getPrice_percent() {
        return price_percent;
    }

    public void setPrice_percent(double price_percent) {
        this.price_percent = price_percent;
    }

    @Override
    public int compare(String s, String t1) {
        return s.compareTo(t1);
    }
}
