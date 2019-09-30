package jtorrent.Client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Class to handle file leeching
 * 
 * @param merkleRoot merkleRoot of the file to be transferred
 */
public class FileLeecher {
    // TODO:accept incoming files
    // TODO: send requested files

    private String merkleRoot = null;
    private ServerSocket serverSocket = null;
    private ArrayList<Socket> peerSockets = null;
    private Integer numPieces = null;
    private ArrayList<String> pendingPieces = new ArrayList<String>();

    public FileLeecher(String merkleRoot) {
        this.merkleRoot = merkleRoot;
        try {
            this.serverSocket = new ServerSocket(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Integer getPortNo() {
        return this.serverSocket.getLocalPort();
    }

    public void establishConnections() {
        while (true) {
            Socket socket;
            try {
                socket = this.serverSocket.accept();
                peerSockets.add(socket);
                new Thread(() -> {
                    Listener(socket);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void BalanceLoad() {
        // TODO:send load distribution to each peer
    }

    public void Listener(Socket socket) {
        // TODO: listen for incoming messages from pieces
    }
}