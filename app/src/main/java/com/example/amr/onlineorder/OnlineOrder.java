package com.example.amr.onlineorder;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

// el mfrod task Ahmed Esmat
public class OnlineOrder extends AppCompatActivity {
    ListView viewAllOrders;
    private ProgressDialog progressDialog;
    ArrayList<Product> products;
    DatabaseReference mDataRef;
    ArrayList<Order> orderlist;
    private DatabaseReference mF;
    private FirebaseDatabase mFirebaseInstance;
    DatabaseReference mData;
    String username = "";
    String adm_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_order);

        Bundle extras = getIntent().getExtras();
        adm_id = extras.getString("ad_id");

        orderlist = new ArrayList<>();
        viewAllOrders = (ListView) findViewById(R.id.listViewOrders);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDataRef = database.getReference();
        mDataRef.child("Order").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                progressDialog.dismiss();
                orderlist.clear();
                Order oneOrder;
                products = new ArrayList<>();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for (DataSnapshot child : children) {

                    oneOrder = child.getValue(Order.class);
                    String admin_id = child.child("brand_id").getValue().toString();

                    if (adm_id.equals(admin_id)) {
                        orderlist.add(oneOrder);
                    }

                }
                // bt3red mn hena el orderlist fel listview
                CustomAdapter myAdapter = new CustomAdapter(orderlist);
                viewAllOrders.setAdapter(myAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    /**
     * Adapter
     */
    class CustomAdapter extends BaseAdapter {
        ArrayList<Order> orderArrayList = new ArrayList<>();

        CustomAdapter(ArrayList<Order> orderArrayList) {
            this.orderArrayList = orderArrayList;
        }

        @Override
        public int getCount() {
            return orderArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return orderArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater linflater = getLayoutInflater();

            View view1 = linflater.inflate(R.layout.row_order_of_admin, null);
            final Spinner state = (Spinner) view1.findViewById(R.id.spinnerstatus);

            final TextView userIDD = (TextView) view1.findViewById(R.id.customer);

            if (orderlist.get(position).getState().equals("Pending")) {
                List<String> list = new ArrayList<String>();
                list.add("Pending");
                list.add("InProgress");
                list.add("Delivered");

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(OnlineOrder.this, android.R.layout.simple_spinner_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                state.setAdapter(adapter);

            } else if (orderlist.get(position).getState().equals("InProgress")) {
                List<String> list = new ArrayList<String>();
                list.add("InProgress");
                list.add("Pending");
                list.add("Delivered");

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(OnlineOrder.this, android.R.layout.simple_spinner_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                state.setAdapter(adapter);
            } else if (orderlist.get(position).getState().equals("Delivered")) {
                List<String> list = new ArrayList<String>();
                list.add("Delivered");
                list.add("Pending");
                list.add("InProgress");

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(OnlineOrder.this, android.R.layout.simple_spinner_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                state.setAdapter(adapter);
            }


            TextView ordername = (TextView) view1.findViewById(R.id.productsname);
            for (Product s : orderlist.get(position).items) {
                ordername.append(s.getName() + " \n");
            }


            /**
             * User Name
             */
            progressDialog.show();
            mData = FirebaseDatabase.getInstance().getReference();
            mData.child("users").addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                    progressDialog.dismiss();

                    for (DataSnapshot child : children) {

                        String uid = child.child("id").getValue().toString();
                        String name = child.child("name").getValue().toString();
                        if (orderArrayList.get(position).userID.equals(uid)) {

                            username = name;

                        }
                    }
                    userIDD.setText("Cust: " + username);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int sposition, long id) {
                    String s = ((String) parent.getItemAtPosition(sposition));

                    mFirebaseInstance = FirebaseDatabase.getInstance();
                    mF = mFirebaseInstance.getReference("Order");
                    mF.child(orderArrayList.get(position).getId()).child("state").setValue(state.getSelectedItem().toString());

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // TODO Auto-generated method stub
                }
            });


            TextView orderprice = (TextView) view1.findViewById(R.id.totalprice);
            orderprice.setText("Total Price: " + orderArrayList.get(position).totalPrice.toString() + " LE");

            return view1;
        }
    }


}