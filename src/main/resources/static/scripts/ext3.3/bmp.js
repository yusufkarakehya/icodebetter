
/*
 * Lokal kur çevirme qry_gunluk_kur1 gibi bir result setin datalarının gönderiyoruz
 */

function kur_cevir_local(tutar, src_para_birim, dst_para_birim, doviz_islem_tipi, kur_data){
	
	var src_kur = {};
	var dst_kur = {};
	var katsayi = 1;
	
	if(src_para_birim != dst_para_birim){	
		for(var i=0; i<kur_data.data.length; i++){
			if(kur_data.data[i].para_tip == src_para_birim){
				src_kur = kur_data.data[i];
			}
			if(kur_data.data[i].para_tip == dst_para_birim){
				dst_kur = kur_data.data[i];
			}
		}
		
		switch(1*doviz_islem_tipi){
			case 1:  katsayi= src_kur.dalis / dst_kur.dalis; break;
			case 2:  katsayi= src_kur.dsatis / dst_kur.dsatis; break;
			case 3:  katsayi= src_kur.ealis / dst_kur.ealis; break;
			case 4:  katsayi= src_kur.esatis / dst_kur.esatis; break;
			default: katsayi=1;
		}
	}
	
	return tutar*(isNaN(katsayi) ? 1 : katsayi);
}

function kur_cevir(amount, date, src_para_birim, dst_para_birim, tip, _callObject){
	
	if(!(1*src_para_birim)){ 
		Ext.Msg.show({title: getLocMsg('js_hata'), msg: getLocMsg('js_hata_no_src_kur'), icon: Ext.MessageBox.ERROR}); return;
	}
	
	if(!(1*dst_para_birim)){
		Ext.Msg.show({title: getLocMsg('js_hata'), msg: getLocMsg('js_hata_no_dst_kur'), icon: Ext.MessageBox.ERROR}); return;
	}
	
	if(!(1*tip)){
		//Ext.Msg.show({title: getLocMsg('js_hata'), msg: getLocMsg('js_hata_no_doviz_tip'), icon: Ext.MessageBox.ERROR}); return;
		tip=_app.client_para_islem_tip;
	}
	
	if(date==''){ 
		Ext.Msg.show({title: getLocMsg('js_hata'), msg: getLocMsg('js_hata_no_date'), icon: Ext.MessageBox.ERROR}); return;		
	}

	var result=0.0;
	//date = fmtShortDate(date);
	amount=1*amount;
	 

	if(src_para_birim == dst_para_birim){
		if(typeof _callObject =='function'){} 
		else if(typeof _callObject =='object'){
			if(_callObject.setValue) _callObject.setValue(1*amount);
		}else if(typeof _callObject =='number')_callObject=1;
	}
	else{
		promisRequest({url:'ajaxQueryData?_qid=310', requestWaitMsg:true, params: {xxwork_dt:date,xexchange_bank_tip:_app.exchange_bank_tip}, successCallback:function(j){
			if(!j.data || j.data.length<=0){
				Ext.Msg.show({title: getLocMsg('js_hata'), msg: getLocMsg('js_tarih_kontrol'), icon: Ext.MessageBox.ERROR}); return;	
			}
	        else{
	        	var src_kur = {};
	        	var dst_kur = {};
	        	
	        	for(var i=0; i<j.data.length; i++){
	        		if(j.data[i].para_tip == src_para_birim){
	        			src_kur = j.data[i];
	        		}
	        		if(j.data[i].para_tip == dst_para_birim){
	        			dst_kur = j.data[i];
	        		}
	        	}
	        	
				switch(1*tip){
					case 1:  result= src_kur.dalis / dst_kur.dalis; break;
					case 2:  result= src_kur.dsatis / dst_kur.dsatis; break;
					case 3:  result= src_kur.ealis / dst_kur.ealis; break;
					case 4:  result= src_kur.esatis / dst_kur.esatis; break;
					default: result=1;
				}
				
				if(typeof _callObject =='function'){} 
				else if(typeof _callObject =='object'){
					if(_callObject.setValue) {
						_callObject.setValue(result*amount);
						_callObject.fireEvent('change');
					}
				}else if(typeof _callObject =='number'){alert('_callObject=result*amount='+result*amount);_callObject=result*amount;}
			}
		}});
	}
}
function kur_cevir_teias(amount, date, src_para_birim, dst_para_birim, tip, _callObject){
	
	if(!src_para_birim){ 
		Ext.Msg.show({title: getLocMsg('js_hata'), msg: getLocMsg('js_hata_no_src_kur'), icon: Ext.MessageBox.ERROR}); return;
	}
	 
	if(!dst_para_birim){
		Ext.Msg.show({title: getLocMsg('js_hata'), msg: getLocMsg('js_hata_no_dst_kur'), icon: Ext.MessageBox.ERROR}); return;
	}
	
	if(!(1*tip)){
		//Ext.Msg.show({title: getLocMsg('js_hata'), msg: getLocMsg('js_hata_no_doviz_tip'), icon: Ext.MessageBox.ERROR}); return;
		tip=_app.client_para_islem_tip;
	}
	
	if(date==''){ 
		Ext.Msg.show({title: getLocMsg('js_hata'), msg: getLocMsg('js_hata_no_date'), icon: Ext.MessageBox.ERROR}); return;		
	}

	var result=0.0;
	//date = fmtShortDate(date);
	amount=1*amount;
	var src_kur = {};
	var dst_kur = {};
 
	if(src_para_birim == dst_para_birim){
		if(typeof _callObject =='function'){} 
		else if(typeof _callObject =='object'){
			if(_callObject.setValue) _callObject.setValue(1*amount);
		}else if(typeof _callObject =='number')_callObject=1;
	}
	
	else 
	{
		if(	 src_para_birim=="TRL"){
			src_kur.alis=1;
			src_kur.satis=1;
			src_kur.ealis=1;
			src_kur.esatis=1;
		}
		
		promisRequest({url:'ajaxQueryData?_qid=2687', requestWaitMsg:true, params: {xdoviz_dt:date,xlkp_para_birim:src_para_birim,xlkp_kur_tip:tip*1}, successCallback:function(j){
	//		alert(objProp(j.data[0]));
		//	alert(objProp(src_kur.data))
			if(!src_kur.data)
			if(!j.data || j.data.length<=0){
				Ext.Msg.show({title: getLocMsg('js_hata'), msg: getLocMsg('js_tarih_kontrol'), icon: Ext.MessageBox.ERROR}); return;	
			}
	        else
	        	
	        	
	        for(var i=0; i<j.data.length; i++)/*{
	        		if(j.data[i].para_tip == src_para_birim){*/
	        			src_kur = j.data[i];
	        	/*	}
	        		if(j.data[i].para_tip == dst_para_birim){
	        			dst_kur = j.data[i];
	        		}
	        	}*/
		 			
		
			
			
			
			if(dst_para_birim=="TRL"){
				
				dst_kur.alis=1;
				dst_kur.satis=1;
				dst_kur.ealis=1;
				dst_kur.esatis=1;
				
			}
			
		
		
		
		
		
		promisRequest({url:'ajaxQueryData?_qid=2687', requestWaitMsg:true, params: {xdoviz_dt:date,xlkp_para_birim:dst_para_birim,xlkp_kur_tip:tip*1}, successCallback:function(j){
			
	 
			if(!dst_kur.data)
			 
			if(!j.data || j.data.length<=0){
				Ext.Msg.show({title: getLocMsg('js_hata'), msg: getLocMsg('js_tarih_kontrol'), icon: Ext.MessageBox.ERROR}); return;	
			}
	        else
	        	
	        	
	        for(var i=0; i<j.data.length; i++)/*{
	        		if(j.data[i].para_tip == src_para_birim){*/
	        			dst_kur = j.data[i];
	        	/*	}
	        		if(j.data[i].para_tip == dst_para_birim){
	        			dst_kur = j.data[i];
	        		}
	        		
	        	}*/
		 
			
		
		 
			switch(1*tip){
			case 1:  result= src_kur.alis / dst_kur.alis; break;
			case 2:  result= src_kur.satis / dst_kur.satis; break;
			case 3:  result= src_kur.ealis / dst_kur.ealis; break;
			case 4:  result= src_kur.esatis / dst_kur.esatis; break;
			default: result=1;
			}
 
			if(typeof _callObject =='function'){} 
			else if(typeof _callObject =='object'){
				if(_callObject.setValue) {
					_callObject.setValue(result*amount);
					_callObject.fireEvent('change');
				}
			}
			else if(typeof _callObject =='number'){alert('_callObject=result*amount='+result*amount);_callObject=result*amount;}
					
		}});
		}});
				
				
				
			}
		
	
}
function kur_cevir_sync(amount, date, src_para_birim, dst_para_birim, tip){
	
	if(!(1*src_para_birim)){ 
		Ext.Msg.show({title: getLocMsg('js_hata'), msg: getLocMsg('js_hata_no_src_kur'), icon: Ext.MessageBox.ERROR}); return;
	}
	
	if(!(1*dst_para_birim)){
		Ext.Msg.show({title: getLocMsg('js_hata'), msg: getLocMsg('js_hata_no_dst_kur'), icon: Ext.MessageBox.ERROR}); return;
	}
	
	if(!(1*tip)){
		Ext.Msg.show({title: getLocMsg('js_hata'), msg: getLocMsg('js_hata_no_doviz_tip'), icon: Ext.MessageBox.ERROR}); return;
	}
	
	if(date==''){ 
		Ext.Msg.show({title: getLocMsg('js_hata'), msg: getLocMsg('js_hata_no_date'), icon: Ext.MessageBox.ERROR}); return;		
	}

	var result=0.0;
	//date = fmtShortDate(date);
	amount=1*amount;
	 

	if(src_para_birim == dst_para_birim){
		return 1*amount;
	}
	else{
		var request = promisManuelAjaxObject();
		request.open("POST", 'ajaxQueryData?_qid=310', false);
		request.setRequestHeader("Content-type", "application/x-www-form-urlencoded")
		request.send('xxwork_dt='+ date+'&xexchange_bank_tip='+_app.exchange_bank_tip);
		var j = eval("("+request.responseText+")");
		
		if(!j.data || j.data.length<=0){
			Ext.Msg.show({title: getLocMsg('js_hata'), msg: getLocMsg('js_tarih_kontrol'), icon: Ext.MessageBox.ERROR}); return;	
		}
        else{
        	var src_kur = {};
        	var dst_kur = {};
        	
        	for(var i=0; i<j.data.length; i++){
        		if(j.data[i].para_tip == src_para_birim){
        			src_kur = j.data[i];
        		}
        		if(j.data[i].para_tip == dst_para_birim){
        			dst_kur = j.data[i];
        		}
        	}
        	
			switch(1*tip){
				case 1:  result= src_kur.dalis / dst_kur.dalis; break;
				case 2:  result= src_kur.dsatis / dst_kur.dsatis; break;
				case 3:  result= src_kur.ealis / dst_kur.ealis; break;
				case 4:  result= src_kur.esatis / dst_kur.esatis; break;
				default: result=1;
			}
			
			return result*amount;
		}
	}
}

function kur_cevir_sabit_sync(amount, date, src_para_birim, dst_para_birim, tip){
	
	if(!(1*src_para_birim)){ 
		Ext.Msg.show({title: getLocMsg('js_hata'), msg: getLocMsg('js_hata_no_src_kur'), icon: Ext.MessageBox.ERROR}); return;
	}
	
	if(!(1*dst_para_birim)){
		Ext.Msg.show({title: getLocMsg('js_hata'), msg: getLocMsg('js_hata_no_dst_kur'), icon: Ext.MessageBox.ERROR}); return;
	}
	
	if(!(1*tip)){
		Ext.Msg.show({title: getLocMsg('js_hata'), msg: getLocMsg('js_hata_no_doviz_tip'), icon: Ext.MessageBox.ERROR}); return;
	}
	
	if(date==''){ 
		Ext.Msg.show({title: getLocMsg('js_hata'), msg: getLocMsg('js_hata_no_date'), icon: Ext.MessageBox.ERROR}); return;		
	}

	var result=0.0;
	//date = fmtShortDate(date);
	amount=1*amount;
	 

	if(src_para_birim == dst_para_birim){
		return 1;
	}
	else{
		var request = promisManuelAjaxObject();
		request.open("POST", 'ajaxQueryData?_qid=1546', false);
		request.setRequestHeader("Content-type", "application/x-www-form-urlencoded")
		request.send('xxwork_dt='+ date);
		var j = eval("("+request.responseText+")");
		
		if(!j.data || j.data.length<=0){
			Ext.Msg.show({title: getLocMsg('js_hata'), msg: getLocMsg('js_tarih_kontrol'), icon: Ext.MessageBox.ERROR}); return;	
		}
        else{
        	var src_kur = {};
        	var dst_kur = {};
        	
        	for(var i=0; i<j.data.length; i++){
        		if(j.data[i].para_tip == src_para_birim){
        			src_kur = j.data[i];
        		}
        		if(j.data[i].para_tip == dst_para_birim){
        			dst_kur = j.data[i];
        		}
        	}
        	
			switch(1*tip){
				case 1:  result= src_kur.dalis / dst_kur.dalis; break;
				case 2:  result= src_kur.dsatis / dst_kur.dsatis; break;
				case 3:  result= src_kur.ealis / dst_kur.ealis; break;
				case 4:  result= src_kur.esatis / dst_kur.esatis; break;
				default: result=1;
			}
			
			return result*amount;
		}
	}
}


function firmRenderer(value, metadata, record, rowIndex, colIndex, store, noImage, other_kara_liste_info){
	/*if(value){
		if(record.data.firma_ozel_not){
			metadata.attr += '" ext:qtip="' + '<b>'+record.data.firma_ozel_not +'</b>'+ '"';
		}else if(record.data.ozel_not){	
			metadata.attr += '" ext:qtip="' + '<b>'+record.data.ozel_not +'</b>'+ '"';
		}
	}	
	var img = '';
	if (!noImage && (record.data.firma_kara_liste_id || record.data.firma_kara_liste_id*1>0)){		
		img = ' <img src="../images/custom/no_entry.png" border=0 onclick="mainPanel.loadTab({attributes:{modalWindow:true, href:\'showForm?a=1&_fid=1569&tfirma_kara_liste_id='+ record.data.firma_kara_liste_id+'&xread_only=1\'}});" >&nbsp;';		
	}
	if (record.data.renk_tip){
		return img + '<font face="arial" color="'+record.data.renk_tip+'"><b>'+  value +'</b></font>';
    }else{
	    return img + value;
	}  */
	if(value){
		// firma_kara_liste_id ~ renk_tip ~ ozel_not ~ kara_liste_not ; 
		var firma_kara_liste_id = '0'; 
		var renk_tip = '';
		var ozel_not = '';
		var kara_liste_not = '';
		
		if (other_kara_liste_info){
			var x=other_kara_liste_info.split('~');
			firma_kara_liste_id = x[0]; 
			renk_tip = x[1];
			ozel_not = x[2];
			kara_liste_not = x[3];
		}
		else if (record.data.kara_liste_info){
			var x=record.data.kara_liste_info.split('~');
			firma_kara_liste_id = x[0]; 
			renk_tip = x[1];
			ozel_not = x[2];
			kara_liste_not = x[3];
		}
		ozel_not=ozel_not.replace(/\"/g,'\&#34');
		kara_liste_not=kara_liste_not.replace(/\"/g,'\&#34');
		if(firma_kara_liste_id>0 && kara_liste_not != ''){
			metadata.attr += ' ext:qtip="' + '<b>'+kara_liste_not +'</b>'+ '"';
		}else if(ozel_not){	
			metadata.attr += ' ext:qtip="' + '<b>'+ozel_not +'</b>'+ '"';
		}
		
		var img = '';
		if (!noImage && firma_kara_liste_id*1>0){		
			img = ' <img src="../images/custom/no_entry.png" border=0 onclick="mainPanel.loadTab({attributes:{modalWindow:true, href:\'showForm?a=1&_fid=1569&tfirma_kara_liste_id='+ firma_kara_liste_id+'&xread_only=1\'}});" >&nbsp;';		
		}
		if (renk_tip){
			return img + '<font face="arial" color="'+renk_tip+'"><b>'+  value +'</b></font>';
	    }else{
		    return img + value;
		} 
	}else{
		return '';
	}
		
	
}
function fnTblRecEdit4Object(oid,tpk,b){
	if(oid)switch(oid){
	case	1:return fnTblRecEdit(28,tpk,b);//firma
	case	2:return fnTblRecEdit(29,tpk,b);//contact
	case	3:return fnTblRecEdit(30,tpk,b);//lead
	case	4:return fnTblRecEdit(37,tpk,b);//lead_follow
	case	6:return fnTblRecEdit(50,tpk,b);//project
	case	10:return fnTblRecEdit(137,tpk,b);//iş geliştirme
	case	15:return fnTblRecEdit(86,tpk,b);//saha servisleri
	case	25:return fnTblRecEdit(85,tpk,b);//servis sözleşmeleri
	case	29:return fnTblRecEdit(149,tpk,b);//satınalma siparişi
	case	33:return fnTblRecEdit(186,tpk,b);//teknik servis
	case	35:return fnTblRecEdit(190,tpk,b);//hızlı satış
	case	36:return fnTblRecEdit(182,tpk,b);//RMA
	case	50:return fnTblRecEdit(787,tpk,b);//arge
	case	55:return fnTblRecEdit(810,tpk,b);//analiz (lab_analysis)
	case	56:return fnTblRecEdit(818,tpk,b);//numune
	case	40:return fnTblRecEdit(1248,tpk,b);//eğitim etkinliği
	default:alert('not defined for '+oid);return false;
	}
	return false;
}


function vehicleTrack(vid){
	var params={}
	if(vid)params.xvehicle_id=vid;
	promisRequest({url:'ajaxQueryData?_qid=1762', requestWaitMsg:true, params: params, successCallback:function(j){
		if(!j.data || j.data.length<=0)return;
		var qmarkers=[],mcenter=null,min_x=1000,min_y=1000,max_x=-1000,max_y=-1000;
		for(var qi=0;qi<j.data.length;qi++){
			var jq=j.data[qi];
			if(qi==0)mcenter={lat:1*jq.pos_x,lng:1*jq.pos_y,marker: {title: jq.vhc_make_tip_qw_+' - '+jq.vhc_license_plate_no+ '\n'+(jq.track_dttm)}}
			else qmarkers.push({lat:1*jq.pos_x,lng:1*jq.pos_y,marker: {title: jq.vhc_make_tip_qw_+' - '+jq.vhc_license_plate_no+ '\n'+(jq.track_dttm)}});
			if(1*jq.pos_x>max_x)max_x=1*jq.pos_x;if(1*jq.pos_x<min_x)min_x=1*jq.pos_x;
			if(1*jq.pos_y>max_y)max_y=1*jq.pos_y;if(1*jq.pos_y<min_y)min_y=1*jq.pos_y;
		}
		var avg_x=(max_x+min_x)/2, avg_y=(max_y+min_y)/2;
		showGMap('Araç Takip: ' + (j.data.length==1 ? (j.data[0].vhc_make_tip_qw_+' - '+j.data[0].vhc_license_plate_no): (j.data.length +  ' adet')), mcenter, qmarkers);
	}});
}



function userTrack(userId){
	var params={};
	if(userId)params.xuser_id=userId;
	else params.xposition_flag=1;// Tüm kullanıcıları göstermek istediğimizde sadece position bilgisi olanlar gelsin
	promisRequest({url:'ajaxQueryData?_qid=2072', requestWaitMsg:true, params: params, successCallback:function(j){
		if(!j.data || j.data.length<=0)return;
		var qmarkers=[],mcenter=null;//,min_x=1000,min_y=1000,max_x=-1000,max_y=-1000;
		for(var qi=0;qi<j.data.length;qi++){
			var jq=j.data[qi];
			if(qi==0)mcenter={lat:1*jq.pos_x,lng:1*jq.pos_y,marker: {title: jq.dsc+ '\n'+(jq.log_dttm)}};
			else qmarkers.push({lat:1*jq.pos_x,lng:1*jq.pos_y,marker: {title: jq.dsc+ '\n'+(jq.log_dttm)}});
		}
		showGMap('Kullanıcı Takip: ' + (j.data.length==1 ? j.data[0].dsc : (j.data.length +  ' kişi')), mcenter, qmarkers);
	}});
}

//timesheet ile ilgili kontrol yapiliyor eksik girilmis ise doldurulmasi için pencere açiliyor. -----------------------------------------------------------------------------------------------------
function checkStartUpTemplate(){
    if (((_app.remember_case_on_login && _app.remember_case_on_login==1) || (_app.remember_timesheet_on_login && _app.remember_timesheet_on_login==1)) && _scd.userTip==2){
        promisRequest({
            url:'ajaxQueryData?_qid=2265', 
            successCallback:function(json){
                if(json.data && json.data.length>0){
                    if (json.data[0].count_case>0 && _app.remember_case_on_login && _app.remember_case_on_login==1){    
                        mainPanel.loadTab({attributes:{modalWindow:true, id:'remember_case', href:'showPage?_tid=985'}});
                    }
                    
                    if (json.data[0].count_timesheet>0 && _app.remember_timesheet_on_login && _app.remember_timesheet_on_login==1){    
                    	mainPanel.loadTab({attributes:{modalWindow:true, id:'remember_timesheet', href:'showPage?_tid=974'}});
                    }
                }                            
            }
        })
    }
}
/*
//timesheet ile ilgili kontrol yapiliyor eksik girilmis ise doldurulmasi için pencere açiliyor. -----------------------------------------------------------------------------------------------------
function checkTimesheet(){
    if (_app.remember_timesheet_on_login && _app.remember_timesheet_on_login==1 && _scd.userTip==2){
        promisRequest({
            url:'ajaxQueryData?_qid=2159&working_day_filter=2', 
            successCallback:function(j){
                if(j.data && j.data.length>0){
                    var x=j.data[0];
                    if (x.tcount>0){    
                        mainPanel.loadTab({attributes:{modalWindow:true, id:'remember_timesheet', href:'showPage?_tid=974'}});
                    }
                }                            
            }
        })
    }
}*/

function clickTimeSheetWidget(){
    mainPanel.loadTab({attributes:{id:'timesheet', href:'showPage?_tid=41&xcontrol=1&xuser_id='+_scd.userId, iconCls:'', icon:'../images/custom/crm/02_zaman.png'}});
}


function getCompanyLogo(){
    if (_app.main_page_use_company_logo*1==1 && _CompanyLogoFileId && _CompanyLogoFileId*1>0){    
        var imgc=Ext.get("bmpHeader").dom;
        imgc.innerHTML ='<img src="./sf/ppicture_logo.png?_fai='+_CompanyLogoFileId+'" style="width:auto; height:32px;padding:1px 0px 0px 3px;">';
        imgc.style.backgroundImage="";
    }    
}



/* Konteyner Check Digit Hesabı ISO 6346 */

var containerNoAlphabetValues = {
	"A" : 10,  	
	"B" : 12,	
	"C" : 13,	
	"D" : 14,	
	"E" : 15,	
	"F" : 16,	
	"G" : 17,	
	"H" : 18,	
	"I" : 19,	
	"J" : 20,	
	"K" : 21,	
	"L" : 23,	
	"M" : 24,
	"N" : 25,
	"O" : 26,
	"P" : 27,
	"Q" : 28,	
	"R" : 29,	
	"S" : 30,	
	"T" : 31,
	"U" : 32,	
	"V" : 34,	
	"W" : 35,	
	"X" : 36,	
	"Y" : 37,	
	"Z"	: 38	
};

function containerCheckDigitFinder(prefix, serial_no){
	var counter = 0;
	var sum = 0;

	for(var i=0; i<prefix.length; i++){
		sum += containerNoAlphabetValues[prefix[i]]*Math.pow(2,counter);
		counter++;
	}
	
	for(var i=0; i<serial_no.length; i++){
		sum += serial_no[i]*Math.pow(2,counter);
		counter++;
	}
	return sum%11;
}

function containerCheckDigitController(prefix, serial_no, check_digit){
	var result = containerCheckDigitFinder(prefix, serial_no);
	return result == check_digit ? true : false ; 
}

/*
 * Muhasebe table_id lere ait query ve param döndüren fonksiyon
 * Şimdilik sadece BA-BS olayını sağlamak için firmalar var.
 * Sonradan belki başka şeyler gerekirse değiştirilir.
 */

function getQueryParams4Muh(table_id, table_pk){
	var result = false;
	switch (table_id) {
	case 28:
		result = {_qid:42};
		if(table_pk)result.xid = table_pk;
		break;
	default:
		result = {_qid:42};
		if(table_pk)result.xid = table_pk;		
		break;
	}
	return result;
}

/* Muhasebeyle ilgili kısa yol tuşlarının çalışması için f tuşarıyla ilgili iptaller */

document.onkeydown = function(event){
    var x = event.which || event.keyCode;
	if (x > 116 && x < 124){
		event.cancelBubble = true;
		event.returnValue = false;
		event.keyCode = false;
		return false;
	}
}

var Money2Text = new function(){
	// Türkçe 
	this.oneToTwentyTr = new Array("","BİR", "İKİ", "ÜÇ", "DÖRT", "BEŞ", "ALTI", "YEDİ", "SEKİZ", "DOKUZ", "ON", "ONBİR", "ONİKİ", "ONÜÇ", "ONDÖRT", "ONBEŞ", "ONALTI", "ONYEDİ", "ONSEKİZ", "ONDOKUZ", "");
	this.tensTr        = new Array("", "ON", "YİRMİ", "OTUZ", "KIRK", "ELLİ", "ALTMIŞ", "YETMİŞ", "SEKSEN", "DOKSAN");
	this.hundredsTr    = new Array("", "YÜZ", "İKİYÜZ", "ÜÇYÜZ", "DÖRTYÜZ", "BEŞYÜZ", "ALTIYÜZ", "YEDİYÜZ", "SEKİZYÜZ", "DOKUZYÜZ");
	this.carpanTr      = new Array("", "BİN", "MİLYON", "MİLYAR");
	// İngilizce
	this.oneToTwentyEn = new Array("", "ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE", "TEN", "ELEVEN", "TWELVE", "THIRTEEN", "FOURTEEN", "FIFTEEN", "SIXTEEN", "SEVENTEEN", "EIGHTEEN", "NINETEEN", "" );
	this.tensEn        = new Array("", "TEN", "TWENTY", "THIRTY", "FOURTY", "FIFTY", "SIXTY", "SEVENTY", "EIGHTY", "NINETY");
	this.hundredsEn    = new Array("", "ONE HUNDRED", "TWO HUNDRED", "THREE HUNDRED", "FOUR HUNDRED", "FIVE HUNDRED", "SIX HUNDRED", "SEVEN HUNDRED", "EIGHT HUNDRED", "NINE HUNDRED");
	this.carpanEn      = new Array("", "THOUSAND", "MILLION", "BILLION");
	// Almanca
	this.oneToTwentyDe = new Array("", "EINS", "ZWEI", "DREI", "VIER", "FÜNF", "SECHS", "SIEBEN", "ACHT", "NEUN", "ZEHN", "ELF", "ZWÖLF", "DREIZEHN", "VIERZEHN", "FÜNFZEHN", "SECHZEHN", "SIEBZEHN", "ACHTZEHN", "NEUNZEHN", "");
	this.tensDe        = new Array("", "ZEHN", "ZWANZIG", "DREIBIG", "VIERZIG", "FÜNFZIG", "SECHZIG", "SIEBZIG", "ACHTZIG", "NEUNZIG");
	this.hundredsDe    = new Array("", "EINS HUNDERT", "ZWEI HUNDERT", "DREI HUNDERT", "VIER HUNDERT", "FÜNF HUNDERT", "SECHS HUNDERT", "SIEBEN HUNDERT", "ACHT HUNDERT", "NEUN HUNDERT");
	this.carpanDe      = new Array("", "TAUSEND", "MILLION", "MILLIARDE");

	this.convertToLocalMoney = function(money){
		if(money.indexOf("TRL") > -1){
		money = money.replace("TRL","TL");
		}
		return money;
	}

	this.convertGroup = function(number, locale){
		var oneToTwenty = new Array();
		var tens = new Array();
		var hundreds = new Array();
				var yuz= "";
			var text= "";

		if(locale == "tr"){
					oneToTwenty = this.oneToTwentyTr;
					tens = this.tensTr;
					hundreds = this.hundredsTr;
					yuz = "YÜZ";
		} else if(locale == "en"){
		oneToTwenty = this.oneToTwentyEn;
					tens = this.tensEn;
					hundreds = this.hundredsEn;
					yuz = "HUNDRED";
		} else {
		oneToTwenty = this.oneToTwentyDe;
					tens = this.tensDe;
					hundreds = this.hundredsDe;
					yuz = "HUNDERT";
		}

		if (number % 100 < 20) {
			text = oneToTwenty[number % 100];
			number = Math.trunc(number / 100);
		} else {
			text = oneToTwenty[number % 10];
			number = Math.trunc(number/10);
			text = tens[number % 10] + text;
			number = Math.trunc(number/10);
		}
		if (number == 0) {
			return text;
		}
		if (number == 1) {
			number--;
		}

		return oneToTwenty[number] + yuz + text;
	}

	this.getCarpanText = function(carpan, locale){
			var text = "";
			if(locale == "tr" ){
				text = this.carpanTr[carpan];

			} else if(locale == "en"){
				text = this.carpanEn[carpan];
			}
			else{
				text = this.carpanDe[carpan];
			}
			return text;
	}

	this.convert = function(number, locale) {
		var text = "";
		var carpanText = "";
		var i = 0;
		var carpan = Array(1000, 1000000, 1000000000, 1000000000000);

		while(i == 0 || carpan[i-1] <= number){
			if(i == 0 && number % carpan[i] != 0){
				text = this.convertGroup((number % carpan[i]), locale);
			}
			if(i > 0){
				var sayi = Math.trunc((number % carpan[i]) / carpan[i-1]);
				if(sayi > 0){
					if(!(i == 1 && sayi == 1)){
						text = this.convertGroup(sayi, locale)+this.getCarpanText(i, locale)+text;
					}
					else{
						text = this.getCarpanText(i, locale)+text;
					}
				}
			}
			i++;
		}
		return text;
	}

	this.getKuruslar = function(moneyType) {
		var kurus = {"TRL":"KURUŞ", "EUR":"CENT", "USD":"CENT", "GBP":"PENI"};
		return kurus[moneyType];
	}
	
	this.getZero = function(locale){
		var zero = {"tr":"SIFIR","en":"ZERO","de":"NULL"};
		return zero[locale];
	}

	this.convertStr = function(money, para_birimi, locale){
		var paraAciklama = {"TRL":"TÜRK LİRASI", "EUR":"EURO", "USD":"ABD DOLARI", "GBP":"İNGİLİZ STERLİNİ"};
		var texttotal = "";
		money = money||0;
		if(money == 0){
			texttotal = this.getZero(locale);
		} else {
			if(money > 1) {
				var moneyTypeDsc="";
				texttotal = this.convert(Math.trunc(money),locale) + " "+this.convertToLocalMoney(paraAciklama[para_birimi]);
			}
			if(money != Math.trunc(money)){ // Kuruşlar varsa
				texttotal += " "+this.convert(Math.trunc(parseFloat(money-Math.trunc(money)).toFixed(2)*100),locale) + " "+this.getKuruslar(para_birimi, locale);
			}
		}
		return texttotal.toUpperCase();
	}
}