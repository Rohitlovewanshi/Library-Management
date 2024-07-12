package com.example.minorproject1.dtos;

import com.example.minorproject1.model.Admin;
import com.example.minorproject1.model.Student;
import com.example.minorproject1.model.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class CreateAdminRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private String name;

    public Admin mapToAdmin(){
        return Admin.builder()
                .name(this.name)
                .user(
                        User.builder()
                                .username(this.username)
                                .password(this.password)
                                .build()
                )
                .build();
    }
}
