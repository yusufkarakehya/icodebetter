function findAddressFromStr(adr){
	var geocoder = new google.maps.Geocoder();
	var request = {address: adr};
	var callBack = function(geocoderResults, geocoderStatus) {
	    if(geocoderStatus === 'OK') {
	        var location = geocoderResults[0].geometry.location;
	        showGMap(getLocMsg('js_harita_konumu'),{lat:location.lat(), lng:location.lng()},null);
	    }
	    else{
	    	Ext.Msg.show({title:getLocMsg('js_bilgi'),msg:getLocMsg('js_adres_bulunamadi'),icon:Ext.MessageBox.WARNING})
	    }
	}
	geocoder.geocode(request,callBack);
}

function queryAddress(adr){
	var request = promisManuelAjaxObject();
	request.open("POST", 'http://maps.googleapis.com/maps/api/geocode/json?address='+adr+'&sensor=false', false);
	request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	request.send();
	var json = eval("("+request.responseText+")");
	return json.results;
}


function showGMap(mtitle, mcenter, mmarkers){
	if(!mcenter.marker)mcenter.marker={};
	if(!mcenter.marker.title)mcenter.marker.title='Here';
	
	var options = {
		gmapType: 'map', zoomLevel: 14, 
	    mapConfOpts: ['enableScrollWheelZoom','enableDoubleClickZoom','enableDragging'],
	    mapControls: ['GSmallMapControl','GMapTypeControl','NonExistantControl'],
	    setCenter:mcenter
	};
	
	if(mmarkers && mmarkers.length>0)options.markers=mmarkers;
	
	var map = new Ext.ux.GMapPanel(options);
	/*var path = [
	                 {lng:37.912828, l:32.545971},
	                 {lng:37.91942, lng:32.5371}
	               ]
	
	map.addPolyline(path);*/

	var	mapwin = new Ext.Window({
	    layout: 'fit',modal:true, shadow:false,
	    title: mtitle,
	    width:500, height:500,
	    items: map
	});
	mapwin.show();
	return false;
}