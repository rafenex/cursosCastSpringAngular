package br.com.castgroup.cursos.dtos;


public class LogDTO {
	private String nome;
	private String descricao;
	private Integer id_log;
	private Integer id_curso;
	private String acao;
	private Integer idUsuario;

	
	public LogDTO() {

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

	public Integer getId_curso() {
		return id_curso;
	}

	public void setId_curso(Integer id_curso) {
		this.id_curso = id_curso;
	}

	public String getAcao() {
		return acao;
	}

	public void setAcao(String acao) {
		this.acao = acao;
	}

	public Integer getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}
	
	
	
	

}
