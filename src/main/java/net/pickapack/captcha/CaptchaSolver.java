package net.pickapack.captcha;

public interface CaptchaSolver {
    CaptchaSolveResult solveCaptcha(byte[] in);

    boolean reportWrongCaptcha(int captchaId);

    double getBalance();
}
