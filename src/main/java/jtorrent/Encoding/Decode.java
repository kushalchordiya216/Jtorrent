package jtorrent.Encoding;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;

public class Decode {
    private HashMap<String, String> metaDataHash;
    private String merkleRoot;
    private String fileName, metaFileName;
    private String rootDirectory, currentDirectory;
    private File metaFile, file;

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
            System.out.println(this.fileName);
            this.file = Paths.get(this.currentDirectory, this.fileName).toFile();
            System.out.println(this.file.getName());
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
            for (int i = 0; i < metaDataHash.size() - 4; i++) {
                byte[] content = read(this.metaDataHash.get(Integer.toString(i)));
                for (byte c : content) {
                    if (c != 0) {
                        outputStream.write(c);
                    }
                }
            }
            outputStream.close();
            CreateMetaFileCopy();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void CreateMetaFileCopy() {
        File metaFileCopy = Paths.get(this.rootDirectory, this.metaFileName).toFile();
        if (!metaFileCopy.exists()) {
            try {
                metaFileCopy.createNewFile();
                ObjectOutputStream metaFileWriter = new ObjectOutputStream(new FileOutputStream(metaFileCopy));
                metaFileWriter.writeObject(this.metaDataHash);
                metaFileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] read(String fileName) {
        byte[] pieceContent = new byte[1024 * 1024];
        File piece = Paths.get(this.rootDirectory, fileName).toFile();
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
        Decode decode = new Decode(
                "/home/kushal/.P2P/myuser/03afa7aae4b80a056f9a41bfdec79be2679c5e469aa8224fb8884f823da3ebef/brave-browser.svg.metadata",
                "/home/kushal/.P2P/myuser");
        decode.Merge();
    }
}