package vc.min.ryan.stopwatch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Ryan on 24/02/2015.
 */
public class LapDataAdapter extends ArrayAdapter<LapItem> {

    private Context context;
    private HashMap<LapItem, Integer> mIdMap = new HashMap<LapItem, Integer>();

    public LapDataAdapter(Context context, int viewId, List<LapItem> objects){
        super(context, viewId, objects);
        for(int i = 0; i < objects.size(); i++){
            mIdMap.put(objects.get(i), i);
        }
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        TextView text = (TextView) rowView.findViewById(android.R.id.text1);
        text.setText("Lap " + position + ": " + getItem(position).getFormattedTime());
        return rowView;
    }

}
