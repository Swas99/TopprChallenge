package com.archer.toppr_c.modules.help;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.archer.toppr_c.R;
import com.archer.toppr_c.data_model.HelpDO;

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
        _help.setHeader("What is Toppr?");
        _help.setContent("We are India's most comprehensive education platform for" +
                " classes 8 to 12. We build technology driven products that leverage" +
                " the internet. We build personal learning products that work hard" +
                " for each of our customers.");
        return _help;
    }
    private HelpDO getItem2() {
        HelpDO _help = new HelpDO();
        _help.setHeader("Who are we?");
        _help.setContent("We surround ourselves with smart people intent on building smart products." +
                " We do what's right and not on what's easy. " +
                "We believe in technology. We solve problems." +
                " We build on game changing technology to solve mind bending problems." );
        return _help;
    }
    private HelpDO getItem3() {
        HelpDO _help = new HelpDO();
        _help.setHeader("Our Vision");
        _help.setContent(
                "Personalise K-12 education with technology");
        return _help;
    }
}
