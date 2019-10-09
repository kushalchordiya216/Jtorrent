package jtorrent.Client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import jtorrent.Communication.P2PMessages.*;
import jtorrent.Encoding.Decode;

/**
 * Class to handle file leeching creates a serversocket that listens for
 * incoming pieces of files balances load between connected peers
 * 
 * @param merkleRoot    merkleRoot of the file to be transferred
 * @param metadataHash  hashmap of the metadata for the file
 * @param rootDirectory the root of the directory where users files are to be
 *                      stored
 */
public class FileLeecher implements Runnable {

    private String merkleRoot = null;
    private ServerSocket serverSocket = null;
    private ArrayList<Socket> peerSockets = null;
    private ArrayList<String> pendingPieces = new ArrayList<String>();
    private HashMap<String, String> metadataHash = null;
    private String rootDirectory = null;
    private Decode metaFileDecoder = null;
    private Integer numPiecesRecieved = null;

    public FileLeecher(String merkleRoot, HashMap<String, String> metadataHash, String rootDirectory,
            Decode metaFileDecoder) {
        this.merkleRoot = merkleRoot;
        this.metadataHash = metadataHash;
        this.rootDirectory = Paths.get(rootDirectory, this.merkleRoot).toString();
        this.metadataHash.remove("merkleRoot");
        this.metadataHash.remove("Name");
        this.metadataHash.remove("Tracker");
        this.pendingPieces = (ArrayList<String>) metadataHash.values();
        this.metaFileDecoder = metaFileDecoder;
        this.numPiecesRecieved = 0;
        try {
            this.serverSocket = new ServerSocket(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    synchronized public void updatePendingPieces(String pieceHash) {
        this.pendingPieces.remove(pieceHash);
    }

    public Integer getPortNo() {
        return this.serverSocket.getLocalPort();
    }

    public void BalanceLoad() {
        Integer numPeers = this.peerSockets.size();
        Integer index = 0;
        for (Socket socket : peerSockets) {
            DistributionMessage distributionMessage;
            distributionMessage = new DistributionMessage(numPeers, index, this.numPiecesRecieved);
            index++;
            try {
                ObjectOutputStream writeToSeeder = new ObjectOutputStream(socket.getOutputStream());
                writeToSeeder.writeObject(distributionMessage);
                writeToSeeder.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    synchronized public void writePiecetoDisk(String pieceId, String name, byte[] content) {
        // TODO: test if it works when this is method is not synchronized
        File newPiece = Paths.get(this.rootDirectory, name).toFile();
        if (!newPiece.exists()) {
            try {
                newPiece.createNewFile();
                OutputStream writeToFile = new FileOutputStream(newPiece);
                writeToFile.write(content);
                updatePendingPieces(pieceId);
                this.numPiecesRecieved++;
                writeToFile.close();
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
                    String pieceHash = piece.getPieceHash();
                    if (pendingPieces.contains(pieceHash)) {
                        String filename = this.metadataHash.get(pieceHash);
                        writePiecetoDisk(pieceHash, filename, piece.getContent());
                    }
                } else if (messageType.equals("DISCONNECT")) {
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

    public void leech() {
        while (this.pendingPieces.size() != 0) {
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

    @Override
    public void run() {
        leech();
        this.metaFileDecoder.Merge();
    }
}