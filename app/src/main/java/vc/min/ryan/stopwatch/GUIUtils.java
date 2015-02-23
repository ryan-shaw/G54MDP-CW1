package vc.min.ryan.stopwatch;

import android.graphics.Outline;
import android.view.View;

/**
 * Created by Ryan on 23/02/2015.
 */
public class GUIUtils {
    public static void configureFab (View fabButton) {

        int fabSize = fabButton.getContext().getResources()
                .getDimensionPixelSize(R.dimen.fab_size);

        Outline fabOutLine = new Outline();
        fabOutLine.setOval(0, 0, fabSize, fabSize);
    }
}
