package net.pickapack.captcha;

public class Captcha {
    private int id;
    private byte[] data;
    private String text;

    public Captcha(int id, byte[] data, String text) {
        this.id = id;
        this.data = data;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }

    public String getText() {
        return text;
    }
}
