package com.example.android.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Me on 14.12.2017 г..
 */

public class MessageViewHolder extends RecyclerView.ViewHolder{


    View mView;

    public MessageViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setContent(String content){
        TextView msgContent = (TextView) mView.findViewById(R.id.message);
        msgContent.setText(content);

    }

    public void setTime(Long time){
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String msgTime = formatter.format(time);
        TextView msgDate = (TextView) mView.findViewById(R.id.time);
        msgDate.setText(msgTime);
    }

    public void setUsername(String username) {
        TextView msgUser = (TextView) mView.findViewById(R.id.user);
        msgUser.setText(username);
    }

    public void setImage(Context context, String path){
        if(path != null) {
            ImageView image = (ImageView) mView.findViewById(R.id.image);
            Picasso.with(context).load(path).into(image);
        }
    }
}
