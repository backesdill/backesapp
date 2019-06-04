package de.backesdill.backesapp.Fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import de.backesdill.backesapp.R;
import de.backesdill.helper.ListStorage;
import de.backesdill.helper.NetDB;
import de.backesdill.helper.TemperatureData;
import de.backesdill.helper.TemperatureReceiverCallback;

/**
 * Created by Tahl on 30.03.2018.
 */

public class FPfingste extends Fragment {
    private static final String TAG = "FPfingste";
    private SharedPreferences pref;
    private ListStorage mConsoleOutput;
    private NetDB mNetDB;
    private TextView tvAnzeige_Temperatur;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private TemperatureData mTempData;

//    private double temperatur = 4.0;
    int blue = 255;
    int red = 255;
    double obergrenze = 40.0;
    double untergrenze = 0.0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pfingste, container, false);
        return view;
    }

    //TODO: muss noch ueberarbeitet werden
    //TODO: Name fuers anzeigen aus den preferences ziehen
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        tvAnzeige_Temperatur = view.findViewById(R.id.tvAnzeige_Temp);
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        // laesst die View wie eine Listview aussehen
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);
        //benoetigt Adapter um auf Daten zuzugreifen
        //       btnRequest.setOnClickListener(new View.OnClickListener() {
        //          public void onClick(View v) {
        //               mNetDB.requestBackesFestData();
        //          }
        //       });
        // Ohne hierdas laeuft der Thread nit.
        // Sollte aber fuer die finale Implementation nicht weiter schlimm sein, da man ja den gesendeten Wert bekommt
        //ExampleThread thread = new ExampleThread();
        //thread.start();
        tvAnzeige_Temperatur.setText("no data");
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceStat) {
        super.onActivityCreated(savedInstanceStat);
        mConsoleOutput = new ListStorage();
        mConsoleOutput.add(false, "FPfingste on ActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();

        mTempData = new TemperatureData();
        try {
            mNetDB = NetDB.getNetDB();
        } catch (Exception e){
            mConsoleOutput.add(true,"FPfingste getNetDB() exception " + e);
        }

        mNetDB.setTemperatureCb(new TemperatureReceiverCallback() {
            @Override
            public void onReceive(TemperatureData tempData) {
                mConsoleOutput.add(false, "FPfingste onReceive()");

                mTempData = tempData;

                updateGui();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();

        mNetDB.resetBackesFestCb();
    }


    private void updateGui(){
        mConsoleOutput.add(false, "Temp received: " + mTempData.temperature);
        float temp = Float.parseFloat(mTempData.temperature);
        tvAnzeige_Temperatur.setTextColor(Color.rgb((int)(red * (temp / obergrenze)), 0, (int)(blue * (1.0 - (temp / obergrenze)))));
        tvAnzeige_Temperatur.setText("" + mTempData.temperature + "°");
    }

    /*class ExampleThread extends Thread {
        double i;

        ExampleThread() {
            i = 0.0;
        }
        @Override
        public void run() {

            while (true) {
                tvAnzeige_Temperatur.setTextColor(Color.rgb((int)(red * (i / obergrenze)), 0, (int)(blue * (1.0 - (i / obergrenze)))));
                Log.d(TAG, "startThread: " + i);
                tvAnzeige_Temperatur.setText("" + i + "°");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i++;
                if (i > obergrenze) {
                    i = untergrenze;
                }

            }
        }
    }*/


}
