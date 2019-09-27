package jtorrent.Client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import jtorrent.Requests.*;
import jtorrent.Encoding.*;

public class Peer {

    Socket trackerEndpoint = null;
    ObjectOutputStream writeToTracker = null;
    ObjectInputStream readFromTracker = null;
    String trackerIP = null;
    InetAddress hostname = null;

    public Peer() {
        try {
            this.trackerEndpoint = new Socket(trackerIP, 8080);
            this.hostname = this.trackerEndpoint.getInetAddress();
            this.writeToTracker = new ObjectOutputStream(trackerEndpoint.getOutputStream());
            this.readFromTracker = new ObjectInputStream(trackerEndpoint.getInputStream());
        } catch (IOException e) {
            System.out.println(
                    "Couldnt establish connection with tracker!\nIf you have a metadata file, it may have info about the trackers address!");
        }
    }

    // TODO: accounce to tracker
    // TODO: establish connections
    // * spin up a server to receive files
    // * client to send files

    /**
     * 
     * @param type     "LOGIN" OR "REGISTER"
     * @param username username of the peer
     * @param password password of the user
     */
    public void Connect(String type, String username, String password) {
        ConnectRequest connectRequest = new ConnectRequest(8080, type, username, password);
        try {
            writeToTracker.writeObject(connectRequest);
            System.out.println("Logging in ....");
            // TODO: wait for acknowledgement response from tracker
        } catch (IOException e) {
            System.out.println("error logging in!\n");
            e.printStackTrace();
        }
    }

    public void RequestFile(String metaFileName) {
        // TODO: read metadata file and get the appropriate merkleRoot
        // TODO: Send leech request for the file
        try {
            Decode metaFileDecoder = new Decode(metaFileName);
            String merkleRoot = metaFileDecoder.getMerkleRoot();
            FileTransfer fileTransfer = new FileTransfer(merkleRoot);

            LeechRequest leechRequest = new LeechRequest(fileTransfer.getPortNo(), merkleRoot);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

    }

}