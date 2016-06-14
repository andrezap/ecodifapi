package br.com.ecodif.dao;

import java.util.HashMap;
import javax.ejb.Stateless;
import br.com.ecodif.domain.Trigger;

/**
 * Classe de acesso a dados (DAO) da entidade de dom�nio Trigger que abstrai as opera��es sobre a respectiva 
 * tabela de aplica��es no banco de dados
 * @author Bruno Costa
 *
 */
@Stateless
public class TriggerDAO extends GenericDAO<Trigger> {

	public TriggerDAO() {
		super(Trigger.class);
	}
	
	/**
	 * Recupera um objeto do tipo Trigger segundo o identificador do objeto do tipo Environment
	 * associado
	 * @param environmentId
	 * @return objeto do tipo Trigger
	 */
	public Trigger findByEnvironmentIddb(int environmentId){
		String strQuery = "Select t From Trigger t WHERE t.environment.iddb = :environmentId";
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("environmentId", environmentId);		
		return super.findOneResult(strQuery, parameters);
	}

}
