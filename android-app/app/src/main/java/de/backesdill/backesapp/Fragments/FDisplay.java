package de.backesdill.backesapp.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Map;

import de.backesdill.backesapp.Helper.Design;
import de.backesdill.backesapp.R;

import static de.backesdill.backesapp.R.color.colorBlue;
import static de.backesdill.backesapp.R.color.colorGreen;
import static de.backesdill.backesapp.R.color.colorPrimaryDark;
import static de.backesdill.backesapp.R.color.colorRed;
import static de.backesdill.backesapp.R.color.colorYellow;

/**
 * Created by Tahl on 26.03.2018.
 */

// TODO: formate the progressbar for better overview
public class FDisplay extends Fragment{
    SharedPreferences pref;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_display, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        pref = getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        int colorIndex = 0;
        ColorStateList color = ColorStateList.valueOf(Color.BLUE);

        Map<String, ?> allEntries = pref.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            switch(colorIndex) {
                case 0:
                    color = ColorStateList.valueOf(getResources().getColor(colorBlue));
                    break;
                case 1:
                    color = ColorStateList.valueOf(getResources().getColor(colorRed));
                    break;
                case 2:
                    color = ColorStateList.valueOf(getResources().getColor(colorYellow));
                    break;
                case 3:
                    color = ColorStateList.valueOf(getResources().getColor(colorGreen));
                    colorIndex = 0;
                    break;
                default:
                    color = ColorStateList.valueOf(Color.BLACK);
            }
            createDisplay(view, entry.getKey(), entry.getValue().toString(), color);
            colorIndex++;
        }

    }

    private void createDisplay(View view, String key, String value, ColorStateList color) {
        TableLayout taLa = (TableLayout) view.findViewById(R.id.displayTable);
        TableRow row = new TableRow(view.getContext());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
        String beerName = "";

        if(key.substring(0, 5).equals("count")) {
            beerName = key.substring(5);
        }

        final TextView beer = new TextView(view.getContext());
        beer.setText(beerName + ":\t");
        beer.setTextSize(20);
        beer.setTypeface(Typeface.DEFAULT_BOLD);

        final TextView numberOfBeers = new TextView(view.getContext());
        numberOfBeers.setText("\t" + value);
        numberOfBeers.setTextSize(20);
        numberOfBeers.setTypeface(Typeface.DEFAULT_BOLD);

        //Widget.ProgressBar.Horizontal
        ProgressBar beerProgress = Design.boldProgressBar(view, value, color);
        if (key.substring(0,5).equals("color")) {
            //beerProgress.setProgressTintList(color);
        }

        row.addView(beer);
        row.addView(beerProgress);
        row.addView(numberOfBeers);
        taLa.addView(row);
    }

}
