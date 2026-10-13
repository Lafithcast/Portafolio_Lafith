package Portafolio_Lafith.service;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SupabaseStorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket}")
    private String bucket;

    private final RestClient restClient = RestClient.create();

    // Sube una imagen a Supabase Storage y devuelve su URL pública.
    public String uploadImage(MultipartFile localFile, String folder, Integer id) throws IOException {
        String originalName = localFile.getOriginalFilename();
        String fileExtension = "";
        if (originalName != null && originalName.contains(".")) {
            fileExtension = originalName.substring(originalName.lastIndexOf("."));
        }

        // Nombre consistente: img + id rellenado a 14 dígitos
        String fileName = "img" + String.format("%014d", id) + fileExtension;
        String path = folder + "/" + fileName;

        String contentType = localFile.getContentType() != null
                ? localFile.getContentType()
                : "application/octet-stream";

        restClient.post()
                .uri(supabaseUrl + "/storage/v1/object/" + bucket + "/" + path)
                .header("Authorization", "Bearer " + supabaseKey)
                .header("apikey", supabaseKey)
                .header("x-upsert", "true") // si ya existe, la reemplaza (útil al modificar)
                .contentType(MediaType.parseMediaType(contentType))
                .body(localFile.getBytes())
                .retrieve()
                .toBodilessEntity();

        // URL pública (el bucket debe ser público). El ?v= evita que el navegador
        // muestre la imagen vieja en caché cuando se reemplaza.
        return supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + path
                + "?v=" + System.currentTimeMillis();
    }
        // Borra la imagen del bucket a partir de la URL pública guardada en la BD.
    public void deleteImage(String publicUrl) {
        if (publicUrl == null || publicUrl.isBlank()) {
            return;
        }
        String prefix = supabaseUrl + "/storage/v1/object/public/" + bucket + "/";
        if (!publicUrl.startsWith(prefix)) {
            return; // no es una imagen de nuestro bucket
        }
        String path = publicUrl.substring(prefix.length());
        int q = path.indexOf('?');
        if (q >= 0) {
            path = path.substring(0, q); // quita el ?v=...
        }

        restClient.delete()
                .uri(supabaseUrl + "/storage/v1/object/" + bucket + "/" + path)
                .header("Authorization", "Bearer " + supabaseKey)
                .header("apikey", supabaseKey)
                .retrieve()
                .toBodilessEntity();
    }
}