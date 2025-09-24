package com.example.bankcards.config;

import com.example.bankcards.constants.SecurityConstants;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition( info = @Info(
        contact = @Contact(
                name = "y.kosachou",
                email = "zanoza.by@gmail.com"
        ),
        description = "A simple bank api",
        title = "bank api",
        version = "1.0",
        license = @License(
                name = "I sure we have license",
                url = "https://our-license-url.com"
        ),
        termsOfService = "Terms of service :P"
))
@SecurityScheme(
        name = SecurityConstants.AUTH_BEARER_TOKEN,
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)

public class OpenApiConfiguration {
}

