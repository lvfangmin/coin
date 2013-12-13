package coin.data;

import java.util.List;

public class Data {
   private String type;
   private double latestPrice;
   private List<Trade> buy;
   private List<Trade> sell;

   public Data() {

   }

   public Data(String type) {
      this.type = type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public void setLatestPrice(double latestPrice) {
      this.latestPrice = latestPrice;
   }

   public void setBuy(List<Trade> buy) {
      this.buy = buy;
   }

   public void setSell(List<Trade> sell) {
      this.sell = sell;
   }

   public String getType() {
      return this.type;
   }

   public double getLatestPrice() {
      return this.latestPrice;
   }

   public List<Trade> getBuy() {
      return this.buy;
   }

   public List<Trade> getSell() {
      return this.sell;
   }
}
