package jtorrent.Tracker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import jtorrent.Requests.*;

public class PeerThread implements Runnable {
    private Socket socket;
    private Tracker tracker;
    private String username;
    private String password;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private DataBaseOps dataBaseOps = new DataBaseOps();
    private ExecutorService executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);

    PeerThread(Socket socket, Tracker tracker, String username, String password)
            throws IOException, SQLException, ClassNotFoundException {
        this.socket = socket;
        this.tracker = tracker;
        this.username = username;
        this.password = password;
        this.ois = new ObjectInputStream(socket.getInputStream());
        this.oos = new ObjectOutputStream(socket.getOutputStream());
    }

    public void sendRequest(SeedRequest seedRequest) {
        try {
            this.oos.writeObject(seedRequest);
        } catch (IOException e) {
            System.out.println("could not send request to peer!");
            e.printStackTrace();
        }
    }

    public void updateIndex() throws IOException, ClassNotFoundException {
        String[] addedFiles = (String[]) ois.readObject();
        String[] removedFiles = (String[]) ois.readObject();
        dataBaseOps.updateFileOwners(this, addedFiles, removedFiles);
    }

    /**
     * 
     * @param leechRequest incoming leechRequest which contains all the required
     *                     data to connnect with the socket
     * 
     */
    public void leechRequest(LeechRequest leechRequest) {
        String merkleRoot = leechRequest.getMerkleRoot();
        leechRequest.setHostName(this.socket.getInetAddress().toString());
        try {
            ArrayList<String> peers = dataBaseOps.queryFileOwners(merkleRoot);
            this.tracker.requestSeed(peers, leechRequest.getHostName(), leechRequest.getPort(),
                    leechRequest.getMerkleRoot());
        } catch (SQLException e) {
            System.out.println("Error while Querying DB!");
            e.printStackTrace();
        }
    }

    public void removeNode() {
        this.tracker.removeConnection(this);
        try {
            this.socket.close();
        } catch (IOException e) {
            System.out.println("Socket Connection with peer" + this.getUsername() + "closed");
        }
    }

    public void processRequest() {
        try {
            Request request = (Request) ois.readObject();
            switch (request.getRequestType()) {
            case "LEECH":
                leechRequest((LeechRequest) request);
                break;
            case "":

            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    /**
     * accepts requests coming from clientside peers, and processes them in a
     * threadpool of size 3
     */
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