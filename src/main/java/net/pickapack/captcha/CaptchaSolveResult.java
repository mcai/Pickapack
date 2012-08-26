package net.pickapack.captcha;

import java.io.Serializable;

public class CaptchaSolveResult implements Serializable {
    private CaptchaSolveResultType type;
    private Captcha captcha;

    public CaptchaSolveResult(CaptchaSolveResultType type, Captcha captcha) {
        this.type = type;
        this.captcha = captcha;
    }

    public CaptchaSolveResultType getType() {
        return type;
    }

    public Captcha getCaptcha() {
        return captcha;
    }
}
