package br.com.ecodif.dao;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import br.com.ecodif.domain.Application;
import br.com.ecodif.searchcriteria.ApplicationSearchCriteria;

/**
 * Classe de acesso a dados (DAO) que abstrai as operações sobre a respectiva 
 * tabela de aplicações no banco de dados
 * @author Everton Cavalcante
 */
@Stateless
public class ApplicationDAO extends GenericDAO<Application> {

	/** Construtor */
	public ApplicationDAO() {
		super(Application.class);
	}
	
	
	/**
	 * Realiza uma consulta por aplicações com base no nome
	 * @param name Nome da aplicação objeto da consulta
	 * @return Aplicação com o nome especificado como parâmetro ou 
	 * 			<code>null</code> (objeto nulo) caso a aplicação não tenha sido 
	 * 			encontrada
	 */
	public Application findApplicationByName(String name) {
		Session session = (Session) em.getDelegate();
		Criteria crobject = session.createCriteria(Application.class);
		crobject.add(Restrictions.eq("name", name));
		return (Application) crobject.uniqueResult();
	}

	
	/**
	 * Retorna uma lista com as aplicações pertencentes a um determinado usuário, 
	 * a partir do identificador do mesmo
	 * @param id Identificador do usuário
	 * @return Lista de aplicações pertencentes ao usuário especificado
	 */
	public List<Application> findApplicationsByUserId(int id) {
		String strQuery = "Select a From Application a left join fetch a.user u WHERE u.id = :id";
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", id);		
		return super.findManyResults(strQuery, parameters);
	}
	
	
	/**
	 * Retorna uma lista com as aplicações caracterizadas pela flag de 
	 * visualização
	 * @param isPrivate Flag de visualização, com valor <code>true</code> para 
	 * 						listar todas as aplicações públicas ou 
	 * 						<code>false</code> para listar todas as aplicações 
	 * 						privadas
	 * @return Lista de aplicações caracterizadas pela flag de visualização
	 */
	public List<Application> findPublicApplications(boolean isPrivate) {
		String strQuery = "Select a from Application a WHERE a._private = :isPrivate";
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("isPrivate", isPrivate);
		return super.findManyResults(strQuery, parameters);
	}


	/**
	 * Realiza uma consulta por aplicações com base em critérios de busca, 
	 * representados por um objeto da classe <code>ApplicationSearchCriteria</code>
	 * @see br.com.ecodif.searchcriteria.ApplicationSearchCriteria
	 * 
	 * @param criteria Objeto da classe <code>ApplicationSearchCriteria</code> 
	 * 			que agrega os critérios de busca a serem considerados na consulta
	 * @return Conjunto de aplicações que atendem aos critérios de busca 
	 * 			especificados
	 */
	@SuppressWarnings({ "unchecked", "static-access" })
	public Set<Application> findApplicationsByCriteria(
			ApplicationSearchCriteria criteria) {
		Session session = (Session) em.getDelegate();
		Criteria crobject = session.createCriteria(Application.class);
		
		// Critério de busca: nome da aplicação
		if (criteria.getName() != null) {
			crobject.add(Restrictions.like("name", "%" + criteria.getName() + "%")
					.ignoreCase());
		}
		
		// Critério de busca: palavras-chave
		if (criteria.getTags() != null) {
			crobject.add(Restrictions.like("tags", "%" + criteria.getTags() + "%")
					.ignoreCase());
		}
		
		// Critério de busca: nome ou login de usuário
		if (criteria.getUser() != null) {
			Criteria cruser = crobject.createCriteria("user", "u");
			Criterion ulogin = 
				Restrictions.like("u.login", "%" + criteria.getUser() + "%")
					.ignoreCase();
			Criterion uname = 
				Restrictions.like("u.name", "%" + criteria.getUser() + "%")
					.ignoreCase();
			cruser.add(Restrictions.or(ulogin, uname));
		}
		
		// Critério de busca: data de criação (aplicação criadas em faixa de datas) 
		if (criteria.getStartDate() != null && criteria.getEndDate() != null)
		{
			GregorianCalendar gcstart = new GregorianCalendar();
			gcstart.setTime(criteria.getStartDate());
				
			GregorianCalendar gcend = new GregorianCalendar();
			gcend.setTime(criteria.getEndDate());
				
			crobject.add(Restrictions.between("creationDate", gcstart, gcend));
		}
		
		/*
		 * Critério de busca: data de atualização (aplicações atualizadas há 
		 * menos de um mês da data atual)
		 */
		if (criteria.isUpdated()) {
			GregorianCalendar gcNow = (GregorianCalendar) GregorianCalendar
					.getInstance();
			GregorianCalendar gcLastUpdate = (GregorianCalendar) GregorianCalendar
					.getInstance();
			gcLastUpdate.add(gcLastUpdate.MONTH, -1);
			crobject.add(Restrictions.between("updateDate", gcLastUpdate, gcNow));
		}
		
		// Critério de busca: apenas por aplicações públicas
		crobject.add(Restrictions.eq("_private", false));
		
		/* 
		 * Realiza a consulta propriamente dita com base nos critérios de busca
		 * especificados 
		 */
		List<Application> applicationList = crobject.list();
		Set<Application> applicationSet = new HashSet<Application>();
		for (Application a : applicationList) {
			applicationSet.add(a);
		}
		
		return applicationSet;
	}
}
