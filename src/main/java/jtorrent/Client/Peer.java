package jtorrent.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse.File;

import jtorrent.Communication.Requests.*;
import jtorrent.Encoding.*;

public class Peer {

    Scanner sc = new Scanner(System.in);
    private Socket trackerEndpoint = null;
    private ObjectOutputStream writeToTracker = null;
    private ObjectInputStream readFromTracker = null;
    private String trackerIP = null;
    private UserProfile userProfile = null;
    HashMap<Integer, String[]> changedFiles = new HashMap<Integer, String[]>();
    private ThreadPoolExecutor leechExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
    private ThreadPoolExecutor seedExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
    private ScheduledExecutorService scheduledExecutorService = (ScheduledExecutorService) Executors
            .newScheduledThreadPool(1);
    public String rootDirectory = null;

    public Peer() {
        try {
            this.trackerEndpoint = new Socket(trackerIP, 8080);
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
        boolean validChoice = false;
        while (!validChoice) {
            System.out.println("1.Login\n2.Register");
            String choice = sc.nextLine();
            this.userProfile.getCredentials();
            switch (choice) {
            case "1":
                type = "LOGIN";
                validChoice = true;
                break;
            case "2":
                type = "REGISTER";
                validChoice = true;
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
        // TODO: receive login confirmation from tracker
        // TODO: initialize dataroot directories according to username
    }

    public void Update() {
        try {
            FileIndexManager fileIndexManager = new FileIndexManager(this.userProfile.getUsername());
            fileIndexManager.CheckForChanges();
            UpdateRequest updateRequest = new UpdateRequest(this.userProfile.getUsername(), null,
                    fileIndexManager.getAddedFiles(), fileIndexManager.getRemovedFiles());
            this.writeToTracker.writeObject(updateRequest);
            fileIndexManager = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void requestFile() {
        System.out.println("Enter metadatafile location");
        String metaFileName = sc.nextLine();
        LeechFile(metaFileName);
        Decode decode = new Decode(metaFileName);
        decode.Merge();
    }

    public void LeechFile(String metaFileName) {
        try {
            Decode metaFileDecoder = new Decode(metaFileName);
            String merkleRoot = metaFileDecoder.getMerkleRoot();
            FileLeecher fileLeecher = new FileLeecher(merkleRoot, metaFileDecoder.getMetaDataHash(), rootDirectory); // start
            LeechRequest leechRequest = new LeechRequest(fileLeecher.getPortNo(), merkleRoot); // ask for files on
            this.writeToTracker.writeObject(leechRequest);
            fileLeecher.leech();
            // TODO: return info from tracker about how many peers have the file
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SeedFile() {
        try {
            SeedRequest seedRequest = (SeedRequest) this.readFromTracker.readObject();
            FileSeeder fileSeeder = new FileSeeder(seedRequest, this.rootDirectory);
            seedExecutor.submit(fileSeeder);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Peer peer = new Peer();
        try {
            peer.Connect();
            peer.scheduledExecutorService.scheduleAtFixedRate(() -> {
                peer.Update();
            }, 0, 5, TimeUnit.MINUTES);
            new Thread(() -> {
                peer.SeedFile();
            });

            while (true) {
                System.out.println("1.Request File\n2.Publish\n3.Exit");
                String choice = sc.nextLine();
                switch (choice) {
                case "1":
                    peer.leechExecutor.submit(() -> {
                        peer.requestFile();
                    });
                    break;
                case "2":
                    String fileName = sc.nextLine();
                    Encode encode = new Encode(fileName, peer.rootDirectory);
                    encode.Split();
                case "3":
                    System.out.println("Going offline, all processes are being stopped");
                    return;
                }
            }
        } catch (IOException e) {
            System.out.println("Error establishing connection with tracker");
        }
        sc.close();
    }
}
