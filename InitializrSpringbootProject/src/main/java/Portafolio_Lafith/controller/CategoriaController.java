/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// CategoriaController.java
package Portafolio_Lafith.controller;

import Portafolio_Lafith.service.CategoriaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import Portafolio_Lafith.domain.Categoria;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequestMapping("/categoria")
public class CategoriaController {
    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping("/listado")
    public String inicio(Model model) {
        var categorias = categoriaService.getCategorias(false);
        model.addAttribute("categorias", categorias);
        model.addAttribute("totalCategorias", categorias.size());
        return "/categoria/listado";
    }
    
        @PostMapping("/guardar")
    public String guardar(@Valid Categoria categoria,
                          @RequestParam("imagenFile") MultipartFile imagenFile) {
        categoriaService.save(categoria, imagenFile);
        return "redirect:/categoria/listado";
    }
    
        @GetMapping("/modificar/{idCategoria}")
    public String actualizar(@PathVariable Integer idCategoria, Model model) {
        Categoria categoria = categoriaService.getCategoria(idCategoria);
        model.addAttribute("categoria", categoria);
        return "/categoria/modifica";
    }
        @PostMapping("/eliminar")
    public String eliminar(@RequestParam("idCategoria") Integer idCategoria) {
        categoriaService.delete(idCategoria);
        return "redirect:/categoria/listado";
    }
    
    
}