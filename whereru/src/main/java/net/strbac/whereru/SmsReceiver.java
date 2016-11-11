package net.strbac.whereru;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.widget.Toast;


public class SmsReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent launchIntent = new Intent(context, MainActivity.class);
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(launchIntent);
//            launchIntent.addCategory(Intent.CATEGORY_HOME);
//            moveTaskToBack(true);
        } else {
            try {
                Bundle intentExtras = intent.getExtras();
                if (intentExtras != null && intentExtras.get(SMS_BUNDLE) != null) {
                    Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
                    MainActivity mainInstance = MainActivity.instance();
                    for (int i = 0; i < sms.length; ++i) {
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);
                        String smsBody = smsMessage.getMessageBody();
                        if (mainInstance.matches(smsBody)) {
                            mainInstance.bringToFront();
                            String callerNumber = smsMessage.getOriginatingAddress();
                            String callerName = getCallerName(context, callerNumber);
                            mainInstance.setCallerNumber(callerNumber);
                            mainInstance.setResponseButtonText(callerName + "\nis asking where you are!");
                            mainInstance.setResponseButtonColor(Color.GREEN);
                        } else if (smsBody.startsWith(MainActivity.uriString)) {
                            mainInstance.showMap(smsBody);
                        } else {
                            //ignore other messages
                        }
                    }

                }
            } catch (Exception e) {
                Toast.makeText(context,
                        "Internal error :(",
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    public static String getCallerName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

}