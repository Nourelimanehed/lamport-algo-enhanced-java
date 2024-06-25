import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Param {

    private int h = 0 ;
    private LinkedList<Message> f = new LinkedList<>();
    
    

    public Param(int nbProc) {
         // Création d'une liste de messages avec le deuxième paramètre variant en fonction de nCopies
      
    for (int i = 1; i <= nbProc; i++) {
    Message message = new Message(Message.MessageType.RELEASE, i, 0); // Utilisation de la valeur de la boucle comme deuxième paramètre
    f.add( message);
}


    }

    public LinkedList<Message> getF() {
        return f;
    }

    public void setF(LinkedList<Message> f) {
        this.f = f;
    }

    

    public int getH() {
        return h;
    }

    public int incrH() {
        this.h ++;
        return this.h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public void addMessage(int index , Message message){
        this.f.set(index - 1 , message);
    }

   public String FtoListOrdonneString() {
  // Create a copy of the original list to avoid modifying it
  List<Message> messages = new ArrayList<>(f);

  // Sort the list by the message's timestamp (h value)
  messages.sort(Comparator.comparingInt(Message::getTimestamp));

  // Build a String representation of the ordered list
  StringBuilder sb = new StringBuilder();
  for (Message message : messages) {
    sb.append("P"+message.getSenderId()+" "+message.getTypeSumm() + " | " ).append("\n"); // Add newline character for each message
  }

  // Return the String representation
  return sb.toString().trim(); // Remove trailing newline if any
}







}