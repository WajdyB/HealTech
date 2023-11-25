package com.authentification.payload;

import lombok.Data;

@Data
public class ForgotPasswordResponse {
    private String emailMessage;
    private String tokenMessage;

    public ForgotPasswordResponse(String emailMessage, String tokenMessage) {
        this.emailMessage = emailMessage;
        this.tokenMessage = tokenMessage;
    }

}

