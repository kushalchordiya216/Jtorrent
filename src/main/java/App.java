import jtorrent.Tracker.Tracker;
import gui.src.gui.*;

public class App {
    public static void main(String[] args) {
        Tracker tracker = new Tracker();
        new Thread(() -> {
            tracker.runTracker();
        });
        Login login = new Login();
        login.main();
    }
}