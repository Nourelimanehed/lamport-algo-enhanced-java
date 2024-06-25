import java.util.*;



public class Message {
   
    public enum MessageType {
        REQUEST, RELEASE, ACK , ABSENT , PING , RENTREE
    }

    private MessageType type;
    private int senderId;
    private int timestamp;

    public Message(MessageType type, int senderId, int timestamp) {
        this.type = type;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }

    public MessageType getType() {
        return type;
    }


    public String getTypeS( ) {
        if (this.type.compareTo(MessageType.REQUEST)  == 0) return "Requète";
        else if (this.type.compareTo(MessageType.RELEASE) == 0) return "Release";
        else if (this.type.compareTo(MessageType.ACK) == 0) return "Acquittement";
        else if (this.type.compareTo(MessageType.PING) == 0) return "Ping";
        else if (this.type.compareTo(MessageType.RENTREE) == 0) return "Rentrée";
        else return "Absent";
    }

    public String getTypeSumm( ) {
        if (this.type.compareTo(MessageType.REQUEST)  == 0) return "Req";
        else if (this.type.compareTo(MessageType.RELEASE) == 0) return "Rel";
        else if (this.type.compareTo(MessageType.ACK) == 0) return "Ack";
        else if (this.type.compareTo(MessageType.PING) == 0) return "P";
        else if (this.type.compareTo(MessageType.RENTREE) == 0) return "Rent";
        else return "Abs";
    }

    public int getSenderId() {
        return senderId;
    }

    public int getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", senderId=" + senderId +
                ", timestamp=" + timestamp +
                '}';
    }


    public static Message fromString(String str) {
        // Remove "Message{" and "}" from the string
        str = str.substring("Message{".length(), str.length() - 1);

        // Split the string by commas
        String[] parts = str.split(", ");

        // Extract values for type, senderId, and timestamp
        MessageType type = MessageType.valueOf(parts[0].split("=")[1]);
        int senderId = Integer.parseInt(parts[1].split("=")[1]);
        int timestamp = Integer.parseInt(parts[2].split("=")[1]);
        
        // Create and return a new Message object
        return new Message(type, senderId, timestamp);
    }

    public static Message findMinTimestamp(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            throw new IllegalArgumentException("Queue of messages is empty or null");
        }

        Message minTimestampMessage = messages.stream()
        .filter(message -> message.getType() != Message.MessageType.ABSENT) // Filtrer les messages de type ABSENT
        .min(Comparator.comparing(Message::getTimestamp)) // Trouver le message avec le plus petit timestamp
        .orElseThrow(() -> new IllegalStateException("No suitable message found"));
    
        // Message minTimestampMessage = messages.stream()
        //                         .min(Comparator.comparing(Message::getTimestamp))
        //                         .orElseThrow(() -> new IllegalStateException("Queue of messages is empty"));
                                //System.out.println(minTimestampMessage.toString();
                                

        return minTimestampMessage;
    }
    

}
