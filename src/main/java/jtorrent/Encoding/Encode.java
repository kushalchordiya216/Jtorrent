package jtorrent.Encoding;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.common.hash.Hashing;

public class Encode {
    private File file, metadata, newFile, tempDirectory;
    private FileInputStream reader;
    private FileOutputStream writer;
    private HashMap<String, String> MetaDataHash = new HashMap<String, String>();
    private String tempDirectoryPath = null, rootDirectoryPath; // tempoarary directory name where encoded pieces will
                                                                // be stored
    private Socket trackerEndpoint;

    public Encode(String filename, String rootDirectory, Socket socket) {
        file = new File(filename);
        this.trackerEndpoint = socket;
        this.newFile = new File("newFile.txt");
        this.rootDirectoryPath = rootDirectory;
        this.tempDirectoryPath = Paths.get(rootDirectory, filename).toString();
        this.tempDirectory = new File(this.tempDirectoryPath);
        if (!this.tempDirectory.exists()) {
            this.tempDirectory.mkdirs();
        }
        metadata = new File(Paths.get(this.tempDirectoryPath, filename + ".metadata").toString()); // metadatafile
        if (!metadata.exists()) {
            try {
                metadata.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } // created
        try {
            reader = new FileInputStream(file);
            writer = new FileOutputStream(newFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void Split() {
        try {
            long totalFileLength = file.length();
            long index = 0;
            long numPieces = totalFileLength / (1024 * 1024);
            int remainder = (int) totalFileLength % (1024 * 1024);
            do {
                byte[] piece = new byte[10];
                reader.read(piece);
                writer.write(piece);
                String hash = Hashing.sha256().hashBytes(piece).toString();
                this.writePiece(hash, piece);
                MetaDataHash.put(Long.toString(index), hash);
                index++;
                numPieces--;
            } while (numPieces > 0);
            byte[] lastPiece = new byte[remainder];
            reader.read(lastPiece);
            reader.close();
            String hash = Hashing.sha256().hashBytes(lastPiece).toString();
            this.writePiece(hash, lastPiece);
            MetaDataHash.put(Long.toString(index), hash);
            index++;

            String merkleRoot = createMerkleRoot(MetaDataHash);
            writeMetaData(merkleRoot);

            this.tempDirectory.renameTo(new File(Paths.get(this.rootDirectoryPath, merkleRoot).toString()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getTrackerInfo() {
        return this.trackerEndpoint.getInetAddress().toString();
    }

    private void writeMetaData(String merkleRoot) {
        try {
            ObjectOutputStream metaDataOutput = new ObjectOutputStream(new FileOutputStream(metadata));
            MetaDataHash.put("merkleRoot", merkleRoot);
            MetaDataHash.put("Tracker", getTrackerInfo());
            MetaDataHash.put("Name", file.getName());
            metaDataOutput.writeObject(MetaDataHash);
            metaDataOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*
     * accept name and content of individual piece as parameter, and write it out to
     * a file on local filesystem
     */
    private void writePiece(String name, byte[] piece) {
        File newFile = new File(Paths.get(this.tempDirectoryPath, name).toString());
        try {
            OutputStream pieceWriter = new FileOutputStream(newFile);
            pieceWriter.write(piece);
            pieceWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String createMerkleRoot(HashMap<String, String> Index) {
        String MerkleRoot = "";
        for (Entry<String, String> entry : Index.entrySet()) {
            MerkleRoot = Hashing.sha256().hashString(MerkleRoot + entry.getValue(), StandardCharsets.UTF_8).toString();
        }
        return MerkleRoot;
    }

    public static void main(String[] args) {
        Socket socket;
        try {
            socket = new Socket("localhost", 8080);
            Encode encode = new Encode("flow.txt", "/home/kushal/.P2P/myuser", socket);
            encode.Split();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
