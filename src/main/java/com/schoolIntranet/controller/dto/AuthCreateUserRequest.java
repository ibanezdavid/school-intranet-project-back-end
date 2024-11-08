package com.schoolIntranet.controller.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthCreateUserRequest {

    @CsvBindByName
    @NotBlank
    private String rut;

    @CsvBindByName
    @NotBlank
    private String name;

    @CsvCustomBindByName(converter = LocalDateConverter.class)
    private LocalDate birthday;

    @CsvBindByName
    @NotBlank
    private String email;

    @CsvBindByName
    @NotBlank
    private String adress;

    @CsvBindByName
    @NotBlank
    private String phoneNumber;

    @CsvBindByName
    @NotBlank
    private String password;

    @CsvBindByName
    @NotBlank
    private String username;

    @CsvCustomBindByName(converter = AuthCreateRoleRequestConverter.class)
    @Valid
    private AuthCreateRoleRequest roleRequest;
}
