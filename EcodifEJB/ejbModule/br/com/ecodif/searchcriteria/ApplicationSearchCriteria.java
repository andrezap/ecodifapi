package br.com.ecodif.searchcriteria;

import java.util.Date;

/**
 * Encapsula os crit�rios de busca utilizados para consulta por aplica��es
 * @author Everton Cavalcante (evertonrsc@ppgsc.ufrn.br)
 */
public class ApplicationSearchCriteria 
{
	/** Nome da aplica��o */
	private String name;
	
	/** Palavras-chave */
	private String tags;
	
	/** Nome ou login de usu�rio */
	private String user;
	
	/** Data-in�cio de cria��o de aplica��es */
	private Date startDate;
	
	/** Data-fim de cria��o de aplica��es */
	private Date endDate;
	
	/** 
	 * Flag que indica se devem ser consideradas apenas aplica��es atualizadas h� menos
	 * de um m�s
	 */
	private boolean updated;
	
	
	/** Construtor */
	public ApplicationSearchCriteria() {
		setName(null);
		setTags(null);
		setUser(null);
		setStartDate(null);
		setEndDate(null);
		setUpdated(false);		// Por padr�o, s�o consideradas todas as aplica��es
	}


	/**
	 * Retorna o nome da aplica��o utilizado como crit�rio de busca
	 * @return Nome da aplica��o utilizado como crit�rio de busca
	 */
	public String getName() {
		return name;
	}

	
	/**
	 * Modifica o nome da aplica��o utilizado como crit�rio de busca
	 * @param name Nome da aplica��o para altera��o
	 */
	public void setName(String name) {
		this.name = name;
	}

	
	/**
	 * Retorna as palavras-chave utilizadas como crit�rio de busca
	 * @return Palavras-chave utilizadas como crit�rio de busca
	 */
	public String getTags() {
		return tags;
	}

	
	/**
	 * Modifica as palavras-chave utilizadas como crit�rio de busca
	 * @param tags Palavras-chave para altera��o
	 */
	public void setTags(String tags) {
		this.tags = tags;
	}

	
	/**
	 * Retorna o nome ou login de usu�rio utilizado como crit�rio de busca
	 * @return Nome ou login de usu�rio utilizado como crit�rio de busca
	 */
	public String getUser() {
		return user;
	}

	
	/**
	 * Modifica o nome ou login de usu�rio utilizado como crit�rio de busca
	 * @param user Nome ou login de usu�rio para altera��o
	 */
	public void setUser(String user) {
		this.user = user;
	}

	
	/**
	 * Retorna a data-in�cio de cria��o utilizada como crit�rio de busca
	 * @return Data-in�cio de cria��o utilizada como crit�rio de busca
	 */
	public Date getStartDate() {
		return startDate;
	}

	
	/**
	 * Modifica a data-in�cio de cria��o utilizada como crit�rio de busca
	 * @param startDate Data-in�cio de cria��o para altera��o
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	
	/**
	 * Retorna a data-fim de cria��o utilizada como crit�rio de busca
	 * @return Data-fim de cria��o utilizada como crit�rio de busca
	 */
	public Date getEndDate() {
		return endDate;
	}

	
	/**
	 * Modifica a data-fim de cria��o utilizada como crit�rio de busca
	 * @param endDate Data-fim de cria��o para altera��o
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	
	/**
	 * Retorna o valor da flag que indica se devem ser consideradas apenas aplica��es 
	 * atualizadas h� menos de um m�s
	 * @return <code>true</code> (verdadeiro), em caso afirmativo,
	 * 		   <code>false</code> (falso), em caso negativo
	 */
	public boolean isUpdated() {
		return updated;
	}

	
	/**
	 * Modifica o valor da flag que indica se devem ser consideradas apenas aplica��es 
	 * atualizadas h� menos de um m�s
	 * @param updated Flag para altera��o
	 */
	public void setUpdated(boolean updated) {
		this.updated = updated;
	}
}
