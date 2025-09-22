package NEXTTRAVELEXPO2025.NEXTTRAVEL.Config.App;

import NEXTTRAVELEXPO2025.NEXTTRAVEL.Utils.JWTUtils;
import NEXTTRAVELEXPO2025.NEXTTRAVEL.Utils.JwtCookieAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public JwtCookieAuthFilter jwtCookieAuthFilter(JWTUtils jwtUtils){
        return new JwtCookieAuthFilter(jwtUtils);
    }

}
