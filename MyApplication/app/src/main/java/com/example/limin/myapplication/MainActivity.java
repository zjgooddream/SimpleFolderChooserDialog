package com.example.limin.myapplication;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickFileSingle(View view) {
        SimpleFileChooser dialog = new SimpleFileChooser();
        dialog.setOnSelectCompleteListener(listener);
        dialog.show();
    }

    public void onClickFileMulti(View view) {
        SimpleFileChooser dialog = new SimpleFileChooser();
        dialog.setSelectType(SimpleFileChooser.SELECT_TYPE.Multi);
        dialog.setOnSelectCompleteListener(listener);
        dialog.show();
    }

    public void onClickFolderSingle(View view) {
        SimpleFileChooser dialog = new SimpleFileChooser();
        dialog.setOpenType(SimpleFileChooser.OPEN_TYPE.Folder);
        dialog.setOnSelectCompleteListener(listener);
        dialog.show();
    }

    public void onClickFolderMulti(View view) {
        SimpleFileChooser dialog = new SimpleFileChooser();
        dialog.setOpenType(SimpleFileChooser.OPEN_TYPE.Folder);
        dialog.setSelectType(SimpleFileChooser.SELECT_TYPE.Multi);
        dialog.setOnSelectCompleteListener(listener);
        dialog.show();
    }

    private SimpleFileChooser.OnSelectCompleteListener listener = new SimpleFileChooser.OnSelectCompleteListener() {
        @Override
        public void onSelectComplete(ArrayList<String> selctedFiles) {
            String toastTip = "You selected :\n";
            for (String str :
                 selctedFiles) {
                toastTip += str + "\n";
            }
            Toast.makeText(MainActivity.this, toastTip, Toast.LENGTH_SHORT).show();
        }
    };
}
