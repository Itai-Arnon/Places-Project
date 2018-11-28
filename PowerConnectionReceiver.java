package com.project.itai.FindAPlace.receiver;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.widget.Toast;

public class PowerConnectionReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

//                Intent.ACTION_BATTERY_CHANGED
//                Toast.makeText(context,"Power change !!!", Toast.LENGTH_LONG).show();

        if (action.equals(Intent.ACTION_POWER_CONNECTED)) {
            // Do something when power connected
            Toast.makeText(context, "AC Power connected", Toast.LENGTH_LONG).show();
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(1000);

        } else if (action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
            // Do something when power disconnected
            Toast.makeText(context, (String) "AC Power has be Disconnected", Toast.LENGTH_LONG).show();
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(1000);
        }

    }

}
