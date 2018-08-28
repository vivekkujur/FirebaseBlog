package com.example.uchiha.firebaseblog;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private  DatabaseReference mref;
    private DatabaseReference mdatabaseuser;
    private FirebaseAuth mAuth;
    private  FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mref=FirebaseDatabase.getInstance().getReference().child("Blog");
        mdatabaseuser=FirebaseDatabase.getInstance().getReference().child("Users");

        mref.keepSynced(true);
        mdatabaseuser.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();

        authStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()== null){
                    Intent  login_intent= new Intent(MainActivity.this,LoginActivity.class) ;
                    login_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(login_intent);

                }
            }
        };

        recyclerView=(RecyclerView)findViewById(R.id.Blog_RecycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onStart() {
        super.onStart();

        checkUserExist();

      mAuth.addAuthStateListener(authStateListener);
        FirebaseRecyclerOptions<Blog> options =
                new FirebaseRecyclerOptions.Builder<Blog>()
                        .setQuery(mref,Blog.class)
                        .build();

        FirebaseRecyclerAdapter<Blog,BlogViewHolder> firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<Blog,BlogViewHolder>(options
        ){
            @NonNull
            @Override
            public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.blog_card, parent, false);

                return new BlogViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull BlogViewHolder holder, int position, @NonNull Blog model) {
                holder.setTitle(model.getTitle());
                holder.setDesc(model.getDesc());
                holder.setImage(model.getImage());




            }
        };

//       recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }


    private void checkUserExist() {

        final String user_id=mAuth.getCurrentUser().getUid();
        mdatabaseuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild(user_id)){

                    Intent setup_inten= new Intent(MainActivity.this,SetupActivity.class) ;
                    setup_inten.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(setup_inten);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    public static  class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView post_title,post_desc;
        ImageView post_image;


        public BlogViewHolder(View itemView) {
            super(itemView);

            mView=itemView;
             post_desc =mView.findViewById(R.id.desc_show);
             post_title=mView.findViewById(R.id.title_show);
        }

        public void setTitle(String title){

            post_title.setText(title);

        }

        public void setDesc (String desc){

            post_desc.setText(desc);
        }

        public void setImage(String image){
            post_image = (ImageView) mView.findViewById(R.id.image_show);
             Picasso.get().load(image).into(post_image);
         // Glide.with(ctx).load(image).into(post_image);
          // Picasso.get().load(image).into(post_image);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_menu,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.upload_image){
            startActivity(new Intent(MainActivity.this,uploadActivity.class));
        }

        if(item.getItemId()== R.id.signout){

            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        mAuth.signOut();
    }
}
