package jtorrent.Tracker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import jtorrent.Communication.Requests.*;
import jtorrent.Database.*;

public class PeerThread implements Runnable {
    private Socket socket;
    private Tracker tracker;
    private ObjectInputStream readFromPeer;
    private ObjectOutputStream writeToPeer;
    private String username, password;
    private UserTable userTable = new UserTable();
    private FilesTable filesTable = new FilesTable();

    private ExecutorService executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);

    PeerThread(Socket socket, Tracker tracker) {
        try {
            this.socket = socket;
            this.tracker = tracker;
            this.readFromPeer = new ObjectInputStream(socket.getInputStream());
            this.writeToPeer = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendRequest(SeedRequest seedRequest) {
        try {
            this.writeToPeer.writeObject(seedRequest);
        } catch (IOException e) {
            System.out.println("could not send request to peer!");
            e.printStackTrace();
        }
    }

    public void processUpdateRequest(UpdateRequest updateRequest) {
        updateRequest.setHostName(this.socket.getInetAddress().toString());
        filesTable.Create(updateRequest);
        filesTable.Delete(updateRequest);
    }

    /**
     * 
     * @param leechRequest incoming leechRequest which contains all the required
     *                     data to connnect with the socket
     * 
     */
    public void processLeechRequest(LeechRequest leechRequest) {
        leechRequest.setHostName(this.socket.getInetAddress().toString());
        ArrayList<String> peerIps = new ArrayList<String>();
        ResultSet rs = filesTable.Retrieve(leechRequest);
        try {
            while (rs.next()) {
                peerIps.add(rs.getString("currentIP"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.tracker.requestSeed(peerIps, leechRequest.getHostName(), leechRequest.getPort(),
                leechRequest.getMerkleRoot());
    }

    public void processConnectRequest(ConnectRequest connectRequest) {
        connectRequest.setHostName(this.socket.getInetAddress().toString());
        Integer result = null;
        switch (connectRequest.getConnectionType()) {
        case "REGISTER":
            connectRequest.setActive(true);
            result = userTable.Create(connectRequest);
            break;
        case "LOGIN":
            connectRequest.setActive(true);
            result = userTable.Update(connectRequest);
            break;
        case "DISCONNECT":
            connectRequest.setActive(false);
            result = userTable.Update(connectRequest);
            this.removeNode();
            break;
        }
        if (result == 0) {
            System.out.println("Invalid credentials!\n");
        } else {
            System.out.println("login successful");
            this.username = connectRequest.getUsername();
            this.password = connectRequest.getPassword();
            this.tracker.addToPeerIndex(this.username, this);
        }
    }
    // TODO: send appropriate response after connecting

    public void removeNode() {
        this.tracker.removeConnection(this);
        try {
            this.socket.close();
        } catch (IOException e) {
            System.out.println("Socket Connection with peer" + this.getUsername() + "closed");
        }
        Thread.currentThread().interrupt();
    }

    public void processRequest(Request request) {
        switch (request.getRequestType()) {
        case "LEECH":
            processLeechRequest((LeechRequest) request);
            break;
        case "CONNECT":
            processConnectRequest((ConnectRequest) request);
            break;
        case "UPDATE":
            processUpdateRequest((UpdateRequest) request);
            break;
        }
    }

    /**
     * accepts requests coming from clientside peers, and processes them in a
     * threadpool of size 3
     */
    @Override
    public void run() {
        while (this.socket.isConnected()) {
            try {
                Request request = (Request) readFromPeer.readObject();
                executor.submit(() -> {
                    processRequest(request);
                });
            } catch (ClassNotFoundException | IOException e1) {
                e1.printStackTrace();
            }
            // try {
            // Thread.sleep(5000);
            // } catch (InterruptedException e) {
            // System.out.println("lol");
            // e.printStackTrace();
            // }
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasssword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}