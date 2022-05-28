package com.julo.weatherappjulo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.julo.weatherappjulo.adapter.FavouriteAdapter;
import com.julo.weatherappjulo.database.DBHelper;

import java.util.ArrayList;

public class FavouriteActivity extends AppCompatActivity {

    DBHelper DB;
    ArrayList<String> favouriteList;
    FavouriteAdapter favouriteAdapter;
    RecyclerView rv_listFavourite;
    SwipeRefreshLayout srl_refreshFavourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

//        initiate for sqlite database
        DB = new DBHelper(FavouriteActivity.this);

//        initiate favourite araylist and recyclerview
        favouriteList = new ArrayList<String>();
        rv_listFavourite = findViewById(R.id.rv_listFavourite);
        srl_refreshFavourite = findViewById(R.id.srl_refreshFavourite);

//        calling getting all data from sqlite function
        getAllSQliteData();

//        initiate facourite adapter
        favouriteAdapter = new FavouriteAdapter(FavouriteActivity.this,favouriteList);

//        setting recyclerview to apply favourite adapter
        rv_listFavourite.setAdapter(favouriteAdapter);

//        make recyclerview cant scroll
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        rv_listFavourite.setLayoutManager(linearLayoutManager);
        rv_listFavourite.setHasFixedSize(true);

//        add function onclick at recyclerciew favourite location and send data to mainactivity to show
        favouriteAdapter.setOnItemClickCallback(new FavouriteAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(String data) {
                Log.d("cekclicked", data);
                Intent transferCitytoMain = new Intent(getApplicationContext(), MainActivity.class);
                transferCitytoMain.putExtra("sendCity", data);
                startActivity(transferCitytoMain);
            }
        });

        srl_refreshFavourite.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllSQliteData();
                srl_refreshFavourite.setRefreshing(false);
            }
        });

    }

//    get all data from sqlite database
    private void getAllSQliteData() {
        Cursor cursor = DB.readAllCityData();
        if (cursor.getCount()==0){
            Toast.makeText(getApplicationContext(), "No favourite data exist!", Toast.LENGTH_SHORT).show();
        }else{
            while (cursor.moveToNext()){
                favouriteList.add(cursor.getString(0));
            }
        }
    }
}