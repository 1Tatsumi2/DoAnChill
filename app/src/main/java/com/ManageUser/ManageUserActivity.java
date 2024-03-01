package com.ManageUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.MusicManager.MusicDetailActivity;
import com.MusicManager.MusicManagerActivity;
import com.example.doanchill.Adapters.UserAdapter;
import com.example.doanchill.Class.Song;
import com.example.doanchill.Class.Users;
import com.example.doanchill.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ManageUserActivity extends AppCompatActivity {

    List<Users> usersList;
    ListView lvUsers;
    SearchView searchView;
    FloatingActionButton fab;
    UserAdapter userAdapter;
    FirebaseFirestore fStore;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);
        lvUsers=findViewById(R.id.lvUsers);
        searchView=findViewById(R.id.searchUser);
        fab=findViewById(R.id.fabUser);
        usersList=new ArrayList<>();
        userAdapter=new UserAdapter(this,1,usersList);
        lvUsers.setAdapter(userAdapter);
        showAllUser();
        fStore=FirebaseFirestore.getInstance();
        CollectionReference collection=fStore.collection("users");
        collection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                {
                    Users users =documentSnapshot.toObject(Users.class);
                    users.setKey(documentSnapshot.getId());
                    usersList.add(users);
                }
                userAdapter.notifyDataSetChanged();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    showAllUser();
                } else {
                    searchList(newText);
                }
                return true;
            }
        });

        lvUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Users users = userAdapter.getItem(position);
                Intent i = new Intent(ManageUserActivity.this, UserDetailActivity.class);
                i.putExtra("user", users);
                i.putExtra("key",users.getKey());
                startActivity(i);
                finish();
            }
        });
    }
    public void searchList(String text) {
        ArrayList<Users> searchList = new ArrayList<>();
        for (Users data : usersList) {
            if(data.getfName().toLowerCase().contains(text.toLowerCase())){
                searchList.add(data);
            }
        }
        userAdapter.searchLst(searchList);
    }

    public void showAllUser() {
        userAdapter.searchLst((ArrayList<Users>) usersList);
    }
}