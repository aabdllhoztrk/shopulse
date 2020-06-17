package com.example.shopulse.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopulse.R;

public class CommentView extends RecyclerView.ViewHolder {

    public TextView myUserName,myComment,myDate,myTime;

    public CommentView(@NonNull View itemView) {
        super(itemView);


        myUserName = itemView.findViewById(R.id.comment_username);
        myComment = itemView.findViewById(R.id.comment_text);
        myDate = itemView.findViewById(R.id.comment_date);
        myTime = itemView.findViewById(R.id.comment_time);
    }


}

