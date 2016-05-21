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

        Button button1 = (Button)findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startListDialog();
            }
        });
    }

    public void startListDialog() {
        SimpleFileChooser dialog = new SimpleFileChooser();
        dialog.show(getSupportFragmentManager(), "Folder");
    }
}
