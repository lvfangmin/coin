package coin.notify;

public class Notification {
    private final String destination;
    private final String content;
    private final DestinationType type;
    
    public Notification(String destination, DestinationType type, String content) {
        this.destination = destination;     
        this.type = type;
        this.content = content;
    }

    public String getDestination() {
        return destination;
    }

    public String getContent() {
        return content;
    }

    public DestinationType getDestinationType() {
        return type; 
    }

    public static enum DestinationType {
        MAIL, SMS
    }
}
