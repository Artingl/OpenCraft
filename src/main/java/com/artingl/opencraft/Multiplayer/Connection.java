package com.artingl.opencraft.Multiplayer;

import java.net.Socket;

public class Connection {

    private final Socket connection;

    public Connection(Socket socket) {
        this.connection = socket;
    }

    public Socket getConnection() {
        return connection;
    }
}
