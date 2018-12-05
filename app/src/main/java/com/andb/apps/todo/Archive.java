package com.andb.apps.todo;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.andb.apps.todo.settings.SettingsActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Archive extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (SettingsActivity.darkTheme) {
        //    this.setTheme(R.style.AppThemeDark);
        //} else {
            this.setTheme(R.style.AppThemeLight);
        //}
        setContentView(R.layout.activity_archive);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (SettingsActivity.darkTheme) {
            darkThemeSet(toolbar);
        }

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        Fragment archiveFragment = new ArchiveFragment();
        fragmentTransaction.add(R.id.archiveFragmentContainer, archiveFragment);
        fragmentTransaction.commit();
    }

    public void darkThemeSet(Toolbar toolbar) {
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorDarkPrimary));
        getWindow().getDecorView().setSystemUiVisibility(0);


    }

    public void onPause(){
        super.onPause();
        ArchiveTaskList.saveTasks(this);
        Log.d("save", "saving TaskList");
    }



}
