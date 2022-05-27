package com.example.rpm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.rpm.modelsDB.Students;

import java.util.List;

public class StudentsListAdapter extends ArrayAdapter<Students> {

    private LayoutInflater inflater;
    private Context thisContext;
    private int layout;
    private List<Students> students;

    public StudentsListAdapter(@NonNull Context context, int resource, @NonNull List<Students> objects) {
        super(context, resource, objects);
        this.students = objects;
        this.layout = resource;
        this.thisContext = context;
        this.inflater = LayoutInflater.from(context);
    }
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = inflater.inflate(this.layout, parent, false);

        TextView nameView = view.findViewById(R.id.std_first_name_text_view);

        TextView surnameView = view.findViewById(R.id.std_second_name_text_view);

        TextView postView = view.findViewById(R.id.std_position_text_view);

        setViewListeners(nameView, surnameView, postView, position);

        Students students = this.students.get(position);

        nameView.setText(students.firstName);
        surnameView.setText(students.secondName);
        postView.setText(students.form);

        return view;
    }
    private void setViewListeners(TextView nameView, TextView surnameView, TextView postView, int position){
        //Long Click
        nameView.setOnLongClickListener(v->{
            ((StudentsListActivity)thisContext).sortByColumn(0);
            return true;
        });
        surnameView.setOnLongClickListener(v->{
            ((StudentsListActivity)thisContext).sortByColumn(1);
            return true;
        });
        postView.setOnLongClickListener(v->{
            ((StudentsListActivity)thisContext).sortByColumn(2);
            return true;
        });

        //Short Click
        nameView.setOnClickListener(v->{
            ((StudentsListActivity)thisContext).simpleClick(position);
        });
        surnameView.setOnClickListener(v->{
            ((StudentsListActivity)thisContext).simpleClick(position);
        });
        postView.setOnClickListener(v->{
            ((StudentsListActivity)thisContext).simpleClick(position);
        });
    }


    public interface ISortByColumn{
        void sortByColumn(int columnId);
        void simpleClick(int position);
    }
}
