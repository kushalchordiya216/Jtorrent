package jtorrent.Client;

import java.util.Scanner;

/**
 * perform profile actions like logging in, signing up, loggin out, etc.
 */
public class UserProfile {
    private String username, password;
    Scanner sc = new Scanner(System.in);

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    Boolean isLoggedIn = false;

    public void getCredentials() {
        System.out.println("Enter username");
        this.setUsername(sc.nextLine());
        System.out.println("Enter password");
        this.setPassword(sc.nextLine());
    }
}