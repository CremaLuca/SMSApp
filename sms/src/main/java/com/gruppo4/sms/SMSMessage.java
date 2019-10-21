package com.gruppo4.sms;

public class SMSMessage {
    String text_message;
    String tel_number;

    SMSMessage(String numeroTelefono, String testo){
        text_message = testo;
        tel_number = numeroTelefono;
    }

    public String getTelephonNumber(){
        return tel_number;
    }

    public String getMessage(){
        return text_message;
    }

}
