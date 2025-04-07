package com.bincher.getCoupon.dto.request.auth;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignUpRequestDto {
    
    @Id 
    String id;

    @NotBlank @Size(min=8, max=20)
    @Pattern(regexp = "^(?=.*[a-xA-Z])(?=.*[0-9])[a-zA-Z0-9]{8,13}$")
    String password;
    
    String telNumber;
}
