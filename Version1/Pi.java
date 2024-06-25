import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Pi implements Runnable{
    // Numero du processus
    private  int i;
    private  GUI gui;
    Param P ;
    int nbProc ;

    public Pi(int i, GUI gui ,int nbProc ){
        this.i = i;
        this.gui = gui;
        this.P = new Param(nbProc);
        this.nbProc = nbProc;
    }
    

    public void broadcastMessage( Message.MessageType type ,int nbProc , Param p ){
        int timestamp = p.getH(); 
        Message message = new Message(type , this.i , timestamp);
        if ( type.compareTo( Message.MessageType.RENTREE) != 0)
        p.addMessage(this.i, message);
        String dataOut = message.toString();
        for (int j = 1; j < nbProc + 1 ; j++) {
                if (j != this.i) {
                    try {
                        Socket client = new Socket("localhost", j);
                        OutputStream outS = client.getOutputStream();
                        outS.write(dataOut.getBytes());
                        gui.addEvent("   P"+i,"   P"+j,"  Envoi de "+ message.getTypeS() );
                        client.close();

                    } catch (IOException e) {
                    }
                }
            }

            p.incrH();   

    }

   
    public void run() {
        //int nbProc = 3;
        Random random = new Random();
        int sleepDuration;
        Message minTimestampMessage ;
        Message message;
        //Param P = new Param(nbProc);
        //Runnable server = new Server(null, P, this.i, gui);
        ServerSocket serverSocket = null;
        // creation du thread serveur
        try {
            serverSocket = new ServerSocket(this.i);
            Runnable server = new Server(serverSocket, P, this.i, gui);
            Thread thread = new Thread(server);
            thread.start();
            // serverSocket.close();
        } catch (IOException e) {
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        


        gui.editQueue(i , P.FtoListOrdonneString());
        // La partie client
        while (true) {
            gui.editState(i,"   En attente");
            sleepDuration = (random.nextInt(10) + 1) * 1000;
            
            
            try {
                Thread.sleep(sleepDuration);
                // Simulate crash with a random probability during waiting state
                //waitForRandomTime(sleepDuration);
               
                    if (random.nextInt(10) == 5 ) {
                        gui.editState(i, "   En Panne ");
                        if (serverSocket != null && !serverSocket.isClosed()) {
                            serverSocket.close();
                        }
                        return;
                    }

                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }

            gui.editState(i,"  Acces à la SC demandé ");
            
            broadcastMessage( Message.MessageType.REQUEST, nbProc , P);
            

            minTimestampMessage = Message.findMinTimestamp(P.getF());
            message = P.getF().get(this.i  - 1);
            while ( minTimestampMessage.getTimestamp() < P.getF().get(this.i - 1).getTimestamp() || minTimestampMessage.getSenderId() != this.i ) {
                  minTimestampMessage = Message.findMinTimestamp(P.getF());
            }



            gui.editState(i,"   **** En SC ****");
            sleepDuration = (random.nextInt(2) + 1) * 1000;

            try {
                
                Thread.sleep(sleepDuration);
                //waitForRandomTime(sleepDuration);
                
               
            } catch (InterruptedException e) {
            }
             

            broadcastMessage( Message.MessageType.RELEASE , nbProc, P);
            




        }

    }
}
