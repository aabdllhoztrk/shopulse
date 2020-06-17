package com.example.shopulse.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.shopulse.Model.Comments;
import com.example.shopulse.Prevalent.Prevalent;
import com.example.shopulse.R;
import com.example.shopulse.ViewHolder.CommentView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

public class CommentActivity extends AppCompatActivity {

    @NonNull
    @Override
    public LayoutInflater getLayoutInflater() {
        return super.getLayoutInflater();
    }

    private ImageButton PostCommentsButton;
    private EditText CommentInputText;
    private RecyclerView CommentsList;
    private String post_key,current_user_id;

    private DatabaseReference UsersRef,ProductsRef;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        post_key =getIntent().getExtras().get("PostKey").toString();

        mAuth=FirebaseAuth.getInstance();
        //  current_user_id=mAuth.getCurrentUser().getUid();

        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products").child(post_key).child("Comments");

        CommentsList =findViewById(R.id.comments_list);
        CommentsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        CommentsList.setLayoutManager(linearLayoutManager);


        CommentInputText=findViewById(R.id.comment_input);
        PostCommentsButton=findViewById(R.id.post_comment_btn);

        PostCommentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)

            {
                UsersRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if(dataSnapshot.exists())
                        {
                            String userName = Prevalent.currentOnlineUser.getName();

                            ValidateComment(userName);

                            CommentInputText.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

    }

    @Override
    protected void onStart()
    {

        super.onStart();


        FirebaseRecyclerOptions<Comments> options =
                new FirebaseRecyclerOptions.Builder<Comments>()
                        .setQuery(ProductsRef,Comments.class)
                        .build();

        FirebaseRecyclerAdapter<Comments, CommentView>adapter
                =new FirebaseRecyclerAdapter<Comments, CommentView>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CommentView commentview, int i, @NonNull Comments comments)
            {
                commentview.myUserName.setText(comments.getUsername());
                commentview.myComment.setText(comments.getComment());
                commentview.myDate.setText(comments.getDate());
                commentview.myTime.setText(comments.getTime());

            }

            @NonNull
            @Override
            public CommentView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_comments_layout, parent, false);
                CommentView holder = new CommentView(view);
                return holder;
            }
        };
        CommentsList.setAdapter(adapter);
        adapter.startListening();

    }

    private void ValidateComment(String userName) {

        String commentText =CommentInputText.getText().toString();
        if(TextUtils.isEmpty(commentText)){

            Toast.makeText(this, "Lutfen Yorum Yazin", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar calForDate=Calendar.getInstance();
            SimpleDateFormat currentDate= new SimpleDateFormat("dd-MMMM-yyyy");
            final String saveCurrentDate=currentDate.format(calForDate.getTime());

            Calendar calForTime=Calendar.getInstance();
            SimpleDateFormat currentTime= new SimpleDateFormat("HH:mm:ss ");
            final String saveCurrentTime=currentTime.format(calForTime.getTime());
            currentTime.setTimeZone(TimeZone.getTimeZone("Turkey"));


            final String RandomKey = userName+saveCurrentDate+saveCurrentTime;

            HashMap commentsMap = new HashMap();

            commentsMap.put("uid",userName);
            commentsMap.put("comment",commentText);
            commentsMap.put("date",saveCurrentDate);
            commentsMap.put("time",saveCurrentTime);
            commentsMap.put("username",userName);


            ProductsRef.child(RandomKey).updateChildren(commentsMap)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                            if(task.isSuccessful())
                            {
                                Toast.makeText(CommentActivity.this, "Basarili Bir Sekilde Yorumladiniz", Toast.LENGTH_SHORT).show();

                            }
                            else
                            {
                                Toast.makeText(CommentActivity.this, "Hata Olustu Tekrar Deneyiziz", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });





        }
    }
}
