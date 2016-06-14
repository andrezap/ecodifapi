/* Deleting */
delete from Device_Sensor;
update Platform set driver_id = null;
delete from Driver;
delete from Value;
delete from Datapoints;
delete from DataDomain;
delete from Applications_Environments;
delete from Application;
delete from Environment_tag;
delete from Environment;
delete from Location;
delete from CurrentValue;
delete from Eeml;
delete from ConnectedDevice;
delete from Device;
delete from Application;
delete from User;
delete from UserType;
delete from Sensor;
delete from Platform;
delete from Driver;
delete from Unit;

/* UserType */
INSERT INTO UserType (description, role) VALUES ('Fabricante de dispositivo', 'FAB_DISP');
INSERT INTO UserType (description, role) VALUES ('Provedor de dados', 'PROV_DADOS');
INSERT INTO UserType (description, role) VALUES ('Desenvolvedor de aplicações', 'DEV_APP');

/* Sensor */
INSERT INTO Sensor (description, name, precisionSensor, technicalSpecifications) VALUES ('Sensor de Temperatura', 'NTC', '10K', '');
INSERT INTO Sensor (description, name, precisionSensor, technicalSpecifications) VALUES ('Sensor de Localização', 'GPS', '', '');
INSERT INTO Sensor (description, name, precisionSensor, technicalSpecifications) VALUES ('Sensor de Corrente Elétrica', '1500V', '10K', '');

/* Platform */
INSERT INTO Platform(description, platformVersion) VALUES ('Embarcado (Arduino)', '1.0.0');
INSERT INTO Platform(description, platformVersion) VALUES ('Embarcado (Dialog 3G)', '1');
INSERT INTO Platform(description, platformVersion) VALUES ('FroYo (Android)', '2.2');
INSERT INTO Platform(description, platformVersion) VALUES ('Gingerbread (Android)', '2.3');
INSERT INTO Platform(description, platformVersion) VALUES ('Honeycomb (Android)', '3.0');
INSERT INTO Platform(description, platformVersion) VALUES ('Ice Cream Sandwich (Android)', '4.0');
INSERT INTO Platform(description, platformVersion) VALUES ('Jelly Bean (Android)', '4.2');

/* Unit */
INSERT INTO Unit(symbol, type, value) VALUES ('ºC', 'Grau Celsius', '');
INSERT INTO Unit(symbol, type, value) VALUES ('m²', 'Área', '');
INSERT INTO Unit(symbol, type, value) VALUES ('m³', 'Volume', '');
INSERT INTO Unit(symbol, type, value) VALUES ('Kg/m³', 'Densidade', '');
INSERT INTO Unit(symbol, type, value) VALUES ('m/s²', 'Aceleração', '');
INSERT INTO Unit(symbol, type, value) VALUES ('W', 'Potência', 'Wat');
INSERT INTO Unit(symbol, type, value) VALUES ('J', 'Energia', 'Joule');
