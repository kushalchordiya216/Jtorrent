package jtorrent.Encoding;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;

public class Decode {
    private HashMap<String, String> metaDataHash;
    private String merkleRoot;
    private String fileName, metaFileName;
    private String rootDirectory, currentDirectory;
    private File metaFile, file, metaFileCopy;

    @SuppressWarnings({ "unchecked" })
    public Decode(String metaFileName, String rootDirectory) {
        try {
            this.metaFileName = metaFileName;
            this.metaFile = new File(this.metaFileName);
            this.currentDirectory = metaFile.getParent();
            System.out.println(this.currentDirectory);
            ObjectInputStream readMetaFile = new ObjectInputStream(new FileInputStream(this.metaFile));
            try {
                this.metaDataHash = (HashMap<String, String>) readMetaFile.readObject();
            } catch (Exception e) {
                System.out.println("Corrupted metadata!\nUse valid metadata file");
                System.exit(-1);
            }
            this.merkleRoot = this.metaDataHash.get("merkleRoot");
            this.fileName = this.metaDataHash.get("Name");
            this.file = Paths.get(this.currentDirectory, this.fileName).toFile();
            this.rootDirectory = Paths.get(rootDirectory, this.merkleRoot).toString();
            readMetaFile.close();
        } catch (IOException e) {
            System.out.println("Error reading metadata file");
            e.printStackTrace();
        }
    }

    public void Merge() {
        try {
            OutputStream outputStream = new FileOutputStream(this.file);
            for (int i = 0; i < metaDataHash.size(); i++) {
                byte[] content = read(this.metaDataHash.get(Integer.toString(i)));
                outputStream.write(content);
            }
            outputStream.close();
            createMetaCopy();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createMetaCopy() {
        this.metaFileCopy = Paths.get(this.rootDirectory, this.metaFileName).toFile();
        try {
            if (!this.metaFileCopy.exists()) {
                this.metaFileCopy.createNewFile();
            }
            ObjectOutputStream fileWriter = new ObjectOutputStream(new FileOutputStream(this.metaFileCopy));
            fileWriter.writeObject(this.metaDataHash);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] read(String fileName) {
        File piece = Paths.get(this.rootDirectory, fileName).toFile();
        byte[] pieceContent = new byte[(int) piece.length()];
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

    public String getMerkleRoot() {
        return this.merkleRoot;
    }

    public String getFileName() {
        return fileName;
    }

    public HashMap<String, String> getMetaDataHash() {
        return this.metaDataHash;
    }

    public static void main(String[] args) {
        Decode decode = new Decode("/home/kushal/Downloads/flow.txt.metadata", "/home/kushal/.P2P/kushal");
        decode.Merge();
    }
}