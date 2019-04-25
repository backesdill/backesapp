package de.backesdill.backesapp.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Map;

import de.backesdill.backesapp.R;
import de.backesdill.helper.BackesFestData;
import de.backesdill.helper.BackesFestReceiverCallback;
import de.backesdill.helper.ListStorage;
import de.backesdill.helper.NetDB;

/**
 * Created by Tahl on 30.03.2018.
 */

public class FDisplayBackesFest extends Fragment{
    private SharedPreferences pref;
    private ListStorage mConsoleOutput;
    private NetDB mNetDB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_backesfest_display, container, false);
        return view;
    }
    //TODO: muss noch ueberarbeitet werden
    //TODO: Name fuers anzeigen aus den preferences ziehen
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        pref = getActivity().getSharedPreferences("preferencesBackes", Context.MODE_PRIVATE);
        String count = "0";
        String clearKey = " ";
        TextView tvBeer1 = view.findViewById(R.id.tvBitburger);
        ProgressBar pBBeer1 = view.findViewById(R.id.progressBeer1);
        TextView tvBeer2 = view.findViewById(R.id.tvKirner);
        ProgressBar pBBeer2 = view.findViewById(R.id.progressBeer2);

        Map<String, ?> allEntries = pref.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            clearKey = entry.getKey();
            switch(clearKey.substring(0, 1)) {
                case "1": // musste tvBeer1.getText().toString sein --> die Anzeige muesste noch dementsprechend angepasst werden, das oben im display auch die Biere aus dem pref angezeigt werden
                    count = entry.getValue().toString();
                    pBBeer1.setProgress(Integer.parseInt(count) * 5);
                    break;
                case "2":
                    count = entry.getValue().toString();
                    //count = pref.getInt(entry.getKey(), 0);
                    pBBeer2.setProgress(Integer.parseInt(count) * 5);
                    break;
                default:
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceStat) {
        super.onActivityCreated(savedInstanceStat);
        mConsoleOutput = new ListStorage();
        mConsoleOutput.add("FDisplayBackesFest on ActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            mNetDB = NetDB.getNetDB();
        } catch (Exception e){
            mConsoleOutput.add("FDisplayBackesFest getNetDB() exception " + e);
        }

        mNetDB.setBackesFestCb(new BackesFestReceiverCallback() {
            @Override
            public void onReceive(BackesFestData bfData) {
                mConsoleOutput.add("FDisplayBackesFest onReceive()");
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();

        mNetDB.resetBackesFestCb();
    }

}
