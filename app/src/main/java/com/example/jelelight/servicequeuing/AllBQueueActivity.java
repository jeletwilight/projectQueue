package com.example.jelelight.servicequeuing;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import java.util.List;

public class AllBQueueActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_all_bqueue);
        mRecyclerView = findViewById(R.id.recycler_bqueues);
        new BDFirebaseDB().readQueues(new BDFirebaseDB.DataStatusB() {
            @Override
            public void DataIsLoaded(List<Queue> queues, List<String> keys) {
                new RecyclerViewConfig().setConfig(mRecyclerView,AllBQueueActivity.this,queues,keys);

            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {

            }
        });
    }
}

