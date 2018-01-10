package com.pdp.bkresv2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pdp.bkresv2.R;
import com.pdp.bkresv2.model.Lake;

import java.util.ArrayList;

/**
 * Created by vutuan on 20/12/2017.
 */

public class LakeAdapter extends RecyclerView.Adapter<LakeAdapter.ViewHolder>{
    private Context mContext;
    private ArrayList<Lake> mList;

    public LakeAdapter(Context mContext, ArrayList<Lake> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(mContext).inflate(R.layout.layout_item_lake,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtLake.setText(mList.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtLake;
        private LinearLayout layoutLake;

        public ViewHolder(View itemView) {
            super(itemView);
            txtLake= (TextView) itemView.findViewById(R.id.txtLake);
            layoutLake= (LinearLayout) itemView.findViewById(R.id.layoutLake);
        }
    }
}
