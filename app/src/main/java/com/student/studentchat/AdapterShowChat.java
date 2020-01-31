package com.student.studentchat;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public final class AdapterShowChat extends RecyclerView.Adapter<AdapterShowChat.HolderMessage>
{

    private List<Message> messages;

    AdapterShowChat (List<Message> messages)
    {
        this.messages = messages;
    }

    @NonNull
    @Override
    public HolderMessage onCreateViewHolder (@NonNull ViewGroup parent , int viewType)
    {
        View view = LayoutInflater.from (parent.getContext ()).inflate (R.layout.chat , parent , false);
        return new HolderMessage (view);
    }

    @Override
    public void onBindViewHolder (@NonNull HolderMessage holder , int position)
    {
        System.out.println ("Gergre");
        Message message = messages.get (position);
        holder.msg.setText (message.message);
        if (!message.me) holder.ll.setBackgroundColor (Color.CYAN);
    }

    @Override
    public int getItemCount ()
    {
        return messages.size ();
    }

    class HolderMessage extends RecyclerView.ViewHolder
    {

        final TextView msg;

        final LinearLayout ll;

        HolderMessage (@NonNull View itemView)
        {
            super (itemView);
            msg = itemView.findViewById (R.id.txt_msg);
            ll = itemView.findViewById (R.id.ll);
        }
    }

    void add (Message message)
    {
        messages.add (message);
        notifyItemInserted (messages.size () - 1);
    }
}
