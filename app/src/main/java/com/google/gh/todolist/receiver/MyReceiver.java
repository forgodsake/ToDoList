package com.google.gh.todolist.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {



    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        context.startService(new Intent(context, MyService.class));
        Toast.makeText(context,"get here",Toast.LENGTH_SHORT).show();
    }
}
