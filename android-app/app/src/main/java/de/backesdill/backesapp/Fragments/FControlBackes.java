package de.backesdill.backesapp.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import de.backesdill.backesapp.R;
import de.backesdill.helper.BackesFestData;
import de.backesdill.helper.BackesFestReceiverCallback;
import de.backesdill.helper.ListStorage;
import de.backesdill.helper.NetDB;

public class FControlBackes extends Fragment{
    private ListStorage mConsoleOutput;
    private NetDB mNetDB;
    private TextView tvBitburgerCount;
    private TextView tvKirnerCount;
    private BackesFestData mBfData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_backesfest_control, container, false);
        return view;
    }
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        //Beer1
        tvBitburgerCount = view.findViewById(R.id.countBeer1);

        //count = pref.getInt(beerKey, 0);
        tvKirnerCount = view.findViewById(R.id.countBeer2);


        final Button buttonAddBeer1 = view.findViewById(R.id.buttonAddBeer1);
        buttonAddBeer1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mBfData.bitburger++;
                updateGui();
                mNetDB.publishBackesFestData(mBfData);
            }
        });

        final Button buttonRedBeer1 = view.findViewById(R.id.buttonRedBeer1);
        buttonRedBeer1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mBfData.bitburger--;
                updateGui();
                mNetDB.publishBackesFestData(mBfData);
            }
        });

        final Button buttonAddBeer2 = view.findViewById(R.id.buttonAddBeer2);
        buttonAddBeer2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mBfData.kirner++;
                updateGui();
                mNetDB.publishBackesFestData(mBfData);
            }
        });

        final Button buttonRedBeer2 = view.findViewById(R.id.buttonRedBeer2);
        buttonRedBeer2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mBfData.kirner--;
                updateGui();
                mNetDB.publishBackesFestData(mBfData);
            }
        });

        final Button btnPublish = view.findViewById(R.id.btnPublish);
        btnPublish.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mNetDB.publishBackesFestData(mBfData);
            }
        });

        final Button btnRequest = view.findViewById(R.id.btnRequest);
        btnRequest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mNetDB.requestBackesFestData();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceStat) {
        super.onActivityCreated(savedInstanceStat);
        mConsoleOutput = new ListStorage();
        mConsoleOutput.add("FPfingste on ActivityCreated");
    }


    @Override
    public void onStart() {
        super.onStart();

        mBfData = new BackesFestData();
        try {
            mNetDB = NetDB.getNetDB();
        } catch (Exception e){
            mConsoleOutput.add("FControlBackes getNetDB() exception " + e);
        }

        mNetDB.setBackesFestCb(new BackesFestReceiverCallback() {
            @Override
            public void onReceive(BackesFestData bfData) {
                mConsoleOutput.add("FPfingste onReceive()");

                mBfData.kirner    = bfData.kirner;
                mBfData.bitburger = bfData.bitburger;

                updateGui();
            }
        });
        mBfData = mNetDB.getBackesFestData();
        updateGui();
    }

    @Override
    public void onStop() {
        super.onStop();

        mNetDB.resetBackesFestCb();
    }


    private void updateGui(){
        tvBitburgerCount.setText(Integer.toString(mBfData.bitburger ));
        tvKirnerCount.setText(Integer.toString(mBfData.kirner ));
    }
}
