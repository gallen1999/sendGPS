package com.me.gallen.sendgps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {

    private String phoneNum = "replace with number you want to send the gps location to";
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction() == "android.provider.Telephony.SMS_RECEIVED") {
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            String msg_from;
            if (bundle != null) {
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for (int i = 0; i < msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();
                        if (msg_from.equals(phoneNum) && msgBody.contains("Location")) {
                            LocationManager locationManager = (LocationManager)
                                    context.getSystemService(Context.LOCATION_SERVICE);

                            LocationListener locationListener = new LocationListener() {
                                @Override
                                public void onStatusChanged(String provider, int status, Bundle extras) {}
                                @Override
                                public void onProviderEnabled(String provider) { }
                                @Override
                                public void onProviderDisabled(String provider) { }
                                @Override
                                public void onLocationChanged(Location location) {
                                    double latitude = location.getLatitude();
                                    double longitude = location.getLongitude();
                                }
                            };

                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                            sendSMS("http://www.google.com/maps/place/" + locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude() + "," + locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude(),context);
                        }
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendSMS(String msg, Context context) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNum, null, msg, null, null);
            Toast.makeText(context, "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(context, ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
}
