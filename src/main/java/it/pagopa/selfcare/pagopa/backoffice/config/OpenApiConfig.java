package it.pagopa.selfcare.pagopa.backoffice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.servers.ServerVariable;
import io.swagger.v3.oas.models.servers.ServerVariables;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static it.pagopa.selfcare.pagopa.backoffice.util.Constants.HEADER_REQUEST_ID;


@Configuration
public class OpenApiConfig {

    public static final String BASE_PATH = "/backoffice/v1";

    @Bean
  OpenAPI customOpenAPI(
      @Value("${info.application.name}") String appName,
      @Value("${info.application.description}") String appDescription,
      @Value("${info.application.version}") String appVersion) {
    return new OpenAPI()
      .servers(List.of(new Server().url("http://localhost:8080"),
          new Server().url("https://{host}{basePath}")
              .variables(new ServerVariables()
                .addServerVariable("host",
                  new ServerVariable()._enum(List.of("api.dev.platform.pagopa.it","api.uat.platform.pagopa.it","api.platform.pagopa.it"))
                      ._default("api.dev.platform.pagopa.it"))
                .addServerVariable("basePath", new ServerVariable()._default(BASE_PATH))
              )))
        .components(
            new Components()
                .addSecuritySchemes(
                    "SubKey",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .description("The Azure Subscription Key to access this API.")
                        .name("Ocp-Apim-Subscription-Key")
                        .in(SecurityScheme.In.HEADER))
                .addSecuritySchemes(
                    "JWT",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .description("JWT token get after Login")
                        .scheme("bearer")
                        .bearerFormat("JWT")))
        .info(
            new Info()
                .title(appName)
                .version(appVersion)
                .description(appDescription)
                .termsOfService("https://www.pagopa.gov.it/"));
  }


  @Bean
  public OpenApiCustomiser addCommonHeaders() {
    return openApi ->
        openApi.getPaths().forEach(
                (key, value) -> {
                  // add Request-ID as request header
                    value.addParametersItem(new Parameter()
                            .in("header")
                            .name(HEADER_REQUEST_ID)
                            .schema(new StringSchema())
                            .description("This header identifies the call, if not passed it is self-generated. This ID is returned in the response."));

                  // add Request-ID as response header
                  value.readOperations().forEach(
                          operation -> operation
                                  .getResponses()
                                  .values()
                                  .forEach(response -> response.addHeaderObject(HEADER_REQUEST_ID, new Header()
                                                  .schema(new StringSchema())
                                                  .description(
                                                      "This header identifies the call"))));
                });
  }
}
