package com.archer.toppr_c.modules.nav_drawer;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.archer.toppr_c.R;
import com.archer.toppr_c.modules.help.Help;
import com.archer.toppr_c.modules.home.Home;
import com.archer.toppr_c.modules.my_account.MyAccount;
import com.archer.toppr_c.util.SharedPreferenceManager;
import com.archer.toppr_c.util.TopprUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class NavDrawer
        extends RecyclerView.Adapter<NavDrawer.MyViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ROW_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    private Home mContext;
    private RecyclerView mDrawerList;
    private List<String> navItemText;
    private int icon_res[];


    public NavDrawer(Home _mContext, RecyclerView _mDrawerList) {
        mContext = _mContext;
        mDrawerList = _mDrawerList;
//        mDrawerList.setBackgroundResource(R.drawable.ic_nav_top);
        String[] osArray = mContext.getResources().getStringArray(R.array.nav_menu);
        navItemText = new ArrayList<>();
        Collections.addAll(navItemText, osArray);
        icon_res = new int[]
                {0,R.drawable.ic_user, R.drawable.ic_help,R.drawable.ic_exit,0};
        addDrawerItems();
    }


    private void addDrawerItems() {

        mDrawerList.setAdapter(this);
        mDrawerList.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mDrawerList.setLayoutManager(mLayoutManager);
        mDrawerList.addItemDecoration(new DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL));
        mDrawerList.setItemAnimator(new DefaultItemAnimator());
        mDrawerList.setAdapter(this);

        mDrawerList.addOnItemTouchListener(new RecyclerTouchListener(mContext, mDrawerList, new ClickListener() {
            final static int MY_ACCOUNT = 1;
            final static int HELP = 2;
            final static int EXIT = 3;

            @Override
            public void onClick(View view, int position) {
                Intent intent;
                switch (position)
                {
                    case MY_ACCOUNT:
                        intent = new Intent(mContext, MyAccount.class);
                        mContext.startActivityForResult(intent, TopprUtils.RC_MY_ACC_DATA);
                        break;
                    case HELP:
                        intent = new Intent(mContext, Help.class);
                        mContext.startActivity(intent);
                        break;
                    case EXIT:
                        mContext.finish();
                        break;

                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SharedPreferenceManager pref;
        View itemView;
        switch (viewType)
        {
            case TYPE_HEADER:
            {
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.adapter_nav_drawer_top, parent, false);
                pref = SharedPreferenceManager.getInstance(mContext);
                String u_id_text = "User id : " + pref.getKeyData(TopprUtils.USER_ID);
                if(!pref.getKeyData(TopprUtils.USER_FULL_NAME).isEmpty())
                    u_id_text+="\n"+pref.getKeyData(TopprUtils.USER_FULL_NAME);

                ((TextView)itemView.findViewById(R.id.user_info)).setText(u_id_text);

                TypedArray nav_drawer_bg = mContext.getResources().obtainTypedArray(R.array.nav_drawer_bg);
                itemView.findViewById(R.id.drawer_top_bg)
                        .setBackgroundResource
                                (nav_drawer_bg.getResourceId((int)
                                        (new Date().getTime()%nav_drawer_bg.length()), 0));
                nav_drawer_bg.recycle();

                return new MyViewHolder(itemView,viewType);
            }
            case TYPE_FOOTER:
            {
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.adapter_nav_drawer_bottom, parent, false);
                return new MyViewHolder(itemView,viewType);
            }
            default:
            {
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.adapter_nav_drawer_item, parent, false);
                return new MyViewHolder(itemView,viewType);
            }
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if(position==0 || position == navItemText.size()-1)
        {
//            ((View)holder.title.getParent()).setBackgroundResource(icon_res[position]);
//            holder.title.setText("asdasd");
        }
        else
        {
            String text = navItemText.get(position);
            holder.title.setText(text);
            holder.icon.setBackgroundResource(icon_res[position]);
        }
    }
    @Override
    public int getItemViewType(int position) {
        if (position==0)
            return TYPE_HEADER;
        if(position==navItemText.size()-1)
            return TYPE_FOOTER;

        return TYPE_ROW_ITEM;
    }
    @Override
    public int getItemCount() {
        return navItemText.size();
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }
    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title;
        public ImageView icon;
        public MyViewHolder(View view,int viewType) {
            super(view);
            switch (viewType)
            {
                case TYPE_HEADER:

                    break;
                case TYPE_FOOTER:
                {
                    view.findViewById(R.id.footer).setOnClickListener(this);
                }
                    break;
                default:
                    title = (TextView) view.findViewById(R.id.title);
                    icon = (ImageView) view.findViewById(R.id.icon);
                    break;
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.footer:
                {
                    String subject = "Reaching out";
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.setType("plain/text");
                    sendIntent.setData(Uri.parse("swsahu9@gmail.com"));
                    sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
                    sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "swsahu9@gmail.com" });
                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                    mContext.startActivity(sendIntent);
                    break;
                }
            }
        }
    }


}
