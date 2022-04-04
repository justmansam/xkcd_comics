package com.neftisoft.xkcd;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ComicAdapter extends RecyclerView.Adapter<ComicAdapter.ViewHolder> {

    LayoutInflater inflater;
    List<Comic> comicsList;
    private ComicSelectListener csListener;

    public ComicAdapter(Context context, List<Comic> comics, ComicSelectListener csListener) {
        this.inflater = LayoutInflater.from(context);
        this.comicsList = comics;
        this.csListener = csListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_comics_rv, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.comicsNumber.setText(comicsList.get(position).getNum());
        holder.comicsYear.setText(comicsList.get(position).getYear());
        Picasso.get().load(comicsList.get(position).getImg()).into(holder.comicsImage);

        holder.comicsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                csListener.onComicClicked(comicsList.get(position));
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView comicsNumber, comicsYear;
        ImageView comicsImage;
        CardView comicsCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            comicsNumber = itemView.findViewById(R.id.comicsNumberTextView);
            comicsYear = itemView.findViewById(R.id.comicsYearTextView);
            comicsImage = itemView.findViewById(R.id.comicsImageView);
            comicsCardView = itemView.findViewById(R.id.comicsCardView);
        }
    }

    @Override
    public int getItemCount() {
        return comicsList.size();
    }

}
