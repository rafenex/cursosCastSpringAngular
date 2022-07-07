package br.com.castgroup.cursos.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.castgroup.cursos.entities.Curso;
import br.com.castgroup.cursos.form.FormCadastroCurso;
import br.com.castgroup.cursos.form.FormUpdateCurso;
import br.com.castgroup.cursos.repository.CategoriaRepository;
import br.com.castgroup.cursos.repository.CursoRepository;
import br.com.castgroup.cursos.service.CursoService;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {

	@Autowired
	CursoService cursoService;

	@Autowired
	CursoRepository cursoRepository;

	@Autowired
	CategoriaRepository categoriaRepository;

	@PostMapping
	public ResponseEntity<String> cadastrar(@RequestBody FormCadastroCurso request) {
		if (!cursoService.isValid(request, cursoRepository)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cursoService.mensagem());
		}
		Curso curso = request.converter(categoriaRepository);
		cursoRepository.save(curso);
		return ResponseEntity.status(HttpStatus.CREATED).body(cursoService.mensagem());
	}
	
	@SuppressWarnings("deprecation")
	@PutMapping(value = "/{id_curso}")
	public ResponseEntity<?> update(@PathVariable("id_curso") Integer id_curso, @RequestBody FormUpdateCurso request) {
		try {
			Optional<Curso> item = cursoRepository.findById(id_curso);
			if (item.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Curso não encontrado");
			} else {
				
				Curso curso = item.get();
		
				if(!cursoService.isValid(request, cursoRepository, curso)) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cursoService.mensagem());
				}
				curso = request.converter(categoriaRepository, curso);
			
				cursoRepository.save(curso);
				return ResponseEntity.status(HttpStatus.OK).body("Curso atualizado");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
		}
	}

	@GetMapping(value = "/{id_curso}")
	public ResponseEntity<?> findById(@PathVariable("id_curso") Integer id_curso) {
		Optional<Curso> item = cursoRepository.findById(id_curso);
		if (item.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Curso não encontrado");
		}
		return ResponseEntity.status(HttpStatus.OK).body(item);
	}

	@GetMapping
	public ResponseEntity<List<Curso>> listarCursos() {
		return ResponseEntity.status(HttpStatus.OK).body(cursoRepository.findAll());
	}

	@GetMapping(value = "/nome/{descricao}")
	public ResponseEntity<?> findByDescricao(@PathVariable("descricao") String descricao) {
		return ResponseEntity.status(HttpStatus.OK).body(cursoRepository.findByDescricao(descricao));

	}


	@GetMapping(value = "/data/{inicio}/{termino}")
	public List<Curso> findByData(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate inicio,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate termino) {
		return cursoRepository.findByInicioBetween(inicio, termino);
	}

	@DeleteMapping(value = "/{id_curso}")
	public ResponseEntity<String> deleteById(@PathVariable("id_curso") Integer id_curso) {
		try {
			Optional<Curso> item = cursoRepository.findById(id_curso);
			if (item.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Curso não encontrado");
			} else {
				Curso curso = item.get();
				cursoRepository.delete(curso);
				return ResponseEntity.status(HttpStatus.OK).body("Curso deletado com sucesso");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
		}
	}

}
