package com.example.asc.activity.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asc.R;

import java.util.List;

public class SpaceAdapter extends RecyclerView.Adapter<SpaceAdapter.ViewHolder>{

    private List<Space> spaceList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView_date;
        ImageView imageView_photo;
        TextView textView_note;

        public ViewHolder(View view){
            super(view);
            textView_date=(TextView)view.findViewById(R.id.textView_date);
            imageView_photo=(ImageView)view.findViewById(R.id.imageView_photo);
            textView_note=(TextView)view.findViewById(R.id.textView_note);
        }
    }

    public SpaceAdapter(List<Space> spaceList){
        this.spaceList=spaceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.space,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        return  holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Space space=spaceList.get(position);
        holder.textView_date.setText(space.getDate());
        holder.imageView_photo.setImageBitmap(space.getBitmap());
        holder.textView_note.setText(space.getNote());
    }


    @Override
    public int getItemCount() {
        return spaceList.size();
    }
}
