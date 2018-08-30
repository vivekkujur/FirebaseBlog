package com.example.uchiha.firebaseblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class uploadActivity extends AppCompatActivity {

    ImageButton add_image;
    EditText edit_title, edit_desc;
    Button button_submit;
    private static final int GALLERY_REQUEST=1;
    Uri image_uri=null;
    private ProgressDialog progressDialog;
    private  FirebaseStorage Storage;
    private StorageReference mStorageRef;
    private DatabaseReference database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);


         Storage = FirebaseStorage.getInstance();
         mStorageRef=Storage.getInstance().getReference();
         database = FirebaseDatabase.getInstance().getReference().child("Blog");


        add_image=findViewById(R.id.imageButton);
        edit_title=findViewById(R.id.editText_title);
        edit_desc=findViewById(R.id.editText_desc);
        button_submit=findViewById(R.id.submit_button);

        progressDialog =new ProgressDialog(this);

        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_REQUEST);

            }
        });



        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startPosting();

            }
        });



    }

    private void startPosting() {

        final String title=edit_title.getText().toString().trim();
        final String desc= edit_desc.getText().toString().trim();


        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(desc) && image_uri!=null ){
            progressDialog.setMessage("posting to blog...");
            progressDialog.show();

           // String img_name=image_uri.getLastPathSegment();
             final StorageReference riversRef = mStorageRef.child("Blog_image/"+image_uri.getLastPathSegment()+".jpg");

             UploadTask uploadTask=riversRef.putFile(image_uri);


           uploadTask
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content

                       // String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                          String downloadUrl= taskSnapshot.getDownloadUrl().toString();
                            DatabaseReference newpost=database.push();
                            newpost.child("title").setValue(title);
                            newpost.child("desc").setValue(desc);
                          newpost.child("image").setValue(downloadUrl);

                            progressDialog.dismiss();

                            startActivity(new Intent(uploadActivity.this, MainActivity.class));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                        }
                    });



        }




    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_REQUEST && resultCode == RESULT_OK){
            image_uri=data.getData();
            add_image.setImageURI(image_uri);

        }
    }
}
