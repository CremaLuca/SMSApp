package com.gruppo4.sms.dataLink;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.gruppo4.sms.dataLink.listeners.SMSSentListener;

/**
 * Broadcast receiver for sent messages, called by Android Library.
 * Must be instantiated and set as receiver with context.registerReceiver(...)
 */
/**
 * @author Luca Crema, Marco Mariotto
 */

class SMSSentBroadcastReceiver extends BroadcastReceiver {

    private SMSSentListener listener;
    private SMSMessage message;
    private SMSMessage.SentState sentState = SMSMessage.SentState.MESSAGE_SENT;

    /**
     * Constructor for the BroadcastReceiver.
     *
     * @param message  message that will be sent.
     * @param listener listener to be called when the operation is completed successfully or not.
     */
    SMSSentBroadcastReceiver(@NonNull final SMSMessage message, SMSSentListener listener) {
        this.listener = listener;
        this.message = message;
    }

    /**
     * @param listener a listener to be called once the message is sent.
     */
    void setListener(SMSSentListener listener) {
        Log.v("SMSSentReceiver", "Changed listener to class:" + listener.getClass());
        this.listener = listener;
    }

    /**
     * @param message a message to pass to the listener once it is sent.
     */
    void setMessage(@NonNull final SMSMessage message) {
        Log.v("SMSSentReceiver", "Changed message");
        this.message = message;
    }

    /**
     * This method is subscribed to the intent of a message sent, and will be called whenever a message is sent using this library.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        SMSMessage.SentState state;
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                state = SMSMessage.SentState.MESSAGE_SENT;
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                state = SMSMessage.SentState.ERROR_RADIO_OFF;
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                state = SMSMessage.SentState.ERROR_NULL_PDU;
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                state = SMSMessage.SentState.ERROR_NO_SERVICE;
                break;
            case SmsManager.RESULT_ERROR_LIMIT_EXCEEDED:
                state = SMSMessage.SentState.ERROR_LIMIT_EXCEEDED;
                break;
            default:
                state = SMSMessage.SentState.ERROR_GENERIC_FAILURE;
                Log.d("SMSSentReceiver", "Generic error for message: " + message.getData() + " from:" + message.getPeer().getAddress());
                break;
        }
        Log.v("SMSSentReceiver", "Sent a packet with state: " + state);

        setSentState(state);

        if (listener != null)
            listener.onSMSSent(message, sentState);
        context.unregisterReceiver(this);

    }

    /**
     * Updates the message sent state, the state is NOT updated if the current state is an error
     *
     * @param sentState state for the current packet
     */
    private void setSentState(SMSMessage.SentState sentState) {
        //The state is modified ONLY IF THE CURRENT STATE IS OK. If a single packet has given an error the state is error
        if (this.sentState == SMSMessage.SentState.MESSAGE_SENT)
            this.sentState = sentState;
    }
}
