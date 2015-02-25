package vc.min.ryan.stopwatch;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ryan on 23/02/2015.
 */
public class TimerDataAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private List<TimerItem> mDataset;
    private Context mContext;

    public TimerDataAdapter(List<TimerItem> dataset, Context context){
        mDataset = dataset;
        mContext= context;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.timer_text_view, parent, false);
        RecyclerViewHolder vh = new RecyclerViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position){
        holder.mTitle.setText(mDataset.get(position).getFormattedTime());
        holder.setClickListener(new RecyclerViewHolder.ClickListener() {
            @Override
            public void onClick(View v, int position, boolean isLongClick) {
                Log.d("TimerDataAdapter", "Click: " + position);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) mContext, (TextView) v.findViewById(R.id.title), "timer");
                Intent intent = new Intent(mContext, TimerActivity.class);
                intent.putExtra("timerId", mDataset.get(position).getId());
                mContext.startActivity(intent, options.toBundle());
            }
        });
    }

    public int getItemCount(){
        return mDataset.size();
    }

    public void updateData(List<TimerItem> newItems){
        mDataset = newItems;
    }

    public List<TimerItem> getData(){
        return mDataset;
    }
}
