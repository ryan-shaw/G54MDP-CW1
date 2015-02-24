package vc.min.ryan.stopwatch;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Ryan on 24/02/2015.
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView mTitle;
    public TextView mContent;
    private ClickListener clickListener;
    public RecyclerViewHolder(View v){
        super(v);
        mTitle = (TextView) v.findViewById(R.id.title);
        mContent = (TextView) v.findViewById(R.id.time);
        v.setOnClickListener(this);
    }

    public interface ClickListener {
        public void onClick(View v, int position, boolean isLongClick);
    }

    public void setClickListener(ClickListener clickListener){
        this.clickListener = clickListener;
    }

    @Override
    public void onClick(View v){
        clickListener.onClick(v, getPosition(), false);
    }

}

