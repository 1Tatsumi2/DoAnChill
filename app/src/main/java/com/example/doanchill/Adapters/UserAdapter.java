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
import com.example.doanchill.Class.Users;
import com.example.doanchill.R;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends ArrayAdapter<Users> {
    private List<Users> userList;

    public UserAdapter(@NonNull Context context, int resource, @NonNull List<Users> objects) {
        super(context, resource, objects);
        this.userList = new ArrayList<>(objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, null);
        TextView name=convertView.findViewById(R.id.tvName);
        TextView email=convertView.findViewById(R.id.tvEmail);
        TextView role=convertView.findViewById(R.id.tvRole);
        ImageView image=convertView.findViewById(R.id.tvImageUser);
        Users user=getItem(position);
        name.setText(user.getfName());
        email.setText(user.getEmail());
        role.setText(user.getRole());
        Glide.with(getContext()).load(user.getImage()).into(image);
        return convertView;
    }
    @Override
    public int getCount() {
        return userList.size();
    }
    public void searchLst(ArrayList<Users> searchList)
    {
        userList = searchList;
        notifyDataSetChanged();
    }
}
