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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.security.Key;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by limin on 2016/5/21.
 */
public class SimpleFileChooser extends DialogFragment {

    public enum SELECT_TYPE {
        Single,
        Multi
    }

    public enum OPEN_TYPE {
        File,
        Folder
    }

    public interface OnSelectCompleteListener {
        /**
         * get selected file paths
         * @return
         */
        public void onSelectComplete(ArrayList<String> selctedFiles);
    }

    private OPEN_TYPE open_type = OPEN_TYPE.File;

    private SELECT_TYPE select_type = SELECT_TYPE.Single;

    private OnSelectCompleteListener listener;

    private int lastRadioButtonIndex = -1;

    private ArrayList<String> selectedFiles = new ArrayList<>();

    private File currentFolder = Environment.getExternalStorageDirectory();

    private ArrayList<File> currentFileList = new ArrayList<>();

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
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (currentFileList.size() >  position) {
                final File file = currentFileList.get(position);

                LayoutInflater inflater = LayoutInflater.from(context);
                View view = inflater.inflate(R.layout.file_list_item, null);
                ItemHolder holder = new ItemHolder();
                holder.imageView = (ImageView)view.findViewById(R.id.imageview);
                holder.textView = (TextView)view.findViewById(R.id.textview);
                holder.checkBox = (CheckBox)view.findViewById(R.id.checkbox);

                holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked)
                            selectedFiles.add(file.getAbsolutePath());
                        else
                            selectedFiles.remove(file.getAbsolutePath());
                    }
                });

                holder.textView.setText(file.getName());
                if (file.isDirectory()) {
                    holder.imageView.setImageResource(R.drawable.folder);
                } else {
                    holder.imageView.setImageResource(R.drawable.file);
                }

                if (SELECT_TYPE.Single == select_type)
                    holder.checkBox.setVisibility(View.INVISIBLE);
                else if (SELECT_TYPE.Multi == select_type) {
                    if (OPEN_TYPE.File == open_type && file.isDirectory())
                        holder.checkBox.setVisibility(View.INVISIBLE);
                }

                return view;
            }

            return null;
        }
    }

    public SimpleFileChooser() {
        //do nothing
    }

    /**
     * default type is File
     * @param open_type
     */
    public void setOpenType(OPEN_TYPE open_type) {
        this.open_type = open_type;
    }

    /**
     * default type is Single
     * @param select_type
     */
    public void setSelectType( SELECT_TYPE select_type) {
        this.select_type = select_type;
    }

    public void setOnSelectCompleteListener(OnSelectCompleteListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        ListView list = new ListView(getActivity());
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File file = currentFileList.get(position);
                if (file.isDirectory()) {
                    currentFolder = file;
                    refreshListView();
                } else if (file.isFile()) {
                    if (OPEN_TYPE.File == open_type && SELECT_TYPE.Single == select_type) {
                        selectedFiles.add(file.getAbsolutePath());
                        onSuccess();
                    }
                }
            }
        });
        listViewAdapter = new ListViewAdapter(getContext());
        list.setAdapter(listViewAdapter);
        builder.setTitle(currentFolder.getPath()).setView(list)
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (KeyEvent.KEYCODE_BACK == keyCode && KeyEvent.ACTION_UP == event.getAction()) {
                            if (currentFolder.equals(Environment.getExternalStorageDirectory())) {
                                if (OPEN_TYPE.Folder == open_type)
                                    return true;
                                else if (OPEN_TYPE.File == open_type)
                                    return false;
                            }

                            currentFolder= currentFolder.getParentFile();
                            refreshListView();
                            return true;
                        }

                        return  false;
                    }
                });

        if (SELECT_TYPE.Multi == select_type ||
                (SELECT_TYPE.Single == select_type && OPEN_TYPE.Folder == open_type)) {
            builder.setPositiveButton(R.string.ok, null) // we set the listener in onShowListener, so we can prevent the dialog from closing (if chosen folder isn't writable)
            .setNeutralButton(R.string.cancel, null); // we set the listener in onShowListener, so we can prevent the dialog from closing (if chosen folder isn't writable)
            if (OPEN_TYPE.Folder == open_type)
                builder.setNegativeButton(R.string.createfolder, null);
        }

        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b_positive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b_positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSuccess();
                    }
                });

                Button b_negative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                b_negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onNewFolder();
                    }
                });
            }
        });

        refreshListView();

        return alertDialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    private class FileComparator implements Comparator {

        @Override
        public int compare(Object lhs, Object rhs) {
            File leftFile = (File)lhs;
            File rightFile = (File)rhs;

            if (null == leftFile)
                return -1;

            if (null == rightFile)
                return 1;


            if (leftFile.isDirectory() && rightFile.isFile()) {
                    return -1;
            } else if (leftFile.isDirectory() && rightFile.isDirectory()) {
                return leftFile.getName().compareToIgnoreCase(rightFile.getName());
            } else if (leftFile.isFile() && rightFile.isDirectory()) {
                return 1;
            } else if (leftFile.isFile() && rightFile.isFile()) {
                String leftExt = "";
                String rightExt = "";

                int index = leftFile.getName().lastIndexOf(".");
                if (index < 0)
                    return -1;
                leftExt = leftFile.getName().substring(index);

                index = rightFile.getName().lastIndexOf(".");
                if (index < 0)
                    return 1;
                rightFile.getName().substring(index);

                if (0 == leftExt.compareTo(rightExt))
                    return leftFile.getName().compareToIgnoreCase(rightFile.getName());
                else {
                    return leftExt.compareTo(rightExt);
                }

            }

            return 0;
        }
    }

    private void refreshListView() {

        if (null == currentFolder)
            return;

        //1.clear last result
        currentFileList.clear();

        //2.get new list
        File [] files = currentFolder.listFiles();
        if (null != files) {
            if (open_type == OPEN_TYPE.File) {
                for (File file : files) {
                    if (file.canRead())
                        currentFileList.add(file);
                }
            } else if (open_type == OPEN_TYPE.Folder) {
                for (File file : files) {
                    if (file.isDirectory() && file.canRead())
                        currentFileList.add(file);
                }
            }
        }

        Collections.sort(currentFileList, new FileComparator());
        alertDialog.setTitle(currentFolder.getPath());
        listViewAdapter.notifyDataSetChanged();
    }

    private void onSuccess() {
        if (null != listener) {
            listener.onSelectComplete(selectedFiles);
        }
        alertDialog.dismiss();
    }

    private void onNewFolder() {
        final EditText editText = new EditText(getContext());
        editText.setSingleLine();

        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.enter_new_folder)
                .setView(editText)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newFolder = currentFolder.getAbsolutePath() + "/" + editText.getText();
                        File newFile = new File(newFolder);
                        if (newFile.exists()) {
                            Toast.makeText(getActivity(), R.string.folder_exists, Toast.LENGTH_SHORT).show();
                        } else {
                            if (newFile.mkdirs()) {
                                currentFolder = newFile;
                                refreshListView();
                            } else {
                                Toast.makeText(getActivity(), R.string.failed_create_folder, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create().show();
    }

}
