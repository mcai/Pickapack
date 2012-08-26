package net.pickapack.captcha;

import javax.swing.*;

public class ManualCaptchaSolver implements CaptchaSolver {
    @Override
    public CaptchaSolveResult solveCaptcha(byte[] data) {
        ImageIcon imageIcon = new ImageIcon(data);
        JLabel label = new JLabel("Enter the text in the captcha image.", imageIcon, SwingConstants.LEFT);
        String text = JOptionPane.showInputDialog(null, label, "Solve CAPTCHA", JOptionPane.QUESTION_MESSAGE);
        return new CaptchaSolveResult(CaptchaSolveResultType.OK, new Captcha(-1, data, text));
    }

    @Override
    public boolean reportWrongCaptcha(int captchaId) {
        return true;
    }

    @Override
    public double getBalance() {
        return Double.MAX_VALUE;
    }
}
