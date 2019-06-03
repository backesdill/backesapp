package de.backesdill.helper;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;

import java.util.Arrays;
import java.util.zip.DataFormatException;

public class NetDB {
    // static member
    static NetDB sNetDB;
    // private member
    private NetworkModule    mNetMod;
    private ReceiverCallback mRcvCbNetMod;
    private BackesFestReceiverCallback  mBfRcvCb;
    private TemperatureReceiverCallback mTempRcvCb;
    private ListStorage      mConsoleOutput;
    private Handler          mHandler;
    private BackesFestData   mBfData;
    private TemperatureData  mTempData;
    private boolean          mBfDataIsValid = false;

    public NetDB( ) throws Exception{
        // allocate members
        mConsoleOutput = new ListStorage();
        mBfData = new BackesFestData();

        mConsoleOutput.add( "NetDB::NetDB() Version: 1.0");

        // create ReceiverCallback
        mRcvCbNetMod = new ReceiverCallback() {
            @Override
            public void onReceive(byte[] data, int size) {
                //mConsoleOutput.add(false, "NetDB::onReceive()");
                Message msg = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putByteArray("data",data);
                bundle.putInt("size", size);
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        };

        // create Handler for transmit data from network to ui thread
        mHandler = new Handler(Looper.getMainLooper()) {

            // receive messages in UI thread with handle Message
            @Override
            public void handleMessage(Message msg) {
                byte[] msgData;
                int msgSize;
                BackesNetworkPacket pkt;
                BackesFestData bfData;

                //mConsoleOutput.add(false, "NetDB::handleMessage()");

                msgData = msg.getData().getByteArray("data");
                msgSize = msg.getData().getInt("size");

                if ((msgSize < BackesNetworkPacket.HEADER_SIZE) ||
                        (msgSize > Byte.MAX_VALUE)) {
                    mConsoleOutput.add(false, "NetDB::handleMessage() invalid packet length " +
                            "(length: " + msgSize +
                            ", header size: " + BackesNetworkPacket.HEADER_SIZE +
                            ", max val: " + Byte.MAX_VALUE + ")");
                    return;
                }

                try {
                    // decode BackesNetworkPacket
                    pkt = receiveBackesNetworkPacket(msgData, msgSize);

                    if ((pkt.version != BackesNetworkPacket.VERSION_V1) ||
                            (pkt.version != BackesNetworkPacket.VERSION_V2)) {
                        mConsoleOutput.add(false, "NetDB::handleMessage() invalid packet version: " + pkt.version);
                    } else {

                        switch (pkt.cmd ){
                            case BackesNetworkPacket.CMD_REQUEST_BF_DB:
                                mConsoleOutput.add(false, "NetDB::handleMessage() CMD_REQUEST_BF_DB");
                                if (mBfDataIsValid == true) {
                                    publishBackesFestData(mBfData);
                                }
                                break;
                            case BackesNetworkPacket.CMD_PUBLISH_BF_DB:
                                mConsoleOutput.add(false, "NetDB::handleMessage() CMD_PUBLISH_BF_DB");

                                // new data received so local data is updated and valid
                                mBfDataIsValid = true;

                                if (pkt.payloadSize == BackesFestData.MAX_SIZE) {
                                    bfData = receiveBackesFestData(pkt.payloadData);

                                    // callback is set if there is a activity in foreground
                                    if (mBfRcvCb != null) {
                                        mBfRcvCb.onReceive(bfData);
                                    }
                                } else {
                                    mConsoleOutput.add(false, "NetDB::handleMessage() wrong BackesFestData size" + pkt.payloadSize);
                                }
                                break;
                            case BackesNetworkPacket.CMD_PUBLISH_TEMP:
                                mConsoleOutput.add(false, "NetDB::handleMessage() CMD_PUBLISH_TEMP");
                                if (pkt.payloadSize == TemperatureData.MAX_SIZE) {
                                    mTempData = receiveTemperatureData(pkt.payloadData);

                                    // callback is set if there is a activity in foreground
                                    if (mTempRcvCb != null) {
                                        mTempRcvCb.onReceive(mTempData);
                                    }
                                } else {
                                    mConsoleOutput.add(false, "NetDB::handleMessage() wrong TemperatureData size" + pkt.payloadSize);
                                }
                                break;
                            default:
                                mConsoleOutput.add(false, "NetDB::handleMessage() unkown command " + pkt.cmd);
                        }
                    }
                } catch (DataFormatException e) {
                    mConsoleOutput.add(true, "NetDB::handleMessage exception " + e);
                }
            }
        };


        // create NetworkModule
        mNetMod = new NetworkModule(mRcvCbNetMod);

    }


    public static NetDB getNetDB() throws Exception{
        if (sNetDB == null) {
                sNetDB = new NetDB();
        }
        // request the data over network
        sNetDB.requestBackesFestData();

        return sNetDB;
    }


    public void setBackesFestCb(BackesFestReceiverCallback rcvCb){
        mBfRcvCb = rcvCb;
    }

    public void resetBackesFestCb(){
        mBfRcvCb = null;
    }

    public void setTemperatureCb(TemperatureReceiverCallback rcvCb){
        mTempRcvCb = rcvCb;
    }

    public void resetTemperatureCb(){
        mTempRcvCb = null;
    }


    private void transmit(BackesNetworkPacket pkt){
        byte[] data = new byte[BackesNetworkPacket.MAX_PACKET_SIZE];

        // copy from packet to byte array
        // copy header
        data[BackesNetworkPacket.VERSION_OFFSET] = pkt.version;
        data[BackesNetworkPacket.CMD_OFFSET]     = pkt.cmd;
        data[BackesNetworkPacket.SIZE_OFFSET]    = pkt.payloadSize;

        // copy payload
        if (pkt.payloadSize > BackesNetworkPacket.MAX_PAYLOAD_SIZE) {
            mConsoleOutput.add(true, "NetDB::transmit wrong payload size");
            return;
        }

        if(pkt.payloadSize > 0) {
             System.arraycopy( pkt.payloadData, 0, data, BackesNetworkPacket.DATA_OFFSET, pkt.payloadSize);
        }

        // transmit data
        mNetMod.transmit(data, (byte) (BackesNetworkPacket.HEADER_SIZE +  pkt.payloadSize));
    }

    public void requestBackesFestData(){
        BackesNetworkPacket pkt = new BackesNetworkPacket();

        pkt.version     = BackesNetworkPacket.VERSION_V1;
        pkt.cmd         = BackesNetworkPacket.CMD_REQUEST_BF_DB;
        pkt.payloadSize = 0;

        mConsoleOutput.add(false, "NetDB::send CMD_REQUEST_BF_DB");

        mBfDataIsValid = false;
        transmit(pkt);
    }

    public BackesFestData getBackesFestData(){
        return mBfData;
    }

    public void publishBackesFestData(BackesFestData bfData){
        BackesNetworkPacket pkt = new BackesNetworkPacket();
        byte[] payloadData = new byte[2];

        // save data to local buffer
        mBfData.kirner = bfData.kirner;
        mBfData.bitburger = bfData.bitburger;

        // copy data to network packet
        payloadData[BackesFestData.KIRNER_OFFSET] = bfData.kirner;
        payloadData[BackesFestData.BITBURGER_OFFSET] = bfData.bitburger;

        pkt.version     = BackesNetworkPacket.VERSION_V1;
        pkt.cmd         = BackesNetworkPacket.CMD_PUBLISH_BF_DB;
        pkt.payloadSize = BackesFestData.MAX_SIZE;
        pkt.payloadData = payloadData;

        mConsoleOutput.add(false, "NetDB::send CMD_PUBLISH_BF_DB");

        // publish data so the data has to be valid
        mBfDataIsValid = true;

        transmit(pkt);
    }

    private BackesNetworkPacket receiveBackesNetworkPacket(byte[] data, int size) throws DataFormatException{
        BackesNetworkPacket pkt = new BackesNetworkPacket();
        byte[] payloadData;

        pkt.version     = data[BackesNetworkPacket.VERSION_OFFSET];
        pkt.cmd         = data[BackesNetworkPacket.CMD_OFFSET];
        pkt.payloadSize = data[BackesNetworkPacket.SIZE_OFFSET];

        // copy payload
        if ((BackesNetworkPacket.HEADER_SIZE + pkt.payloadSize) != size) {
            mConsoleOutput.add(true, "NetDB::receiveBackesNetworkPacket udp size and backesNetwork size are inconsistent."
                + "(backes packet size: " + BackesNetworkPacket.HEADER_SIZE + pkt.payloadSize
                + ", size: " + size +")");
            throw new DataFormatException();
        }

        // copy payload
        if (pkt.payloadSize > BackesNetworkPacket.MAX_PAYLOAD_SIZE) {
            mConsoleOutput.add(true, "NetDB::receiveBackesNetworkPacket wrong payload size");
            return pkt;
        }
        if (pkt.payloadSize > 0) {
            payloadData = Arrays.copyOfRange(data, BackesNetworkPacket.DATA_OFFSET, BackesNetworkPacket.DATA_OFFSET + pkt.payloadSize); // todo do not copy data / check this
            pkt.payloadData = payloadData;
        }

        return pkt;
    }

    private BackesFestData receiveBackesFestData(byte[] data){
        BackesFestData bfData = new BackesFestData();

        bfData.kirner = data[BackesFestData.KIRNER_OFFSET];
        bfData.bitburger = data[BackesFestData.BITBURGER_OFFSET];

        return bfData;
    }

    public void publishTemperatureData(byte[] tempData){
        BackesNetworkPacket pkt = new BackesNetworkPacket();

        pkt.version     = BackesNetworkPacket.VERSION_V2;
        pkt.cmd         = BackesNetworkPacket.CMD_PUBLISH_TEMP;
        pkt.payloadSize = TemperatureData.MAX_SIZE;
        pkt.payloadData = tempData;

        mConsoleOutput.add(false, "NetDB::send CMD_PUBLISH_TEMP");

        // publish data so the data has to be valid
        mBfDataIsValid = true;

        transmit(pkt);
    }


    private TemperatureData receiveTemperatureData(byte[] data){
        TemperatureData tempData= new TemperatureData();

        tempData.temperature = new String(data);

        return tempData;
    }
}
