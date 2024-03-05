package com.example.doanchill.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.MusicManager.PlaylistDetailActivity;
import com.bumptech.glide.Glide;
import com.example.doanchill.Class.Playlist;
import com.example.doanchill.R;

import java.util.ArrayList;
import java.util.List;

public class PlaylistMainAdapter extends RecyclerView.Adapter<PlaylistMainAdapter.PlaylistViewHolder> {
    private static Context context;
    private List<Playlist> playlists;

    public PlaylistMainAdapter(Context context,List<Playlist> playlists) {
        this.context = context;
        this.playlists = playlists;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_playlist, parent, false);
        return new PlaylistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        holder.bind(playlist);
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;
        private LinearLayout recCard;

        PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.main_playlist_image);
            textView = itemView.findViewById(R.id.main_playlist_name);
            recCard=itemView.findViewById(R.id.recCard);
        }

        void bind(Playlist playlist) {
            textView.setText(playlist.getName());
            Glide.with(itemView.getContext()).load(playlist.getImage()).into(imageView);
            recCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    Intent i=new Intent(context, PlaylistDetailActivity.class);
                    i.putExtra("playlist",playlist.getImage());
                    i.putExtra("key",playlist.getKey());
                    context.startActivity(i);
                }
            });
        }
    }
}

