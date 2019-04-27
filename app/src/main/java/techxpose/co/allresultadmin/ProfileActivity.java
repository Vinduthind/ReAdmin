package techxpose.co.allresultadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mprofileimage;
    private EditText name;
    private EditText managerName;
    private EditText place;
    private EditText phone;
    private EditText adhaar;
    private EditText qualification;
    private EditText role;
    private EditText status,email,doj;
    private FirebaseAuth mAuth;
    private Uri imageuri=null;
    private Date currentTime;
    private java.text.DateFormat dateFormat;
    private static  final  int GALLERY_REQUEST =   1;
    private DatabaseReference mDatabaseUser;
    private StorageReference mStorageimage;
    ProgressDialog mprogress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mprofileimage=findViewById(R.id.profileImage);
        name=findViewById(R.id.name);
        managerName=findViewById(R.id.managerName);
        place=findViewById(R.id.Place);
        phone=findViewById(R.id.Phone);
        role=findViewById(R.id.role);
        status=findViewById(R.id.status);
        doj=findViewById(R.id.doj);
        qualification = findViewById(R.id.qualification);
        adhaar = findViewById(R.id.adhaar);
        mAuth=FirebaseAuth.getInstance();
        mStorageimage= FirebaseStorage.getInstance().getReference().child("Profile_images");
        mDatabaseUser= FirebaseDatabase.getInstance().getReference().child("auth").child("admin");
        mprogress=new ProgressDialog(this);
        email = findViewById(R.id.email);
        currentTime = Calendar.getInstance().getTime();
        dateFormat = android.text.format.DateFormat.getDateFormat(this);
        doj.setText(dateFormat.format(currentTime));

        if(status.getText().toString().equals("Inactive"))
        {
            status.setTextColor(Color.RED);
        }else{
            status.setTextColor(Color.GREEN);
        }

        mprofileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_REQUEST);

            }
        });




    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GALLERY_REQUEST && resultCode==RESULT_OK)
        {

            Uri  imageUri = data.getData();



            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageuri = result.getUri();

                mprofileimage.setImageURI(imageuri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mAuth.signOut();
        finish();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==R.id.action_submitpost)
        {
            startSetupAccount();


        }


        return super.onOptionsItemSelected(item);
    }

    private void startSetupAccount() {


        final String user_id = mAuth.getCurrentUser().getUid();
        final String name = this.name.getText().toString().trim();
        final String managerName = this.managerName.getText().toString().trim();
        final String place = this.place.getText().toString().trim();
        final String role = this.role.getText().toString().trim();
        final String phone = this.phone.getText().toString().trim();
        final String status = this.status.getText().toString().trim();
        final String adhaar = this.adhaar.getText().toString().trim();
        final String qualification = this.qualification.getText().toString().trim();
        final String email = this.email.getText().toString().trim();
        if (!TextUtils.isEmpty(name) && imageuri != null && !TextUtils.isEmpty(managerName)
                && !TextUtils.isEmpty(place) && !TextUtils.isEmpty(role) && !TextUtils.isEmpty(phone)
                && !TextUtils.isEmpty(status) && !TextUtils.isEmpty(adhaar)&& !TextUtils.isEmpty(qualification)
                && !TextUtils.isEmpty(email)) {
            mprogress.setMessage("finishing Setup.......");
            mprogress.show();
            final StorageReference filepath = mStorageimage.child(imageuri.getLastPathSegment());
            filepath.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                        Uri downloaduri = urlTask.getResult();

                    Toast.makeText(ProfileActivity.this, filepath.getDownloadUrl().toString(), Toast.LENGTH_SHORT).show();
                    mDatabaseUser.child(user_id).child("empid").setValue("10");
                    mDatabaseUser.child(user_id).child("name").setValue(name);
                    mDatabaseUser.child(user_id).child("managername").setValue(managerName);
                    mDatabaseUser.child(user_id).child("place").setValue(place);
                    mDatabaseUser.child(user_id).child("role").setValue(role);
                    mDatabaseUser.child(user_id).child("phone").setValue(phone);
                    mDatabaseUser.child(user_id).child("status").setValue(status);
                    mDatabaseUser.child(user_id).child("qualification").setValue(qualification);
                    mDatabaseUser.child(user_id).child("adhaar").setValue(adhaar);
                    mDatabaseUser.child(user_id).child("email").setValue(email);
                    mDatabaseUser.child(user_id).child("permission").setValue("read,write,modify");
                    mDatabaseUser.child(user_id).child("salary").setValue("1000");
                    mDatabaseUser.child(user_id).child("doj").setValue(dateFormat.format(currentTime));

                    mDatabaseUser.child(user_id).child("image").setValue(downloaduri.toString());

                    mprogress.dismiss();
                    Intent mainintent = new Intent(ProfileActivity.this, MainActivity.class);
                    mainintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainintent);

                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    System.out.println("link : "+task.getResult().toString());
                }
            });


        } else {

            Toast.makeText(this, "All fields are Mandatory", Toast.LENGTH_SHORT).show();
        }

    }

}
