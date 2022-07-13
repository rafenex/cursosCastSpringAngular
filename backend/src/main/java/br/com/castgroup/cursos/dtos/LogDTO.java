package br.com.castgroup.cursos.dtos;

import java.time.LocalDate;

public class LogDTO {
	private LocalDate inclusao;
	private LocalDate ultimaAtualizacao;
	private String nome;
	private String descricao;
	private Integer id_log;
	private Integer id_curso;
	private String acao;


	

	public LogDTO(LocalDate inclusao, LocalDate ultimaAtualizacao, String nome, String descricao, Integer id_log,
			Integer id_curso, String acao) {
		super();
		this.inclusao = inclusao;
		this.ultimaAtualizacao = ultimaAtualizacao;
		this.nome = nome;
		this.descricao = descricao;
		this.id_log = id_log;
		this.id_curso = id_curso;
		this.acao = acao;
	}



	public LogDTO() {

	}
	
	
	
	
	public Integer getId_curso() {
		return id_curso;
	}



	public void setId_curso(Integer id_curso) {
		this.id_curso = id_curso;
	}



	public LocalDate getInclusao() {
		return inclusao;
	}



	public void setInclusao(LocalDate inclusao) {
		this.inclusao = inclusao;
	}



	public LocalDate getUltimaAtualizacao() {
		return ultimaAtualizacao;
	}



	public void setUltimaAtualizacao(LocalDate ultimaAtualizacao) {
		this.ultimaAtualizacao = ultimaAtualizacao;
	}



	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getId_log() {
		return id_log;
	}

	public void setId_log(Integer id_log) {
		this.id_log = id_log;
	}




	public String getAcao() {
		return acao;
	}

	public void setAcao(String acao) {
		this.acao = acao;
	}


	
	
	
	

}
