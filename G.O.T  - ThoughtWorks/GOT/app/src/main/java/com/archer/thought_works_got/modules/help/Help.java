package com.archer.thought_works_got.modules.help;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.archer.thought_works_got.R;
import com.archer.thought_works_got.data_model.HelpDO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Swastik on 11-08-2016.
 */
public class Help extends AppCompatActivity
{
    Dialog dialog;
    protected RecyclerView mRecyclerView;
    protected RecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_help);
        getSupportActionBar().setTitle("Help");
        
        init();
    }

    private void init() {

        mRecyclerView = (RecyclerView) findViewById(R.id.rvHelp);
        mRecyclerView.setLayoutManager( new LinearLayoutManager(this));
        List<HelpDO> list = new ArrayList<>();
        list.add(getItem1());
        list.add(getItem2());
        list.add(getItem3());
        mAdapter = new RecyclerAdapter(this,list);
        mRecyclerView.setAdapter(mAdapter);
    }

    private HelpDO getItem1() {
        HelpDO _help = new HelpDO();
        _help.setHeader("..");
        _help.setContent("..");
        return _help;
    }
    private HelpDO getItem2() {
        HelpDO _help = new HelpDO();
        _help.setHeader("..");
        _help.setContent("..");
        return _help;
    }
    private HelpDO getItem3() {
        HelpDO _help = new HelpDO();
        _help.setHeader("..");
        _help.setContent("..");
        return _help;
    }
}
