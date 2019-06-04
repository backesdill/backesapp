package de.backesdill.helper;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tahl on 19.03.2018.
 */

public class ListStorage {
    static private List<String> sStorage;

    public ListStorage() {
        // grund fuer ArrayList --> erweitert sich automatisch und kann wie bei array auf einzelne Elemente zugreifen
        if (sStorage == null) {
            sStorage = new ArrayList<>();
        }
    }

    // if the informations come as a String
    public void add(boolean error, String input) {
        sStorage.add(input);

        // choose log level
        if (error){
            Log.e("BackesApp", input);

        } else {
            Log.d("BackesApp", input);
        }
    }

    // if the informations are saved in a String array
    public void fillList(String[] arrayInput) {
        for(int i = 0; i < arrayInput.length; i++) {
            sStorage.add(arrayInput[i]);
        }
    }

    // prints the ArrayList
    public String printList() {
        String ausgabe = "";
        for(int i = 0; i < sStorage.size(); i++)
        {
            ausgabe += (sStorage.get(i) + "\n");
        }
        return ausgabe;
    }




}