package com.archer.toppr_c.modules.favorites;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.archer.toppr_c.R;
import com.archer.toppr_c.data_model.EventDO;
import com.archer.toppr_c.util.MySQLiteHelper;
import com.archer.toppr_c.util.SharedPreferenceManager;
import com.archer.toppr_c.util.TopprUtils;

import java.util.List;

/**
 * Created by Swastik on 11-08-2016.
 */
public class Favorites
        extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        init();
    }

    private void init()
    {
        getSupportActionBar().setTitle("Favorites");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fetchEvents();
    }

    private void loadEvents(List<EventDO> results)
    {
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.rvEvents);
        mRecyclerView.setLayoutManager( new LinearLayoutManager(this));
        if(results==null || results.size()==0)
            findViewById(R.id.tvNoResults).setVisibility(View.VISIBLE);
        else
        {
            EventListAdapter mAdapter = new EventListAdapter(results,this);
            mRecyclerView.setAdapter(mAdapter);
            findViewById(R.id.tvNoResults).setVisibility(View.INVISIBLE);
        }
    }

    private void fetchEvents() {
        SharedPreferenceManager pref = SharedPreferenceManager.getInstance(this);
        List<EventDO> events = MySQLiteHelper.fetchFavEventsFromFavTable(this,pref.getKeyData(TopprUtils.USER_ID));
        loadEvents(events);
    }


}
