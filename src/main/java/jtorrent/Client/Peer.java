package jtorrent.Client;

import java.io.*;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import jtorrent.Communication.Requests.*;
import jtorrent.Encoding.*;

public class Peer {

    Scanner sc = new Scanner(System.in);
    private Socket trackerEndpoint = null;
    private ObjectOutputStream writeToTracker = null;
    private ObjectInputStream readFromTracker = null;
    private String trackerIP = new String("localhost");
    private UserProfile userProfile = new UserProfile();
    public String rootDirectory = null;
    HashMap<Integer, String[]> changedFiles = new HashMap<Integer, String[]>();

    private ThreadPoolExecutor leechExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
    private ThreadPoolExecutor seedExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
    private ScheduledExecutorService updateExecutor = (ScheduledExecutorService) Executors.newScheduledThreadPool(1);

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

    public void leechFile() {
        try {
            System.out.println("Enter metadatafile location");
            String metaFileName = sc.nextLine();
            Decode decode = new Decode(metaFileName, this.rootDirectory);
            String merkleRoot = decode.getMerkleRoot();

            FileLeecher fileLeecher = new FileLeecher(merkleRoot, decode.getMetaDataHash(), rootDirectory, decode); // start
            LeechRequest leechRequest = new LeechRequest(fileLeecher.getPortNo(), merkleRoot); // ask for files on
            this.writeToTracker.writeObject(leechRequest);
            this.leechExecutor.submit((fileLeecher));

            Integer numSeeders = (Integer) this.readFromTracker.readObject();
            System.out.println("There are " + numSeeders + " seeders currently available");
        } catch (IOException | ClassNotFoundException e) {
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
            peer.Connect(); // connect to tracker endpoint by providing username and password
            // TODO: if connect successful, intialize rootdirectory
            // as(~/home/.P2P/{username}
            peer.updateExecutor.scheduleAtFixedRate(() -> {
                try {
                    peer.Update();
                    // periodically send updates to tracker about files that have been added or
                    // deleted
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, 0, 5, TimeUnit.MINUTES);

            new Thread(() -> {
                peer.SeedFile();
            });

            while (true) {
                System.out.println("1.Request File\n2.Publish\n3.Exit");
                System.out.println(
                        "Note that you can request a total of 3 simultaneous leechs at a time, further than that will be queued!");
                String choice = sc.nextLine();
                switch (choice) {
                case "1":
                    peer.leechFile();
                    break;
                case "2":
                    String fileName = sc.nextLine();
                    Encode encode = new Encode(fileName, peer.rootDirectory, peer.trackerEndpoint);
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

    public String getRootDirectory() {
        return rootDirectory;
    }

    public void setRootDirectory(String rootDirectory) {
        this.rootDirectory = Paths.get(System.getProperty("user.home"), ".P2P", this.userProfile.getUsername())
                .toString();
    }
}
