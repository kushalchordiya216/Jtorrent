package jtorrent.Encoding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.common.hash.Hashing;

public class Encode {
    // TODO: test out all functionalities
    // TODO: write to proper path
    private File file, metadata;
    private BufferedReader reader;
    private HashMap<String, String> MetaDataHash;
    private String tempDirectory = null; // tempoarary directory name where encoded pieces will be stored

    public Encode(String filename, String rootDirectory) {
        file = new File(filename);
        this.tempDirectory = rootDirectory + "temp/";
        metadata = new File(this.tempDirectory + filename + ".metadata"); // metadatafile created inside temp directory
        try {
            FileReader fileReader = new FileReader(file);
            reader = new BufferedReader(fileReader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void Split() {
        int index = 0; // index for a piece, this will be used to construct the metadataHashmap
        int readChar = 0;
        do {
            byte[] piece = new byte[1024 * 1024];
            int byteIndex = 0; // read data byte by byte, so that no extra garbage gets appended at the end
            do {
                try {
                    readChar = reader.read();
                    piece[byteIndex] = (byte) readChar;
                    byteIndex++;
                } catch (IOException e) {
                    System.out.println("Error while reading into file!\n");
                    return;
                }
            } while (readChar != -1 && byteIndex < 1024 * 1024);
            String hash = Hashing.sha256().hashBytes(piece).toString();
            this.writePiece(hash, piece);
            MetaDataHash.put(Integer.toString(index), hash);
            index++;
        } while (readChar != -1);
        String merkleRoot = createMerkleRoot(MetaDataHash); // once all pieces are created, obtain merkleRoot of the
        writeMetaData(merkleRoot); // write metadata file
        File tempDirectoryFolder = new File(this.tempDirectory);
        tempDirectoryFolder.renameTo(new File(createMerkleRoot(MetaDataHash))); // rename tempoaray folder with
                                                                                // merkleRootfolder
    }

    private String getTrackerInfo() {
        // TODO:return tracker IP/port
        return null;
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
        File newFile = new File(name);
        try {
            OutputStream pieceWriter = new FileOutputStream(this.tempDirectory + newFile);
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

    public void main() {
        Split();
    }
}
