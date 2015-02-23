package vc.min.ryan.stopwatch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private ImageButton fabAdd;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Context context;
    private GUIUtils guiUtils;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        guiUtils = new GUIUtils();
        fabAdd = (ImageButton) findViewById(R.id.add_button);
        guiUtils.configureFab(fabAdd);
        fabAdd.setOnClickListener(addOnClickListener);

        // Setup list
        mRecyclerView = (RecyclerView) findViewById(R.id.timers_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
//        String[] dataset = {"test", "test1"};
        List<TimerItem> dataset = new ArrayList<TimerItem>();
        dataset.add(new TimerItem(123));
        mAdapter = new TimerDataAdapter(dataset);
        mRecyclerView.setAdapter(mAdapter);

    }

    View.OnClickListener addOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view){
            Intent intent = new Intent(context, TimerActivity.class);
            startActivity(intent);
        }
    };
}