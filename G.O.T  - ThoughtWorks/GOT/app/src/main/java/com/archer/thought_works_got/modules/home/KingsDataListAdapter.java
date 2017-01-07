package com.archer.thought_works_got.modules.home;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.archer.thought_works_got.R;
import com.archer.thought_works_got.data_model.sql_db_models.KingsDO;
import com.archer.thought_works_got.modules.king_details.KingDetails;
import com.archer.thought_works_got.util.GOT_Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Swastik on 25-09-2016.
 */
public class KingsDataListAdapter
        extends RecyclerView.Adapter<KingsDataListAdapter.MyViewHolder>
        implements Filterable
{
    private Home mContext;
    private List<KingsDO> itemList;
    private List<KingsDO> originalList;

    private ItemFilter mFilter = new ItemFilter();

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public KingsDataListAdapter(List<KingsDO> _kingData, Home _context)
    {
        mContext = _context;
        itemList = _kingData;
        originalList = itemList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_king_rank, parent, false);

        return new MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


        String display_text = "<u>" + itemList.get(position).getName() + "</u>";
        holder.tvKingName.setText(Html.fromHtml(display_text));
        switch (itemList.get(position).getName())
        {
            case GOT_Util.MANCE:
                holder.ivKing.setImageResource(R.drawable.ic_mance_rayder);
                break;
            case GOT_Util.BALON:
                holder.ivKing.setImageResource(R.drawable.ic_balon);
                break;
            case GOT_Util.RENLY:
                holder.ivKing.setImageResource(R.drawable.ic_renly);
                break;
            case GOT_Util.JOFFREY:
                holder.ivKing.setImageResource(R.drawable.ic_joffrey);
                break;
            case GOT_Util.STANNIS:
                holder.ivKing.setImageResource(R.drawable.ic_stannis);
                break;
            case GOT_Util.ROB_STARK:
                holder.ivKing.setImageResource(R.drawable.ic_robb_stark);
                break;
        }
        display_text = "Rank: " + itemList.get(position).getCurrentRank();
        holder.tvRank.setText(display_text);
        display_text = itemList.get(position).getCurrentRating();
        if(display_text.indexOf(".")>0)
            display_text = display_text.substring(0,display_text.indexOf("."));
        display_text = "Rating: " + display_text;
        holder.tvRating.setText(display_text);
        display_text = "Total battles: " + itemList.get(position).getTotalBattles();
        holder.tvTotalBattles.setText(display_text);
        display_text = "Battles won: " + itemList.get(position).getBattlesWon();
        holder.tvBattlesWon.setText(display_text);

        holder.thisKing.setTag(position);
        holder.regionBattleLog.setTag(position);
    }

    @Override
    public int getItemViewType(int position) {
        return GOT_Util.TYPE_ROW_ITEM;
    }
    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivKing;
        TextView tvRank;
        TextView tvRating;
        TextView tvBattlesWon;
        TextView tvTotalBattles;
        TextView tvKingName;
        View thisKing;
        View regionBattleLog;
        public MyViewHolder(View view) {
            super(view);
            ivKing = (ImageView) view.findViewById(R.id.ivKing);
            tvRank = (TextView) view.findViewById(R.id.tvRank);
            tvRating = (TextView) view.findViewById(R.id.tvRating);
            tvBattlesWon = (TextView) view.findViewById(R.id.tvBattlesWon);
            tvTotalBattles = (TextView) view.findViewById(R.id.tvTotalBattles);
            tvKingName = (TextView) view.findViewById(R.id.tvKingName);
            regionBattleLog = view.findViewById(R.id.region_battle_log);
            thisKing = view;
            thisKing.setOnClickListener(this);
            regionBattleLog.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent nextIntent;
            int position = Integer.parseInt(String.valueOf(view.getTag()));
            switch (view.getId())
            {
                case R.id.region_battle_log:
                    nextIntent = new Intent(mContext, KingDetails.class);
                    nextIntent.putExtra(GOT_Util.KING_NAME,itemList.get(position).getName());
                    mContext.startActivity(nextIntent);
                    break;
                default:
                    nextIntent = new Intent(mContext, KingDetails.class);
                    nextIntent.putExtra(GOT_Util.KING_NAME,itemList.get(position).getName());
                    mContext.startActivity(nextIntent);
                    break;

            }
        }
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            List<KingsDO> items = new ArrayList<>();

            for(KingsDO item : originalList)
            {
                if(item.getName().toLowerCase().contains(filterString.toLowerCase()))
                    items.add(item);
            }
            itemList = items;

            results.count = items.size();
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notifyDataSetChanged();
        }

    }


}
