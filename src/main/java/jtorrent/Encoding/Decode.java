package jtorrent.Encoding;

import java.io.*;
import java.util.HashMap;

public class Decode {
    private HashMap<String, String> metaDataHash;
    private String username;
    // TODO: Read from proper path
    // TODO: Create copy of metadata in proper path
    public Decode(String metaFilePath,String username) {
    	this.username = username;
    	metaDataHash = new HashMap<String, String>();
        File metaFile1 = new File(metaFilePath);
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
        File file = new File(System.getProperty("user.home") + "/Downloads/" + this.metaDataHash.get("Name"));
        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);    
            for (int i = 0; i < metaDataHash.size()-3; i++) {
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
        File piece = new File(System.getProperty("user.home") + "/.P2P/" + username + "/" + getMerkleRoot() + "/" +fileName);
        byte[] pieceContent = new byte[(int) piece.length()];
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(piece);
            inputStream.read(pieceContent);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pieceContent;
    }
//    public static void main(String args[])
//    {
//    	Decode ob = new Decode("/home/chetan/.P2P/Chetan/c5a50472f73cc1b3cfc643494369622b62099707e7be52c62d6a5b3b34812b1a/a1.txt.metadata","Chetan");
//    	ob.Merge();
//    }
}
