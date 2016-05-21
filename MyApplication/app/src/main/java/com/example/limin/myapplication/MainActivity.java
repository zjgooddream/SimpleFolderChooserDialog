package com.example.limin.myapplication;

import android.app.Activity;
import android.app.Fragment;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickFileSingle(View view) {
        SimpleFileChooser dialog = new SimpleFileChooser();
        dialog.show(getSupportFragmentManager(), "File Single");
    }

    public void onClickFileMulti(View view) {
        SimpleFileChooser dialog = new SimpleFileChooser();
        dialog.setSelectType(SimpleFileChooser.SELECT_TYPE.Multi);
        dialog.show(getSupportFragmentManager(), "File Multi");
    }

    public void onClickFolderSingle(View view) {
        SimpleFileChooser dialog = new SimpleFileChooser();
        dialog.setOpenType(SimpleFileChooser.OPEN_TYPE.Folder);
        dialog.show(getSupportFragmentManager(), "Folder Single");
    }

    public void onClickFolderMulti(View view) {
        SimpleFileChooser dialog = new SimpleFileChooser();
        dialog.setOpenType(SimpleFileChooser.OPEN_TYPE.Folder);
        dialog.setSelectType(SimpleFileChooser.SELECT_TYPE.Multi);
        dialog.show(getSupportFragmentManager(), "Folder Multi");
    }
}
