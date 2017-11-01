
package com.example.viper2.blog_config;
/*
https://danielme.com/tip-android-12-animar-mostrarocultar-layout-con-desplazamientos/ // mostrar y ocultar layouts
https://neurobin.org/docs/android/android-time-picker-example/
http://www.technotalkative.com/android-get-current-date-and-time/
https://developer.android.com/reference/java/text/SimpleDateFormat.html

https://danielme.com/2013/04/25/diseno-android-spinner
 */

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;
import android.view.View.OnClickListener;

public class BthActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout layoutAnimado1,layoutAnimado2;

    private static final String TAG = "Read";
    // EXTRA string to send on to ConfigActivity
    private static Handler bluetoothIn;
    static final int handlerState = 0;        				 //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();
    private ConnectedThread mConnectedThread;
    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // String for MAC address
    private static String address = null;

    Button btnOptionfr, btnConfigfr,btn1,btn2,btn3,btn4,btnSend,btnChangeTime;
    TextView txt1,txt2,txt3;
    Spinner sPack,sSpeed,sBtime;
    String sendConfig=null;
    String YY,MM,DD,W,HA,MA,HI=null,MI=null,FF=null,N=null,MS=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bth);
        layoutAnimado1 = (LinearLayout) findViewById(R.id.OptionLayout);
        layoutAnimado2 = (LinearLayout) findViewById(R.id.ConfigLayout);
        //original
        layoutAnimado1.setVisibility(View.VISIBLE);
        layoutAnimado2.setVisibility(View.GONE);
        /*
        layoutAnimado1.setVisibility(View.GONE);
        layoutAnimado2.setVisibility(View.VISIBLE);
          */
        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {                                     //if message is what we want
                    String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);//keep appending to string until ~
                    int endOfLineIndex = recDataString.indexOf("\n");                    // determine the end-of-line
                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
                        //txt1.setText("D_Rx = " + dataInPrint);
                        int dataLength = dataInPrint.length();                          //get length of data received
                        //txtStringLength.setText("String Length = " + String.valueOf(dataLength));
                        if (recDataString.charAt(0) == '+')                             //if it starts with # we know it is what we are looking for
                        {
                            //YY/MY/DD/W/HA/MA/HI/MI/FF/N/MS
                            String dd = recDataString.substring(1, 3);
                            String mm= recDataString.substring(4, 6);
                            String yy= recDataString.substring(7, 9);
                            String ha = recDataString.substring(10, 12);
                            String ma = recDataString.substring(13, 15);
                            String hi = recDataString.substring(16, 18);
                            String mi = recDataString.substring(19, 21);
                            String n = recDataString.substring(22, 26);
                            String ff = recDataString.substring(27, 29);
                            String ms = recDataString.substring(30, 32);
                            //hacer calculo para N

                            //Toast.makeText(getBaseContext(), recDataString, Toast.LENGTH_SHORT).show();
                            Toast.makeText(getBaseContext(), "Sending program...", Toast.LENGTH_SHORT).show();
                            txt1.setText(" Date = " + dd + "/"+ mm + "/"+ yy + " Time = "+ ha + ":" + ma);
                            txt2.setText(" Start Time = " + hi + ":" + mi );
                            txt3.setText(" N(Pack) = " + n + " F(Hz) = " + ff+ " Break Time =" + ms);
                            //txt3.setText("D_Rx = " + dataInPrint);
                        }
                        if (recDataString.charAt(0) == '-')                             //if it starts with # we know it is what we are looking for
                        {
                            int i = 1;
                            while(recDataString.charAt(i) != '/')
                            {
                                i++;
                            }
                            String Temp = recDataString.substring(1, i-1);
                            String Presion = recDataString.substring(i+1, recDataString.length());

                            //Toast.makeText(getBaseContext(), recDataString, Toast.LENGTH_SHORT).show();

                            txt1.setText("Check Sensor Status");
                            txt2.setText(" Temp (C) = " + Temp);
                            txt3.setText(" P(Bar) = " + Presion);

                        }
                        recDataString.delete(0, recDataString.length());                    //clear all string data
                        // strIncom =" ";
                        dataInPrint = " ";
                    }
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState_2();

        btnOptionfr = (Button)findViewById(R.id.btnOptionfr);
        btnConfigfr = (Button)findViewById(R.id.btnConfigfr);
       // btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);
        btnSend = (Button) findViewById(R.id.btnSend);
        sPack = (Spinner) findViewById(R.id.sPack);
        sSpeed = (Spinner) findViewById(R.id.sSpeed);
        sBtime = (Spinner) findViewById(R.id.sBtime);

        txt1 = (TextView) findViewById(R.id.txt1);
        txt2 = (TextView) findViewById(R.id.txt2);
        txt3 = (TextView) findViewById(R.id.txt3);

        btnOptionfr.setOnClickListener(this);
        btnConfigfr.setOnClickListener(this);
        /*
        btn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //mConnectedThread.write("1");    // Send "0" via Bluetooth
                Toast.makeText(getBaseContext(), "boton 1", Toast.LENGTH_SHORT).show();
                txt1.setText("");
                txt2.setText("");
                txt3.setText("");

                //Intent i = new Intent(OptionActivity.this, ConfigActivity.class);
                //i.putExtra(EXTRA_DEVICE_ADDRESS,address);
                // i.putExtra(EXTRA_DEVICE_SOCKET,btSocket);
                //startActivity(i);
                //finish();
                //mConnectedThread.stop();
                //try {
                //    btSocket.close();
                //} catch (IOException e) {
                //    e.printStackTrace();
                //}

            }
        });
        */
        btn2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mConnectedThread.write("+");    // Send "0" via Bluetooth
                //Toast.makeText(getBaseContext(), "boton 2", Toast.LENGTH_SHORT).show();
                txt1.setText("");
                txt2.setText("");
                txt3.setText("");
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mConnectedThread.write("-");    // Send "0" via Bluetooth
                //Toast.makeText(getBaseContext(), "boton 3", Toast.LENGTH_SHORT).show();
                txt1.setText("");
                txt2.setText("");
                txt3.setText("");
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //mConnectedThread.write("2");    // Send "0" via Bluetooth
                //Toast.makeText(getBaseContext(), "boton 4", Toast.LENGTH_SHORT).show();
                txt1.setText("");
                txt2.setText("");
                txt3.setText("");
                btAdapter.disable();
                //btSocket.close();
                Intent i = new Intent(BthActivity.this,InitActivity.class);
                startActivity(i);
                finish();

            }
        });

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.Pack, R.layout.spinner_item);
        sPack.setAdapter(adapter1);

        sPack.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                //Toast.makeText(getBaseContext(),"Data N = "+Integer.toString(i+1), Toast.LENGTH_SHORT).show();
                N=Integer.toString(i+1);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.Speed, R.layout.spinner_item);
        sSpeed.setAdapter(adapter2);

        sSpeed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                //Toast.makeText(getBaseContext(),"Hz = "+Integer.toString(i+1), Toast.LENGTH_SHORT).show();
                FF="0"+Integer.toString(i+1);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this, R.array.Btime, R.layout.spinner_item);
        sBtime.setAdapter(adapter3);

        sBtime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {

                Resources res = getResources();
                String[] Btimestr = res.getStringArray(R.array.Btime);
                MS=Btimestr[i];
                //Toast.makeText(getBaseContext(),"Btime = "+ Btimestr[i], Toast.LENGTH_SHORT).show();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Calendar c = Calendar.getInstance();
                //System.out.println("Current time =&gt; "+c.getTime());
                SimpleDateFormat df = new SimpleDateFormat("yy-MM-dd-EE HH:mm");
                String formattedDate = df.format(c.getTime());
                // Now formattedDate have current date/time
                //Toast.makeText(getBaseContext(), formattedDate, Toast.LENGTH_SHORT).show();

                //YY = Integer.toString(Integer.parseInt(formattedDate.substring(0, 2))+2000);//no paso nada
                YY = formattedDate.substring(0, 2);
                MM = formattedDate.substring(3, 5);
                DD = formattedDate.substring(6, 8);
                W=formattedDate.substring(9, 12);
                //numeracion del reloj ds1302
                if(W.equals("dom")|| W.equals("sun")){W=Integer.toString(1);}
                if(W.equals("lun")|| W.equals("mon")){W=Integer.toString(2);}
                if(W.equals("mar")|| W.equals("tue")){W=Integer.toString(3);}
                if(W.equals("mié")|| W.equals("wed")){W=Integer.toString(4);}
                if(W.equals("jue")|| W.equals("thu")){W=Integer.toString(5);}
                if(W.equals("vie")|| W.equals("fri")){W=Integer.toString(6);}
                if(W.equals("sáb")|| W.equals("sat")){W=Integer.toString(7);}


                HA = formattedDate.substring(14, 16);
                MA = formattedDate.substring(17, 19);

                if(HI==null||N==null||FF==null||MS==null){
                    Toast.makeText(getBaseContext(),"falta config", Toast.LENGTH_SHORT).show();
                }
                else {
                    String k="/";
                 sendConfig=YY+k+MM+k+DD+k+W+k+HA+k+MA+k+HI+k+MI+k+FF+k+N+k+MS+k+"*";
                    //revisar año en el dato de retorno (esta malo en el arduino IDE)
               // Toast.makeText(getBaseContext(),sendConfig, Toast.LENGTH_SHORT).show();
                    mConnectedThread.write(sendConfig);    // Send "Config" via Bluetoot
                    mConnectedThread.write(sendConfig);    // Send "Config" via Bluetoot
                }
                /*
                HI = formattedDate.substring(18, 20);
                MI = formattedDate.substring(21, 23);
                FF = formattedDate.substring(24, 28);
                N = formattedDate.substring(29, 31);
                MS = formattedDate.substring(32, 34);
                */
                //YY,MM,DD,W,HA,MA,HI,MI,FF,N,MS
         //mConnectedThread.write("+");    // Send "Config" via Bluetoot
        //Toast.makeText(getBaseContext(),"Envio trama confg", Toast.LENGTH_SHORT).show();

            }
        });


        btnChangeTime = (Button) findViewById(R.id.btnChangeTime);

        btnChangeTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(BthActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        btnChangeTime.setText( selectedHour + ":" + selectedMinute);
                        //para la hora
                        if(selectedHour<10){
                            HI="0"+Integer.toString(selectedHour);
                        }
                        else { HI=Integer.toString(selectedHour);}
                        ///para el minuto
                        if(selectedMinute<10){
                            MI="0"+Integer.toString(selectedMinute);
                        }
                        else { MI=Integer.toString(selectedMinute);}
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select start hour of burst");
                mTimePicker.show();
            }
        });
    }
    //
    //

    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btnOptionfr:
                /*
                OptionFragment fragmento1 = new OptionFragment();
                FragmentTransaction transition =  getSupportFragmentManager().beginTransaction();
                transition.replace(R.id.Contenedor, fragmento1);
                transition.commit();
                */
                if (layoutAnimado1.getVisibility() == View.GONE)
                {
                    //animar(true);
                    layoutAnimado1.setVisibility(View.VISIBLE);
                    layoutAnimado2.setVisibility(View.GONE);
                }
                break;

            case R.id.btnConfigfr:
                /*
                ConfigFragment fragmento2 = new ConfigFragment();
                FragmentTransaction transition1 =  getSupportFragmentManager().beginTransaction();
                transition1.replace(R.id.Contenedor, fragmento2);
                transition1.commit();
                */
                if (layoutAnimado2.getVisibility() == View.GONE)
                {
                    //animar(true);
                    layoutAnimado1.setVisibility(View.GONE);
                    layoutAnimado2.setVisibility(View.VISIBLE);
                }
                break;
        }

    }
    // ***********************funciones bluetooth************************************
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }
    //***************************************************
    public void onResume() {
        super.onResume();

        //Get MAC address from DeviceListActivity via intent
        //Intent intent = getIntent();
        //Get the MAC address from the DeviceListActivty via EXTRA
        //address = intent.getStringExtra(DevicesActivity.EXTRA_DEVICE_ADDRESS);
        //BluetoothDevice device = btAdapter.getRemoteDevice(address);
        //Toast.makeText(getBaseContext(), address, Toast.LENGTH_LONG).show();

        //DevicesActivity Socket = (DevicesActivity)activity.getApplication();
        btSocket=DevicesActivity.SocketHandler.getSocket();
        /*
        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG).show();
        }
        */
        // Establish the Bluetooth socket connection.
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try
            {
                btSocket.close();
            } catch (IOException e2)
            {
                //insert code to deal with this
            }
        }

        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
        /*
        try {
            //set time in mili
            Thread.sleep(1000);

        }catch (Exception e){
            e.printStackTrace();
        }
        */
        /*
        if(isBluetoothHeadsetConnected()) {
            //Do something if connected
            Toast.makeText(getApplicationContext(), "BT Connected", Toast.LENGTH_SHORT).show();
            mConnectedThread.write("+");
        }
        else {
            //btAdapter.disable();
            Intent i = new Intent(BthActivity.this, DevicesActivity.class);
            startActivity(i);
            finish();
            Toast.makeText(getApplicationContext(), "BT No Connected", Toast.LENGTH_SHORT).show();
        }
        */


        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
        //mConnectedThread.write("+");

    }
    //-----------------------
    public static boolean isBluetoothHeadsetConnected() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()
                && mBluetoothAdapter.getProfileConnectionState(BluetoothHeadset.HEADSET) == BluetoothHeadset.STATE_CONNECTED;
    }
    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private void checkBTState_2() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    //create new class for connect thread

    class ConnectedThread extends Thread {
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
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;

        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {

                try {
                    bytes = mmInStream.read(buffer);        	//read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                    //box= bluetoothIn.toString();
                    //Toast.makeText(getBaseContext(), readMessage, Toast.LENGTH_LONG).show();
                    //readMessage.replace('\n',' ');
                    // box= readMessage;
                    //Log.d(TAG, box);
                    //Toast.makeText(getBaseContext(), box, Toast.LENGTH_LONG).show();
                    //box=null;
                    //txt1.setText(readMessage);
                } catch (IOException e) {
                    break;
                }

                //Toast.makeText(getBaseContext(), "Read", Toast.LENGTH_LONG).show();
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                //Toast.makeText(getBaseContext(), "Send", Toast.LENGTH_LONG).show();
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                /*
                try {
                    btSocket.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
                */
                btAdapter.disable();
                Intent i = new Intent(BthActivity.this, InitActivity.class);
                startActivity(i);
                finish();

            }
        }
    }
}
