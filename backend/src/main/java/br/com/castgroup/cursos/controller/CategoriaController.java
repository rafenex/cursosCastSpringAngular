package br.com.castgroup.cursos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.castgroup.cursos.entities.Categoria;
import br.com.castgroup.cursos.service.CategoriaService;
import io.swagger.annotations.ApiOperation;
@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {
	

	@Autowired
	CategoriaService categoriaService;
	
	@ApiOperation("Serviço para listar categorias")
	@CrossOrigin
	@GetMapping
	public List<Categoria> listarCursos() {		
		return categoriaService.acharTodos();
	}


}