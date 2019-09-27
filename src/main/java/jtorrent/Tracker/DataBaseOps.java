package jtorrent.Tracker;

import java.sql.*;
import java.util.ArrayList;

public class DataBaseOps {
    private Connection connection = null;
    PreparedStatement stmt = null;

    public DataBaseOps() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String password = "rootpasswd123";
            String username = "root";
            String URL = "jdbc:mysql://localhost:3306/P2P";
            connection = DriverManager.getConnection(URL, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateFileOwners(PeerThread peerThread, String[] addedFiles, String[] removedFiles) {
        try {
            stmt = connection
                    .prepareStatement("INSERT INTO FileOwners (username, merkleRoot, currentIP) VALUES(?,?,?)");
            stmt.setString(1, peerThread.getUsername());
            stmt.setString(3, peerThread.getSocket().getInetAddress().toString());
            for (String addedFile : addedFiles) {
                stmt.setString(2, addedFile);
                stmt.executeUpdate();
            }
            stmt = connection.prepareStatement("DELETE FROM FileOwners WHERE username=? AND merkleRoot=?");
            stmt.setString(1, peerThread.getUsername());
            for (String removedFile : removedFiles) {
                stmt.setString(2, removedFile);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> queryFileOwners(String merkleRoot) throws SQLException {
        ArrayList<String> peers = new ArrayList<String>();
        stmt = connection
                .prepareStatement("SELECT currentIP, username FROM FileOwners WHERE merkleRoot=? AND active=?");
        stmt.setString(1, merkleRoot);
        stmt.setBoolean(2, true);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            peers.add(rs.getString("username"));
        }
        return peers;
    }

    public Integer addUser(String username, String IP, boolean active, String password) {
        try {
            stmt = connection
                    .prepareStatement("INSERT INTO Users(username, password,currentIP,active) VALUES(?,?,?,?)");
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, IP);
            stmt.setBoolean(4, active);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void addFile() {

    }

    public Integer updateUser(String username, String IP, boolean active, String password) throws SQLException {
        stmt = connection.prepareStatement("UPDATE TABLE Users SET IP=?, active=? WHERE username=? AND password=?");
        stmt.setString(1, IP);
        stmt.setBoolean(2, active);
        stmt.setString(3, username);
        stmt.setString(4, password);
        return stmt.executeUpdate();
    }
}
