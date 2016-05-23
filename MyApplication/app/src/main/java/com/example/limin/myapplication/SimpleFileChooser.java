package com.example.limin.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by yyd on 2016/5/21.
 */
public class SimpleFileChooser {

    private Activity activity;
    
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

    private ArrayList<String> selectedFiles = new ArrayList<String>();

    private enum StorageType {
        Internal,
        External,
        USB
    }

    private int currentfolderDepth = 0;

    private File currentFolder = null;

    private ArrayList<File> currentFileList = new ArrayList<File>();

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

            ItemHolder holder = null;

            if (null == convertView) {
                holder = new ItemHolder();
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.file_list_item, null);

                holder.imageView = (ImageView)convertView.findViewById(R.id.imageview);
                holder.textView = (TextView)convertView.findViewById(R.id.textview);
                holder.checkBox = (CheckBox)convertView.findViewById(R.id.checkbox);

                convertView.setTag(holder);

            } else {
                holder = (ItemHolder)convertView.getTag();
            }

            final File file = currentFileList.get(position);


            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        selectedFiles.add(file.getAbsolutePath());
                    else
                        selectedFiles.remove(file.getAbsolutePath());
                }
            });

            if (0 == currentfolderDepth) {

                holder.checkBox.setVisibility(View.INVISIBLE);

                StorageType type = StorageType.External;
                if (file.equals(Environment.getExternalStorageDirectory()))
                    type = StorageType.Internal;
                else if(file.getAbsolutePath().toLowerCase().indexOf("usb") != -1)
                    type = StorageType.USB;

                if (StorageType.Internal == type) {
                    holder.textView.setText(R.string.internalstorage);
                    holder.imageView.setImageResource(R.drawable.internal);
                }
                else if (StorageType.External == type) {
                    holder.textView.setText(R.string.externalstorage);
                    holder.imageView.setImageResource(R.drawable.external);
                }
                else if (StorageType.USB == type) {
                    holder.textView.setText(R.string.usbstorage);
                    holder.imageView.setImageResource(R.drawable.usb);
                }
            } else {

                holder.textView.setText(file.getName());

                if (file.isDirectory()) {
                    holder.imageView.setImageResource(R.drawable.folder);
                } else {
                    holder.imageView.setImageResource(R.drawable.file);
                }

                if (SELECT_TYPE.Single == select_type || (SELECT_TYPE.Multi == select_type && OPEN_TYPE.File == open_type && file.isDirectory() ) )
                    holder.checkBox.setVisibility(View.INVISIBLE);
                else
                    holder.checkBox.setVisibility(View.VISIBLE);
            }

            return convertView;
        }
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

    public AlertDialog createDialog(Activity activity) {
        
        this.activity = activity;
        
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        ListView list = new ListView(activity);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File file = currentFileList.get(position);
                if (file.isDirectory()) {
                    goToFolder(file);
                } else if (file.isFile()) {
                    if (OPEN_TYPE.File == open_type && SELECT_TYPE.Single == select_type) {
                        selectedFiles.add(file.getAbsolutePath());
                        onSuccess();
                    }
                }
            }
        });
        listViewAdapter = new ListViewAdapter(activity);
        list.setAdapter(listViewAdapter);
        builder.setView(list)
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (KeyEvent.KEYCODE_BACK == keyCode && KeyEvent.ACTION_UP == event.getAction()) {
                            if (0 == currentfolderDepth) {
                                if (OPEN_TYPE.File == open_type && SELECT_TYPE.Single == select_type)
                                    return false;
                                else
                                    return true;
                            }

                            onBack();
                            return true;
                        }

                        return  false;
                    }
                });

        if (SELECT_TYPE.Multi == select_type ||
                (SELECT_TYPE.Single == select_type && OPEN_TYPE.Folder == open_type)) {
            builder.setPositiveButton(android.R.string.ok, null) // we set the listener in onShowListener, so we can prevent the dialog from closing (if chosen folder isn't writable)
            .setNeutralButton(android.R.string.cancel, null); // we set the listener in onShowListener, so we can prevent the dialog from closing (if chosen folder isn't writable)
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
                        if (OPEN_TYPE.Folder == open_type && SELECT_TYPE.Single == select_type)
                            selectedFiles.add(currentFolder.getAbsolutePath());
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

    public void show() {
        alertDialog.show();
    }

    private void readVolumeState() {
        StorageManager storageManager = (StorageManager)activity.getSystemService(Activity.STORAGE_SERVICE);

        try {
            Method method = storageManager.getClass().getMethod("getVolumePaths");
            String[] paths = (String[])method.invoke(storageManager);

            for (String string:
                 paths) {
                File file = new File(string);
                if (file.isDirectory() && file.canRead()) {
                    currentFileList.add(file);
                }
            }

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private class FileComparator implements Comparator<Object> {

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
        //1.clear last result
        currentFileList.clear();

        //2.get new list
        if (0 == currentfolderDepth) {
            //did not specify root directory
            readVolumeState();
            alertDialog.setTitle((OPEN_TYPE.File == open_type) ? R.string.filedialog : R.string.folderdialog);
        } else {
            File[] files = currentFolder.listFiles();
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
        }

        listViewAdapter.notifyDataSetChanged();
    }

    private void onSuccess() {
        alertDialog.dismiss();
        if (null != listener) {
            listener.onSelectComplete(selectedFiles);
        }
    }

    private void onNewFolder() {
        final EditText editText = new EditText(activity);
        editText.setSingleLine();

        new AlertDialog.Builder(activity)
                .setTitle(R.string.enter_new_folder)
                .setView(editText)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newFolder = currentFolder.getAbsolutePath() + "/" + editText.getText();
                        File newFile = new File(newFolder);
                        if (newFile.exists()) {
                            Toast.makeText(activity, R.string.folder_exists, Toast.LENGTH_SHORT).show();
                        } else {
                            if (newFile.mkdirs()) {
                                goToFolder(newFile);
                            } else {
                                Toast.makeText(activity, R.string.failed_create_folder, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create().show();
    }

    private void onBack() {
        currentfolderDepth--;
        currentFolder = currentFolder.getParentFile();
        refreshListView();
    }

    private void goToFolder(File file) {
        currentfolderDepth++;
        currentFolder = file;
        refreshListView();
    }
}
