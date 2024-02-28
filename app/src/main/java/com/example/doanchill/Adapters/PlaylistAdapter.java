package com.example.doanchill.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.doanchill.Class.Playlist;
import com.example.doanchill.Class.Song;
import com.example.doanchill.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PlaylistAdapter extends ArrayAdapter<Playlist> {
    private List<Playlist> playlists;

    public PlaylistAdapter(@NonNull Context context, int resource, @NonNull List<Playlist> objects) {
        super(context, resource, objects);
        this.playlists=new ArrayList<>(objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist,null);
        TextView playlistTitle=convertView.findViewById(R.id.namePlaylist);
        TextView playlistAuthor=convertView.findViewById(R.id.authorPlaylist);
        ImageView image=convertView.findViewById(R.id.imagePlaylist);
        Playlist playlist=getItem(position);
        playlistTitle.setText(playlist.getName());
        playlist.getAuthor().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                playlistAuthor.setText(documentSnapshot.getString("fName"));
            }
        });
        Glide.with(getContext()).load(playlist.getImage()).into(image);
        return  convertView;

    }
    @Override
    public int getCount() {
        return playlists.size();
    }

    public void searchPlaylist(ArrayList<Playlist> searchList)
    {
        playlists = searchList;
        notifyDataSetChanged();
    }
}
