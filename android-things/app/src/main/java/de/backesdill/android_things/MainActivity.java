package de.backesdill.android_things;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.os.Handler;

import de.backesdill.helper.BackesFestData;
import de.backesdill.helper.BackesFestReceiverCallback;
import de.backesdill.helper.ListStorage;
import de.backesdill.helper.NetDB;

import android.content.Intent;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 * <p>
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class MainActivity extends Activity {
    private ListStorage mConsoleOutput;
    private NetDB mNetDB;
    private TextView tvBitburgerCount;
    private TextView tvKirnerCount;
    private BackesFestData mBfData;


    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mConsoleOutput = new ListStorage();
        mConsoleOutput.add(false,"MainActivity on ActivityCreated");
    }

    @Override
    protected void onStart() {
        super.onStart();

        getActionBar().setTitle("Backes-Fest II - Bieranzeige");

        tvBitburgerCount = findViewById(R.id.tvBitburgerCount);
        tvKirnerCount = findViewById(R.id.tvKirnerCount);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // create network module
                mBfData = new BackesFestData();
                try {
                    mNetDB = NetDB.getNetDB();
                } catch (Exception e) {
                    mConsoleOutput.add(false,"MainActivity getNetDB Exception " + e);
                    callFatalError();
                }

                if (mNetDB != null) {
                    mNetDB.setBackesFestCb(new BackesFestReceiverCallback() {
                        @Override
                        public void onReceive(BackesFestData bfData) {
                            mConsoleOutput.add("FDisplayBackesFest onReceive()");

                            mBfData.kirner = bfData.kirner;
                            mBfData.bitburger = bfData.bitburger;

                            updateGui();
                        }
                    });
                    mBfData = mNetDB.getBackesFestData();
                }
                updateGui();

            }
        }, 10000);

    }

    @Override
    public void onStop() {
        super.onStop();

        //mNetDB.resetBackesFestCb();
    }

    private void updateGui(){
        if (mBfData.bitburger != Integer.parseInt(tvBitburgerCount.getText().toString())){
            blinkyBill(tvBitburgerCount);
            tvBitburgerCount.setText(Integer.toString(mBfData.bitburger ));

        }
        if (mBfData.kirner != Integer.parseInt(tvKirnerCount.getText().toString())){
            blinkyBill(tvKirnerCount);
            tvKirnerCount.setText(Integer.toString(mBfData.kirner ));
        }
    }

    public void callFatalError() {
        Intent intent = new Intent(this, FATAL_ERROR.class);
        startActivity(intent);
    }

    private void blinkyBill(final TextView myTv) {
        myTv.setTextColor(Color.RED);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                myTv.setTextColor(Color.BLACK);
            }
        }, 500);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                myTv.setTextColor(Color.RED);
            }
        }, 1000);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                myTv.setTextColor(Color.BLACK);
            }
        }, 1500);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                myTv.setTextColor(Color.RED);
            }
        }, 2000);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                myTv.setTextColor(Color.BLACK);
            }
        }, 2500);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                myTv.setTextColor(Color.RED);
            }
        }, 3000);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                myTv.setTextColor(Color.BLACK);
            }
        }, 3500);
    }



}
