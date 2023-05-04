package org.woheller69.lavatories.ui.RecycleList;

import android.content.Context;

import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.woheller69.lavatories.R;
import org.woheller69.lavatories.database.Lavatory;
import org.woheller69.lavatories.ui.Help.StringFormatUtils;

import java.time.Instant;
import java.util.List;
import java.util.TimeZone;

//**
// * Created by yonjuni on 02.01.17.
// * Adapter for the horizontal listView for course of the day.
// */import java.util.List;

public class LavatoriesAdapter extends RecyclerView.Adapter<LavatoriesAdapter.LavatoryViewHolder> {

    private List<Lavatory> lavatoryList;
    private Context context;

//Adapter for Lavatories recycler view
    LavatoriesAdapter(List<Lavatory> lavatoryList, Context context) {
        this.context = context;
        this.lavatoryList = lavatoryList;
    }


    @Override
    public LavatoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_lavatory, parent, false);
        return new LavatoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LavatoryViewHolder holder, int position) {

        holder.dist.setText(lavatoryList.get(position).getDistance()+" km");
        SharedPreferences prefManager = PreferenceManager.getDefaultSharedPreferences(context);
        if (prefManager.getBoolean("pref_Debug",false)){
            holder.address.setText((lavatoryList.get(position).getAddress1()).toUpperCase()+"\nOSM_ID: N"+lavatoryList.get(position).getUuid());
        }else{
            holder.address.setText((lavatoryList.get(position).getAddress1()).toUpperCase());
        }


        if (!lavatoryList.get(position).getOperator().trim().equals("")) {
            holder.operator.setText(lavatoryList.get(position).getOperator().toUpperCase());
            holder.operator.setVisibility(View.VISIBLE);
        }
        else holder.operator.setVisibility(View.GONE);

        if (!lavatoryList.get(position).getOpeningHours().trim().equals("")) {
            holder.openingHours.setText(lavatoryList.get(position).getOpeningHours());
            holder.openingHours.setVisibility(View.VISIBLE);
        }
        else holder.openingHours.setVisibility(View.GONE);

        if (!lavatoryList.get(position).isPaid()) holder.paid.setImageIcon(null); else holder.paid.setImageResource(R.drawable.ic_paid_black_24dp);
        if (!lavatoryList.get(position).isWheelchair()) holder.wheelchair.setImageIcon(null); else holder.wheelchair.setImageResource(R.drawable.ic_accessible_black_24dp);
        if (!lavatoryList.get(position).isBabyChanging()) holder.babyChanging.setImageIcon(null); else holder.babyChanging.setImageResource(R.drawable.ic_baby_changing_station_black_24dp);
    }

    @Override
    public int getItemCount() {
        return lavatoryList.size();
    }

    class LavatoryViewHolder extends RecyclerView.ViewHolder {

        TextView operator;
        TextView openingHours;
        TextView dist;
        TextView address;
        ImageView wheelchair;
        ImageView babyChanging;
        ImageView paid;

        LavatoryViewHolder(View itemView) {
            super(itemView);

            openingHours = itemView.findViewById(R.id.lavatory_hours);
            operator = itemView.findViewById(R.id.lavatory_operator);
            dist = itemView.findViewById(R.id.lavatory_dist);
            address = itemView.findViewById(R.id.lavatory_address);
            wheelchair = itemView.findViewById(R.id.lavatory_wheelchair);
            babyChanging = itemView.findViewById(R.id.lavatory_baby_changing);
            paid = itemView.findViewById(R.id.lavatory_paid);
        }
    }
}

