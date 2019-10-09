package jtorrent.Communication.Requests;

public class UpdateRequest implements Request {

    private static final long serialVersionUID = 1726946734101117489L;
    private final String requestType = "UPDATE";
    private String[] addedFiles = null, removedFiles = null;
    private String hostname = null, username = null;
    private Integer port = null, filesizeMB = null;

    public UpdateRequest(String username, String[] addedFiles, String[] removedFiles) {
        this.username = username;
        this.addedFiles = addedFiles;
        this.removedFiles = removedFiles;
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
        this.hostname = hostName;
    }

    public String[] getAddedFiles() {
        return this.addedFiles;
    }

    public String[] getRemovedFiles() {
        return this.removedFiles;
    }

    public String getUsername() {
        return username;
    }

    public Integer getFilesizeMB() {
        return filesizeMB;
    }
}