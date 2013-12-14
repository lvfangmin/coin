package coin.rule;

import coin.redis.data.UserData;

public abstract class PriceRule extends Rule {

    public PriceRule(String ruleId) {
        super(ruleId);
    }

    public abstract boolean meet(UserData user, double currentPrice, String type);

    public abstract String generateMessage();

    public static class GEPriceRule extends PriceRule {

        public GEPriceRule(String ruleId) {
            super(ruleId);
        }

        @Override
        public boolean meet(UserData user, double currentPrice, String type) {
            String[] userParams = user.param.split(":");
            String listenType = userParams[0];
            String listenValue = userParams[1];

            if (!listenType.equals(type)) {
                return false; 
            }

            if (currentPrice >= Double.valueOf(listenValue)) {
                return true;
            }

            return false;
        }

        @Override
        public String generateMessage() {
            return null;
        }
    }

    public static class LEPriceRule extends PriceRule {

        public LEPriceRule(String ruleId) {
            super(ruleId);
        }

        @Override
        public boolean meet(UserData user, double currentPrice, String type) {
            String[] userParams = user.param.split(":");
            String listenType = userParams[0];
            String listenValue = userParams[1];

            if (!listenType.equals(type)) {
                return false; 
            }

            if (currentPrice <= Double.valueOf(listenValue)) {
                return true;
            }

            return false;
        }

        @Override
        public String generateMessage() {
            return null;
        }
    }

}
