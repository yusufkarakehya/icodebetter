var scada = scada || {};

scada.wsock = new SockJS("http://" + window.location.host + '/inscada-webapp/scada/wsock');
scada.wsock.onmessage = function(msg) {
	if (msg instanceof Blob) {
		console.debug("Blob received!");
	} else {
		//console.info(JSON.parse(msg.data));
		scada.wsock.processMessage(JSON.parse(msg.data)); //TODO Check parse
	}
};

scada.wsock.onopen = function() {
	console.log('connected')
}

scada.wsock.processMessage = function(msg) {
	var notificator = scada.Notificator;
	if (!notificator) {
		return;
	}
	
	switch (msg.type) {
	case 'error':
		scada.wsock.close();
		break;
	
	/*
	 * Communication
	 */
	case 'connection.status':
		notificator.publishConnectionStatus(msg);
		break;
		
	case 'variables.changed':
		notificator.publishVariablesChanged(msg);
		break;
		
	case 'project.comm.status':
		notificator.publishProjectCommunicationStatus(msg);
		break;
		
	/*
	 * Alarm
	 */
	case 'alarm.status':
		notificator.publishAlarmStatus(msg);
		break;
		
	case 'project.alarm.status':
		notificator.publishProjectAlarmStatus(msg);
		break;
		
	case 'alarm.fired.status':
		notificator.publishAlarmFiredStatus(msg);
		break;
		
	case 'alarm.acked':
		notificator.publishAlarmAcked(msg);
		break;
		
	default:
		break;
	}
};

