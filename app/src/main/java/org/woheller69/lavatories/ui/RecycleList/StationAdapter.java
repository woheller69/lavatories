package org.woheller69.lavatories.ui.RecycleList;

import android.content.Context;

import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    private RecyclerView recyclerView;
    private ImageView fav;

//Adapter for Stations recycler view
    StationAdapter(List<Station> stationList, Context context, TextView recyclerViewHeader, RecyclerView recyclerView) {
        this.context = context;
        this.stationList = stationList;
        this.recyclerViewHeader=recyclerViewHeader;
        this.recyclerView=recyclerView;
    }


    @Override
    public StationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_station, parent, false);
        return new StationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StationViewHolder holder, int position) {
        SharedPreferences prefManager = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

        if (stationList !=null && stationList.size()!=0 && stationList.get(0)!=null) {
            long time = stationList.get(0).getTimestamp();
            long zoneseconds = TimeZone.getDefault().getOffset(Instant.now().toEpochMilli()) / 1000L;
            long updateTime = ((time + zoneseconds) * 1000);
            recyclerViewHeader.setText(String.format("%s (%s)", context.getResources().getString(R.string.card_stations_heading), StringFormatUtils.formatTimeWithoutZone(context, updateTime)));
        }

        if (prefManager.getBoolean("prefBrands", false)) {  //if preferred brands are defined
            String[] brands = prefManager.getString("prefBrandsString", "").split(","); //read comma separated list
            for (String brand : brands) {
                if (stationList.get(position).getBrand().toLowerCase().contains(brand.toLowerCase().trim())) {
                    holder.fav.setVisibility(View.VISIBLE);
                    break;
                }
            }
        }

        holder.dist.setText(stationList.get(position).getDistance()+" km");
        holder.address.setText((stationList.get(position).getAddress1()+", "+stationList.get(position).getAddress2()).toUpperCase());

        if (!stationList.get(position).getBrand().trim().equals("")) holder.name.setText(stationList.get(position).getBrand().toUpperCase());
        else holder.name.setVisibility(View.GONE);

        if (!stationList.get(position).getName().trim().equals("")) holder.hours.setText(stationList.get(position).getName());
        else holder.hours.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return stationList.size();
    }

    class StationViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView hours;
        TextView dist;
        TextView address;
        ImageView fav;

        StationViewHolder(View itemView) {
            super(itemView);

            hours = itemView.findViewById(R.id.station_hours);
            name = itemView.findViewById(R.id.station_brand);
            dist = itemView.findViewById(R.id.station_dist);
            address = itemView.findViewById(R.id.station_address);
            fav = itemView.findViewById(R.id.station_fav);

        }
    }
}

