package de.backesdill.android_things;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import de.backesdill.helper.ListStorage;

public class FATAL_ERROR extends Activity {
    private ListStorage mConsoleOutput;
    TextView tvErrorLog;

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }


    @Override
    protected void onStart() {
        super.onStart();

        setContentView(R.layout.activity_fatal_error);

        mConsoleOutput = new ListStorage();
        mConsoleOutput.add(false,"FatalErrorActivity onStart");

        tvErrorLog = findViewById(R.id.tvErrorLog);

        mConsoleOutput = new ListStorage();
        tvErrorLog.setText(mConsoleOutput.printList());

    }
}
