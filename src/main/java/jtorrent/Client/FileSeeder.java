package jtorrent.Client;

import java.io.*;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import jtorrent.Communication.P2PMessages.*;
import jtorrent.Communication.Requests.SeedRequest;

public class FileSeeder implements Runnable {

    private String merkleRoot, rootDirectory;
    private ArrayList<String> assignedPieces;
    private Socket peerEndPoint;
    private ObjectOutputStream writeToPeer = null;
    private ObjectInputStream readFromPeer = null;
    File metaDataFile = null;
    HashMap<String, String> metaDataHash = null;

    public FileSeeder(SeedRequest seedRequest, String rootDirectory) {
        try {
            this.rootDirectory = Paths.get(rootDirectory, merkleRoot).toString();
            this.merkleRoot = seedRequest.getMerkleRoot();

            this.peerEndPoint = new Socket(seedRequest.getHostName(), seedRequest.getPort());
            this.writeToPeer = new ObjectOutputStream(this.peerEndPoint.getOutputStream());
            this.readFromPeer = new ObjectInputStream(this.peerEndPoint.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    synchronized public void updateAssignedPieces(String[] newAssignedPieces) {
        this.assignedPieces = (ArrayList<String>) Arrays.asList(newAssignedPieces);
    }

    public void listener() {
        try {
            Message message = (Message) this.readFromPeer.readObject();
            if (message.getMessageType().equals("DISTRIBUTION")) {
                DistributionMessage distributionMessage = (DistributionMessage) message;
                updateAssignedPieces(distributionMessage.getPieceHashes());
            } else {
                System.out.println("Completion message received from peer\nStopping transmission");
                Thread.currentThread().join();
            }
        } catch (ClassNotFoundException | IOException | InterruptedException e) {
            System.out.println("Connection with peer interrupted\nStopping transmission!");
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    public void readMetaData() {
        File mainFolder = new File(this.rootDirectory);
        File[] fileList = mainFolder.listFiles();
        for (File file : fileList) {
            if (file.isFile() && file.getName().contains("metadata")) {
                try {
                    ObjectInputStream metaFileReader = new ObjectInputStream(new FileInputStream(file));
                    this.metaDataHash = (HashMap<String, String>) metaFileReader.readObject();
                    metaFileReader.close();
                    return;
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendPieces() {

        byte[] content = new byte[1024 * 1024];
        try {
            String fileName = this.metaDataHash.get(assignedPieces.get(0));
            InputStream fileReader = new FileInputStream(new File(this.rootDirectory + "/data/" + fileName));
            fileReader.read(content);
            Piece piece = new Piece(assignedPieces.get(0), content);
            try {
                writeToPeer.writeObject(piece);
            } catch (IOException e) {
                System.out.println("Connection with leecher broken, stopping transmission");
                try {
                    Thread.currentThread().join();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
                fileReader.close();
                return;
            }
            assignedPieces.remove(0);
            fileReader.close();
        } catch (IOException e) {
            System.out.println("Connection with leecher broken, stopping transmission");
            try {
                Thread.currentThread().join();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            System.out.println("no files left in assignedPieces\nIdle for now");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e1) {
                System.out.println("Seeder thread execution was stopped!");
            }
        }
    }

    public void disconnect() {
        DisconnectMessage disconnectMessage = new DisconnectMessage();
        try {
            this.writeToPeer.writeObject(disconnectMessage);
        } catch (IOException e) {
            System.out.println("Connection with leecher broken, stopping transmission");
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            DistributionMessage distributionMessage = (DistributionMessage) this.readFromPeer.readObject();
            updateAssignedPieces(distributionMessage.getPieceHashes());
            readMetaData();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            listener();
        });
        new Thread(() -> {
            sendPieces();
        });
    }

    protected void finalize() {
        this.disconnect();
    }
}