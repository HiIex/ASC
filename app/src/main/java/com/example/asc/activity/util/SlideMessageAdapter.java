package com.example.asc.activity.util;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.example.asc.R;

import java.util.List;

public class SlideMessageAdapter extends BaseRecyclerViewAdapter<Message> {

    //private List<Person> personList;
    private OnDeleteClickLister mDeleteClickListener;
    private MyDatabaseHelper myDatabaseHelper;

    public SlideMessageAdapter(Context context, List<Message> personList){
        super(context,personList, R.layout.message);
    }

    @Override
    protected void onBindData(RecyclerViewHolder holder, Message bean, int position) {
        View view=holder.getView(R.id.tv_delete);
        view.setTag(position);
        if(!view.hasOnClickListeners()){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mDeleteClickListener!=null){
                        mDeleteClickListener.onDeleteClick(view,(Integer)view.getTag());
                    }
                }
            });
        }

        ((TextView)holder.getView(R.id.textView_phone)).setText(bean.getPhone());
        ((TextView)holder.getView(R.id.textView_content)).setText(bean.getContent());
    }

    public void setOnDeleteClickListener(OnDeleteClickLister listener) {
        this.mDeleteClickListener = listener;
    }


    public interface OnDeleteClickLister {
        void onDeleteClick(View view, int position);
    }
}
