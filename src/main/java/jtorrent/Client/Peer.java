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

import jtorrent.Communication.Requests.*;
import jtorrent.Encoding.*;

public class Peer {

    Scanner sc = new Scanner(System.in);
    private Socket trackerEndpoint = null;
    private ObjectOutputStream writeToTracker = null;
    private ObjectInputStream readFromTracker = null;
    private String trackerIP = new String("localhost");
    private UserProfile userProfile = new UserProfile();
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
     * @throws ClassNotFoundException
     */
    public void Connect() throws IOException {
        String type = null;
        Integer loginStatus = 0;
        while (loginStatus.equals(0)) {
            System.out.println("1.Login\n2.Register");
            String choice = sc.nextLine();
            this.userProfile.getCredentials();
            switch (choice) {
            case "1":
                type = "LOGIN";
                break;
            case "2":
                type = "REGISTER";
                break;
            default:
                type = "REGISTER";
                break;
            }
            ConnectRequest connectRequest = new ConnectRequest(8080, type, this.userProfile.getUsername(),
                    this.userProfile.getPassword());
            System.out.println("Logging in ....");
            writeToTracker.writeObject(connectRequest);
            try {
                loginStatus = (Integer) this.readFromTracker.readObject();
                if (loginStatus.equals(0)) {
                    System.out.println(
                            "Given credetials are invalid!\nIf you're registering for the first time this means the username is taken");
                }
            } catch (ClassNotFoundException e) {
                System.out.println("unexpected datatype returned from tracker");
            }
        }
    }

    public void Update() throws IOException {
        FileIndexManager fileIndexManager = new FileIndexManager(this.userProfile.getUsername());
        fileIndexManager.CheckForChanges();
        UpdateRequest updateRequest = new UpdateRequest(this.userProfile.getUsername(), null,
                fileIndexManager.getAddedFiles(), fileIndexManager.getRemovedFiles());
        this.writeToTracker.writeObject(updateRequest);
        fileIndexManager = null; // ? it'll get garbage connected nevertheless
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
                try {
                    peer.Update();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                    break;
                case "3":
                    System.out.println("Going offline, all processes are being stopped");
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            System.out.println("Error establishing connection with tracker");
        }
        sc.close();
    }
}
