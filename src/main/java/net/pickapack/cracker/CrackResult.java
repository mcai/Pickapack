package net.pickapack.cracker;

public class CrackResult {
    private boolean cracked;
    private String crackedPassword;
    private int endIndex;

    public CrackResult(boolean cracked, String crackedPassword, int endIndex) {
        this.cracked = cracked;
        this.crackedPassword = crackedPassword;
        this.endIndex = endIndex;
    }

    public boolean isCracked() {
        return cracked;
    }

    public String getCrackedPassword() {
        return crackedPassword;
    }

    public int getEndIndex() {
        return endIndex;
    }
}
