package jtorrent.Encoding;

import java.io.*;
import java.util.HashMap;

public class Decode {
    private HashMap<String, String> metaDataHash;

    public Decode(String metaFile) throws IOException, ClassNotFoundException {
        File metaFile1 = new File(metaFile);
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(metaFile1));
        this.metaDataHash = (HashMap<String, String>) ois.readObject();
        ois.close();
    }

    public String getMerkleRoot() {
        return this.metaDataHash.get("merkleRoot");
    }

    public void Merge() throws IOException {
        File file = new File(this.metaDataHash.get("Name"));
        OutputStream outputStream = new FileOutputStream(file);
        for (int i = 0; i < metaDataHash.size(); i++) {
            byte[] content = read(this.metaDataHash.get(Integer.toString(i)));
            outputStream.write(content);
            outputStream.flush();
        }
        new Thread(() -> {
            // TODO: add this file in list of files, with filename and hash. announce to
            // tracker
        }).start();
        outputStream.close();
    }

    private byte[] read(String fileName) throws IOException {
        byte[] pieceContent = new byte[1024 * 1024];
        File piece = new File(fileName);
        InputStream inputStream = new FileInputStream(piece);
        inputStream.read(pieceContent);
        inputStream.close();
        return pieceContent;
    }
}