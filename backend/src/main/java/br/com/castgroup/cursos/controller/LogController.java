package br.com.castgroup.cursos.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.castgroup.cursos.dtos.LogDTO;
import br.com.castgroup.cursos.entities.Log;
import br.com.castgroup.cursos.repository.LogRepository;

@CrossOrigin
@RestController
@RequestMapping("/api/logs")
public class LogController {
	
	@Autowired
	LogRepository logRepository;



	@GetMapping
	public List<Object> listarLog() {		
		List<Object> lista = new ArrayList<>();
		for (Log log : logRepository.findAll()) {
			LogDTO logDTO = new LogDTO();			
			BeanUtils.copyProperties(log, logDTO);
			lista.add(logDTO);			
		}		
		return lista;
	}
	

	
	
	
	
	
	

}
