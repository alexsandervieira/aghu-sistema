package br.gov.mec.aghu.casca.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.sql.JoinType;
import org.hibernate.type.IntegerType;

import br.gov.mec.aghu.casca.model.Aplicacao;
import br.gov.mec.aghu.casca.model.Componente;
import br.gov.mec.aghu.casca.model.Menu;
import br.gov.mec.aghu.casca.model.Modulo;
import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.casca.model.PerfisPermissoes;
import br.gov.mec.aghu.casca.model.PerfisUsuarios;
import br.gov.mec.aghu.casca.model.Permissao;
import br.gov.mec.aghu.casca.model.PermissaoModulo;
import br.gov.mec.aghu.casca.model.PermissoesComponentes;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.lucene.Fonetizador;
import br.gov.mec.aghu.core.search.Lucene;
import br.gov.mec.aghu.dominio.DominioSituacao;

public class MenuDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<Menu> {

	private static final String MODULO = "modulo.";
	private static final String PERF_USUARIO = "perfUsuario.";
	private static final String PERMISSAO = "permissao.";
	private static final String PERFIL = "perfil.";
	private static final String MENU = "menu";
	private static final String APLICACAO = "aplicacao";
	private static final String MENU_PAI = "menuPai";
	private static final long serialVersionUID = -1352779356469149305L;
	
	@Inject
    private Lucene lucene;
	private static final Log LOG = LogFactory.getLog(MenuDAO.class);

	/**
	 * Realiza uma query para a verificação da existencia de menu na hierarquia com mesmo nome ou mesma URL por aplicação.
	 * 
	 * @param nome
	 * @param aplicacao
	 * @param menuPai
	 * @param url
	 * @param id
	 * @return
	 */
	public List<Menu> validaCadastroMenu(String nome, Aplicacao aplicacao, Menu menuPai, String url, Integer id) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Menu.class);

		if (id != null) {
			criteria.add(Restrictions.ne(Menu.Fields.ID.toString(), id));
		}

		Criterion and = Restrictions.eq(Menu.Fields.NOME.toString(), nome);

		if (menuPai == null) {
			and = Restrictions.and(and,
					Restrictions.isNull(Menu.Fields.MENU_PAI.toString()));
		} else {
			and = Restrictions.and(and,
					Restrictions.eq(Menu.Fields.MENU_PAI.toString(), menuPai));
		}

		if (url != null) {
			criteria.add(Restrictions.or(and,
					Restrictions.eq(Menu.Fields.URL.toString(), url)));
		} else {
			criteria.add(and);
		}

		criteria.add(Restrictions.eq(Menu.Fields.APLICACAO.toString(),
				aplicacao));

		return executeCriteria(criteria);
	}

	public List<Menu> pesquisarMenu(String nomeMenu, String urlMenu,
			Integer idAplicacao, Integer idMeuPai, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {

		final DetachedCriteria criteria = criteriaCode(nomeMenu, urlMenu, idAplicacao, idMeuPai);
		if(orderProperty != null){
			if(asc){
				criteria.addOrder(Order.asc(orderProperty));
			} else {
				criteria.addOrder(Order.desc(orderProperty));
			}
		}
		return executeCriteria(criteria, firstResult, maxResult, null);
	}

	public Long pesquisarMenucrudCount(String nomeMenu, String urlMenu,
			Integer idAplicacao, Integer idMeuPai) {
		return executeCriteriaCount(criteriaCode(nomeMenu, urlMenu, idAplicacao, idMeuPai));
	}

	private DetachedCriteria criteriaCode(String nomeMenu, String urlMenu,
			Integer idAplicacao, Integer idMeuPai) {

		DetachedCriteria criteria = DetachedCriteria.forClass(Menu.class);
		criteria.createAlias(Menu.Fields.APLICACAO.toString(), APLICACAO);
		criteria.createAlias(Menu.Fields.MENU_PAI.toString(), MENU_PAI, JoinType.LEFT_OUTER_JOIN);

		if (StringUtils.isNotBlank(nomeMenu)) {
			criteria.add(Restrictions.ilike(Menu.Fields.NOME.toString(),
					nomeMenu, MatchMode.ANYWHERE));
		}

		if (StringUtils.isNotBlank(urlMenu)) {
			criteria.add(Restrictions.ilike(Menu.Fields.URL.toString(),
					urlMenu, MatchMode.ANYWHERE));
		}

		if (idAplicacao != null) {
			criteria.add(Restrictions.eq("aplicacao." + Aplicacao.Fields.ID.toString(),
							idAplicacao));
		}
		
		if (idMeuPai != null) {
			criteria.add(Restrictions.eq("menuPai."+Menu.Fields.ID.toString(), idMeuPai));
		}

		return criteria;
	}


	public List<Menu> obterMenuPorURL(String url) {
		if (url == null) {
			return new ArrayList<Menu>();
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(Menu.class);
		criteria.add(Restrictions.eq(Menu.Fields.URL.toString(), url.trim()));
		
		return executeCriteria(criteria);
	}


	public List<Menu> pesquisar(Integer id) {
		DetachedCriteria _criteria = DetachedCriteria.forClass(Menu.class);
		
		if (id != null) {
			_criteria.add(Restrictions.eq(Menu.Fields.ID.toString(), id));
		}
		
		return executeCriteria(_criteria);
	}

	public List<Menu> pesquisar(String nome) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Menu.class);
		if (StringUtils.isNotBlank(nome)) {
			criteria.add(Restrictions.ilike(Menu.Fields.NOME.toString(),
					nome, MatchMode.ANYWHERE));
		}
		criteria.addOrder(Order.asc(Menu.Fields.NOME.toString()));
		return executeCriteria(criteria);
	}
	
	public List<Menu> recuperarSubMenusValidos(Boolean force) {		
		DetachedCriteria menus = DetachedCriteria.forClass(Menu.class, MENU);
		menus.add(Restrictions.eq(Menu.Fields.ATIVO.toString(),true));
		menus.add(Restrictions.isNull(Menu.Fields.URL.toString()));
		menus.add(Restrictions.isNotEmpty(Menu.Fields.MENUS.toString()));
		menus.addOrder(Order.asc(Menu.Fields.ORDEM.toString()));	
		if (!force) {
			return executeCriteria(menus,"regionMenu");
		} else {
			this.evictQueryRegion("regionMenu");
			return executeCriteria(menus);
		}
	}
	
	public List<Menu> recuperarMenus() {		
		DetachedCriteria menus = DetachedCriteria.forClass(Menu.class, MENU);
		menus.createAlias(Menu.Fields.MENU_PAI.toString(), MENU_PAI, JoinType.LEFT_OUTER_JOIN);
		menus.createAlias(Menu.Fields.APLICACAO.toString(), APLICACAO);
		menus.add(Restrictions.eq(Menu.Fields.ATIVO.toString(),true));
		menus.add(Restrictions.isNotNull(Menu.Fields.URL.toString()));

		menus.addOrder(Order.asc("menuPai." + Menu.Fields.ORDEM.toString()));
		menus.addOrder(Order.asc(Menu.Fields.ORDEM.toString()));
		
		return executeCriteria(menus);
	}	

	public List<Integer> recuperarMenusValidos(String loginUser) {
		return recuperarMenusValidos(loginUser, new HashSet<String>());
	}
	
	public List<Integer> recuperarMenusValidos(String loginUser, Set<String> conjuntoModulosAtivos) {		
		DetachedCriteria permissoes = DetachedCriteria.forClass(Componente.class,"comp");
		permissoes.setProjection(Projections.property("id"));
		permissoes.createAlias(Componente.Fields.PERMISSOES_COMPONENTES.toString(), "permComp");
		permissoes.createAlias("permComp." + PermissoesComponentes.Fields.PERMISSAO.toString(), "permissao"); 
		permissoes.createAlias(PERMISSAO + Permissao.Fields.PERFIS_PERMISSOES.toString(), "perfPerm");		
		permissoes.createAlias(PERMISSAO + Permissao.Fields.PERMISSOES_MODULOS, "perfModulo");
		permissoes.createAlias("perfModulo." + PermissaoModulo.Fields.MODULO, "modulo");
		permissoes.createAlias("perfPerm." + PerfisPermissoes.Fields.PERFIL.toString(), "perfil");
		permissoes.createAlias(PERFIL + Perfil.Fields.PERFIS_USUARIOS.toString(), "perfUsuario");
		permissoes.createAlias(PERF_USUARIO + PerfisUsuarios.Fields.USUARIO.toString(), "usuario");
		
		permissoes.add(Restrictions.or(
						Restrictions.isNull(PERF_USUARIO + PerfisUsuarios.Fields.DATA_EXPIRACAO.toString()),
						Restrictions.gt(PERF_USUARIO + PerfisUsuarios.Fields.DATA_EXPIRACAO.toString(), new Date())));
		permissoes.add(Restrictions.eq(PERFIL + Perfil.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		if (conjuntoModulosAtivos != null && !conjuntoModulosAtivos.isEmpty()) {
			permissoes.add(Restrictions.in(MODULO + Modulo.Fields.NOME.toString(), conjuntoModulosAtivos));			
		} else {
			permissoes.add(Restrictions.eq(MODULO + Modulo.Fields.ATIVO.toString(), Boolean.TRUE));
		}
		
		permissoes.add(Restrictions.eq("usuario." + Usuario.Fields.LOGIN.toString(), loginUser).ignoreCase());
		permissoes.add(Restrictions.eqProperty("comp." + Componente.Fields.NOME.toString(), "menu."+Menu.Fields.URL.toString()));
		
		DetachedCriteria menus = DetachedCriteria.forClass(Menu.class, MENU);
		menus.createAlias(Menu.Fields.MENU_PAI.toString(), MENU_PAI, JoinType.LEFT_OUTER_JOIN);
		menus.createAlias(Menu.Fields.APLICACAO.toString(), APLICACAO);
		menus.add(Restrictions.eq(Menu.Fields.ATIVO.toString(),true));
		menus.add(Restrictions.isNotNull(Menu.Fields.URL.toString()));
		menus.setProjection(Projections.property("menu.id"));

		menus.add(Subqueries.exists(permissoes));
		menus.addOrder(Order.asc("menuPai." + Menu.Fields.ORDEM.toString()));
		menus.addOrder(Order.asc(Menu.Fields.ORDEM.toString()));
		
		return executeCriteria(menus,true);
	}
	
	public List<Menu> pesquisarMenusAtivosLucene(String strPesquisa){
		BooleanQuery totalQuery = new BooleanQuery();
		String campoAnalisado = Menu.Fields.NOME.toString();
		
		String campoFonetico = Menu.Fields.NOME_FONETICO.toString();
		
		Query luceneQueryBrazilian = null;
		Query luceneQueryKeyword = null;
		try {
			String buscaBrazilianAnalyzer = campoAnalisado+":("+strPesquisa.trim()+"*) OR "+campoAnalisado+":("+strPesquisa.trim()+")";
			luceneQueryBrazilian = lucene.createQuery(buscaBrazilianAnalyzer, new BrazilianAnalyzer());
			String buscaKeywordAnalyzer = campoFonetico+":("+Fonetizador.fonetizar(strPesquisa.trim()).toLowerCase()+")";
			luceneQueryKeyword = lucene.createQuery(buscaKeywordAnalyzer, new KeywordAnalyzer());
			
			TermQuery situacaoQuery = new TermQuery(new Term(Menu.Fields.ATIVO.toString(), "N" ));
			
			
			totalQuery.add(luceneQueryBrazilian, Occur.SHOULD);
			totalQuery.add(luceneQueryKeyword, Occur.SHOULD);
			totalQuery.add(situacaoQuery, Occur.MUST_NOT);
		} catch (ParseException e) {
			LOG.error(e.getMessage(),e);
		}
		
		FullTextQuery query = createFullTextQuery(totalQuery, Menu.class);
		
		return query.setMaxResults(100).getResultList();
	}
	
	public List<Menu> recuperarMenusValidos(String loginUser, List<Integer> listaMenus, Set<String> conjuntoModulosAtivos) {
		DetachedCriteria permissoes = DetachedCriteria.forClass(Componente.class,"comp");
		permissoes.setProjection(Projections.property("id"));
		permissoes.createAlias(Componente.Fields.PERMISSOES_COMPONENTES.toString(), "permComp");
		permissoes.createAlias("permComp." + PermissoesComponentes.Fields.PERMISSAO.toString(), "permissao"); 
		permissoes.createAlias(PERMISSAO + Permissao.Fields.PERFIS_PERMISSOES.toString(), "perfPerm");		
		permissoes.createAlias(PERMISSAO + Permissao.Fields.PERMISSOES_MODULOS, "perfModulo");
		permissoes.createAlias("perfModulo." + PermissaoModulo.Fields.MODULO, "modulo");
		permissoes.createAlias("perfPerm." + PerfisPermissoes.Fields.PERFIL.toString(), "perfil");
		permissoes.createAlias(PERFIL + Perfil.Fields.PERFIS_USUARIOS.toString(), "perfUsuario");
		permissoes.createAlias(PERF_USUARIO + PerfisUsuarios.Fields.USUARIO.toString(), "usuario");
		
		permissoes.add(Restrictions.or(
						Restrictions.isNull(PERF_USUARIO + PerfisUsuarios.Fields.DATA_EXPIRACAO.toString()),
						Restrictions.gt(PERF_USUARIO + PerfisUsuarios.Fields.DATA_EXPIRACAO.toString(), new Date())));
		permissoes.add(Restrictions.eq(PERFIL + Perfil.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		if (conjuntoModulosAtivos != null && !conjuntoModulosAtivos.isEmpty()) {
			permissoes.add(Restrictions.in(MODULO + Modulo.Fields.NOME.toString(), conjuntoModulosAtivos));			
		} else {
			permissoes.add(Restrictions.eq(MODULO + Modulo.Fields.ATIVO.toString(), Boolean.TRUE));
		}
		
		permissoes.add(Restrictions.eq("usuario." + Usuario.Fields.LOGIN.toString(), loginUser).ignoreCase());
		permissoes.add(Restrictions.eqProperty("comp." + Componente.Fields.NOME.toString(), "menu."+Menu.Fields.URL.toString()));
		
		DetachedCriteria menus = DetachedCriteria.forClass(Menu.class, MENU);
		menus.createAlias(Menu.Fields.MENU_PAI.toString(), MENU_PAI);
		menus.createAlias(Menu.Fields.APLICACAO.toString(), APLICACAO);
		menus.createAlias(Menu.Fields.PALAVRAS_CHAVE.toString(), "PCM", JoinType.LEFT_OUTER_JOIN);
		menus.add(Restrictions.eq(Menu.Fields.ATIVO.toString(),true));
		menus.add(Restrictions.isNotNull(Menu.Fields.URL.toString()));
		menus.add(Restrictions.in(Menu.Fields.ID.toString(), listaMenus));
		
		menus.add(Subqueries.exists(permissoes));
		menus.addOrder(Order.asc(Menu.Fields.MENU_PAI.toString()));		
		menus.addOrder(Order.asc(Menu.Fields.ORDEM.toString()));
		menus.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		return executeCriteria(menus,true);
	}
	

	public List<Menu> pesquisarMenusTabelasSistema(String parametro) {
		if (StringUtils.isNotBlank(parametro)
				&& CoreUtil.isNumeroInteger(parametro)) {
			DetachedCriteria criteria = DetachedCriteria.forClass(Menu.class);

			criteria.add(Restrictions.eq(Menu.Fields.ATIVO.toString(),true));

			criteria.add(Restrictions.eq(Menu.Fields.ID.toString(),
					Integer.valueOf(parametro)));

			criteria.add(Restrictions.isNotNull(Menu.Fields.URL.toString()));
			
			//criteria.add(Restrictions.ne(Menu.Fields.URL.toString(), ""));
			adicionarRestricaoUrlNaoVazia(criteria);
			List<Menu> menus = executeCriteria(criteria);

			if (menus != null && !menus.isEmpty()) {
				return menus;
			}
		}

		DetachedCriteria criteria = obterPesquisaMenusTabelasSistemaCriteria(parametro);
		return executeCriteria(criteria, 0, 100, Menu.Fields.NOME.toString(),
				true);
	}

	public Long pesquisarCountMenusTabelasSistema(String parametro) {
		if (StringUtils.isNotBlank(parametro)
				&& CoreUtil.isNumeroInteger(parametro)) {
			DetachedCriteria criteria = DetachedCriteria.forClass(Menu.class);

			criteria.add(Restrictions.eq(Menu.Fields.ID.toString(),
					Integer.valueOf(parametro)));

			criteria.add(Restrictions.eq(Menu.Fields.ATIVO.toString(),
					true));

			criteria.add(Restrictions.isNotNull(Menu.Fields.URL.toString()));
			
			//criteria.add(Restrictions.ne(Menu.Fields.URL.toString(), ""));
			adicionarRestricaoUrlNaoVazia(criteria);
			Long menuCount = executeCriteriaCount(criteria);

			if (menuCount != null && menuCount != 0) {
				return menuCount;
			}
		}

		DetachedCriteria criteria = obterPesquisaMenusTabelasSistemaCriteria(parametro);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria obterPesquisaMenusTabelasSistemaCriteria(
			String parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Menu.class);

		criteria.add(Restrictions.eq(Menu.Fields.ATIVO.toString(), true));

		criteria.add(Restrictions.isNotNull(Menu.Fields.URL.toString()));
		
		//criteria.add(Restrictions.ne(Menu.Fields.URL.toString(), ""));
		adicionarRestricaoUrlNaoVazia(criteria);
		if (StringUtils.isNotBlank(parametro)) {
			criteria.add(Restrictions.ilike(Menu.Fields.NOME.toString(), parametro, MatchMode.ANYWHERE));
		}

		return criteria;
	}
	
	private void adicionarRestricaoUrlNaoVazia(DetachedCriteria criteria) {
		// #42221
		// length({alias}.URL) > ?
		StringBuilder sql = new StringBuilder(20);
		sql.append("length({alias}.");
		sql.append(Menu.Fields.URL.toString());
		sql.append(") > ?");

		criteria.add(Restrictions.sqlRestriction(sql.toString(), 0, IntegerType.INSTANCE));

	}

}