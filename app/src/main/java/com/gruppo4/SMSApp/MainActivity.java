package com.gruppo4.SMSApp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gruppo4.sms.dataLink.SMSController;
import com.gruppo4.sms.dataLink.SMSMessage;
import com.gruppo4.sms.dataLink.exceptions.InvalidSMSMessageException;
import com.gruppo4.sms.dataLink.exceptions.InvalidTelephoneNumberException;
import com.gruppo4.sms.dataLink.listeners.SMSReceivedListener;
import com.gruppo4.sms.dataLink.listeners.SMSSentListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SMSReceivedListener, SMSSentListener {

    private static final String SMILE_COMMAND = "SMILE_COMMAND";
    private static final String HEART_COMMAND = "HEART_COMMAND";
    private static final String LONG_COMMAND_PREFIX = "LONG_COMMAND";
    private static final int APP_ID = 123;
    private static final String LONG_COMMAND = LONG_COMMAND_PREFIX + " This command is way too long to be sent in one single sms, this takes at least two or three sms to be completely sent. " +
            "And to prove it i can just send you this";
    private static final int SMS_PERMISSION_CODE = 1;
    private RecyclerView listView;
    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //If the application is given the permissions before sending the first message, the SMSController setup is immediately done.
        if(checkSelfPermission(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
            setupSMSController(getApplicationContext(), APP_ID);
        }

        ArrayList<String> events = new ArrayList<>();

        listView = findViewById(R.id.my_recycler_view);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(layoutManager);

        adapter = new ListAdapter(events);
        listView.setAdapter(adapter);

        findViewById(R.id.sendSmileButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendSmileButton(getApplicationContext());
            }
        });
        findViewById(R.id.sendHeartButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendHeartButton(getApplicationContext());
            }
        });
        findViewById(R.id.sendLongButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendLongButton(getApplicationContext());
            }
        });

    }

    private void setupSMSController(Context ctx, int appID) {
        SMSController.setup(ctx, appID);
        SMSController.addOnReceiveListener(this);
    }

    /**
     * Sends a message using the sms library
     *
     * @param text the content of the message
     * @param telephoneNumber the target telephone number
     */
    private void sendMessage(Context context, String text, String telephoneNumber) {
        try {
            SMSMessage message = new SMSMessage(context, telephoneNumber, text);
            SMSController.sendMessage(context, message, this);
        } catch (InvalidSMSMessageException messageException) {
            Log.e("MainActivity", messageException.getMessage());
        } catch (InvalidTelephoneNumberException telephoneException) {
            String error = "";
            switch (telephoneException.getState()) {
                case TELEPHONE_NUMBER_TOO_SHORT:
                    error = "Telephone number is too short!";
                    break;
                case TELEPHONE_NUMBER_TOO_LONG:
                    error = "Telephone number is too long!";
                    break;
                case TELEPHONE_NUMBER_NO_COUNTRY_CODE:
                    error = "You have to insert the country code";
                    break;
                case TELEPHONE_NUMBER_NOT_A_NUMBER:
                    error = "The telephone number is not valid";
                    break;
            }
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            Log.e("MainActivity", telephoneException.getMessage() + ", shown error: " + error);
        }
    }

    public void onSendHeartButton(Context ctx) {
        if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        } else {
            String phoneNumber = ((EditText) findViewById(R.id.phoneNumberTextView)).getText().toString();
            sendMessage(ctx, HEART_COMMAND, phoneNumber);
        }
    }

    /**
     * Callback for send smile button pressed. Sends a message to the number specified in the phoneNumberTextView
     */
    public void onSendSmileButton(Context ctx) {
        if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        } else {
            String phoneNumber = ((EditText) findViewById(R.id.phoneNumberTextView)).getText().toString();
            sendMessage(ctx, SMILE_COMMAND, phoneNumber);
        }
    }

    /**
     * Callback for send long message button pressed. Sends a message to the number specified in the phoneNumberTextView
     */
    public void onSendLongButton(Context ctx) {
        if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        } else {
            String phoneNumber = ((EditText) findViewById(R.id.phoneNumberTextView)).getText().toString();
            sendMessage(ctx, LONG_COMMAND, phoneNumber);
        }
    }

    @Override
    public void onSMSReceived(SMSMessage message) {
        Log.d("MainActivity", "Received message:" + message.getMessage());
        if (message.getMessage().equals(SMILE_COMMAND)) {
            adapter.getEvents().add(message.getTelephoneNumber() + " sent you a smile :)");

        } else if (message.getMessage().equals(HEART_COMMAND)) {
            adapter.getEvents().add(message.getTelephoneNumber() + " sent you a heart <3");
        } else if (message.getMessage().startsWith(LONG_COMMAND_PREFIX)) {
            adapter.getEvents().add(message.getTelephoneNumber() + " sent you a looong command");
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSMSSent(SMSMessage message, SMSController.SentState state) {
        Log.d("MainActivity", "Message sent: " + message.getMessage());
        if (state == SMSController.SentState.MESSAGE_SENT) {
            Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show();
            if (message.getMessage().equals(SMILE_COMMAND)) {
                adapter.getEvents().add("You sent a :) to " + message.getTelephoneNumber());
            } else if (message.getMessage().equals(HEART_COMMAND)) {
                adapter.getEvents().add("You sent a <3 to " + message.getTelephoneNumber());
            } else if (message.getMessage().startsWith(LONG_COMMAND_PREFIX)) {
                adapter.getEvents().add("You sent a looong command to " + message.getTelephoneNumber());
            }
            adapter.notifyDataSetChanged();
        } else {
            Log.w("MainActivity", "Unable to send sms, reason: " + state);
            Toast.makeText(this, "Unable to send message, reason: " + state, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == SMS_PERMISSION_CODE) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay!
                setupSMSController(getApplicationContext(), APP_ID);
            } else {
                // permission denied, boo!
                // close the app then
                finish();
                System.exit(0);
            }
        }
    }
}
