var scada = scada || {};

/* Core taken from
 * https://github.com/addyosmani/pubsubz/blob/master/pubsubz.js
 */

scada.Notificator = (function() {
	var topics = {},
		subUid = -1,
		shelf = {},
		publish = function(topic, args) {
		 	if (!topics[topic]) {
	            return false;
	        }
	        
            var subscribers = topics[topic],
                len = subscribers ? subscribers.length : 0;

            while (len--) {
                subscribers[len].func(args);
            }

	        return true;
		},
		publishNotification = function(topic, notification, shelfStoreFunc) {
			if (notification['projectKey']['projectId'] == null || notification['projectKey']['customizationId'] == null) {
				return false;
			}
			
			shelfStoreFunc(notification);
			return publish(topic, notification);
		},
		findProjectCommunicationStatus = function(projectId, customizationId) {
			var all = shelf['project.comm.status'];
			for (var i = 0, j = all.length; i < j; i++) {
				if (all[i]['projectKey']['projectId'] == projectId && all[i]['projectKey']['customizationId'] == customizationId) {
					return all[i];
				}
			}
			return false;
		},
		storeProjectCommunicationStatus = function(n) {
			if (!shelf['project.comm.status']) {
				shelf['project.comm.status'] = [];
			}
			
			var found = findProjectCommunicationStatus(n['projectKey']['projectId'], n['projectKey']['customizationId'])
			found ?	shelf['project.comm.status'].splice(shelf['project.comm.status'].indexOf(found), 1, n) : shelf['project.comm.status'].push(n);
		},
		publishProjectCommunicationStatus = function(notification) {
			return publishNotification('project.comm.status', notification, storeProjectCommunicationStatus);			
		},
		findConnectionStatus = function(projectId, customizationId, connectionId) {
			var key = JSON.stringify({projectId: projectId, customizationId: customizationId});
			if (!shelf['connection.status'][key]) {
				return false;
			}
			
			var all = shelf['connection.status'][key];
			for (var i = 0, j = all.length; i < j; i++) {
				if (all[i]['connection']['connectionKey']['connectionId'] == connectionId) {
					return all[i];
				}
			}
			
			return false;
		},
		storeConnectionStatus = function(n) {
			if (!shelf['connection.status']) {
				shelf['connection.status'] = {};
			}
		
			var key = JSON.stringify({projectId: n['projectKey']['projectId'], customizationId: n['projectKey']['customizationId']});
			var found = findConnectionStatus(n['projectKey']['projectId'], n['projectKey']['customizationId'], n['connection']['connectionKey']['connectionId']);
			if (found) {
				shelf['connection.status'][key].splice(shelf['connection.status'][key].indexOf(found), 1, n);
			} else {
				if (!shelf['connection.status'][key]) {
					shelf['connection.status'][key] = [];
				}
				shelf['connection.status'][key].push(n);
			}
		},
		publishConnectionStatus = function(notification) {
			return publishNotification('connection.status', notification, storeConnectionStatus);
		},
		findVariablesChanged = function(projectId, customizationId) {
			var key = JSON.stringify({projectId: projectId, customizationId: customizationId});
			if (!shelf['variables.changed'][key]) {
				return false;
			}
			
			return shelf['variables.changed'][key];
		},
		storeVariablesChanged = function(n) {
			if (!shelf['variables.changed']) {
				shelf['variables.changed'] = {};
			}
			
			var all = findVariablesChanged(n['projectKey']['projectId'], n['projectKey']['customizationId']);
			if (!all) {
				var key = JSON.stringify({projectId: n['projectKey']['projectId'], customizationId: n['projectKey']['customizationId']});
				all = shelf['variables.changed'][key] = [];
			}
			
			var found;
			for (var k = 0, p = n['variables'].length; k < p; k++) {
				found = false;
				for (var i = 0, j = all.length; i < j; i++) {
					if (n['variables'][k].variable.variableKey.variableId == all[i].variable.variableKey.variableId) {
						all[i] = n['variables'][k];
						found = true;
						break;
					}
				}

				if (!found) {
					all.push(n['variables'][k]);
				}
			}
		},
		publishVariablesChanged = function(notification) {
			return publishNotification('variables.changed', notification, storeVariablesChanged);
		},
		findProjectAlarmStatus = function(projectId, customizationId) {
			var all = shelf['project.alarm.status'];
			for (var i = 0, j = all.length; i < j; i++) {
				if (all[i]['projectKey']['projectId'] == projectId && all[i]['projectKey']['customizationId'] == customizationId) {
					return all[i];
				}
			}
			return false;
		},
		storeProjectAlarmStatus = function(n) {
			if (!shelf['project.alarm.status']) {
				shelf['project.alarm.status'] = [];
			}
			
			var found = findProjectAlarmStatus(n['projectKey']['projectId'], n['projectKey']['customizationId'])
			found ?	shelf['project.alarm.status'].splice(shelf['project.alarm.status'].indexOf(found), 1, n) : shelf['project.alarm.status'].push(n);
		},
		publishProjectAlarmStatus = function(notification) {
			return publishNotification('project.alarm.status', notification, storeProjectAlarmStatus);			
		},
		findAlarmStatus = function(projectId, customizationId, alarmId) {
			var key = JSON.stringify({projectId: projectId, customizationId: customizationId});
			if (!shelf['alarm.status'][key]) {
				return false;
			}
			
			var all = shelf['alarm.status'][key];
			for (var i = 0, j = all.length; i < j; i++) {
				if (all[i]['alarm']['alarmKey']['alarmId'] == alarmId) {
					return all[i];
				}
			}
			
			return false;
		},
		storeAlarmStatus = function(n) {
			if (!shelf['alarm.status']) {
				shelf['alarm.status'] = {};
			}
		
			var key = JSON.stringify({projectId: n['projectKey']['projectId'], customizationId: n['projectKey']['customizationId']});
			var found = findAlarmStatus(n['projectKey']['projectId'], n['projectKey']['customizationId'], n['alarm']['alarmKey']['alarmId']);
			if (found) {
				shelf['alarm.status'][key].splice(shelf['alarm.status'][key].indexOf(found), 1, n);
			} else {
				if (!shelf['alarm.status'][key]) {
					shelf['alarm.status'][key] = [];
				}
				shelf['alarm.status'][key].push(n);
			}
		},
		publishAlarmStatus = function(notification) {
			return publishNotification('alarm.status', notification, storeAlarmStatus);
		},
		findAlarmFiredStatus = function(projectId, customizationId, firedAlarmId) {
			var key = JSON.stringify({projectId: projectId, customizationId: customizationId});
			if (!shelf['alarm.fired.status'][key]) {
				return false;
			}
			
			var all = shelf['alarm.fired.status'][key];
			for (var i = 0, j = all.length; i < j; i++) {
				if (all[i]['id'] == firedAlarmId) {
					return all[i];
				}
			}
			
			return false;
		},
		storeAlarmFiredStatus = function(n) {
			if (!shelf['alarm.fired.status']) {
				shelf['alarm.fired.status'] = {};
			}
		
			var key = JSON.stringify({projectId: n['projectKey']['projectId'], customizationId: n['projectKey']['customizationId']});
			var found = findAlarmFiredStatus(n['projectKey']['projectId'], n['projectKey']['customizationId'], n['id']);
			if (found) {
				shelf['alarm.fired.status'][key].splice(shelf['alarm.fired.status'][key].indexOf(found), 1, n);
			} else {
				if (!shelf['alarm.fired.status'][key]) {
					shelf['alarm.fired.status'][key] = [];
				}
				shelf['alarm.fired.status'][key].push(n);
			}
		},
		publishAlarmFiredStatus = function(notification) {
			return publishNotification('alarm.fired.status', notification, storeAlarmFiredStatus);
		},
		subscribe = function(topic, func) {
			if (!topics[topic]) {
		        topics[topic] = [];
		    }
		
		    var token = (++subUid).toString();
		    topics[topic].push({
		        token: token,
		        func: func
		    });
		    return token;
		},
		subscribeToProjectCommunicationStatus = function(func) {
			subscribe('project.comm.status', func);
		},
		subscribeToConnectionStatus = function(func) {
			subscribe('connection.status', func);
		},
		subscribeToVariablesChanged = function(func) {
			subscribe('variables.changed', func);
		},
		subscribeToProjectAlarmStatus = function(func) {
			subscribe('project.alarm.status', func);
		},
		subscribeToAlarmStatus = function(func) {
			subscribe('alarm.status', func);
		},
		subscribeToAlarmFiredStatus = function(func) {
			subscribe('alarm.fired.status', func);
		},
		unsubscribe = function(token) {
			for (var m in topics) {
	            if (topics[m]) {
	                for (var i = 0, j = topics[m].length; i < j; i++) {
	                    if (topics[m][i].token === token) {
	                        topics[m].splice(i, 1);
	                        return true;
	                    }
	                }
	            }
	        }
	        return false;
		},
		pullProjectCommunicationStatus = function(projectId, customizationId) {
			if (!shelf['project.comm.status'])
				return null;
			
			return findProjectCommunicationStatus(projectId, customizationId);
		},
		pullConnectionStatus = function(projectId, customizationId, connectionId) {
			if (!shelf['connection.status'])
				return null;
			
			return findConnectionStatus(projectId, customizationId, connectionId);
		},
		pullVariablesChanged = function(projectId, customizationId) {
			if (!shelf['variables.changed'])
				return null;
			
			return findVariablesChanged(projectId, customizationId);
		},
		pullProjectAlarmStatus = function(projectId, customizationId) {
			if (!shelf['project.alarm.status'])
				return null;
			
			return findProjectAlarmStatus(projectId, customizationId);
		},
		pullAlarmStatus = function(projectId, customizationId, alarmId) {
			if (!shelf['alarm.status'])
				return null;
			
			return findAlarmStatus(projectId, customizationId, alarmId);
		},
		pullAlarmFiredStatus = function(projectId, customizationId, firedAlarmId) {
			if (!shelf['alarm.fired.status'])
				return null;
			
			return findAlarmFiredStatus(projectId, customizationId, firedAlarmId);
		};
		
	return {
		/*
		 * Communication
		 */
		publishProjectCommunicationStatus: publishProjectCommunicationStatus,
		publishConnectionStatus: publishConnectionStatus,
		publishVariablesChanged: publishVariablesChanged,
		subscribeToProjectCommunicationStatus: subscribeToProjectCommunicationStatus,
		subscribeToConnectionStatus: subscribeToConnectionStatus,
		subscribeToVariablesChanged: subscribeToVariablesChanged,
		unsubscribe: unsubscribe,
		pullProjectCommunicationStatus: pullProjectCommunicationStatus,
		pullConnectionStatus: pullConnectionStatus,
		pullVariablesChanged: pullVariablesChanged,
		/*
		 * Alarm
		 */
		publishProjectAlarmStatus: publishProjectAlarmStatus,
		publishAlarmStatus: publishAlarmStatus,
		publishAlarmFiredStatus: publishAlarmFiredStatus,
		subscribeToProjectAlarmStatus: subscribeToProjectAlarmStatus,
		subscribeToAlarmStatus: subscribeToAlarmStatus,
		subscribeToAlarmFiredStatus: subscribeToAlarmFiredStatus,
		pullProjectAlarmStatus: pullProjectAlarmStatus,
		pullAlarmStatus: pullAlarmStatus,
		pullAlarmFiredStatus: pullAlarmFiredStatus
	};
})();