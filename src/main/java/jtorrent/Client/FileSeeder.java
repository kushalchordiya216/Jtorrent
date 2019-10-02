package jtorrent.Client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import jtorrent.Communication.P2PMessages.*;
import jtorrent.Communication.Requests.SeedRequest;

public class FileSeeder implements Runnable {

    private String merkleRoot, directoryRoot;
    private ArrayList<String> assignedPieces;
    private Socket peerEndPoint;
    private ObjectOutputStream writeToPeer = null;
    private ObjectInputStream readFromPeer = null;
    File metaDataFile = null;
    HashMap<String, String> metaDataHash = null;
    Boolean arrayAccessFlag = false;

    public FileSeeder(SeedRequest seedRequest, String directoryRoot) {
        try {
            this.merkleRoot = seedRequest.getMerkleRoot();
            this.directoryRoot = directoryRoot + this.merkleRoot + "/";
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
            DistributionMessage distributionMessage = (DistributionMessage) this.readFromPeer.readObject();
            updateAssignedPieces(distributionMessage.getPieceIds());
            arrayAccessFlag = true;
        } catch (ClassNotFoundException | IOException e) {
            System.out.println("Connection with peer interrupted\nStopping transmission!");
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    public void readMetaData() {
        File mainFolder = new File(this.directoryRoot);
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
        String fileName = this.metaDataHash.get(assignedPieces.get(0));
        byte[] content = new byte[1024 * 1024]; // TODO: initialize all to null
        try {
            InputStream fileReader = new FileInputStream(new File(this.directoryRoot + "/data/" + fileName));
            fileReader.read(content);
            Piece piece = new Piece(assignedPieces.get(0), content);
            try {
                writeToPeer.writeObject(piece);
            } catch (IOException e) {
                System.out.println("Connection with leecher broken, stopping transmission");
                Thread.currentThread().interrupt();
                e.printStackTrace();
                fileReader.close();
                return;
            }
            if (!arrayAccessFlag) {
                assignedPieces.remove(0);
            } else {
                arrayAccessFlag = false;
            }
            fileReader.close();
        } catch (IOException e) {
            System.out.println("Connection with leecher broken, stopping transmission");
            Thread.currentThread().interrupt();
            e.printStackTrace();
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
            updateAssignedPieces(distributionMessage.getPieceIds());
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
    // TODO: If node is going offline send exit message
}