package com.archer.thought_works_got.modules.battle_log;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.archer.thought_works_got.R;
import com.archer.thought_works_got.data_model.network_models.BattleDO;
import com.archer.thought_works_got.data_model.sql_db_models.KingsDO;
import com.archer.thought_works_got.util.GOT_Util;
import com.archer.thought_works_got.util.MySQLiteHelper;

import java.util.List;

public class BattleLog
        extends AppCompatActivity{

    BattleLogAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_log);

        init();
    }

    private void init()
    {

        KingsDO data = MySQLiteHelper.getKingData(this,getIntent().getStringExtra(GOT_Util.KING_NAME));
        List<BattleDO> battle_data;
    }

    private void loadKingRanking(List<KingsDO> kingsData)
    {
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.rvList);
        mRecyclerView.setLayoutManager( new LinearLayoutManager(this));
        if(kingsData==null || kingsData.size()==0)
            findViewById(R.id.tvNoResults).setVisibility(View.VISIBLE);
        else
        {
            mAdapter = new BattleLogAdapter(kingsData,this);
            mRecyclerView.setAdapter(mAdapter);
            findViewById(R.id.tvNoResults).setVisibility(View.INVISIBLE);
        }
    }

}