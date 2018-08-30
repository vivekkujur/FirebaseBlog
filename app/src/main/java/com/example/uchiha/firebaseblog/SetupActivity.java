package com.example.uchiha.firebaseblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class SetupActivity extends AppCompatActivity {

    EditText setupname;
    ImageButton setupimage;
    Button setupdonebtn;

    private static final int GALLERY_REQUEST=1;
    private  Uri resultUri;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUser;
    private ProgressDialog mProgress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth=FirebaseAuth.getInstance();
        mDatabaseUser= FirebaseDatabase.getInstance().getReference().child("Users");
        storageReference= FirebaseStorage.getInstance().getReference("profile_image");

        mProgress= new ProgressDialog(this);



        setupdonebtn=findViewById(R.id.Setup_Done_btn);
        setupname=findViewById(R.id.Setup_name);
        setupimage=findViewById(R.id.Setup_image);

        setupimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent set_imageintent= new Intent(Intent.ACTION_GET_CONTENT);
                set_imageintent.setType("image/*");
                startActivityForResult(set_imageintent,GALLERY_REQUEST);
            }
        });

        setupdonebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setupDone();
            }
        });

    }

    private void setupDone() {
        final String name=setupname.getText().toString().trim();
        final String user_id= mAuth.getCurrentUser().getUid();

        if(!TextUtils.isEmpty(name) && resultUri != null){
            mProgress.setMessage("Finishing Setup...");
            mProgress.show();
            StorageReference mStorage=storageReference.child(resultUri.getLastPathSegment());
            mStorage.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    String downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();

                    mDatabaseUser.child(user_id).child("name").setValue(name);
                    mDatabaseUser.child(user_id).child("image").setValue(downloadUrl);

                    mProgress.dismiss();
                    Intent main_intent= new Intent(SetupActivity.this,MainActivity.class) ;
                    main_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(main_intent);



                }
            });

        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_REQUEST && resultCode== RESULT_OK){


            Uri Image_uri = data.getData();

            CropImage.activity(Image_uri)
                    .setAspectRatio(1,1)
                    .setFixAspectRatio(true)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);



        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                resultUri = result.getUri();
                setupimage.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
