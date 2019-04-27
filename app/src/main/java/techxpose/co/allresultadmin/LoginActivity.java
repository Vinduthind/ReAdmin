package techxpose.co.allresultadmin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        mAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("auth").child("admin");
        databaseReference.keepSynced(true);
        progressDialog=new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        email=findViewById(R.id.editemail);
        password=findViewById(R.id.editpassword);
        loginButton=findViewById(R.id.btnlogin);





        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin();
            }
        });

    }

    private void checkLogin() {
        progressDialog.setMessage("Login.....");
        progressDialog.show();
        String emailtext= email.getText().toString().trim();
        String passwordtext= password.getText().toString().trim();
        if(!TextUtils.isEmpty(emailtext)&&!TextUtils.isEmpty(passwordtext))
        {

            mAuth.signInWithEmailAndPassword(emailtext,passwordtext).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {

                        checkUserExist();
                        progressDialog.dismiss();

                    }
                    else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "login Error", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }

        else{

            Toast.makeText(this, "Enter Email and password", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkUserExist() {
        final String user_id=mAuth.getCurrentUser().getUid();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(user_id))
                {
                    //Toast.makeText(LoginActivity.this, dataSnapshot.child(user_id).child("status").get.toString(), Toast.LENGTH_SHORT).show();
                        Intent mainintent = new Intent(LoginActivity.this,ShowUpdateActivity.class);
                        mainintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainintent);


                }
                else
                {
                    Intent setupintent = new Intent(LoginActivity.this,ProfileActivity.class);
                    setupintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(setupintent);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, "Connection lost", Toast.LENGTH_SHORT).show();


            }
        });
    }

}
