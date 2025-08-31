package NEXTTRAVELEXPO2025.NEXTTRAVEL.Controllers.Config.Cloudinary;

import com.cloudinary.Cloudinary;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
@Configuration
public class CloudinaryConfig {
    //Variable para almacenar las credenciales de Cloudinary

    private String cloudName;
    private String apiKey;
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary(){
        //Crear un objeto de tipo Dotenv
        Dotenv dotenv = Dotenv.load();

        //Crear un Map para guardar la clave valor del archivo .env
        Map<String,String> config = new HashMap<>();
        config.put("cloud_name",dotenv.get("CLOUDINARY_CLOUD_NAME")); // Nombre de la nube en Cloudinary
        config.put("api_key", dotenv.get("CLOUDINARY_API_KEY"));  // Api key
        config.put("api_secret", dotenv.get("CLOUDINARY_API_SECRET")); //Api Secret

        //Retornar una nueva instancia de Cloud con la config cargada
        return new Cloudinary(config);
    }
}
