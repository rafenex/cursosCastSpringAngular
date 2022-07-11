package br.com.castgroup.cursos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.castgroup.cursos.entities.Log;

public interface LogRepository extends JpaRepository<Log, Integer>{
	   

//	@Query("select u from Usuario u where u.login = :login and u.senha = :senha")	
//	Usuario findByLoginAndSenha(@Param("login") String login, @Param("senha") String senha);
	   
	
//	@Query("SELECT u.nome, l.usuario.idUsuario, l.id_log, l.acao, c.descricao from Log l join Usuario u on u.idUsuario = l.usuario.idUsuario"
//			+ " join Curso c on c.id_curso = l.curso.id_curso")	
//	List<Log> listarLogs();
	
	@Query("SELECT u.nome, l.usuario.idUsuario, l.id_log, l.acao, c.descricao from Log l join Usuario u on u.idUsuario = l.usuario.idUsuario"
			+ " join Curso c on c.id_curso = l.curso.id_curso")	
	List<Object> listarLogs();
		
		
		
//	@Query("select count(*) from Curso c where (:inicio <= c.inicio and :termino >= c.inicio)"
//			+ "OR"
//			+ "								   (:inicio <= c.termino and :termino >= c.termino)"
//			+ "OR"
//			+ "								   (:inicio >= c.inicio and :termino <= c.termino)")	
}