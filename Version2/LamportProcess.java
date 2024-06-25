import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class LamportProcess {
    private int id;
    private int port;
    private long timestamp;
    private PriorityQueue<Request> requestQueue;
    private Set<Integer> acks;
    private boolean inCriticalSection;
    private Random random;
    private volatile boolean isActive;
    private static final int TOTAL_PROCESSES = 10;
    private static volatile int FAILED_PROCESSES = 0;
    private static final int FAILURE_PROBABILITY = 10; // 10% chance of failure
    private static final int FAILURE_DURATION = 3000; // Failure duration in milliseconds
    private static LamportProcessGUI gui;

    public LamportProcess(int id, int port, LamportProcessGUI gui) {
        this.id = id;
        this.port = port;
        this.timestamp = 0;
        this.requestQueue = new PriorityQueue<>();
        this.acks = ConcurrentHashMap.newKeySet();
        this.inCriticalSection = false;
        this.random = new Random();
        this.isActive = true;
        LamportProcess.gui = gui;
    }

    public void run() {
        // Thread to handle incoming messages
        new Thread(new Server()).start();

        while (true) {
            try {
                Thread.sleep(random.nextInt(1000) + 1000); // Wait 1-2 seconds
                //generate a failure every 2 seconds 
                if (isActive && FAILED_PROCESSES < 2 && random.nextInt(100) < FAILURE_PROBABILITY) {
                    simulateFailure();
                }
                if (isActive) {
                    enterCriticalSection();
                    Thread.sleep(random.nextInt(1000) + 1000); // Critical section for 1-2 seconds
                    exitCriticalSection();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void simulateFailure() {
        isActive = false;
        FAILED_PROCESSES++;
        gui.updateProcessStatus(id, "Failed");
        gui.logMessage("Process " + id + " has failed at "+timestamp);
        broadcastMessage("FAILURE", this.timestamp, this.id);
        // Simulate failure by stopping all activities
        try {
            Thread.sleep(FAILURE_DURATION); // Failure duration
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Restart the process
        isActive = true;
        FAILED_PROCESSES--;
        gui.updateProcessStatus(id, "Active");
        gui.logMessage("Process " + id + " has recovered.");
        broadcastMessage("RECOVER", this.timestamp, this.id);
        gui.updateTimestamp(id, timestamp);
    }
    
    private void enterCriticalSection() {
        incrementTimestamp();
        gui.updateTimestamp(id, timestamp);
        Request myRequest = new Request(timestamp, id);
        requestQueue.add(myRequest);
        acks.clear();
        gui.logMessage("Process " + id + " requested for critical section at timestamp " + timestamp);
        broadcastMessage("REQUEST", timestamp, id);
    
        while (!canEnterCriticalSection(myRequest)) {
            try {
                Thread.sleep(100); // Wait a bit before checking again
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    
        inCriticalSection = true;
        gui.logMessage("Process " + id + " entered critical section at timestamp " + timestamp);
        updateQueueLabel();
    }
    
    private void exitCriticalSection() {
        inCriticalSection = false;
        requestQueue.poll(); // Remove own request
        broadcastMessage("RELEASE", timestamp, id);
        gui.logMessage("Process " + id + " exited critical section at timestamp " + timestamp);
        updateQueueLabel();
        gui.updateTimestamp(id, timestamp);
    }
    

    private void incrementTimestamp() {
        timestamp++;
    }

    private boolean canEnterCriticalSection(Request myRequest) {
        if (!isActive) return false;
        synchronized (requestQueue) {
            if (acks.size() < TOTAL_PROCESSES - 1 - FAILED_PROCESSES) { // Need ACKs from other processes
                return false;
            }
            Request head = requestQueue.peek();
            return head != null && head.equals(myRequest) && head.getId() == id;
        }
    }

    private void broadcastMessage(String type, long timestamp, int id) {
        if (!isActive) return;
        for (int i = 1; i <= TOTAL_PROCESSES; i++) {
            if (i != this.id) {
                int finalI = i;
                new Thread(() -> {
                    try (Socket socket = new Socket("localhost", 5000 + finalI);
                         PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                        out.println(type + " " + timestamp + " " + id);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }
    }

    private class Server implements Runnable {
        public void run() {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                while (true) {
                    try (Socket clientSocket = serverSocket.accept();
                         BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                        String message = in.readLine();
                        if (message != null) {
                            handleMessage(message);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void handleMessage(String message) {
            String[] parts = message.split(" ");
            String type = parts[0];
            long timestamp = Long.parseLong(parts[1]);
            int id = Integer.parseInt(parts[2]);

            synchronized (LamportProcess.this) {
                LamportProcess.this.timestamp = Math.max(LamportProcess.this.timestamp, timestamp) + 1;
            }

            switch (type) {
                case "REQUEST":
                    synchronized (requestQueue) {
                        requestQueue.add(new Request(timestamp, id));
                    }
                    sendMessage("ACK", LamportProcess.this.timestamp, id);
                    updateQueueLabel();
                    break;
                case "RELEASE":
                    synchronized (requestQueue) {
                        requestQueue.removeIf(req -> req.getId() == id);
                    }
                    updateQueueLabel();
                    break;
                case "ACK":
                    acks.add(id);
                    break;
                case "CHECK":
                    sendMessage("ALIVE", LamportProcess.this.timestamp, id);
                    break;
                case "FAILURE":
                    gui.logMessage("The process " + LamportProcess.this.id + " detected restarting of process " + id +" at "+timestamp);
                    break;
                case "RECOVER":
                    gui.logMessage("The process " + LamportProcess.this.id + " detected failure of process " + id +" at "+timestamp);
                    break;
                default:
                    break;
            }
        }

        private void sendMessage(String type, long timestamp, int targetId) {
            new Thread(() -> {
                try (Socket socket = new Socket("localhost", 5000 + targetId);
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                    out.println(type + " " + timestamp + " " + LamportProcess.this.id);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private static class Request implements Comparable<Request> {
        private final long timestamp;
        private final int id;

        public Request(long timestamp, int id) {
            this.timestamp = timestamp;
            this.id = id;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public int getId() {
            return id;
        }

        @Override
        public int compareTo(Request o) {
            if (this.timestamp != o.timestamp) {
                return Long.compare(this.timestamp, o.timestamp);
            }
            return this.id - o.id;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Request request = (Request) obj;
            return timestamp == request.timestamp && id == request.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(timestamp, id);
        }
    }

    private void updateQueueLabel() {
        StringBuilder queueBuilder = new StringBuilder();
        queueBuilder.append("[");
        for (Request req : requestQueue) {
            queueBuilder.append("(").append(req.getTimestamp()).append(",").append(req.getId()).append(")");
        }
        queueBuilder.append("]");
        gui.updateQueue(id, queueBuilder.toString());
    }

    public static void main(String[] args) {
        LamportProcessGUI gui = new LamportProcessGUI(TOTAL_PROCESSES);
        for (int i = 1; i <= TOTAL_PROCESSES; i++) {
            int port = 5000 + i;
            int finalI = i;
            new Thread(() -> new LamportProcess(finalI, port, gui).run()).start();
        }
    }
}
