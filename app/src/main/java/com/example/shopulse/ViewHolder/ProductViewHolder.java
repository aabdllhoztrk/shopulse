package com.example.shopulse.ViewHolder;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopulse.Interface.ItemClickListener;
import com.example.shopulse.Prevalent.Prevalent;
import com.example.shopulse.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.view.View;
import android.widget.Toast;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{

    public TextView txtProductName, txtProductDescription, txtProductPrice;
    public ImageView imageView;
    public ItemClickListener listener;
    public ImageButton mLikebtn;
    DatabaseReference mDatabaseLike;
    FirebaseAuth mAunth;
    public ImageView CommentPostButton;

    public ProductViewHolder(@NonNull android.view.View itemView)
    {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.product_image);
        txtProductName = (TextView) itemView.findViewById(R.id.product_name);
        txtProductDescription = (TextView) itemView.findViewById(R.id.product_description);
        txtProductPrice = (TextView) itemView.findViewById(R.id.product_price);
        mLikebtn=itemView.findViewById(R.id.like_btn);
        CommentPostButton=itemView.findViewById(R.id.comment_btn);



        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Products");
        mAunth=FirebaseAuth.getInstance();

    }

    public void setItemClickListener(ItemClickListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void onClick(View view)
    {
        listener.onClick(view, getAdapterPosition(), false);
    }

    public void setLikeBtn(final String post_key) {

        mDatabaseLike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(post_key).hasChild(Prevalent.currentOnlineUser.getName())){

                    mLikebtn.setImageResource(R.drawable.ic_favred);

                }else{
                    mLikebtn.setImageResource(R.drawable.ic_fav);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
