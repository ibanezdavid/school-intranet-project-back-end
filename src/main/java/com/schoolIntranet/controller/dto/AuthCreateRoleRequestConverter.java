package com.schoolIntranet.controller.dto;

import com.opencsv.bean.AbstractBeanField;

import java.util.Arrays;
import java.util.List;

public class AuthCreateRoleRequestConverter extends AbstractBeanField<AuthCreateRoleRequest, String> {

    @Override
    protected AuthCreateRoleRequest convert(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        List<String> roleList = Arrays.asList(value.split(","));
        return new AuthCreateRoleRequest(roleList);
    }
}
