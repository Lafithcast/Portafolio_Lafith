// CategoriaService.java
package Portafolio_Lafith.service;

import Portafolio_Lafith.domain.Categoria;
import Portafolio_Lafith.repository.CategoriaRepository;
import java.io.IOException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final SupabaseStorageService storageService;

    public CategoriaService(CategoriaRepository categoriaRepository,
                            SupabaseStorageService storageService) {
        this.categoriaRepository = categoriaRepository;
        this.storageService = storageService;
    }
    
        @Transactional(readOnly = true)
    public Categoria getCategoria(Integer idCategoria) {
        return categoriaRepository.findById(idCategoria).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Categoria> getCategorias(boolean activo) {
        if (activo) {
            return categoriaRepository.findByActivoTrue();
        }
        return categoriaRepository.findAll();
    }

    @Transactional
    public void save(Categoria categoria, MultipartFile imagenFile) {
        categoria = categoriaRepository.save(categoria);
        if (imagenFile != null && !imagenFile.isEmpty()) {
            try {
                String url = storageService.uploadImage(
                        imagenFile, "categoria", categoria.getIdCategoria());
                categoria.setRutaImagen(url);
                categoriaRepository.save(categoria);
            } catch (IOException e) {
                throw new RuntimeException("Error al subir la imagen", e);
            }
        }
    }
        @Transactional
    public void delete(Integer idCategoria) {
        Categoria categoria = categoriaRepository.findById(idCategoria).orElse(null);
        if (categoria == null) {
            return;
        }
        try {
            storageService.deleteImage(categoria.getRutaImagen());
        } catch (Exception e) {
            // si falla el borrado de la imagen, igual se elimina la categoría
        }
        categoriaRepository.deleteById(idCategoria);
    }
}