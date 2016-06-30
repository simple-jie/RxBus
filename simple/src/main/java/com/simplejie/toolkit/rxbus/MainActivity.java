package com.simplejie.toolkit.rxbus;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.simplejie.toolkit.rxbus.annotation.Subscribe;

public class MainActivity extends BaseActivity {
    int eventId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RxBus.getDefault().pubishArray(++eventId);
                RxBus.getDefault().pubish(eventId, new CustomEvent());
                if (eventId == 12) {
                    eventId = 0;
                }
            }
        });

        RxBus.getDefault().register(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Subscribe(eventId = {6, 7, 8, 9, 10, 11, 12}, thread = PostingThread.MAIN)
    public void onEvent(int id, Object[] args) {
        if (id < 7)
            super.onEvent(id, args);
        else
            Toast.makeText(this, String.format("eventId is %d", id), Toast.LENGTH_SHORT).show();
    }

    @Subscribe(eventId = {7}, thread = PostingThread.COMPUTATION)
    public void onEvent(int id, CustomEvent arg) {
        Log.e("onEvent", String.format("id = %d threadId = %d", id, Thread.currentThread().getId()));
    }

    public static class CustomEvent {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getDefault().unRegister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
