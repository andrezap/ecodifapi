package br.com.ecodif.framework;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import br.com.ecodif.domain.CurrentValue;
import br.com.ecodif.domain.Data;
import br.com.ecodif.domain.Datapoints;
import br.com.ecodif.domain.Eeml;
import br.com.ecodif.domain.Environment;
import br.com.ecodif.domain.Location;
import br.com.ecodif.domain.Sensor;
import br.com.ecodif.domain.Trigger;
import br.com.ecodif.domain.Unit;
import br.com.ecodif.domain.UserGroup;
import br.com.ecodif.domain.Value;
import br.com.ecodif.eeml_contract.Eeml_Contract;
import br.com.ecodif.searchcriteria.EnvironmentSearchCriteria;

/**
 * Classe respons�vel por toda manipula��o de arquivos EEML
 * @author Bruno Costa
 *
 */
@Stateless
public class EemlManager {

	@PersistenceContext
	private EntityManager manager;

	/**
	 * L� um arquivo EEML xml e retorna um objeto do tipo Eeml_Contract 
	 * @param eemlLocation Endere�o de localiza��o do arquivo EEML
	 * @return objeto do tipo Eeml_Contract
	 * @throws JAXBException
	 * @throws IOException
	 */
	public Eeml_Contract read_eeml(String eemlLocation) throws JAXBException,
			IOException {
		JAXBContext ctx = JAXBContext.newInstance(Eeml_Contract.class);
		Unmarshaller unm = ctx.createUnmarshaller();
		Eeml_Contract objEeml = (Eeml_Contract) unm.unmarshal(new File(
				eemlLocation));
		return objEeml;
	}
	

	public void saveSensor(Sensor sensor) {
		manager.persist(sensor);
	}
	
	/**
	 * 
	 * @param environmentIddb
	 * @return nome (String) do objeto do tipo Environment
	 */
	public String findEnvironmentNameById(int environmentIddb) {
		String jpql = "select e.title from Environment e where e.iddb = :environmentIddb";
		Query query = this.manager.createQuery(jpql);
		query.setParameter("environmentIddb",  environmentIddb);
		return (String) query.getSingleResult();
	}

	/**
	 * L� um InputStream contendo um XML eeml e retorna um objeto do tipo Eeml_Contract
	 * @param eemlInput
	 * @return objeto do tipo Eeml_Contract
	 * @throws JAXBException
	 * @throws IOException
	 */
	public Eeml_Contract read_eemlFromInputStream(InputStream eemlInput)
			throws JAXBException, IOException {
		JAXBContext ctx = JAXBContext.newInstance(Eeml_Contract.class);
		Unmarshaller unm = ctx.createUnmarshaller();
		Eeml_Contract objEeml = (Eeml_Contract) unm.unmarshal(eemlInput);

		return objEeml;
	}

	/**
	 * L� um Reader contendo um XML eeml e retorna um objeto do tipo Eeml_Contract
	 * @param eemlReader
	 * @return
	 * @throws JAXBException
	 * @throws IOException
	 */
	public Eeml_Contract read_eemlFromReader(Reader eemlReader)
			throws JAXBException, IOException {
		JAXBContext ctx = JAXBContext.newInstance(Eeml_Contract.class);
		Unmarshaller unm = ctx.createUnmarshaller();
		Eeml_Contract objEeml = (Eeml_Contract) unm.unmarshal(eemlReader);

		return objEeml;
	}

	/**
	 * F�brica de objetos Eeml
	 * 
	 * @return Eeml
	 */
	public Eeml createEeml() {
		Eeml eeml = new Eeml();
		Environment environment = new Environment(); // feed
		Location location = new Location();
		environment.setLocation(location);
		eeml.getEnvironment().add(environment);

		Data data = new Data();
		CurrentValue currentValue = new CurrentValue();
		data.setCurrentValue(currentValue);
		Unit unit = new Unit();
		data.setUnit(unit);
		Datapoints dataPoints = new Datapoints();
		Value value = new Value();
		dataPoints.getValue().add(value);
		data.getDatapoints().add(dataPoints);

		environment.getData().add(data);

		return eeml;
	}

	/**
	 * Converte objetos do tipo Eeml_Contract em objetos de dom�nio Eeml
	 * 
	 * @param objeto do tipo Eeml_Contract
	 * @return objeto do tipo Eeml
	 */
	public Eeml eemlContractToDomain(Eeml_Contract contract) {
		Eeml eemlDomain = new Eeml();
		eemlDomain.setVersion(contract.getVersion());

		/************ Environments *************/
		if (contract.getEnvironment() != null) {
			List<Environment> environments = new ArrayList<Environment>();
			for (Eeml_Contract.Environment e : contract.getEnvironment()) {
				Environment environmentDomain = new Environment();
				environmentDomain.setTitle(e.getTitle());
				environmentDomain.setFeed(e.getFeed());
				environmentDomain.setStatus(e.getStatus());
				environmentDomain.set_private(e.getPrivate());
				environmentDomain.setDescription(e.getDescription());
				environmentDomain.setIcon(e.getIcon());
				environmentDomain.setWebsite(e.getWebsite());
				environmentDomain.setEmail(e.getEmail());

				/************ Tags *************/
				if (e.getData() != null) {
					List<String> environmentTags = new ArrayList<String>();
					for (String tag : e.getTag()) {
						environmentTags.add(tag);
					}
					environmentDomain.setTag(environmentTags);
				}

				/************ Location *************/
				if (e.getLocation() != null) {
					Location location = new Location();
					location.setName(e.getLocation().getName());
					location.setLat(e.getLocation().getLat());
					location.setLon(e.getLocation().getLon());
					location.setEle(e.getLocation().getEle());
					location.setExposure(e.getLocation().getExposure());
					location.setDomain(e.getLocation().getDomain());
					location.setDisposition(e.getLocation().getDisposition());
					environmentDomain.setLocation(location);
				}

				/************ Datas *************/
				if (e.getData() != null) {
					List<Data> domainDatas = new ArrayList<Data>();
					for (Eeml_Contract.Environment.Data d : e.getData()) {
						Data data = new Data();

						/************ Tags *************/
						List<String> domainDataTags = new ArrayList<String>();
						for (String dt : d.getTag()) {
							domainDataTags.add(dt);
						}

						/************ CurrentValue *************/
						if (d.getCurrentValue() != null) {
							CurrentValue currentValue = new CurrentValue();
							currentValue.setValue(d.getCurrentValue()
									.getValue());

							if (d.getCurrentValue().getAt() != null) {
								currentValue.setAt(d.getCurrentValue().getAt()
										.toGregorianCalendar());
							}
							data.setTag(domainDataTags);
							data.setCurrentValue(currentValue);
						}

						/************ MaxValue *************/
						data.setMaxValue(d.getMaxValue());

						/************ MinValue *************/
						data.setMinValue(d.getMinValue());

						/************ DataPoints and Value *************/
						List<Value> domainValues = new ArrayList<Value>();

						if (d.getDatapoints() != null) {
							for (Eeml_Contract.Environment.Data.Datapoints.Value v : d
									.getDatapoints().getValue()) {
								Value value = new Value();
								value.setValue(v.getValue());
								value.setAt(v.getAt().toGregorianCalendar());

								domainValues.add(value);
							}
							Datapoints dataPoints = new Datapoints();
							dataPoints.setValue(domainValues);
							data.getDatapoints().add(dataPoints);

						}

						/************ Unit *************/
						if (d.getUnit() != null) {
							Unit unit = new Unit();
							unit.setValue(d.getUnit().getValue());
							unit.setSymbol(d.getUnit().getSymbol());
							unit.setType(d.getUnit().getType());
							data.setUnit(unit);
						}

						/************ Id *************/
						data.setId(d.getId());

						domainDatas.add(data);
					}

					environmentDomain.setData(domainDatas);
				}

				if (e.getUpdated() != null) {
					environmentDomain.setUpdated(e.getUpdated()
							.toGregorianCalendar());
				}
				environmentDomain.setCreator(e.getCreator());
				environmentDomain.setId(e.getId());

				environments.add(environmentDomain);
			}

			eemlDomain.setEnvironment(environments);
		}

		return eemlDomain;
	}

	/**
	 * Converte objetos do tipo Eeml em objetos de dom�nio Eeml_Contract
	 * 
	 * @param domain
	 * @return
	 * @throws DatatypeConfigurationException
	 */
	public Eeml_Contract eemlDomainToContract(Eeml domain)
			throws DatatypeConfigurationException {
		Eeml_Contract contract = new Eeml_Contract();

		/************ Environments *************/
		if (domain.getEnvironment() != null) {
			for (Environment e : domain.getEnvironment()) {
				br.com.ecodif.eeml_contract.Eeml_Contract.Environment environmentContract = new br.com.ecodif.eeml_contract.Eeml_Contract.Environment();
				environmentContract.setTitle(e.getTitle());
				environmentContract.setFeed(e.getFeed());
				environmentContract.setStatus(e.getStatus());
				environmentContract.setPrivate(e.get_private());
				environmentContract.setDescription(e.getDescription());
				environmentContract.setIcon(e.getIcon());
				environmentContract.setWebsite(e.getWebsite());
				environmentContract.setEmail(e.getEmail());

				/************ Tags *************/
				e = findEnvironmentWithDatas(e.getIddb());
				if (e.getData() != null) {
					for (String tag : e.getTag()) {
						environmentContract.getTag().add(tag);
					}

				}

				/************ Location *************/
				if (e.getLocation() != null) {
					br.com.ecodif.eeml_contract.Eeml_Contract.Environment.Location location;

					location = new br.com.ecodif.eeml_contract.Eeml_Contract.Environment.Location();
					location.setName(e.getLocation().getName());
					location.setLat(e.getLocation().getLat());
					location.setLon(e.getLocation().getLon());
					location.setEle(e.getLocation().getEle());
					location.setExposure(e.getLocation().getExposure());
					location.setDomain(e.getLocation().getDomain() == null ? "physical"
							: e.getLocation().getDomain());
					location.setDisposition(e.getLocation().getDisposition());
					environmentContract.setLocation(location);

					environmentContract.setLocation(location);
				}

				/************ Datas *************/
				if (e.getData() != null) {

					for (Data d : e.getData()) {
						br.com.ecodif.eeml_contract.Eeml_Contract.Environment.Data dataContract = new br.com.ecodif.eeml_contract.Eeml_Contract.Environment.Data();

						/************ Tags *************/
						List<String> contractDataTags = new ArrayList<String>();
						for (String dt : d.getTag()) {
							contractDataTags.add(dt);
						}

						/************ CurrentValue *************/
						if (d.getCurrentValue() != null) {
							br.com.ecodif.eeml_contract.Eeml_Contract.Environment.Data.CurrentValue currentValue = new br.com.ecodif.eeml_contract.Eeml_Contract.Environment.Data.CurrentValue();
							currentValue.setValue(d.getCurrentValue()
									.getValue());

							if (d.getCurrentValue().getAt() != null) {
								XMLGregorianCalendar xgcald = DatatypeFactory
										.newInstance().newXMLGregorianCalendar(
												d.getCurrentValue().getAt());
								currentValue.setAt(xgcald);
							}
							dataContract.setCurrentValue(currentValue);
						}

						/************ MaxValue *************/
						dataContract.setMaxValue(d.getMaxValue());

						/************ MinValue *************/
						dataContract.setMinValue(d.getMinValue());

						/************ DataPoints and Value *************/
						if (d.getDatapoints() != null) {

							br.com.ecodif.eeml_contract.Eeml_Contract.Environment.Data.Datapoints datapointContract = new br.com.ecodif.eeml_contract.Eeml_Contract.Environment.Data.Datapoints();
							for (Datapoints dataP : d.getDatapoints()) {

								for (Value v : dataP.getValue()) {

									br.com.ecodif.eeml_contract.Eeml_Contract.Environment.Data.Datapoints.Value valueContract = new br.com.ecodif.eeml_contract.Eeml_Contract.Environment.Data.Datapoints.Value();
									valueContract.setValue(v.getValue());

									if (d.getCurrentValue().getAt() != null) {
										XMLGregorianCalendar xgcal = DatatypeFactory
												.newInstance()
												.newXMLGregorianCalendar(
														v.getAt());

										valueContract.setAt(xgcal);
									}

									datapointContract.getValue().add(
											valueContract);

								}

							}

							dataContract.setDatapoints(datapointContract);
						}

						/************ Unit *************/
						if (d.getUnit() != null) {
							br.com.ecodif.eeml_contract.Eeml_Contract.Environment.Data.Unit unitContract = new br.com.ecodif.eeml_contract.Eeml_Contract.Environment.Data.Unit();
							unitContract.setValue(d.getUnit().getValue());
							unitContract.setSymbol(d.getUnit().getSymbol());
							unitContract.setType(d.getUnit().getType());
							dataContract.setUnit(unitContract);
						}

						/************ Id *************/
						BigInteger bigInt = new BigInteger(Integer.toString(d
								.getIddb()));
						environmentContract.setCreator(e.getCreator());
						dataContract.setId(bigInt);

						environmentContract.getData().add(dataContract);

						contract.getEnvironment().add(environmentContract);
					}

				}
			}

		}

		return contract;
	}

	/**
	 * Persiste um objeto do tipo Eeml
	 * 
	 * @param eeml
	 */
	public void saveEeml(Eeml eeml) {
		manager.persist(eeml);
	}

	/**
	 * Atualiza um objeto do tipo Eeml
	 * @param eeml
	 */
	public void updateEeml(Eeml eeml) {
		manager.merge(eeml);
	}

	public void updateData(Data data) {
		manager.merge(data);
	}

	public void saveValue(Value value) {
		manager.persist(value);
	}

	public void saveData(Data data) {
		manager.persist(data);
	}

	public void saveDataPoint(Datapoints datapoint) {
		manager.persist(datapoint);
	}

	public void updateEnvironment(Environment environment) {
		manager.merge(environment);
	}
	
	public void updateTrigger(Trigger trigger){
		manager.merge(trigger);
	}

	/**
	 * 
	 * @param environmentId
	 * @return objeto do tipo Environment
	 */
	public Environment findEnvironmentByEnvironmentId(String environmentId) {
		return manager.find(Environment.class, Integer.valueOf(environmentId));
	}

	/**
	 * 
	 * @param environmentId
	 * @return objeto do tipo Data
	 */
	public Data findDataByEnvironmentId(String environmentId) {
		String jpql = "select data from Data as data join fetch environment.data as environment where environment.id = :environmentId";
		Query query = this.manager.createQuery(jpql);
		query.setParameter("environmentId", environmentId);
		return (Data) query.getSingleResult();
	}
	
	/**
	 * 
	 * @param environmentIddb
	 * @return nome (String) do objeto do tipo Environment
	 */
	public String findEnironmentNameById(int environmentIddb) {
		String jpql = "select e.title from Environment e where e.iddb = :environmentIddb";
		Query query = this.manager.createQuery(jpql);
		query.setParameter("environmentIddb",  environmentIddb);
		return (String) query.getSingleResult();
	}

	/**
	 * 
	 * @param title
	 * @return lista de objetos do tipo Environment
	 */
	public List<Environment> findEnvironmentsByTitle(String title) {
		String jpql = "select e from Environment e where e.title LIKE :title and e._private != 'Y'";
		Query query = this.manager.createQuery(jpql);
		query.setParameter("title", "%" + title + "%");
		return query.getResultList();
	}

	/**
	 * 
	 * @param creator
	 * @return lista de objetos do tipo Environment
	 */
	public List<Environment> findEnvironmentsByUserLogin(String creator) {
		String jpql = "select e.environment from Eeml e join e.environment as ev where ev.creator = :creator";
		Query query = this.manager.createQuery(jpql);
		query.setParameter("creator", creator);
		return query.getResultList();
	}

	/**
	 * 
	 * @return lista de objetos do tipo Environment
	 */
	public List<Environment> findAllEnvironments() {
		String jpql = "select e from Environment e where e._private != 'Y' order by e.iddb asc";
		Query query = this.manager.createQuery(jpql);
		return query.getResultList();
	}

	/**
	 * 
	 * @param userName
	 * @return lista de objetos do tipo Environment
	 */
	public List<Environment> findAllEnvironmentsByUser(String userName) {
		// Get userGroups that associated to user
		String jpql = "select ug from UserGroup ug inner join ug.users u where u.login =:userName";
		Query query = this.manager.createQuery(jpql);
		query.setParameter("userName", userName);
		List<UserGroup> userGroupsByUser = query.getResultList();

		StringBuilder jpql_2 = new StringBuilder();
		StringBuilder jpql_envIddb = new StringBuilder();
		jpql_2.append("select e from Environment e where e.iddb in (");

		// Get environments associated to userGroups
		for (UserGroup ug : userGroupsByUser) {
			if (ug.getEnvironment() != null) {
				jpql_envIddb.append(ug.getEnvironment().getIddb() + ",");
			}
		}

		if (jpql_envIddb.length() != 0) {
			jpql_envIddb.replace(jpql_envIddb.length() - 1,
					jpql_envIddb.length(), ")");
			jpql_2.append(jpql_envIddb);
			Query query_2 = this.manager.createQuery(jpql_2.toString());
			List<Environment> environments = query_2.getResultList();
			return environments;
		} else {
			return new ArrayList<Environment>();
		}
	}

	/**
	 * 
	 * @param id
	 * @return objeto do tipoEnvironment
	 */
	public Environment findEnvironmentById(int id) {
		return manager.find(Environment.class, id);
	}

	/**
	 * 
	 * @param iddb
	 * @return objeto do tipo Location
	 */
	public Location findLocationByEnvironment(int iddb) {
		String jpql = "select e.location from Environment e join e.location l where e.iddb = :iddb";
		Query query = this.manager.createQuery(jpql);
		query.setParameter("iddb", iddb);
		return (Location) query.getSingleResult();
	}

	/**
	 * 
	 * @param id
	 * @return objeto do tipo Unit
	 */
	public Unit findUnitById(int id) {
		return manager.find(Unit.class, id);
	}

	/**
	 * 
	 * @param idEnvironment
	 * @return objeto do tipo Environment
	 */
	public Environment findEnvironmentWithDatas(int idEnvironment) {
		String jpql = "Select e From Environment e left join fetch e.data d WHERE e.iddb = :iddb";
		Query query = this.manager.createQuery(jpql);
		query.setParameter("iddb", idEnvironment);
		return (Environment) query.getSingleResult();
	}

	/**
	 * 
	 * @param id identificador do objeto do tipo Data
	 * @return objeto do tipo Data
	 */
	public Data findDataWithDatapoints(int id) {
		String jpql = "Select d From Data d left join fetch d.datapoints WHERE d.iddb = :iddb";
		Query query = this.manager.createQuery(jpql);
		query.setParameter("iddb", id);
		return (Data) query.getSingleResult();
	}

	/**
	 * 
	 * @param userName
	 * @return lista de objetos do tipo Data
	 */
	public List<Data> findDataByUserNameWithDatapoints(String userName) {
		String jpql = "Select d From Environment e join e.data d left join fetch d.datapoints WHERE e.creator = :userName";
		Query query = this.manager.createQuery(jpql);
		query.setParameter("userName", userName);
		return query.getResultList();
	}

	/**
	 * 
	 * @param idData
	 * @return lista de objetos do tipo Value
	 */
	public List<Value> findValuesByData(int idData) {
		String jpql = "select v from Data d join d.datapoints dp join dp.value v where d.iddb =:idData ORDER BY v.iddb DESC";
		Query query = this.manager.createQuery(jpql).setMaxResults(10);
		query.setParameter("idData", idData);
		return query.getResultList();
	}

	public void deleteEnvironment(Environment environment) {
		Eeml eeml = findEemlByIddbEnvironment(environment.getIddb());
		manager.merge(eeml);
		manager.remove(eeml);
	}

	/**
	 * 
	 * @param idbd
	 * @return objeto do tipo Eeml
	 */
	public Eeml findEemlByIddbEnvironment(int idbd) {
		String jpql = "select e from Eeml e join fetch e.environment v where v.iddb =:idbd";
		Query query = this.manager.createQuery(jpql);
		query.setMaxResults(30);
		query.setParameter("idbd", idbd);
		return (Eeml) query.getSingleResult();
	}

	/**
	 * 
	 * @param userName
	 * @return lista de objetos do tipo Eeml
	 */
	public List<Eeml> findEemlByUserName(String userName) {
		String jpql = "select e from Eeml e join fetch e.environment v where v.creator =:userName";
		Query query = this.manager.createQuery(jpql);
		query.setParameter("userName", userName);
		return query.getResultList();
	}

	/**
	 * 
	 * @param idEnvironment
	 * @param idData
	 * @return objeto do tipo Eeml
	 */
	public Eeml findEemlByIddbEnvironmentIdData(int idEnvironment, int idData) {
		String jpql = "select e from Eeml e join fetch e.environment ev join ev.data d where ev.iddb =:idEnvironment and d.iddb =:idData";
		Query query = this.manager.createQuery(jpql);
		query.setParameter("idEnvironment", idEnvironment);
		query.setParameter("idData", idData);
		return (Eeml) query.getSingleResult();
	}

	/**
	 * 
	 * @param idbd
	 * @return objeto do tipo Eeml
	 */
	public Eeml findEemlByIddbEnvironmentWithData(int idbd) {
		String jpql = "select e from Eeml e join fetch e.environment v join fetch v.data where v.iddb =:idbd";
		Query query = this.manager.createQuery(jpql);
		query.setMaxResults(30);
		query.setParameter("idbd", idbd);
		return (Eeml) query.getSingleResult();
	}

	/**
	 * 
	 * @param p_tag
	 * @return lista de Environment.Adrress
	 */
	public List<String> findEnvironmentAdreesByTag(String p_tag) {
		String nativeQuery = "SELECT e.website FROM Environment e INNER JOIN Environment_tag t ON e.iddb = Environment_iddb "
				+ "where t.tag =:p_tag";
		Query query = this.manager.createNativeQuery(nativeQuery);
		query.setParameter("p_tag", p_tag);
		return query.getResultList();
	}

	/**
	 * 
	 * @return objeto do tipo Environment
	 */
	public Environment findEnvironmentWrnp() {
		String jpql = "Select e From Environment e WHERE e.description LIKE :mapa";
		Query query = this.manager.createQuery(jpql);
		query.setParameter("mapa", "%Mapa%");
		return (Environment) query.getSingleResult();
	}

	/**
	 * 
	 * @return Lista de objetos do tipo Unit
	 */
	public List<Unit> findAllUnits() {
		String jpql = "Select u From Unit u";
		Query query = this.manager.createQuery(jpql);
		return query.getResultList();
	}
	
	public void saveUnit(Unit unit){
		manager.persist(unit);
	}

	@SuppressWarnings("unchecked")
	public List<Eeml_Contract> findEemlEnvironmentsByCriteria(
			EnvironmentSearchCriteria criteria) {
		Session session = (Session) manager.getDelegate();
		Criteria crobject = session.createCriteria(Environment.class);

		if (criteria.getDescription() != null) {
			crobject.add(Restrictions.like("description", "%" + criteria.getDescription() + "%")
					.ignoreCase());
		}

		if (criteria.getCreator() != null) {
			crobject.add(Restrictions.eq("creator", criteria.getCreator()).ignoreCase());
		}
		
		if (criteria.getUpdated() != null){
			GregorianCalendar update = new GregorianCalendar();
			update.setTime(criteria.getUpdated());
			crobject.add(Restrictions.eq("updated", update));
		}
		
		if (criteria.get_private() != null) {
			crobject.add(Restrictions.eq("_private", criteria.get_private()));
		}
		
		
		if (criteria.getSensor() != null) {
			int sensorID = Integer.valueOf(criteria.getSensor());
			crobject.add(Restrictions.eq("sensor", sensorID));
		}
		
		if (criteria.getStatus() != null) {
			crobject.add(Restrictions.eq("status", criteria.getStatus()));
		}
		
		if (criteria.getTitle() != null) {
			crobject.add(Restrictions.like("title", "%" + criteria.getTitle() + "%"));
		}
	
		if (criteria.getUnit() != null) {
			int unitID = Integer.valueOf(criteria.getUnit());
			crobject.add(Restrictions.eq("unit",unitID));
		}
		
		if (criteria.getConnectedDevice() != null) {
			int cnndeviceID = Integer.valueOf(criteria.getConnectedDevice());
			crobject.add(Restrictions.eq("connectedDevice",cnndeviceID));
		}
		
		List<Environment> environmentList = crobject.list();
		List<Eeml_Contract> eemlList = new ArrayList<Eeml_Contract>();
		for (Environment e : environmentList) {
			Eeml eeml = findEemlByIddbEnvironment(e.getIddb());			
			try {
				eemlList.add(this.eemlDomainToContract(eeml));
			} catch (DatatypeConfigurationException e1) {
				e1.printStackTrace();
			}
		}
		
		return eemlList;
		
	}
}
