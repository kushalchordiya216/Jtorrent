package jtorrent.Client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import jtorrent.Communication.P2PMessages.*;

/**
 * Class to handle file leeching creates a serversocket that listens for
 * incoming pieces of files balances load between connected peers
 * 
 * @param merkleRoot    merkleRoot of the file to be transferred
 * @param metadataHash  hashmap of the metadata for the file
 * @param rootDirectory the root of the directory where users files are to be
 *                      stored
 */
public class FileLeecher {

    private String merkleRoot = null;
    private ServerSocket serverSocket = null;
    private ArrayList<Socket> peerSockets = null;
    private ArrayList<String> pendingPieces = new ArrayList<String>();
    private HashMap<String, String> metadataHash = null;
    private String directoryDataRoot = null;

    public FileLeecher(String merkleRoot, HashMap<String, String> metadataHash, String directoryRoot) {
        this.merkleRoot = merkleRoot;
        this.metadataHash = metadataHash;
        this.directoryDataRoot = directoryRoot + this.merkleRoot + "/Data/";
        this.pendingPieces = (ArrayList<String>) metadataHash.values();
        this.pendingPieces.removeAll(new ArrayList<String>(Arrays.asList("merkleRoot", "Tracker", "name")));
        // metadatahash also includes info about merkleRoot, name and Tracker but that
        // is not needed
        try {
            this.serverSocket = new ServerSocket(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    synchronized public void updatePendingPieces(String pieceId) {
        this.pendingPieces.remove(pieceId);
        // TODO: only one thread changes pendingPieces list at a given time
    }

    public Integer getPortNo() {
        return this.serverSocket.getLocalPort();
    }

    public void leech() {
        while (true) {
            Socket socket;
            try {
                if (this.peerSockets.size() <= 10) {
                    socket = this.serverSocket.accept();
                    peerSockets.add(socket);
                    this.BalanceLoad();
                    new Thread(() -> {
                        Listener(socket);
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void BalanceLoad() {
        Integer numPeers = this.peerSockets.size();
        Integer numPendingPieces = this.pendingPieces.size();
        Integer loadPerPeer = numPendingPieces / numPeers;
        Integer index = 0;
        for (Socket socket : peerSockets) {
            DistributionMessage distributionMessage;
            try {
                distributionMessage = new DistributionMessage(
                        (String[]) pendingPieces.subList(index, index + loadPerPeer).toArray());
                index += numPeers;
            } catch (IndexOutOfBoundsException e) {
                distributionMessage = new DistributionMessage((String[]) pendingPieces.toArray());
            }
            ObjectOutputStream writeToSeeder;
            try {
                writeToSeeder = new ObjectOutputStream(socket.getOutputStream());
                writeToSeeder.writeObject(distributionMessage);
                writeToSeeder.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void Listener(Socket socket) {
        while (!socket.isClosed() && !pendingPieces.isEmpty()) {
            try {
                ObjectInputStream receiveFromPeer = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) receiveFromPeer.readObject();
                String messageType = message.getMessageType();
                if (messageType.equals("PIECE")) {
                    // peer is sending over a piece
                    Piece piece = (Piece) message;
                    String pieceId = piece.getPieceId();
                    if (pendingPieces.contains(pieceId)) {
                        String filename = this.metadataHash.get(pieceId);
                        writePiecetoDisk(pieceId, filename, piece.getContent());
                    }
                } else if (messageType.equals("DISCONNECT")) {
                    // Peer is disconnecting
                    peerSockets.remove(socket);
                    BalanceLoad();
                    Thread.currentThread().interrupt();
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("One peer has disconnected!\n");
                peerSockets.remove(socket);
                e.printStackTrace();
            }
        }
    }

    synchronized public void writePiecetoDisk(String pieceId, String name, byte[] content) {
        // TODO: test if it works when this is method is not synchronized
        // TODO: write content of piece to appropriate file name in {directoryRootData}
        File newPiece = new File(this.directoryDataRoot + name);
        try {
            OutputStream writeToFile = new FileOutputStream(newPiece);
            writeToFile.write(content);
            updatePendingPieces(pieceId);
            writeToFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}