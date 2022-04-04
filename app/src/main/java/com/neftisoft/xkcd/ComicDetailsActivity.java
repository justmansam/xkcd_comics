package com.neftisoft.xkcd;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.DrawableCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class ComicDetailsActivity extends AppCompatActivity {

    private TextView comicNameTV;
    private TextView comicNumberTV;
    private TextView comicDateTV;
    private TextView comicAltTV;
    private ImageView comicImageIV;
    private ImageView shareComicIV;
    private ImageView favComicIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_details);

        Window window = ComicDetailsActivity.this.getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); //For grey status bar!
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS); //For grey status bar!
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(ComicDetailsActivity.this,R.color.darker_blue)); //For grey status bar!

        comicNameTV = findViewById(R.id.comicNameTV);
        comicNumberTV = findViewById(R.id.comicNumberTV);
        comicDateTV = findViewById(R.id.comicDateTV);
        comicAltTV = findViewById(R.id.comicAltTV);
        comicImageIV = findViewById(R.id.comicImageIV);
        shareComicIV = findViewById(R.id.shareComicIV);
        favComicIV = findViewById(R.id.favComicIV);

        Intent partyIntent = getIntent();
        String comicImgStr = partyIntent.getStringExtra("comicImg");
        String comicNum = partyIntent.getStringExtra("comicNum");
        String comicUrlStr = "https://xkcd.com/" + comicNum + "/info.0.json";

        // Instantiate the RequestQueue for Current Comic.
        RequestQueue currentComicQueue = Volley.newRequestQueue(this);
        // Request a string response from the provided URL.
        StringRequest currentComicStringRequest = new StringRequest(Request.Method.GET, comicUrlStr,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String month = response.substring(response.indexOf("month\":")+9, response.indexOf("num\":")-4);
                        String num = response.substring(response.indexOf("num\":")+6, response.indexOf("link\":")-3);
                        String link = response.substring(response.indexOf("link\":")+8, response.indexOf("year\":")-4);
                        String year = response.substring(response.indexOf("year\":")+8, response.indexOf("news\":")-4);
                        String news = response.substring(response.indexOf("news\":")+8, response.indexOf("safe_title\":")-4);
                        String safeTitle = response.substring(response.indexOf("safe_title\":")+14, response.indexOf("transcript\":")-4);
                        String transcript = response.substring(response.indexOf("transcript\":")+14, response.indexOf("alt\":")-4);
                        String alt = response.substring(response.indexOf("alt\":")+7, response.indexOf("img\":")-4);
                        String img = response.substring(response.indexOf("img\":")+7, response.indexOf("\"title\":")-3);
                        String title = response.substring(response.indexOf("\"title\":")+10, response.indexOf("\"day\":")-3);
                        String day = response.substring(response.indexOf("\"day\":")+8).substring(0,1);
                        //day = String.valueOf(day.charAt(0));

                        setDataInPlace(month,num,link,year,news,safeTitle,transcript,alt,img,title,day);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //textView.setText("That didn't work!");
            }
        });
        currentComicQueue.add(currentComicStringRequest);

        //TO SHARE COMIC
        shareComicIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, comicImgStr);
                shareIntent.setType("text/*");
                startActivity(Intent.createChooser(shareIntent, null));
            }
        });

        //TO FAV COMIC
        favComicIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawableCompat.setTint(favComicIV.getDrawable(),ContextCompat.getColor(getApplicationContext(), R.color.blue));
            }
        });
    }

    private void setDataInPlace(String month, String num, String link, String year, String news, String safeTitle, String transcript, String alt, String img, String title, String day) {
        //Toast.makeText(this, day, Toast.LENGTH_SHORT).show();

        comicNameTV.setText(title);
        comicNumberTV.setText("#" + num);
        comicDateTV.setText(month + "/" + year);
        comicAltTV.setText(alt);
        Glide.with(comicImageIV.getContext())
                .load(img)
                .fitCenter()
                .into(comicImageIV);
    }
}
