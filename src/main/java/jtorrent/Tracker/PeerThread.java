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
    private String username, password;
    private ObjectInputStream readFromPeer;
    private ObjectOutputStream writeToPeer;
    private UserTable userTable = new UserTable();
    private FilesTable filesTable = new FilesTable();

    private ExecutorService executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);

    PeerThread(Socket socket, Tracker tracker, String username, String password) {
        try {
            this.socket = socket;
            this.tracker = tracker;
            this.username = username;
            this.password = password;
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
        switch (connectRequest.getConnectionType()) {
        case "REGISTER":
            connectRequest.setActive(true);
            userTable.Create(connectRequest);
            break;
        case "LOGIN":
            connectRequest.setActive(true);
            userTable.Update(connectRequest);
            break;
        case "DISCONNECT":
            connectRequest.setActive(false);
            userTable.Update(connectRequest);
            this.removeNode();
            break;
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

    public void processRequest() {
        try {
            Request request = (Request) readFromPeer.readObject();
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
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * accepts requests coming from clientside peers, and processes them in a
     * threadpool of size 3
     */
    @Override
    public void run() {
        while (this.socket.isConnected()) {
            executor.submit(() -> {
                processRequest();
            });
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
}