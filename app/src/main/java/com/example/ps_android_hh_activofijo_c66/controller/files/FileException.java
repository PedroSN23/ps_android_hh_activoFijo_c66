package com.example.ps_android_hh_activofijo_c66.controller.files;

public class    FileException extends Throwable {
    static final long serialVersionUID = -3387516993124229968L;

    public FileException() {
        super();
    }

    public FileException(String message) {
        super(message);
    }

    public FileException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileException(Throwable cause) {
        super(cause);
    }

    protected FileException(String message, Throwable cause, boolean enableSuppression, boolean     writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
