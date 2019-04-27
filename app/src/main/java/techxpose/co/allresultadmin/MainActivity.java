package techxpose.co.allresultadmin;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

import javax.xml.transform.Result;

import techxpose.co.allresultadmin.Holder.ModelViewHolder;
import techxpose.co.allresultadmin.Model.ShowUpdateModel;

public class MainActivity extends AppCompatActivity {
    Button msubmit;
    EditText mbranch;
    EditText mlink, myear;
    EditText mresultdate;
    String getdate;
    ProgressDialog mdialog;
    private DatabaseReference mdatabase,mdatabasedate,mdatabasenotification;
    RadioGroup radioGroup;
    Button notificationsubmit;
    EditText notificationField;
    String radioValue;
    private String[] dateSheetType;
    private Spinner spinner;
    private String getDateSheet;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = findViewById(R.id.dateSheetType);
        msubmit =  findViewById(R.id.submit);
        mbranch =  findViewById(R.id.branch_name);
        mlink =  findViewById(R.id.resultlink);
        mresultdate =  findViewById(R.id.dateformat);
        myear =  findViewById(R.id.year);
        mdialog = new ProgressDialog(this);
        radioGroup = findViewById(R.id.radiodecide);
        radioGroup.clearCheck();
        notificationField= findViewById(R.id.notification);
        notificationsubmit=findViewById(R.id.submitnotification);
        mAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();
       // radioGroup.clearCheck();no

        if(mAuth.getCurrentUser()==null){

            Intent loginintent = new Intent(MainActivity.this,LoginActivity.class);
            loginintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginintent);
            finish();

        }else{
            databaseReference.child("auth").child("admin").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child("status").getValue().equals("Inactive")){
                        Intent loginintent = new Intent(MainActivity.this,LoginActivity.class);
                        loginintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(loginintent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        dateSheetType = getResources().getStringArray(R.array.dateSheetType);
        ArrayAdapter<String> examTypeAdapter =  new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, dateSheetType);
        examTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(examTypeAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int index = adapterView.getSelectedItemPosition();



                getDateSheet=dateSheetType[index];
                Toast.makeText(MainActivity.this, getDateSheet, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        notificationsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String getnotification=notificationField.getText().toString().trim();
                if (getnotification.equals(""))
                {
                    getnotification="No Notification Yet";
                }
                mdatabasenotification = FirebaseDatabase.getInstance().getReference().child("update");
                mdatabasenotification.child("notification").setValue(getnotification);
                mdatabasenotification.child("date").setValue(getdate);
                notificationField.setText("");
                Toast.makeText(MainActivity.this, "Notification Update", Toast.LENGTH_SHORT).show();

            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    mdatabase = FirebaseDatabase.getInstance().getReference().child(rb.getText().toString());
                    mdatabasedate = FirebaseDatabase.getInstance().getReference().child("lastpostdate");
                    radioValue=rb.getText().toString();
                    if(radioValue.equals("Result")){
                        spinner.setVisibility(View.GONE);
                    }else{
                        spinner.setVisibility(View.VISIBLE);
                    }

                }

            }
        });
        mdialog.setMessage("Please Wait....");

        Date currentTime = Calendar.getInstance().getTime();
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(this);
        getdate = dateFormat.format(currentTime);


        msubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdialog.show();
                startPosting();
            }
        });
    }

    private void startPosting() {
        final String getbranch = mbranch.getText().toString().trim();
        final String getlink = mlink.getText().toString().trim();
        final String getyear = myear.getText().toString().trim();
        final String getresultdate = mresultdate.getText().toString().trim();
        if (getresultdate.equals("") || getlink.equals("") || getyear.equals("")  ) {
            Toast.makeText(this, "Fill All Contents ", Toast.LENGTH_SHORT).show();
            mdialog.dismiss();
        } else {
            mdatabasedate.child("date").setValue(getdate);
            DatabaseReference newpost = mdatabase.push();
            if(radioValue.equals("Result")) {
                newpost.child("resultDate").setValue(getresultdate);
                newpost.child("branchname").setValue(getbranch);
                newpost.child("resultlink").setValue(getlink);
                newpost.child("date").setValue(getdate);
                newpost.child("uid").setValue(mAuth.getUid());
                newpost.child("postedby").setValue(mAuth.getUid());
            }else {
                newpost.child("description").setValue(getresultdate);
                newpost.child("course").setValue(getDateSheet);
                newpost.child("name").setValue(getbranch);
                newpost.child("link").setValue(getlink);
                newpost.child("postedby").setValue(mAuth.getUid());
                newpost.child("uid").setValue(mAuth.getUid());
                newpost.child("date").setValue(getdate);
            }
            newpost.child("examination").setValue(getyear, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                   if(databaseError!=null) {
                       System.out.println("Error : " + databaseError.getMessage());
                       Toast.makeText(MainActivity.this, "Error : " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                       Toast.makeText(MainActivity.this, "Post Update again", Toast.LENGTH_SHORT).show();
                   }else
                       Toast.makeText(MainActivity.this, "Post Update Successfully", Toast.LENGTH_SHORT).show();

                   return;
                }
            });
            mbranch.setText("");
            mlink.setText("");
            mdialog.dismiss();
        }    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(MainActivity.this,ShowUpdateActivity.class));
    }


}