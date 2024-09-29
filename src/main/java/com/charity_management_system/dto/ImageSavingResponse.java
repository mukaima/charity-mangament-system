package com.charity_management_system.dto;

import lombok.Data;

@Data
public class ImageSavingResponse {

    private int status;
    private String message;
    private String url;
}
