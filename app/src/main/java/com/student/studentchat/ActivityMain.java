package com.student.studentchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class ActivityMain extends AppCompatActivity
{

    private Client client;

    private EditText txtMessage;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
        setTools ();
    }

    private void setTools ()
    {
        RecyclerView chat = findViewById (R.id.chat);

        List<Message> messages = new ArrayList<> ();

        chat.setLayoutManager (new LinearLayoutManager (this , LinearLayoutManager.VERTICAL , false));

        AdapterShowChat adapterShowChat = new AdapterShowChat (messages);
        chat.setAdapter (adapterShowChat);


        SharedPreferences info_login = getSharedPreferences ("INFO_LOGIN" , MODE_PRIVATE);

        client = new Client (info_login.getString ("username" , "") , message ->
                ActivityMain.this.runOnUiThread (() -> adapterShowChat.add (message)) , this);


        txtMessage = findViewById (R.id.txt_message);
        Button btnSend = findViewById (R.id.btn_send);

        btnSend.setOnClickListener (v ->
        {
            String message = txtMessage.getText ().toString ();
            if (!message.isEmpty ())
            {
                client.message (message);
                adapterShowChat.add (new Message (message , true));
            }
        });
    }
}
