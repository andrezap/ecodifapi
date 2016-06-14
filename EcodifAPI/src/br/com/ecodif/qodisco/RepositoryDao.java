package br.com.ecodif.qodisco;

import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;

public class RepositoryDao {

	private String repositoryUrl;
	
	public RepositoryDao(String repositoryUrl){
		this.repositoryUrl = repositoryUrl;
	}
	
	public void insertObservationResultValue(String observationName, String data){
		StringBuilder sb = new StringBuilder();
		sb.append("PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#>");
		
		sb.append(" INSERT DATA { ");
		sb.append(" <http://consiste.dimap.ufrn.br/ontologies/qodisco/observation#" + observationName + "_resultvalue> a ssn:ObservationValue ;");
		sb.append(" ssn:hasQuantityValue " + data + " . }");
		
		System.out.println(sb.toString());
		
		UpdateRequest request = UpdateFactory.create(sb.toString());
		UpdateProcessor proc = UpdateExecutionFactory.createRemote(request, repositoryUrl + "/update");
		proc.execute();
	}
	
	public void insertQoCIndicator(String observationName, String qoCCriterion, String qoCValue){
		StringBuilder sb = new StringBuilder();
		sb.append("PREFIX qodisco: <http://consiste.dimap.ufrn.br/ontologies/qodisco#>");
		
		sb.append(" INSERT DATA { ");
		sb.append(" <http://consiste.dimap.ufrn.br/ontologies/qodisco/observation#"+ observationName+"_qocindicator> a qodisco:QoCIndicator ;");
		sb.append(" qodisco:has_qoc_criterion <" + qoCCriterion + "> ; ");
		sb.append(" qodisco:has_qoc_value " + qoCValue + " . } ");
		
		System.out.println(sb.toString());
		
		UpdateRequest request = UpdateFactory.create(sb.toString());
		UpdateProcessor proc = UpdateExecutionFactory.createRemote(request, repositoryUrl + "/update");
		proc.execute();
	}
	
	public void insertSensorOutput(String observationName, String sensorName){
		StringBuilder sb = new StringBuilder();
		sb.append("PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#>");
		sb.append(" PREFIX qodisco: <http://consiste.dimap.ufrn.br/ontologies/qodisco#>");
		
		sb.append(" INSERT DATA { ");
		sb.append(" <http://consiste.dimap.ufrn.br/ontologies/qodisco#observation"+observationName+"_output> a ssn:SensorOutput ;");
		sb.append(" ssn:isProducedBy <http://consiste.dimap.ufrn.br/ontologies/qodisco#"+sensorName+"> ; ");
		sb.append(" qodisco:has_qoc <http://consiste.dimap.ufrn.br/ontologies/qodisco#observation#" + observationName +  "_qocindicator> ; ");
		sb.append(" ssn:hasValue <http://consiste.dimap.ufrn.br/ontologies/qodisco#observation#"+ observationName+ "_resultvalue> . } ");
		
		System.out.println(sb.toString());
		
		UpdateRequest request = UpdateFactory.create(sb.toString());
		UpdateProcessor proc = UpdateExecutionFactory.createRemote(request, repositoryUrl+"/update");
		proc.execute();
	}
	
	public void insertObservation(String observationName, String date, String sensorName, String observedProperty){
		StringBuilder sb = new StringBuilder();
		sb.append("PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#>");
		
		sb.append(" INSERT DATA { ");
		sb.append(" <http://consiste.dimap.ufrn.br/ontologies/qodisco#observation"+observationName+"> a ssn:Observation ;");
		sb.append(" ssn:observationResultTime '" + date + "' ; " );
		sb.append(" ssn:observedBy <http://consiste.dimap.ufrn.br/ontologies/qodisco#"+ sensorName + "> ; " );
		sb.append(" ssn:observedProperty <"+ observedProperty + "> ; " );
		sb.append(" ssn:observationResult <http://consiste.dimap.ufrn.br/ontologies/qodisco/observation#"+ observationName+ "_output> . }");
		
		System.out.println(sb.toString());
		
		UpdateRequest request = UpdateFactory.create(sb.toString());
		UpdateProcessor proc = UpdateExecutionFactory.createRemote(request, repositoryUrl+"/update");
		proc.execute();
	}
	
}
