package com.example.doanchill.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.MusicManager.PlaylistDetailActivity;
import com.bumptech.glide.Glide;
import com.example.doanchill.Class.Playlist;
import com.example.doanchill.R;

import java.util.List;

public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.ExploreViewHolder> {
    private static Context context;
    private List<Playlist> playlists;

    public ExploreAdapter(Context context,List<Playlist> playlists) {
        this.context = context;
        this.playlists = playlists;
    }

    @NonNull
    @Override
    public ExploreAdapter.ExploreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_explore, parent, false);
        return new ExploreAdapter.ExploreViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExploreViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        holder.bind(playlist);
    }



    @Override
    public int getItemCount() {
        return playlists.size();
    }

    static class ExploreViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private LinearLayout recCard;

        ExploreViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageExplore);
            recCard=itemView.findViewById(R.id.cardExplore);
        }

        void bind(Playlist playlist) {
            Glide.with(itemView.getContext()).load(playlist.getImage()).into(imageView);
            recCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(context, PlaylistDetailActivity.class);
                    i.putExtra("playlist",playlist.getImage());
                    i.putExtra("key",playlist.getKey());
                    context.startActivity(i);
                }
            });
        }
    }
}
