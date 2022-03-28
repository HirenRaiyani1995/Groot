package com.groot.app.iec.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.groot.app.iec.R;
import com.groot.app.iec.model.FilterListModel;

import java.util.ArrayList;

public class SelectFilterAdapter extends ArrayAdapter<FilterListModel> {
    private LayoutInflater inflater;
    private ArrayList<FilterListModel> objects;
    public ViewHolder holder = null;

    public SelectFilterAdapter(Context context, int textViewResourceId, ArrayList<FilterListModel> objects) {
        super(context, textViewResourceId, objects);
        inflater = ((Activity) context).getLayoutInflater();
        this.objects = objects;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        FilterListModel filterListModel = objects.get(position);
        View row = convertView;
        if (null == row) {
            holder = new ViewHolder();
            row = inflater.inflate(R.layout.spinner_row, parent, false);
            holder.name = (AppCompatTextView) row.findViewById(R.id.spinner_name);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        holder.name.setText(filterListModel.getName());
        return row;
    }

    static class ViewHolder {
        TextView name;
    }
}