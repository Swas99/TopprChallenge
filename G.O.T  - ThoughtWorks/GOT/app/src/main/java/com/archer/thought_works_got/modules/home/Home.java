package com.archer.thought_works_got.modules.home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;

import com.archer.thought_works_got.R;
import com.archer.thought_works_got.data_model.network_models.BattleDO;
import com.archer.thought_works_got.data_model.sql_db_models.KingsDO;
import com.archer.thought_works_got.modules.nav_drawer.NavDrawer;
import com.archer.thought_works_got.network.NetworkRequest;
import com.archer.thought_works_got.network.OnTaskCompleted;
import com.archer.thought_works_got.util.EloRatingSystem;
import com.archer.thought_works_got.util.GOT_Util;
import com.archer.thought_works_got.util.MySQLiteHelper;
import com.archer.thought_works_got.util.NetworkUtil;
import com.archer.thought_works_got.util.SharedPreferenceManager;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home
        extends AppCompatActivity
        implements OnTaskCompleted, View.OnClickListener {
    private static final int RC_FETCH_BATTLES = 101;
    private static final int F_ALL = 0;
    private static final int F_LESS_THAN_TEN_BATTLES = 1;
    private static final int F_MORE_THAN_TEN_BATTLES = 3;

    private static final int S_DEFAULT = 18;
    private static final int S_RANK_ASC = 19;
    private static final int S_BATTLES_WON_ASC = 20;
    private static final int S_TOTAL_BATTLES_ASC = 21;
    private static final int S_RANK_DSC = 22;
    private static final int S_BATTLES_WON_DSC = 23;
    private static final int S_TOTAL_BATTLES_DSC = 24;


    int sortType;
    int filterType;
    KingsDataListAdapter mAdapter;

    Dialog dialog;
    public ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();
    }

    private void init()
    {
        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setTitle(Html.fromHtml("<u><b>G</b>ame</u>&nbsp;&nbsp;<u><i>of</i></u>&nbsp;&nbsp;<u><b>T</b>hrones</u>"));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        SharedPreferenceManager pref = SharedPreferenceManager.getInstance(this);
        String lastUpdate = pref.getKeyData(GOT_Util.LAST_UPDATE_META_DATA);
        if(!lastUpdate.isEmpty())
        {
            long ONE_DAY = 1000 * 60 *60 *24;
            long now = new Date().getTime();
            if(now-Long.parseLong(lastUpdate)<ONE_DAY)
                loadKingRanking( MySQLiteHelper.getKingData(this));
            else
                fetchBattles();
        }
        else
            fetchBattles();

        initializeNavDrawer();
        findViewById(R.id.btnFilters).setOnClickListener(this);
    }

    private void loadKingRanking(List<KingsDO> kingsData)
    {
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.rvList);
        mRecyclerView.setLayoutManager( new LinearLayoutManager(this));
        if(kingsData==null || kingsData.size()==0)
            findViewById(R.id.tvNoResults).setVisibility(View.VISIBLE);
        else
        {
            mAdapter = new KingsDataListAdapter(kingsData,this);
            mRecyclerView.setAdapter(mAdapter);
            findViewById(R.id.tvNoResults).setVisibility(View.INVISIBLE);
        }
    }

    private void fetchBattles() {
        NetworkRequest req = new NetworkRequest(Home.this);
        req.fetchBattles(NetworkUtil.FETCH_BATTLES, RC_FETCH_BATTLES, this);
    }

    @Override
    public void onTaskCompleted(String response, int requestCode) {
        switch (requestCode)
        {
            case RC_FETCH_BATTLES:
            {
                try
                {
                    ObjectMapper objectMapper = new ObjectMapper();
                    List<BattleDO> results = Arrays.asList(objectMapper.readValue(response, BattleDO[].class));
                    MySQLiteHelper.insertRowsToBattleDataTable(this,results);
                    ProcessData(results);
                    filterType = F_ALL;
                    sortType = S_DEFAULT;
                }
                catch(IOException e)
                {
                    GOT_Util.showAlertMessage("Jackson Mapper error",e.getMessage(),Home.this);
                }
                break;
            }
        }

    }

    private void ProcessData(List<BattleDO> battles)
    {
        Collections.sort(battles, new Comparator<BattleDO>() {
            @Override
            public int compare(BattleDO a1, BattleDO a2) {
                return a1.getBattle_number() < a2.getBattle_number() ? -1
                        : a1.getBattle_number() > a2.getBattle_number() ? 1
                        : 0;
            }
        });

        Map<String,Double> rating = new HashMap<>();
        Map<String,Double> lowest_rating = new HashMap<>();
        Map<String,Double> highest_rating = new HashMap<>();

        Map<String,Integer> best_rank = new HashMap<>();
        Map<String,Integer> worst_rank = new HashMap<>();

        Map<String,Integer> battle_won_count = new HashMap<>();
        Map<String,Integer> battle_loss_count = new HashMap<>();
        Map<String,Integer> battle_total_count = new HashMap<>();

        for(BattleDO battle : battles)
        {
            String king1 = battle.getAttacker_king();
            String king2 = battle.getDefender_king();
            if(king1.isEmpty() || king2.isEmpty())
                continue;

            if(!rating.containsKey(king1))
            {
                rating.put(king1,400.0);
                lowest_rating.put(king1,Double.MAX_VALUE);
                highest_rating.put(king1,Double.MIN_VALUE);
                best_rank.put(king1,Integer.MAX_VALUE);
                worst_rank.put(king1,Integer.MIN_VALUE);

                battle_won_count.put(king1,0);
                battle_loss_count.put(king1,0);
                battle_total_count.put(king1,0);
            }
            if(!rating.containsKey(king2))
            {
                rating.put(king2,400.0);
                lowest_rating.put(king2,Double.MAX_VALUE);
                highest_rating.put(king2,Double.MIN_VALUE);
                best_rank.put(king2,Integer.MAX_VALUE);
                worst_rank.put(king2,Integer.MIN_VALUE);

                battle_won_count.put(king2,0);
                battle_loss_count.put(king2,0);
                battle_total_count.put(king2,0);
            }

            double king1_old_rating,king2_old_rating;
            double king1_new_rating,king2_new_rating;
            double king1_lowest_rating,king2_lowest_rating;
            double king1_highest_rating,king2_highest_rating;
            int king1_best_rank, king2_best_rank;
            int king1_worst_rank, king2_worst_rank;
            int king1_battles_total,king1_battles_won,king1_battles_lost;
            int king2_battles_total,king2_battles_won,king2_battles_lost;
            king1_old_rating = rating.get(king1);
            king2_old_rating = rating.get(king2);
            king1_lowest_rating = lowest_rating.get(king1);
            king2_lowest_rating = lowest_rating.get(king2);
            king1_highest_rating = highest_rating.get(king1);
            king2_highest_rating = highest_rating.get(king2);
            king1_best_rank = best_rank.get(king1);
            king2_best_rank = best_rank.get(king2);
            king1_worst_rank = worst_rank.get(king1);
            king2_worst_rank = worst_rank.get(king2);

            king1_battles_won = battle_won_count.get(king1);
            king1_battles_lost = battle_loss_count.get(king1);
            king1_battles_total = battle_total_count.get(king1);

            king2_battles_won = battle_won_count.get(king2);
            king2_battles_lost = battle_loss_count.get(king2);
            king2_battles_total = battle_total_count.get(king2);

            king1_battles_total++;
            king2_battles_total++;
            int king1_outcome, king2_outcome;
            switch (battle.getAttacker_outcome())
            {
                case NetworkUtil.OUTCOME_WIN:
                    king1_outcome = GOT_Util.OUTCOME_WIN;
                    king2_outcome = GOT_Util.OUTCOME_LOSS;
                    king1_battles_won++;
                    king2_battles_lost++;
                    break;
                case NetworkUtil.OUTCOME_LOSS:
                    king1_outcome = GOT_Util.OUTCOME_LOSS;
                    king2_outcome = GOT_Util.OUTCOME_WIN;
                    king2_battles_won++;
                    king1_battles_lost++;
                    break;
                default:
                    king1_outcome = king2_outcome = GOT_Util.OUTCOME_DRAW;
                    break;
            }

            king1_new_rating = EloRatingSystem.getNewRating(king1_old_rating,king2_old_rating,king1_outcome);
            king2_new_rating = EloRatingSystem.getNewRating(king2_old_rating,king1_old_rating,king2_outcome);
            if(king1_new_rating>king1_highest_rating)
                highest_rating.put(king1,king1_new_rating);
            if(king2_new_rating>king2_highest_rating)
                highest_rating.put(king2,king2_new_rating);
            if(king1_new_rating<king1_lowest_rating)
                lowest_rating.put(king1,king1_new_rating);
            if(king2_new_rating<king2_lowest_rating)
                lowest_rating.put(king2,king2_new_rating);

            rating.put(king1, king1_new_rating);
            rating.put(king2, king2_new_rating);

            int king1_current_rank,king2_current_rank;
            king1_current_rank = GOT_Util.getCurrentRank(king1_new_rating,king1,rating);
            king2_current_rank = GOT_Util.getCurrentRank(king2_new_rating,king2,rating);
            if(king1_current_rank<king1_best_rank)
                best_rank.put(king1,king1_current_rank);
            if(king2_current_rank<king2_best_rank)
                best_rank.put(king2,king2_current_rank);
            if(king1_current_rank>king1_worst_rank)
                worst_rank.put(king1,king1_current_rank);
            if(king2_current_rank>king2_worst_rank)
                worst_rank.put(king2,king2_current_rank);

            battle_won_count.put(king1,king1_battles_won);
            battle_loss_count.put(king1,king1_battles_lost);
            battle_total_count.put(king1,king1_battles_total);
            battle_won_count.put(king2,king2_battles_won);
            battle_loss_count.put(king2,king2_battles_lost);
            battle_total_count.put(king2,king2_battles_total);
        }

        MySQLiteHelper.initKingDataTable(this, rating , lowest_rating, highest_rating,
                best_rank, worst_rank, battle_won_count,battle_loss_count ,battle_total_count);

        SharedPreferenceManager pref = SharedPreferenceManager.getInstance(this);
        pref.saveKeyData(GOT_Util.LAST_UPDATE_META_DATA,String.valueOf(new Date().getTime()));

        loadKingRanking( MySQLiteHelper.getKingData(this));
    }

    @Override
    public void onError(String response, int requestCode) {
        GOT_Util.showAlertMessage("Error", response,Home.this);
        findViewById(R.id.tvNoResults).setVisibility(View.VISIBLE);
    }

    //not used
//    private void initializeSwipeRefresh() {
//        SwipeRefreshLayout swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
//        swipeRefresh.setDistanceToTriggerSync(270);
//        swipeRefresh.setColorSchemeResources(
//                R.color.refresh_progress_1,
//                R.color.refresh_progress_2,
//                R.color.refresh_progress_3);
//        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//
//            }
//        });
//    }


    private void initializeNavDrawer() {
        new NavDrawer(this,(RecyclerView)findViewById(R.id.navList));
        setupDrawer();
    }

    private void setupDrawer() {
        DrawerLayout mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
//        mDrawerLayout.
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

//        MenuItem menuItem;
//        menuItem = menu.add(Menu.NONE, R.id.menu_sign_out, 0, "Sign out");
//        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        searchItem.getActionView().setX(-54f);
        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextChange(String newText)
            {
                // this is your adapter that will be filtered
                mAdapter.getFilter().filter(newText);
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                // this is your adapter that will be filtered
                mAdapter.getFilter().filter(query);
                return true;
            }
        };
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
            searchView.setOnQueryTextListener(textChangeListener);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId())
        {
//            case R.id.menu_favorites:
//            {
//                startActivity(new Intent(Home.this, Favorites.class));
//                break;
//            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btnFilters:
                dialog = getDialog(R.layout.dialog_filters);
                updateDialogUI();
                dialog.findViewById(R.id.rbAll).setOnClickListener(this);
                dialog.findViewById(R.id.rbMinTenBattles).setOnClickListener(this);
                dialog.findViewById(R.id.rbLessThanTenBattles).setOnClickListener(this);

                dialog.findViewById(R.id.rbRank).setOnClickListener(this);
                dialog.findViewById(R.id.rbBattlesWon).setOnClickListener(this);
                dialog.findViewById(R.id.rbTotalBattles).setOnClickListener(this);
                dialog.findViewById(R.id.rbRankDsc).setOnClickListener(this);
                dialog.findViewById(R.id.rbBattlesWonDsc).setOnClickListener(this);
                dialog.findViewById(R.id.rbTotalBattlesDsc).setOnClickListener(this);

                dialog.findViewById(R.id.btnOk).setOnClickListener(this);
                dialog.findViewById(R.id.btnCancel).setOnClickListener(this);
                break;
            case R.id.rbAll:
                filterType = F_ALL;
                break;
            case R.id.rbLessThanTenBattles:
                filterType = F_LESS_THAN_TEN_BATTLES;
                break;
            case R.id.rbMinTenBattles:
                filterType = F_MORE_THAN_TEN_BATTLES;
                break;
            case R.id.rbRank:
                sortType = S_RANK_ASC;
                break;
            case R.id.rbBattlesWon:
                sortType = S_BATTLES_WON_ASC;
                break;
            case R.id.rbTotalBattles:
                sortType = S_TOTAL_BATTLES_ASC;
                break;
            case R.id.rbRankDsc:
                sortType = S_RANK_DSC;
                break;
            case R.id.rbBattlesWonDsc:
                sortType = S_BATTLES_WON_DSC;
                break;
            case R.id.rbTotalBattlesDsc:
                sortType = S_TOTAL_BATTLES_DSC;
                break;
            case R.id.btnCancel:
                dialog.dismiss();
                break;
            case R.id.btnOk:
                dialog.dismiss();
                processFilters();
                break;

        }
    }

    private void updateDialogUI() {
        switch (filterType)
        {
            case F_ALL:
                ((RadioButton)dialog.findViewById(R.id.rbAll)).setChecked(true);
                break;
            case F_MORE_THAN_TEN_BATTLES:
                ((RadioButton)dialog.findViewById(R.id.rbMinTenBattles)).setChecked(true);
                break;
            case F_LESS_THAN_TEN_BATTLES:
                ((RadioButton)dialog.findViewById(R.id.rbLessThanTenBattles)).setChecked(true);
                break;
        }
        switch (sortType)
        {
            case S_BATTLES_WON_ASC:
                ((RadioButton)dialog.findViewById(R.id.rbBattlesWon)).setChecked(true);
                break;
            case S_RANK_ASC:
                ((RadioButton)dialog.findViewById(R.id.rbRank)).setChecked(true);
                break;
            case S_RANK_DSC:
                ((RadioButton)dialog.findViewById(R.id.rbRankDsc)).setChecked(true);
                break;
            case S_BATTLES_WON_DSC:
                ((RadioButton)dialog.findViewById(R.id.rbBattlesWonDsc)).setChecked(true);
                break;
            case S_TOTAL_BATTLES_ASC:
                ((RadioButton)dialog.findViewById(R.id.rbTotalBattles)).setChecked(true);
                break;
            case S_TOTAL_BATTLES_DSC:
                ((RadioButton)dialog.findViewById(R.id.rbTotalBattlesDsc)).setChecked(true);
                break;
        }
    }

    private void processFilters()
    {
        List<KingsDO> filteredList = new ArrayList<>();
        switch (filterType)
        {
            case F_LESS_THAN_TEN_BATTLES:
            {
                for(KingsDO item : MySQLiteHelper.getKingData(this))
                {
                    long battles = Long.parseLong(item.getTotalBattles());
                    if(battles<10)
                        filteredList.add(item);
                }
                findViewById(R.id.btnFilters).setBackgroundResource(R.drawable.ic_filter_dot);
            }
            break;
            case F_MORE_THAN_TEN_BATTLES:
            {
                for(KingsDO item : MySQLiteHelper.getKingData(this))
                {
                    long battles = Long.parseLong(item.getTotalBattles());
                    if(battles>=10)
                        filteredList.add(item);
                }
                findViewById(R.id.btnFilters).setBackgroundResource(R.drawable.ic_filter_dot);
            }
                break;
            default:
            {
                filteredList = MySQLiteHelper.getKingData(this);
                findViewById(R.id.btnFilters).setBackgroundResource(R.drawable.ic_filter_dot);
            }
        }

        switch (sortType)
        {
            case S_BATTLES_WON_ASC:
            {
                Collections.sort(filteredList, new Comparator<KingsDO>() {
                    @Override
                    public int compare(KingsDO a1, KingsDO a2) {
                        long x,y;
                        x = Long.parseLong(a1.getBattlesWon());
                        y = Long.parseLong(a2.getBattlesWon());
                        return x < y ? -1 : x > y ? 1 : 0;
                    }
                });
                break;
            }
            case S_TOTAL_BATTLES_ASC:
            {
                Collections.sort(filteredList, new Comparator<KingsDO>() {
                    @Override
                    public int compare(KingsDO a1, KingsDO a2) {
                        long x,y;
                        x = Long.parseLong(a1.getTotalBattles());
                        y = Long.parseLong(a2.getTotalBattles());
                        return x < y ? -1 : x > y ? 1 : 0;
                    }
                });
                break;
            }
            case S_RANK_ASC:
            {
                Collections.sort(filteredList, new Comparator<KingsDO>() {
                    @Override
                    public int compare(KingsDO a1, KingsDO a2) {
                        long x,y;
                        x = Long.parseLong(a1.getCurrentRank());
                        y = Long.parseLong(a2.getCurrentRank());
                        return x < y ? -1 : x > y ? 1 : 0;
                    }
                });
                break;
            }
            case S_BATTLES_WON_DSC:
            {
                Collections.sort(filteredList, new Comparator<KingsDO>() {
                    @Override
                    public int compare(KingsDO a1, KingsDO a2) {
                        long x,y;
                        x = Long.parseLong(a1.getBattlesWon());
                        y = Long.parseLong(a2.getBattlesWon());
                        return x < y ? 1 : x > y ? -1 : 0;
                    }
                });
                break;
            }
            case S_TOTAL_BATTLES_DSC:
            {
                Collections.sort(filteredList, new Comparator<KingsDO>() {
                    @Override
                    public int compare(KingsDO a1, KingsDO a2) {
                        long x,y;
                        x = Long.parseLong(a1.getTotalBattles());
                        y = Long.parseLong(a2.getTotalBattles());
                        return x < y ? 1 : x > y ? -1 : 0;
                    }
                });
                break;
            }
            case S_RANK_DSC:
            {
                Collections.sort(filteredList, new Comparator<KingsDO>() {
                    @Override
                    public int compare(KingsDO a1, KingsDO a2) {
                        long x,y;
                        x = Long.parseLong(a1.getCurrentRank());
                        y = Long.parseLong(a2.getCurrentRank());
                        return x < y ? 1 : x > y ? -1 : 0;
                    }
                });
                break;
            }
        }

        loadKingRanking(filteredList);
    }

    public Dialog getDialog(int id)
    {
        if(dialog==null)
        {
            dialog = new AlertDialog.Builder(this).show();
            dialog.setCancelable(true);
//            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//            Window window = dialog.getWindow();
//            lp.copyFrom(window.getAttributes());
//            lp.width = JojoUtils.getScreenWidth(getWindowManager().getDefaultDisplay())
//                    - JojoUtils.ConvertToPx(getApplicationContext(), 40); //WindowManager.LayoutParams.WRAP_CONTENT;
//            window.setAttributes(lp);
        }
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(id, null, true);
        dialog.setContentView(view);
        dialog.show();
        dialog.getWindow()
                .clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }
}