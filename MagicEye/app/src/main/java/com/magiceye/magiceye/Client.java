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
    public boolean liveFeed=true;

    Client() {

    }

    public void run(){
        serverName = MainActivity.jIP.getText().toString();
        try {
            while(liveFeed) {
                socket = new Socket(serverName, port);
                in = socket.getInputStream();
                out = socket.getOutputStream();

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
