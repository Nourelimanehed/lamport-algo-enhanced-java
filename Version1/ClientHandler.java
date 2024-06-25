import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private Param p;
    private int i;
    private GUI gui;
    private volatile boolean isCrashed = false;

    public void setCrashed() {
        this.isCrashed = true;
    }

    public ClientHandler(Socket socket, Param p, int i, GUI gui) {
        this.socket = socket;
        this.p = p;
        this.i = i;
        this.gui = gui;
    }

    public void sendAckMessage(int senderId ,int receiverId){
        Message message = new Message(Message.MessageType.ACK, senderId, p.getH());
        String dataOut = message.toString();
        try {
            Socket client = new Socket("localhost", receiverId);
            OutputStream outS = client.getOutputStream();
            outS.write(dataOut.getBytes());
           // System.out.println(this.i + " : J'ai envoyer un aqquitt à " + receiverId);
            gui.addEvent("   P"+i,"   P"+receiverId,"  Envoi de "+ message.getTypeS() );
            client.close();

        } catch (IOException e) {
        }
    }

    public void sendReponseMessage(int senderId ,int receiverId){
        Message message = p.getF().get(senderId - 1);
        String dataOut = message.toString();
        try {
            Socket client = new Socket("localhost", receiverId);
            OutputStream outS = client.getOutputStream();
            outS.write(dataOut.getBytes());
           // System.out.println(this.i + " : J'ai envoyer un aqquitt à " + receiverId);
            gui.addEvent("   P"+i,"   P"+ receiverId,"  Envoi de "+ message.getTypeS() );
            client.close();

        } catch (IOException e) {
        }
    }

    public void sendAckCorrdinateur(int senderId ){
        Message message = new Message(Message.MessageType.ACK, senderId,-1);
        String dataOut = message.toString();
        try {
            Socket client = new Socket("localhost", 5000);
            OutputStream outS = client.getOutputStream();
            outS.write(dataOut.getBytes());
           // System.out.println(this.i + " : J'ai envoyer un aqquitt à " + receiverId);
            gui.addEvent("   P"+i,"   P"+11,"  Envoi de "+ message.getTypeS() );
            client.close();

        } catch (IOException e) {
        }
    }

    

    public void run() {
        try {
            if (isCrashed){  return; };
            byte[] buffer = new byte[1024];
            InputStream inS = socket.getInputStream();
            int bytesRead = inS.read(buffer);
            String dataIn = new String(buffer, 0, bytesRead);

            //System.out.println(dataIn); 
            Message message = Message.fromString(dataIn);
            //System.out.println(message.toString()); 
            p.setH(Math.max( message.getTimestamp(), p.getH())  + 1 );
            gui.editH(i, ""+ p.getH());
            if (message.getType().equals(Message.MessageType.REQUEST)){
                p.addMessage(message.getSenderId(), message);
                gui.editQueue(i , p.FtoListOrdonneString());
                sendAckMessage(this.i, message.getSenderId());
            }
           else if (message.getType().equals(Message.MessageType.RELEASE)){
            p.addMessage(message.getSenderId(), message);
            gui.editQueue(i , p.FtoListOrdonneString());
           }
           else if (message.getType().equals(Message.MessageType.ACK)){
                Message messageJ = p.getF().get(message.getSenderId() - 1);
                // System.out.println(message.toString()); 
               //  System.out.println("je suis "+ this.i +"   Sender id " + message.getSenderId()+ " type :" + messageJ.getType()); 
                     if (Message.MessageType.REQUEST.compareTo(messageJ.getType()) != 0  ){
                         p.addMessage(message.getSenderId(), message);
                         gui.editQueue(i , p.FtoListOrdonneString());
                       //  System.out.println("je suis "+ this.i +"   Sender id " + message.getSenderId() +"  done"); 
                     }
            }
            else  if (message.getType().equals(Message.MessageType.ABSENT)){
                  p.addMessage(message.getSenderId(), message);   
                  gui.editQueue(i , p.FtoListOrdonneString());    
            }
            else  if (message.getType().equals(Message.MessageType.RENTREE)){
                sendReponseMessage(this.i ,message.getSenderId());
                      
          }
          

           

           

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
