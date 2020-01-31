package com.student.studentchat;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ActivityLogin extends AppCompatActivity
{

    private EditText txtUsername, txtPassword;

    private Button btnLogin;
    private TextView txtRegister, resultConnectServer;

    private Socket socket;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_login);

        setTools ();
    }

    private void setTools ()
    {
        txtUsername = findViewById (R.id.txt_username);
        txtPassword = findViewById (R.id.txt_password);
        btnLogin = findViewById (R.id.btn_login);
        txtRegister = findViewById (R.id.txt_register);
        resultConnectServer = findViewById (R.id.result_connect_server);
        connectServer ();
    }


    @SuppressLint ("SetTextI18n")
    private void connectServer ()
    {
        ConnectServer connectServer = new ConnectServer ();
        new Thread (() ->
        {
            while (true)
            {
                socket = connectServer.connect (Integer.parseInt (getString (R.string.port_login_register)));
                if (socket != null && socket.isConnected ())
                {
                    ActivityLogin.this.runOnUiThread (() -> resultConnectServer.setText ("Connected"));
                    afterConnect ();
                    return;
                }
                ActivityLogin.this.runOnUiThread (() -> resultConnectServer.setText ("Error connect, Connecting..."));
            }

        }).start ();

    }

    private void afterConnect ()
    {
        btnLogin.setOnClickListener (v -> onClickBtnLogin ("validation_login"));
        txtRegister.setOnClickListener (v -> onClickBtnLogin ("register"));
    }

    private void onClickBtnLogin (String type)
    {
        String username = txtUsername.getText ().toString ();
        String password = txtPassword.getText ().toString ();

        if (username.isEmpty () || password.isEmpty ())
            showToast ("Value is empty");
        else
        {
            if (socket == null || socket.isClosed () || !socket.isConnected ())
            {
                showToast ("Not connection");
                connectServer ();
            }
            else
            {
                try
                {
                    JSONObject jsonRequest = new JSONObject ();
                    jsonRequest.put (KeyJson.username.name () , username);
                    jsonRequest.put (KeyJson.password.name () , password);
                    jsonRequest.put (KeyJson.type.name () , type);

                    new Thread (() ->
                    {
                        try
                        {
                            OutputStream outputStream = socket.getOutputStream ();

                            PrintWriter writer = new PrintWriter (outputStream);

                            writer.println (jsonRequest.toString ());
                            writer.flush ();

                            InputStream inputStream = socket.getInputStream ();
                            BufferedReader reader = new BufferedReader (new InputStreamReader (inputStream));

                            String line;
                            StringBuilder answer = new StringBuilder ();
                            while ((line = reader.readLine ()) != null) answer.append (line);

                            writer.close ();
                            socket.close ();
                            inputStream.close ();
                            outputStream.close ();
                            reader.close ();

                            answerServer (answer.toString () , username , password , type);

                        }
                        catch (IOException e)
                        {
                            showToast ("Error please try again");
                            if (socket == null || socket.isClosed () || !socket.isConnected ())
                                ActivityLogin.this.connectServer ();
                        }
                    }).start ();
                }
                catch (JSONException e)
                {
                    showToast ("Error please try again");
                    if (socket == null || socket.isClosed () || !socket.isConnected ())
                        ActivityLogin.this.connectServer ();
                }
            }
        }

    }

    private void answerServer (String answer , String username , String password , String type)
    {
        try
        {
            JSONObject jsonAnswer = new JSONObject (answer);
            showToast (jsonAnswer.getString (KeyJson.message.name ()));
            if (jsonAnswer.has (KeyJson.ok.name ()) && jsonAnswer.getBoolean (KeyJson.ok.name ()))
            {
                if (type.equals ("validation_login"))
                {
                    ActivityLogin.this.runOnUiThread (() -> startActivity (new Intent (this , ActivityMain.class)));
                    SharedPreferences info_login = getSharedPreferences ("INFO_LOGIN" , MODE_PRIVATE);
                    @SuppressLint ("CommitPrefEdits") SharedPreferences.Editor edit = info_login.edit ();
                    edit.putString ("username" , username);
                    edit.putString ("password" , password);
                    edit.apply ();
                    finish ();
                    return;
                }
            }
            connectServer ();
        }
        catch (JSONException e)
        {
            showToast ("Error please try again");
        }
    }

    private void showToast (String message)
    {
        runOnUiThread (() -> Toast.makeText (this , message , Toast.LENGTH_LONG).show ());
    }


    private enum KeyJson
    {
        type,

        username, password,

        ok, message
    }
}
