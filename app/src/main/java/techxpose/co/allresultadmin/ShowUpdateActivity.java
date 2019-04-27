package techxpose.co.allresultadmin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import techxpose.co.allresultadmin.Holder.ModelViewHolder;
import techxpose.co.allresultadmin.Model.ShowUpdateModel;

public class ShowUpdateActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private boolean doubleBackToExitPressedOnce = false;
    private TextView nav_name,nav_role,nav_email;
    private ImageView nav_image;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private MenuItem nav_status,nav_role_body,nav_result,nav_datesheet,nav_no_activity;
    private Menu menu;
    private Query mQuery;
    private AlertDialog alertDialog;
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private  String authUID,checkRole;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_update);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavigationView navigationView=findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        menu = navigationView.getMenu();
        mAuth=FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);


        alertDialog = new ProgressDialog(this);
        alertDialog.setMessage("Loading...");
        alertDialog.setCancelable(true);
        alertDialog.show();

        nav_status = menu.findItem(R.id.nav_status);
        nav_role_body = menu.findItem(R.id.nav_role);
        nav_result=menu.findItem(R.id.nav_result);
        nav_datesheet = menu.findItem(R.id.nav_datesheet);
        nav_no_activity =menu.findItem(R.id.nav_no_activity);

        nav_name = view.findViewById(R.id.nav_name);
        nav_role =view.findViewById(R.id.nav_role);
        nav_image =view.findViewById(R.id.nav_imageView);
        nav_email = view.findViewById(R.id.nav_email);

        recyclerView=findViewById(R.id.blog);
        fab = findViewById(R.id.fab);

        authUID=mAuth.getUid();
        mQuery=databaseReference.child("Result");


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShowUpdateActivity.this, MainActivity.class));

            }
        });

        if(mAuth.getCurrentUser()==null){

            Intent loginintent = new Intent(ShowUpdateActivity.this,LoginActivity.class);
            loginintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginintent);
            alertDialog.dismiss();
            finish();

        }


        else{
            databaseReference.child("auth").child("admin").child(authUID).addValueEventListener(new ValueEventListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.child("status").getValue().equals("Inactive")) {
                            fab.setVisibility(View.GONE);
                            nav_datesheet.setVisible(false);
                            nav_result.setVisible(false);
                            nav_no_activity.setVisible(true);
                            getPermissionData(dataSnapshot);
                            recyclerView.setVisibility(View.GONE);
                            alertDialog.dismiss();

                        } else {
                            fab.setVisibility(View.VISIBLE);
                            nav_datesheet.setVisible(true);
                            nav_result.setVisible(true);
                            nav_no_activity.setVisible(false);
                            getPermissionData(dataSnapshot);
                            recyclepostwork();
                            alertDialog.dismiss();
                        }
                    }catch (Exception e)
                    {

                        Intent loginintent = new Intent(ShowUpdateActivity.this,LoginActivity.class);
                        loginintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(loginintent);
                        alertDialog.dismiss();
                        finish();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }


        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        // find MenuItem you want to change

        // set new title to the MenuItem


        databaseReference.keepSynced(true);
    }


    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();

        }


        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);


    }

    public void getPermissionData(DataSnapshot dataSnapshot){
        nav_email.setText(mAuth.getCurrentUser().getEmail());
        final String status = dataSnapshot.child("status").getValue().toString();
        nav_name.setText(dataSnapshot.child("name").getValue().toString());
        checkRole = dataSnapshot.child("role").getValue().toString();
        nav_no_activity.setTitle(Html.fromHtml("<font color='#ff0000'>No activity is active for you!</font>"));

        if(status.equalsIgnoreCase("active"))
            nav_status.setTitle(Html.fromHtml("<font color='#BCD379'>Status : "+status+"</font>"));
        else
            nav_status.setTitle(Html.fromHtml("<font color='#ff0000'>Status : "+status+"</font>"));

        nav_role_body.setTitle("Role : "+dataSnapshot.child("role").getValue().toString());
        Picasso.get().load(dataSnapshot.child("image").getValue().toString()).resize(100,100)
                .centerCrop().into(nav_image);



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show_update, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_signout) {
            mAuth.signOut();
            startActivity(new Intent(ShowUpdateActivity.this,LoginActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_result) {
            mQuery=databaseReference.child("Result");
            alertDialog.show();
            recyclepostwork();
        } else if (id == R.id.nav_datesheet) {
            mQuery=databaseReference.child("DateSheet");
            alertDialog.show();
            recyclepostwork();
        } else if (id == R.id.nav_role) {

        } else if (id == R.id.nav_status) {

        }else if (id == R.id.nav_logout) {
            mAuth.signOut();
            startActivity(new Intent(ShowUpdateActivity.this,LoginActivity.class));
            finish();
        }

        DrawerLayout drawer =findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void recyclepostwork() {

        FirebaseRecyclerAdapter<ShowUpdateModel, ModelViewHolder> firebaseRecycleAdapter = new FirebaseRecyclerAdapter<ShowUpdateModel, ModelViewHolder>(

                ShowUpdateModel.class,
                R.layout.model_showupdate,
                ModelViewHolder.class,
                mQuery


        ) {
            @Override
            protected void populateViewHolder(final ModelViewHolder viewHolder, final ShowUpdateModel model, final int position) {
                System.out.println(mQuery.getRef().toString().contains("Result"));
                getRef(position).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            if(checkRole.contains("Admin")){
                                viewHolder.showVisibility();
                            }else if (!dataSnapshot.child("postedby").getValue().equals(authUID) ) {
                                viewHolder.hideVisibility();
                            }else{
                                viewHolder.showVisibility();
                            }

                        }catch(Exception e){
                            viewHolder.hideVisibility();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                if(mQuery.getRef().toString().contains("Result")){
                    viewHolder.setBranchname(model.getBranchname());
                    viewHolder.setdate(model.getResultDate());
                } else {
                    viewHolder.setBranchname(model.getName());
                    viewHolder.setdate(model.getCourse());
                }
                viewHolder.setResultlink(model.getResultlink());

                viewHolder.setExamination(model.getExamination());
                viewHolder.getDelete().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        getRef(position).removeValue();
                        Toast.makeText(ShowUpdateActivity.this,"Item Removed" , Toast.LENGTH_SHORT).show();

                    }
                });
                viewHolder.getMview().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }};
        final LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.canScrollVertically();
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(mLinearLayoutManager);
        firebaseRecycleAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                // int friendlyMessageCount = firebaseRecyclerAdapter.getItemCount();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.\
                alertDialog.dismiss();
                mLinearLayoutManager.scrollToPosition(positionStart);
                System.out.println("Total Count : "+positionStart);
                System.out.println("Total  : "+itemCount);

            }


        });

        recyclerView.setAdapter(firebaseRecycleAdapter);
        alertDialog.dismiss();
    }

}
