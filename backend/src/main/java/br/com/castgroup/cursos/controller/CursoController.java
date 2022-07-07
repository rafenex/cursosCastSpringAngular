package br.com.castgroup.cursos.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
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

import br.com.castgroup.cursos.dto.CursoDTO;
import br.com.castgroup.cursos.entities.Curso;
import br.com.castgroup.cursos.form.FormCadastroCurso;
import br.com.castgroup.cursos.form.FormUpdateCurso;
import br.com.castgroup.cursos.repository.CategoriaRepository;
import br.com.castgroup.cursos.repository.CursoRepository;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {

	@Autowired
	CursoRepository cursoRepository;

	@Autowired
	CategoriaRepository categoriaRepository;

	@SuppressWarnings("deprecation")
	@PostMapping
	public ResponseEntity<String> cadastrar(@RequestBody FormCadastroCurso request) {
		try {
			Curso curso = new Curso();			
			curso.setCategoria(categoriaRepository.getById(request.getIdCategoria()));
			curso.setDescricao(request.getDescricao());
			curso.setTermino(request.getTermino());
			curso.setInicio(request.getInicio());
			curso.setQuantidadeAlunos(request.getQuantidade());
			if (request.getTermino().isBefore(request.getInicio())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Não é permitida a inclusão de cursos com a data de início menor que a data atual.");				
			} 
			for(Curso curso1 : cursoRepository.findAll()) {
				if (request.getInicio().isBefore(curso1.getInicio()) && (request.getTermino().isAfter(curso1.getInicio()))) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body("Existe(m) curso(s) planejados(s) dentro do período informado.");			
				}									
			}
			
			cursoRepository.save(curso);
			return ResponseEntity.status(HttpStatus.CREATED).body("Curso cadastrado com sucesso");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro: " + e.getMessage());
		}
	}

	@GetMapping
	public ResponseEntity<List<CursoDTO>> listarCursos() {
		List<CursoDTO> response = new ArrayList<>();

		for (Curso curso : cursoRepository.findAll()) {
			CursoDTO cursoDto = new CursoDTO();
			cursoDto.setId_curso(curso.getId_curso());
			cursoDto.setCategoria(curso.getCategoria().getCategoria());
			cursoDto.setDescricao(curso.getDescricao());
			cursoDto.setInicio(curso.getInicio());
			cursoDto.setQuantidade(curso.getQuantidadeAlunos());
			cursoDto.setTermino(curso.getTermino());
			response.add(cursoDto);
		}
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping(value = "/{id_curso}")
	public ResponseEntity<?> findById(@PathVariable("id_curso") Integer id_curso) {
		Optional<Curso> item = cursoRepository.findById(id_curso);
		if (item.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Curso não encontrado");
		} else {
			CursoDTO response = new CursoDTO();
			Curso curso = item.get();
			response.setCategoria(curso.getCategoria().getCategoria());
			response.setDescricao(curso.getDescricao());
			response.setInicio(curso.getInicio());
			response.setQuantidade(curso.getQuantidadeAlunos());
			response.setTermino(curso.getTermino());
			return ResponseEntity.status(HttpStatus.OK).body(response);
		}
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

	@SuppressWarnings("deprecation")
	@PutMapping(value = "/{id_curso}")
	public ResponseEntity<?> update(@PathVariable("id_curso") Integer id_curso, @RequestBody FormUpdateCurso request) {
		try {
			Optional<Curso> item = cursoRepository.findById(id_curso);
			if (item.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Curso não encontrado");
			} else {
				Curso curso = item.get();
				curso.setCategoria(categoriaRepository.getById(request.getIdCategoria()));
				curso.setDescricao(request.getDescricao());
				curso.setTermino(request.getTermino());
				curso.setInicio(request.getInicio());
				curso.setQuantidadeAlunos(request.getQuantidade());
				cursoRepository.save(curso);
				return ResponseEntity.status(HttpStatus.OK).body("Curso atualizado");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
		}

	}

}
