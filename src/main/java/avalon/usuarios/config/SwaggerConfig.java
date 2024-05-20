package avalon.usuarios.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "value")
public class SwaggerConfig {

//@Value("${value.title}")
//  String summary;

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .title("Microservice Store Usuario API")
                        .description("Microservice Store Usuarios API is a generic store for anything usuario.")
                        .version("Version 1.0"));
    }
}