package com.example.asc.activity.util;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asc.R;
import com.example.asc.activity.ui.ShineButton;

import java.util.List;

public class PhonebookAdapter extends RecyclerView.Adapter<PhonebookAdapter.ViewHolder>{

    private List<Person> personList;
    private MyDatabaseHelper myDatabaseHelper;
    private Object AfterLoginActivity;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView_name;
        TextView textView_phone;
        EditText editText_name;
        EditText editText_phone;
        ImageButton change_name;
        ImageButton change_phone;
        ImageButton save_name;
        ImageButton save_phone;
        ShineButton shineButton;
        ImageButton delete;

        public ViewHolder(View view){
            super(view);
            textView_name=(TextView)view.findViewById(R.id.textView_name);
            textView_phone=(TextView)view.findViewById(R.id.textView_phone);
            editText_name=(EditText)view.findViewById(R.id.editText_name);
            editText_phone=(EditText)view.findViewById(R.id.editText_phone);
            change_name=(ImageButton)view.findViewById(R.id.update_name);
            change_phone=(ImageButton)view.findViewById(R.id.update_phone);
            save_name=(ImageButton)view.findViewById(R.id.save_name);
            save_phone=(ImageButton)view.findViewById(R.id.save_phone);
            shineButton=(ShineButton)view.findViewById(R.id.shineButton_special);
            delete=(ImageButton)view.findViewById(R.id.imageButton_delete);
        }

    }

    public PhonebookAdapter(List<Person> personList, Object afterLoginActivity){
        this.personList=personList;
        AfterLoginActivity = afterLoginActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.phonebook, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.shineButton.init((Activity) AfterLoginActivity);
        holder.editText_phone.setVisibility(View.INVISIBLE);
        holder.editText_name.setVisibility(View.INVISIBLE);
        holder.textView_phone.setVisibility(View.VISIBLE);
        holder.textView_name.setVisibility(View.VISIBLE);
        holder.change_name.setVisibility(View.VISIBLE);
        holder.change_phone.setVisibility(View.VISIBLE);
        holder.save_name.setVisibility(View.INVISIBLE);
        holder.save_phone.setVisibility(View.INVISIBLE);
        holder.change_phone.setEnabled(true);
        holder.change_name.setEnabled(true);
        holder.save_phone.setEnabled(false);
        holder.save_name.setEnabled(false);

        //点击修改名字
        holder.change_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.textView_name.setVisibility(View.INVISIBLE);
                holder.editText_name.setVisibility(View.VISIBLE);
                String name=holder.textView_name.getText().toString();
                holder.editText_name.setText(name);
                holder.change_name.setVisibility(View.INVISIBLE);
                holder.save_name.setVisibility(View.VISIBLE);
                holder.save_name.setEnabled(true);
                holder.change_name.setEnabled(false);
            }
        });

        //点击修改电话
        holder.change_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.textView_phone.setVisibility(View.INVISIBLE);
                holder.editText_phone.setVisibility(View.VISIBLE);
                String phone=holder.textView_phone.getText().toString();
                holder.editText_phone.setText(phone);
                holder.change_phone.setVisibility(View.INVISIBLE);
                holder.save_phone.setVisibility(View.VISIBLE);
                holder.save_phone.setEnabled(true);
                holder.change_phone.setEnabled(false);
            }
        });

        //点击保存名字
        holder.save_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=holder.editText_name.getText().toString();
                String phone=holder.textView_phone.getText().toString();
                holder.textView_name.setText(name);
                holder.textView_name.setVisibility(View.VISIBLE);
                holder.editText_name.setText(name);
                holder.editText_name.setVisibility(View.INVISIBLE);
                holder.save_name.setEnabled(false);
                holder.change_name.setEnabled(true);
                holder.change_name.setVisibility(View.VISIBLE);
                holder.save_name.setVisibility(View.INVISIBLE);

                myDatabaseHelper=new MyDatabaseHelper(view.getContext(),"Phonebook.db",null,1,1);
                SQLiteDatabase db=myDatabaseHelper.getWritableDatabase();
                ContentValues contentValues=new ContentValues();
                contentValues.put("name",name);
                db.update("Phonebook",contentValues,"phone=?",new String[]{phone});
                Toast.makeText(view.getContext(),"联系人姓名已在数据库中更新!",Toast.LENGTH_SHORT).show();
            }
        });

        //点击保存电话
        holder.save_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=holder.textView_name.getText().toString();
                String phone=holder.editText_phone.getText().toString();
                holder.textView_phone.setText(phone);
                holder.editText_phone.setText(phone);
                holder.textView_phone.setVisibility(View.VISIBLE);
                holder.editText_phone.setVisibility(View.INVISIBLE);
                holder.save_phone.setEnabled(false);
                holder.change_phone.setVisibility(View.VISIBLE);
                holder.change_phone.setEnabled(true);
                holder.save_phone.setVisibility(View.INVISIBLE);

                myDatabaseHelper=new MyDatabaseHelper(view.getContext(),"Phonebook.db",null,1,1);
                SQLiteDatabase db=myDatabaseHelper.getWritableDatabase();
                ContentValues contentValues=new ContentValues();
                contentValues.put("phone",phone);
                db.update("Phonebook",contentValues,"name=?",new String[]{name});
                Toast.makeText(view.getContext(),"联系人电话已在数据库中更新!",Toast.LENGTH_SHORT).show();
            }
        });

        //删除联系人
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                executeAnimation(view);
                final int position=holder.getAdapterPosition();
                CountDownTimer countDownTimer=new CountDownTimer(200,100) {
                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {
                        deleteItem(position,view);
                    }
                };
                countDownTimer.start();
            }
        });

        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Person person=personList.get(position);
        holder.textView_name.setText(person.getName());
        holder.textView_phone.setText(person.getPhone());
        holder.editText_phone.setHint(person.getPhone());
        holder.editText_name.setHint(person.getName());
        if(personList.get(position).getType()==Person.CONTACT_PUBLIC){
            holder.delete.setEnabled(false);
            holder.change_name.setEnabled(false);
            holder.change_phone.setEnabled(false);
        }else{
            holder.delete.setEnabled(true);
            holder.change_phone.setEnabled(true);
            holder.change_name.setEnabled(true);
        }
    }

    @Override
    public int getItemCount() {
        return personList.size();
    }

    //点击按钮缩放
    private void executeAnimation(View view){
        Animation scaleAnimation= AnimationUtils.loadAnimation(view.getContext(),R.anim.anim_scale);
        view.startAnimation(scaleAnimation);
    }

    //删除item
    public void deleteItem(final int position, final View view){
        final String name=personList.get(position).getName();
        personList.remove(position);
        //AfterLginActivity.teachList.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();

        //数据库删除
        new Thread(new Runnable() {
            @Override
            public void run() {
                myDatabaseHelper=new MyDatabaseHelper(view.getContext(),"Phonebook.db",null,1,1);
                SQLiteDatabase db=myDatabaseHelper.getWritableDatabase();
                db.delete("Phonebook","name=?",new String[]{name});
            }
        }).start();



    }
}
