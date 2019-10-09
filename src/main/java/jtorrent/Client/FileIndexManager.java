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
			//fileWriter = new ObjectOutputStream(new FileOutputStream(this.indexFile, false));
			//fileWriter.writeObject(l); //?no need to write into file if it already exists
			//fileWriter.close();
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

			List<String> addList = new ArrayList<String>(newList);
			addList.removeAll(prevList);
			List<String> removeList = new ArrayList<String>(prevList);
			removeList.removeAll(newList);

			this.addedFiles = new String[addList.size()];
			this.removedFiles = new String[removeList.size()];
			this.addedFiles = addList.toArray(this.addedFiles);
			this.removedFiles = removeList.toArray(this.removedFiles);

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
