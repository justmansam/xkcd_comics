package com.neftisoft.xkcd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ComicSelectListener {

    String baseUrl = "https://xkcd.com/";
    String urlExtension = "/info.0.json";
    //Comic comic;

    RecyclerView recyclerView;
    ArrayList<Comic> comicsArrayList;
    ComicAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = MainActivity.this.getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); //For grey status bar!
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS); //For grey status bar!
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.darker_blue)); //For grey status bar!

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BottomFragmentComics()).commit();
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);

        recyclerView = findViewById(R.id.comicsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        comicsArrayList = new ArrayList<>();
        adapter = new ComicAdapter(this, comicsArrayList, this);

        // Instantiate the RequestQueue for Current Comic.
        RequestQueue currentComicQueue = Volley.newRequestQueue(this);
        String currentComicUrl = "https://xkcd.com/info.0.json";
        // Request a string response from the provided URL.
        StringRequest currentComicStringRequest = new StringRequest(Request.Method.GET, currentComicUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        int currentComicNum = Integer.parseInt(response.substring(response.indexOf("num\":")+6, response.indexOf("link\":")-3));

                        listComics(currentComicNum);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //textView.setText("That didn't work!");
            }
        });
        currentComicQueue.add(currentComicStringRequest);
    }

    private void listComics(int currentComicNum) {
        RequestQueue comicQueue = Volley.newRequestQueue(this);
        for (int i = currentComicNum; i > currentComicNum-100; i--) {
            String comicUrl = baseUrl + i + urlExtension;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, comicUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            setComicsArrayList(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Do something
                }
            });
            // Add the request to the RequestQueue.
            comicQueue.add(stringRequest);
        }
    }

    private void setComicsArrayList(String response) {

        //LONG AND HARD WAY!!!
        //String month = response.substring(response.indexOf("month\":")+9, response.indexOf("num\":")-4);
        String num = response.substring(response.indexOf("num\":")+6, response.indexOf("link\":")-3);
        //String link = response.substring(response.indexOf("link\":")+8, response.indexOf("year\":")-4);
        String year = response.substring(response.indexOf("year\":")+8, response.indexOf("news\":")-4);
        //String news = response.substring(response.indexOf("news\":")+8, response.indexOf("safe_title\":")-4);
        //String safeTitle = response.substring(response.indexOf("safe_title\":")+14, response.indexOf("transcript\":")-4);
        //String transcript = response.substring(response.indexOf("transcript\":")+14, response.indexOf("alt\":")-4);
        //String alt = response.substring(response.indexOf("alt\":")+7, response.indexOf("img\":")-4);
        String img = response.substring(response.indexOf("img\":")+7, response.indexOf("\"title\":")-3);
        //String title = response.substring(response.indexOf("\"title\":")+10, response.indexOf("\"day\":")-3);
        //String day = response.substring(response.indexOf("\"day\":")+8, response.indexOf("\"}"));

        Comic comic = new Comic();
        comic.setNum(num);
        comic.setYear(year);
        comic.setImg(img);
        comicsArrayList.add(comic);

        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onComicClicked(Comic comic) {
        //Toast.makeText(this, comic.getNum(), Toast.LENGTH_SHORT).show();
        String comicNum = comic.getNum();
        String comicImg = comic.getImg();

        Intent myIntent = new Intent(this, ComicDetailsActivity.class);
        myIntent.putExtra("comicNum", comicNum);
        myIntent.putExtra("comicImg", comicImg);
        this.startActivity(myIntent);
    }

    private NavigationBarView.OnItemSelectedListener navListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.nav_home:
                    selectedFragment = new BottomFragmentComics();
                    break;
                case R.id.nav_search:
                    selectedFragment = new BottomFragmentSearch();
                    break;
                case R.id.nav_fav:
                    selectedFragment = new BottomFragmentFavs();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

            return true;
        }
    };
}
