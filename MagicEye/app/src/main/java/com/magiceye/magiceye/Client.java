package com.magiceye.magiceye;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraManager;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mithileshhinge on 20/06/16.
 */
public class Client extends Thread {

    private String serverName;
    private Socket socket;
    private int port = 6666;
    private InputStream in;
    private OutputStream out;

    Client() {

    }

    public void run(){
        serverName = MainActivity.jIP.getText().toString();
        try {
            while(true) {
                socket = new Socket(serverName, port);
                in = socket.getInputStream();
                out = socket.getOutputStream();
                //out.write(0);
                //out.flush();
                //DataInputStream din = new DataInputStream(in);
                //DataOutputStream dout = new DataOutputStream(out);


                //while(true) {
                //byte[] frameBytesLenBytes = new byte[4];
                //in.read(frameBytesLenBytes);
                //Log.d("tobhi", Arrays.toString(frameBytesLenBytes));
                //ByteBuffer bb = ByteBuffer.allocate(4);
                //bb.put(frameBytesLenBytes);
                //int frameBytesLen = bb.getInt(0);
                //Log.d("debuggg", String.valueOf(frameBytesLen));
                //byte[] frameBytes =new byte[frameBytesLen];

                /*ByteArrayOutputStream bout = new ByteArrayOutputStream();
                Log.d("DEBUG2", "BLAHBLAH");
                //int off = 0;
                while (true) {
                    Log.d("DEBUG3", "BLAHBLAH");
                    //if (off >= frameBytesLen) break;
                    byte[] readBytes = new byte[8192];
                    //int lenRead = in.read(frameBytes, off, Math.min(8192, frameBytesLen-off));
                    int lenRead  = in.read(readBytes);
                    if (lenRead == -1) break;
                    bout.write(readBytes);
                    Log.d("DEBUG4", "BLAHBLAH");
                    //off += 8192;
                }

                byte[] frameBytes = bout.toByteArray();
                Log.d("frameBytesLength", String.valueOf(frameBytes.length));
                Log.d("frameBytes", Arrays.toString(frameBytes));
                MainActivity.frame = BitmapFactory.decodeByteArray(frameBytes, 257, frameBytes.length-257);
                MainActivity.frameChanged = true;*/
                MainActivity.frame = BitmapFactory.decodeStream(new FlushedInputStream(in));
                MainActivity.frameChanged = true;
                socket.close();
            }

            //}
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
