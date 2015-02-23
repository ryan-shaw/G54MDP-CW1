package vc.min.ryan.stopwatch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends Activity {

    private ImageButton fabAdd;

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
    }

    View.OnClickListener addOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view){
            Intent intent = new Intent(context, TimerActivity.class);
            startActivity(intent);
        }
    };
}