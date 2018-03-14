package com.gconnectdroid.blogapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mBlogList;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUsers;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Boolean mProcessLike = false;
    private DatabaseReference mDatabaseLike;
    private DatabaseReference mDatabaseCurrentUser;
    private Query mQueryCurrentUser;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener= new  FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
             if (firebaseAuth.getCurrentUser()==null){
                 Intent loginIntent =new Intent(MainActivity.this,LoginActivity.class);
                 loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                 startActivity(loginIntent);
             }
             /*else{
                 checkUserExist();
             }*/
            }
        };

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseUsers=FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseLike =FirebaseDatabase.getInstance().getReference().child("Likes");


           /* String currentUserId= mAuth.getCurrentUser().getUid();
            mDatabaseCurrentUser= FirebaseDatabase.getInstance().getReference().child("Blog");
            mQueryCurrentUser = mDatabaseCurrentUser.orderByChild("uid").equalTo(currentUserId);
      */




        mDatabaseUsers.keepSynced(true);
        mDatabase.keepSynced(true);
        mDatabaseLike.setValue(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        mBlogList=(RecyclerView) findViewById(R.id.blog_list);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(layoutManager);
//        mBlogList.setLayoutManager(new LinearLayoutManager(this));
      checkUserExist();
    }
    @Override
    protected void onStart(){
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
        FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                Blog.class,
                R.layout.blog_row,
                BlogViewHolder.class,
                mDatabase
              //  mQueryCurrentUser
        ){
            @Override
            public void populateViewHolder(BlogViewHolder viewHolder, Blog model, int position){
                final String post_key = getRef(position).getKey();


                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getApplicationContext(),model.getImage());
                viewHolder.setUsername(model.getUsername());

                viewHolder.setLikeBtn(post_key);
                viewHolder.mView.setOnClickListener(new View.OnClickListener(){
                    @Override
                            public void onClick(View view){
//                        Toast.makeText(MainActivity.this,post_key, Toast.LENGTH_LONG).show();
                        Intent SingleBlogIntent = new  Intent(MainActivity.this, BlogSingleActivity.class);
                        SingleBlogIntent.putExtra("blog_id",post_key);
                        startActivity(SingleBlogIntent);

                    }
                });

                viewHolder.mLikeBtn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                      mProcessLike = true;


                            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (mProcessLike) {
                                        if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                                            mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                            mProcessLike = false;

                                        } else {
                                            mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("RandomValue");
                                            mProcessLike = false;
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                    }
                                                       }
                );
            }

        };
        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }


   /* @Override
    protected void onStop() {
        super.onStop();

        if(mAuthListener !=null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
*/
  private void checkUserExist() {
        if (mAuth.getCurrentUser() != null) {
            final String user_id = mAuth.getCurrentUser().getUid();
            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(user_id)) {
                        Intent mainIntent = new Intent(MainActivity.this, SetUpActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    public static class BlogViewHolder extends RecyclerView.ViewHolder{
       View mView;
//        TextView post_username;
         ImageButton mLikeBtn;
        DatabaseReference mDatabaseLike;
        FirebaseAuth mAuth;

        TextView post_title;
        TextView post_desc;
        TextView post_username;

        public BlogViewHolder(View itemView){
            super(itemView);
            mView =itemView;

            mLikeBtn =(ImageButton)mView.findViewById(R.id.like_btn);

            mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
            mAuth =FirebaseAuth.getInstance();
            mDatabaseLike.keepSynced(true);
            post_username = (TextView) mView.findViewById(R.id.post_username);

            post_desc = (TextView) mView.findViewById(R.id.post_desc);
            post_title =(TextView)mView.findViewById(R.id.post_title);
            post_title.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    Log.v("mainActivity", "Some Text");
                }
            });
        }

        public void setLikeBtn(final String post_key){
            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){
                        mLikeBtn.setImageResource(R.drawable.thumb1);
                    }else{
                        mLikeBtn.setImageResource(R.drawable.thumb);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


        public void setTitle(String title){

            post_title.setText(title);
        }
        public void setDesc(String desc){

            post_desc.setText(desc);
        }

        public void setUsername(String username){

            post_username.setText(username);
        }

        public void setImage(final Context ctx, final String image){
            final ImageView post_image = (ImageView) mView.findViewById(R.id.post_image);
//            Picasso.with(ctx).load(image).into(post_image);

            Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(post_image, new Callback(){
                @Override
                public void onSuccess(){

                }
                @Override
                public void onError(){
                  Picasso.with(ctx).load(image).into(post_image);
                }
                    });

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            startActivity(new Intent(MainActivity.this, PostActivity.class));
        }
        if (item.getItemId() == R.id.action_logout) {
           logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout(){
        mAuth.signOut();
    }
}
