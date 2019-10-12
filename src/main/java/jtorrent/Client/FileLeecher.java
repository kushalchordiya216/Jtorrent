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
public class FileLeecher {

    private String merkleRoot = null;
    private ServerSocket serverSocket = null;
    private ArrayList<Socket> peerSockets = new ArrayList<Socket>();
    private ArrayList<String> pendingPieces = new ArrayList<String>();
    private HashMap<String, String> metadataHash = null;
    private String rootDirectory = null;
    private Decode metaFileDecoder = null;
    private Integer numPiecesRecieved = null;
    private File rootDirectoryFolder = null;

    public FileLeecher(String merkleRoot, HashMap<String, String> metadataHash, String rootDirectory,
            Decode metaFileDecoder) {
        this.merkleRoot = merkleRoot;
        this.metadataHash = metadataHash;
        this.rootDirectory = Paths.get(rootDirectory, this.merkleRoot).toString();
        this.rootDirectoryFolder = new File(this.rootDirectory);
        if (!this.rootDirectoryFolder.exists()) {
            this.rootDirectoryFolder.mkdirs();
        }
        this.metadataHash.remove("merkleRoot");
        this.metadataHash.remove("Name");
        this.metadataHash.remove("Tracker");
        this.metadataHash.remove("fileSizeMB");
        this.pendingPieces = new ArrayList<String>(metadataHash.values());
        this.metaFileDecoder = metaFileDecoder;
        this.numPiecesRecieved = 0;
        try {
            this.serverSocket = new ServerSocket(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updatePendingPieces(String pieceHash) {
        this.pendingPieces.remove(pieceHash);
        if (pendingPieces.size() == 0) {
            disconnect();
        }
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writePiecetoDisk(String pieceHash, byte[] content) {
        File newPiece = Paths.get(this.rootDirectory, pieceHash).toFile();
        try {
            if (!newPiece.exists()) {
                newPiece.createNewFile();
            }
            OutputStream writeToFile = new FileOutputStream(newPiece);
            writeToFile.write(content);
            updatePendingPieces(pieceHash);
            this.numPiecesRecieved++;
            writeToFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Listener(Socket socket) {
        try {
            ObjectInputStream receiveFromPeer = new ObjectInputStream(socket.getInputStream());
            while (!socket.isClosed() && !pendingPieces.isEmpty()) {
                Message message = (Message) receiveFromPeer.readObject();
                String messageType = message.getMessageType();
                if (messageType.equals("PIECE")) {
                    // peer is sending over a piece
                    Piece piece = (Piece) message;
                    String pieceHash = piece.getPieceHash();
                    if (pendingPieces.contains(pieceHash)) {
                        writePiecetoDisk(pieceHash, piece.getContent());
                    }
                } else if (messageType.equals("DISCONNECT")) {
                    peerSockets.remove(socket);
                    BalanceLoad();
                    Thread.currentThread().interrupt();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("One peer has disconnected!\n");
            peerSockets.remove(socket);
            e.printStackTrace();
        }
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void disconnect() {
        for (Socket socket : peerSockets) {
            DisconnectMessage disconnectMessage = new DisconnectMessage();
            try {
                ObjectOutputStream writeToSeeder = new ObjectOutputStream(socket.getOutputStream());
                writeToSeeder.writeObject(disconnectMessage);
                writeToSeeder.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("All piece received\nReconstructing file ....");
        this.metaFileDecoder.Merge();
    }

    public void leech() {
        while (this.pendingPieces.size() != 0) {
            Socket socket;
            try {
                if (this.peerSockets.size() <= 10) {
                    socket = this.serverSocket.accept();
                    System.out.println(socket.getInetAddress().toString() + "\n" + socket.getLocalPort());
                    peerSockets.add(socket);
                    this.BalanceLoad();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Listener(socket);
                        }
                    }).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        disconnect();
    }

    public void run() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                leech();
            }
        }).start();
    }
}