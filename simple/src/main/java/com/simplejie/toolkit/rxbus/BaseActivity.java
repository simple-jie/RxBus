package com.simplejie.toolkit.rxbus;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.simplejie.toolkit.rxbus.annotation.Subscribe;

/**
 * Created by Xingbo.Jie on 30/6/16.
 */
public class BaseActivity extends AppCompatActivity {

    @Subscribe(eventId = {1, 2, 3, 4, 5, 6}, thread = PostingThread.IO)
    public void onEvent(int id, Object[] args) {
        Log.e("onEventParent", String.format("id = %d threadId = %d", id, Thread.currentThread().getId()));
    }

    @Subscribe(eventId = {1, 2, 3, 4, 5, 6}, thread = PostingThread.NEW_THEAD)
    public void onEventC(int id, Object[] args) {
        Log.e("onEventC", String.format("id = %d threadId = %d", id, Thread.currentThread().getId()));
    }
}
