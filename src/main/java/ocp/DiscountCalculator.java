package ocp;

public class DiscountCalculator {

    public double computeDiscount(String customerType, double amount) {
        if (customerType == null) return 0.0;
      return switch (customerType.toUpperCase()) {
        case "STANDARD" -> 0.0;
        case "PREMIUM" -> amount * 0.10;
        case "VIP" -> amount * 0.20;
        default -> 0.0;
      };
    }
}
