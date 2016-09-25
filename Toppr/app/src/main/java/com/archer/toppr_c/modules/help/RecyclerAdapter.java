/*
* Copyright (C) 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.archer.toppr_c.modules.help;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.archer.toppr_c.R;
import com.archer.toppr_c.data_model.HelpDO;
import com.archer.toppr_c.util.TopprUtils;

import java.util.List;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private Help mContext;
    private List<HelpDO> itemList;

    public RecyclerAdapter(Help _mContext, List<HelpDO> _ticketList) {
        mContext = _mContext;
        itemList = _ticketList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_help_data, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.header.setText(itemList.get(position).getHeader());
        holder.content.setText(itemList.get(position).getContent());
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
        public TextView header;
        public TextView content;
        public MyViewHolder(View view) {
            super(view);
            header = (TextView) view.findViewById(R.id.tvHeader);
            content = (TextView) view.findViewById(R.id.tvContent);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            TopprUtils.showAlertMessage(header.getText().toString(),
                    content.getText().toString(),mContext);
//            Dialog d = mContext.getDialog(R.layout.dialog_help);
//            ((TextView)d.findViewById(R.id.tvHeader)).setText(text.getText());
//            ((TextView)d.findViewById(R.id.tvContent)).setText(content.getText());
        }
    }


}
