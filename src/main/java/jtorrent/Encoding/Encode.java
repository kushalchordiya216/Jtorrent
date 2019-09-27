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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.common.hash.Hashing;

public class Encode {
    private File file, metadata;
    private BufferedReader reader;
    private HashMap<String, String> MetaDataHash;
    private int index = 0;

    private Encode(String filename) {
        file = new File(filename);
        metadata = new File(filename + ".metadata");
        try {
            FileReader fileReader = new FileReader(file);
            reader = new BufferedReader(fileReader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void Split() {
        int readChar = 0;
        do {
            byte[] piece = new byte[1024 * 1024];
            int byteIndex = 0;
            do {
                try {
                    readChar = reader.read();
                    piece[byteIndex] = (byte) readChar;
                    byteIndex++;
                } catch (IOException e) {
                    System.out.println("Error while reading into file!\n");
                    // do nothing
                }
            } while (readChar != -1 && byteIndex < 1024 * 1024);
            String hash = Integer.toString(Arrays.hashCode(piece));
            this.write(hash, piece);
            MetaDataHash.put(Integer.toString(index), hash);
            index++;
        } while (readChar != -1);
        writeMetaData();
    }

    private String getTrackerInfo() {
        // TODO:return tracker IP/port
        return null;
    }

    private void writeMetaData() {
        try {
            ObjectOutputStream metaDataOutput = new ObjectOutputStream(new FileOutputStream(metadata));
            MetaDataHash.put("merkleRoot", createMerkleRoot(MetaDataHash));
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
    private void write(String name, byte[] piece) {
        File newFile = new File(name);
        try {
            OutputStream oos = new FileOutputStream(newFile);
            oos.write(piece);
            oos.close();
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
        Encode encode = new Encode("/home/kushal/WorkSpace/Java/SDL Project/Final/test.txt");
        encode.Split();
    }
}
