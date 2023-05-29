package com.example.uploadingfiles.storage;

import org.springframework.http.HttpStatus;


public class StorageFileNotFoundException  extends RuntimeException{

    public StorageFileNotFoundException(String message){
        super(message);
    }

    public HttpStatus status(){
        return HttpStatus.BAD_REQUEST;
    }
}
