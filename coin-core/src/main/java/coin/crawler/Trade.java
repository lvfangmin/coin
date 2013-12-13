public class Trade {
    private double price;
    private double amount;

    public Trade(double price, double amount) {
        this.price = price;
        this.amount = amount;
    }
 
    public String toString() {
        return Double.toString(price) + ":" + Double.toString(amount);
    }
}
