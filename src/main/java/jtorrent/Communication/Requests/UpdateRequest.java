package jtorrent.Communication.Requests;

public class UpdateRequest implements Request {

    private static final long serialVersionUID = 1726946734101117489L;
    private final String requestType = "UPDATE";
    private String[] addedMerkleRoots = null, removedMerkleRoots = null, addedFileNames = null, removedFileNames = null;
    private String hostname = null, username = null;
    private Integer port = null, filesizeMB = null;

    public UpdateRequest(String username, String[] addedMerkleFiles, String[] removedMerkleRoots,
            String[] addedFileNames) {
        this.username = username;
        this.addedMerkleRoots = addedMerkleFiles;
        this.removedMerkleRoots = removedMerkleRoots;
        this.addedFileNames = addedFileNames;
    }

    @Override
    public String getRequestType() {
        return this.requestType;
    }

    @Override
    public String getHostName() {
        return this.hostname;
    }

    @Override
    public Integer getPort() {
        return this.port;
    }

    @Override
    public void setHostName(String hostName) {
        this.hostname = hostName.substring(1);
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

    public String[] getRemovedFileNames() {
        return this.removedFileNames;
    }

    public String getUsername() {
        return username;
    }

    public Integer getFilesizeMB() {
        return filesizeMB;
    }
}