package br.com.castgroup.cursos.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Service;

import br.com.castgroup.cursos.entities.Categoria;
import br.com.castgroup.cursos.entities.Curso;
import br.com.castgroup.cursos.entities.Log;
import br.com.castgroup.cursos.entities.Usuario;
import br.com.castgroup.cursos.repository.CategoriaRepository;
import br.com.castgroup.cursos.repository.CursoRepository;
import br.com.castgroup.cursos.repository.LogRepository;
import br.com.castgroup.cursos.repository.UsuarioRepository;

@Service
public class CursoService {

	@Autowired
	UsuarioRepository usuarioRepository;

	@Autowired
	LogRepository logRepository;

	@Autowired
	CursoRepository cursoRepository;

	@Autowired
	CategoriaRepository categoriaRepository;

	@PersistenceContext
	EntityManager em;

	Usuario usuarioLogado = new Usuario();

	public void usuarioLogado(Usuario usuario) {
		usuarioLogado = usuario;
	}

	@Transactional
	public void cadastrarCurso(Curso curso) {
		validaCampos(curso);
		existeDescricao(curso);
		validaData(curso);		
		existeCategoria(curso);

		finalizado(curso);
		@SuppressWarnings("deprecation")
		Log log = new Log(null, LocalDate.now(), LocalDate.now(), curso, "Cadastrou Curso",
				usuarioRepository.getOne(usuarioLogado.getIdUsuario()));
		curso.setInclusao(LocalDate.now());
		logRepository.save(log);
		cursoRepository.save(curso);
	}

	@Transactional
	public void atualizarCurso(Curso request, Integer id_curso) {
		finalizado(request);
		validaCampos(request);
		existeDescricaoUpdate(request);
		validaDataUpdate(request);
		
		existeCategoria(request);
		Optional<Curso> item = cursoRepository.findById(id_curso);
		if (item.isEmpty()) {
			throw new RuntimeException("Curso não encontrado");
		} else {
			Curso curso = item.get();
			if (curso.getFinalizado()) {
				throw new RuntimeException("Curso finalizado não pode ser editado");
			}		
			request.setInclusao(curso.getInclusao());
			BeanUtils.copyProperties(request, curso);	

			@SuppressWarnings("deprecation")
			Log log = new Log(null, item.get().getInclusao(), LocalDate.now(), curso, "Atualizou Curso",
					usuarioRepository.getOne(usuarioLogado.getIdUsuario()));
			logRepository.save(log);
			cursoRepository.save(curso);
		}
	}

	@Transactional
	public void deletarCursoPorId(Integer id_curso) {
		Optional<Curso> item = cursoRepository.findById(id_curso);
		if (item.isEmpty()) {
			throw new RuntimeException("Curso não encontrado");
		} else {
			Curso curso = item.get();
			if (curso.getFinalizado()) {
				throw new RuntimeException("Curso finalizado não pode ser excluido");
			}
			@SuppressWarnings("deprecation")
			Log log = new Log(null, item.get().getInclusao(), LocalDate.now(), curso, "Deletou Curso",
					usuarioRepository.getOne(usuarioLogado.getIdUsuario()));
			logRepository.save(log);
			cursoRepository.delete(curso);
		}
	}

	public List<Curso> acharTodos() {
		checarTerminoCurso();
		return cursoRepository.findAll();
	}

	public List<Curso> listarPorData(LocalDate inicio, LocalDate termino) {
		return cursoRepository.cursosPorData(inicio, termino);
	}

	public Optional<Curso> acharPorId(Integer id_curso) {
		Optional<Curso> item = cursoRepository.findById(id_curso);
		if (item.isEmpty()) {
			throw new RuntimeException("Curso não encontrado");
		}
		return item;
	}

	public List<Curso> acharPorDescricao(String descricao) {
		List<Curso> listaDeCursos = cursoRepository.findByDescricao(descricao);
		if (listaDeCursos.isEmpty()) {
			throw new RuntimeException("Não foi encotrado nenhum curso com esse nome");
		}
		return listaDeCursos;
	}

	public Page<Curso> filtrar(String descricao, LocalDate inicio, LocalDate termino, Pageable pagina) {
		CriteriaBuilder cb = em.getCriteriaBuilder(); // constroi CB
		CriteriaQuery<Curso> cq = cb.createQuery(Curso.class); // QUERY CURSO
		Root<Curso> cursoRoot = cq.from(Curso.class); // SELECT * FROM CURSO
		List<Predicate> predicates = new ArrayList<>(); // pode ser vazia ou nao
		if (descricao != null) {
			Predicate descricaoPredicate = cb.equal(cursoRoot.get("descricao"), descricao);
			predicates.add(descricaoPredicate);
		}
		if (inicio != null) {
			Predicate inicioPredicate = cb.greaterThanOrEqualTo(cursoRoot.get("inicio"), inicio);
			predicates.add(inicioPredicate);
		}
		if (termino != null) {
			Predicate descricaoPredicate = cb.lessThanOrEqualTo(cursoRoot.get("termino"), termino);
			predicates.add(descricaoPredicate);
		}
		Predicate[] predicateArr = new Predicate[predicates.size()];
		predicates.toArray(predicateArr);
		cq.where(predicateArr);
		// cq.orderBy(cb.desc(cursoRoot.get("inclusao")));
		cq.orderBy(QueryUtils.toOrders(pagina.getSort(), cursoRoot, cb));

		// Esta consulta busca o Curso conforme o Limite da Página
		List<Curso> result = em.createQuery(cq).setFirstResult((int) pagina.getOffset())
				.setMaxResults(pagina.getPageSize()).getResultList();

		// Cria consulta de contagem
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<Curso> cursosRootCount = countQuery.from(Curso.class);
		countQuery.select(cb.count(cursosRootCount))
				.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));

		// Busca a contagem de todos os Cursos de acordo com os critérios fornecidos
		Long count = em.createQuery(countQuery).getSingleResult();

		Page<Curso> result1 = new PageImpl<>(result, pagina, count);
		return result1;

	}

	// Validações

	private void validaCampos(Curso request) {
		if ((request.getDescricao().isEmpty())) {
			throw new RuntimeException("O campo descrição é obrigatório");
		}
		if ((request.getInicio() == null)) {
			throw new RuntimeException("O campo início é obrigatório");
		}
		if ((request.getTermino() == null)) {
			throw new RuntimeException("O campo término é obrigatório");
		}
		if ((request.getCategoria().getId_categoria() == null)) {
			throw new RuntimeException("O campo categoria é obrigatório");
		}

	}

	private void validaData(Curso request) {

		if (request.getInicio().isBefore(LocalDate.now())) {
			throw new RuntimeException("Data de inicio menor que a data atual");
		}

		if (request.getInicio().isAfter(request.getTermino())) {
			throw new RuntimeException("Data de início após data de término");
		}
		if (cursoRepository.contador(request.getInicio(), request.getTermino()) > 0) {
			throw new RuntimeException("Existe(m) curso(s) planejados(s) dentro do período informado.");
		}
	}

	private void validaDataUpdate(Curso request) {
		List<Curso> cursosPorData = cursoRepository.cursosPorData(request.getInicio(), request.getTermino());
		if (cursoRepository.contador(request.getInicio(), request.getTermino()) == 1) {
			Boolean valido = false;
			for (Curso curso : cursosPorData) {
				if (curso.getId_curso().equals(request.getId_curso())) {
					valido = true;
				}
			}
			if (!valido)
				throw new RuntimeException("Existe(m) curso(s) planejados(s) dentro do período informado.");
		}
		if (request.getInicio().isAfter(request.getTermino())) {
			throw new RuntimeException("Data de início após data de término");
		}
		if (cursoRepository.contador(request.getInicio(), request.getTermino()) > 1) {
			throw new RuntimeException("Existe(m) curso(s) planejados(s) dentro do período informado.");
		}
	}

	private void existeCategoria(Curso request) {
		Optional<Categoria> categoria = categoriaRepository.findById(request.getCategoria().getId_categoria());
		if (categoria.isEmpty()) {
			throw new RuntimeException("Não existe a categoria informada");
		}

	}

	private void existeDescricaoUpdate(Curso request) {
		List<Curso> findByDescricao = cursoRepository.findByDescricao(request.getDescricao());
		List<Curso> naoFinalizado = cursoRepository.findByDescricaoAndFinalizado(request.getDescricao(), false);
		System.out.println(findByDescricao.size());
		if (findByDescricao.size() >= 1) {
			Boolean valido = false;
			for (Curso curso : findByDescricao) {
				if (curso.getId_curso().equals(request.getId_curso())) {
					valido = true;
				}
				if (naoFinalizado.size() < 1) {
					valido = true;
				}
			}
			if (!valido)
				throw new RuntimeException("Curso já cadastrado.");
		}
	}

	private void existeDescricao(Curso curso) {
		List<Curso> findByDescricao = cursoRepository.findByDescricao(curso.getDescricao());
		List<Curso> naoFinalizado = cursoRepository.findByDescricaoAndFinalizado(curso.getDescricao(), false);

		if ((findByDescricao.size() > 0) && (naoFinalizado.size() > 0)) {
			throw new RuntimeException("Curso já cadastrado.");
		}
	}

	public void checarTerminoCurso() {
		List<Curso> lista = cursoRepository.findAll();
		for (Curso curso : lista) {
			if (curso.getTermino().isBefore(LocalDate.now())) {
				curso.setFinalizado(true);
				cursoRepository.save(curso);
			} else {
				curso.setFinalizado(false);
			}
		}
	}

	public void finalizado(Curso curso) {
		if (curso.getTermino().isBefore(LocalDate.now())) {
			curso.setFinalizado(true);
		} else {
			curso.setFinalizado(false);
		}
	}

}
