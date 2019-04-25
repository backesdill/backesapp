package de.backesdill.backesapp.Helper;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Tahl on 30.03.2018.
 */

public class Methods {
    //Button for inserting value into preferences
    public void setListenerEditor(Button button, final String key, final String method, final TextView textView, final SharedPreferences pref) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditor(key, method, textView, pref);
            }
        });
    }

    private void setEditor(String key, String method, TextView textView, SharedPreferences pref) {
        int count = pref.getInt(key, 0);
        SharedPreferences.Editor editor = pref.edit();
        if(method.equals("add") && count < 20) {
            count += 1;
        } else if (method.equals("reduce") && count > 0) {
            count -= 1;
        }
        textView.setText(String.valueOf(count));
        editor.putInt(key, count);
        editor.apply();
    }

    // for Buttons to delete everything in selected preferences
    public void setListenerDeleteAll(Button button, final SharedPreferences pref) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pref.edit().clear().commit();
            }
        });
    }

    // deletes an Entry in SharedPreferences
    public void setListenerDeleteEntryTV(Button button, final SharedPreferences pref, final TextView textView) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // count + beername aus dem editText
                String key = "count" + textView.getText();
                // wegen :\t am Ende der TextViews
                String clearKey = key.substring(0, key.length() - 2);
                pref.edit().remove(clearKey).commit();
            }
        });
    }

    public void setListenerDeleteEntryET(Button button, final SharedPreferences pref, final EditText editText) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // count + beername aus dem editText
                String key = editText.getText().toString();
                // wegen :\t am Ende der TextViews
                //String clearKey = key.substring(0, key.length() - 1);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt(key, 0);
                editor.apply();
            }
        });
    }

    public void initEntryPreferences(String key, SharedPreferences pref) {
        if(pref.getInt(key, 0) == 0) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt(key, 0);
            editor.apply();
        }

    }

}
