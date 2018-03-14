package com.gconnectdroid.blogapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class BlogSingleActivity extends AppCompatActivity {

    private String mPost_key = null;

    private ImageView mBlogSingleImage;
    private TextView mBlogSingleTitle;
    private TextView mBlogSingleDesc;
    private Button mSingleRemoveBtn;
    private FirebaseAuth mAuth;

    private DatabaseReference mDatabase;
    private String post_uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_single);

        mAuth= FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mPost_key = getIntent().getExtras().getString("blog_id");

        mBlogSingleImage =(ImageView) findViewById(R.id.singleBlogImage);
        mBlogSingleTitle = (TextView) findViewById(R.id.singleBlogTitle);
        mBlogSingleDesc = (TextView) findViewById(R.id.singleBlogDesc);
        mSingleRemoveBtn = (Button) findViewById(R.id.singleRemoveBtn);


//        Toast.makeText(BlogSingleActivity.this, post_key, Toast.LENGTH_LONG).show();

        mDatabase.child(mPost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String post_title = (String)dataSnapshot.child("title").getValue();
                String post_desc = (String)dataSnapshot.child("desc").getValue();
                String post_image = (String)dataSnapshot.child("image").getValue();
                String post_uid = (String)dataSnapshot.child("uid").getValue();

                mBlogSingleTitle.setText(post_title);
                mBlogSingleDesc.setText(post_desc);

                Picasso.with(BlogSingleActivity.this).load(post_image).into(mBlogSingleImage);

                if(mAuth.getCurrentUser().getUid().equals(post_uid)){
                    mSingleRemoveBtn.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        mSingleRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // remove post if current user is the owner of the post
                if (mAuth.getCurrentUser().getUid().equals(post_uid)){

                    mDatabase.child(mPost_key).removeValue();
                    Intent mainIntent= new Intent(BlogSingleActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                }

            }
        });

    }
}
