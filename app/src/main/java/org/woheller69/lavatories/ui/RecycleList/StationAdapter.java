package org.woheller69.lavatories.ui.RecycleList;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.woheller69.lavatories.R;
import org.woheller69.lavatories.database.Station;
import org.woheller69.lavatories.ui.Help.StringFormatUtils;

import java.time.Instant;
import java.util.List;
import java.util.TimeZone;

//**
// * Created by yonjuni on 02.01.17.
// * Adapter for the horizontal listView for course of the day.
// */import java.util.List;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.StationViewHolder> {

    private List<Station> stationList;
    private Context context;
    private TextView recyclerViewHeader;

//Adapter for Stations recycler view
    StationAdapter(List<Station> stationList, Context context, TextView recyclerViewHeader, RecyclerView recyclerView) {
        this.context = context;
        this.stationList = stationList;
        this.recyclerViewHeader=recyclerViewHeader;
    }


    @Override
    public StationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_station, parent, false);
        return new StationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StationViewHolder holder, int position) {

        if (stationList !=null && stationList.size()!=0 && stationList.get(0)!=null) {
            long time = stationList.get(0).getTimestamp();
            long zoneseconds = TimeZone.getDefault().getOffset(Instant.now().toEpochMilli()) / 1000L;
            long updateTime = ((time + zoneseconds) * 1000);
            recyclerViewHeader.setText(String.format("%s (%s)", context.getResources().getString(R.string.card_stations_heading), StringFormatUtils.formatTimeWithoutZone(context, updateTime)));
        }

        holder.dist.setText(stationList.get(position).getDistance()+" km");
        holder.address.setText((stationList.get(position).getAddress1()+", "+stationList.get(position).getAddress2()).toUpperCase());

        if (!stationList.get(position).getOperator().trim().equals("")) holder.operator.setText(stationList.get(position).getOperator().toUpperCase());
        else holder.operator.setVisibility(View.GONE);

        if (!stationList.get(position).getName().trim().equals("")) holder.openingHours.setText(stationList.get(position).getName());
        else holder.openingHours.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return stationList.size();
    }

    class StationViewHolder extends RecyclerView.ViewHolder {

        TextView operator;
        TextView openingHours;
        TextView dist;
        TextView address;

        StationViewHolder(View itemView) {
            super(itemView);

            openingHours = itemView.findViewById(R.id.station_hours);
            operator = itemView.findViewById(R.id.station_brand);
            dist = itemView.findViewById(R.id.station_dist);
            address = itemView.findViewById(R.id.station_address);
        }
    }
}

