package com.example.uchiha.firebaseblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText name,email,password;
    Button btn_register;

    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth=FirebaseAuth.getInstance();
        progressDialog= new ProgressDialog(this);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Users");


        name=findViewById(R.id.name_field);
        email=findViewById(R.id.email_field);
        password=findViewById(R.id.password_field);
        btn_register=findViewById(R.id.register_btn);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });
    }

    private void startRegister() {

        final String Name= name.getText().toString();
        String Email= email.getText().toString();
        String Password= password.getText().toString();

        if(!TextUtils.isEmpty(Name) && !TextUtils.isEmpty(Email) && !TextUtils.isEmpty(Password)){
            progressDialog.setMessage("Signing up..");
            progressDialog.show();
                mAuth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            String user_id= mAuth.getCurrentUser().getUid();
                            DatabaseReference current_user_id = mDatabase.child(user_id);
                            current_user_id.child("name").setValue(Name);
                            current_user_id.child("image").setValue("default");

                            progressDialog.dismiss();

                            Intent intent= new Intent(RegisterActivity.this,MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);


                        }
                    }
                });
        }

    }
}
