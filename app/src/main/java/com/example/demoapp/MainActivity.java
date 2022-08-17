package com.example.demoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    String[] items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        listView = (ListView)findViewById(R.id.listviews);
        runtimePermissions();
    }

    public void runtimePermissions(){
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                displaySong();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    public ArrayList<File> findSong(File f){
        ArrayList<File> arrayList = new ArrayList<>();
        File[] mp3files = f.listFiles();
        if (mp3files != null) {
            for (File single : mp3files) {
                if (single.isDirectory()) {
                    arrayList.addAll(findSong(single));
                }
                else if (single.getAbsolutePath().endsWith(".mp3")) {
                    int file_size = Integer.parseInt(String.valueOf(single.length()/1024));
                    if (file_size > 150){
                        arrayList.add(single);
                    }
                }
            }
        }
        return arrayList;
    }

    public void displaySong(){
        final ArrayList<File> mySong = findSong(Environment.getExternalStorageDirectory());

        items = new String[mySong.size()];

        for (int i=0;i<mySong.size();i++){
            items[i] = mySong.get(i).getName().toString().replace(".mp3","");
        }

        Arrays.sort(items);

        customAdapter customAdapter = new customAdapter();
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String songname = (String) listView.getItemAtPosition(i);
                startActivity(new Intent(getApplicationContext(), PlayerActivity.class)
                        .putExtra("songs",mySong)
                        .putExtra("songname",songname)
                        .putExtra("pos",i));
            }
        });
    }

    class customAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = getLayoutInflater().inflate(R.layout.list_item,null);
            TextView textSong = v.findViewById(R.id.textsong);
            textSong.setSelected(true);
            textSong.setText(items[i]);
            return v;
        }
    }
}