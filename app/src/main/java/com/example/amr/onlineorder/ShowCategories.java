package com.example.amr.onlineorder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowCategories extends AppCompatActivity {

    private ProgressDialog progressDialog;
    ArrayList<Category> data;
    DatabaseReference databaseReference;
    CategoriesAdapter categoriesAdapter;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_categories);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        data = new ArrayList<>();
        lv = (ListView) findViewById(R.id.listview_cat);
        progressDialog = new ProgressDialog(this);


        //displaying progress dialog while fetching images
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        databaseReference.child("categoriesAdmin").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                data.clear();
                for (DataSnapshot child : children) {
                    String uid = child.getKey();
                    String name = child.child("name").getValue().toString();
                    String color = child.child("color").getValue().toString();
                    String admin_id = child.child("admin_id").getValue().toString();
                    Category c = new Category(uid,name, color, admin_id);

                    if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(admin_id)) {
                        data.add(c);
                    }
                }
                categoriesAdapter = new CategoriesAdapter(ShowCategories.this, data);
                lv.setAdapter(categoriesAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                //   Toast.makeText(ShowCategories.this, data.get(position).getName(), Toast.LENGTH_SHORT).show();

                Bundle dataBundle = new Bundle();
                dataBundle.putString("cat_id", data.get(position).getId());
                dataBundle.putString("cat_color", data.get(position).getColor());
                Intent i = new Intent(ShowCategories.this, ShowProducts.class);
                i.putExtras(dataBundle);
                startActivity(i);

            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                           final int index, long arg3) {

                Bundle dataBundle = new Bundle();
                dataBundle.putString("id_cat", data.get(index).getId());
                dataBundle.putString("name_cat", data.get(index).getName());
                dataBundle.putString("color_cat", data.get(index).getColor());
                Intent i = new Intent(ShowCategories.this, SelectHowToDo.class);
                i.putExtras(dataBundle);
                startActivity(i);

                return true;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ShowCategories.this, AddCategory.class);
                startActivity(i);
            }
        });
    }



}