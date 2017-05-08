package com.example.ericpeng.drawertest;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.support.v7.widget.PopupMenu;
import android.util.Log;
import java.util.Date;
import java.text.*;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;

import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Set;

public class RecordActivity extends Activity implements OnClickListener, PopupMenu.OnMenuItemClickListener {

    private BluetoothAdapter BA;
    private Set<BluetoothDevice>pairedDevices;
    ListView lv;
    TextView data;
    TextView history;

    Handler bluetoothIn;

    boolean clicked = false;
    boolean firstTime = true;

    public volatile boolean stopThread = false;

    final int handlerState = 0;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();

    private ConnectedThread mConnectedThread;
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static String address;

    String currentText, historyText;

    Button start;
    Button stop;
    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        start = (Button)findViewById(R.id.start_button);
        stop = (Button)findViewById(R.id.stop_button);
        save = (Button)findViewById(R.id.save_button);

        start.setOnClickListener(this);
        stop.setOnClickListener(this);

        BA = BluetoothAdapter.getDefaultAdapter();

        //data = (TextView)findViewById(R.id.data);
        history = (TextView)findViewById(R.id.history);

        //data.setVisibility(View.INVISIBLE);
        history.setVisibility(View.INVISIBLE);

        start.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopThread = false;
                writeMessage();
            }
        });
        //writeMessage();
        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                //data.setText("");
                if (msg.what == handlerState) {
                    String readMessage = (String) msg.obj;

                    recDataString.append(readMessage);
                    int value = recDataString.toString().lastIndexOf('.');
                    String voltage = recDataString.substring(value - 1, recDataString.toString().length()-1);

                    voltage = voltage.trim().replaceAll("\n ", "");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy | HH:mm:ss");
                    String currentTime  = dateFormat.format(new Date());

                    if (!stopThread) {
                        currentText = "Voltage = " + voltage + " V";
                        historyText = currentTime + " (" + voltage + " V)\n" + history.getText().toString();
                    }
                }
                //data.setMovementMethod(new ScrollingMovementMethod());
                history.setMovementMethod(new ScrollingMovementMethod());

                //data.setText(currentText);
                history.setText(historyText);
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();

        stop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopThread = true;
                firstTime = false;
            }
        });

        save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(RecordActivity.this, save);
                //popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
                popup.setOnMenuItemClickListener(RecordActivity.this);
                popup.inflate(R.menu.popup);
                popup.show();

            }
        });
    }

    public boolean onMenuItemClick(MenuItem item){
        String text = history.getText().toString();
        switch (item.getItemId()){
            case R.id.email_save:
                Intent email_intent = new Intent(Intent.ACTION_SEND);
                email_intent.setType("*/*");

                email_intent.putExtra(android.content.Intent.EXTRA_TEXT, text);
                startActivity(email_intent);
                return true;

            case R.id.dropbox_save:
                Uri uri = Uri.parse("http://dropbox.com/");
                Intent db_intent = new Intent(Intent.ACTION_VIEW, uri);

                String filename = "data";
                FileOutputStream outputStream;

                try {
                    outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                    outputStream.write(text.getBytes());
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                startActivity(db_intent);
                return true;

            case R.id.delete_data:
                //Toast.makeText(this, "Delete Clicked", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.yes_delete:
                //data.setText("");
                //history.setText("");
                firstTime = true;
                finish();
                startActivity(getIntent());
                //Toast.makeText(this, "Yes Clicked", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.no_delete:
                //Toast.makeText(this, "No Clicked", Toast.LENGTH_SHORT).show();
                return true;
        }
        //Toast.makeText(RecordActivity.this,"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();
        return true;
    }

    public void writeMessage(){
        if (firstTime){
            //data.setText("");
            history.setText("");
        }
        //data.setVisibility(View.VISIBLE);
        history.setVisibility(View.VISIBLE);

    }

    public void onClick(View view){
        if(view.equals(save)){
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = getIntent();

        address = "98:D3:37:00:98:0B";

        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                Toast.makeText(getBaseContext(), "Connection Closed", Toast.LENGTH_LONG).show();

            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
        //mConnectedThread.write("x");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mConnectedThread.cancel();
        try{
            btSocket.close();
        } catch (IOException e2) {
        }
    }

    private void checkBTState() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }


    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }
            System.out.println("INTERRUPTED Click");
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void cancel() {

        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            while (!interrupted()) {
                try {
                    bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }

            }
        }

        public void write(String input) {
            byte[] msgBuffer = input.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    public void on(View v){
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Turned on",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_LONG).show();
        }
    }

    public void off(View v){
        BA.disable();
        Toast.makeText(getApplicationContext(), "Turned off" ,Toast.LENGTH_LONG).show();
    }


    public void visible(View v){
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(getVisible, 0);
    }


    public void list(View v){
        pairedDevices = BA.getBondedDevices();

        ArrayList list = new ArrayList();

        for(BluetoothDevice bt : pairedDevices) list.add(bt.getName());
        Toast.makeText(getApplicationContext(), "Showing Paired Devices",Toast.LENGTH_SHORT).show();

        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);

        lv.setAdapter(adapter);
    }
}
