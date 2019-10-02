package jtorrent.Encoding;

import java.io.*;
import java.util.HashMap;

public class Decode {
    private HashMap<String, String> metaDataHash;

    // TODO: Read from proper path
    // TODO: Create copy of metadata in proper path
    public Decode(String metaFile) {
        File metaFile1 = new File(metaFile);
        ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(new FileInputStream(metaFile1));
            this.metaDataHash = (HashMap<String, String>) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e1) {
            System.out.println("Error reading metadata file");
            e1.printStackTrace();
        }
    }

    public String getMerkleRoot() {
        return this.metaDataHash.get("merkleRoot");
    }

    public void Merge() {
        File file = new File(this.metaDataHash.get("Name"));
        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
            for (int i = 0; i < metaDataHash.size(); i++) {
                byte[] content = read(this.metaDataHash.get(Integer.toString(i)));
                outputStream.write(content);
                outputStream.flush();
            }
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, String> getMetaDataHash() {
        return this.metaDataHash;
    }

    private byte[] read(String fileName) {
        byte[] pieceContent = new byte[1024 * 1024];
        File piece = new File(fileName);
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(piece);
            inputStream.read(pieceContent);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pieceContent;
    }
}