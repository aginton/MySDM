package models.order;

public enum eRate {
    POOR(1), AVERAGE(2), GOOD(3), EXCELLENT(4), AWESOME(5);

    private int value;

    private eRate(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
