package com.wf.spring_jwt_oauth.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RequestLoginDto {
    private String email;
    private String password;
}
