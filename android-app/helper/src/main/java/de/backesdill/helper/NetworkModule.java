package de.backesdill.helper;

import java.io.*;
import java.net.*;
import java.util.*;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by kurjon on 04.02.2018.
 */

public class NetworkModule {

    private ServerThread mServerThread;
    private ListStorage mConsoleOutput;

    private ReceiverCallback mReceiverCallback;

    public NetworkModule(ReceiverCallback rcvCb) throws Exception {
        mConsoleOutput = new ListStorage();
        mConsoleOutput.add(false, "NetworkModule() CTOR");

        if (rcvCb == null){
            mConsoleOutput.add(true, "Receiver callback is null");
            return;
        }

        mReceiverCallback = rcvCb;

        // create server thread
        mServerThread = new ServerThread();

        // start server thread
        mServerThread.start();
    }

    public void transmit(final byte[] data, byte size) {
        mServerThread.transmit(data, size);
    }

    private class ServerThread extends Thread {

        protected DatagramSocket socket = null;
        protected BufferedReader in = null;
        protected boolean        serverActive = true;
        private   int            port = 1338;
        private   InetAddress    localAddr;
        private   InetAddress    broadcastAddr;
        private   ListStorage    mConsoleOutput;
        private   ByteFIFO       txFifo;

        public ServerThread( ) throws Exception {
            super("ServerThreadSuper");

            mConsoleOutput = new ListStorage();
            mConsoleOutput.add(false, "ServerThread() CTOR");

            txFifo = new ByteFIFO(256);

           // get broadcast
            InterfaceAddress niAddr = getIPAddress();
            if (niAddr == null){
                mConsoleOutput.add(true, "IP Address is invalid!");
                throw new NullPointerException("niAddr is null");
            }

            // create socket
            socket = new DatagramSocket(port);
            if (socket == null){
                mConsoleOutput.add(true, "create DatagramSocket failed");
                throw new NullPointerException("socket is null");
            }

            broadcastAddr = niAddr.getBroadcast();
            localAddr = niAddr.getAddress();
        }

        public void transmit(final byte[] data, final byte size) {
            Thread  send;

            //mConsoleOutput.add( "ServerThread::transmit()");
            //mConsoleOutput.add( "ServerThread::transmit() "
            //+ "data.length " + data.length + " size " +size);

            try {
                txFifo.add(data, size);
            } catch (InterruptedException e) {
                mConsoleOutput.add(true, " txFifo.add InterruptedException " + e);
            }

            send = new Thread("send_thread") {
                public void run() {
                    DatagramPacket packet;
                    byte[] data;


                    data = txFifo.removeAll();

                    //mConsoleOutput.add( "SendThread::run() socket.send");

                    packet = new DatagramPacket(data, data.length, broadcastAddr, port);
                    try {
                        socket.send(packet);
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                        mConsoleOutput.add(true, " socket.send(packet) IOException " + e);
                    }
                }
            };

            send.start();
        }

        public void run() {
            byte[] buf = new byte[256];
            DatagramPacket packet;

            while (serverActive) {
                try {
                    //mConsoleOutput.add(false, "serverThread::run() => ready to receive");

                    // receive request
                    packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);


                    if (!localAddr.equals(packet.getAddress())){
                        mConsoleOutput.add(false, "serverThread::run() => packet received");
                        mConsoleOutput.add(false, "serverThread::run() size: "
                                + packet.getLength()+ " data: "
                                + Arrays.toString(Arrays.copyOf(packet.getData(), packet.getLength())));
                       // mConsoleOutput.add(false, "serverThread::run() Rcv Address: " + packet.getAddress().toString());

                        // send message
                        mReceiverCallback.onReceive(packet.getData(), packet.getLength());
                    }

                    // send the response to the client at "address" and "port"
                    //packet = new DatagramPacket(buf, buf.length, broadcastAddr, port);
                    //socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                    serverActive = false;
                }
            }

            mConsoleOutput.add(false, "Close Socket ");
            socket.close();
        }

        /**
         * Get IP address from first non-localhost interface
         * @return  returns local address or null
         */
        InterfaceAddress getIPAddress() throws Exception{
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());

            if (interfaces == null){
                mConsoleOutput.add(true, "getIPAddress interfaces == null: " );
                return null;
            }

            // print all network interfaces
            String listOfNis = "";

            for (NetworkInterface intf : interfaces) {
                listOfNis += intf.getName() + ", ";
            }

            mConsoleOutput.add(false, "Network Interfaces: " + listOfNis);

            for (NetworkInterface intf : interfaces) {
                if (intf.getName().contains("wlan")) {
                    mConsoleOutput.add(false, "Using NI: " + intf.getName());
                    for (InterfaceAddress addr : intf.getInterfaceAddresses()) {
                        if (!addr.getAddress().isLoopbackAddress()) {
                            String sAddr = addr.getAddress().getHostAddress();
                            //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                            boolean isIPv4 = sAddr.indexOf(':') < 0;

                            if (isIPv4) {
                                mConsoleOutput.add(false, "Using Address: " + addr.toString());
                                return addr;
                            }
                        }
                    }
                } else {
                    mConsoleOutput.add(true, "lan not found ");
                }
            }


            return null;
        }
    }

}
