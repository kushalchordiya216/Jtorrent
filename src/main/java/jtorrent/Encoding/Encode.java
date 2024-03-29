package jtorrent.Encoding;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.common.hash.Hashing;

public class Encode {
    private String filename;
    private File file, metadata, tempDirectory;
    private FileInputStream reader;
    private HashMap<String, String> MetaDataHash = new HashMap<String, String>();
    private String tempDirectoryPath = null, rootDirectoryPath; // tempoarary directory name where encoded pieces will//
                                                                // be stored
    private Socket trackerEndpoint;
    private float fileSize;

    public Encode(String filename, String rootDirectory, Socket socket) {
        file = new File(filename);
        String tempArr[] = filename.split("/");
        filename = tempArr[tempArr.length - 1];
        this.filename = tempArr[tempArr.length - 1];
        this.trackerEndpoint = socket;
        this.rootDirectoryPath = rootDirectory;
        this.tempDirectoryPath = Paths.get(rootDirectory, this.filename).toString();
        this.tempDirectory = new File(this.tempDirectoryPath);
        if (!this.tempDirectory.exists()) {
            this.tempDirectory.mkdirs();
        }
        metadata = new File(Paths.get(this.tempDirectoryPath, this.filename + ".metadata").toString()); // metadatafile
        if (!metadata.exists()) {
            try {
                metadata.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } // created
        try {
            reader = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void Split() {
        try {
            long totalFileLength = file.length();
            this.fileSize = (float) totalFileLength / (1024 * 1024);
            long index = 0;
            long numPieces = totalFileLength / (1024 * 1024);
            int remainder = (int) totalFileLength % (1024 * 1024);
            while (numPieces > 0) {
                byte[] piece = new byte[1024 * 1024];
                reader.read(piece);
                String hash = Hashing.sha256().hashBytes(piece).toString();
                this.writePiece(hash, piece);
                MetaDataHash.put(Long.toString(index), hash);
                index++;
                numPieces--;
            }
            byte[] lastPiece = new byte[remainder];
            System.out.println("last piece is " + remainder + " bytes");
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
            MetaDataHash.put("fileSizeMB", Float.toString(this.fileSize));
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
            Encode encode = new Encode("/home/kushal/brave-browser.svg", "/home/kushal/.P2P/myuser", socket);
            encode.Split();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
