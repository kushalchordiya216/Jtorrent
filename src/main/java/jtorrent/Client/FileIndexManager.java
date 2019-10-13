package jtorrent.Client;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

public class FileIndexManager {

	String rootDirectory = null;
	String[] addedMerkleRoots, removedMerkleRoots, addedFileNames;
	File indexFile;
	File folder;
	ObjectOutputStream fileWriter;

	public FileIndexManager(String username) {
		this.rootDirectory = System.getProperty("user.home") + "/.P2P/" + username;

		folder = new File(rootDirectory);
		if (!(folder.exists() && folder.isDirectory())) {
			folder.mkdirs();
		}
		try {
			List<String> l = new ArrayList<String>();
			this.indexFile = new File(this.rootDirectory + "/indexFile.ser");
			if (!this.indexFile.exists()) {
				this.indexFile.createNewFile();
				fileWriter = new ObjectOutputStream(new FileOutputStream(this.indexFile, false));
				fileWriter.writeObject(l);
				fileWriter.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void CheckForChanges() {
		try {
			String files[] = folder.list();
			List<String> newList = new ArrayList<String>(Arrays.asList(files));
			newList.remove("indexFile.ser");
			ObjectInputStream fileReader = new ObjectInputStream(new FileInputStream(this.indexFile));
			@SuppressWarnings({ "unchecked" })
			List<String> prevList = (List<String>) fileReader.readObject();
			for (String oldfile : prevList) {
				System.out.println(oldfile);
			}
			for (String newfile : newList) {
				System.out.println("newfile: " + newfile);
			}
			List<String> addList = new ArrayList<String>(newList);
			addList.removeAll(prevList);
			List<String> removeList = new ArrayList<String>(prevList);
			removeList.removeAll(newList);

			this.addedMerkleRoots = new String[addList.size()];
			this.removedMerkleRoots = new String[removeList.size()];
			this.addedMerkleRoots = addList.toArray(this.addedMerkleRoots);
			this.removedMerkleRoots = removeList.toArray(this.removedMerkleRoots);
			// TODO: test metafile copy
			ObjectOutputStream fileWriter = new ObjectOutputStream(new FileOutputStream(this.indexFile, false));
			fileWriter.writeObject(newList);

			fileReader.close();
			fileWriter.close();
			getFileNames();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void getFileNames() {
		ArrayList<String> addedList = new ArrayList<String>();
		for (String merkleRoot : this.addedMerkleRoots) {
			File directory = Paths.get(this.rootDirectory, merkleRoot).toFile();
			String[] filelist = directory.list();
			for (String file : filelist) {
				if (file.contains(".metadata")) {
					addedList.add(file.substring(0, file.length() - 9));
				}
			}
		}
		this.addedFileNames = new String[addedList.size()];
		this.addedFileNames = addedList.toArray(this.addedFileNames);

	}

	public String[] getAddedMerkleRoots() {
		return this.addedMerkleRoots;
	}

	public String[] getRemovedMerkleRoots() {
		return this.removedMerkleRoots;
	}

	public String[] getAddedFileNames() {
		return this.addedFileNames;
	}
}
