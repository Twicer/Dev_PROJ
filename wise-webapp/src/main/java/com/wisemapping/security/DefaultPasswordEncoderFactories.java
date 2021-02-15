package com.wisemapping.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

public class DefaultPasswordEncoderFactories {

    public static final String ENCODING_ID = "bcrypt";

    static PasswordEncoder createDelegatingPasswordEncoder() {

        final Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(ENCODING_ID, new BCryptPasswordEncoder(12));

        DelegatingPasswordEncoder result = new DelegatingPasswordEncoder(ENCODING_ID, encoders);
        result.setDefaultPasswordEncoderForMatches(new LegacyPasswordEncoder());

        return result;
    }

}