package jtorrent.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

import jtorrent.Communication.Requests.*;
import jtorrent.Encoding.*;

public class Peer {

    private Socket trackerEndpoint = null;
    private ObjectOutputStream writeToTracker = null;
    private ObjectInputStream readFromTracker = null;
    private String trackerIP = null;
    private InetAddress hostname = null;
    private UserProfile userProfile = null;
    HashMap<Integer, String[]> changedFiles = new HashMap<Integer, String[]>();

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

    /**
     * 
     * @param type     "LOGIN" OR "REGISTER"
     * @param username username of the peer
     * @param password password of the user
     * @throws IOException
     */
    public void Connect() throws IOException {
        String type = null;
        Scanner sc = new Scanner(System.in);
        boolean isLoggedIn = false;
        while (!isLoggedIn) {
            System.out.println("1.Login\n2.Register");
            String choice = sc.nextLine();
            this.userProfile.getCredentials();
            switch (choice) {
            case "1":
                type = "LOGIN";
                isLoggedIn = true;
                break;
            case "2":
                type = "REGISTER";
                isLoggedIn = true;
                break;
            default:
                System.out.println("ENTER VALID CHOICE!");
                break;
            }
        }
        ConnectRequest connectRequest = new ConnectRequest(8080, type, this.userProfile.getUsername(),
                this.userProfile.getPassword());
        System.out.println("Logging in ....");
        writeToTracker.writeObject(connectRequest);
    }

    public void checkForChanges() {
        // TODO: check to see if files have been added deleted and update changedFiles
        // hashmap
        // 1 for addedfiles, -1 for removedfiles
    }

    public void Update() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest(this.userProfile.getUsername(), null, changedFiles.get(1),
                changedFiles.get(-1));
        this.writeToTracker.writeObject(updateRequest);
    }

    public void LeechFile(String metaFileName) {
        try {
            Decode metaFileDecoder = new Decode(metaFileName);
            String merkleRoot = metaFileDecoder.getMerkleRoot();
            FileLeecher fileTransfer = new FileLeecher(merkleRoot); // start up a serversocket to accept peer connection
            LeechRequest leechRequest = new LeechRequest(fileTransfer.getPortNo(), merkleRoot); // ask for files on
                                                                                                // given port
            this.writeToTracker.writeObject(leechRequest);
            // TODO: return info from tracker about how many peers have the file
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Peer peer = new Peer();
        try {
            peer.Connect();
            peer.Update();
        } catch (IOException e) {
            System.out.println("Error establishing connection with tracker");
        }
    }
}
