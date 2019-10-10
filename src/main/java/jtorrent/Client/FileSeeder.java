package jtorrent.Client;

import java.io.*;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.HashMap;

import jtorrent.Communication.P2PMessages.*;
import jtorrent.Communication.Requests.SeedRequest;

public class FileSeeder implements Runnable {

    private String merkleRoot;
    private File rootDirectory;
    private Socket peerEndPoint;
    private ObjectOutputStream writeToPeer = null;
    private ObjectInputStream readFromPeer = null;
    File metaDataFile = null;
    HashMap<String, String> metaDataHash = null;
    private Integer assignedIndex = null, numPeers = null, numfileRecieved = null;
    private String[] totalPieces = null;

    public FileSeeder(SeedRequest seedRequest, String rootDirectory) {
        try {
            this.merkleRoot = seedRequest.getMerkleRoot();
            this.rootDirectory = Paths.get(rootDirectory, this.merkleRoot).toFile();
            this.totalPieces = this.rootDirectory.list();
            this.peerEndPoint = new Socket(seedRequest.getHostName(), seedRequest.getPort());
            this.writeToPeer = new ObjectOutputStream(this.peerEndPoint.getOutputStream());
            this.readFromPeer = new ObjectInputStream(this.peerEndPoint.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listener() {
        try {
            Message message = (Message) this.readFromPeer.readObject();
            if (message.getMessageType().equals("DISTRIBUTION")) {
                DistributionMessage distributionMessage = (DistributionMessage) message;
                this.assignedIndex = distributionMessage.getAssignedIndex();
                this.numPeers = distributionMessage.getNumPeers();
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

    public void sendPieces() {
        for (int i = this.numfileRecieved; i < this.totalPieces.length; i++) {
            if (i % this.numPeers == this.assignedIndex) {
                byte[] content = new byte[1024 * 1024];
                String fileName = totalPieces[i];
                if (fileName.contains(".metadata")) {
                    continue;
                }
                InputStream fileReader;
                try {
                    fileReader = new FileInputStream(
                            Paths.get(this.rootDirectory.toPath().toString(), fileName).toFile());
                    fileReader.read(content);
                    Piece piece = new Piece(fileName, content);
                    writeToPeer.writeObject(piece);
                    fileReader.close();
                } catch (IOException e) {
                    System.out.println("Connection with leecher broken, stopping transmission");
                    try {
                        Thread.currentThread().join();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }
            if (i == this.totalPieces.length - 1) {
                i = 0;
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
            this.assignedIndex = distributionMessage.getAssignedIndex();
            this.assignedIndex = distributionMessage.getNumPeers();
            this.numfileRecieved = distributionMessage.getNumFilesRecieved();
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