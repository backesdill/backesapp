package de.backesdill.backesapp.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.text.method.ScrollingMovementMethod;

import de.backesdill.helper.ListStorage;
import de.backesdill.helper.NetDB;
import de.backesdill.backesapp.R;

/**
 * Created by Tahl on 26.03.2018.
 */

public class FDebug extends Fragment{
    //setting textview
    private ListStorage mConsoleOutput;
    private TextView tv_debug;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_debug, container, false);

        tv_debug = (TextView) view.findViewById(R.id.tv_debug);
        tv_debug.setMovementMethod(new ScrollingMovementMethod());

        final Button btn_refresh = view.findViewById(R.id.btn_refresh);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tv_debug.setText(mConsoleOutput.printList());
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // initialize view elements after setContentView
        mConsoleOutput = new ListStorage();

        // create network module
        tv_debug.setText(mConsoleOutput.printList());

    }
}
