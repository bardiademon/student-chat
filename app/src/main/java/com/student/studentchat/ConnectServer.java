package com.student.studentchat;

import java.io.IOException;
import java.net.Socket;

class ConnectServer
{


    private static final String HOST = "10.0.2.2";

    Socket connect (int port)
    {
        try
        {
            return new Socket (HOST , port);
        }
        catch (IOException e)
        {
            return null;
        }
    }

}
