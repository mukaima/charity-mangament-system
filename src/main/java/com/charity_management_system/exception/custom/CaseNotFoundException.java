package com.charity_management_system.exception.custom;

public class CaseNotFoundException extends RuntimeException{

    public CaseNotFoundException(String message){
        super(message);
    }
}
