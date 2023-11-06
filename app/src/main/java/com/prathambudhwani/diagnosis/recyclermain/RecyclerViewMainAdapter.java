package com.prathambudhwani.diagnosis.recyclermain;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.prathambudhwani.diagnosis.R;

import java.util.ArrayList;

public class RecyclerViewMainAdapter extends RecyclerView.Adapter<RecyclerViewMainAdapter.ViewHolder> {
    Context context;
    ArrayList<DiagnoseListModel> diagnoseListModels;
    ItemClickListener mItemListener;


    public RecyclerViewMainAdapter(Context context, ArrayList<DiagnoseListModel> diagnoseListModels, ItemClickListener itemClickListener) {
        this.context = context;
        this.diagnoseListModels = diagnoseListModels;
        this.mItemListener=itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.recyclercard,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txttitle.setText(diagnoseListModels.get(position).title );
        holder.txtcontent.setText(diagnoseListModels.get(position).context);

        holder.itemView.setOnClickListener(view -> {
            mItemListener.onItemCick(diagnoseListModels.get(position),position);
        });
    }

    @Override
    public int getItemCount() {
        return diagnoseListModels.size();
    }



    public interface ItemClickListener{
        void onItemCick(DiagnoseListModel diagnoseListModel,int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView txttitle, txtcontent;
        LinearLayout llrow;
        CardView maincard;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txttitle=itemView.findViewById(R.id.txttitle);
            txtcontent=itemView.findViewById(R.id.txtcontent);
            llrow=itemView.findViewById(R.id.llrow);
            maincard=itemView.findViewById(R.id.maincard);

        }
    }
}
