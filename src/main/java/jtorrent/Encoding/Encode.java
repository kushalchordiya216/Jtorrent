package jtorrent.Encoding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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
    // TODO: test out all functionalities
    // TODO: write to proper path
    private File file, metadata;
    private BufferedReader reader;
    private HashMap<String, String> MetaDataHash;
    private String tempDirectory = null; // temporary directory name where encoded pieces will be stored
    private String rootDirectory=null;
    public Encode(String filepath, String Username) {
    	MetaDataHash = new HashMap<String, String>();
        file = new File(filepath);
        this.rootDirectory = System.getProperty("user.home")+"/.P2P/"+Username;
        this.tempDirectory = rootDirectory +  "/temp";
        File f = new File(this.tempDirectory);
        f.mkdir();
        metadata = new File(this.tempDirectory + "/" + file.getName() + ".metadata"); // metadatafile created inside temp directory
      
    }

    public void Split() {
        int index = 0;
        long fileParts=(long)file.length()/(1024*1024);
		int remainPartLength=(int)((long)file.length()%(1024*1024));
		try {
			FileInputStream reader = new FileInputStream(file);
			for(index = 0;index < fileParts;index++)
			{
				byte piece[] = new byte[1024*1024];
				reader.read(piece);
				String hash = Hashing.sha256().hashBytes(piece).toString();
				this.writePiece(hash, piece);
				MetaDataHash.put(Integer.toString(index), hash);
			}
			byte piece[] = new byte[remainPartLength];
			reader.read(piece);
			String hash = Hashing.sha256().hashBytes(piece).toString();
			this.writePiece(hash, piece);
			MetaDataHash.put(Integer.toString(index), hash);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        String merkleRoot = createMerkleRoot(MetaDataHash); // once all pieces are created, obtain merkleRoot of the
        writeMetaData(merkleRoot); // write metadata file
        File tempDirectoryFolder = new File(this.tempDirectory);
        tempDirectoryFolder.renameTo(new File(this.rootDirectory + "/" + merkleRoot));
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
        File newFile = new File(this.tempDirectory +"/" + name);
        try {
            FileOutputStream pieceWriter = new FileOutputStream(newFile);
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

//    public static void main(String args[]) {
//    Encode e=new Encode("/home/chetan/Downloads/a1.txt","Chetan");
//        e.Split();
//    }
}
