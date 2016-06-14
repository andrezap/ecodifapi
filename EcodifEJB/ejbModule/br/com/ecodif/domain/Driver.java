package br.com.ecodif.domain;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Modelo de um <em>driver</em>
 * @author Bruno Costa
 */
@Entity
@Table(name = "Driver")
public class Driver {

	/**
	 * Identificador do <em>driver</em>, gerado automaticamente quando o 
	 * <em>driver</em> é persistido em banco de dados
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	/** Nome do <em>driver</em> */
	private String name;
	
	/** Descrição do <em>driver</em> */
	private String description;
	
	/** Versão do <em>driver</em> */
	private String version;
	
	/** Localização do <em>driver</em> no sistema de arquivos */
	private String locationInDirectory;
	
	/** Data da última atualização do <em>driver</em> */
	private Date lastUpdate;
	
	/** 
	 * Lista de plataformas às quais o driver oferece suporte
	 * @see br.com.ecodif.domain.Platform
	 */
	@OneToMany
	@JoinColumn(name="driver_id")
	private List<Platform> platforms;
	
	
	/**
	 * Retorna o identificador do <em>driver</em>
	 * @return Identificador do <em>driver</em>
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Modifica o identificador do <em>driver</em>
	 * @param id Identificador para alteração
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Retorna o nome do <em>driver</em>
	 * @return Nome do <em>driver</em>
	 */
	public String getName() {
		return name;
	}
	
	/** 
	 * Modifica o nome do <em>driver</em>
	 * @param name Nome para alteração
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Retorna a descrição do <em>driver</em>
	 * @return Descrição do <em>driver</em>
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Modifica a descrição do <em>driver</em>
	 * @param description Descrição para alteração
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Retorna a versão do <em>driver</em>
	 * @return Versão do <em>driver</em>
	 */
	public String getVesrsion() {
		return version;
	}
	
	/**
	 * Modifica a versão do <em>driver</em>
	 * @param version Versão para alteração
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	
	/**
	 * Retorna a localização do <em>driver</em> no sistema de arquivos
	 * @return Localização do <em>driver</em> no sistema de arquivos
	 */
	public String getLocationInDirectory() {
		return locationInDirectory;
	}
	
	/**
	 * Modifica a localização do <em>driver</em> no sistema de arquivos
	 * @param locationInDirectory Localização para alteração
	 */
	public void setLocationInDirectory(String locationInDirectory) {
		this.locationInDirectory = locationInDirectory;
	}

	/**
	 * Retorna a data da última atualização do <em>driver</em>
	 * @return Data da última atualização do <em>driver</em>
	 */
	public Date getLastUpdate() {
		return lastUpdate;
	}
	
	/**
	 * Modificação da data da última atualização do <em>driver</em>
	 * @param lastUpdate Data para alteração
	 */
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	/**
	 * Retorna a lista de plataformas às quais o <em>driver</em> oferece
	 * suporte
	 * @return Lista de plataformas às quais o <em>driver</em> oferece suporte
	 */
	public List<Platform> getPlatforms() {
		if (platforms == null) {
			platforms = new ArrayList<Platform>();
		}
		return platforms;
	}
	
	/**
	 * Modifica a lista de plataformas às quais o <em>driver</em> oferece
	 * suporte
	 * @param platforms Lista de plataformas para alteração
	 */
	public void setPlatforms(List<Platform> platforms) {
		this.platforms = platforms;
	}
}
