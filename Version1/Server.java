import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    private ServerSocket server;
    private Param p;
    private int i;
    private GUI gui;
    private volatile boolean isCrashed = false;

    public Server(ServerSocket server, Param p, int i, GUI gui) {
        this.server = server;
        this.p = p;
        this.i = i;
        this.gui = gui;
    }

    public void setCrashed() {
        this.isCrashed = true;
    }

    public void run() {
        while (true) {
            if (isCrashed) return;

            try {
                Socket socket = server.accept();
                ClientHandler client = new ClientHandler(socket, p, this.i, gui);
                if (isCrashed) {
                    client.setCrashed();
                }
                Thread thread = new Thread(client);
                thread.start();
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
    }
}
