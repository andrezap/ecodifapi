package br.com.ecodif.mb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import br.com.ecodif.dao.ApplicationDAO;
import br.com.ecodif.dao.UserDAO;
import br.com.ecodif.domain.Application;
import br.com.ecodif.domain.Eeml;
import br.com.ecodif.domain.Environment;
import br.com.ecodif.domain.User;
import br.com.ecodif.eeml_contract.Eeml_Contract;
import br.com.ecodif.framework.EemlManager;
import br.com.ecodif.searchcriteria.ApplicationSearchCriteria;

/**
 * <i>Managed bean</i> que intermedia a comunicação entre a interface Web e as
 * classes relacionadas à lógica de negócio referente a aplicações
 * @author Everton Cavalcante (evertonrsc@ppgsc.ufrn.br)
 */
@ManagedBean
@ViewScoped
public class ApplicationMB {

	/**
	 * Referência à classe de acesso a dados associada a aplicações
	 * @see br.com.ecodif.dao.ApplicationDAO
	 */
	@EJB
	private ApplicationDAO applicationDAO;

	/**
	 * Referência à classe de acesso a dados associada a usuários
	 * @see br.com.ecodif.dao.UserDAO
	 */
	@EJB
	private UserDAO userDAO;

	/**
	 * Referência ao <i>managed bean</i> associado a usuários
	 * @see br.com.ecodif.mb.UserMB
	 */
	@ManagedProperty("#{userMB}")
	private UserMB userMB;

	/**
	 * Referência ao <i>managed bean</i> associado a <i>feeds</i>
	 * @see br.com.ecodif.mb.EemlMB
	 */
	@ManagedProperty("#{eemlMB}")
	private EemlMB eemlMB;

	/**
	 * Aplicação em questão
	 * @see br.com.ecodif.domain.Application
	 */
	private Application application;

	/**
	 * Critérios de busca utilizados para consulta
	 * @see br.com.ecodif.searchcriteria.ApplicationSearchCriteria
	 */
	private ApplicationSearchCriteria criteria;

	/**
	 * Arquivo informado pelo usuário para <i>upload</i>
	 * @see org.primefaces.model.UploadedFile
	 */
	private UploadedFile applicationFile;

	/** Lista que armazena resultados de consultas por aplicações */
	private List<Application> applicationsAll;

	/** Lista de aplicações pertencentes ao usuário */
	private List<Application> applicationsUser;

	/** Objeto que armazena o resultado da execução de uma aplicação */
	private String resultExecution = new String("");

	/**
	 * Objeto <code>HashMap</code> que armazena a seleção de <i>checkboxes</i>
	 * na pagina Web, contendo o identificador do <i>feed</i> a ser incluído na
	 * aplicação e uma flag indicando se esse objeto foi selecionado ou não
	 */
	private Map<Long, Boolean> inclusionFeeds;

	/**
	 * Objeto <code>HashMap</code> que armazena a seleção de <i>checkboxes</i>
	 * na pagina Web, contendo o identificador do <i>feed</i> a ser excluído da
	 * aplicação e uma flag indicando se esse objeto foi selecionado ou não
	 */
	private Map<Long, Boolean> exclusionFeeds;

	private Eeml_Contract eemlContract;

	private Eeml eeml;

	@EJB
	private EemlManager eemlManager;

	
	/** Método invocado na instanciação do <i>managed</i> bean */
	@PostConstruct
	public void init() {

		/*
		 * Para acessar o objeto da classe Application proveniente de outra
		 * página, é utilizado um objeto HttpSession
		 */
		try {
			HttpServletRequest request = (HttpServletRequest) FacesContext
					.getCurrentInstance().getExternalContext().getRequest();
			HttpSession session = (HttpSession) request.getSession();
			
			if (session.getAttribute("application") != null) {
				this.setApplication((Application) session
						.getAttribute("application"));
			}
		} catch (NoResultException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		/*
		 * Para acessar o objeto referente ao resultado da execução de
		 * aplicação, realizada em outra página, é utilizado um objeto
		 * HttpSession
		 */
		try {
			HttpServletRequest request = (HttpServletRequest) FacesContext
					.getCurrentInstance().getExternalContext().getRequest();
			HttpSession session = (HttpSession) request.getSession();
			
			if (session.getAttribute("resultExecution") != null) {
				this.setResultExecution((String) session
						.getAttribute("resultExecution"));
			}
		} catch (NoResultException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		/*
		 * Lista previamente carregada com todas as aplicações públicas
		 * registradas
		 */
		if (applicationsAll == null) {
			this.setAllApplications(applicationDAO.findPublicApplications(false));
		}
	}

	public Eeml_Contract getEemlContract() {
		if (eemlContract == null)
			eemlContract = new Eeml_Contract();
		return eemlContract;
	}

	public void setEemlContract(Eeml_Contract eemlContract) {
		this.eemlContract = eemlContract;
	}

	public Eeml getEeml() {
		if (eeml == null)
			eeml = new Eeml();
		return eeml;
	}

	public void setEeml(Eeml eeml) {
		this.eeml = eeml;
	}

	/**
	 * Retorna a aplicação em questão 
	 * @return Aplicação em questão
	 */
	public Application getApplication() {
		if (application == null) {
			application = new Application();
		}
		return application;
	}

	/**
	 * Modifica a aplicação em questão
	 * @param application Objeto da classe <code>Application</code> para alteração
	 */
	public void setApplication(Application application) {
		this.application = application;
	}

	/**
	 * Retorna os critérios de busca utilizados, agregados como um objeto da
	 * classe <code>ApplicationSearchCriteria</code>
	 * @return Critérios de busca utilizados
	 */
	public ApplicationSearchCriteria getCriteria() {
		if (criteria == null) {
			criteria = new ApplicationSearchCriteria();
		}
		return criteria;
	}

	/**
	 * Modifica os critérios de busca
	 * @param criteria Objeto da classe <code>ApplicationSearchCriteria</code> 
	 * 			para alteração
	 */
	public void setCriteria(ApplicationSearchCriteria criteria) {
		this.criteria = criteria;
	}

	/**
	 * Modifica a referência para o <i>managed bean</i> associado a usuários
	 * @param userMB Objeto da classe <code>UserMB</code> para alteração
	 */
	public void setUserMB(UserMB userMB) {
		this.userMB = userMB;
	}

	/**
	 * Modifica a referência para o <i>managed bean</i> associado a <i>feeds</i>
	 * @param userMB Objeto da classe <code>EemlMB</code> para alteração
	 */
	public void setEemlMB(EemlMB eemlMB) {
		this.eemlMB = eemlMB;
	}

	/**
	 * Retorna o arquivo informado pelo usuário para <i>upload</i>
	 * @return Arquivo informado pelo usuário para <i>upload</i>
	 */
	public UploadedFile getApplicationFile() {
		return applicationFile;
	}

	/**
	 * Modifica o arquivo informado pelo usuário para <i>upload</i>
	 * @param applicationFile Arquivo para alteração
	 */
	public void setApplicationFile(UploadedFile applicationFile) {
		this.applicationFile = applicationFile;
	}

	/**
	 * Retorna a lista que armazena resultados de consultas por aplicações
	 * @return Lista que armazena resultados de consultas por aplicações
	 */
	public List<Application> getApplicationsAll() {
		return applicationsAll;
	}

	/**
	 * Modifica a lista que armazena resultados de consultas por aplicações
	 * @param applications Lista de aplicações para alteração
	 */
	public void setAllApplications(List<Application> applications) {
		this.applicationsAll = applications;
	}

	/**
	 * Retorna a lista de aplicações pertencentes ao usuário em questão, dado
	 * pelo objeto <code>userMB</code>
	 * @return Lista de aplicações pertencentes ao usuário
	 */
	public List<Application> getApplicationsUser() {
		User user = userDAO.findByLogin(userMB.getUserName());
		if (applicationsUser == null) {
			this.setApplicationsUser(applicationDAO
					.findApplicationsByUserId(user.getId()));
		}
		return applicationsUser;
	}

	/**
	 * Modifica a lista de aplicações pertencentes ao usuário
	 * @param applicationsUser Lista de aplicações para alteração
	 */
	public void setApplicationsUser(List<Application> applicationsUser) {
		this.applicationsUser = applicationsUser;
	}

	/**
	 * Retorna o objeto que armazena o resultado da execução de uma aplicação
	 * @return Objeto que armazena o resultado da execução de uma aplicação
	 */
	public String getResultExecution() {
		return resultExecution;
	}

	/**
	 * Modifica o objeto que armazena o resultado da execução de uma aplicação
	 * @param resultExecution Objeto para alteração
	 */
	public void setResultExecution(String resultExecution) {
		this.resultExecution = resultExecution;
	}

	/**
	 * Retorna o <code>HashMap</code> que armazena a seleção de
	 * <i>checkboxes</i> na pagina Web
	 * @return Objeto com a seleção de <i>checkboxes</i>
	 */
	public Map<Long, Boolean> getInclusionFeeds() {
		if (inclusionFeeds == null) {
			inclusionFeeds = new HashMap<Long, Boolean>();
		}
		return inclusionFeeds;
	}

	/**
	 * Modifica o <code>HashMap</code> que armazena a seleção de
	 * <i>checkboxes</i> na pagina Web
	 * @param checked Objeto para alteração
	 */
	public void setInclusionFeeds(Map<Long, Boolean> checked) {
		this.inclusionFeeds = checked;
	}

	/**
	 * Retorna o <code>HashMap</code> que armazena a seleção de
	 * <i>checkboxes</i> na pagina Web
	 * @return Objeto com a seleção de <i>checkboxes</i>
	 */
	public Map<Long, Boolean> getExclusionFeeds() {
		if (exclusionFeeds == null) {
			exclusionFeeds = new HashMap<Long, Boolean>();
		}
		return exclusionFeeds;
	}

	/**
	 * Modifica o <code>HashMap</code> que armazena a seleção de
	 * <i>checkboxes</i> na pagina Web
	 * @param checked Objeto para alteração
	 */
	public void setExclusionFeeds(Map<Long, Boolean> checked) {
		this.exclusionFeeds = checked;
	}

	/**
	 * Realiza uma busca por aplicações com base em critérios de busca,
	 * especificados pelo objeto <code>criteria</code> da classe
	 * <code>ApplicationSearchCriteria</code>
	 */
	public void searchApplications() {

		/*
		 * Se apenas uma das datas especificadas para consulta por data de
		 * criação é informada, é exibida uma mensagem de erro para o usuário
		 */
		if ((criteria.getStartDate() != null && criteria.getEndDate() == null)
				|| (criteria.getStartDate() == null && criteria.getEndDate() != null)) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							"Por favor, informe as duas datas da faixa de busca."));
			return;

		} else {

			/*
			 * Se ambas as datas para a consulta são informadas, porém a segunda
			 * data (data-fim) é anterior à primeira data (data-início), é
			 * exibida uma mensagem de erro exibida quando o usuário
			 */
			if (criteria.getStartDate() != null
					&& criteria.getEndDate() != null
					&& criteria.getEndDate().before(criteria.getStartDate())) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								"As datas informadas são inválidas. Por favor, tente novamente."));
				return;

			} else {

				/*
				 * Realiza a busca por aplicações com base nos critérios de
				 * busca especificados
				 */
				List<Application> apps = new ArrayList<Application>();
				Iterator<Application> it = applicationDAO
						.findApplicationsByCriteria(criteria).iterator();
				while (it.hasNext()) {
					apps.add(0, it.next());
				}

				this.setAllApplications(apps);
			}
		}
	}

	/**
	 * Retorna um objeto <code>StreamedContent</code> associado a um arquivo
	 * para <i>download</i>
	 * @see {@link http://www.primefaces.org/showcase/ui/fileDownload.jsf}
	 * 
	 * @return Objeto <code>StreamedContent</code> associado a um arquivo para
	 *         <i>download</i>
	 */
	public StreamedContent getFile() {
		StreamedContent file = null;
		if (this.getApplication().getId() != 0) {
			try {
				File fileDownload = new File(this.getApplication()
						.getEmmlReference());
				FileInputStream stream = new FileInputStream(fileDownload);
				file = new DefaultStreamedContent(stream, "",
						fileDownload.getName());
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		return file;
	}

	/** Salva (persiste) uma aplicação */
	public void saveApplication() {
		try {

			/*
			 * Se o nome da aplicação não for informado no formulário de
			 * cadastro, é exibida uma mensagem de erro para o usuário
			 */
			if (application.getName().isEmpty()) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							"Por favor, informe o nome da aplicação a ser cadastrada e tente novamente."));
				return;
			}

			// Usuário em questão
			User user = userDAO.findByLogin(userMB.getUserName());
			application.setUser(user);

			/* 
			 * Referência para o arquivo EMML a ser criado automaticamente no
			 * diretório de aplicações
			 */
			String fileNameRep = FacesContext.getCurrentInstance()
					.getExternalContext()
					.getInitParameter("ApplicationsDirectory")
					+ userMB.getUserName() + "_" + application.getName() + ".emml";
			application.setEmmlReference(fileNameRep);

			/*
			 * Criação automática de arquivo EMML a partir dos dados de cadastro
			 * da aplicação
			 */
			createEMMLFile();

			if (applicationFile != null) {
				// Copia do arquivo para o diretório de aplicações
				copyFile(applicationFile.getInputstream(), fileNameRep);
				
				/* 
				 * Referência para o arquivo EMML a ser criado automaticamente 
				 * no diretório do motor de execução
				 */
				String fileNameEng = FacesContext.getCurrentInstance()
						.getExternalContext()
						.getInitParameter("EMMLEnginePath") + File.separator +
						userMB.getUserName() + "_" + application.getName() + ".emml";
				
				// Copia do arquivo para o diretório do motor de execução
				copyFile(applicationFile.getInputstream(), fileNameEng);

				/*
				 * Se o arquivo EMML importado pelo usuário for inválido (i.e.
				 * não estiver em conformidade com o esquema da linguagem EMML,
				 * é exibida uma mensagem de erro para o usuário
				 */
				if (!isValidEMML(fileNameRep)) {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								"O arquivo EMML a ser importado é inválido. Por favor, tente novamente."));

					File fileDelete = new File(fileNameRep);
					fileDelete.delete();

					return;
				}
			}

			/*
			 * Se já existir uma aplicação cadastrada com o nome informado no
			 * formulário, é exibida uma mensagem de erro para o usuário
			 */
			if (application.getId() == 0) {
				if (!application.getName().isEmpty()
						&& applicationDAO.findApplicationByName(application
								.getName()) != null) {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								"Já existe uma aplicação cadastrada com o nome informado. Por favor, tente novamente."));
					return;
				}

				// Datas de criação e atualização setadas como data/hora atual
				GregorianCalendar gc = (GregorianCalendar) GregorianCalendar
						.getInstance();
				application.setCreationDate(gc);
				application.setUpdateDate(gc);

				// Criação da aplicação
				applicationDAO.save(application);
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								"Aplicação criada com sucesso."));

			} else {
				// Data de atualização setada como data/hora atual
				application.setUpdateDate((GregorianCalendar) GregorianCalendar
						.getInstance());

				// Atualização da aplicação
				applicationDAO.update(application);
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								"Aplicação atualizada com sucesso."));
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
					"Erro ocorrido ao tentar cadastrar a aplicação. Por favor, tente novamente."));
		} finally {
			FacesContext fc = FacesContext.getCurrentInstance();
			HttpSession session = (HttpSession) fc.getExternalContext().
					getSession(false);
			session.setAttribute("application", application);
		}
	}

	/**
	 * Criação automática de arquivo EMML a partir dos dados de cadastro da
	 * aplicação
	 * @throws java.io.IOException
	 */
	public void createEMMLFile() throws IOException {

		// Criação de conteúdo a partir do arquivo EMML default
		File file = new File(FacesContext.getCurrentInstance()
				.getExternalContext().getInitParameter("EMMLDefaultFile"));
		byte[] buffer = Files.readAllBytes(file.toPath());
		String stub = new String(buffer);

		/*
		 * Substutuição dos padrões do arquivo default pelas informações
		 * fornecidas no cadastro
		 */
		Pattern regex = Pattern.compile("#\\{([^}]*)\\}");
		Matcher regexMatcher = regex.matcher(stub);
		StringBuffer sb = new StringBuffer();
		int i = 0;
		while (regexMatcher.find()) {
			regexMatcher.appendReplacement(sb,
					replaceWithValue(regexMatcher.group()));
			i = regexMatcher.end();
		}

		// Escrita do conteúdo no respectivo arquivo EMML associado à aplicação
		String contents = sb.toString() + stub.substring(i);
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(application.getEmmlReference()),
				"ISO-8859-1"));
		out.write(contents);
		out.close();
	}

	/**
	 * Realiza o casamento entre os padrões presentes no arquivo EMML default
	 * pelas respectivas informações de cadastro da aplicação
	 * @param group Padrão a ser substituído
	 * @return Respectiva informação a substituir o padrão
	 */
	private String replaceWithValue(String group) {
		if (group.equals("#{app_name}")) {
			return application.getName();
		} else if (group.equals("#{app_description}")) {
			return application.getDescription();
		} else if (group.equals("#{author}")) {
			return application.getUser().getName();
		} else if (group.equals("#{author_email}")) {
			return application.getUser().getEmail();
		} else if (group.equals("#{app_tags}")) {
			return application.getTags();
		}

		return "";
	}

	/**
	 * Verifica se um arquivo EMML é válido, i.e. está em conformidade com o
	 * esquema XSD da linguagem EMML 
	 * @see {@link http://www.openmashup.org/schemas/v1.0/EMMLSpec.xsd}
	 * 
	 * @param emmlfile Arquivo EMML a ser validado
	 * @return <code>true</code> (verdadeiro) se o arquivo EMML é válido,
	 *         <code>false</code> caso contrário
	 * 
	 * @throws org.xml.sax.SAXException
	 * @throws java.io.IOException
	 * @throws javax.xml.parsers.ParserConfigurationException
	 */
	public boolean isValidEMML(String emmlfile) {
		try {
			// Referência para o esquema XSD da linguagem EMML
			String EMMLSchema = FacesContext.getCurrentInstance()
					.getExternalContext().getInitParameter("EMMLSchema");
			Source schemaFile = new StreamSource(new File(EMMLSchema));
						
			// Estrutura baseada em XML a ser validada
			InputStream inputStream = new FileInputStream(new File(emmlfile));
			Reader reader = new InputStreamReader(inputStream, "UTF-8");
			InputSource xmlFile = new InputSource(reader);
			xmlFile.setEncoding("UTF-8");
				
			// Validação frente ao esquema XSD
			SchemaFactory schemaFactory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(schemaFile);
			Validator validator = schema.newValidator();
			validator.validate(new SAXSource(xmlFile));
			return true;
		} catch (SAXException e) {
			System.out.println("Reason: " + e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Redireciona para a página pública de visualização de detalhes de
	 * aplicação
	 * @param currentApplication Aplicação em questão
	 * @return Referência para a página pública de visualização de detalhes de
	 *         aplicação
	 * @see /WEB-INF/faces-config.xml
	 */
	public String viewApplicationPublic(Application currentApplication) {

		/*
		 * Passagem do objeto referente à aplicação em questão para a página de
		 * visualizacao
		 */
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext()
				.getSession(false);
		session.setAttribute("application", currentApplication);
		return "viewapplication";
	}

	/**
	 * Redireciona para a página protegida de visualização de detalhes de
	 * aplicação
	 * 
	 * @param currentApplication Aplicação em questão
	 * @return Referência para a página protegida de visualização de detalhes de
	 *         aplicação
	 * @see /WEB-INF/faces-config.xml
	 */
	public String viewApplicationDevApp(Application currentApplication) {

		/*
		 * Passagem do objeto referente à aplicação em questão para a página de
		 * visualização
		 */
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().
				getSession(false);
		session.setAttribute("application", currentApplication);
		return "viewapplicationdevapp";
	}

	/**
	 * Redireciona para a página de edição de aplicação
	 * @return Referência para a página de edição de aplicação
	 * @see /WEB-INF/faces-config.xml
	 */
	public String editApplication() {

		/*
		 * Passagem do objeto referente à aplicação em questão para a página de
		 * edição
		 */
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().
				getSession(false);
		session.setAttribute("application", application);
		return "cadapplication";
	}

	/**
	 * Realiza a exclusão da aplicação em questão
	 * @see /WEB-INF/faces-config.xml
	 */
	public void deleteApplication() {
		try {
			// Caminho do arquivo EMML associado à aplicação a ser excluída
			String emmlFile = application.getEmmlReference();

			/*
			 * Exclusão da aplicação em questão da respectiva tabela no banco
			 * dados
			 */
			applicationDAO.delete(application);

			// Remoção da aplicação em questão da lista de aplicações do usuário
			this.getApplicationsUser().remove(application);

			// Exclusão do arquivo EMML do diretório de aplicações
			File fileRep = new File(emmlFile);
			fileRep.delete();
			
			/* 
			 * Referência para o arquivo EMML a ser criado automaticamente 
			 * no diretório do motor de execução
			 */
			String fileNameEng = FacesContext.getCurrentInstance()
					.getExternalContext()
					.getInitParameter("EMMLEnginePath") + File.separator +
					userMB.getUserName() + "_" + application.getName() + ".emml";
			
			// Exclusão do arquivo EMML do diretório do motor de execução
			File fileEng = new File(fileNameEng);
			fileEng.delete();

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							"Aplicação excluída com sucesso."));

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							"Erro ocorrido ao tentar excluir a aplicação"));
		}
	}

	/**
	 * Execução de aplicação (<i>mashup</i> EMML) em área pública
	 * @param currentApplication Aplicação a ser executada
	 * @return Referência para a página pública de apresentação de resultados da
	 *         execução
	 */
	public String executeApplicationPublic(Application currentApplication) {
		String emmlreference = currentApplication.getEmmlReference();
		String appname = emmlreference.substring(
				emmlreference.lastIndexOf(File.separator) + 1,
				emmlreference.lastIndexOf("."));

		StringBuilder sb = new StringBuilder();
		try {
			// Chamada à aplicação no motor de execução EMML
			URL url = new URL(FacesContext.getCurrentInstance()
					.getExternalContext().getInitParameter("EMMLEngineURL")
					+ appname);

			InputStream execution = url.openStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					execution, "ISO-8859-1"));

			// Construção do conteúdo resultante da execução da aplicação
			String str = "";
			while ((str = in.readLine()) != null) {
				sb.append(str + "\n");
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.setResultExecution(sb.toString());
		
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext()
				.getSession(false);
		session.setAttribute("application", currentApplication);
		session.setAttribute("resultExecution", resultExecution);

		return "resultexecution";
	}

	/**
	 * Execução de aplicação (<i>mashup</i> EMML) em área protegida
	 * @param currentApplication Aplicação a ser executada
	 * @return Referência para a página protegida de apresentação de resultados
	 *         da execução
	 */
	public String executeApplicationDevApp(Application currentApplication) {
		String emmlreference = currentApplication.getEmmlReference();
		String appname = emmlreference.substring(
				emmlreference.lastIndexOf(File.separator) + 1,
				emmlreference.lastIndexOf("."));

		StringBuilder sb = new StringBuilder();
		try {
			// Chamada à aplicação no motor de execução EMML
			URL url = new URL(FacesContext.getCurrentInstance()
					.getExternalContext().getInitParameter("EMMLEngineURL")
					+ appname);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					url.openStream(), "ISO-8859-1"));

			// Construção do conteúdo resultante da execução da aplicação
			String str = "";
			while ((str = in.readLine()) != null) {
				sb.append(str + "\n");
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.setResultExecution(sb.toString());
		
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext()
				.getSession(false);
		session.setAttribute("application", currentApplication);
		session.setAttribute("resultExecution", resultExecution);

		return "resultexecutiondevapp";
	}

	/**
	 * Copia um determinado conteúdo em um arquivo
	 * @param in Conteúdo a ser copiado para o arquivo especificado
	 * @param fileName Caminho do arquivo de origem
	 */
	public void copyFile(InputStream in, String fileName) {
		try {
			OutputStream out = new FileOutputStream(new File(fileName));

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = in.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}

			in.close();
			out.flush();
			out.close();

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Copia o conteúdo especificado em um arquivo de origem para um arquivo de
	 * destino
	 * @param origin Caminho do arquivo de origem
	 * @param target Caminho do arquivo de destino
	 */
	public void copyFile(String origin, String target) {
		try {
			
			File file = new File(origin);
			byte[] buffer = Files.readAllBytes(file.toPath());
			String stub = new String(buffer);

			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(target), "ISO-8859-1"));
			out.write(stub);
			out.flush();
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Redireciona para a página de vinculação de <i>feeds</i>
	 * @return Referência para a página de vinculação de <i>feeds</i>
	 * @see /WEB-INF/faces-config.xml
	 */
	public String includeFeeds() {
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext()
				.getSession(false);
		session.setAttribute("application", application);
		return "includefeeds";
	}

	/** Vinculação de <i>feeds</i> à aplicação em questão */
	public void bindFeeds() {
		try {

			// Inclusão dos feeds selecionados na página Web à aplicação em
			// questão
			for (Environment e : eemlMB.getAllEnvironments()) {
				if (inclusionFeeds.get(e.getIddb())) {
					application.getFeeds().add(e);
				}
			}

			/*
			 * Construção de tags no arquivo EMML associado à aplicação a fim de
			 * permitir a utilização dos feeds como variáveis de entrada para o
			 * mashup
			 */
			buildEMMLInputFeeds();

			// Data de atualização setada como data/hora atual
			application.setUpdateDate((GregorianCalendar) GregorianCalendar
					.getInstance());

			// Atualização da aplicação
			applicationDAO.update(application);

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
						"Os feeds selecionados foram incluídos com sucesso na aplicação."));

			inclusionFeeds.clear();

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
					"Erro ocorrido ao tentar incluir os feeds selecionados na aplicação."));
		}
	}

	/**
	 * Construção de <i>tags</i> no arquivo EMML associado à aplicação a fim de
	 * permitir a utilização dos <i>feeds</i> como variáveis de entrada para o
	 * <i>mashup</i>
	 * @throws IOException
	 */
	private void buildEMMLInputFeeds() throws IOException {

		// Feeds da aplicação
		List<Environment> feeds = new ArrayList<Environment>();
		Iterator<Environment> it = application.getFeeds().iterator();
		while (it.hasNext()) {
			feeds.add(0, it.next());
		}

		String addressPortal = FacesContext.getCurrentInstance()
				.getExternalContext().getInitParameter("AddressPortal");

		/*
		 * Construção das tags referentes às variáveis de entrada do mashup
		 * (<variable>), uma para cada feed vinculado à aplicação. Os nomes das
		 * variáveis são convencionados como feed<id>, onde <id> é o
		 * identificador do feed em questão.
		 */
		StringBuilder sb = new StringBuilder();
		sb.append("\n\t<variables>\n");
		for (Environment e : feeds) {
			sb.append("\t\t<variable name=\"feed" + e.getIddb()
					+ "\" type=\"document\" />\n");
		}
		sb.append("\t</variables>\n\n");

		/*
		 * Construção das tags referentes à obtenção via requisição HTTP GET do
		 * conteúdo dos feeds vinculados à aplicação (<directinvoke>), conteúdo
		 * esse que será armazenado nas respectivas variáveis referentes aos
		 * feeds
		 */
		for (Environment e : feeds) {
			sb.append("\t<directinvoke endpoint=\"" + addressPortal
					+ e.getWebsite()
					+ "\" method=\"GET\" outputvariable=\"$feed" + e.getIddb()
					+ "\" />\n");
		}
		sb.append("\n</mashup>");

		/*
		 * Atualização automática do arquivo EMML da aplicação com as tags
		 * construídas
		 */
		File file = new File(application.getEmmlReference());
		byte[] buffer = Files.readAllBytes(file.toPath());
		String stub = new String(buffer);
		stub = stub.replace("</mashup>", sb.toString());

		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(application.getEmmlReference()),
				"ISO-8859-1"));
		out.write(stub);
		out.close();
	}

	/**
	 * Redireciona para a página de desvinculação de <i>feeds</i>
	 * @return Referência para a página de desvinculação de <i>feeds</i>
	 * @see /WEB-INF/faces-config.xml
	 */
	public String deleteFeeds() {
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext()
				.getSession(false);
		session.setAttribute("application", application);
		return "deletefeeds";
	}

	/** Desvinculação de <i>feeds</i> da aplicação em questão */
	public void unbindFeeds() {
		try {

			String addressPortal = FacesContext.getCurrentInstance()
					.getExternalContext().getInitParameter("AddressPortal");

			/*
			 * Carregamento do conteúdo atual do arquivo EMML associado à
			 * aplicação
			 */
			File file = new File(application.getEmmlReference());
			byte[] buffer = Files.readAllBytes(file.toPath());
			String stub = new String(buffer);

			for (Environment e : application.getFeeds()) {
				if (exclusionFeeds.get(e.getIddb())) {

					// Exclusão das tags associadas ao feed a ser excluído
					stub = stub.replace("\t\t<variable name=\"feed"
							+ addressPortal + e.getIddb()
							+ "\" type=\"document\" />\n", "");
					stub = stub.replace("\t<directinvoke endpoint=\""
								+ addressPortal + e.getWebsite()
								+ "\" method=\"GET\" outputvariable=\"$feed"
								+ e.getIddb() + "\" />\n", "");

					// Remoção efetiva dos feeds selecionados para desvinculação
					application.getFeeds().remove(e);
				}
			}

			// Atualização do arquivo EMML associado à aplicação
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(application.getEmmlReference()),
					"ISO-8859-1"));
			out.write(stub);
			out.close();

			// Data de atualização setada como data/hora atual
			application.setUpdateDate((GregorianCalendar) GregorianCalendar
					.getInstance());

			// Atualização da aplicação
			applicationDAO.update(application);

			FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null,
					"Os feeds selecionados foram incluídos com sucesso na aplicação."));

			exclusionFeeds.clear();

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
					"Erro ocorrido ao tentar incluir os feeds selecionados na aplicação."));
		}
	}

	/**
	 * Retorna uma lista com os <i>feeds</i> atualmente vinculados à aplicação
	 * @return Lista com <i>feeds</i> atualmente vinculados à aplicação
	 */
	public List<Environment> getFeeds() {
		List<Environment> feeds = new ArrayList<Environment>();
		Iterator<Environment> it = application.getFeeds().iterator();
		while (it.hasNext()) {
			feeds.add(0, it.next());
		}

		return feeds;
	}
	
	public String getContentsEmmlFile() {
		String contents = "";
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(application
					.getEmmlReference()));
			while (in.ready()) {
				contents += in.readLine() + "\n";
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return contents;
	}

	/**
	 * Verifica se a aplicação em questão já está cadastrada
	 * @return <code>true</code> (verdadeiro), em caso afirmativo,
	 *         <code>false</code> (falso), em caso negativo
	 */
	public boolean isApplicationRegistered() {
		return !(this.getApplication().getId() == 0);
	}

	/**
	 * Verifica se o <em>upload</em> do arquivo associado à aplicação foi 
	 * realizado com sucesso
	 * @return <code>true</code> (verdadeiro), em caso afirmativo,
	 *         <code>false</code> (falso), em caso negativo
	 */
	public boolean isApplicationFileUploaded() {
		if (this.getApplication().getId() == 0) {
			return false;
		} else {
			return !(this.applicationDAO.find(this.getApplication().getId())
					.getEmmlReference() == null);
		}
	}

	/**
	 * Verifica se a lista de aplicações como resultados de consultas por
	 * aplicações está vazia
	 * @return <code>true</code> (verdadeiro), em caso afirmativo,
	 *         <code>false</code> (falso), em caso negativo
	 */
	public boolean isApplicationsAllEmpty() {
		return applicationsAll.isEmpty();
	}

	/**
	 * Verifica se a lista de aplicações pertencentes ao usuário em questão está
	 * vazia
	 * @return <code>true</code> (verdadeiro), em caso afirmativo,
	 *         <code>false</code> (falso), em caso negativo
	 */
	public boolean isApplicationsUserEmpty() {
		User user = userDAO.findByLogin(userMB.getUserName());
		return applicationDAO.findApplicationsByUserId(user.getId()).isEmpty();
	}

	/**
	 * Verifica se o conjunto de <i>feeds</i> atualmente vinculados à aplicação
	 * está vazio
	 * @return <code>true</code> (verdadeiro), em caso afirmativo,
	 *         <code>false</code> (falso), em caso negativo
	 */
	public boolean isFeedsListEmpty() {
		return application.getFeeds().isEmpty();
	}
	

	// FIXME Este metodo futuramente sera removido desta classe
	public Eeml createApplicationFromFeeds() {
		Eeml eeml = new Eeml();

		// Lista com os endereços dos feeds
		List<String> feedsWebSites = new ArrayList<String>();
		feedsWebSites
				.add("http://localhost:8080/EcodifAPI/api/feeds/1/datastreams/1");
		feedsWebSites
				.add("http://localhost:8080/EcodifAPI/api/feeds/2/datastreams/2");

		String filepath = FacesContext.getCurrentInstance()
				.getExternalContext().getInitParameter("ApplicationsDirectory")
				+ "tempmashup.emml";

		// Criacao de mashup EMML temporario que agrega os feeds
		createTempMashup(feedsWebSites, filepath);

		/*
		 * Copia do conteúdo do arquivo EMML criado para um novo arquivo
		 * presente no diretório de deploy de aplicações no motor de execução
		 * EMML
		 */
		String fileExecute = FacesContext.getCurrentInstance()
				.getExternalContext().getInitParameter("EMMLEnginePath")
				+ "/tempmashup.emml";
		copyFile(filepath, fileExecute);

		try {

			// Chamada à aplicação no motor de execução EMML
			URL url = new URL(FacesContext.getCurrentInstance()
					.getExternalContext().getInitParameter("EMMLEngineURL")
					+ "tempmashup");
			InputStream execute = url.openStream();

			/*
			 * Conversão do resultado da execução do mashup para um objeto EEML,
			 * conforme o contrato do protocolo
			 */
			Eeml_Contract contract = eemlManager
					.read_eemlFromInputStream(execute);
			eeml = eemlManager.eemlContractToDomain(contract);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		return eeml;
	}

	// FIXME Este metodo futuramente sera removido desta classe
	private void createTempMashup(List<String> feedsWebSites, String filepath) {
		StringBuilder sb = new StringBuilder();

		// Cabeçalho do mashup EMML
		String header = "<mashup"
				+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
				+ " xsi:schemaLocation=\"http://www.openemml.org/2009-04-15/EMMLSchema"
				+ " ../schemas/EMMLSpec.xsd\""
				+ " xmlns=\"http://www.openemml.org/2009-04-15/EMMLSchema\""
				+ " name=\"tempmashup\">\n";
		sb.append(header);

		// Declaração da variável de saída do mashup EMML
		sb.append("\n\t<output name=\"result\" type=\"document\" />\n");

		int numfeeds = feedsWebSites.size();
		if (numfeeds > 0) {
			/*
			 * Construção das tags referentes às variáveis de entrada do mashup
			 * (<variable>), uma para cada feed
			 */
			sb.append("\n\t<variables>\n");
			for (int i = 0; i < numfeeds; i++) {
				sb.append("\t\t<variable name=\"feed" + (i + 1)
						+ "\" type=\"document\" />\n");
			}
			sb.append("\t</variables>\n\n");

			/*
			 * Construção das tags referentes à obtenção via requisição HTTP GET
			 * do conteúdo dos feeds (<directinvoke>), conteúdo esse que será
			 * armazenado nas respectivas variáveis referentes aos feeds
			 */
			for (int i = 0; i < numfeeds; i++) {
				sb.append("\t<directinvoke endpoint=\"" + feedsWebSites.get(i)
						+ "\" method=\"GET\" outputvariable=\"$feed" + (i + 1)
						+ "\" />\n");
			}

			// Agregação dos conteúdos dos feeds
			if (numfeeds == 1) {
				String assign = "\n\t<assign fromvariable=\"$feed1\""
						+ " outputvariable=\"$result\" />";
				sb.append(assign);
			} else {
				String merge = "\n\t<merge inputvariables=\"";
				for (int i = 0; i < numfeeds; i++) {
					merge += (i == (numfeeds - 1)) ? ("$feed" + (i + 1) + "\" ")
							: ("$feed" + (i + 1) + ", ");
				}
				merge += "outputvariable=\"$result\" />";
				sb.append(merge);
			}
		}

		sb.append("\n</mashup>");

		try {
			// Operação de escrita do arquivo temporario
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filepath), "ISO-8859-1"));
			out.write(sb.toString());
			out.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
