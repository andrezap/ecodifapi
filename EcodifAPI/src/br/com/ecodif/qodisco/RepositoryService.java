package br.com.ecodif.qodisco;

import br.com.ecodif.qodisco.RepositoryDao;

public class RepositoryService {
	
	private RepositoryDao repositoryDao;

	public void insertObservation(String repositoryUrl, String sensorName, String data, String observedProperty, String date, String qoCCriterion, int qoCValue, String observationName){
		repositoryDao = new RepositoryDao(repositoryUrl);
		
		repositoryDao.insertObservationResultValue(observationName, data);
		repositoryDao.insertQoCIndicator(observationName, qoCCriterion, ""+qoCValue);
		repositoryDao.insertSensorOutput(observationName, sensorName);
		repositoryDao.insertObservation(observationName, date, sensorName, observedProperty);
	}
	
}
