import java.time.LocalTime;

public class Message implements Comparable<Message>{

    private String message;
    private LocalTime time;

    public Message(){
        this.message = "";
        this.time = LocalTime.now();
    }
    public Message(String message, LocalTime time) {
        this.message = message;
        this.time = time;
    }

    public int compareTo(Message msg){
        return time.compareTo(msg.time);
    }

    public String getMessage() {
        return message;
    }

    public LocalTime getTime() {
        return time;
    }
}
