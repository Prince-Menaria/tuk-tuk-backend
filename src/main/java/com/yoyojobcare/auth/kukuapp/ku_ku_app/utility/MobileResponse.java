package com.yoyojobcare.auth.kukuapp.ku_ku_app.utility;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MobileResponse<T> {

    private boolean status;
    private String message;
    private T data;

}
