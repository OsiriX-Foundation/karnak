package org.karnak.profilepipe.action;

public abstract class AbstractAction implements ActionItem {

    protected final String symbol;
    protected String dummyValue;

    public AbstractAction(String symbol) {
        this.symbol = symbol;
        this.dummyValue = null;
    }

    public AbstractAction(String symbol, String dummyValue) {
        this.symbol = symbol;
        this.dummyValue = dummyValue;
    }

    public static AbstractAction convertAction(String action) {
        if (action == null) {
            return null;
        }
        return switch (action) {
            case "Z" -> new ReplaceNull("Z");
            case "X" -> new Remove("X");
            case "K" -> new Keep("K");
            case "U" -> new UID("U");
            case "DDum" -> new DefaultDummy("DDum");
            case "D" -> new Replace("D");
            default -> null;
        };
    }

    public String getSymbol() {
        return symbol;
    }

    public String getDummyValue() {
        return dummyValue;
    }

    public void setDummyValue(String dummyValue) {
        this.dummyValue = dummyValue;
    }

}
