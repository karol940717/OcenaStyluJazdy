package com.example.ocenastylujazdy.BluetoothELM327;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.ocenastylujazdy.DataBase.MyDatabase;
import com.example.ocenastylujazdy.R;
import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.control.ModuleVoltageCommand;
import com.github.pires.obd.commands.control.VinCommand;
import com.github.pires.obd.commands.engine.LoadCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.fuel.FuelLevelCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.enums.ObdProtocols;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


public class MainActivityELM327 extends Activity {

    final static int ENABLE_BT_REQUEST = 1;

    private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothSocket btSocket;
    private String chosenDeviceName, chosenDeviceAddress;
    private Button bConnect, bStart, bStop, bChooseDevice;
    private TextView command1Label, command2Label, command3Label, command4Label, command5Label, command6Label, command7Label;
    private TextView command1Result, command2Result, command3Result, command4Result, command5Result, command6Result, command7Result;

    private ObdCommand command1 = new RPMCommand();
    private ObdCommand command2 = new SpeedCommand();
    private ObdCommand command3 = new LoadCommand();
    private ObdCommand command4 = new FuelLevelCommand();
    private ObdCommand command5 = new ModuleVoltageCommand();
    private ObdCommand command6 = new com.example.ocenastylujazdy.BluetoothELM327.OBD.commands.engine.ThrottlePositionCommand();
    private ObdCommand command7 = new VinCommand();

    private Timer timer;

    public MyDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_e_l_m327);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        command1Label = findViewById(R.id.command1Label);
        command2Label = findViewById(R.id.command2Label);
        command3Label = findViewById(R.id.command3Label);
        command4Label = findViewById(R.id.command4Label);
        command5Label = findViewById(R.id.command5Label);
        command6Label = findViewById(R.id.command6Label);
        command7Label = findViewById(R.id.command7Label);


        command1Result = findViewById(R.id.command1Result);
        command2Result = findViewById(R.id.command2Result);
        command3Result = findViewById(R.id.command3Result);
        command4Result = findViewById(R.id.command4Result);
        command5Result = findViewById(R.id.command5Result);
        command6Result = findViewById(R.id.command6Result);
        command7Result = findViewById(R.id.command7Result);


        bChooseDevice = findViewById(R.id.bChooseDevice);
        bChooseDevice.setOnClickListener(e -> chooseBluetoothDevice());


        bConnect = findViewById(R.id.bConnect);
        bConnect.setOnClickListener(e -> connectOBD());
        bConnect.setEnabled(false);

        bStart = findViewById(R.id.bStart);
        bStart.setOnClickListener(e -> startOBD());
        bStart.setEnabled(false);

        bStop = findViewById(R.id.bStop);
        bStop.setOnClickListener(e -> stopOBD());
        bStop.setEnabled(false);

        database = new MyDatabase(this, 1);
    }

    private void chooseBluetoothDevice() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            Toast.makeText(this, "Urządzenie nie wspiera Bluetooth", Toast.LENGTH_LONG).show();
        }
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, ENABLE_BT_REQUEST);
        } else {
            continueBluetooth();
        }
    }

    private void continueBluetooth() {
        final ArrayList<String> pairedDevicesNames = new ArrayList<>();
        final ArrayList<String> pairedDevicesAddresses = new ArrayList<>();

        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                pairedDevicesNames.add(device.getName());
                pairedDevicesAddresses.add(device.getAddress());
            }

            final String[] devicesString = pairedDevicesNames.toArray(new String[0]);
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivityELM327.this);
            mBuilder.setTitle("Wybierz urządzenie OBD:");
            mBuilder.setSingleChoiceItems(devicesString, -1, (dialog, i) -> {
                dialog.dismiss();
                int position = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                chosenDeviceAddress = pairedDevicesAddresses.get(position);
                chosenDeviceName = pairedDevicesNames.get(position);
                Toast.makeText(MainActivityELM327.this, "Wybrany: " + chosenDeviceName, Toast.LENGTH_SHORT).show();

                TextView info = findViewById(R.id.info);
                info.setText(String.format("Nazwa: %s\tAddress: %s", chosenDeviceName, chosenDeviceAddress));
                bConnect.setEnabled(true);
            });

            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        } else {
            Toast.makeText(this, "Nie znaleziono sparowanych urzadzeń", Toast.LENGTH_SHORT).show();
        }
    }

    private void connectOBD() {
        try {
            BluetoothDevice device = btAdapter.getRemoteDevice(chosenDeviceAddress);
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

            btSocket = device.createRfcommSocketToServiceRecord(uuid);
            btSocket.connect();

            new EchoOffCommand().run(btSocket.getInputStream(), btSocket.getOutputStream());
            new LineFeedOffCommand().run(btSocket.getInputStream(), btSocket.getOutputStream());
            new SelectProtocolCommand(ObdProtocols.AUTO).run(btSocket.getInputStream(), btSocket.getOutputStream());

            Toast.makeText(MainActivityELM327.this, "Połączono z OBD", Toast.LENGTH_SHORT).show();
            bStart.setEnabled(true);
            bConnect.setEnabled(false);

        } catch (IllegalArgumentException e) {
            Toast.makeText(MainActivityELM327.this, "Wybierz najpierwsz urządzenie Bluetooth", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(MainActivityELM327.this, "Nie można nawiązać połączenia", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(MainActivityELM327.this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void startOBD() {
        bStart.setEnabled(false);
        bConnect.setEnabled(false);
        bChooseDevice.setEnabled(false);
        bStop.setEnabled(true);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    command1.run(btSocket.getInputStream(), btSocket.getOutputStream());
                    command1Result.setText(command1.getCalculatedResult());
                    command2.run(btSocket.getInputStream(), btSocket.getOutputStream());
                    command2Result.setText(command2.getCalculatedResult());
                    command3.run(btSocket.getInputStream(), btSocket.getOutputStream());
                    command3Result.setText(command3.getCalculatedResult());
                    command4.run(btSocket.getInputStream(), btSocket.getOutputStream());
                    command4Result.setText(command4.getCalculatedResult());
                    command5.run(btSocket.getInputStream(), btSocket.getOutputStream());
                    command5Result.setText(command5.getCalculatedResult());
                    command6.run(btSocket.getInputStream(), btSocket.getOutputStream());
                    command6Result.setText(command6.getCalculatedResult());
                    database.writeDataThrottle(Float.valueOf(command6.getCalculatedResult()));
                    if(Float.valueOf(command6.getCalculatedResult())>50){
                        database.writeDataThrottleX(Float.valueOf(command6.getCalculatedResult()));
                    }

                } catch (Exception e) {
                    Toast.makeText(MainActivityELM327.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, 100, 1000);
        try {
            command7.run(btSocket.getInputStream(), btSocket.getOutputStream());
        } catch (NumberFormatException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
        command7Result.setText(command7.getCalculatedResult());


    }

    private void stopOBD() {
        command1Result.setText("");
        command2Result.setText("");
        command3Result.setText("");
        command4Result.setText("");
        command5Result.setText("");
        command6Result.setText("");
        command7Result.setText("");
        timer.cancel();
        bStart.setEnabled(true);
        bStop.setEnabled(false);
        bConnect.setEnabled(true);
        bChooseDevice.setEnabled(true);
    }
}


