package com.example.uchiha.firebaseblog;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.LoginFilter;
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

    EditText email,password;
    Button btn_login, btn_reg;

    private FirebaseAuth mAuth;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth=FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance().getReference().child("User");
        database.keepSynced(true);


        email=findViewById(R.id.loginemailfield);
        password=findViewById(R.id.loginpasswordfield);
        btn_login=findViewById(R.id.login_Btn);
        btn_reg=findViewById(R.id.login_not);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startlogin();
            }
        });


        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent reg_intent= new Intent(LoginActivity.this,RegisterActivity.class) ;
                reg_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(reg_intent);

            }
        });
    }

    private void startlogin() {

        String Email=email.getText().toString().trim();
        String Password= password.getText().toString().trim();

        if(!TextUtils.isEmpty(Email) && !TextUtils.isEmpty(Password)){

            mAuth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){

                        checkUserExist();

                    }else {
                        Toast.makeText(LoginActivity.this,"error login",Toast.LENGTH_LONG).show();
                    }


                }
            });

        }


    }

    private void checkUserExist() {

        final String user_id=mAuth.getCurrentUser().getUid();
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(user_id)){

                    Intent main_intent= new Intent(LoginActivity.this,MainActivity.class) ;
                    main_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(main_intent);


                }else{

                    Intent setup_intent= new Intent(LoginActivity.this,MainActivity.class) ;
                    setup_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(setup_intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
