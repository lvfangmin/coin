package coin.rule;

public abstract class Rule {
    private String ruleId;

    public Rule(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleId() {
        return ruleId;
    }
}
