package net.pickapack.captcha;

public enum CaptchaSolveResultType {
    OK,
    FAILED_UPLOADING,
    FAILED_SOLVING,
    FAILED_ACCESS_DENIED,
    FAILED_SERVICE_OVERLOAD,
    FAILED_INVALID_CAPTCHA,
    FAILED_MISC
}
