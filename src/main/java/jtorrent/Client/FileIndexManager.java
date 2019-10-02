package jtorrent.Client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

public class FileIndexManager {

	String rootDirectory = null;
	String[] addedFiles, removedFiles;
	File indexFile;

	public FileIndexManager(String username) {
		this.rootDirectory = System.getProperty("user.home") + "/.P2P/" + username + "/";
	}

	public void CheckForChanges() {
		try {
			File folder = new File(rootDirectory);
			if (folder.exists() && folder.isDirectory()) {
				this.indexFile = new File(this.rootDirectory + "indexFile.ser");
			} else {
				folder.mkdir();
				this.indexFile = new File(this.rootDirectory + "indexFile.ser");
			}
			String files[] = folder.list();
			List<String> newList = new ArrayList<String>(Arrays.asList(files));
			newList.remove("indexFile.ser");
			ObjectInputStream fileReader = new ObjectInputStream(new FileInputStream(this.indexFile));
			@SuppressWarnings({ "unchecked" })
			List<String> prevList = (List<String>) fileReader.readObject();

			List<String> addList = new ArrayList<String>(newList);
			addList.removeAll(prevList);
			List<String> removeList = new ArrayList<String>(prevList);
			removeList.removeAll(newList);

			this.addedFiles = (String[]) addList.toArray();
			this.removedFiles = (String[]) removeList.toArray();

			ObjectOutputStream fileWriter = new ObjectOutputStream(new FileOutputStream(this.indexFile, false));
			fileWriter.writeObject(newList);

			fileReader.close();
			fileWriter.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public String[] getAddedFiles() {
		return this.addedFiles;
	}

	public String[] getRemovedFiles() {
		return this.removedFiles;
	}

}
