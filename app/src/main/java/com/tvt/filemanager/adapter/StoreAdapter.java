package com.tvt.filemanager.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tvt.filemanager.R;
import com.tvt.filemanager.activity.MainActivity;
import com.tvt.filemanager.models.ItemStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StoreAdapter extends BaseAdapter {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
    private MainActivity context;
    private List<ItemStore> itemStoreList;
    private ItemClick itemClick;


    public StoreAdapter(MainActivity context, List<ItemStore> itemStoreList, ItemClick itemClick) {
        this.context = context;
        this.itemStoreList = itemStoreList;
        this.itemClick = itemClick;
    }

    public void setData(List<ItemStore> itemStoreList){
        this.itemStoreList = itemStoreList;
    }

    @Override
    public int getCount() {
        return itemStoreList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemStoreList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {

        ImageView img_folder, over_flow;
        TextView tvName, tvModifier;
        private int posstions;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;


        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_store, parent, false);
            holder.img_folder = convertView.findViewById(R.id.iv_folder);
            holder.tvName = convertView.findViewById(R.id.tv_name);
            holder.tvModifier = convertView.findViewById(R.id.tv_modifier);
            holder.posstions=position;
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ItemStore itemStore = itemStoreList.get(position);

        holder.tvName.setText(itemStore.getName());
        holder.tvModifier.setText(simpleDateFormat.format(itemStore.getLastModifier()));

        if (itemStore.getExtent() == null) {
            holder.img_folder.setImageResource(R.drawable.folder);
        } else {

            if (itemStore.getExtent().equals("mp3")) {
                holder.img_folder.setImageResource(R.drawable.music);

            }

            if (itemStore.getExtent().equals("txt")) {
                holder.img_folder.setImageResource(R.drawable.txt);

            }

        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClick.onClickItem(position);
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                itemClick.onClickLongItemView(position,v);
                return false;
            }
        });


        return convertView;
    }



    public interface ItemClick {
        void onClickItem(int position);
        void onClickLongItemView(int position,View view);
    }









}
