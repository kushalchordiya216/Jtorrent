package jtorrent.Tracker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import jtorrent.Requests.*;

public class Tracker {

    private HashMap<String, PeerThread> connectedPeers = null;
    private ServerSocket serverSocket;
    private DataBaseOps dataBaseOps = new DataBaseOps();

    public Tracker() {
        try {
            this.serverSocket = new ServerSocket(8080);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void acceptIncomingConnection() throws IOException, ClassNotFoundException, SQLException {
        Socket socket = this.serverSocket.accept();
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        ConnectRequest connRequest = (ConnectRequest) ois.readObject();
        connRequest.setHostName(socket.getInetAddress().toString());
        Integer result;
        if (connRequest.getConnectionType() == "LOGIN") {
            result = dataBaseOps.updateUser(connRequest.getUsername(), connRequest.getHostName(), true,
                    connRequest.getPassword());
        } else {
            result = dataBaseOps.addUser(connRequest.getUsername(), connRequest.getHostName(), true,
                    connRequest.getPassword());
        }
        // TODO: send appropriate response for failure / success

        if (result != -1) {
            PeerThread peerThread = new PeerThread(socket, this, connRequest.getUsername(), connRequest.getPassword());
            this.connectedPeers.put(connRequest.getUsername(), peerThread);
            Thread peer = new Thread(peerThread);
            peer.start();
        }
    }

    public void removeConnection(PeerThread peerThread) {
        try {
            dataBaseOps.updateUser(peerThread.getUsername(), "", false, peerThread.getUsername());
            connectedPeers.remove(peerThread.getUsername());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void requestSeed(ArrayList<String> peers, String leecherIp, int port, String merkleRoot) {
        for (String peer : peers) {
            connectedPeers.get(peer).sendRequest(new SeedRequest(leecherIp, port, merkleRoot));
        }
    }

    public void main(String[] args) {
        new Thread(() -> {
            while (true) {
                try {
                    acceptIncomingConnection();
                } catch (IOException | ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}

class Routing {

}