package com.hkmc.vsmhistory.common.exception;

public class PreStepCallException extends RuntimeException{

    private static final String MESSAGE = "PreStep Call에 실패하였습니다.";

    public PreStepCallException() {
        super(MESSAGE);
    }
}

