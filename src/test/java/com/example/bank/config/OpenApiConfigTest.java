package com.example.bank.config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class OpenApiConfigTest {

    @Test
    void apiBeanShouldReturnConfiguredOpenAPI() {
        OpenApiConfig config = new OpenApiConfig();
        OpenAPI openAPI = config.api();

        assertNotNull(openAPI);

        // Check Info
        Info info = openAPI.getInfo();
        assertNotNull(info);
        assertEquals("Eagle Bank API", info.getTitle());
        assertEquals("v1", info.getVersion());
        assertEquals("REST API for Eagle Bank coding test", info.getDescription());

        // Check Security Requirement
        assertNotNull(openAPI.getSecurity());
        assertFalse(openAPI.getSecurity().isEmpty());
        SecurityRequirement securityRequirement = openAPI.getSecurity().get(0);
        assertTrue(securityRequirement.containsKey("bearerAuth"));

        // Check Security Scheme
        Components components = openAPI.getComponents();
        assertNotNull(components);
        SecurityScheme securityScheme = components.getSecuritySchemes().get("bearerAuth");
        assertNotNull(securityScheme);
        assertEquals(SecurityScheme.Type.HTTP, securityScheme.getType());
        assertEquals("bearer", securityScheme.getScheme());
        assertEquals("JWT", securityScheme.getBearerFormat());
    }
}