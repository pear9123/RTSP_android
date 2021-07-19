package com.bae.message.ipcam.ui.home;


import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bae.message.R;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private final List<CardItem> mDataList;

    // 어댑터 클릭 이벤트
    public interface RecyclerViewClickListener {
        void onItemClicked(int position); // 내가 클릭한걸 외부 전달
        void onEditButtonClicked(int position, int _id);
        void onDeleteButtonClicked(int position, int _id);
    }

    private RecyclerViewClickListener mListener;

    public void setOnClickListener(RecyclerViewClickListener listener) {
        mListener = listener;
    }

    public RecyclerAdapter(List<CardItem> dataList) {
        mDataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false); // view를 강제로 지정
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) { // 데이터 세팅
        CardItem item = mDataList.get(position);
        holder.title.setText(item.getTitle());
        holder.contents.setText(item.getContents());
        if(mListener != null) { //외부에서 연결
            final int pos = position;
            final int _id = item.get_id();
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClicked(pos);
                }
            });
            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onEditButtonClicked(pos, _id);
                }
            });
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onDeleteButtonClicked(pos, _id);
                }
            });
        }
    }

    @Override
    public int getItemCount() {  // 어댑터가 가지고있는 아이템의 갯수 지정
        return mDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView contents;
        Button delete;
        Button edit;
        TextView _id;
       public ViewHolder(@NonNull View itemView) {
           super(itemView);
           title = itemView.findViewById(R.id.title_text);
           contents = itemView.findViewById(R.id.content_text);
           delete = itemView.findViewById(R.id.delete_button);
           edit = itemView.findViewById(R.id.edit_button);
           _id = itemView.findViewById(R.id._id);
       }
   }

}
