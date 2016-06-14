package br.com.ecodif.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * Modelo de um conjunto de <em>datapoints</em> referentes a um dado 
 * <em>datastream</em>, de modo que um <em>datapoint</em> � um �nico valor de 
 * um <em>datastream</em> em um dado instante de tempo.<br/>
 * Classe gerada com base no XML Schema da linguagem EEML: 
 * {@link http://eeml.org/xsd/0.5.1/0.5.1.xsd}
 * 
 * @see {@link http://eeml.org/xml/0.5.1/complete.xml}
 * 
 * @author Bruno Costa
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "Datapoints")
public class Datapoints {

	/**
	 * Identificador gerado automaticamente quando o registro � persistido em 
	 * banco de dados
	 */
	@Id
	@XmlTransient
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int iddb;

	/**
	 * Lista de valores de <em>datapoints</em>
	 * @see br.com.ecodif.domain.Value
	 */
	@OneToMany(cascade=CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinColumn(name="datapoints_iddb")
	private List<Value> value;
	
	
	/**
	 * Retorna o identificador de registro em banco de dados
	 * @return Identificador de registro em banco de dados
	 */
	@JsonIgnore
	public int getIddb() {
		return iddb;
	}

	/**
	 * Modifica o identificador de registro em banco de dados
	 * @param iddb Identificador para altera��o
	 */
	public void setIddb(int iddb) {
		this.iddb = iddb;
	}

	/**
	 * Retorna a lista de valores de <em>datapoints</em>
	 * @return Lista de valores de <em>datapoints</em>
	 */
	public List<Value> getValue() {
		if (value == null) {
            value = new ArrayList<Value>();
        }
        return this.value;
	}

	/**
	 * Modifica a lista de valores de <em>datapoints</em>
	 * @param value Lista de valores de <em>datapoints</em> para altera��o
	 */
	public void setValue(List<Value> value) {
		this.value = value;
	}
}
