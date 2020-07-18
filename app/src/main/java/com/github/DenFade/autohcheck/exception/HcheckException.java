package com.github.DenFade.autohcheck.exception;

import java.io.IOException;

public class HcheckException extends IOException {
    public HcheckException(String cause){
        super(cause);
    }
}
