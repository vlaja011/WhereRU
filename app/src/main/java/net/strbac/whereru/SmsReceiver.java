package net.strbac.whereru;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsMessage;


public class SmsReceiver extends BroadcastReceiver {

    private static SmsReceiver instance;
    public static final String SMS_BUNDLE = "pdus";



    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            Main mainInstance = Main.instance();
            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);
                String smsBody = smsMessage.getMessageBody().toString();
                if (smsBody.equals(Main.whereruString)) {
                    String callersNumber = smsMessage.getOriginatingAddress();
                    mainInstance.setCallersNumber(callersNumber);
                    mainInstance.setResponseButtonColor(Color.RED);
                } else if (smsBody.startsWith(Main.uriString)) {
                    mainInstance.showMap(smsBody);
                }
            }

        }
    }
}