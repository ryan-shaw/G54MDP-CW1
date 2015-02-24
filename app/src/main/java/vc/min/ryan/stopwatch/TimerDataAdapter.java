package vc.min.ryan.stopwatch;

import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ryan on 23/02/2015.
 */
public class TimerDataAdapter extends RecyclerView.Adapter<TimerDataAdapter.ViewHolder> {

    private List<TimerItem> mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView mTitle;
        public TextView mContent;
        public ViewHolder(View v){
            super(v);
            v.setOnClickListener(this);
            mTitle = (TextView) v.findViewById(R.id.title);
            mContent = (TextView) v.findViewById(R.id.time);
        }

        @Override
        public void onClick(View view){
            //TODO: Change activity to TimerActivity


        }
    }

    public TimerDataAdapter(List<TimerItem> dataset){
        mDataset = dataset;
    }

    @Override
    public TimerDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.timer_text_view, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
//        holder.mTextView.setText(mDataset[position]);
//        holder.mContent.setText(""+ mDataset.get(position).time);
        holder.mTitle.setText(mDataset.get(position).getFormattedTime());

    }

    public int getItemCount(){
        return mDataset.size();
    }

    public void updateData(List<TimerItem> newItems){
        mDataset = newItems;
    }
}
