package de.backesdill.backesapp.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Map;

import de.backesdill.backesapp.Helper.Design;
import de.backesdill.backesapp.Helper.Methods;
import de.backesdill.backesapp.R;

/**
 * Created by Tahl on 24.03.2018.
 */

// TODO: Refresh after deleting something
public class FControl extends Fragment {

    private SharedPreferences pref;
    private Methods meth;
    @Override
    public void onActivityCreated(Bundle savedInstanceStat) {
        super.onActivityCreated(savedInstanceStat);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_control, container, false);
        return view;
    }


    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        meth = new Methods();
        //wenn preferences noch nicht existiert, wird es erstellt.
        //sonst greift er darauf zu.
        pref = getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String beerName = "";

        Map<String, ?> allEntries = pref.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            if(key.substring(0, 5).equals("count")) {
                beerName = key.substring(5);
                createTableRow(view, beerName, pref);
            }
        }

        //brauche onClickListener für den Button Hinzufuegen damit die Aktion ausgefuehrt wird
        Button addBeer =(Button) view.findViewById(R.id.addCategory);

        final EditText beerToAdd = (EditText) view.findViewById(R.id.beerToAdd);

        addBeer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meth.initEntryPreferences("count" + beerToAdd.getText().toString(), pref);
                createTableRow(view, beerToAdd.getText().toString(), pref);
            }
        });

        meth.setListenerDeleteAll((Button) view.findViewById(R.id.deleteAll), pref);
    }

    public final void createTableRow(View view, String beername, final SharedPreferences pref) {
        TableLayout taLa = (TableLayout) view.findViewById(R.id.controlTable);
        final String key = "count" + beername;
        final int count = pref.getInt(key, 0);
        TableRow row = new TableRow(view.getContext());

        final SharedPreferences.Editor editor = pref.edit();

        //Identifier for the Beername
        final TextView beer = Design.createTextViewBold(view, beername + ":", 20);

        //TextView for count --> setting here so reduce knows whats up
        final TextView countBeer = Design.createTextViewBold(view, "0", 20);
        if(count != 0) {
            countBeer.setText(String.valueOf(count));
        }

        //Setting reduceButton
        final Button reduce = new Button(view.getContext());
        reduce.setText("-");
        //In Fragments den onClickListener verwenden!!!
        meth.setListenerEditor(reduce, key, "reduce", countBeer, pref);

        //Setting addButton
        final Button add = new Button(view.getContext());
        add.setText("+");
        //In Fragments den onClickListener verwenden!!!
        meth.setListenerEditor(add, key, "add", countBeer, pref);

        //Setting clearPrefButton
        //TODO: Sicherheitsabfrage: wirklich alles loeschen?!
        final Button clearPref = new Button(view.getContext());
        clearPref.setText("Delete");
        meth.setListenerDeleteEntryTV(clearPref, pref, beer);

        //adding Viewelements here for better overView
        row.addView(beer);
        row.addView(clearPref);
        row.addView(reduce);
        row.addView(countBeer);
        row.addView(add);
        taLa.addView(row);
    }
}
