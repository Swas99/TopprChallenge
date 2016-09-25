package com.archer.toppr_c.modules.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.archer.toppr_c.R;
import com.archer.toppr_c.data_model.EventDO;
import com.archer.toppr_c.modules.event_details.EventDetails;
import com.archer.toppr_c.util.MySQLiteHelper;
import com.archer.toppr_c.util.SharedPreferenceManager;
import com.archer.toppr_c.util.TopprUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Swastik on 25-09-2016.
 */
public class EventListAdapter
        extends RecyclerView.Adapter<EventListAdapter.MyViewHolder>
        implements Filterable
{
    String u_id;
    private Home mContext;
    private List<EventDO> itemList;
    private List<EventDO> originalList;

    private ItemFilter mFilter = new ItemFilter();

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public EventListAdapter(List<EventDO> _ticketList, Home _context)
    {
        mContext = _context;
        itemList = _ticketList;
        originalList = itemList;

        SharedPreferenceManager pref = SharedPreferenceManager.getInstance(mContext);
        u_id = pref.getKeyData(TopprUtils.USER_ID);
//        clearCache();
    }

    private void clearCache() {
        SharedPreferenceManager pref = SharedPreferenceManager.getInstance(mContext);
        for(int i=0;i<itemList.size();i++)
            pref.deleteKey(String.valueOf(i));
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_event, parent, false);

        return new MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        SharedPreferenceManager pref = SharedPreferenceManager.getInstance(mContext);
        if(pref.getKeyData(itemList.get(position).getId()).isEmpty())
        {
            holder.ivLogo.setImageBitmap(null);
            holder.ivLogo.setBackgroundResource(R.drawable.ic_event_place_holder);
            String image_url = itemList.get(position).getImage();
            downloadAndSetImage(holder.ivLogo,image_url,itemList.get(position).getId());
        }
        else
            loadLogoFromCache(itemList.get(position).getId(),holder.ivLogo);



        String eventType =  itemList.get(position).getCategory();
        if(eventType.trim().toLowerCase().equals(TopprUtils.HIRING))
            holder.ivCategory.setBackgroundResource(R.drawable.ic_hiring);
        else
            holder.ivCategory.setBackgroundResource(R.drawable.ic_compititive);

        String name = "<u>" + itemList.get(position).getName() + "</u>";
        holder.tvName.setText(Html.fromHtml(name));

        holder.ivFav.setTag(position);
        holder.thisEvent.setTag(position);
        if(MySQLiteHelper.checkEventForFavorite(mContext,u_id,itemList.get(position).getId()))
        {
            itemList.get(position).setFav(true);
            holder.ivFav.setBackgroundResource(R.drawable.ic_full_star);
        }
        else
        {
            itemList.get(position).setFav(false);
            holder.ivFav.setBackgroundResource(R.drawable.ic_empty_star);
        }

    }

    private void loadLogoFromCache(final String id,final ImageView iv) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final SharedPreferenceManager pref = SharedPreferenceManager.getInstance(mContext);
                final Bitmap logo = TopprUtils.decodeBase64ToBitmap(pref.getKeyData(String.valueOf(id)));
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iv.setImageBitmap(logo);
                    }
                });
            }
        });
        t.start();
    }

    private void downloadAndSetImage(final ImageView imageView, final String _url, final String position) {
        imageView.setTag(_url);
        new AsyncTask<Void, Void, Bitmap >()
        {
            @Override
            protected Bitmap  doInBackground(Void... params) {
                Bitmap bmp = null;
                    URL url;
                    try {
                        url = new URL(_url);
                        final BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 10;
                        bmp = BitmapFactory.decodeStream(url
                                .openConnection().getInputStream());
                        SharedPreferenceManager pref = SharedPreferenceManager.getInstance(mContext);
                        pref.saveKeyData(String.valueOf(position),TopprUtils.encodeBitmapToBase64(bmp));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                return bmp;

            }

            @Override
            protected void onPostExecute(Bitmap  result) {
                if(result!=null)
                {
                    String url = String.valueOf(imageView.getTag());
                    if(url.equals(_url))
                    {
                        imageView.setBackgroundResource(0);
                        imageView.setImageBitmap(result);
                    }
                }

            }
        }.execute();
    }


    @Override
    public int getItemViewType(int position) {
        return TopprUtils.TYPE_ROW_ITEM;
    }
    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvName;
        ImageView ivFav;
        ImageView ivLogo;
        ImageView ivCategory;
        View thisEvent;
        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvName);
            ivFav = (ImageView) view.findViewById(R.id.ivFav);
            ivLogo = (ImageView) view.findViewById(R.id.ivLogo);
            ivCategory = (ImageView) view.findViewById(R.id.ivCategory);
            ivFav.setOnClickListener(this);
            thisEvent = view;
            thisEvent.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = Integer.parseInt(String.valueOf(view.getTag()));
            switch (view.getId())
            {
                case R.id.ivFav:
                {
                    if(itemList.get(position).getFav())
                    {
                        itemList.get(position).setFav(false);
                        view.setBackgroundResource(R.drawable.ic_empty_star);
                        MySQLiteHelper.deleteRowFromFavTable(mContext,u_id,itemList.get(position).getId());
                    }
                    else
                    {
                        itemList.get(position).setFav(true);
                        view.setBackgroundResource(R.drawable.ic_full_star);
                        MySQLiteHelper.insertRowToFavoriteTable(mContext,itemList.get(position),u_id);
                    }
                    break;
                }
                default:
                {
                    SharedPreferenceManager pref = SharedPreferenceManager.getInstance(mContext);
                    pref.saveKeyData(TopprUtils.ID,itemList.get(position).getId());
                    pref.saveKeyData(TopprUtils.NAME,itemList.get(position).getName());
                    pref.saveKeyData(TopprUtils.IMAGE,itemList.get(position).getImage());
                    if(itemList.get(position).getFav())
                        pref.saveKeyData(TopprUtils.IS_FAV, "1");
                    else
                        pref.saveKeyData(TopprUtils.IS_FAV, "");
                    pref.saveKeyData(TopprUtils.CATEGORY,itemList.get(position).getCategory());
                    pref.saveKeyData(TopprUtils.EXPERIENCE,itemList.get(position).getExperience());
                    pref.saveKeyData(TopprUtils.DESCRIPTION,itemList.get(position).getDescription());
                    Intent i = new Intent(mContext, EventDetails.class);
                    mContext.startActivity(i);
                }
            }
        }
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            List<EventDO> items = new ArrayList<>();

            for(EventDO item : originalList)
            {
                if(item.getDescription().toLowerCase().contains(filterString.toLowerCase())
                        || item.getName().toLowerCase().contains(filterString.toLowerCase())
                        || item.getCategory().toLowerCase().contains(filterString.toLowerCase()))
                    items.add(item);
            }

            itemList = items;
            results.count = items.size();
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clearCache();
            notifyDataSetChanged();
        }

    }


}
