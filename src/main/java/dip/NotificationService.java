package dip;

public class NotificationService {

    public String notifyUser(String channel, String message) {
        if ("email".equalsIgnoreCase(channel)) {
            return new EmailSender().send(message);
        } else if ("sms".equalsIgnoreCase(channel)) {
            return new SmsSender().send(message);
        }
        return message;
    }
}
