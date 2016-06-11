package com.google.gh.todolist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class SmsActivity extends AppCompatActivity {

    ArrayList<String> mContactSms = MainActivity.mContactSms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        ListView listView = (ListView) findViewById(R.id.list_sms);
        listView.setAdapter(new SmsAdapter());



    }


    class SmsAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mContactSms.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView = getLayoutInflater().inflate(R.layout.sms_item,null);
            }
            TextView textSms = (TextView) convertView.findViewById(R.id.text_sms);
            textSms.setText(mContactSms.get(position));
            return convertView;
        }
    }
}
