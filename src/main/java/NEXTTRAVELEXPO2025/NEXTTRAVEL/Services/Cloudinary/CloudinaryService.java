package NEXTTRAVELEXPO2025.NEXTTRAVEL.Services.Cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
@Service
public class CloudinaryService { //Constante que define el tamaño maximo permitido para los archivos (5MB)
    private static  final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    //Constante para definir los tipos de archivos admitidos
    private static final String[] ALLOWED_EXTENSION = {".jpg", ".jpeg", ".png" , ".gif"};
    //Cliente de Cloudinary inyectado como dependencia
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * @param file
     * @return
     * @throws IOException
     *
     *
     * */
    public String uploadImage(MultipartFile file) throws IOException {
        //1. Validamos el archivo
        validateImage(file);
        //Sube el archivo a Cloudinary con configuraciones basicas
        //tipo de recurso auo-detectado
        //Calidad automatica con nivel "good"
        Map<?,?> uploadResult = cloudinary.uploader()
                .upload(file.getBytes(), ObjectUtils.asMap(
                        "resource_type", "auto",
                        "quality", "auto.good"
                ));
        //Retorna la URL segura de la imagen
        return (String) uploadResult.get("secure_url");
    }
    /**
     * Sube una imagen a una carpeta en especifico
     * @param file
     * @param folder carpeta de destino
     * @return URL segura(https) de la imagean subida
     * @throws  IOException Si ocurre un error durante la subida
     **/
    public String uploadImage(MultipartFile file, String folder) throws  IOException{
        validateImage(file);
        //Generar un nombre unico para el archivo
        // Conservar la extesion original
        //Agregar un prefijo y UUID para evitar colisiones

        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = "img" + UUID.randomUUID() + fileExtension;
        //Subir imagen
        Map<String, Object> options = ObjectUtils.asMap(
                "folder", folder, //Carpeta destino
                "public_id", uniqueFilename, // Nombre unico
                "use_filename", false, // No usar el nombre original
                "unique_filename", false, //No generar nombre unico
                "overwrite", false, //No sobreescribir archivos
                "resource_type", "auto", // Auto-detectar tipo de recurso
                "quality", "auto:good" // Optimizacion de calidad automatica
        );
        //Subir el archivo
        Map<?,?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
        //Retornamos la URL segura
        return (String) uploadResult.get("secure_url");
    }

    private void validateImage(MultipartFile file){
        //1. Verificar si el archivo esta vacio
        if (file.isEmpty()){
            throw new IllegalArgumentException("No puede estar vacio.");
        }
        //2.Verificar el tamaño de la imagen
        if (file.getSize()> MAX_FILE_SIZE){
            throw new IllegalArgumentException("El archivo no puede ser mayor a 5MB");
        }
        //3. Obtener y validar el nombre original del archivo
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null){
            throw new IllegalArgumentException("Nombre de archivo invalido");
        }
        //4. Extraer y validar la extension
        String extension = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();
        if (!Arrays.asList(ALLOWED_EXTENSION).contains(extension)){
            throw new IllegalArgumentException("Solo se permiten archivos JPG, JPEG Y PNG");
        }
        //5.Verficar que el tipo de MIME sea una imagen
        if (!file.getContentType().startsWith("image/")){
            throw new IllegalArgumentException("El archivo debe ser una imagen valida");
        }
    }
}
