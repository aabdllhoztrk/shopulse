package com.example.shopulse.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.shopulse.Model.Products;
import com.example.shopulse.Prevalent.Prevalent;
import com.example.shopulse.R;
import com.example.shopulse.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SearchProductsActivity extends AppCompatActivity
{

    private Button SearchBtn;
    private EditText inputText;
    private RecyclerView searchList;
    private String SearchInput;
    private boolean mProcessLike = false;
    private DatabaseReference mDatabaseLike;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_products);
        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Products");


        inputText =  findViewById(R.id.search_urun_ismi);
        SearchBtn =  findViewById(R.id.search_btn);
        searchList = findViewById(R.id.search_list);
        searchList.setLayoutManager(new LinearLayoutManager(SearchProductsActivity.this));

        SearchBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SearchInput = inputText.getText().toString();

                onStart();
            }
        });

    }


    @Override
    protected void onStart()
    {
        super.onStart();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(reference.orderByChild("pname").startAt(SearchInput), Products.class)
                .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options)
                {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull final Products products)
                    {

                        final String post_key=getRef(i).getKey();
                        productViewHolder.txtProductName.setText(products.getPname());
                        productViewHolder.txtProductDescription.setText(products.getDescription());
                        productViewHolder.txtProductPrice.setText("Price =  " + products.getPrice() + "₺");
                        Picasso.get().load(products.getImage()).into(productViewHolder.imageView);

                        productViewHolder.CommentPostButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent commentIntent=new Intent(SearchProductsActivity.this, CommentActivity.class);
                                commentIntent.putExtra("PostKey",post_key);
                                startActivity(commentIntent);
                            }
                        });

                        productViewHolder.setLikeBtn(post_key);
                        productViewHolder.mLikebtn.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {

                                mProcessLike=true;

                                mDatabaseLike.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                        if (mProcessLike) {

                                            if(dataSnapshot.child(post_key).hasChild((Prevalent.currentOnlineUser.getName())))
                                            {
                                                mDatabaseLike.child(post_key).child(Prevalent.currentOnlineUser.getName()).removeValue();


                                                mProcessLike=false;

                                            }                                                else{

                                                mDatabaseLike.child(post_key).child(Prevalent.currentOnlineUser.getName()).setValue("Like");

                                                mProcessLike=false;
                                            }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }


                        });




                        productViewHolder.itemView.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Intent intent = new Intent(SearchProductsActivity.this, ProductDetailsActivity.class);
                                intent.putExtra("pid", products.getPid());
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };


        searchList.setAdapter(adapter);
        adapter.startListening();
    }
}
