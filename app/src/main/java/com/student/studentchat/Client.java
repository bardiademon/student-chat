package com.student.studentchat;

import android.app.Activity;

import java.io.*;
import java.net.Socket;


public class Client
{

    private BufferedReader reader;
    private PrintWriter writer;
    public static final String OK = "000000OK000000";

    public Client (String username , Message message , Activity activity)
    {
        new Thread (() ->
        {
            try
            {
                Socket socket = new Socket ("10.0.2.2" , Integer.parseInt (activity.getString (R.string.port_chat)));
                InputStream inputStream = socket.getInputStream ();
                OutputStream outputStream = socket.getOutputStream ();

                reader = new BufferedReader (new InputStreamReader (inputStream));

                writer = new PrintWriter (outputStream);

                writer.println (username);
                writer.flush ();
                System.out.println (reader.readLine ());

                new Thread (() ->
                {
                    try
                    {
                        String line;
                        while (true)
                        {
                            line = reader.readLine ();
                            if (line != null)
                            {
                                if (!line.equals (OK))
                                {
                                    System.out.println (line);
                                    message.NewMessage (new com.student.studentchat.Message (line , false));
                                }

                                writer.println (OK);
                                writer.flush ();
                            }
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace ();
                    }
                }).start ();

            }
            catch (IOException e)
            {
                e.printStackTrace ();
            }
        }).start ();
    }

    public void message (String message)
    {
        new Thread (() ->
        {
            try
            {
                if (message != null)
                {
                    writer.println (message);
                    writer.flush ();
                    reader.readLine ();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace ();
            }
        }).start ();
    }

    public interface Message
    {
        void NewMessage (com.student.studentchat.Message message);
    }
}
