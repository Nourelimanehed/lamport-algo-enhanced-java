import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class Coordinator implements Runnable {
    private int nbProc;
    private GUI gui;
    private volatile boolean running;

    public Coordinator(int nbProc, GUI gui) {
        this.nbProc = nbProc;
        this.gui = gui;
        this.running = true;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        gui.addEvent("   P11", "", "  Initialisation ");
        startHeartbeat();
    }

    public void startHeartbeat() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (running) {
                    checkProcesses();
                } else {
                    timer.cancel(); // Stop monitoring if not running
                }
            }
        }, 0, 3000); // Check every 3 seconds
    }

    private void checkProcesses() {
        Message pingMessage = new Message(Message.MessageType.PING, 11, -1);
        String dataOut = pingMessage.toString();
        for (int i = 1; i <= nbProc; i++) {
            try (Socket socket = new Socket("localhost", i)) {
                System.out.println("Sending to: " + i);
                OutputStream outS = socket.getOutputStream();
                outS.write(dataOut.getBytes());
                outS.flush();
            } catch (IOException e) {
                // Process is unreachable, likely crashed
                System.out.println("Process Pi " + i + " has crashed!");
                broadcastCrash(i);
                reactivateProcess(i);
            }
        }
    }

    private void reactivateProcess(int procId) {
        Pi p = new Pi(procId, gui , nbProc);
        Thread t = new Thread(p);
        t.start();
        p.broadcastMessage(Message.MessageType.RENTREE, this.nbProc, p.P);
    }

    public void broadcastCrash(int procId) {
        gui.addEvent("   P11 ", "   All", " P" + procId + "   En panne");
        Message message = new Message(Message.MessageType.ABSENT, procId, 0);
        String dataOut = message.toString();
        for (int j = 1; j <= nbProc; j++) {
            try {
                Socket client = new Socket("localhost", j);
                OutputStream outS = client.getOutputStream();
                outS.write(dataOut.getBytes());
                //gui.addEvent("   P11", "   P" + j, "  Envoi de " + message.getTypeS());
                outS.flush();
                client.close();
            } catch (IOException e) {
                System.err.println("Unable to send message to P" + j + " : " + e.getMessage());
            }
        }
    }

    public void stop() {
        this.running = false; // Stop the monitoring loop
    }
}
