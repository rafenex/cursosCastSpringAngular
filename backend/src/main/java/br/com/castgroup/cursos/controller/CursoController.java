package br.com.castgroup.cursos.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import br.com.castgroup.cursos.entities.Log;
import br.com.castgroup.cursos.repository.CategoriaRepository;
import br.com.castgroup.cursos.repository.CursoRepository;
import br.com.castgroup.cursos.repository.LogRepository;
import br.com.castgroup.cursos.repository.UsuarioRepository;
import br.com.castgroup.cursos.service.CursoService;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {

	@Autowired
	CursoService cursoService;

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String>cadastrar(@RequestBody Curso request){
		try {
			cursoService.cadastrarCurso(request);
			return ResponseEntity.status(HttpStatus.CREATED).body("Curso cadastrado com sucesso");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
	
	@PutMapping(value = "/{id_curso}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String>atualizar(@PathVariable("id_curso") Integer id_curso, @RequestBody Curso request){
		try {
			cursoService.atualizarCurso(request, id_curso);
			return ResponseEntity.status(HttpStatus.CREATED).body("Curso atualizado com sucesso");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
	
	@GetMapping(value = "/data/{inicio}/{termino}")
	public List<Curso> findByData(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate inicio,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate termino) {		
		return cursoService.listarPorData(inicio, termino);
	}
	
	@GetMapping(value = "/{id_curso}")
	public ResponseEntity<?> findById(@PathVariable("id_curso") Integer id_curso) {		
		try {
			return ResponseEntity.status(HttpStatus.OK).body(cursoService.acharPorId(id_curso));			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}		
	}


	@GetMapping
	public List<Curso> listarCursos() {		
		return cursoService.acharTodos();
	}

	@GetMapping(value = "/nome/{descricao}")
	public ResponseEntity<List<Curso>> findByDescricao(@PathVariable("descricao") String descricao) {
		return ResponseEntity.status(HttpStatus.OK).body(cursoService.acharPorDescricao(descricao));
	}
	
	

	@DeleteMapping(value = "/{id_curso}")
	public ResponseEntity<String> deleteById(@PathVariable("id_curso") Integer id_curso) {		
		try {
			cursoService.deletarCursoPorId(id_curso);
			return ResponseEntity.status(HttpStatus.OK).body("Curso deletado com sucesso");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
		
		
	}

}
