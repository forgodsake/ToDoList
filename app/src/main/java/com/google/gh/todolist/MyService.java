package com.google.gh.todolist;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("mytag","onBind");
        return new MyBinder();
    }

    public class MyBinder extends Binder{

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("mytag","onCreate");

        Toast.makeText(getApplicationContext(),"开机完成",Toast.LENGTH_SHORT).show();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("mytag","onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("mytag","onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("mytag","onDestroy");
    }

}
