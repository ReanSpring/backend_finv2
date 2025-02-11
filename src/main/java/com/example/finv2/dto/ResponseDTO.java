package com.example.finv2.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ResponseDTO<M> {
    private String message;
    private String status;
    private Object data;

    public ResponseDTO(String message, Object data, String status) {
        this.message = message;
        this.status = status;
        this.data = data;
    }

    public static ResponseDTO<Map<String, String>> success(Map<String, String> tokenMap, String tokenGeneratedSuccessfully) {
        return new ResponseDTO<>("Success", tokenMap, "200");
    }


}

