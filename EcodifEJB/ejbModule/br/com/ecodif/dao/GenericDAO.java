package br.com.ecodif.dao;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;

/**
 * Classe genérica que implementa métodos padrão para acesso
 * a dados.
 * @author Bruno Costa
 *
 * @param <T> Classe de domínio
 */
public abstract class GenericDAO<T> {
	private final static String UNIT_NAME = "EcodifPU";

	@PersistenceContext(unitName = UNIT_NAME)
	protected EntityManager em;

	private Class<T> entityClass;

	public GenericDAO(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	/**
	 * Persisitir uma entidade de domínio no banco de dados
	 * 
	 * @param entity
	 */
	public void save(T entity) {
		em.persist(entity);
	}

	/**
	 * Remover uma entidade no banco de dados
	 * 
	 * @param entity
	 */
	public void delete(T entity) {
		T entityToBeRemoved = em.merge(entity);

		em.remove(entityToBeRemoved);
	}

	/**
	 * Atualizar uma entidade no banco de dados
	 * 
	 * @param entity
	 * @return 'true' if it was successful, 'false' if wasn't
	 */
	public T update(T entity) {
		return em.merge(entity);
	}

	/**
	 * Recuperar uma entidade no banco de dados
	 * 
	 * @param entityID
	 * @return entity
	 */
	public T find(int entityID) {
		return em.find(entityClass, entityID);

	}

	/**
	 * Recuperar todos os registros da entidade no banco de dados
	 * 
	 * @return List of entities
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<T> findAll() {
		CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
		cq.select(cq.from(entityClass));
		return em.createQuery(cq).getResultList();
	}

	/**
	 * Recuperar uma entidade com base em uma query de pesquisa
	 * 
	 * @param namedQuery
	 *            A namedQuery to execute in Database
	 * @param parameters
	 *            Parameters for query
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T findOneResult(String strQuery, Map<String, Object> parameters) {
		T result = null;

		try {
			Query query = em.createQuery(strQuery);

			// Method that will populate parameters if they are passed not null
			// and empty
			if (parameters != null && !parameters.isEmpty()) {
				populateQueryParameters(query, parameters);
			}

			result = (T) query.getSingleResult();

		} catch (NoResultException e) {
			System.out.println("No entity found for query: " + strQuery);
		}

		catch (Exception e) {
			System.out.println("Error while running query: " + e.getMessage());
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Localizar muitas entidades com uma query de pesquisa
	 * @param strQuery
	 * @param parameters
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<T> findManyResults(String strQuery,
			Map<String, Object> parameters) {
		List<T> result = null;

		try {
			Query query = em.createQuery(strQuery);

			// Method that will populate parameters if they are passed not null
			// and empty
			if (parameters != null && !parameters.isEmpty()) {
				populateQueryParameters(query, parameters);
			}

			result = query.getResultList();

		} catch (NoResultException e) {
			System.out.println("No entity found for query: " + strQuery);
		} catch (Exception e) {
			System.out.println("Error while running query: " + e.getMessage());
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Popular parâmetros em uma query de pesquisa.
	 * 
	 * @param query
	 * @param parameters
	 */
	public void populateQueryParameters(Query query,
			Map<String, Object> parameters) {

		for (Entry<String, Object> entry : parameters.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
	}
}
