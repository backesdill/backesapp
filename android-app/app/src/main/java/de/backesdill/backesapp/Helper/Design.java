package de.backesdill.backesapp.Helper;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * Created by Tahl on 29.03.2018.
 */

public class Design {
    //creates a TextView with bold Text
    public static TextView createTextViewBold(View view, String text, int textSize) {
        TextView textView = new TextView(view.getContext());
        textView.setText(text + "\t");
        textView.setTextSize(textSize);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        return textView;
    }

    public static ProgressBar boldProgressBar(View view, String value,ColorStateList color) {
        ProgressBar progressBar = new ProgressBar(view.getContext(), null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setProgressTintList(color);
        progressBar.setProgressBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
        progressBar.setProgress(Integer.parseInt(value) * 5);
        return progressBar;
    }
}
