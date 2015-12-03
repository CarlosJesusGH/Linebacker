package com.cmsys.linebacker.util;

import android.view.View;
import android.widget.ProgressBar;

/**
 * Created by cj on 28/11/15.
 */
public class ViewUtils {

    public static void hideProgressBar(ProgressBar progressBar){
        if(progressBar.getVisibility() == View.VISIBLE)
            progressBar.setVisibility(View.GONE);
    }

}
