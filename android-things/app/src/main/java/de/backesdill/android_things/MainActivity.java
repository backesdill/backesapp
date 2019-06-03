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

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
    // debug
    private ListStorage mConsoleOutput;
    // network
    private NetDB mNetDB;
    // backesfest
    private TextView tvBitburgerCount;
    private TextView tvKirnerCount;
    private BackesFestData mBfData;
    // gpio
    private static final String GPIO_NAME = "BCM21";
    private Gpio gpio;
    // I2C
    private static final String I2C_DEVICE_NAME  = "I2C1";
    private static final int I2C_ADDRESS = 0x10;
    private I2cDevice mDevice;


    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mConsoleOutput = new ListStorage();
        findGpioPorts();
        //gpioInit();
        findI2CPorts();
        i2cInit();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mConsoleOutput.add(false,"onStart");
        getActionBar().setTitle("Backes-Fest II - Bieranzeige");

        tvBitburgerCount = findViewById(R.id.tvBitburgerCount);
        tvKirnerCount = findViewById(R.id.tvKirnerCount);

        /*Handler handler = new Handler();
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
        }, 10000);*/
/*
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
        }, 10000);*/

        //gpioDoSomething();

        i2cDoSomething();

        mConsoleOutput.add(false,"onStart finished");

    }

    @Override
    public void onStop() {
        super.onStop();
        mConsoleOutput.add(false,"onStop");
        //mNetDB.resetBackesFestCb();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mConsoleOutput.add(false,"onDestroy");
        //gpioDeInit();
        i2cDeInit();

    }


    private void findGpioPorts(){
        PeripheralManager manager = PeripheralManager.getInstance();
        List<String> portList = manager.getGpioList();
        if (portList.isEmpty()) {
            mConsoleOutput.add(true , "No GPIO port available on this device.");
        } else {
            mConsoleOutput.add(false, "List of available ports: " + portList);
        }
    }


    private void gpioInit(){

        // Attempt to access the GPIO
        try {
            PeripheralManager manager = PeripheralManager.getInstance();
            gpio = manager.openGpio(GPIO_NAME);
        } catch (IOException e) {
            mConsoleOutput.add( true, "Unable to access GPIO" + e);
        }
    }

    private void gpioDeInit(){

        if (gpio != null) {
            try {
                gpio.close();
                gpio = null;
            } catch (IOException e) {
                mConsoleOutput.add(false, "Unable to close GPIO" + e);
            }
        }
    }

    private void gpioDoSomething(){
        try {
            // Initialize the pin as a high output
            gpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
            // Low voltage is considered active
            gpio.setActiveType(Gpio.ACTIVE_HIGH);

            // Toggle the value to be LOW
            gpio.setValue(true);
        } catch (IOException e) {
            mConsoleOutput.add(false, "gpioDoSomething: Unable to set GPIO" + e);
        }
    }

    private void findI2CPorts(){
        PeripheralManager manager = PeripheralManager.getInstance();
        List<String> deviceList = manager.getI2cBusList();
        if (deviceList.isEmpty()) {
            mConsoleOutput.add(true, "No I2C bus available on this device.");
        } else {
            mConsoleOutput.add(false, "List of available devices: " + deviceList);
        }
    }

    private void i2cInit(){

        // Attempt to access the I2C device
        try {
            PeripheralManager manager = PeripheralManager.getInstance();
            mDevice = manager.openI2cDevice(I2C_DEVICE_NAME, I2C_ADDRESS);
        } catch (IOException e) {
            mConsoleOutput.add( true, "Unable to access I2C device " + e);
        }
    }

    private void i2cDeInit(){
        if (mDevice != null) {
            try {
                mDevice.close();
                mDevice = null;
            } catch (IOException e) {
                mConsoleOutput.add( true, "Unable to close I2C device"+ e);
            }
        }

    }

    private void i2cDoSomething(){
        byte[] buffer = new byte[6];


        try {
            mDevice.read( buffer, 6);
            mConsoleOutput.add(false, "i2cDoSomething: " + Arrays.toString(buffer));
            mConsoleOutput.add(false, "i2cDoSomething: " + new String(buffer));
        } catch (IOException e) {
            mConsoleOutput.add(false, "i2cDoSomething: Unable to read. " + e);
        }
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
