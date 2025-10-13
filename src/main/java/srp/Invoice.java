package srp;

import java.io.PrintWriter;

public class Invoice {
    private final String customer;
    private final double amount;

    public Invoice(String customer, double amount) {
        this.customer = customer;
        this.amount = amount;
    }

    public String summary() {
        return customer + " owes " + amount;
    }

    public void saveToFile(String path) {
        try (PrintWriter w = new java.io.PrintWriter(path)) { w.println(summary()); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    public void sendEmail(String to) {
        System.out.println("Email to " + to + ": " + summary());
    }
}
