package jtorrent.Client;

import java.io.IOException;
import java.net.ServerSocket;

public class FileTransfer {
    // TODO:accept incoming files
    // TODO: send requested files

    private String merkleRoot = null;
    private ServerSocket serverSocket = null;

    public FileTransfer(String merkleRoot) {
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

}