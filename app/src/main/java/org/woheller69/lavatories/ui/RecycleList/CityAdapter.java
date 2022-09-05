package org.woheller69.lavatories.ui.RecycleList;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.woheller69.lavatories.R;
import org.woheller69.lavatories.activities.LavSeekerActivity;
import org.woheller69.lavatories.database.Lavatory;
import org.woheller69.lavatories.database.SQLiteHelper;
import org.woheller69.lavatories.ui.viewPager.CityPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {

    private int[] dataSetTypes;
    private List<Lavatory> lavatoryList;
    private int cityID;
    private Context context;

    public static final int OVERVIEW = 0;
    public static final int DETAILS = 1;
    public static final int LAVATORIES = 2;

//Adapter for CityFragment
    public CityAdapter(int cityID, int[] dataSetTypes, Context context) {

        this.dataSetTypes = dataSetTypes;
        this.context = context;
        this.cityID = cityID;
        SQLiteHelper database = SQLiteHelper.getInstance(context.getApplicationContext());

        List<Lavatory> lavatories = database.getLavatoriesByCityId(cityID);

        updateLavatoriesData(lavatories);

    }

    public void updateLavatoriesData(List<Lavatory> lavatories) {

        lavatoryList = new ArrayList<>();
        lavatoryList.addAll(lavatories);

            notifyDataSetChanged();
    }



    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View v) {
            super(v);
        }
    }

    public class OverViewHolder extends ViewHolder {

        OverViewHolder(View v) {
            super(v);
        }
    }

    public class DetailViewHolder extends ViewHolder {

        DetailViewHolder(View v) {
            super(v);

        }
    }



    public class LavatoryViewHolder extends ViewHolder {
        RecyclerView recyclerView;
        TextView recyclerViewHeader;

        LavatoryViewHolder(View v) {
            super(v);
            recyclerView = v.findViewById(R.id.recycler_view_lavatories);
            recyclerView.setHasFixedSize(false);
            recyclerViewHeader=v.findViewById(R.id.recycler_view_header);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (!recyclerView.canScrollVertically(-1)){
                        recyclerView.setOnTouchListener(new OnSwipeDownListener(context) {
                            public void onSwipeDown() {
                                CityPagerAdapter.refreshSingleData(context,cityID);
                                LavSeekerActivity.startRefreshAnimation();
                            }
                        });
                    }else recyclerView.setOnTouchListener(null);
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        if (viewType == OVERVIEW) {
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.card_overview, viewGroup, false);

            return new OverViewHolder(v);

        } else if (viewType == DETAILS) {

            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.card_details, viewGroup, false);
            return new DetailViewHolder(v);

        }  else  {

            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.card_lavatory, viewGroup, false);
            return new LavatoryViewHolder(v);

        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        if (viewHolder.getItemViewType() == OVERVIEW) {
            OverViewHolder holder = (OverViewHolder) viewHolder;


        } else if (viewHolder.getItemViewType() == DETAILS) {

            DetailViewHolder holder = (DetailViewHolder) viewHolder;


        }  else if (viewHolder.getItemViewType() == LAVATORIES) {

            LavatoryViewHolder holder = (LavatoryViewHolder) viewHolder;
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            holder.recyclerView.setLayoutManager(layoutManager);
            holder.recyclerView.addItemDecoration(new DividerItemDecoration(holder.recyclerView.getContext(), DividerItemDecoration.VERTICAL));
            LavatoriesAdapter adapter = new LavatoriesAdapter(lavatoryList, context, holder.recyclerViewHeader, holder.recyclerView);
            holder.recyclerView.setAdapter(adapter);
            holder.recyclerView.setFocusable(false);
            holder.recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, holder.recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    String loc = lavatoryList.get(position).getLatitude() + "," + lavatoryList.get(position).getLongitude();
                    try {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("geo:" + loc + "?q=" + loc)));
                    } catch (ActivityNotFoundException ignored) {
                        Toast.makeText(context,R.string.error_no_map_app, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onLongItemClick(View view, int position) {

                }
            }));
        }
        //No update for error needed
    }

    @Override
    public int getItemCount() {
        return dataSetTypes.length;
    }

    @Override
    public int getItemViewType(int position) {
        return dataSetTypes[position];
    }
}