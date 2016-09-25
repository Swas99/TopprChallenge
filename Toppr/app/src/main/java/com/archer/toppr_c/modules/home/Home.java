package com.archer.toppr_c.modules.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;

import com.archer.toppr_c.R;
import com.archer.toppr_c.data_model.EventDO;
import com.archer.toppr_c.data_model.ResponseDO;
import com.archer.toppr_c.modules.favorites.Favorites;
import com.archer.toppr_c.modules.login.Login;
import com.archer.toppr_c.modules.nav_drawer.NavDrawer;
import com.archer.toppr_c.network.NetworkRequest;
import com.archer.toppr_c.network.OnTaskCompleted;
import com.archer.toppr_c.util.MySQLiteHelper;
import com.archer.toppr_c.util.NetworkUtil;
import com.archer.toppr_c.util.SharedPreferenceManager;
import com.archer.toppr_c.util.TopprUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Home
        extends AppCompatActivity
        implements OnTaskCompleted, View.OnClickListener {
    private static final int RC_FETCH_EVENTS = 101;

    private static final int F_ALL = 1;
    private static final int F_HIRING = 2;
    private static final int F_OTHERS = 3;

    private static final int S_HIRING = 1;
    private static final int S_FAVORITES = 2;
    private static final int S_COMPETITIONS = 3;
    private static final int S_DEFAULT = 4 ;

    int sortType;
    int filterType;
    List<EventDO> events;
    EventListAdapter mAdapter;

    private long max_quota;
    private long available_quota;

    Dialog dialog;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();
    }

    private void init()
    {
        getSupportActionBar().setTitle("Events");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fetchEvents();
        initializeNavDrawer();

        findViewById(R.id.btnFilters).setOnClickListener(this);
    }

    private void loadEvents(List<EventDO> results)
    {
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.rvEvents);
        mRecyclerView.setLayoutManager( new LinearLayoutManager(this));
        if(results==null || results.size()==0)
            findViewById(R.id.tvNoResults).setVisibility(View.VISIBLE);
        else
        {
            mAdapter = new EventListAdapter(results,this);
            mRecyclerView.setAdapter(mAdapter);
            findViewById(R.id.tvNoResults).setVisibility(View.INVISIBLE);
        }
    }

    private void fetchEvents() {
        NetworkRequest req = new NetworkRequest(Home.this);
        req.fetchEvents(NetworkUtil.FETCH_EVENTS, RC_FETCH_EVENTS, this);
    }

    @Override
    public void onTaskCompleted(String response, int requestCode) {
        switch (requestCode)
        {
            case RC_FETCH_EVENTS:
            {
                try
                {
                    ObjectMapper objectMapper = new ObjectMapper();
                    ResponseDO result = objectMapper.readValue(response, ResponseDO.class);
                    max_quota = result.getQuote_max();
                    available_quota = result.getQuote_available();
                    events = result.getWebsites();
                    loadEvents(result.getWebsites());
                    filterType = F_ALL;
                    sortType = S_DEFAULT;
                }
                catch(IOException e)
                {
                    TopprUtils.showAlertMessage("Jackson Mapper error",e.getMessage(),Home.this);
                }
                break;
            }
        }

    }

    @Override
    public void onError(String response, int requestCode) {
        TopprUtils.showAlertMessage("Error", response,Home.this);
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

        MenuItem menuItem;
        menuItem = menu.add(Menu.NONE, R.id.menu_sign_out, 0, "Sign out");
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);

        MenuItem searchItem = menu.findItem(R.id.action_search);

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
            case R.id.menu_sign_out:
            {
                SharedPreferenceManager pref = SharedPreferenceManager.getInstance(getApplicationContext());
                pref.deleteKey(TopprUtils.USER_ID);
                finish();
                startActivity(new Intent(Home.this, Login.class));
                break;
            }
            case R.id.menu_favorites:
            {
                startActivity(new Intent(Home.this, Favorites.class));
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == TopprUtils.RC_MY_ACC_DATA)
        {
            if(resultCode == Activity.RESULT_OK){
                finish();
                startActivity(new Intent(Home.this, Login.class));
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btnFilters:
                dialog = getDialog(R.layout.dialog_filters);
                updateDialogUI();
                dialog.findViewById(R.id.rbHiring).setOnClickListener(this);
                dialog.findViewById(R.id.rbOthers).setOnClickListener(this);
                dialog.findViewById(R.id.rbAll).setOnClickListener(this);

                dialog.findViewById(R.id.rbHiringFirst).setOnClickListener(this);
                dialog.findViewById(R.id.rbCompetitiveFirst).setOnClickListener(this);
                dialog.findViewById(R.id.rbFavFirst).setOnClickListener(this);

                dialog.findViewById(R.id.btnOk).setOnClickListener(this);
                dialog.findViewById(R.id.btnCancel).setOnClickListener(this);
                break;
            case R.id.rbHiring:
                filterType = F_HIRING;
                break;
            case R.id.rbAll:
                filterType = F_ALL;
                break;
            case R.id.rbOthers:
                filterType = F_OTHERS;
                break;
            case R.id.rbHiringFirst:
                sortType = S_HIRING;
                break;
            case R.id.rbFavFirst:
                sortType = S_FAVORITES;
                break;
            case R.id.rbCompetitiveFirst:
                sortType = S_COMPETITIONS;
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
            case F_OTHERS:
                ((RadioButton)dialog.findViewById(R.id.rbOthers)).setChecked(true);
                break;
            case F_HIRING:
                ((RadioButton)dialog.findViewById(R.id.rbHiring)).setChecked(true);
                break;
        }
        switch (sortType)
        {
            case S_HIRING:
                ((RadioButton)dialog.findViewById(R.id.rbHiringFirst)).setChecked(true);
                break;
            case S_COMPETITIONS:
                ((RadioButton)dialog.findViewById(R.id.rbCompetitiveFirst)).setChecked(true);
                break;
            case S_FAVORITES:
                ((RadioButton)dialog.findViewById(R.id.rbFavFirst)).setChecked(true);
                break;
        }
    }

    private void processFilters()
    {

        List<EventDO> sortedList = new ArrayList<>();
        List<EventDO> filteredList = new ArrayList<>();
        switch (filterType)
        {
            case F_HIRING:
            {
                for(EventDO item : events)
                    if(item.getCategory().trim().toLowerCase().equals(TopprUtils.HIRING))
                        filteredList.add(item);
                findViewById(R.id.btnFilters).setBackgroundResource(R.drawable.ic_filter_dot);
            }
            break;
            case F_OTHERS:
            {
                for(EventDO item : events)
                    if(!item.getCategory().trim().toLowerCase().equals(TopprUtils.HIRING))
                        filteredList.add(item);
                findViewById(R.id.btnFilters).setBackgroundResource(R.drawable.ic_filter_dot);
            }
                break;
            default:
            {
                filteredList = events;
                findViewById(R.id.btnFilters).setBackgroundResource(R.drawable.ic_filter);
            }
        }

        SharedPreferenceManager pref = SharedPreferenceManager.getInstance(this);
        String user_id = pref.getKeyData(TopprUtils.USER_ID);
        switch (sortType)
        {
            case S_FAVORITES:
            {
                for(EventDO item : filteredList)
                    if( MySQLiteHelper.checkEventForFavorite(this,user_id,item.getId()))
                        sortedList.add(item);
                    else
                        item.setFav(false);

                for(EventDO item : filteredList)
                    if(!item.getFav())
                        sortedList.add(item);
                break;
            }
            case S_HIRING:
            {
                for(EventDO item : filteredList)
                    if(item.getCategory().trim().toLowerCase().equals(TopprUtils.HIRING))
                        sortedList.add(item);

                for(EventDO item : filteredList)
                    if(!item.getCategory().trim().toLowerCase().equals(TopprUtils.HIRING))
                        sortedList.add(item);
                break;
            }
            case S_COMPETITIONS:
            {
                for(EventDO item : filteredList)
                    if(!item.getCategory().trim().toLowerCase().equals(TopprUtils.HIRING))
                        sortedList.add(item);

                for(EventDO item : filteredList)
                    if(item.getCategory().trim().toLowerCase().equals(TopprUtils.HIRING))
                        sortedList.add(item);
                break;
            }
            default:
                sortedList = filteredList;
        }

        loadEvents(sortedList);
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