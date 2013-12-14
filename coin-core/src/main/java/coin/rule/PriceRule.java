package coin.rule;

public abstract class PriceRule extends Rule {

    public PriceRule(String ruleId) {
        super(ruleId);
    }

    public class GEPriceRule extends PriceRule {

        public GEPriceRule(String ruleId) {
            super(ruleId);
        }
    }

}
