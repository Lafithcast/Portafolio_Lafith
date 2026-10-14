/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Portafolio_Lafith.service;

import Portafolio_Lafith.domain.Producto;
import Portafolio_Lafith.repository.ProductoRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final SupabaseStorageService storageService;

    public ProductoService(ProductoRepository productoRepository,
                           SupabaseStorageService storageService) {
        this.productoRepository = productoRepository;
        this.storageService = storageService;
    }

    @Transactional(readOnly = true)
    public List<Producto> getProductos(boolean activo) {
        if (activo) {
            return productoRepository.findByActivoTrue();
        }
        return productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Producto> getProducto(Integer idProducto) {
        return productoRepository.findById(idProducto);
    }

    @Transactional
    public void save(Producto producto, MultipartFile imagenFile) {
        producto = productoRepository.save(producto); // primero guarda para obtener el id
        if (imagenFile != null && !imagenFile.isEmpty()) {
            try {
                String url = storageService.uploadImage(
                        imagenFile, "producto", producto.getIdProducto());
                producto.setRutaImagen(url);
                productoRepository.save(producto);
            } catch (IOException e) {
                throw new RuntimeException("Error al subir la imagen", e);
            }
        }
    }

    @Transactional
    public void delete(Integer idProducto) {
        // Verifica que el producto exista antes de intentar eliminarlo
        Optional<Producto> productoOpt = productoRepository.findById(idProducto);
        if (productoOpt.isEmpty()) {
            throw new IllegalArgumentException("El producto con ID " + idProducto + " no existe.");
        }
        try {
            productoRepository.deleteById(idProducto);
            productoRepository.flush(); // fuerza el borrado para detectar errores de integridad ya
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar el producto. Tiene datos asociados.", e);
        }
        // Solo si se borró la fila, se borra la imagen del bucket
        // (si la ruta es una URL externa, deleteImage no hace nada)
        try {
            storageService.deleteImage(productoOpt.get().getRutaImagen());
        } catch (Exception e) {
            // si falla el borrado de la imagen, el producto ya se eliminó igual
        }
    }
}
