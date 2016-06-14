package com.google.gh.todolist;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

public class FABActivity extends AppCompatActivity {

    private FloatingActionButton button1;
    private LinearLayout layout1,layout2,layout3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fab);
        button1 = (FloatingActionButton) findViewById(R.id.button1);
        layout1 = (LinearLayout) findViewById(R.id.layout1);
        layout2 = (LinearLayout) findViewById(R.id.layout2);
        layout3 = (LinearLayout) findViewById(R.id.layout3);
        initAction();
    }

    private void initAction() {
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float h = button1.getHeight();
                int i = layout1.getVisibility();
                if(i== View.VISIBLE){
                    FABAnim.hideView(layout1,h*1.2f);
                    FABAnim.hideView(layout2,h*2.5f);
                    FABAnim.hideView(layout3,h*5);
                }else {
                    FABAnim.showView(layout1,h*1.2f);
                    FABAnim.showView(layout2,h*2.5f);
                    FABAnim.showView(layout3,h*5);
                }
            }
        });
    }
}
