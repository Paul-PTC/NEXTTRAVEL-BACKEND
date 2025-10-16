package NEXTTRAVELEXPO2025.NEXTTRAVEL.Cloudinary;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.HashMap;
import java.util.Map;
@Configuration
@CrossOrigin
public class CloudinaryConfig {
    //Variable para almacenar las credenciales de Cloudinary

    private String cloudName;
    private String apiKey;
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary(){


        //Crear un Map para guardar la clave valor del archivo .env
        Map<String,String> config = new HashMap<>();
        config.put("cloud_name", System.getenv("CLOUDINARY_CLOUD_NAME")); // Nombre de la nube en Cloudinary
        config.put("api_key", System.getenv("CLOUDINARY_API_KEY"));  // Api key
        config.put("api_secret", System.getenv("CLOUDINARY_API_SECRET")); //Api Secret

        //Retornar una nueva instancia de Cloud con la config cargada
        return new Cloudinary(config);
    }
}
