package com.example.limin.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.security.Key;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by limin on 2016/5/21.
 */
public class SimpleFileChooser extends DialogFragment {

    private File currentFolder = Environment.getExternalStorageDirectory();

    private ArrayList<File> currentFileList = new ArrayList<>();

    private ListView list;

    private ListViewAdapter listViewAdapter;

    private AlertDialog alertDialog;

    public class ItemHolder {
        ImageView imageView;
        TextView textView;
        CheckBox checkBox;
    }

    public class ListViewAdapter extends BaseAdapter {

        private Context context = null;

        public ListViewAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return currentFileList.size();
        }

        @Override
        public Object getItem(int position) {
            if (currentFileList.size() > position)
                return currentFileList.get(position);

            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (currentFileList.size() >  position) {
                LayoutInflater inflater = LayoutInflater.from(context);
                View view = inflater.inflate(R.layout.file_list_item, null);
                ItemHolder holder = new ItemHolder();
                holder.imageView = (ImageView)view.findViewById(R.id.imageview);
                holder.textView = (TextView)view.findViewById(R.id.textview);
                holder.checkBox = (CheckBox)view.findViewById(R.id.checkbox);

                File file = currentFileList.get(position);

                holder.textView.setText(file.getName());
                if (file.isDirectory()) {
                    holder.imageView.setImageResource(R.drawable.folder);
                    holder.checkBox.setVisibility(View.INVISIBLE);
                } else {
                    holder.imageView.setImageResource(R.drawable.file);
                    holder.checkBox.setVisibility(View.VISIBLE);
                }

                return view;
            }

            return null;
        }
    }

    public SimpleFileChooser() {
        //refreshListView();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        list = new ListView(getActivity());
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File file = currentFileList.get(position);
                if (file.isDirectory()) {
                    currentFolder = file;
                    refreshListView();
                }
            }
        });
        listViewAdapter = new ListViewAdapter(getContext());
        list.setAdapter(listViewAdapter);
        builder.setTitle(currentFolder.getPath()).setView(list)
                .setCancelable(true)
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (KeyEvent.KEYCODE_BACK == keyCode && KeyEvent.ACTION_UP == event.getAction()) {
                            if (currentFolder.equals(Environment.getExternalStorageDirectory()))
                                return false;

                            currentFolder= currentFolder.getParentFile();
                            refreshListView();
                            return true;
                        }

                        return  false;
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                    }
                })
                .setPositiveButton(R.string.rightButton, null)
                .setNeutralButton(R.string.leftButton, null)
                .setNegativeButton(R.string.middleButton, null);

        alertDialog = builder.create();

        refreshListView();

        return alertDialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    private void refreshListView() {
        if (null != currentFolder) {
            currentFileList.clear();

            File [] files = currentFolder.listFiles();
            if (null == files)
                return;

            for (File file: files) {
                if (file.isDirectory() || file.isFile())
                    currentFileList.add(file);
            }

            Collections.sort(currentFileList);
            alertDialog.setTitle(currentFolder.getPath());
            listViewAdapter.notifyDataSetChanged();
        }
    }
}
