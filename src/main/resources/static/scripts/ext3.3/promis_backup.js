if(typeof(_localeMsg) == 'undefined')_localeMsg = {};

function promisManuelAjaxObject() //bu Fonksiyon kullanilmiyor. fakat ileride senkron islemler icin kullanilabilir.(27/12/2012 itibariyle de kur_cevir_sync de kullanılıyor)
{
 var xmlhttp = null;	
 try
 {
  xmlhttp = new XMLHttpRequest();
 }
 catch(err1)
 {
  var ieXmlHttpVersions = new Array();
  ieXmlHttpVersions[ieXmlHttpVersions.length] = "MSXML2.XMLHttp.7.0";
  ieXmlHttpVersions[ieXmlHttpVersions.length] = "MSXML2.XMLHttp.6.0";
  ieXmlHttpVersions[ieXmlHttpVersions.length] = "MSXML2.XMLHttp.5.0";
  ieXmlHttpVersions[ieXmlHttpVersions.length] = "MSXML2.XMLHttp.4.0";
  ieXmlHttpVersions[ieXmlHttpVersions.length] = "MSXML2.XMLHttp.3.0";
  ieXmlHttpVersions[ieXmlHttpVersions.length] = "MSXML2.XMLHttp";
  ieXmlHttpVersions[ieXmlHttpVersions.length] = "Microsoft.XMLHttp";

  var i;
  for (i=0; i < ieXmlHttpVersions.length; i++)
  {
   try
   {
    xmlhttp = new ActiveXObject(ieXmlHttpVersions[i]);
    break;
   }
   catch (err2)
   {
    Ext.Msg.alert(_localeMsg.js_bilgi, ieXmlHttpVersions[i] + " not supported.");
   }
  }
 }
 return xmlhttp;
}


function objProp(o){
	var t="";
	for(var q in o)t+=o[q] instanceof Function ? q + " = function{}\n" : q + " = " + o[q] + "\n";
	return t;
}


function obj2ArrayString(o,ml){
	if(!ml)ml=0;
	if(ml>3)return "";
	var t="[";
	var b=false;
	for(var qi=0;qi<o.length;qi++){
		var n=o[qi];
		if(n && !(n instanceof Function)){
			if(b)t+=",\n"; else b=true;
			switch(typeof n){
			case 'object':
				if(n instanceof Array){
					t+=obj2ArrayString(n,ml+1);
				} else if(n instanceof Date){
					t+='"' + fmtDateTime(n) + '"';
				} else {
					t+=obj2JsonString(n,ml+1);
				}
				break;
			case 'string':
				t+='"' + n + '"';
				break;
			default:
				t+=n ;
				break;
			}
		}
	}
	t+="]";
	return t;
}

function obj2JsonString(o,ml){
	if(!ml)ml=0;
	if(ml>3)return "";
	
	var t="{";
	var b=false;
	for(var q in o){
		var n=o[q];
		if(n && !(n instanceof Function)){
			if(b)t+=","; else b=true;
			switch(typeof n){
			case 'object':
				if(n instanceof Array){
					t+='"'+q +'" : "' + obj2ArrayString(n,ml+1);
				} else if(n instanceof Date){
					t+='"'+q + '" :"' + fmtDateTime(n) + '"';
				} else {
					t+='"'+q + '":' + obj2JsonString(n,ml+1);
				}
				break;
			case 'string':
				t+='"'+q + '":"' + n + '"';
				break;
			default:
				t+='"'+ q + '" : ' + n ;
				break;
			}
		}
	}
	t+="}";
	return t;
}

function gridStore2JsonString(ds){
	var items=ds.data.items;
	var newItems=[]
	for(var qi=0;qi<items.length;qi++)newItems.push({id:items[qi].id,data:items[qi].json});
	return obj2ArrayString(newItems)

}

(function(){ // Edit-String (Özel Tamamlama) icin TAB silme sorunu
     var old = Ext.form.ComboBox.prototype.initEvents;
     Ext.form.ComboBox.prototype.initEvents = function(){
          old.apply(this, arguments);
          delete this.keyNav.tab;
     };
     Ext.Ajax.defaultHeaders = {'Powered-By': 'ProMIS(r) Team'};
})();

function promisLoadException(a,b,c){
    if(c && c.responseText){
        ajaxErrorHandler(eval("("+c.responseText+")"));
    } else Ext.Msg.show({title:_localeMsg.js_bilgi,msg: _localeMsg.js_no_connection_error,icon: Ext.MessageBox.WARNING})
}

function getScreenSize() {
  var myWidth = 0, myHeight = 0;
  if( typeof( window.innerWidth ) == 'number' ) {
    //Non-IE
    myWidth = window.innerWidth;
    myHeight = window.innerHeight;
  } else if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) {
    //IE 6+ in 'standards compliant mode'
    myWidth = document.documentElement.clientWidth;
    myHeight = document.documentElement.clientHeight;
  } else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) ) {
    //IE 4 compatible
    myWidth = document.body.clientWidth;
    myHeight = document.body.clientHeight;
  }
  return {"width": myWidth, "height": myHeight};
}

function getCookie(c_name){
	if(document.cookie.length>0){
		c_start=document.cookie.indexOf(c_name + "=");
		if (c_start!=-1){
		    c_start=c_start + c_name.length+1;
		    c_end=document.cookie.indexOf(";",c_start);
		    if (c_end==-1) c_end=document.cookie.length;
		    return unescape(document.cookie.substring(c_start,c_end));
		}
	}
	return "";
}

/* IBAN Validation */

Ext.form.VTypes['ibanVal'] = /[a-zA-Z]{2}[0-9]{2}[-\s][0-9]{4}[-\s][0-9]{4}[-\s][0-9]{4}[-\s][0-9]{4}[-\s][0-9]{4}$|[a-zA-Z]{2}[0-9]{22}$/;
Ext.form.VTypes['ibanMask'] = /[-\sA-Za-z0-9]/;
Ext.form.VTypes['ibanText'] = _localeMsg.js_invalid_iban;
Ext.form.VTypes['iban'] = function(v){
	return Ext.form.VTypes['ibanVal'].test(v);
};

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

function disabledCheckBoxHtml(x){
	return	'<img src="../images/custom/'+(x!=0?'':'un')+'checked.gif" border=0>';
}

function accessControlHtml(x){
	return	x ? '<img src="../images/custom/bullet_key.png" border=0>':'';
}

function fileAttachmentHtml(x){
	return	x ? '<img src="../images/custom/bullet_file_attach.png" border=0>':'';
}


function fileAttachmentRenderer(a){
	return function(ax,bx,cx){
		return	ax ? '<img src="../images/custom/bullet_file_attach.png" border=0 onclick="mainPanel.loadTab({attributes:{modalWindow:true, _title_:\''+a.name+'\',href:\'showPage?_tid=518&_gid1=458&_gid458_a=1\',_pk:{tfile_attachment_id:\'file_attachment_id\'},baseParams:{xtable_id:'+a.crudTableId+', xtable_pk:'+cx.id+'}}});">':'';
	};
}

function commentHtml(x){
	return	x ? '<img src="../images/custom/bullet_comment.png" border=0>':'';
}

function pictureHtml(x){
	return	x ? '<img src="../images/custom/bullet_picture.png" border=0>':'';
}

function keywordHtml(x){
	return	x ? '<img src="../images/custom/bullet_keyword.png" border=0>':'';
}

function revisionHtml(x){
	return	x==1 ? '<img src="../images/custom/revision1.png" border=0>':'';
}

function approvalHtml(x,y,z){
	if(!x)return '';
	var str=(x>0) ? '<img src="../images/custom/bullet_approval.gif" border=0> ':'';
	str+='<a href=# onclick="return mainPanel.loadTab({attributes:{modalWindow:true,href:\'showPage?_tid=238&_gid1=530\',baseParams:{xapproval_record_id:'+z.data.pkpkpk_arf_id+'}}})"';
	if(z.data.app_role_ids_qw_ || z.data.app_user_ids_qw_){
		str+=' title=":'+_localeMsg.js_onaylayacaklar;
		var bb=false;
		if(z.data.app_role_ids_qw_){
			str+=' ['+_localeMsg.js_roller+': '+z.data.app_role_ids_qw_+']';
			bb=true;
		}
		if(z.data.app_user_ids_qw_){
			if(bb)str+=",";
			str+=' ['+_localeMsg.js_users+': '+z.data.app_user_ids_qw_+']';
		}
		str+='"';
	}
	str+='>'+z.data.pkpkpk_arf_qw_+'</a>';
	return	str;
}

function wideScreenTooltip(value, metadata, record, rowIndex, colIndex, store){
	if(value){ metadata.attr += ' ext:qtip=" <b>' + Ext.util.Format.htmlEncode(value) + '</b>"';}
	return value;
}



function mailBoxRenderer(a){
	return function(ax,bx,cx){
		return	ax ? '<img src="../images/custom/bullet_mail.gif" border=0 onclick="mainPanel.loadTab({attributes:{modalWindow:true, _iconCls:\'icon-email\', _title_:\''+a.name+'\',href:\'showPage?_tid=518&_gid1=874\',baseParams:{xtable_id:'+a.crudTableId+',xtable_pk:'+cx.id+'}}});">':'';
	};
}

/*
function fmtDecimal(f){
	f=1*f;
	if (f)f+=0.000049;
	if(!f)return '0.0000';
	var s=1*f<0?1:0;
	f+='';
	var x=f.split('.');
	var x1=x[0];
	var x2='.';
	if(x.length>1)
		switch(x[1].length){
		case 0:x2+='0000';
			break;
		case 1:x2+=x[1];x2+='0';
			break;
		case 2:x2+=x[1];
			break;
		default:x2+=x[1].substr(0,4);
		}
	else 
		x2+='0000';
	for(var i=x1.length-3;i>s;i-=3)x1=x1.substr(0,i)+','+x1.substr(i);
	x1+=x2;
	return x1;
}

function fmtDecimalNew(value,digit){
	if(!value)return '0';
	if(!digit)digit=4;	
	var result = Math.round(value*Math.pow(10,digit))/Math.pow(10,digit)+'';
	var s=1*result<0?1:0;
	var x=result.split('.');
	var x1=x[0],x2=x[1];
	for(var i=x1.length-3;i>s;i-=3)x1=x1.substr(0,i)+','+x1.substr(i);
	if(x2 && x2>0) return x1+'.'+x2;
	return x1;	
}

function fmtParaShow(strValue){
	if(strValue){
		strValue = strValue.toString().replace(/\$|\,/g,'');
		dblValue = parseFloat(strValue);
		blnSign = (dblValue == (dblValue = Math.abs(dblValue)));
		dblValue = Math.floor(dblValue*100+0.50000000001);
		intCents = dblValue%100;
		strCents = intCents.toString();
		dblValue = Math.floor(dblValue/100).toString();
		if(intCents<10)
			strCents = "0" + strCents;
		for (var i = 0; i < Math.floor((dblValue.length-(1+i))/3); i++)
			dblValue = dblValue.substring(0,dblValue.length-(4*i+3))+(_app.digit_separator || '.')+
			dblValue.substring(dblValue.length-(4*i+3));
		return (((blnSign)?'':'-') + dblValue + (_app.decimal_separator || ',') + strCents);
	} else return 0;
}*/

function fmtDecimal(value){
	if(!value)return '0';
	var digit=(_app.number_decimal_places*1 || 4);	
	var result = Math.round(value*Math.pow(10,digit))/Math.pow(10,digit)+'';
	var s=1*result<0?1:0;
	var x=result.split('.');
	var x1=x[0],x2=x[1];
	for(var i=x1.length-3;i>s;i-=3)x1=x1.substr(0,i)+(_app.digit_separator || '.')+x1.substr(i);
	if(x2 && x2>0) return x1+(_app.decimal_separator || ',')+x2;
	return x1;	
}

function fmtDecimalNew(value,digit){
	if(!value)return '0';
	if(!digit)digit=(_app.number_decimal_places*1 || 4);	
	var result = Math.round(value*Math.pow(10,digit))/Math.pow(10,digit)+'';
	var s=1*result<0?1:0;
	var x=result.split('.');
	var x1=x[0],x2=x[1];
	for(var i=x1.length-3;i>s;i-=3)x1=x1.substr(0,i)+(_app.digit_separator || '.')+x1.substr(i);
	if(x2 && x2>0) return x1+(_app.decimal_separator || ',')+x2;
	return x1;	
}

function fmtParaShow(value){
	if(!value) value = '0';
	var digit=(_app.money_decimal_places*1 || 4);	
	var result = Math.round(value*Math.pow(10,digit))/Math.pow(10,digit)+'';
	var s=1*result<0?1:0;
	var x=result.split('.');
	var x1=x[0],x2=x[1];
	if(!x2) x2='0';
	
	for (var j=x2.length; j<digit; j++){
		x2 = x2 + '0'; 
	}
	
	for(var i=x1.length-3;i>s;i-=3)x1=x1.substr(0,i)+(_app.digit_separator || '.')+x1.substr(i);
	return x1+(_app.decimal_separator || ',')+x2;
}

function getGridSel (a){
  if(!a || !a._grid || !a._grid.sm.getSelected()){
    Ext.Msg.show({title: _localeMsg.js_commons_info, msg: _localeMsg.js_commons_error_secim, icon: Ext.MessageBox.INFO});
    return null;
  }
  else {
    return a._grid.sm.getSelected();
  }
}

function getMasterGridSel (a){
  if(!a || !a._grid || !a._grid._masterGrid || !a._grid._masterGrid.sm.getSelected()){
    Ext.Msg.show({title: _localeMsg.js_commons_info, msg: _localeMsg.js_commons_error_secim, icon: Ext.MessageBox.INFO});
    return null;
  }
  else {
    return a._grid._masterGrid.sm.getSelected();
  }
}

function fmtFileSize(a){
	if(!a)return '-';;
	a*=1;
	var d='B';
	if(a>1024){a=a/1024;d='KB';}
	if(a>1024){a=a/1024;d='MB';}
	if(a>1024){a=a/1024;d='GB';}
	if(d!='B')a=Math.round(a*10)/10;
	return a+' '+d;
}

function fmtTimeAgo(a){
	if(!a)return '-';;
	a=Math.round((1*a)/1000);
	var d=_localeMsg.js_saniye;
	if(a>60){
		a=Math.round(a/60);d=_localeMsg.js_dakika;
		if(a>60){
			a=Math.round(a/60);d=_localeMsg.js_saat;
			if(a>24){
				a=Math.round(a/24);d=_localeMsg.js_gun;
			}else if(a>15){
				return _localeMsg.js_yaklasik_bir_gun;
			}
		}else if(a>40){
			return _localeMsg.js_yaklasik_bir_saat;
		}
	} else if(a>40){
		return _localeMsg.js_yaklasik_bir_dakika;
	}
	return a+' '+d;
}

function fmtShortDate(x){
	return x ? (x.dateFormat ? x.dateFormat('d/m/Y') : x) : "";
}
Ext.util.Format.fmtShortDate=fmtShortDate;

function fmtDateTime(x){
	return x ? (x.dateFormat ? x.dateFormat('d/m/Y H:i:s') : x) : "";
}
Ext.util.Format.fmtDateTime=fmtDateTime;

function fmtDateTimeWithDay(x,addsec){
	if(addsec){
		return x ? (x.dateFormat ? x.dateFormat('d/m/Y H:i:s D') : x) : "";
	}
	else{
		return x ? (x.dateFormat ? x.dateFormat('d/m/Y H:i D') : x) : "";		
	}
}
Ext.util.Format.fmtDateTimeWithDay=fmtDateTimeWithDay;

function fmtDateTimeWithDay2(x){
	return x ? (x.dateFormat ? x.dateFormat('d/m/Y H:i:s D') : x) : "";
}

Ext.util.Format.fmtDateTimeWithDay2=fmtDateTimeWithDay2;


function buildParams(params, map){
	var bp={};
	for(var key in params){
		var newKey = params[key];
		if(typeof newKey == 'function'){
			bp[key] = newKey(params);
		}else if(newKey.charAt(0)=='!')
			bp[key] = newKey.substring(1);
		else
			bp[key]=map[params[key]];
	}
	return bp;
}

function gcx(w,h,r){
	var l=(screen.width-w)/2;
	var t=(screen.height-h)/2;
	r=r ? 1 : 0;
	return 'toolbar=0,scrollbars=0,location=0,status=1,menubar=0,resizable='+r+',width='+w+',height='+h+',left='+l+',top='+t;
}

function openPopup(url,name,x,y,r){
	var wh=window.open(url,name,gcx(x,y,r));
	if(!wh) Ext.Msg.alert(_localeMsg.js_bilgi, _localeMsg.js_once_popup_engel_kaldir); else wh.focus();
	return false;
}

function grid2grid(gridMaster,gridDetail,params, tp){//tabpanel
	gridDetail.store.baseParams = null;
	gridMaster.getSelectionModel().on("selectionchange",function(a,b,c){
		if(!gridDetail.initialConfig.onlyCommitBtn && gridDetail.initialConfig.editMode)gridDetail.btnEditMode.toggle();
		if(a.hasSelection()){
			if(params || gridDetail._baseParams){
				gridDetail.store.baseParams=Ext.apply(gridDetail._baseParams||{},params ? buildParams(params, a.getSelected().data) :{});
			}
			if(gridDetail.isVisible()){
				if(gridDetail.initialConfig.master_column_id)gridDetail.store.load({add:false,params:gridDetail.store.baseParams,scope:gridDetail.store}); 
				else gridDetail.store.reload(); //Eğer burada hata olursa geri aç 24.06.2013
				
/*				if(false && gridDetail.initialConfig.extraOutMap && gridDetail.initialConfig.extraOutMap.revMstTableId){
					var m = gridDetail.initialConfig.extraOutMap;
					var zz = gridDetail.initialConfig; 
					promisRequest({url:'ajaxGetRevisionInfo?_mtid='+m.revMstTableId+'&_dtid='+m.revDtlTableId+'&_mtpk='+a.getSelected().data[m.revMstTablePKField], successCallback: function(aq,bq,cq){
						var str=aq.dsc || '-';
						str = str.substring(0,15); 
						if(aq.dirty)str='<b style="color:red">'+str+'</b>';
						zz._revDf.setValue(str);
					}});
					
				}
	*/			
				
				//else gridDetail.store.reload({add:false,params:gridDetail.store.baseParams,scope:gridDetail.store});
			} else 
				gridDetail.loadOnShow=true;
		} else {
			gridDetail.store.baseParams = null;
			gridDetail.store.removeAll();
		}
		//???????????????????gridDetail.getSelectionModel().fireEvent("selectionchange",gridDetail.getSelectionModel());
	}); 
	
	if(gridDetail.initialConfig.extraOutMap && gridDetail.initialConfig.extraOutMap.revMstTableId){
		var zz = gridDetail.initialConfig; 
		gridDetail.store.on("load",function(ax,bx,cx){
			zz._revDf.setValue("-");
			if(ax.reader && ax.reader.jsonData && ax.reader.jsonData.extraOutMap){
				var aq=ax.reader.jsonData.extraOutMap;
				var str=aq.revision_dsc || '-';
				str = str.substring(0,17); 
				//if(aq.dirty)str='<b style="color:red">'+str+'</b>';
				zz._revDf.setValue(str);
			}
		});
/*		var m = gridDetail.initialConfig.extraOutMap;
		var zz = gridDetail.initialConfig; 
		promisRequest({url:'ajaxGetRevisionInfo?_mtid='+m.revMstTableId+'&_dtid='+m.revDtlTableId+'&_mtpk='+a.getSelected().data[m.revMstTablePKField], successCallback: function(aq,bq,cq){
			var str=aq.dsc || '-';
			if(aq.dirty)str='<b style="color:red">'+str+'</b>';
			zz._revDf.setValue(str);
		}});
		*/
	}
	
	if(!tp){
		gridDetail.on("show",function(a,b,c){
			if(!gridDetail.initialConfig.onlyCommitBtn && gridDetail.initialConfig.editMode)gridDetail.btnEditMode.toggle();
			if(gridDetail.store.baseParams){
				if(gridDetail.initialConfig.master_column_id)gridDetail.store.load({add:false,params:gridDetail.store.baseParams,scope:gridDetail.store}); 
				else gridDetail.store.reload();
			}
		});
	} 
	else {
		tp.on("show",function(a,b,c){
			if(!gridDetail.initialConfig.onlyCommitBtn && gridDetail.initialConfig.editMode)gridDetail.btnEditMode.toggle();
			if(gridDetail.store.baseParams){
				if(gridDetail.initialConfig.master_column_id)gridDetail.store.load({add:false,params:gridDetail.store.baseParams,scope:gridDetail.store}); 
				else gridDetail.store.reload();
			}
		});
	}
}

function fnShowDetailDialog(a,b){
	/*
	 * TODO memory leak olabilir.
	 */
	var sel = a._grid.sm.getSelected(),dv;
	var window = new Ext.Window({
		title: '',
		id: 'grid_detail_dialog_id',
		width: 900,
		height: 600,
		autoScroll: true,  
		fbar: [{  
		     text: _localeMsg.kapat,  
		     handler: function(){  
		       Ext.getCmp('grid_detail_dialog_id').close();  
		     }  
		   }] ,
		items: [
			dv=new Ext.DataView({
				store: new Ext.data.JsonStore({
					fields: a._grid.ds.reader.meta.fields,
					root: 'data'
				}),
				tpl:a._grid.detailView,
				autoScroll:true,
				itemSelector:'table.mva-detail'
			})
		]
	});
	window.show();			
	dv.store.loadData({data:[sel.json]});
}

function showBulletinDetail(bulletinid){
	mainPanel.loadTab({attributes:{href:'showForm?_fid=1554&a=1&tbulletin_id='+bulletinid+'&sv_btn_visible=0'}});
	return false;
}

function fnClearFilters(a,b){
	a._grid._gp.filters.clearFilters();
} 

function fnTableImport(a,b){
	var im=a.ximport || a._grid.crudFlags.ximport;
	if(typeof im=='boolean'){
		Ext.Msg.alert(_localeMsg.js_bilgi, _localeMsg.js_table_import_setting_error);
		return
	}
	
	var cfg={attributes:{modalWindow:true,id:'git'+a._grid.id,_title_:a._grid.name,
		href:'showPage?_tid=178&_gid1=895&xmaster_table_id='+im.xmaster_table_id+'&xtable_id='+a._grid.crudTableId+(im.xobject_tip ? '&xobject_tip='+im.xobject_tip:''), _grid:a._grid, ximport:im}};
	mainPanel.loadTab(cfg);
}

function showTableChildList(e, vtip, vxid, mtid, mtpk, relId){
//	switch(vtip){} // TODO
//	alert(objProp(event)) screenX, screenY
	if (typeof e == 'undefined' && window.event) { e = window.event; }
	var elx=Ext.get("idLinkRel_"+relId);
	promisRequest({url:'ajaxGetTableRelationData',params:{_tb_id:mtid, _tb_pk:mtpk, _rel_id:relId}, successCallback:function(j){
			var items=[];
			for(var qi=0;qi<j.data.length;qi++)items.push({text:(j.data[qi].dsc.length>100 ? j.data[qi].dsc.substring(0,97)+'...':j.data[qi].dsc), _id: j.data[qi].id, handler:function(ax){fnTblRecEdit(j.queryId,ax._id);}});
			if(j.browseInfo.totalCount>j.browseInfo.fetchCount){
				items.push('-');
				items.push({text:'More....(Toplam '+j.browseInfo.totalCount+' adet)', handler:function(){alert('yakinda')}});
			}
			new Ext.menu.Menu({enableScrolling:false, items:items}).showAt([elx.getX()+16, elx.getY()+16]);
		}
	})
	return false;
}

var recordInfoWindow = null;
function renderTableRecordInfo(j){
	if(!j || !j.dsc)return false;
	var s='<p>';if(j.profile_picture_id)s+=Ext.util.Format.getPic3Mini(j)+' &nbsp;';
	s+='<a href=# style="font-size:16px;color:green" onclick="return fnTblRecEdit('+j.tableId+','+j.tablePk+', true);">'+j.dsc+'</a></p><table border=0 width=100%><tr><td width=70% valign=top>';
	if(j.commentFlag && j.commentCount>0)s+=' &nbsp; <img src="../images/custom/comments.png" title="Yorumlar"> '+(j.commentCount);
	if(j.fileAttachFlag && j.fileAttachCount>0)s+=' &nbsp; <img src="../images/custom/crm/attachment.gif" title="İlişkili Dosyalar"> '+(j.fileAttachCount);
	if(j.accessControlFlag && j.accessControlCount>0)s+=' &nbsp; <img src="../images/custom/record_security.png" title="Kayıt Bazlı Güvenlik"> '+(j.accessControlCount);
	if(j.keywords && j.keywords.length>0)s+=' &nbsp; <img src="../images/custom/keyword.png" title="Anahtar Kelimeler"> '+j.keywords;
//	if(j.relationFlag && j.relationCount>0)s+='&nbsp; <img src="../images/custom/record_security.png" title="İlişkili Kayıtlar"> '+(j.relationCount>0 ? j.relationCount:'');
	s+='</td><td width=30% align=right valign=top>';
	if(j.formSmsMailCount)s+=' &nbsp; <img src="../images/famfam/email.png" title="EPosta/SMS Dönüşümleri"> '+j.formSmsMailCount;
	if(j.conversionCount)s+=' &nbsp; <img src="../images/famfam/cog.png" title="Form Dönüşümleri"> '+j.conversionCount;
	s+='</td></tr></table><hr>';
	var rs=j.parents;
	var ss='';
    for(var qi=rs.length-1;qi>=0;qi--){
        var r=rs[qi];
        if(qi!=rs.length-1)ss+='<br>';
        for(var zi=rs.length-1;zi>qi;zi--)ss+=' &nbsp; &nbsp;';
        ss+='&gt '+(qi!=0 ? (r.tdsc) : ('<b>'+r.tdsc+'</b>'));
        if(r.dsc){
        	var rdsc=r.dsc;if(rdsc.length>300)rdsc=rdsc.substring(0,297)+'...';
        	ss+=(qi!=0 ? ': <a href=# onclick="return fnTblRecEdit('+r.tid+','+r.tpk+', false);">'+rdsc+'</a>':': '+rdsc);// else ss+=': (...)';
        }
    }
    ss = '<div class="dfeed">'+ss+'</div>';
/*    rs=j.parents[0];
    if(typeof rs.tcc!='undefined')ss+=' · ' + ('<a href=# onclick="return fnTblRecComment('+j.tableId+','+j.tablePk+');">'+(!rs.tcc ? 'Yorum Yap':('Yorumlar ('+rs.tcc+')'))+'</a>')
    if(typeof rs.tfc!='undefined')ss+=' · ' + ('<a href=# onclick="return fnNewFileAttachment4Form('+j.tableId+','+j.tablePk+');">'+(!rs.tfc ? 'İlişkili Dosyalar':('İlişkili Dosyalar ('+rs.tfc+')'))+'</a>')
  */  	
    s+=ss+'<p><br>';
    if(j.insert_user_id){
    	s+='<span class="cfeed"> · <a href=# onclick="return openChatWindow('+j.insert_user_id+',\''+j.insert_user_id_qw_+'\',true)">'+j.insert_user_id_qw_+'</a> tarafindan '+j.insert_dttm+' tarihinde kayıt yapılmış';
    	if(j.version_no && j.version_no>1)s+='<br> · <a href=# onclick="return openChatWindow('+j.version_user_id+',\''+j.version_user_id_qw_+'\',true)">'+j.version_user_id_qw_+'</a> tarafindan '+j.version_dttm+' tarihinde son kez değiştirilmiş <br> · toplam '+j.version_no+' kere değiştirilmiş</span><p>'
    }
    	
    rs=j.childs;
    if(!rs || !rs.length)return s;
	ss='<br><b>Alt kırılımlar</b>';
    for(var qi=0;qi<rs.length;qi++){
        var r=rs[qi];
        ss+='<br> · '+ (r.vtip ? '<a href=# id="idLinkRel_'+r.rel_id+'" onclick="return showTableChildList(event,'+r.vtip+','+r.void+','+r.mtbid+','+r.mtbpk+','+r.rel_id+');return false;">'+r.tdsc +'</a>': r.dsc) + ' ('+r.tc + (_app.table_children_max_record_number && 1*r.tc==1*_app.table_children_max_record_number-1 ? '+':'')+' adet)';
    	if(r.tcc)ss+=' &nbsp; <img src="../images/custom/comments.png" title="Yorumlar"> '+(r.tcc);
    	if(r.tfc)ss+=' &nbsp; <img src="../images/custom/crm/attachment.gif" title="İlişkili Dosyalar"> '+(r.tfc);

//        if(r.dsc)ss+=(qi!=0 ? ': '+r.dsc:': <b>'+r.dsc+'</b>');// else ss+=': (...)';
    }
    return s+ss;    	
}


function fnTblRecEdit(tid,tpk,b){
	if(b){
		if(recordInfoWindow && recordInfoWindow.isVisible()){recordInfoWindow.destroy();recordInfoWindow=null;}
		mainPanel.loadTab({attributes:{id:'g-'+tid+'-'+tpk,href:'showForm?_tb_id='+tid+'&_tb_pk='+tpk}});
	} else {
		promisRequest({url:'getTableRecordInfo',params:{_tb_id:tid, _tb_pk:tpk}, successCallback:function(j){
				if(j.dsc){
					if(j.dsc.length>100)j.dsc=j.dsc.substring(0,97)+'...';
					if(recordInfoWindow && recordInfoWindow.isVisible()){
//						recordInfoWindow.destroy();
						recordInfoWindow.update(renderTableRecordInfo(j));
						recordInfoWindow.setTitle(j.parents[0].tdsc);
						recordInfoWindow.setIconClass('icon-folder-explorer');
					} else {
						recordInfoWindow = new Ext.Window({
			        		modal:true, shadow:false,
			        		title:j.parents[0].tdsc,
			        		width: 500, 
			        		autoHeight:true, bodyStyle:'padding:3px;background-color:#FFF',
			        		iconCls:'icon-folder-explorer',
			        		border: false, html: renderTableRecordInfo(j)});
						recordInfoWindow.show();
					}
				} else alert('hata.. geri don')
			}
		})
	}
	return false;
}


function fnOpenUrl(url){
	mainPanel.loadTab({attributes:{href:url}});
	return false;
}

function fnTblRecComment(tid,tpk){
//	mainPanel.loadTab({attributes:{modalWindow:true, href:'showPage?_tid=238&_gid1=457',slideIn:'t',_pk:{tcomment_id:'comment_id'},baseParams:{xtable_id:tid, xtable_pk:tpk}}});
//	mainPanel.loadTab({attributes:{modalWindow:true, href:'showPage?_tid=833&_lvid1=3',slideIn:'t',_pk:{tcomment_id:'comment_id'},baseParams:{xtable_id:tid, xtable_pk:tpk}}});
	mainPanel.loadTab({attributes:{modalWindow:true, href:'showPage?_tid=836',slideIn:'t',_pk:{tcomment_id:'comment_id'},baseParams:{xtable_id:tid, xtable_pk:tpk}}});
	return false;
}

function fnTblRecPicture(tid,tpk){
	alert('fnTblRecPicture');
	//mainPanel.loadTab({attributes:{modalWindow:true, href:'showPage?_tid=238&_gid1=457',_pk:{tcomment_id:'comment_id'},baseParams:{xtable_id:tid, xtable_pk:tpk}}});
	return false;
}
function fnRowBulkEdit(a,b){
	mainPanel.loadTab({attributes:{id:'gu-'+a._grid.id,href:'showForm?_fid=1617&xtable_id='+a._grid.crudTableId, _grid:a._grid}});
}

function fnRowBulkMail(a,b){
	mainPanel.loadTab({attributes:{id:'gum-'+a._grid.id,href:'showForm?_fid=2128&xtable_id='+a._grid.crudTableId, _grid:a._grid}});
}

function fnRowEdit(a,b){
    if(!a._grid.onlyCommitBtn && a._grid.editMode){
    	Ext.Msg.alert(_localeMsg.js_bilgi, _localeMsg.js_yazma_modundan_cikmalisiniz);
    	return;
    }
    if(!a._grid.sm.hasSelection()){
    	Ext.Msg.alert(_localeMsg.js_bilgi,_localeMsg.js_once_birseyler_secmelisiniz);
    	return
    }
    
    if(a._grid.multiSelect){
    }
  	var sel = a._grid.sm.getSelected();
  	var href='showForm?a=1&_fid='+a._grid.crudFormId;
  	var idz='';
	for(var key in a._grid._pk){
		href+="&"+key+"="+sel.data[a._grid._pk[key]];
		idz+=sel.data[a._grid._pk[key]];
	}
  	if(typeof a._grid._postUpdate == 'function'){
  		href = a._grid._postUpdate(sel,href,a); // null donerse acilmayacak
  	} else {
    	if(a._grid._postUpdate)href+="&"+a._grid._postUpdate;
  	}
	if(href){
		var cfg={attributes:{id:'g'+a._grid.id+'-'+idz,href:href, _grid:a._grid}};
		if(a.showModalWindowFlag)cfg.attributes.modalWindow=true;
		mainPanel.loadTab(cfg);
	}
};

function fnRowEdit4Log(a,b){
    if(!a._grid.sm.hasSelection()){
    	Ext.Msg.alert(_localeMsg.js_bilgi,_localeMsg.js_once_birseyler_secmelisiniz);
    	return
    }
  	var sel = a._grid.sm.getSelected();
  	var href='showForm?a=1&_fid='+a._cgrid.crudFormId+'&_log5_dttm='+fmtDateTime(sel.data.log5_dttm);
  	var idz='';
  	var _pk=a._cgrid._pk;
	for(var key in _pk){
		href+="&"+key+"="+sel.data[_pk[key]];
		idz+=sel.data[_pk[key]];
	}
  	if(typeof a._grid._postUpdate == 'function'){
  		href = a._grid._postUpdate(sel,href,a); // null donerse acilmayacak
  	} else {
    	if(a._grid._postUpdate)href+="&"+a._grid._postUpdate;
  	}
	if(href){
		var cfg={attributes:{modalWindow:true,id:'g'+a._grid.id+'-'+idz,href:href, _grid:a._cgrid}};
		mainPanel.loadTab(cfg);
	}
};

function fnDataMoveUpDown(a,b){
    if(!a._grid.onlyCommitBtn && a._grid.editMode){
    	Ext.Msg.alert(_localeMsg.js_bilgi,_localeMsg.js_yazma_modundan_cikmalisiniz);
    	return;
    }
    
	var sel = a._grid.sm.getSelected();
	promisRequest({url:'ajaxExecDbFunc?_did=701&ptable_id='+a._grid.crudTableId+'&ptable_pk='+sel.id+'&pdirection='+a._direction, successDs: a._grid.ds});
}

function fnRowEditDblClick(a,b){
	fnRowEdit({_grid:a.initialConfig},b)
}

function fnRowInsert(a,b){
    if(!a._grid.onlyCommitBtn && a._grid.editMode){
    	Ext.Msg.alert(_localeMsg.js_bilgi,_localeMsg.js_yazma_modundan_cikmalisiniz);
    	return;
    }
  	var sel = a._grid.sm.getSelected();
  	var href='showForm?a=2&_fid='+a._grid.crudFormId;
  	if(typeof a._grid._postInsert == 'function'){
  		href = a._grid._postInsert(sel,href,a); // null donerse acilmayacak
  	} else {
    	if(a._grid._postInsert)href+="&"+a._grid._postInsert;
  	}
	if(href){
		var cfg={attributes:{id:'g'+a._grid.id+'-i',href:href, _grid:a._grid}};
		if(a.showModalWindowFlag)cfg.attributes.modalWindow=true;
		mainPanel.loadTab(cfg);
	}
};
function fnRowCopy(a,b){
    if(!a._grid.sm.hasSelection()){
    	Ext.Msg.alert(_localeMsg.js_bilgi,_localeMsg.js_once_birseyler_secmelisiniz);
    	return
    }
    
    if(a._grid.multiSelect){
    }
  	var sel = a._grid.sm.getSelected();
  	var href='showForm?a=5&_fid='+a._grid.crudFormId;
  	var idz='';
	for(var key in a._grid._pk){
		href+="&"+key+"="+sel.data[a._grid._pk[key]];
		idz+=sel.data[a._grid._pk[key]];
	}
  	if(typeof a._grid._postInsert == 'function'){
  		href = a._grid._postInsert(sel,href,a); // null donerse acilmayacak
  	} else {
    	if(a._grid._postInsert)href+="&"+a._grid._postInsert;
  	}
	if(href){
		var cfg={attributes:{id:'gc'+a._grid.id+'-'+idz,href:href, _grid:a._grid}};
		if(a.showModalWindowFlag)cfg.attributes.modalWindow=true;
		mainPanel.loadTab(cfg);
	}
}
function fnRowDelete(a,b){
    if(!a._grid.sm.hasSelection()){
    	Ext.Msg.alert(_localeMsg.js_bilgi,_localeMsg.js_once_birseyler_secmelisiniz);
    	return
    }
    if(a._grid.multiSelect){
    	var sels=a._grid.sm.getSelections();
	    if(a._grid.editMode){
	    	var sel=null;
	    	for(var zz=0;zz<sels.length;zz++){
	    		sel=sels[zz];
		    	var delItem={};
		    	for(var key in a._grid._pk)delItem[key]=sel.data[a._grid._pk[key]];
		    	a._grid._deletedItems.push(delItem);
		    	a._grid.ds.remove(sel);
		    }
	    	var ds=a._grid.ds || a._grid.store;
	    	var io=ds.indexOf(sel);
	    	ds.remove(sel);
	    	if(ds.getCount()>0){
	    		if(io>=ds.getCount())io=ds.getCount()-1;
	    		a._grid.sm.selectRow(io,false);
	    	}
	    	return;
	    }
	    
	    Ext.MessageBox.confirm(_localeMsg.js_warning, _localeMsg.js_secili_kayitlari_silmek_emin+' ('+sels.length+' '+_localeMsg.js_kayit+')', function(btn){
	        if(btn == 'yes'){
		    	var href='ajaxPostEditGrid?_fid='+a._grid.crudFormId;
		    	var params={_cnt:sels.length};
		    	if(typeof a._grid._postDelete == 'function'){
		      		href = a._grid._postDelete(sels,href,a); // null donerse acilmayacak
		      	}else{
			      	for(var bjk=0;bjk<sels.length;bjk++){ // delete
			      		for(var key in a._grid._pk)params[key+""+(bjk+1)]=sels[bjk].data[a._grid._pk[key]];
			      		params["a"+(bjk+1)]=3;
			      	}
		      	}
		    	if(href)promisRequest({
					url: href,
					params:params,
					successDs: a._grid.ds,
					successCallback:function(j2){
		    			if(j2.logErrors || j2.msgs){
		                	var str='';
		                	if(j2.msgs)str=j2.msgs.join('<br>')+'<p>';
		                	if(j2.logErrors)str+=prepareLogErrors(j2);
		                	Ext.Msg.show({title: _localeMsg.js_bilgi,msg: str,icon: Ext.MessageBox.INFO});
		    			}
		    		}
		    	});
	        }
	    });
    } else {
	    var sel=a._grid.sm.getSelected();
	    if(a._grid.onlyCommitBtn || a._grid.editMode){
	    	var delItem={};
	    	for(var key in a._grid._pk)delItem[key]=sel.data[a._grid._pk[key]];
	    	if(!a._grid._deletedItems)a._grid._deletedItems=[];
	    	a._grid._deletedItems.push(delItem);
	    	var ds=a._grid.ds || a._grid.store;
	    	var io=ds.indexOf(sel);
	    	ds.remove(sel);
	    	if(ds.getCount()>0){
	    		if(io>=ds.getCount())io=ds.getCount()-1;
	    		a._grid.sm.selectRow(io,false);
	    	}
	    	return;
	    }
	    Ext.MessageBox.confirm(_localeMsg.js_warning, _localeMsg.js_secili_kayit_silmek_emin, function(btn){
	        if(btn == 'yes'){
		    	var href='ajaxPostForm?a=3&_fid='+a._grid.crudFormId;
		    	if(typeof a._grid._postDelete == 'function'){
		      		href = a._grid._postDelete(sel,href,a); // null donerse acilmayacak
		      	} else {
			    	for(var key in a._grid._pk)href+="&"+key+"="+sel.data[a._grid._pk[key]];
			    	if(a._grid._postDelete)href+="&"+a._grid._postDelete;
		      	}
		    	if(href)promisRequest({
					url: href,
					successDs: a._grid.ds,
					successCallback:function(j2){
		    			if(j2.logErrors || j2.msgs){
		                	var str='';
		                	if(j2.msgs)str=j2.msgs.join('<br>')+'<p>';
		                	if(j2.logErrors)str+=prepareLogErrors(j2);
		                	Ext.Msg.show({title: _localeMsg.js_bilgi,msg: str,icon: Ext.MessageBox.INFO});
		    			}
		    		}
		    	});
	        }
	    });
    }
};

function fnRightClick(grid, rowIndex, e){
	e.stopEvent();
	grid.getSelectionModel().selectRow(rowIndex);
	var coords = e.getXY();
	grid.messageContextMenu.showAt([coords[0], coords[1]]);
}

function selections2string(selections,seperator){
	if(!selections)return '';
	if(!seperator)seperator='|';
	var str='';
	for(var d=0;d<selections.length;d++)str+=seperator+selections[d].id;
	return str.substring(1);
}

function fnExportGridData(b){
	return function(a){
	    var g=a._grid;
	    if(g.ds.getTotalCount()==0){
	    	Ext.Msg.alert(_localeMsg.js_bilgi,_localeMsg.js_no_data);
	    	return
	    }
	    var cols='';
	    for(var z=0;z<g.columns.length;z++){
	    	if((!g.columns[z].hidden) && (g.columns[z].dataIndex))cols+=';'+g.columns[z].dataIndex+','+g.columns[z].width;
	    }
	    var url='grd/'+g.name+'.'+b+'?_gid='+g.gridId+'&_columns='+cols.substr(1);
	    var vals=g.ds.baseParams;
	    for(var k in vals)url+='&'+k+'='+vals[k];
	    if(g.ds.sortInfo){
	    	if(g.ds.sortInfo.field)url+='&sort='+g.ds.sortInfo.field;
	    	if(g.ds.sortInfo.direction)url+='&dir='+g.ds.sortInfo.direction;
	    }
	    openPopup(url,'_blank',800,600);
	};
}

function fnNewFileAttachmentMail(a){
	var fp = a._form._cfg.formPanel;
	var hasReqestedVersion = true;
	/*var hasReqestedVersion = DetectFlashVer(9, 0, 0); // Bu flash yüklü mü değil mi onu sorguluyor. (major ver, minor ver, revision no)*/
   	if(hasReqestedVersion){
   		var href='showForm?_fid=750&_did=447&table_id=48&table_pk=-1';
   	}
   	else{
   		var href='showForm?a=2&_fid=512&table_id='+a._grid.crudTableId+'&table_pk='+table_pk.substring(1);
   	}
  	mainPanel.loadTab({attributes:{modalWindow:true, id:a._form.formId+'f',href:href, _form: fp, iconCls:'icon-attachment', title:'Dosya Ekle'}});
}

function fnNewFileAttachment(a){
	var hasReqestedVersion = DetectFlashVer(9, 0, 0); // Bu flash yüklü mü değil mi onu sorguluyor. (major ver, minor ver, revision no)
  	var sel = a._grid.sm.getSelected();
    if(!sel){
    	Ext.Msg.alert(_localeMsg.js_bilgi,_localeMsg.js_once_birseyler_secmelisiniz);
    	return
    }
  	var table_pk='';
   	for(var key in a._grid._pk)table_pk+="|"+sel.data[a._grid._pk[key]];
   	if(hasReqestedVersion && (a._grid.gridId*1 != 1082)){ // Acente/Bayi logolarının eklenmesi biraz farklı
   		var href='showForm?_fid=714&_did=447&table_id='+a._grid.crudTableId+'&table_pk='+table_pk.substring(1);
   	}
   	else{
   		var href='showForm?a=2&_fid=43&table_id='+a._grid.crudTableId+'&table_pk='+table_pk.substring(1);
   	}
  	mainPanel.loadTab({attributes:{modalWindow:true, id:a._grid.id+'f',href:href, _grid:a._grid, iconCls:'icon-attachment', title:a._grid.name}});
}

function fnNewFileAttachment4Form(tid,tpk){
	var hasReqestedVersion = DetectFlashVer(9, 0, 0); // Bu flash yüklü mü değil mi onu sorguluyor. (major ver, minor ver, revision no)
   	if(hasReqestedVersion){ // Acente/Bayi logolarının eklenmesi biraz farklı
   		var href='showForm?_fid=714&_did=447&table_id='+tid+'&table_pk='+tpk;
   	}
   	else{
   		var href='showForm?a=2&_fid=43&table_id='+tid+'&table_pk='+tpk;
   	}
  	mainPanel.loadTab({attributes:{modalWindow:true, id:tid+'xf',href:href, iconCls:'icon-attachment', title:'Dosya Ekle'}});
  	return false;
}

function fnFileAttachmentList(a){
  	var sel = a._grid.sm.getSelected();
    if(!sel){
    	Ext.Msg.alert(_localeMsg.js_bilgi,_localeMsg.js_once_birseyler_secmelisiniz);
    	return
    }
  	var table_pk='';
   	for(var key in a._grid._pk)table_pk+="|"+sel.data[a._grid._pk[key]];
	var cfg={attributes:{modalWindow:true, href:'showPage?_tid=518&_gid1=458&_gid458_a=1',_pk:{tfile_attachment_id:'file_attachment_id'},baseParams:{xtable_id:a._grid.crudTableId, xtable_pk:table_pk.substring(1)}}};
	cfg.attributes._title_=(sel.data.dsc ? a._grid.name+': '+sel.data.dsc : a._grid.name);
	mainPanel.loadTab(cfg);
}

function fnCommit(a){
	var params={};
  	var dirtyCount=0;
	if(a._grid.fnCommit){
		params=a._grid.fnCommit(a._grid);
		if(!params)return;
		dirtyCount=params._cnt;
	} else {
	  	var items = a._grid._deletedItems;
	  	if(items)for(var bjk=0;bjk<items.length;bjk++){ // delete
	  		dirtyCount++;
	  		for(var key in items[bjk])params[key+""+dirtyCount]=items[bjk][key];
	  		params["a"+dirtyCount]=3;
	  	}
	  	
	  	items = a._grid.ds.data.items;
	  	if(items)for(var bjk=0;bjk<items.length;bjk++)if(items[bjk].dirty){ // edit
	  		if(a._grid.editGridValidation){
	  			if(a._grid.editGridValidation(items[bjk])===false)return;
	  		}
	  		dirtyCount++;
	
	  		var changes=items[bjk].getChanges();
	  		for(var key in changes){
	  			var valx = changes[key];
	  			params[key+""+dirtyCount]= valx!=null ? (valx instanceof Date ? fmtDateTime(valx):valx) : null;
	  		}
			if(a._grid._insertedItems && a._grid._insertedItems[items[bjk].id]){
				params["a"+dirtyCount]=2;
				if(a._grid._postInsertParams){
					var xparams=null;
					if(typeof a._grid._postInsertParams=='function')xparams=a._grid._postInsertParams(items[bjk]);
					else xparams=a._grid._postInsertParams;
					if(xparams)for(var key in xparams)params[key+dirtyCount]=xparams[key];
				}
			} else {
		  		for(var key in a._grid._pk){
		  			var val=a._grid._pk[key];
		  			params[key+""+dirtyCount]=(val.charAt(0)=='!') ? val.substring(1):items[bjk].data[val];
		  		}
				params["a"+dirtyCount]=1;
			}
	  	}
	}
  	if(dirtyCount>0){
  		params._cnt=dirtyCount;  		
  		Ext.MessageBox.confirm(_localeMsg.js_warning, _localeMsg.js_degisiklik_kayit_emin, function(btn){
	        if(btn == 'yes'){
	      		promisRequest({
	    			url:'ajaxPostEditGrid?_fid='+a._grid.crudFormId,
	    			params:params,
	    			successDs: a._grid.ds,
	    			successCallback:function(j2){
		      			if(a._grid._deletedItems)a._grid._deletedItems=[];
		      			if(a._grid._insertedItems)a._grid._insertedItems=[];
		    			if(j2.logErrors || j2.msgs){
		                	var str='';
		                	if(j2.msgs)str=j2.msgs.join('<br>')+'<p>';
		                	if(j2.logErrors)str+=prepareLogErrors(j2);
		                	Ext.Msg.show({title: _localeMsg.js_bilgi,msg: str,icon: Ext.MessageBox.INFO});
		    			}
	      			}
	    		});
	        }
	    });
  	} else Ext.Msg.alert(_localeMsg.js_bilgi,_localeMsg.js_degisiklik_yok);
}


function fnToggleEditMode(a){	
	
	a._grid.editMode = !a._grid.editMode;
	
  	if(typeof a._grid._postToggleEditMode == 'function'){
  		if(a._grid.editMode && !a._grid._postToggleEditMode(a)){
  			a._grid._gp.btnEditMode.toggle();
  			return null;
  		}
  	}
  	
	if(a._grid.editMode){ // editMode'a geçti
		a._grid._deletedItems=[];
		if(a._grid._gp.btnCommit)a._grid._gp.btnCommit.enable();
	} else {
		if(a._grid._gp.btnCommit)a._grid._gp.btnCommit.disable();
		var modified = false;
		if(a._grid._deletedItems.length>0)modified=true;
		if(!modified){
			var items = a._grid.ds.data.items;
			for(var bjk=0;bjk<items.length;bjk++)if(items[bjk].dirty){modified=true;break;}
		}
		
		if(modified)a._grid.ds.reload();
/*		if(modified){
			if(confirm('Değişiklikleri kaydetmeden çıkmak istediğinizden emin misiniz?'))a._grid.ds.reload();
			else {
				a._grid.editMode = true;
				a.toggle();
			}
		} */
	}
}

function fnGridSetting(a){
	var cfg=null
	if(a._grid.searchForm){
		cfg={attributes:{modalWindow:true, _width_:600, _height_:400, href:'showPage?_tid=543&_gid1=440&_gid3=439&_fid4=998&a=1&tform_id='+a._grid.searchForm.formId+'&_fid2=999&tgrid_id='+a._grid.gridId,_pk1:{tquery_field_id:'query_field_id',tgrid_id:'grid_id'},_pk3:{tform_cell_id:'form_cell_id'},baseParams:{xgrid_id:a._grid.gridId,xform_id:a._grid.searchForm.formId}}};
	} else {
		cfg={attributes:{modalWindow:true, href:'showPage?_tid=543&_gid1=440&_fid2=999&a=1&tgrid_id='+a._grid.gridId,_pk1:{tquery_field_id:'query_field_id',tgrid_id:'grid_id'},baseParams:{xgrid_id:a._grid.gridId}}};
	}
	cfg.attributes._title_= _localeMsg.js_mazgal_ayarlari+' ('+a._grid.name+')';
	mainPanel.loadTab(cfg);
}

function fnGridReportSetting(a){
	if(!a._grid.crudTableId)return false;
	var cfg={attributes:{modalWindow:true, href:'showPage?_tid=543&_gid1=1626',_pk1:{treport_id:'report_id'},baseParams:{xmaster_table_id:a._grid.crudTableId}}};
	cfg.attributes._title_= _localeMsg.report_settings;
	mainPanel.loadTab(cfg);
}



function fnGridPrivilege(a){
	var url='showPage?_tid=543&_gid1=442';
	var attr={modalWindow:true,_pk1:{ttable_field_id:'table_field_id'},baseParams:{xgrid_id:a._grid.gridId,xobject_tip:5,xobject_id:a._grid.gridId},_title_:'Grid Yetkileri ('+a._grid.name+')'};
	var adet=1;
	if(a._grid.extraButtons && a._grid.extraButtons.length>0){
		adet++;
		url+='&_gid'+adet+'=838';
		attr['_pk'+adet]={ttoolbar_item_id:'toolbar_item_id'};
	}
	if(a._grid.menuButtons && a._grid.menuButtons.items.length>0){	
		adet++;
		url+='&_gid'+adet+'=839';
		attr['_pk'+adet]={tmenu_item_id:'menu_item_id'};
	}
	if(a._grid.isMainGrid){
		adet++;
		url+='&_gid'+adet+'=803';
		attr.baseParams.xparent_object_id=a._grid.extraOutMap.tplObjId;
		attr['_pk'+adet]={ttemplate_object_id:'template_object_id'};
	}
	attr.href=url;
	mainPanel.loadTab({attributes:attr});
}

function fnRecordComments(a){//TODO: daha duzgun bir chat interface'i yap
  	var sel = a._grid.sm.getSelected();
    if(!sel){
    	Ext.Msg.alert(_localeMsg.js_bilgi,_localeMsg.js_once_birseyler_secmelisiniz);
    	return
    }
  	var table_pk='';
   	for(var key in a._grid._pk)table_pk+="|"+sel.data[a._grid._pk[key]];
//  	var cfg={attributes:{modalWindow:true, href:'showPage?_tid=238&_gid1=457',slideIn:'t',_pk:{tcomment_id:'comment_id'},baseParams:{xtable_id:a._grid.crudTableId, xtable_pk:table_pk.substring(1)}}};
//	var cfg={attributes:{modalWindow:true, href:'showPage?_tid=830&_dvid1=1',slideIn:'t',_pk:{tcomment_id:'comment_id'},baseParams:{xtable_id:a._grid.crudTableId, xtable_pk:table_pk.substring(1)}}};
//	var cfg={attributes:{modalWindow:true, href:'showPage?_tid=833&_lvid1=3',slideIn:'t',_pk:{tcomment_id:'comment_id'},baseParams:{xtable_id:a._grid.crudTableId, xtable_pk:table_pk.substring(1)}}};
	var cfg={attributes:{modalWindow:true, href:'showPage?_tid=836',slideIn:'t',_pk:{tcomment_id:'comment_id'},baseParams:{xtable_id:a._grid.crudTableId, xtable_pk:table_pk.substring(1)}}};
	cfg.attributes._title_=(sel.data.dsc ? a._grid.name+': '+sel.data.dsc : a._grid.name);
	mainPanel.loadTab(cfg);
}

function fnRecordPictures(a){	
	var sel = a._grid.sm.getSelected();
    if(!sel){
    	Ext.Msg.alert(_localeMsg.js_bilgi,_localeMsg.js_once_birseyler_secmelisiniz);
    	return
    }
  	var table_pk='',table_id;
   	for(var key in a._grid._pk)table_pk+="|"+sel.data[a._grid._pk[key]];
   	table_pk=table_pk.substring(1);
   	table_id=a._grid.crudTableId;
  	var cfg={attributes:{modalWindow:true, href:'showPage?_tid=714&table_id='+table_id+'&table_pk='+table_pk,_pk:{tfile_attachment_id:'file_attachment_id'},baseParams:{xtable_id:table_id, xtable_pk:table_pk}}};
	cfg.attributes._title_=(sel.data.dsc ? a._grid.name+': '+sel.data.dsc : a._grid.name);
	mainPanel.loadTab(cfg);
}

function fnRecordConvertionLog(a){
  	var sel = a._grid.sm.getSelected();
    if(!sel){
    	Ext.Msg.alert(_localeMsg.js_bilgi,_localeMsg.js_once_birseyler_secmelisiniz);
    	return
    }
  	var table_pk='';
  	for(var key in a._grid._pk)table_pk+="|"+sel.data[a._grid._pk[key]]; 	
	var cfg={attributes:{modalWindow:true, href:'showPage?_tid=178&_gid1=1731', baseParams:{xsrc_table_id:a._grid.crudTableId, xsrc_table_pk:table_pk.substring(1)}}};
	cfg.attributes._title_=(sel.data.dsc ? a._grid.name+': '+sel.data.dsc : a._grid.name);
	mainPanel.loadTab(cfg);
}

function fnRecordKeywords(a){
  	var sel = a._grid.sm.getSelected();
    if(!sel){
    	Ext.Msg.alert(_localeMsg.js_bilgi,_localeMsg.js_once_birseyler_secmelisiniz);
    	return
    }
  	var table_pk='';
   	for(var key in a._grid._pk)table_pk+="|"+sel.data[a._grid._pk[key]];
  	var cfg={attributes:{modalWindow:true, href:'showPage?_tid=238&_gid1=54',_pk:{tkeyword_id:'keyword_id',ttable_id:'table_id',ttable_pk:'table_pk'},baseParams:{xtable_id:a._grid.crudTableId, xtable_pk:table_pk.substring(1)}}};
	cfg.attributes._title_=sel.data.dsc ? a._grid.name+': '+sel.data.dsc : a._grid.name;
	mainPanel.loadTab(cfg);
}

function fnSendMail(a){
	var sel=null,xtable_ids = null;
	
    if(a._grid.multiSelect){
    	xtable_ids = selections2string(a._grid.sm.getSelections(),',');
    	sel = a._grid.sm.getSelections()[0];
    	table_relation = '&_tableId='+a._grid.crudTableId+'&_tablePk='+a._grid.sm.getSelections()[0].data.id;// çoklu seçimde ilk kayıtla ilişkilendirilsin.
    } else {
    	sel = a._grid.sm.getSelected();
    	xtable_ids = sel.id;
    }

    var cfg={attributes:{modalWindow:true, href:'showForm?_fid=650&_tableId='+a._grid.crudTableId+'&_tablePk='+sel.id+'&xtable_ids='+xtable_ids}};
	
	if(sel.data.dsc)cfg.attributes._title_=sel.data.dsc;
	mainPanel.loadTab(cfg);
}

/*
function fnSendMail(a){
	var sel=null, sels=null;
    if(a._grid.multiSelect){
    	var sels=a._grid.sm.getSelections();
    	sel = sels[0];
    	sels=selections2string(sels,',');
    } else {
		sel=a._grid.sm.getSelected();
    }
  	var table_pk='', url='';
  	for(var key in a._grid._pk){
  		table_pk+='|'+sel.data[a._grid._pk[key]];
  		url+="&"+a._grid._pk[key]+"="+sel.data[a._grid._pk[key]];
  	}
  	if(sels)table_pk+="&_sels="+sels;
	var cfg={attributes:{modalWindow:true, href:'showForm?_fid=650&_tableId='+a._grid.crudTableId+'&_tablePk='+table_pk.substring(1)+url}};
	if(sel.data.dsc)cfg.attributes._title_=sel.data.dsc;
	mainPanel.loadTab(cfg);
}
*/
function fnSendSms(a){
	var sel=a._grid.sm.getSelected();
  	var table_pk='', url='';
  	for(var key in a._grid._pk){
  		table_pk+='|'+sel.data[a._grid._pk[key]];
  		url+="&"+a._grid._pk[key]+"="+sel.data[a._grid._pk[key]];
  	}
	var cfg={attributes:{modalWindow:true, href:'showForm?_fid=631&_tableId='+a._grid.crudTableId+'&_tablePk='+table_pk.substring(1)+url}};
	if(sel.data.dsc)cfg.attributes._title_=sel.data.dsc;
	mainPanel.loadTab(cfg);
}

function fnRecordPrivileges(a){
  	var sel = a._grid.sm.getSelected();
    if(!sel){
    	Ext.Msg.alert(_localeMsg.js_bilgi,_localeMsg.js_once_birseyler_secmelisiniz);
    	return
    }
	var sel=a._grid.sm.getSelected();
	var cfg={attributes:{modalWindow:true, href:'showPage?_tid=238&_gid1=477&crud_table_id='+a._grid.crudTableId+'&_table_pk='+sel.id,_pk:{access_roles:'access_roles',access_users:'access_users',paccess_flag:'access_flag',paccess_tip:'val',ptable_id:'!'+a._grid.crudTableId,ptable_pk:'!'+sel.id},baseParams:{xtable_id:a._grid.crudTableId, xtable_pk:sel.id}}};
	cfg.attributes._title_=sel.data.dsc ? a._grid.name+': '+sel.data.dsc : a._grid.name;
	mainPanel.loadTab(cfg);
}

function fnRecordDealerPrivilege(a){
  	var sel = a._grid.sm.getSelected();
    if(!sel){
    	Ext.Msg.alert(_localeMsg.js_bilgi,_localeMsg.js_once_birseyler_secmelisiniz);
    	return
    }
	var sel=a._grid.sm.getSelected();
	var cfg={attributes:{modalWindow:true, href:'showPage?_tid=656&xtable_id='+a._grid.crudTableId+'&xtable_pk='+sel.id,_pk:{pview_flag:'view_flag',pupdate_flag:'update_flag',pdealer_id:'dealer_id',ptable_id:'!'+a._grid.crudTableId,ptable_pk:'!'+sel.id},baseParams:{xtable_id:a._grid.crudTableId, xtable_pk:sel.id}}};
	cfg.attributes._title_=sel.data.dsc ? a._grid.name+': '+sel.data.dsc : a._grid.name;
	mainPanel.loadTab(cfg);
}

function buildHelpWindow(cfg){
    win = new Ext.Window({
    	id: cfg.hid,
        layout:'fit',
        width: cfg.hwidth*1,
        height:cfg.hheight*1,
        title: cfg.htitle,
        items : [
            {
            	xtype: 'panel',
            	autoScroll: true,
            	html: '<div style="margin: 5px 5px 5px 5px">'+cfg.hdsc+'</div>' 
            }
        ]
    });
    win.show();
    win.setPagePosition((mainViewport.getWidth()-win.getWidth())/2, (mainViewport.getHeight()-win.getHeight())/2);
}

function fnShowLog4Update(a,b){
	var sel=a._grid.sm.getSelected();
	if(!sel){
		Ext.Msg.alert(_localeMsg.js_bilgi,_localeMsg.js_once_birseyler_secmelisiniz);
    	return;
	}
	var paramz={_vlm:1};
	for(var key in a._grid._pk)paramz[key]=sel.data[a._grid._pk[key]];

	mainPanel.loadTab({attributes:{_title_:_localeMsg.js_duzenle_kaydi+":"+ (sel.data.dsc||_localeMsg.js_kayit),modalWindow:true, _grid:a._grid, href:'showPage?_tid=298&_gid1='+a._grid.gridId,baseParams:Ext.apply(paramz,a._grid.ds.baseParams)}})
}

function fnShowLog4Delete(a,b){
	 mainPanel.loadTab({attributes:{_title_:_localeMsg.js_silinenler_kaydi+":",modalWindow:true, href:'showPage?_tid=298&_gid1='+a._grid.gridId,_grid:a._grid,baseParams:Ext.apply({_vlm:3},a._grid.ds.baseParams)}})
}

function addMoveUpDownButtons(xbuttons, xgrid){
	if(xgrid.crudTableId){
		if(xbuttons.length>0)xbuttons.push('-');
		xbuttons.push({tooltip:_localeMsg.js_yukari, cls:'x-btn-icon x-grid-go-up', disabled:true, _activeOnSelection:true, _grid:xgrid, _direction: -1, handler:fnDataMoveUpDown});
		xbuttons.push({tooltip:_localeMsg.js_asagi, cls:'x-btn-icon x-grid-go-down', disabled:true, _activeOnSelection:true, _grid:xgrid, _direction: 1, handler:fnDataMoveUpDown});
	}
}

function addDefaultCrudButtons(xbuttons, xgrid, modalflag){
	if(xbuttons.length>0)xbuttons.push('-');
	var xbl = xbuttons.length;
	/* crud buttons & import*/
	if(xgrid.crudFlags.insert)xbuttons.push({tooltip:_localeMsg.js_new, cls:'x-btn-icon x-grid-new', showModalWindowFlag: modalflag||false, _activeOnSelection:false, _grid:xgrid, handler:xgrid.fnRowInsert||fnRowInsert});
	if(xgrid.crudFlags.edit)xbuttons.push({tooltip:_localeMsg.js_edit,cls:'x-btn-icon x-grid-edit', disabled:true, showModalWindowFlag: modalflag||false, _activeOnSelection:true, _grid:xgrid, handler:xgrid.fnRowEdit||fnRowEdit});
	if(xgrid.crudFlags.remove)xbuttons.push({tooltip:_localeMsg.js_delete,cls:'x-btn-icon x-grid-delete', disabled:true, _activeOnSelection:true, _grid:xgrid, handler:xgrid.fnRowDelete||fnRowDelete});
	if(xgrid.crudFlags.xcopy)xbuttons.push({tooltip:_localeMsg.js_copy,cls:'x-btn-icon x-grid-copy-record', disabled:true, showModalWindowFlag: modalflag||false, _activeOnSelection:true, _grid:xgrid, handler:xgrid.fnRowCopy||fnRowCopy});
	if(xgrid.crudFlags.ximport){
		if(typeof xgrid.crudFlags.ximport=='object' && typeof xgrid.crudFlags.ximport.length!='undefined'){
			var xmenu=[];
			for(var qi=0;qi<xgrid.crudFlags.ximport.length;qi++)if(!xgrid.crudFlags.ximport[qi].dsc)xmenu.push(xgrid.crudFlags.ximport[qi]); else {
				xmenu.push({text:xgrid.crudFlags.ximport[qi].dsc,cls:xgrid.crudFlags.ximport[qi].cls || '', _activeOnSelection:false, _grid:xgrid, ximport:xgrid.crudFlags.ximport[qi],handler:fnTableImport});
			}
			if(xgrid.extraButtons){
				var bxx=xmenu.length>0;
				for(var qi=0;qi<xgrid.extraButtons.length;qi++)if(xgrid.extraButtons[qi] && xgrid.extraButtons[qi].ref && xgrid.extraButtons[qi].ref.indexOf('../import_')==0){
					if(bxx){bxx=false;xmenu.push('-');}
					xgrid.extraButtons[qi]._grid=xgrid;
					xmenu.push(xgrid.extraButtons[qi]);
					xgrid.extraButtons.splice(qi,1);qi--;
				}
				if(xgrid.extraButtons.length==0)xgrid.extraButtons=undefined;
			}
			xbuttons.push({tooltip:_localeMsg.js_diger_kayitlardan_aktar, cls:'x-btn-icon x-grid-import', _activeOnSelection:false, _grid:xgrid, menu:xmenu});
		} else
			xbuttons.push({tooltip:_localeMsg.js_diger_kayitlardan_aktar, cls:'x-btn-icon x-grid-import', _activeOnSelection:false, _grid:xgrid, handler:fnTableImport});
	}
	/* revision */
	if(xgrid.extraOutMap && xgrid.extraOutMap.revMstTableId){
		if(xbl < xbuttons.length)xbuttons.push('-');
		xbuttons.push({tooltip:_localeMsg.js_revision, cls:'x-btn-icon x-grid-revision', _activeOnSelection:false, _grid:xgrid, menu:[
	       {text:_localeMsg.js_revision_save,_grid:xgrid, handler:function(ax,bx,cx){
	    	   var qstr=prompt(_localeMsg.enter_revision_name,'REV-'+fmtShortDate(new Date()));
	    	   if(qstr){
	    		   promisRequest({
	    			   url:'ajaxSaveRevision', 
	    			   requestWaitMsg:true, 
	    			   params: {_dsc:qstr,_dtid:ax._grid.crudTableId, _mtid:ax._grid._masterGrid.crudTableId, _mtpk:ax._grid._masterGrid.sm.getSelected().id},
	    			   successDs:xgrid.ds
	    		   })
	    	   }
	       }},		                                                                                                   
	       {text:_localeMsg.js_revision_list,_grid:xgrid, handler:function(ax,bx,cx){
	    	   var cfg= {attributes:{modalWindow:true,_title_:ax._grid.name,
	    			href:'showPage?_tid=178&_gid1=1381',baseParams:{xmaster_table_id:ax._grid._masterGrid.crudTableId,xtable_id:ax._grid.crudTableId,xmaster_table_pk:ax._grid._masterGrid.sm.getSelected().id,_grid:ax._grid}}};
	    	   mainPanel.loadTab(cfg);	    	   
	    	}}		                                                                                                   
		]});
		var revDf=new Ext.form.DisplayField({labelSeparator:'',width:100, value:'-'});
		xgrid._revDf=revDf;
		xbuttons.push(revDf);
	}
	/*
	if(xgrid.crudFlags.xrevision){
		if(xbl < xbuttons.length)xbuttons.push('-');
		xbuttons.push({tooltip:_localeMsg.js_revision,cls:'x-btn-icon x-grid-revision', _grid:xgrid, menu:[
	       {text:_localeMsg.js_revision_save,_grid:xgrid, handler:function(ax,bx,cx){
	    	   var qstr=prompt('Revizyon İsmi Giriniz','REV-'+fmtShortDate(new Date()));
	    	   if(qstr){
	    		   promisRequest({url:'ajaxPostForm?_fid=1683&a=2', requestWaitMsg:true, params: {dsc:qstr,table_id:ax._grid.crudTableId, master_table_id:ax._grid._masterGrid.crudTableId, master_table_pk:ax._grid._masterGrid.sm.getSelected().id}})
	    	   }
	       }},		                                                                                                   
	       {text:_localeMsg.js_revision_list,_grid:xgrid, handler:function(ax,bx,cx){
	    	   var cfg= {attributes:{modalWindow:true,_title_:ax._grid.name,
	    			href:'showPage?_tid=178&_gid1=1381',baseParams:{xmaster_table_id:ax._grid._masterGrid.crudTableId,xtable_id:ax._grid.crudTableId,xmaster_table_pk:ax._grid._masterGrid.sm.getSelected().id,_grid:ax._grid}}};
	    	   mainPanel.loadTab(cfg);	    	   
	    	}}		                                                                                                   
		]});
	}
	*/
	if(xgrid.accessControlFlag && xgrid.accessDealerControlFlag){
		xbuttons.push({tooltip:_localeMsg.js_yetkilendirmeler,cls:'x-btn-icon x-grid-record-privilege', disabled:true, _activeOnSelection:true ,menu:[{text:_localeMsg.js_kayit_bazli_yetkilendirme, _grid:xgrid,handler:fnRecordPrivileges},{text:_localeMsg.js_bayi_yetkilendirme, _grid:xgrid,handler:fnRecordDealerPrivilege}]});
	} else {
		if(xgrid.accessControlFlag)xbuttons.push({tooltip:_localeMsg.js_kayit_bazli_yetkilendirme,cls:'x-btn-icon x-grid-record-privilege', disabled:true, _activeOnSelection:true ,_grid:xgrid, handler:fnRecordPrivileges});
		if(xgrid.accessDealerControlFlag)xbuttons.push({tooltip:_localeMsg.js_bayi_yetkilendirme,cls:'x-btn-icon x-grid-record-privilege', disabled:true, _activeOnSelection:true ,_grid:xgrid, handler:fnRecordDealerPrivilege});
	}
	
	if(/*_scd.administratorFlag && */xgrid.logFlags){
		xbuttons.push('-');
		var xmenu=[];
		if(xgrid.logFlags.edit)xmenu.push({text:_localeMsg.js_guncellenme_listesini_goster, _grid:xgrid, handler:fnShowLog4Update});
		if(xgrid.logFlags.remove)xmenu.push({text:_localeMsg.js_show_deleted_records, _grid:xgrid, handler:fnShowLog4Delete});
		xbuttons.push({tooltip:_localeMsg.js_log,cls:'x-btn-icon icon-log', _activeOnSelection:false, _grid:xgrid, menu:xmenu});
	}
}
function openFormSmsMail(tId,tPk,fsmId,fsmFrmId){
	mainPanel.loadTab({attributes:{href:'showForm?_fid=650&_tableId='+tId+'&_tablePk='+tPk+'&_fsmId='+fsmId+'&_fsmFrmId='+fsmFrmId}});
}

function addDefaultSpecialButtons(xbuttons, xgrid){
	var special=true;
	if(_app.mail_flag && 1*_app.mail_flag && xgrid.sendMailFlag){
		if(special)xbuttons.push('-');
		special=false;
		xbuttons.push({tooltip:_localeMsg.js_send_email,cls:'x-btn-icon x-grid-mail', disabled:true, _activeOnSelection:true, _grid:xgrid, handler:fnSendMail});
	}
    if((_app.form_conversion_flag && 1*_app.form_conversion_flag && xgrid.formConversionList) || (_app.mail_flag && 1*_app.mail_flag && xgrid.formSmsMailList) || xgrid.reportList){
    	if(!xgrid.menuButtons)xgrid.menuButtons=[];
    	if(xgrid.menuButtons.length>0)xgrid.menuButtons.push('-');
    	if(_app.form_conversion_flag && 1*_app.form_conversion_flag && xgrid.formConversionList){
        	for(var qz=0;qz<xgrid.formConversionList.length;qz++){
        		xgrid.formConversionList[qz]._grid=xgrid;
				xgrid.formConversionList[qz].handler=function(aq,bq,cq){
					var sel=aq._grid.sm.getSelected();
					if(!sel)return;
					mainPanel.loadTab({attributes:{href:'showForm?a=2&_fid='+aq._fid+'&_cnvId='+aq.xid+'&_cnvTblPk='+sel.id}});
				}
        	}
    		xgrid.menuButtons.push({text:_localeMsg.convert,iconCls:'icon-operation',menu:xgrid.formConversionList});
    	}
 
    	if(_app.mail_flag && 1*_app.mail_flag && xgrid.formSmsMailList){
			for(var qz=0;qz<xgrid.formSmsMailList.length;qz++){
				xgrid.formSmsMailList[qz]._grid=xgrid;
				xgrid.formSmsMailList[qz].handler=function(aq,bq,cq){
					var sel=aq._grid.sm.getSelected();
					if(!sel)return;
					mainPanel.loadTab({attributes:{href:'showForm?_fid='+(1*aq.smsMailTip ? 650:631)+'&_tableId='+aq._grid.crudTableId+'&_tablePk='+sel.id+'&_fsmId='+aq.xid+'&_fsmFrmId='+aq._grid.crudFormId}});
				}
			}
			xgrid.menuButtons.push({text:_localeMsg.js_send_email,iconCls:'icon-email',menu:xgrid.formSmsMailList});
//			xbuttons.push({tooltip:_localeMsg.js_send_email,cls:'x-btn-icon x-grid-mail', disabled:true, _activeOnSelection:true, _grid:xgrid, menu:xgrid.formSmsMailList});
    	}
        if(xgrid.reportList){
        	for(var qz=0;qz<xgrid.reportList.length;qz++){
        		xgrid.reportList[qz]._grid=xgrid;
    			xgrid.reportList[qz].handler=function(aq,bq,cq){
    				var sel=aq._grid.sm.getSelected();
    				if(!sel)return;
    				//mainPanel.loadTab({attributes:{href:'showForm?a=2&_fid='+aq._fid+'&_cnvId='+aq.xid+'&_cnvTblPk='+sel.id}});
    				openPopup("getReportResult?_rid="+aq.xid+"&_tb_pk="+sel.id,"report-"+aq.xid,800,600,1);
    			}
        	}
        	xgrid.menuButtons.push({text:_localeMsg.reports,iconCls:'icon-ireport',menu:xgrid.reportList});
        }
    }

    
	if(xgrid.sendSmsFlag){
		if(special)xbuttons.push('-');
		special=false;
		xbuttons.push({tooltip:_localeMsg.js_send_sms,cls:'x-btn-icon x-grid-sms', disabled:true, _activeOnSelection:true, _grid:xgrid, handler:fnSendSms});
	}
	
	special=true;
	if(_app.file_attachment_flag && 1*_app.file_attachment_flag && xgrid.fileAttachFlag){
		if(xbuttons.length>0)xbuttons.push('-');
		special=false;
		
		if(xgrid.gridId*1 != 1082){
			xbuttons.push({tooltip:_localeMsg.js_iliskili_dosyalar,cls:'x-btn-icon x-grid-attachment', disabled:true, _activeOnSelection:true ,_grid:xgrid, menu:[
			              {text:_localeMsg.js_dosya_sisteminden_ekle,_grid:xgrid,handler:fnNewFileAttachment},
			              {text:_localeMsg.js_daha_once_eklenmis_dosyalardan_ekle,_grid:xgrid,handler:function(a,b){
			                    mainPanel.loadTab({attributes:{_title_:xgrid.name,modalWindow:true, href:'showPage?_tid=238&_gid1=672',tableId:a._grid.crudTableId,tablePk:a._grid.sm.getSelected().id}})
			              }}]});			
		}
		
		else{
			xbuttons.push({tooltip:_localeMsg.js_dosya_sisteminden_ekle,cls:'x-btn-icon x-grid-attachment', disabled:true, _activeOnSelection:true ,_grid:xgrid, handler:fnNewFileAttachment});		
		}
	}
	if(_app.make_comment_flag && 1*_app.make_comment_flag && xgrid.makeCommentFlag){
		if(special)xbuttons.push('-');
		special=false;
		xbuttons.push({tooltip:_localeMsg.js_yorumlar,cls:'x-btn-icon x-grid-comment', disabled:true, _activeOnSelection:true, _grid:xgrid, handler:fnRecordComments});
	}	
	if(_app.attach_picture_flag && 1*_app.attach_picture_flag && xgrid.attachPictureFlag){
		if(special)xbuttons.push('-');
		special=false;
		xbuttons.push({tooltip:_localeMsg.js_resimler,cls:'x-btn-icon x-grid-picture', disabled:true, _activeOnSelection:true, _grid:xgrid, handler:fnRecordPictures});
	}
	if(_app.form_conversion_flag && 1*_app.form_conversion_flag && xgrid.formConversionList){
		xbuttons.push({tooltip:_localeMsg.js_converted_records,cls:'x-btn-icon x-grid-conversion', disabled:true, _activeOnSelection:true, _grid:xgrid, handler:fnRecordConvertionLog});
	}
	if(_app.keyword_flag && 1*_app.keyword_flag && xgrid.keywordFlag){
		if(special)xbuttons.push('-');
		special=false;
		xbuttons.push({tooltip:_localeMsg.js_anahtar_kelimeler,cls:'x-btn-icon x-grid-keywords', disabled:true, _activeOnSelection:true, _grid:xgrid, handler:fnRecordKeywords});
	}
	
}

function addGridExtraButtons(xbuttons, xgrid){
	if(!xgrid.extraButtons)return;
	if(xbuttons.length>0)xbuttons.push('-');
	var report_menu=[];
	var toolbar_menu=[];
	for(var j=0;j<xgrid.extraButtons.length;j++){
	    xgrid.extraButtons[j]._grid=xgrid;
	    xgrid.extraButtons[j].disabled=xgrid.extraButtons[j]._activeOnSelection;
	    
	    if(xgrid.extraButtons[j].ref && xgrid.extraButtons[j].ref.indexOf('../report_')==0){
	        report_menu.push(xgrid.extraButtons[j]);
	    }else{
	    	//if(toolbar_menu.length>0){toolbar_menu.push('-');}// toolbarlar arasına otomatik | koyar.
	        toolbar_menu.push(xgrid.extraButtons[j]);
	    }
	}
	xgrid.extraButtons = []; 

	if (report_menu.length != 0){
	    xgrid.extraButtons.push({tooltip:_localeMsg.js_report, cls:'x-btn-icon x-grid-report', _activeOnSelection:false, _grid:xgrid, menu:report_menu});
	    xgrid.extraButtons.push('-');
	}
	if (toolbar_menu.length != 0)xgrid.extraButtons.push(toolbar_menu);

	xbuttons.push(xgrid.extraButtons);
}

function addDefaultReportButtons(xbuttons, xgrid){
	if (!xgrid.helpButton){xbuttons.push('->');
		if(xgrid.displayInfo)xbuttons.push('-');
	}
	var xxmenu=[];
	xxmenu.push({text:_localeMsg.js_pdfe_aktar, _activeOnSelection:false, _grid:xgrid, handler:fnExportGridData('pdf')});
	xxmenu.push({text:_localeMsg.js_excele_aktar,_activeOnSelection:false, _grid:xgrid, handler:fnExportGridData('xls')});
	xbuttons.push({tooltip:_localeMsg.reports,cls:'x-btn-icon x-grid-pdf', _activeOnSelection:false, _grid:xgrid, menu: xxmenu});
}

function addHelpButton(xbuttons, xgrid){
	//alert(xgrid.getExtDef().mf.basParams._tid);
	xbuttons.push('->');
	if(xgrid.displayInfo)xbuttons.push('-');	
	xbuttons.push({tooltip:_localeMsg.help,cls:'x-btn-icon x-grid-help', _activeOnSelection:false, _grid:xgrid, handler:function(){
		mainPanel.loadTab({
			attributes: {
				_title_: xgrid.name,
				modalWindow: true,
				href: 'showPage?_tid=977&xtable_id=64&xtable_pk='+ xgrid.extraOutMap.tplId
			}
		});		
	}});
	xgrid.helpButton = true;
}

function addGridUserCustomizationsMenu(xgrid){
	var a= Ext.getCmp('grd_pers_buttons'+xgrid.gridId);
    //alert(objProp(km));
		if(!a._loaded){
			a._loaded=true;
			promisRequest({url:'ajaxQueryData?_qid=1052',params:{tgrid_id:a._grid.gridId},
			    successCallback:function(j){
				if(j.success && j.data.length>0){
				    while(a.menu.items.items.length>2)a.menu.remove(2);
				    a.menu.add('-')
				    for(var q=0;q<j.data.length;q++){
					var mitem={text:j.data[q].dsc,_id:j.data[q].user_grid_id,handler:function(ay,by,cy){
						promisRequest({requestWaitMsg:true,url:'ajaxExecDbFunc?_did=649',params:{puser_grid_id:ay._id}, successCallback:function(j2){
								if(j2.success && j2.result){
								var sort=j2.result.pout_sort_dsc;
								if(sort){
									var oo=sort.split(' ');
									if(oo.length>0 && oo[0])a._grid.ds.sortInfo={field:oo[0]};
									if(oo.length>1 && oo[1])a._grid.ds.sortInfo.direction=oo[1];
								}
								var cols=j2.result.pout_columns;
								if(cols && cols.length){
									var gp=a._grid._gp.getColumnModel();
									var oo=cols.split(';')
									for(var qi=0;qi<oo.length;qi++){
										var ooo=oo[qi].split(',');
										var ix=gp.findColumnIndex(ooo[0]);
										if(ix==-1){
										}else{
											if(ix!=qi){gp.moveColumn(ix,qi);}
											if(1*ooo[1]!=gp.getColumnWidth(qi)){/*gp.setColumnWidth(qi,j2.data[qi].column_width,true);*/}
											var h=1*ooo[2]==0
											if(!gp.config[qi].hidden ? h : !h)gp.setHidden(qi,h);
										}
									}
								}
								a._loaded=false;
								
								if(a._grid.searchForm){
									var cells=j2.result.pout_sfrm_cells;
									var fp=a._grid.ds._formPanel;
									if(cells && cells.length && fp){
										var oo=cells.split(';');
										var vals={}
										for(var qi=0;qi<oo.length;qi++){
											var ooo=oo[qi].split(',');
											vals[ooo[0]]=ooo[1].replace('~',',');
										}
										fp.getForm().setValues(vals);
									}
									if(j2.result.pout_grid_height && 1*j2.result.pout_grid_height>50){
										a._grid._gp.setSize(undefined,1*j2.result.pout_grid_height);
									}
									var c=1*j2.result.pout_sfrm_visible_flag==0;
									if(!fp.collapsed ? c:!c){if(c)fp.collapse(true);else fp.expand(true);}
									a._grid.ds.reload();
								}
							}
						}});
					}};
					if(1*j.data[q].default_flag!=0)mitem.checked=true;
					a.menu.add(mitem);
				    }
				}
			    }
			});
		}	
}

function addDefaultGridPersonalizationButtons(xbuttons, xgrid){
	xbuttons.push({id:'grd_pers_buttons'+xgrid.gridId, text:_localeMsg.js_kisisellestir, _grid:xgrid,
		menu:{items:[{text:_localeMsg.js_bu_ayarlari_kaydet, iconCls:'icon-ekle',_grid:xgrid,handler:function(ax,bx,cx){
				var pdsc =  prompt(_localeMsg.js_yeni_goruntu_adi);
				if(!pdsc)return;
				var g=ax._grid,cols='',sort='',cells='';
				for(var z=0;z<g.columns.length;z++)cols+=';'+g.columns[z].dataIndex+','+g.columns[z].width+','+(!g.columns[z].hidden ? 1:0);
				if(g.ds.sortInfo && g.ds.sortInfo.field){
					sort=g.ds.sortInfo.field;
					if(g.ds.sortInfo.direction)sort+=' '+g.ds.sortInfo.direction;
				}
				var params={pcolumns:cols.substr(1),pdsc:pdsc,pgrid_id:ax._grid.gridId,psort_dsc:sort}
				if(g.searchForm){
					var fp=g.ds._formPanel
					if(fp){
						var m=fp.getForm().getValues();
						for(var qi in m)if(m[qi])cells+=';'+qi+','+m[qi].replace(/\,/g,'~')
						if(cells)params.psfrm_cells=cells.substr(1);
					}
					params.pgrid_height=g._gp.getHeight();
					params.psfrm_visible_flag=fp.collapsed?0:1;
				}
				
				promisRequest({requestWaitMsg:true,url:'ajaxExecDbFunc?_did=648',params:params, successCallback:function(j){
					Ext.Msg.alert(_localeMsg.js_tamam, _localeMsg.js_mazgal_yeni_ayarlarla_gorunecek);
				}});
				}
			},{text:_localeMsg.js_kaydedilenleri_duzenle, _grid:xgrid,handler:function(ax,bx,cx){
				mainPanel.loadTab({attributes:{_title_:ax._grid.name,modalWindow:true, href:'showPage?_tid=238&_gid1=851&tgrid_id='+ax._grid.gridId,_pk:{tuser_grid_id:'user_grid_id'},baseParams:{tgrid_id:ax._grid.gridId}}})
			}}]
		}
	});
}


function addDefaultPrivilegeButtons(xbuttons, xgrid){
	if(_scd.administratorFlag || _scd.customizerFlag || xgrid.saveUserInfo){
		xbuttons.push(xgrid.gridReport ? '-':'->');
		var xxmenu=[], bx=false;
	    if(_scd.customizerFlag){xxmenu.push({text:_localeMsg.js_ayarlar,cls:'x-btn-icon x-grid-setting', _activeOnSelection:false, _grid:xgrid, handler:fnGridSetting});bx=true;}
	    if(_scd.administratorFlag){xxmenu.push({text:_localeMsg.js_yetkiler,cls:'x-btn-icon x-grid-privilege', _activeOnSelection:false, _grid:xgrid, handler:fnGridPrivilege});bx=true;}
	    if(xgrid.bulkEmailFlag || xgrid.bulkUpdateFlag){
	    	if(bx)xxmenu.push('-');else bx=true;
	    	if(xgrid.bulkUpdateFlag)xxmenu.push({text:_localeMsg.js_bulk_edit,cls:'x-btn-icon x-grid-bulk-edit', _grid:xgrid, handler:xgrid.fnRowBulkEdit||fnRowBulkEdit});
	    	if(xgrid.bulkEmailFlag)xxmenu.push({text:_localeMsg.js_bulk_mail,cls:'x-btn-icon x-grid-bulk-mail', _grid:xgrid, handler:xgrid.fnRowBulkMail||fnRowBulkMail});
	    }
	    if(_scd.customizerFlag && xgrid.crudTableId){
			if(bx)xxmenu.push('-');else bx=true;
			xxmenu.push({text:_localeMsg.report_settings,cls:'x-btn-icon x-grid-setting', _activeOnSelection:false, _grid:xgrid, handler:fnGridReportSetting});
	    }
		if(xgrid.saveUserInfo){
			if(bx)xxmenu.push('-');else bx=true;
			addDefaultGridPersonalizationButtons(xxmenu,xgrid);
		}
		xbuttons.push({tooltip:_localeMsg.js_ayarlar,cls:'x-btn-icon x-grid-setting', _activeOnSelection:false, _grid:xgrid, handler:function(x){addGridUserCustomizationsMenu(x._grid);},menu: xxmenu});
	}
}

function addDefaultCommitButtons(xbuttons, xgrid){
	xgrid.editMode = xgrid.onlyCommitBtn||false;
	if(xbuttons.length>0 || xgrid.pageSize)xbuttons.push('-');
	if(xgrid.crudTableId)xbuttons.push({tooltip:_localeMsg.js_commit,cls:'x-btn-icon x-grid-commit', disabled: !xgrid.editMode, _activeOnSelection:false, ref:'../btnCommit', _grid:xgrid, handler:xgrid.fnCommit || fnCommit});
	if(!xgrid.onlyCommitBtn)xbuttons.push({tooltip:_localeMsg.js_duzenle_modu,cls:'x-btn-icon x-grid-startedit', _activeOnSelection:false, _grid:xgrid, ref:'../btnEditMode', enableToggle: true, toggleHandler: fnToggleEditMode});
}

function addTab4GridWSearchForm(obj){
	var mainGrid = obj.grid;
	if(obj.pk)mainGrid._pk = obj.pk; //{tcase_id:'case_id',tclient_id:'client_id',tobject_tip:'!4'}
	
	var grdExtra={
		stripeRows: true,
		region: 'center', clicksToEdit: 1*_app.edit_grid_clicks_to_edit
	};
	
	
	var buttons=[];
	if(mainGrid.searchForm && !mainGrid.pageSize){//refresh buttonu
		buttons.push({tooltip:_localeMsg.js_refresh, iconCls:'x-tbar-loading', _activeOnSelection:false, _grid:mainGrid, handler:function(a){
			a._grid.ds.reload({params:a._grid._gp.store._formPanel.getForm().getValues()});
			}});
	}
	if(1*_app.grid_reset_btn && mainGrid.searchForm){
		if(mainGrid.pageSize)buttons.push('-');
        buttons.push({tooltip:_localeMsg.js_clear,cls:'x-btn-icon x-grid-clear', _activeOnSelection:false, handler:function(){searchFormPanel.getForm().reset();}});
	}
	if(mainGrid.editGrid)addDefaultCommitButtons(buttons, mainGrid);	
	if(mainGrid.crudFlags)addDefaultCrudButtons(buttons, mainGrid);
	if(mainGrid.moveUpDown)addMoveUpDownButtons(buttons, mainGrid);
	addDefaultSpecialButtons(buttons, mainGrid);
	addGridExtraButtons(buttons,mainGrid);
	 
	if(mainGrid.detailView && mainGrid.detailDlg){ //selectionMode:5
		buttons.push({tooltip:_localeMsg.js_detay_penceresi, cls:'x-btn-icon x-grid-dpencere', _grid:mainGrid, handler: fnShowDetailDialog});
	}

    if(mainGrid.menuButtons){
    	for(var j=0;j<mainGrid.menuButtons.length;j++){
        	mainGrid.menuButtons[j]._grid=mainGrid;
        }
    	mainGrid.menuButtons = new Ext.menu.Menu({enableScrolling:false, items:mainGrid.menuButtons});
        if(1*_app.toolbar_edit_btn){
        	if(buttons.length>0)buttons.push('-');
        	buttons.push({tooltip:_localeMsg.js_islemler,cls:'x-btn-icon x-grid-menu', disabled:true, _activeOnSelection:true, menu: mainGrid.menuButtons});
        }
    }
    
    addHelpButton(buttons,mainGrid);    
	if(mainGrid.gridReport)addDefaultReportButtons(buttons,mainGrid);
	
	addDefaultPrivilegeButtons(buttons,mainGrid);

    if(mainGrid.pageSize){ // paging'li toolbar
        var tbarExtra = {xtype:'paging', store: mainGrid.ds, pageSize: mainGrid.pageSize, displayInfo: mainGrid.displayInfo};
        if(buttons.length>0)tbarExtra.items=organizeButtons(buttons);
        grdExtra.tbar=tbarExtra;
    } else if(buttons.length>0){//standart toolbar
        grdExtra.tbar=organizeButtons(buttons);
    }
     
    //grid 
    var eg = mainGrid.master_column_id ? (mainGrid.editGrid ? Ext.ux.maximgb.tg.EditorGridPanel : Ext.ux.maximgb.tg.GridPanel) : (mainGrid.editGrid ? Ext.grid.EditorGridPanel : Ext.grid.GridPanel);
    var mainGridPanel =  new eg(Ext.apply(mainGrid,grdExtra));
    mainGrid._gp=mainGridPanel;    
    if(mainGrid.editGrid){
    	mainGridPanel.getColumnModel()._grid=mainGrid;
    	if(!mainGrid.onlyCommitBtn){
    		mainGridPanel.getColumnModel().isCellEditable=function(colIndex,rowIndex){
    			if(this._grid._isCellEditable && this._grid._isCellEditable(colIndex,rowIndex,this._grid)===false)return false;
    			return this._grid.editMode;
    		}
    	} else if(mainGrid._isCellEditable)mainGridPanel.getColumnModel().isCellEditable=function(colIndex,rowIndex){
			return this._grid._isCellEditable(colIndex,rowIndex,this._grid);
		}
    }
    
    if(buttons.length>0){
    	mainGridPanel.getSelectionModel().on("selectionchange",function(a,b,c){
    		if(!a || !a.grid)return;
    		var titems = a.grid.getTopToolbar().items.items;
    		for(var ti=0;ti<titems.length;ti++){
    			if(titems[ti]._activeOnSelection)
    				titems[ti].setDisabled(!a.hasSelection());
    		}
    	});
    }
    var items = [];
	//---search form
    if(mainGrid.searchForm){
		    searchFormPanel =  new Ext.FormPanel(Ext.apply(mainGrid.searchForm.getExtDef(),{
			region: 'north', bodyStyle:'padding:3px',
//			height: mainGrid.searchForm.defaultHeight||120,
			autoHeight:true,
			anchor: '100%',
			collapsible:true,
			title:mainGrid.searchForm.name,
			border: false,
			keys:{key:13,fn:mainGridPanel.store.reload,scope:mainGridPanel.store}
		}));
		
	    //--standart beforeload, ondbliclick, onrowcontextmenu
		if(mainGrid.crudFlags && mainGrid.crudFlags.edit && !mainGrid.crudFlags.nonEditDblClick/* && 1*_app.toolbar_edit_btn*/){mainGridPanel.on('rowdblclick',fnRowEditDblClick);}
		mainGridPanel.store._formPanel = searchFormPanel;
		mainGridPanel.store._grid = mainGrid;
		mainGridPanel.store.on('beforeload',function(a,b){
			if(a){
				if(a._grid.editMode)a._grid._deletedItems=[];
				if(a._formPanel.getForm())a.baseParams = Ext.apply(a._grid._baseParams || {},a._formPanel.getForm().getValues());
			}
		});
		items.push(searchFormPanel);
    }
	
    items.push(mainGridPanel);
	var p ={
        title: obj._title_||mainGrid.name,
        border: false,
        closable:true,
        layout: 'border',
        items: items,
        refreshGrids: obj._dontRefresh ? null : [mainGridPanel]
    };
//	p.iconCls='icon-cmp';
    p = new Ext.Panel(p);
	p._windowCfg = {layout:'border'};
	p._callCfg=obj;
	p._gridMap={};if(obj.t)p._gridMap.t=obj.t;
	p._gridMap['_grid_'+mainGrid.gridId] = mainGridPanel;
    return p;
}

function organizeButtons(items){
	if(!items)return null;
	for(var q=0;q<items.length;q++){
		if(items[q]._text)items[q].tooltip=items[q]._text;
	}
	return items;
}

function addTab4GridWSearchFormWithDetailGrids(obj, master_flag){

	var mainGrid = obj.grid;
	if(obj.pk)mainGrid._pk = obj.pk;
	
	var grdExtra=Ext.apply({
		region:obj.region || 'north',
		autoScroll:true,
		border: false},obj.grdExtra || {
		split:true,stripeRows: true,
		clicksToEdit: 1*_app.edit_grid_clicks_to_edit
	});
	if(grdExtra.region=='north'){
		grdExtra.height=mainGrid.defaultHeight||120;
		grdExtra.minSize=90;
		grdExtra.maxSize=300;
	} else {
		grdExtra.width=mainGrid.defaultWidth||400;		
		grdExtra.minSize=200;			
		if(grdExtra.width < 0){
			grdExtra.width = (-1*grdExtra.width)+"%";				
		}
		else{
			grdExtra.maxSize=grdExtra.width+100;
		}
	}
	
	var buttons=[];
	if(mainGrid.searchForm && !mainGrid.pageSize){//refresh buttonu
		buttons.push({tooltip:_localeMsg.js_refresh, iconCls:'x-tbar-loading', _activeOnSelection:false, _grid:mainGrid, handler:function(a){
			a._grid.ds.reload({params:a._grid._gp.store._formPanel.getForm().getValues()});
			}});
	}
 	if(1*_app.grid_reset_btn && mainGrid.searchForm){
 		if(mainGrid.pageSize)buttons.push('-');
        buttons.push({tooltip:_localeMsg.js_clear,cls:'x-btn-icon x-grid-clear', _activeOnSelection:false, handler:function(){searchFormPanel.getForm().reset();}});
	}
	if(mainGrid.editGrid)addDefaultCommitButtons(buttons, mainGrid);
//	if(mainGrid.gridId==26)alert(objProp(mainGrid.crudFlags))
	if(mainGrid.crudFlags)addDefaultCrudButtons(buttons, mainGrid);
	if(mainGrid.moveUpDown)addMoveUpDownButtons(buttons, mainGrid);
	addDefaultSpecialButtons(buttons, mainGrid);

	if(mainGrid.detailView && mainGrid.detailDlg){ //selectionMode:5
        buttons.push({tooltip:_localeMsg.js_detay_penceresi, cls:'x-btn-icon x-grid-dpencere', _grid:mainGrid, handler: fnShowDetailDialog});
	}


    addGridExtraButtons(buttons,mainGrid);
    
    
    if(mainGrid.rmenu){
    	for(var j=0;j<mainGrid.rmenu.length;j++){
        	mainGrid.rmenu[j]._grid=mainGrid;
        }
    	mainGrid.rmenu = new Ext.menu.Menu({enableScrolling:false, items:mainGrid.rmenu});
        if(1*_app.toolbar_edit_btn){
        	if(buttons.length>0)buttons.push('-');
        	buttons.push({tooltip:_localeMsg.js_report,cls:'x-btn-icon icon-report', disabled:true, _activeOnSelection:true, menu: mainGrid.rmenu});
        }
    }

    if(mainGrid.menuButtons){
    	for(var j=0;j<mainGrid.menuButtons.length;j++){
        	mainGrid.menuButtons[j]._grid=mainGrid;
        }
    	mainGrid.menuButtons = new Ext.menu.Menu({enableScrolling:false, items:mainGrid.menuButtons});
        if(1*_app.toolbar_edit_btn){
        	if(buttons.length>0)buttons.push('-');
        	buttons.push({tooltip:_localeMsg.js_islemler,cls:'x-btn-icon x-grid-menu', disabled:true, _activeOnSelection:true, menu: mainGrid.menuButtons});
        }
    }
    if(!master_flag)addHelpButton(buttons,mainGrid);  
	if(mainGrid.gridReport)addDefaultReportButtons(buttons,mainGrid);
	mainGrid.isMainGrid=true;
	addDefaultPrivilegeButtons(buttons,mainGrid);

    if(mainGrid.pageSize){ // paging'li toolbar
        var tbarExtra = {xtype:'paging',store: mainGrid.ds, pageSize: mainGrid.pageSize, displayInfo: mainGrid.displayInfo};
        if(buttons.length>0)tbarExtra.items=organizeButtons(buttons);
        grdExtra.tbar=tbarExtra;
    } else if(buttons.length>0){//standart toolbar
        grdExtra.tbar=organizeButtons(buttons);
    }
    
    //grid 
    var eg = mainGrid.master_column_id ? (mainGrid.editGrid ? Ext.ux.maximgb.tg.EditorGridPanel : Ext.ux.maximgb.tg.GridPanel) : (mainGrid.editGrid ? Ext.grid.EditorGridPanel : Ext.grid.GridPanel);
    var mainGridPanel =  new eg(Ext.apply(mainGrid,grdExtra));
    mainGrid._gp=mainGridPanel;
    if(mainGrid.editGrid){
    	mainGridPanel.getColumnModel()._grid=mainGrid;
    	if(!mainGrid.onlyCommitBtn){
    		mainGridPanel.getColumnModel().isCellEditable=function(colIndex,rowIndex){
    			if(this._grid._isCellEditable && this._grid._isCellEditable(colIndex,rowIndex,this._grid)===false)return false;
    			return this._grid.editMode;
    		};
    	} else if(mainGrid._isCellEditable)mainGridPanel.getColumnModel().isCellEditable=function(colIndex,rowIndex){
			return this._grid._isCellEditable(colIndex,rowIndex,this._grid);
		};
    }
    	
    if(buttons.length>0){
    	mainGridPanel.getSelectionModel().on("selectionchange",function(a,b,c){
    		if(!a || !a.grid)return;
    		var titems = a.grid.getTopToolbar().items.items;
    		for(var ti=0;ti<titems.length;ti++){
    			if(titems[ti]._activeOnSelection)
    				titems[ti].setDisabled(!a.hasSelection());
    		}
    	});
    }
    if(mainGrid.menuButtons/* && !1*_app.toolbar_edit_btn*/){
     	mainGridPanel.messageContextMenu = mainGrid.menuButtons;
        mainGridPanel.on('rowcontextmenu', fnRightClick);
    }
	//---search form
	var searchFormPanel = null;
    if(mainGrid.searchForm){
    	var searchFormPanel = mainGrid.searchForm.fp = new Ext.FormPanel(Ext.apply(mainGrid.searchForm.getExtDef(),{
			region: 'north', bodyStyle:'padding:3px',
//			height: mainGrid.searchForm.defaultHeight||120,
			autoHeight:true,
			anchor: '100%',
			collapsible:true,
			title:mainGrid.searchForm.name,
			border: false,
			keys:{key:13,fn:mainGridPanel.store.reload,scope:mainGridPanel.store}
		}));
    	mainGridPanel.store._formPanel = searchFormPanel;
    }
	
    //--standart beforeload, ondbliclick, onrowcontextmenu
	if(mainGrid.crudFlags && mainGrid.crudFlags.edit && !mainGrid.crudFlags.nonEditDblClick/* && 1*_app.toolbar_edit_btn*/){mainGridPanel.on('rowdblclick',fnRowEditDblClick);}

	mainGridPanel.store.on('beforeload',function(a,b){
		if(searchFormPanel){
			//mainGridPanel.store._formPanel = searchFormPanel;
			mainGridPanel.store._grid = mainGrid;
			if(a._grid.editMode)a._grid._deletedItems=[];
			a.baseParams = Ext.apply(a._grid._baseParams || {},a._formPanel.getForm().getValues());//a._formPanel.getForm().getValues();
		}
		if(mainGridPanel.getSelectionModel().getSelected())mainGridPanel._lastSelectedGridRowId = mainGridPanel.getSelectionModel().getSelected().id;
	});

	mainGridPanel.store.on('load',function(a,b){
		if(a.totalLength==0)return;
		var sm=mainGridPanel.getSelectionModel();
		if(!sm.hasSelection())sm.selectFirstRow();
		if(mainGridPanel._lastSelectedGridRowId && (1*mainGridPanel._lastSelectedGridRowId == 1*sm.getSelected().id)){
			mainGridPanel.getSelectionModel().fireEvent("selectionchange",mainGridPanel.getSelectionModel());
		}
	});
    
	//detail tabs
	var detailGridPanels = [];
	if(mainGrid.detailView && !mainGrid.detailDlg){ //selectionMode:5
		
		mainGridPanel._detailView = new Ext.DataView(Ext.apply({
			store: new Ext.data.JsonStore({
				fields: mainGridPanel.store.reader.meta.fields,
				root: 'data'
			}),
			tpl:mainGrid.detailView,
			autoScroll:true,
			overClass:'x-view-over',
			itemSelector:'table.grid_detay'
		},mainGrid.detailViewExtra || {}));
		
		mainGridPanel._detailViewPanel = new Ext.Panel({
			autoScroll:true,
		    title:_localeMsg.js_detay,
		    items:mainGridPanel._detailView});//mainGridPanel._detailView
		   
		detailGridPanels.push(mainGridPanel._detailViewPanel);
		mainGridPanel.getSelectionModel().on("selectionchange",function(a,b,c){
		//			if(!a.grid._detailViewPanel.isVisible())return;
			var sel=a.grid.getSelectionModel().getSelected();
			if(sel)a.grid._detailView.store.loadData({data:[sel.json]});
			else a.grid._detailView.store.loadData({data:[]});
		});
		/*mainGridPanel._detailViewPanel.on("show",function(a,b,c){
			if(gridDetail.initialConfig.editMode)gridDetail.btnEditMode.toggle();
			if(gridDetail.store.baseParams)gridDetail.store.reload()
		})*/
	}
	 	
	if(obj.detailGrids.length>1) obj.detailGrids.sort(function(a,b){ return (a.grid.tabOrder||-1) -(b.grid.tabOrder||-1)});// gridler template object sirasina gore geliyor.
	var detailGridMap = {};
	for(var i=0;i<obj.detailGrids.length;i++){
		if(obj.detailGrids[i].detailGrids){//master/detail olacak
			if(!obj.detailGrids[i].grid.gridId)continue;
			
			delete  obj.detailGrids[i].grid.searchForm;// Detail gridlerin searchFormu olamaz. Patlıyor.
			
			var xmxm=addTab4GridWSearchFormWithDetailGrids(obj.detailGrids[i], 1);
			obj.detailGrids[i].grid._masterGrid = mainGrid;
			if(xmxm.items.items[0].xtype=='form'){ //ilk sıradaki gridin ,detail gridi varsa Search Formunu yok ediyor
				xmxm.items.items[0].destroy();	
			}
			if(xmxm._gridMap)for(var k in xmxm._gridMap)detailGridMap[k]=xmxm._gridMap[k];
			var detailGridPanel=xmxm.items.items[0].items.items[0];
			
			grid2grid(mainGridPanel,detailGridPanel,obj.detailGrids[i].params, xmxm);
/*			
			xmxm.store=detailGridPanel.store;			
	        detailGridPanel.store._formPanel = searchFormPanel;
	        detailGridPanel.store._grid = mainGrid;
	        detailGridPanel.store.on('beforeload',function(a,b){
				if(a._grid.editMode)a._grid._deletedItems=[];
				if(a && a._formPanel.getForm())a.baseParams = a._formPanel.getForm().getValues();
			});	       */ 
	        xmxm.closable=false;	     
			detailGridPanels.push(xmxm);
			
		} else {
			var detailGrid = obj.detailGrids[i].grid;			
			if(!detailGrid || !detailGrid.gridId)continue;
			detailGrid._masterGrid = mainGrid;
			
			if(detailGrid._ready){
				detailGridPanels.push(detailGrid);
				if(detailGrid.gridId)detailGridMap['_grid_'+detailGrid.gridId] = detailGrid;
				continue;
			}
			if(obj.detailGrids[i].pk)detailGrid._pk = obj.detailGrids[i].pk;
			var grdExtra={
				title:obj.detailGrids[i]._title_||detailGrid.name,stripeRows: true,id:'gr'+Math.random(), border:false,
				autoScroll:true, clicksToEdit: 1*_app.edit_grid_clicks_to_edit};
			var buttons=[];
	
	   		if(detailGrid.editGrid)addDefaultCommitButtons(buttons, detailGrid);	
	
			if(detailGrid.detailView && detailGrid.detailDlg){ //selectionMode:5
	            buttons.push({tooltip:_localeMsg.js_detay_penceresi, cls:'x-btn-icon x-grid-dpencere', _grid:detailGrid, handler: fnShowDetailDialog});
			}
			
			if(detailGrid.hasFilter){
				    if(buttons.length>0)buttons.push('-');
				    buttons.push({tooltip:_localeMsg.js_filtreyi_kaldir,cls:'x-btn-icon x-grid-funnel',_grid:detailGrid, handler:fnClearFilters});
			}
			
			if(detailGrid.crudFlags)addDefaultCrudButtons(buttons, detailGrid);
			if(detailGrid.moveUpDown)addMoveUpDownButtons(buttons, detailGrid);
			addDefaultSpecialButtons(buttons, detailGrid);
		    addGridExtraButtons(buttons,detailGrid);
            
		    if(detailGrid.menuButtons){
	         	for(var j=0;j<detailGrid.menuButtons.length;j++){
	         		detailGrid.menuButtons[j]._grid=detailGrid;
	             }
	         	detailGrid.menuButtons = new Ext.menu.Menu({enableScrolling:false, items:detailGrid.menuButtons});
	             if(1*_app.toolbar_edit_btn){
	             	if(buttons.length>0)buttons.push('-');
	             	buttons.push({tooltip:_localeMsg.js_islemler,cls:'x-btn-icon x-grid-menu', disabled:true, _activeOnSelection:true, menu: detailGrid.menuButtons});
	             }
	         }
	     	if(detailGrid.gridReport)addDefaultReportButtons(buttons,detailGrid);
	    	addDefaultPrivilegeButtons(buttons,detailGrid);
	    	if(detailGrid.maximizeFlag){
	    		buttons.push('-');
	    		buttons.push({tooltip:_localeMsg.js_mazgali_buyut,cls:'x-btn-icon x-grid-resize', _activeOnSelection:false, _grid:detailGrid, handler:function(a,b,c){
	    			var href='showPage?_tid=622&_gid1='+a._grid.gridId;
	    			if(a._grid._masterGrid && a._grid._masterGrid.crudTableId && a._grid._masterGrid.sm.hasSelection()){
	    				href+='&_mtid='+a._grid._masterGrid.crudTableId+'&_mtpk='+a._grid._masterGrid.sm.getSelected().id;
	    			}
	    			mainPanel.loadTab({attributes:{href:href,_grid:a._grid,baseParams:a._grid.ds.baseParams}});
	    		}});
	    	}
	
	 		
	         if(detailGrid.pageSize){ // paging'li toolbar
	             var tbarExtra = {xtype:'paging', store: detailGrid.ds, pageSize: detailGrid.pageSize, displayInfo: detailGrid.displayInfo};
	             if(buttons.length>0)tbarExtra.items=organizeButtons(buttons);
	             grdExtra.tbar=tbarExtra;
	         } else if(buttons.length>0){//standart toolbar
	             grdExtra.tbar=organizeButtons(buttons);
	         }
	
	        var eg = detailGrid.master_column_id ? (detailGrid.editGrid ? Ext.ux.maximgb.tg.EditorGridPanel : Ext.ux.maximgb.tg.GridPanel) : (detailGrid.editGrid ? Ext.grid.EditorGridPanel : Ext.grid.GridPanel);
			var detailGridPanel = new eg(Ext.apply(detailGrid,grdExtra));
			detailGrid._gp=detailGridPanel;
		    if(detailGrid.editGrid){
		    	detailGridPanel.getColumnModel()._grid=detailGrid;
		    	if(!detailGrid.onlyCommitBtn){
		    		detailGridPanel.getColumnModel().isCellEditable=function(colIndex,rowIndex){
		    			if(this._grid._isCellEditable && this._grid._isCellEditable(colIndex,rowIndex,this._grid)===false)return false;
		    			return this._grid.editMode;
		    		};
		    	} else if(detailGrid._isCellEditable)mainGridPanel.getColumnModel().isCellEditable=function(colIndex,rowIndex){
					return this._grid._isCellEditable(colIndex,rowIndex,this._grid);
				};
		    }
	
			if(detailGrid.menuButtons/* && !1*_app.toolbar_edit_btn*/){
				detailGridPanel.messageContextMenu = detailGrid.menuButtons;
				detailGridPanel.on('rowcontextmenu', fnRightClick);
	        }
	/*	    if(detailGrid.saveUserInfo)detailGridPanel.on("afterrender",function(a,b,c){
				detailGridPanel.getView().hmenu.add('-',{text: 'Mazgal Ayarları',cls:'grid-options1',menu: {items:[{text:'Mazgal Ayarlarını Kaydet',handler:function(){saveGridColumnInfo(grid.getColumnModel(),mainGrid.gridId)}},
			    	                                                                                       {text:'Varsayılan Ayarlara Dön',handler:function(){resetGridColumnInfo(mainGrid.gridId)}}]}});
		    }); */
			if(detailGridPanel.crudFlags && detailGridPanel.crudFlags.edit && !detailGridPanel.crudFlags.nonEditDblClick/* && 1*_app.toolbar_edit_btn*/){detailGridPanel.on('rowdblclick',fnRowEditDblClick);}
			if(detailGrid.extraOutMap && detailGrid.extraOutMap.revMstTableId){
				obj.detailGrids[i].params._revMstTableId='!'+detailGrid.extraOutMap.revMstTableId;
				obj.detailGrids[i].params._revMstTablePk=detailGrid.extraOutMap.revMstTablePKField;
				obj.detailGrids[i].params._revDtlTableId='!'+detailGrid.extraOutMap.revDtlTableId;
			}
			grid2grid(mainGridPanel,detailGridPanel,obj.detailGrids[i].params);
	        if(buttons.length>0){
	        	detailGridPanel.getSelectionModel().on("selectionchange",function(a,b,c){
	        		if(!a || !a.grid)return;
	        		var titems = a.grid.getTopToolbar().items.items;
	        		for(var ti=0;ti<titems.length;ti++){
	        			if(titems[ti]._activeOnSelection)titems[ti].setDisabled(!a.hasSelection());
	        		}
	        	});
	        }
			detailGridPanels.push(detailGridPanel);
			if(detailGrid.gridId)detailGridMap['_grid_'+detailGrid.gridId] = detailGridPanel;
		}
	} 
    /*if(!(obj.autoLoad===false))mainGridPanel.on("afterrender",function(a,b,c){
    	mainGridPanel.store.reload()
        if(mainGrid.saveUserInfo){
        	mainGridPanel.getView().hmenu.add('-',{text: 'Mazgal Ayarları',cls:'grid-options1',menu: {items:[{text:'Mazgal Ayarlarını Kaydet',handler:function(){saveGridColumnInfo(grid.getColumnModel(),mainGrid.gridId)}},
        	                                                                                       {text:'Varsayılan Ayarlara Dön',handler:function(){resetGridColumnInfo(mainGrid.gridId)}}]}});
        }
    });*/
	var scrollerMenu = new Ext.ux.TabScrollerMenu({
		maxText  : 15,
		pageSize : 5
	});
    var lastItems=[];
    if(searchFormPanel!=null){
    	lastItems.push(searchFormPanel)
    }
    lastItems.push({
		region:'center',
		layout:'border',
		items:[mainGridPanel,new Ext.TabPanel({
			region:'center',
			enableTabScroll:true,
			activeTab:0,
			visible:false,
			items:detailGridPanels,
			plugins:[scrollerMenu]
			
	})]})
    var p = {
	    layout: 'border',
        title:obj._title_||mainGrid.name,
//        id: obj.id||'x'+new Date().getTime(),
        border: false,
        closable:true,
        items:lastItems,        
        //refreshGrids: searchFormPanel?[mainGridPanel]:null
        refreshGrids: obj._dontRefresh ? null : (searchFormPanel?[mainGridPanel]:null)
    };    
//    p.iconCls='icon-cmp';
    p = new Ext.Panel(p);
	p._windowCfg = {layout:'border'};
	p._callCfg=obj;
	p._gridMap={};if(obj.t)p._gridMap.t=obj.t;
	p._gridMap['_grid_'+mainGrid.gridId] = mainGridPanel;
	for(var k in detailGridMap)p._gridMap[k]=detailGridMap[k];
    return p;
}

function prepareLogErrors(obj){
	if(!obj.logErrors)return 'eksik';
	var str="";
	for(var xi=0;xi<obj.logErrors.length;xi++){
		str+="<b>"+(xi+1)+".</b> "+ obj.logErrors[xi].dsc;
		if(obj.logErrors[xi]._record)str+=renderParentRecords(obj.logErrors[xi]._record,1)+"<br>";
		else if(xi<obj.logErrors.length-1){
			str+="<br>&nbsp;<br>";
		}
	}
	return str;
}
function ajaxErrorHandler(obj){
		
    if (obj.errorType && obj.errorType=='sql'){
    	if(1*_app.debug != 1){
    		var jsm ={title: _localeMsg.js_hata,
        			msg: '<b>'+obj.error+'</b>',
        			icon: Ext.MessageBox.ERROR}
    		if(obj.logErrors){
    			jsm.msg+='<br/>&nbsp<br/>'+obj.logErrors.length+' adet detay kayit vardir. Detay gormek ister misiniz?'
    			jsm.buttons=Ext.MessageBox.YESNO;
    			jsm.fn=function(btn){
    				if(btn=='yes')Ext.Msg.show({title: 'Log Details...',msg: prepareLogErrors(obj),icon: Ext.MessageBox.WARNING});
    			}
    		}
    		Ext.Msg.show(jsm);
    	}
    	else{
    		var xbuttons =[{text:_localeMsg.js_hatayi_bildir,handler:function(){Ext.Msg.alert(_localeMsg.js_bilgi,'Yakında');}},{text:_localeMsg.js_kapat,handler:function(){wndx.close();}}];
    		if(obj.logErrors)xbuttons.push({text:'Log Details...('+obj.logErrors.length+')',handler:function(){
    			Ext.Msg.show({
    		    	title: 'Log Details...', 
    		    	msg: prepareLogErrors(obj), 
    		    	icon: Ext.MessageBox.WARNING});
    			}});
	        var wndx=new Ext.Window({
	            modal:true,
	            title:'DB.SQL Error',
	            width: 800,
	            height:187,
	            items:[{    
	                xtype:'propertygrid',
	                autoHeight: true,
	                propertyNames: {
	                },
	                customEditors:{
	                    'SQL': new Ext.grid.GridEditor(new Ext.form.TextArea({selectOnFocus:true,height:120})),
	                    'Error':new Ext.grid.GridEditor(new Ext.form.TextArea({selectOnFocus:true,height:120}))
	                },
	                source: {
	                    'Object Type': obj.objectType || '<'+_localeMsg.js_belirtilmemis+'>',
	                    'Object ID': obj.objectId || '<'+_localeMsg.js_belirtilmemis+'>',
	                    'Error': obj.error || '<'+_localeMsg.js_belirtilmemis+'>',
	                    'SQL':obj.sql || '<'+_localeMsg.js_belirtilmemis+'>'
	                },
	                viewConfig : { forceFit: true, scrollOffset: 2 // the grid will never have scrollbars
	                }
	            }],
	        buttons:xbuttons
	        });
	        wndx.show();
	        wndx.items.items[0].getColumnModel().setColumnWidth(0,100);
	        wndx.items.items[0].getColumnModel().setColumnWidth(1,wndx.items.items[0].getColumnModel().getColumnWidth(1)+300);
    	}
    }else if (obj.errorType && obj.errorType=='validation'){
        var msg='<b>'+_localeMsg.js_alan_dogrulama_hatalari+'</b><ul>';
        if(obj.errors){
        	for (var i=0;i<obj.errors.length;i++)if (obj.errors[i].id!='_')
        		msg+='<li>&nbsp;&nbsp;&nbsp;&nbsp;'+(obj.errors[i].dsc || obj.errors[i].id)+' - '+obj.errors[i].msg+'</li>';
        } else if(obj.error){
        	msg+=obj.error;
        }
        msg+='</ul>';
        Ext.Msg.show({title:_localeMsg.js_hata,msg: msg,icon: Ext.MessageBox.ERROR})
    }else if (obj.errorType && obj.errorType=='framework'){
    	var xbuttons =[{text:_localeMsg.js_hatayi_bildir,handler:function(){Ext.Msg.alert(_localeMsg.js_bilgi,'Yakında');}},{text:_localeMsg.js_kapat,handler:function(){wndx.close();}}];
		if(obj.logErrors)xbuttons.push({text:'Log Details...('+obj.logErrors.length+')',handler:function(){    			Ext.Msg.show({
	    	title: 'Log Details...', 
	    	msg: prepareLogErrors(obj), 
	    	icon: Ext.MessageBox.WARNING});
		}});
        var wndx=new Ext.Window({
            modal:true,
            title:'Framework Error',
            width: 800,
            height:167,
            items:[{    
                xtype:'propertygrid',
                autoHeight: true,
                source: {
                    'Object Type': obj.objectType || '<'+_localeMsg.js_belirtilmemis+'>',
                    'Object ID': obj.objectId || '<'+_localeMsg.js_belirtilmemis+'>',
                    'Error': obj.error || '<'+_localeMsg.js_belirtilmemis+'>'
                },  
                customEditors:{
                    'Error':new Ext.grid.GridEditor(new Ext.form.TextArea({selectOnFocus:true,height:120}))
                },
            
                viewConfig : {
                    forceFit: true,
                    scrollOffset: 2 // the grid will never have scrollbars
                }
            }],
        buttons:xbuttons
        });
        wndx.show();
        wndx.items.items[0].getColumnModel().setColumnWidth(0,100);
        wndx.items.items[0].getColumnModel().setColumnWidth(1,wndx.items.items[0].getColumnModel().getColumnWidth(1)+300);          
    }else if (obj.errorType && obj.errorType=='session')
        showLoginDialog(obj);
    else if (obj.errorType && obj.errorType=='security')
	    Ext.Msg.show({
	    	title: _localeMsg.js_guvenlik_hatasi, 
	    	msg: _localeMsg.js_hata+': <b>'+(obj.error || _localeMsg.js_belirtilmemis)+'</b><br/>'+obj.objectType+" Id: <b>"+obj.objectId+'</b>', 
	    	icon: Ext.MessageBox.ERROR});
    else
        Ext.Msg.show({title:_localeMsg.js_bilgi,msg: msg,icon: Ext.MessageBox.INFO})
    
}
var lw=null;
function ajaxAuthenticateUser(){
    Ext.getCmp('loginForm').getForm().submit({
        url: 'ajaxAuthenticateUser?userRoleId='+_scd.userRoleId+'&locale='+_scd.locale+'&.w='+_webPageId,
        method:'POST', 
		clientValidation: true,
        waitMsg : _localeMsg.js_entering+'...',
        success: function(o,resp){
	        if(resp.result.success){
	        	lw.destroy();
	        	if(typeof onlineUsersGridPanel!='undefined' && onlineUsersGridPanel)reloadOnlineUsers();
	        }else {
	            Ext.MessageBox.alert(_localeMsg.js_hata,resp.errorMsg||_localeMsg.js_yanlis_kullanici_adi_sifre);
			}
    	},
	    failure: function(o,resp){
			var resp=eval('('+resp.response.responseText+')');
	        if(resp.errorMsg){
	            Ext.MessageBox.alert(_localeMsg.js_hata,resp.errorMsg);
	            getSecurityWord();
	        }else{
	            Ext.MessageBox.alert(_localeMsg.js_hata,resp.error||_localeMsg.js_verileri_kontrol);
	        }
	    }
    });
  return false;    
}

function getSecurityWord(){
	 Ext.Ajax.request({
	     url : 'ajaxGetSecurityPicture' ,       
	 method: 'POST',
	 success: function (result, request) {
	 var resp=eval('('+result.responseText+')');
	 if(resp.success && resp.data.length>0 ){				
		var file_name=resp.data[0].file_name;				
		Ext.get("security_div").dom.innerHTML = '<img src=../images/security/'+file_name+'>';
	    }
	 else {
		if (Ext.get("security_word_table")){
	Ext.get("security_word_table").dom.innerHTML='';
	Ext.get("securityword_l").dom.innerHTML='';	   
		}     
	 }
	 }
	});			
}
function showLoginDialog(xobj){
	if(lw && lw.isVisible())return;
	if(typeof onlineUsersGridPanel!='undefined' && onlineUsersGridPanel)onlineUsersGridPanel.store.removeAll();
	var fs = new Ext.form.FormPanel({
		id: 'loginForm',
		name: 'loginForm',
		frame: false,
		border: false,
		labelAlign:'right',
		labelWidth:100,
		waitMsgTarget: true,
		method: 'POST',
		buttonAlign: 'center',
		buttons: [
		{
			text: _localeMsg.js_giris,
			iconCls: 'button-enter',
			handler: ajaxAuthenticateUser
		},
		{
           text: _localeMsg.js_cikis,
           iconCls: 'button-exit',
           handler: function(){document.location='login.htm?r='+new Date().getTime();
        }
		}],                       
		items :pfrm_login.getExtDef().items[0].items
	});
	
	lw = new Ext.Window({
        modal:true,
        title:pfrm_login.name,
        width: 350,
        height: 225,
        layout: 'fit',
        items:fs,
        bodyStyle:'background-color:#fff;padding: 10px',
        closable:false
    });
	lw.show();
	
	var nav = new Ext.KeyNav(Ext.getCmp('loginForm').getForm().getEl(), {
		'enter':ajaxAuthenticateUser,
		'scope': Ext.getCmp('loginForm')
	});	
	//if(xobj || xobj.error)alert(xobj.error);
}
/*var alignments={TOP_LEFT:{anchor:'tl-tl',offsets:[0,0],slide:'t'},BOTTOM_RIGHT:{anchor:'br-br',offsets:[0,-70],slide:'b'},TOP_CENTER:{anchor:'t-t',offsets:[0,0],slide:'t'}};
function notification(css,title,format){
	if(!notificationContainer){
		notificationContainer=Ext.DomHelper.insertFirst(document.body,{id:'notification-div'},true);
		notificationContainer.alignTo(UserDesktop.getDesktop().getEl(),alignments[position].anchor,alignments[position].offsets);
	}
	notificationContainer.alignTo(UserDesktop.getDesktop().getEl(),alignments[position].anchor,alignments[position].offsets);
	var s=String.format.apply(String,Array.prototype.slice.call(arguments,2));
	var m=Ext.DomHelper.append(notificationContainer,{html:createBox(css,title,s)},true);
	m.slideIn(alignments[position].slide).pause(pause).ghost(alignments[position].slide,{remove:true});
};*/
Ext.infoMsg = function(){
    var msgCt;

    function createBox(t, s){
        return ['<div class="msg">',
                '<div class="x-box-tl"><div class="x-box-tr"><div class="x-box-tc"></div></div></div>',
                '<div class="x-box-ml"><div class="x-box-mr"><div class="x-box-mc"><h3>', t, '</h3>', s, '</div></div></div>',
                '<div class="x-box-bl"><div class="x-box-br"><div class="x-box-bc"></div></div></div>',
                '</div>'].join('');
    }
    return {
        msg : function(title, format, duration){
            if(!msgCt){
                msgCt = Ext.DomHelper.insertFirst(document.body, {id:'msg-div'}, true);
            }
            msgCt.alignTo(document, 'br-br',[0,-70]);
            var s = String.format.apply(String, Array.prototype.slice.call(arguments, 1));
            var m = Ext.DomHelper.append(msgCt, {html:createBox(title, s)}, true);
            m.slideIn('b').pause(duration || 1).ghost("b", {remove:true});
        },

        init : function(){
            var lb = Ext.get('lib-bar');
            if(lb){
                lb.show();
            }
        }
    };
}();
Ext.onReady(Ext.infoMsg.init, Ext.infoMsg);


function formSubmit(submitConfig){
    var cfg = {
        waitMsg: _localeMsg.js_lutfen_bekleyin,
        clientValidation: true,
        success: function(form,action){
            var myJson = eval('(' + action.response.responseText+ ')');
            var jsonQueue = [];
            if(myJson.smsMailPreviews && myJson.smsMailPreviews.length>0){
            	for(var ix=0;ix<myJson.smsMailPreviews.length;ix++){
            		var smp=myJson.smsMailPreviews[ix];            		
            		var ss="";
            		if (smp.tbId==683)//w5_customer_feedback
            			ss="&tcustomization_id="+submitConfig.extraParams.tcustomization_id;
            		jsonQueue.push({attributes:{href:'showForm?_fid='+(smp.fsmTip ? 650:631)+'&_tableId='+smp.tbId+'&_tablePk='+smp.tbPk+'&_fsmId='+smp.fsmId+'&_fsmFrmId='+myJson.formId+ss}});
            	}
            } 
            if(myJson.conversionPreviews && myJson.conversionPreviews.length>0){
            	for(var ix=0;ix<myJson.conversionPreviews.length;ix++){
            		var cnvp=myJson.conversionPreviews[ix];
//            		mainPanel.loadTab({attributes:{href:'showForm?a=2&_fid='+cnvp._fid+'&_cnvId='+cnvp._cnvId+'&_cnvTblPk='+cnvp._cnvTblPk}});
//            		if(cnvp._cnvDsc)Ext.infoMsg.msg('Form Donusum',cnvp._cnvDsc);
            		var ppp = {attributes:{href:'showForm?a=2&_fid='+cnvp._fid+'&_cnvId='+cnvp._cnvId+'&_cnvTblPk='+cnvp._cnvTblPk}};
            		if(cnvp._cnvDsc)ppp._cnvDsc=cnvp._cnvDsc;
            		jsonQueue.push(ppp);
            	}
            } 
            
            if(myJson.alarmPreviews && myJson.alarmPreviews.length>0){
            	alert('TODO ALARM PREVIEWS: ' + myJson.alarmPreviews.length + " adet")
/*            	for(var ix=0;ix<myJson.alarmPreviews.length;ix++){
            		var alm=myJson.alarmPreviews[ix];
	            	Ext.Msg.show({
	            	    title: 'Alarm',
	            	    msg: alm.dsc,
	            	    buttons: Ext.MessageBox.OKCANCEL,
	            	    inputField: new Ext.DatePicker({"width": 115, "format": "d/m/Y H:i"}),
	            	    fn: function(buttonId, text) {
	            	        if (buttonId == 'ok'){
	            	        	alert('hoho')
	            	        }
	            	    }
	            	});
            	} */
            } 
            
            if(jsonQueue.length>0){
            	var jsonQueueCounter = 0;
            	var autoOpenForms = new Ext.util.DelayedTask(function(){
            		mainPanel.loadTab(jsonQueue[jsonQueueCounter]);
            		if(jsonQueue[jsonQueueCounter]._cnvDsc)Ext.infoMsg.msg('Form Donusum',jsonQueue[jsonQueueCounter]._cnvDsc);
            		jsonQueueCounter++;
            		if(jsonQueue.length>jsonQueueCounter)autoOpenForms.delay(1000);
            	});
            	autoOpenForms.delay(1);
            }
            
            if(myJson.nextBpmActions && myJson.nextBpmActions.length>0){
            	myJson.nextBpmActions[0].checked=true;
            	var qwin = new Ext.Window({
                    layout:'fit',
                    width:300,
                    height:200,
                    closeAction:'destroy',
                    plain: true, modal:true,
                    title:'BPM Devami...',
                    items: {xtype:'form', border:false,labelWidth:10,items:{
                    xtype: 'radiogroup',itemCls: 'x-check-group-alt',id:'promisRadioGroup1',columns: 1,items:myJson.nextBpmActions}},
                    buttons: [{
                        text:'Sec',
                        handler:function(ax,bx,cx){
                    		var prg = Ext.getCmp('promisRadioGroup1');
                            if(prg.getValue() && prg.getValue().getValue()){
                            	var fxx=prg.getValue().js_code;
                                qwin.destroy();
                            	eval('fxx=function(xax,xbx){\n'+fxx+'\n}');
                            	fxx(myJson);
                            }
                        }
                    },{
                        text: 'Iptal',
                        handler: function(){
                            qwin.destroy();
                        }
                    }]
                });
                qwin.show();
                
            }else if(myJson.logErrors || myJson.msgs){
            	var str='';
            	if(myJson.msgs)str=myJson.msgs.join('<br>')+'<p>';
            	if(myJson.logErrors)str+=prepareLogErrors(myJson);
            	Ext.Msg.show({title: _localeMsg.js_bilgi,msg: str,icon: Ext.MessageBox.INFO});
            } /*else if(1*_app.mail_send_background_flag!=0 && myJson.outs && myJson.outs.thread_id){ //DEPRECATED
            	Ext.infoMsg.msg(_localeMsg.js_tamam,_localeMsg.js_eposta_gonderiliyor+'...');
            }*/
            else if(_app.show_info_msg && 1*_app.show_info_msg!=0)Ext.infoMsg.msg(_localeMsg.js_tamam,_localeMsg.js_islem_basariyla_tamamlandi);
            if(submitConfig.callback){
            	if(submitConfig.callback(myJson,submitConfig)===false)return;
            }
            
            if (submitConfig._closeWindow){
            	submitConfig._closeWindow.destroy();
            }
            else if(submitConfig.modalWindowFormSubmit){
            	submitConfig.tabp.remove(submitConfig.tabp.getActiveTab());          	
            }
            
            else if (!submitConfig.dontClose && !mainPanel.closeModalWindow()){
            	mainPanel.remove(mainPanel.getActiveTab());
            }
            
            if(submitConfig.resetValues){
            	submitConfig.formPanel.getForm().reset();
            }
            if(submitConfig._callAttributes){
            	if(submitConfig._callAttributes._grid)submitConfig._callAttributes._grid.ds.reload();
            }

        },
        failure:function(form, action) {
            switch (action.failureType) {
            case Ext.form.Action.CLIENT_INVALID:
                Ext.Msg.alert(_localeMsg.js_hata, _localeMsg.js_form_alan_veri_dogrulama_hatasi);
                break;
            case Ext.form.Action.CONNECT_FAILURE:
                Ext.Msg.alert(_localeMsg.js_hata, _localeMsg.js_no_connection_error);
                break;
            case Ext.form.Action.SERVER_INVALID:
            	if(action.result.msg){
	            	Ext.Msg.alert(_localeMsg.js_hata, action.result.msg);
	            	break;
            	}
            case Ext.form.Action.LOAD_FAILURE:
            	ajaxErrorHandler(action.result);
            	break;
            default:
            	ajaxErrorHandler(action.result);
            }
        } 
    };
	if(typeof _webPageId != 'undefined'){
		if(!cfg.params)cfg.params={};
		if(!cfg.params['.w'])cfg.params['.w']=_webPageId;
	}
    if(submitConfig.extraParams)cfg.params=submitConfig.extraParams;
    submitConfig.formPanel.getForm().submit(cfg);
};

function promisLoadException(a,b,c){
	if(c && c.responseText){
		ajaxErrorHandler(eval("("+c.responseText+")"));
	} else Ext.Msg.show({title: _localeMsg.js_bilgi, msg: _localeMsg.js_no_connection_error, icon: Ext.MessageBox.INFO});
}

function promisRequest(rcfg){
	if(typeof _webPageId != 'undefined'){
		if(!rcfg.params)rcfg.params={};
		if(!rcfg.params['.w'])rcfg.params['.w']=_webPageId;
	}

	var reqWaitMsg = 1*_app.request_wait_msg;	
	if(typeof rcfg.requestWaitMsg == 'boolean'){
		if (rcfg.requestWaitMsg)
			reqWaitMsg=1;
		else 
			reqWaitMsg=0;		
	}		
	if(reqWaitMsg==1)Ext.Msg.wait((rcfg.requestWaitMsg == '' ||typeof rcfg.requestWaitMsg == 'undefined' || typeof rcfg.requestWaitMsg == 'boolean') ? _localeMsg.js_lutfen_bekleyin : rcfg.requestWaitMsg);
	
	Ext.Ajax.request(Ext.apply({
	    success: function(a,b,c){
	    	if(reqWaitMsg==1) Ext.Msg.hide();
			if(rcfg.successResponse)
				rcfg.successResponse(a,b,c);
			else try{
				var json = eval("("+a.responseText+")");
				if(json.success){
					if(rcfg.successDs){
						if(!rcfg.successDs.length)
							rcfg.successDs.reload(rcfg.successDs);
						else if(rcfg.successDs.length>0){
							for(var qi=0;qi<rcfg.successDs.length;qi++)
								rcfg.successDs[qi].reload(rcfg.successDs[qi]);
						}
					}
					if(rcfg.successCallback)rcfg.successCallback(json);
					else if(_app.show_info_msg && 1*_app.show_info_msg!=0) {try { Ext.infoMsg.msg(_localeMsg.js_tamam,_localeMsg.js_islem_basariyla_tamamlandi); } catch (e) {} }					
				} else {
			    	if(rcfg.noSuccessCallback)rcfg.noSuccessCallback(json);
			    	else ajaxErrorHandler(json);
				}
			}catch(e){
				if(1*_app.debug != 0){
	                e.requestedUrl = rcfg.url;
	                if(e.stack)e.stack=null;
	                alert(objProp(e));
				} else alert('Framework Error - 3'); //???
			} 
		},
	    failure: function(a,b,c){
	    	if(reqWaitMsg==1) Ext.Msg.hide();
			promisLoadException(a,b,c);
		}
	}, rcfg));
}

/* Bizim eklediğimiz validation typelar */

Ext.apply(Ext.form.VTypes, { 
    daterange : function(val, field) { // Daha Küçük bir tarihin ileriki tarih olarak girilememesi
        var date = field.parseDate(val);

        if(!date){
            return false;
        }
        if (field.startDateField) {
            var start = Ext.getCmp(field.startDateField);
            if (!start.maxValue || (date.getTime() != start.maxValue.getTime())) {
                start.setMaxValue(date);
                start.validate();
            }
        }
        else if (field.endDateField) {
            var end = Ext.getCmp(field.endDateField);
            if (!end.minValue || (date.getTime() != end.minValue.getTime())) {
                end.setMinValue(date);
                end.validate();
            }
        }
        return true;
    }
});


function combo2combo(comboMaster,comboDetail,param,formAction){//formAction:2(insert) ise ve comboDetail reload olunca 1 kayit geliyorsa otomatik onu sec
	if(typeof comboMaster=='undefined' || typeof comboDetail=='undefined')return;
//	if(typeof comboDetail.hiddenValue!='undefined')return;
	if(typeof comboMaster.hiddenValue=='undefined'){
		comboMaster.on('select',function(a,b,c){
			var p = null;
	        if(typeof param == 'function'){
	        	p = param(comboMaster.getValue(),b);
	        	if(comboDetail._controlTip != 60){
		        	if(!p){
		        		comboDetail.disable();
		        		comboDetail.setValue('');
		        	} else comboDetail.enable();
	        	}else{
	        		comboDetail.clearValue();// Aşırı sıkış
	        	}
	        } else {
	        	p = {};
	            p[param]=comboMaster.getValue();
	        }
			if(p){
				if(typeof comboDetail.hiddenValue=='undefined'){
					comboDetail.store.baseParams=p;
					comboDetail.store.reload({											
						callback:function(ax){
							if(typeof comboDetail._controlTip!='undefined' && (comboDetail._controlTip==16 || comboDetail._controlTip== 60)){//lovcombo-remote
								if(comboDetail._oldValue && comboDetail._oldValue != null){
									comboDetail.setValue(comboDetail._oldValue);
									comboDetail._oldValue=null;
								}								
							} else if((ax && !(ax.length)) || comboMaster.getValue() == ''){
								comboDetail.clearValue();
							} else if(ax && ax.length==1 && (comboDetail.getValue()==ax[0].id || formAction == 2)){
								comboDetail.setValue(ax[0].id);
							} else if(ax && ax.length>1 && comboDetail.getValue()){
								if(comboDetail.store.getById(comboDetail.getValue())){
									comboDetail.setValue(comboDetail.getValue());	
								}
								else{
									comboDetail.clearValue();									
								}
							}
							if(comboDetail.getValue())comboDetail.fireEvent('select');
						}
					});
				} else {
					p.xid=comboDetail.hiddenValue;
					promisRequest({url:'ajaxQueryData',params:p,successCallback:function(j2){
						if(j2 && j2.data && j2.data.length)for(var qi=0;qi<j2.data.length;qi++)if(''+j2.data[qi].id==''+comboDetail.hiddenValue){
							comboDetail.setValue('<b>'+j2.data[qi].dsc+'</b>');
						}
					}});
				}
			}
		});
		if(comboMaster.getValue())comboDetail.on('afterrender',function(a,b){comboMaster.fireEvent('select');});
	} else {//master hiddenValue
		var p = null;
        if(typeof param == 'function'){
        	p = param(comboMaster.hiddenValue,comboMaster);
        	if(!p){
        		comboDetail.disable();
        		comboDetail.setValue('');
        	} else comboDetail.enable();
        } else {
        	p = {};
            p[param]=comboMaster.hiddenValue;
        }
		if(p){
			if(typeof comboDetail.hiddenValue=='undefined'){
				comboDetail.store.baseParams=p;
				comboDetail.store.reload({
	//				params:p,
					callback:function(ax){
						if(typeof formAction!='undefined' && formAction==2 && ax && ax.length==1){
							comboDetail.setValue(ax[0].id);
						} else if(comboDetail.getValue()){
							comboDetail.setValue(comboDetail.getValue());
						}
						if(comboDetail.getValue())comboDetail.fireEvent('select');
					}
				});
			} else {
				p.xid=comboDetail.hiddenValue;
				promisRequest({url:'ajaxQueryData',params:p,successCallback:function(j2){
					if(j2 && j2.data && j2.data.length)for(var qi=0;qi<j2.data.length;qi++)if(''+j2.data[qi].id==''+comboDetail.hiddenValue){
						comboDetail.setValue('<b>'+j2.data[qi].dsc+'</b>');
					}
				}});
			}
		}
	}
}

function loadCombo(comboMaster,param,formAction){
	if(typeof comboMaster=='undefined' || typeof comboMaster.hiddenValue!='undefined' || !param)return;
	/*comboMaster.store.on('beforeload', function(st,opt){
		comboMaster.setReadOnly(true);
	});*/
	comboMaster.store.reload({
		params:param,
		callback:function(ax){
			if(typeof formAction!='undefined' && formAction==2 && ax && ax.length==1/* && !comboMaster.getValue()*/){comboMaster.setValue(ax[0].id);} 
			else if(comboMaster.getValue() || comboMaster._oldValue)comboMaster.setValue(comboMaster.getValue() || comboMaster._oldValue);
			if(comboMaster.getValue())comboMaster.fireEvent('select');
			//comboMaster.setReadOnly(false);
		}
	});
}

function openModal(cfg){
	mainPanel.loadTab({attributes:{href:cfg.url,modalWindow:true}});
}

try{
Ext.util.Format.fmtParaShow=fmtParaShow;
Ext.util.Format.fmtShortDate=fmtShortDate;
Ext.util.Format.fmtDateTime=fmtDateTime;
Ext.util.Format.disabledCheckBoxHtml=disabledCheckBoxHtml;
}catch(e){}


function gridQwRenderer(field){
    return function(a,b,c){
        return c.data[field+'_qw_'];
    };
}
function gridQwRendererWithLink(field,tbl_id){
    return function(a,b,c){
        return c.data[field]!=undefined ? '<a href=# onclick="return fnTblRecEdit('+tbl_id+','+c.data[field]+')">'+c.data[field+'_qw_']+'</a>' :'';
    };
}

function gridUserRenderer(field){
    return function(a,b,c){
        return c.data[field]==_scd.userId ? c.data[field+'_qw_'] : '<a href=# onclick="return openChatWindow('+c.data[field]+',\''+c.data[field+'_qw_']+'\',true)">'+c.data[field+'_qw_']+'</a>';
    };
}
function editGridComboRenderer(combo){
    return function(value){
    	if(!combo || !combo.store)return '???';
        var record = combo.store.getById(value);
        return record ? record.get('dsc') : '';
    };
}
function editGridTreeComboRenderer(combo,field){
    return function(value,b,c){
    	if(!combo || !combo.treePanel)return '???';
        var record = combo.treePanel.getNodeById(value);
        if(record)record=record.text;
        else record = (value && value!=0) ? c.data[field+'_qw_'] : '';
        return record;
    };
}

function editGridLovComboRenderer(combo){
	return function(value){ 
    	if(!combo)return '???';
		var valueList = [];
		if(typeof value == 'undefined')return '';
		if(!value && (''+value).length==0)return '';
		var findArr = value.split(',');
		var i, l=findArr.length;
		for (i=0; i<l; i++) {
			if (record = combo.store.getById(findArr[i])) {
				valueList.push(record.get('dsc'));
			}
		}
		return valueList.join(',');
	};
}

function handleMouseDown(g, rowIndex, e){
    if(e.button !== 0 || this.isLocked()){
        return;
    };
    var view = this.grid.getView();
    if(e.shiftKey && this.last !== false){
        var last = this.last;
        this.selectRange(last, rowIndex, e.ctrlKey);
        this.last = last; // reset the last
        view.focusRow(rowIndex);
    }else{
        var isSelected = this.isSelected(rowIndex);
        if(e.ctrlKey && isSelected){
            this.deselectRow(rowIndex);
        }else if(!isSelected || this.getCount() > 1){
            this.selectRow(rowIndex, true);
            view.focusRow(rowIndex);
        }
    }
}

/*if(appSetting.gridCheckBoxStaySelected)Ext.grid.CheckboxSelectionModel.override({
    handleMouseDown : function(g, rowIndex, e){
        if(e.button !== 0 || this.isLocked()){
            return;
        };
        var view = this.grid.getView();
        if(e.shiftKey && this.last !== false){
            var last = this.last;
            this.selectRange(last, rowIndex, e.ctrlKey);
            this.last = last; // reset the last
            view.focusRow(rowIndex);
        }else{
            var isSelected = this.isSelected(rowIndex);
            if(e.ctrlKey && isSelected){
                this.deselectRow(rowIndex);
            }else if(!isSelected || this.getCount() > 1){
                this.selectRow(rowIndex, true);
                view.focusRow(rowIndex);
            }
        }
    }
});*/

function approveTableRecord(aa,a){
	var sel = a._grid.sm.getSelected();
	
	if(!sel){
    	Ext.Msg.alert(_localeMsg.js_bilgi,_localeMsg.js_once_birseyler_secmelisiniz);
    	return
    }
    if(aa==2 && 1*sel.data.return_flag==0){
    	Ext.Msg.alert(_localeMsg.js_bilgi,_localeMsg.js_bu_surecte_iade_yapilamaz);
    	return
    }      
    
    var onayMap=['',_localeMsg.js_onayla,_localeMsg.js_iade_et,_localeMsg.js_reddet];
    onayMap[901]=_localeMsg.js_onayi_baslat;
    var caption = onayMap[aa]+' ('+sel.data.dsc+')';
    var e_sign_flag = sel.data.e_sign_flag;
    
    if((1*e_sign_flag == 1) && (aa*1 == 1)){ //
        openPopup('showPage?_tid=691&_arid='+sel.data.approval_record_id,'_blank',800,600,1);                        
        return;            
    }
    
    var urlek='';
    var dynamix = (sel.data.approval_flow_tip*1==3 && aa==901)? true:false;
    if(dynamix)
    	urlek='&xapp_record_id4user_ids='+sel.data.approval_record_id;
    var lvcombo = new Ext.ux.form.LovCombo({
		labelSeparator:'',
		fieldLabel:_localeMsg.dynamic_step_user_ids, 
		hiddenName: 'approve_user_ids',
		store: new Ext.data.JsonStore({
			url:'ajaxQueryData?_qid=585' + urlek,
			root:'data',
			totalProperty:'browseInfo.totalCount',
			id:'id',
			autoLoad: true,
			fields:[{name:'dsc'},{name:'id',type:'int'}],
			listeners:{loadexception:promisLoadException}
		}),
		valueField:'id',
		displayField:'dsc',
		mode: 'local',
		triggerAction: 'all',
		anchor:'100%'});
	
	if(!dynamix){
		lvcombo.setVisible(false);
	}	
	
	var cform = new Ext.form.FormPanel({
		baseCls: 'x-plain',
		labelWidth: 150,
		frame:false,
		bodyStyle:'padding:5px 5px 0',
		labelAlign: 'top',
		
		items: [lvcombo, {
			xtype: 'textarea',
			fieldLabel: _localeMsg.js_yorumunuzu_girin,
			name: '_comment',
			anchor: '100% -5'  // anchor width by percentage and height by raw adjustment
		}]
	});
	
	var win = new Ext.Window({
		layout:'fit',
		width:500,
		height:300,
		plain: true,
		buttonAlign:'center',
		modal:true,
		title: caption,
		
		items: cform,
		buttons: [{text:_localeMsg.js_tamam,
				handler: function(ax,bx,cx){	
					var _dynamic_approval_users = win.items.items[0].items.items[0].getValue();	
					var _comment = win.items.items[0].items.items[1].getValue();
					promisRequest({
						url: 'ajaxApproveRecord',
						params:{_arid:sel.id,_adsc:_comment,_aa:aa, _avno:sel.data.version_no, _appUserIds:_dynamic_approval_users},
						successDs: a._grid.ds
						,successCallback:aa!=901?win.close():function(){win.close();Ext.Msg.alert(_localeMsg.js_bilgi,_localeMsg.js_onay_sureci_baslamistir);}
					});
				}},
				{text:_localeMsg.js_iptal,
				handler: function(){
					win.close();
				}
			}]
	});
	win.show(this);  
	
    /*Ext.MessageBox.show({
    	   title: caption,
    	   msg: _localeMsg.js_yorumunuzu_girin+':',
    	   width:500,
    	   buttons: Ext.MessageBox.OKCANCEL,
    	   multiline: true,
    	   fn: function(ax,bx,c){
    	if(ax=='ok'){
	    	promisRequest({
				url: 'ajaxApproveRecord',
				params:{_arid:sel.id,_adsc:bx,_aa:aa, _avno:sel.data.version_no},
				successDs: a._grid.ds
				,successCallback:aa!=901?undefined:function(){alert(_localeMsg.js_onay_sureci_baslamistir);}
			});
    	}}
    	,animEl:a
   	});
   	*/
}

function approveTableRecords(aa, a){
	var sels = a._grid.sm.getSelections();

	if(sels.length==0){
		Ext.Msg.alert(_localeMsg.js_error,_localeMsg.js_once_birseyler_secmelisiniz);
    	return
    }
	var tek_kayit = sels.length == 1 ? true : false;
	var sel_ids = [];
	var urlek='';
	var dynamix = false;
	var vers = [];
	var step = 0;
	var rec_id;
	var step_id;

	for (var i=0; i<sels.length; i++){
		if (sels[i].data.approval_record_id){
			rec_id = sels[i].data.approval_record_id;
		}else{
			rec_id = sels[i].data.pkpkpk_arf_id;			
		}
		
		if (sels[i].data.approval_step_id){
			step_id = sels[i].data.approval_step_id;
		}else{
			step_id = sels[i].data.pkpkpk_arf;			
		}				
		
		if (step!=0 && step_id*1!=step ){
			Ext.Msg.alert(_localeMsg.js_error,_localeMsg.js_secilenlerin_onay_adimi_ayni_olmali);
			return;
		}
		step = step_id;	

		if (step_id*1==998){
			Ext.Msg.alert(_localeMsg.js_error,_localeMsg.js_kayit_onaylanmis);
			return;
		}	
				
		if (step_id<0){
			Ext.Msg.alert(_localeMsg.js_error,_localeMsg.js_onay_adiminda_yer_almiyorsunuz);
			return;
		} 
		
		if (sels[i].data.in_approval_users && sels[i].data.in_approval_roles){
			if (sels[i].data.in_approval_users*1!=1 && sels[i].data.in_approval_roles*1!=1){
				Ext.Msg.alert(_localeMsg.js_error,_localeMsg.js_onay_adiminda_yer_almiyorsunuz);
				return;
			}
		}
		
		if(aa==2 && 1*sels[i].data.return_flag==0){
			Ext.Msg.alert(_localeMsg.js_error,_localeMsg.js_bu_surecte_iade_yapilamaz);
			return
		}      
		var e_sign_flag = sels[i].data.e_sign_flag || 0;
		if((1*e_sign_flag == 1) && (aa*1 == 1) && (tek_kayit == false)){ //
			Ext.Msg.alert(_localeMsg.js_error,_localeMsg.js_e_imza_onay_tek_kayit_secilmeli);                    
			return;            
		}
		dynamix = (sels[0].data.approval_flow_tip*1==3 && aa==901)? true:false;
		if (dynamix==true && tek_kayit==false){
			Ext.Msg.alert(_localeMsg.js_error,_localeMsg.js_dinamik_onay_tek_kayit_secilmeli);                    
			return;  
		}
		if(dynamix)
			urlek='&xapp_record_id4user_ids='+rec_id;
		if (rec_id*1>0){			
			sel_ids.push(rec_id);
			vers.push(sels[i].data.ar_version_no||sels[i].data.version_no);
		}
	}
    var onayMap=['',_localeMsg.js_onayla,_localeMsg.js_iade_et,_localeMsg.js_reddet];
    onayMap[901]=_localeMsg.js_onayi_baslat;
    var caption = onayMap[aa];
                        
    if(sel_ids.length==0)return;
    
    var lvcombo = new Ext.ux.form.LovCombo({
		labelSeparator:'',
		fieldLabel:_localeMsg.dynamic_step_user_ids, 
		hiddenName: 'approve_user_ids',
		store: dynamix == true ? (new Ext.data.JsonStore({
			url:'ajaxQueryData?_qid=585' + urlek,
			root:'data',
			totalProperty:'browseInfo.totalCount',
			id:'id',
			autoLoad: true,
			fields:[{name:'dsc'},{name:'id',type:'int'}],
			listeners:{loadexception:promisLoadException}
		})): null,
		valueField:'id',
		displayField:'dsc',
		mode: 'local',
		triggerAction: 'all',
		anchor:'100%'});
	
	if(!dynamix){
		lvcombo.setVisible(false);
	}	
	
	var cform = new Ext.form.FormPanel({
		baseCls: 'x-plain',
		labelWidth: 150,
		frame:false,
		bodyStyle:'padding:5px 5px 0',
		labelAlign: 'top',
		
		items: [lvcombo, {
			xtype: 'textarea',
			fieldLabel: _localeMsg.js_yorumunuzu_girin,
			name: '_comment',
			anchor: '100% -5'  // anchor width by percentage and height by raw adjustment
		}]
	});
	
	var win = new Ext.Window({
		layout:'fit',
		width:500,
		height:300,
		plain: true,
		buttonAlign:'center',
		modal:true,
		title: caption,
		
		items: cform,
		buttons: [{text:_localeMsg.js_tamam,
				handler: function(ax,bx,cx){	
					var _dynamic_approval_users = (dynamix == true ? (win.items.items[0].items.items[0].getValue()) : null);	
					var _comment = win.items.items[0].items.items[1].getValue();
					/*promisRequest({
						url: 'ajaxApproveRecord',
						params:{_arids:sel_ids,_adsc:_comment,_aa:aa, _avnos:vers, _appUserIds:_dynamic_approval_users},
						successDs: a._grid.ds
						,successCallback:win.close()
					});*/

					//senkron hale getirildi
					var prms = "";
					for(var i=0; i<sel_ids.length; i++){
						prms +=  '_arids='+sel_ids[i]+'&';
						prms +=  '_avnos='+vers[i]+'&';
					}					
					prms += '_adsc' + '=' + encodeURIComponent(_comment) + '&' + '_aa' + '=' + encodeURIComponent(aa)+ '&' + '_appUserIds' + '=' + encodeURIComponent(_dynamic_approval_users);
					
					Ext.Msg.wait('',_localeMsg.js_lutfen_bekleyin );
					var request = promisManuelAjaxObject();
					request.open("POST", 'ajaxApproveRecord', false);
					request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
					request.send(prms);
					var json = eval("("+request.responseText+")");
					Ext.Msg.hide();
					if(json.success){
						win.close();
						a._grid.ds.reload();
						Ext.infoMsg.msg(_localeMsg.js_tamam,_localeMsg.js_islem_basariyla_tamamlandi);			
					} else ajaxErrorHandler(json);
					
				}},
				{text:_localeMsg.js_iptal,
				handler: function(){
					win.close();
				}
			}]
	});
	win.show(this);  	
}

function submitAndApproveTableRecord(aa, frm, dynamix){
    var caption = null;
    if(aa != null){
    	caption = ['',_localeMsg.js_onayla,_localeMsg.js_iade_et,_localeMsg.js_reddet][aa];
    }
    else{
    	caption = _localeMsg.js_onay_mek_baslat;
    }
    var urlek='';
    if(dynamix)
    	urlek='&xapp_record_id4user_ids='+frm.approval.approvalRecordId;
	var lvcombo = new Ext.ux.form.LovCombo({
		labelSeparator:'',
		fieldLabel:_localeMsg.dynamic_step_user_ids, 
		hiddenName: 'approve_user_ids',
		store: new Ext.data.JsonStore({
			url:'ajaxQueryData?_qid=585' + urlek,
			root:'data',
			totalProperty:'browseInfo.totalCount',
			id:'id',
			autoLoad: true,
			fields:[{name:'dsc'},{name:'id',type:'int'}],
			listeners:{loadexception:promisLoadException}
		}),
		valueField:'id',
		displayField:'dsc',
		mode: 'local',
		triggerAction: 'all',
		anchor:'100%'});
	
	if(!dynamix){
		lvcombo.setVisible(false);
	}
		
	var cform = new Ext.form.FormPanel({
		baseCls: 'x-plain',
		labelWidth: 150,
		frame:false,
		bodyStyle:'padding:5px 5px 0',
		labelAlign: 'top',
		
		items: [lvcombo, {
			xtype: 'textarea',
			fieldLabel: _localeMsg.js_yorumunuzu_girin,
			name: '_comment',
			anchor: '100% -5'  // anchor width by percentage and height by raw adjustment
		}]
	});
	
	var win = new Ext.Window({
		layout:'fit',
		width:500,
		height:300,
		plain: true,
		buttonAlign:'center',
		modal:true,
		title: caption,		
		items: cform,
		buttons: [{text:_localeMsg.js_tamam,
				handler: function(ax,bx,cx){	
					var _dynamic_approval_users = win.items.items[0].items.items[0].getValue();	
					var _comment = win.items.items[0].items.items[1].getValue();
					if((aa==1 && (!_app.form_approval_save_flag || 1*_app.form_approval_save_flag==0)) || frm.viewMode){
		    			var prms = frm.pk;
		    			prms._arid = frm.approval.approvalRecordId;
		    			prms._aa = aa;
		    			prms._adsc = _comment;
		    			prms._avno = frm.approval.versionNo;
		    			promisRequest({
		    				url: 'ajaxApproveRecord',
		    				params:prms,
		    				successCallback:function(json){
		    					win.close();
			    				var submitConfig=frm._cfg;
		    					if(submitConfig._callAttributes){
		    		            	if(submitConfig._callAttributes._grid)submitConfig._callAttributes._grid.ds.reload();
		    		            }
		    					if (submitConfig._closeWindow){
		    		            	submitConfig._closeWindow.destroy();
		    		            }
		    		            else if(submitConfig.modalWindowFormSubmit){
		    		            	submitConfig.tabp.remove(submitConfig.tabp.getActiveTab());          	
		    		            }
		    		            else if (!mainPanel.closeModalWindow() && !submitConfig.dontClose){
		    		            	mainPanel.remove(mainPanel.getActiveTab());
		    		            }		    					
		    				}
		    			});
		    		} else {
		    			if(aa != -1){
							frm._cfg.extraParams=Ext.apply(frm._cfg.extraParams||{},{_arid:frm.approval.approvalRecordId,_aa:aa,_adsc:_comment,_avno:frm.approval.versionNo+1, _appUserIds:_dynamic_approval_users});
		    			}
		    			else{
		    				frm._cfg.extraParams=Ext.apply(frm._cfg.extraParams||{},{_aa:aa,_adsc:_comment});
		    			}
						formSubmit(frm._cfg);
		    		}
					win.close();
				}},
				{text:_localeMsg.js_iptal,
				handler: function(){
					win.close();
				}
			}]
	});
	win.show(this);    	
}

function addTab4Portal(obj){
	var detailGridPanels = [];
	for(var i=0;i<obj.detailGrids.length;i++){
			var detailGrid = obj.detailGrids[i].grid;
			if(!detailGrid || !detailGrid.gridId)continue;
			if(detailGrid._ready){
				detailGridPanels.push(detailGrid);
				continue;
			}
			if(obj.detailGrids[i].pk)detailGrid._pk = obj.detailGrids[i].pk;
			var grdExtra={
				stripeRows: true,id:'gr'+Math.random(),
				autoScroll:true, clicksToEdit: 1*_app.edit_grid_clicks_to_edit};
			var buttons=[];
	   		if(detailGrid.editGrid)addDefaultCommitButtons(buttons, detailGrid);	
			
			if(detailGrid.hasFilter){
				    if(buttons.length>0)buttons.push('-');
				    buttons.push({tooltip:_localeMsg.js_filtreyi_kaldir,cls:'x-btn-icon x-grid-funnel',_grid:detailGrid, handler:fnClearFilters});
			}
			
			if(detailGrid.crudFlags)addDefaultCrudButtons(buttons, detailGrid);
			if(detailGrid.moveUpDown)addMoveUpDownButtons(buttons, detailGrid);
			addDefaultSpecialButtons(buttons, detailGrid);

	         if(detailGrid.extraButtons){
	             if(buttons.length>0)buttons.push('-');
	             for(var j=0;j<detailGrid.extraButtons.length;j++){
	            	 detailGrid.extraButtons[j]._grid=detailGrid;
	            	 detailGrid.extraButtons[j].disabled=detailGrid.extraButtons[j]._activeOnSelection;
	             }
	             buttons.push(detailGrid.extraButtons);
	         }
	         if(detailGrid.menuButtons){
	         	for(var j=0;j<detailGrid.menuButtons.length;j++){
	         		detailGrid.menuButtons[j]._grid=detailGrid;
	             }
	         	detailGrid.menuButtons = new Ext.menu.Menu({enableScrolling:false, items:detailGrid.menuButtons});
	             if(1*_app.toolbar_edit_btn){
	             	if(buttons.length>0)buttons.push('-');
	             	buttons.push({tooltip:_localeMsg.js_islemler,cls:'x-btn-icon x-grid-menu', disabled:true, _activeOnSelection:true, menu: detailGrid.menuButtons});
	             }
	         }
	         if(detailGrid.gridReport)addDefaultReportButtons(buttons, detailGrid);
	         addDefaultPrivilegeButtons(buttons,detailGrid);

	 		
	         if(detailGrid.pageSize){ // paging'li toolbar
	             var tbarExtra = {xtype:'paging', store: detailGrid.ds, pageSize: detailGrid.pageSize, displayInfo: detailGrid.displayInfo};
	             if(buttons.length>0)tbarExtra.items=organizeButtons(buttons);
	             grdExtra.tbar=tbarExtra;
	         } else if(buttons.length>0){//standart toolbar
	             grdExtra.tbar=organizeButtons(buttons);
	         }
	        var eg = detailGrid.master_column_id ? (detailGrid.editGrid ? Ext.ux.maximgb.tg.EditorGridPanel : Ext.ux.maximgb.tg.GridPanel) : (detailGrid.editGrid ? Ext.grid.EditorGridPanel : Ext.grid.GridPanel);
			var detailGridPanel = new eg(Ext.apply(detailGrid,grdExtra));
        	if(detailGrid.crudFlags && detailGrid.crudFlags.edit)detailGridPanel.on('rowdblclick',fnRowEditDblClick);

			detailGrid._gp=detailGridPanel;
		    if(detailGrid.editGrid){
		    	detailGridPanel.getColumnModel()._grid=detailGrid;
		    	if(!detailGrid.onlyCommitBtn){
		    		detailGridPanel.getColumnModel().isCellEditable=function(colIndex,rowIndex){
		    			if(this._grid._isCellEditable && this._grid._isCellEditable(colIndex,rowIndex,this._grid)===false)return false;
		    			return this._grid.editMode;
		    		}
		    	} else if(detailGrid._isCellEditable)mainGridPanel.getColumnModel().isCellEditable=function(colIndex,rowIndex){
					return this._grid._isCellEditable(colIndex,rowIndex,this._grid);
				}
		    }

			if(detailGrid.menuButtons/* && !1*_app.toolbar_edit_btn*/){
				detailGridPanel.messageContextMenu = detailGrid.menuButtons;
				detailGridPanel.on('rowcontextmenu', fnRightClick);
	        }

	        if(buttons.length>0){
	        	detailGridPanel.getSelectionModel().on("selectionchange",function(a,b,c){
	        		if(!a || !a.grid)return;
	        		var titems = a.grid.getTopToolbar().items.items;
	        		for(var ti=0;ti<titems.length;ti++){
	        			if(titems[ti]._activeOnSelection)
	        				titems[ti].setDisabled(!a.hasSelection());
	        		}

	        	});
	        }
			detailGridPanels.push(detailGridPanel);
		} 
		return detailGridPanels;
}
function fnRowInsert2(a,b){
	var ex = new a._grid.record(Ext.apply({},a._grid.initRecord));
	a._grid._insertedItems[ex.id]=true;
	ex.markDirty();
	var gp=Ext.getCmp(a._grid.id)
	gp.stopEditing();
	var insertIndex = !a._grid._insertAtLastIndex || !gp.getStore().data.items.length ? 0 : gp.getStore().data.items.length;
	gp.getStore().insert(insertIndex, ex);
	gp.getView().refresh();
	gp.getSelectionModel().selectRow(insertIndex);
	gp.startEditing(insertIndex,typeof a._grid._startEditColumn=='undefined' ? 1:a._grid._startEditColumn);
};


function fnRowDelete2(a,b){
	var sel=a._grid.sm.getSelected();
	if(!sel)return;
	if(a._grid._deleteControl && a._grid._deleteControl(sel,a._grid)==false){
		return;
	}
	if(a._grid._insertedItems[sel.id]){
		a._grid._insertedItems[sel.id]=false;
	}else{
		var delItem={};
		for(var key in a._grid._pk)delItem[key]=sel.data[a._grid._pk[key]];
		a._grid._deletedItems.push(delItem);
	}
	var ds=a._grid.ds || a._grid.store;
	var io=ds.indexOf(sel);
	ds.remove(sel);
	if(ds.getCount()>0){
		if(io>=ds.getCount())io=ds.getCount()-1;
		a._grid.sm.selectRow(io,false);
	}
};

function prepareParams4grid(grid, prefix){
  	var dirtyCount=0;
  	var params={};
	var items = grid._deletedItems;
  	if(items)for(var bjk=0;bjk<items.length;bjk++){ //deleted
  		dirtyCount++;
  		for(var key in items[bjk])params[key+prefix+"."+dirtyCount]=items[bjk][key];
  		params["a"+prefix+"."+dirtyCount]=3;
  	}
	items = grid.ds.data.items;
	var pk = grid._pk;
	if(items)for(var bjk=0;bjk<items.length;bjk++)if(items[bjk].dirty || grid._insertedItems[items[bjk].id]){ // edited&inserted
		dirtyCount++;
		for(var key in pk){
			var val=pk[key];
			if(typeof val == 'function'){
				params[key+prefix+"."+dirtyCount] = val(items[bjk].data);
			} else {
				params[key+prefix+"."+dirtyCount]=(val.charAt(0)=='!') ? val.substring(1):items[bjk].data[val];
			}
			
		}
		var changes=items[bjk].getChanges();
		for(var key in changes)params[key+prefix+"."+dirtyCount]=changes[key];
		if(grid._insertedItems[items[bjk].id]){
			params["a"+prefix+"."+dirtyCount]=2;
			if(grid._postMap)for(var key in grid._postMap){
				var val = grid._postMap[key];
				if(typeof val == 'function'){
					params[key+prefix+"."+dirtyCount] = val(items[bjk].data);
				} else {
					params[key+prefix+"."+dirtyCount]=(val.charAt(0)=='!') ? val.substring(1):items[bjk].data[val];
				}
			}
			if(grid._postInsertParams){
				for(var key in grid._postInsertParams)params[key+prefix+"."+dirtyCount]=grid._postInsertParams[key];
			}
		} else {
			params["a"+prefix+"."+dirtyCount]=1;
		}
	}
	if(dirtyCount>0){
		params['_cnt'+prefix]=dirtyCount;
		params['_fid'+prefix]=grid.crudFormId;
		return params;
	} else return {};
}

function prepareParams4gridINSERT(grid, prefix){//sadece master-insert durumunda cagir. farki _postMap ve hic bir zaman _insertedItems,_deletedItems dikkate almamasi
	var dirtyCount=0;
	var params={};
  	var dirtyCount=0;
	var items = grid.ds.data.items;
	if(items)for(var bjk=0;bjk<items.length;bjk++){ // inserted
		dirtyCount++;
		if(grid._postMap)for(var key in grid._postMap){
			var val = grid._postMap[key];
			if(typeof val == 'function'){
				params[key+prefix+"."+dirtyCount] = val(items[bjk].data);
			} else {
				params[key+prefix+"."+dirtyCount]=(val.charAt(0)=='!') ? val.substring(1):items[bjk].data[val];
			}
		}
		params["a"+prefix+"."+dirtyCount]=2;
		if(grid._postInsertParams){
			for(var key in grid._postInsertParams)params[key+prefix+"."+dirtyCount]=grid._postInsertParams[key];
		}
	}
	if(dirtyCount>0){
		params['_cnt'+prefix]=dirtyCount;
		params['_fid'+prefix]=grid.crudFormId;
		return params;
	} else return {};
}


function prepareDetailGridCRUDButtons(grid,pk,toExtraButtonsFlag){
	function add_menu(){
		if(grid.menuButtons){
	    	for(var j=0;j<grid.menuButtons.length;j++){
	    		grid.menuButtons[j]._grid=grid;
	        }
	    	grid.menuButtons = new Ext.menu.Menu({enableScrolling:false, items:grid.menuButtons});
	        if(1*_app.toolbar_edit_btn){
	        	if(buttons.length>0)buttons.push('-');
	        	buttons.push({tooltip:_localeMsg.js_islemler,cls:'x-btn-icon x-grid-menu', menu: grid.menuButtons});
	        }
	        grid.messageContextMenu = grid.menuButtons;
	        if(!grid.listeners)grid.listeners={}
	        grid.listeners.rowcontextmenu=fnRightClick;
	        
	    }
	}
	if(pk)grid._pk=pk;
	var buttons=[];
	grid._insertedItems={};

	if (grid.crudFlags){
		if(grid.crudFlags.insertEditMode){
			buttons.push({tooltip:_localeMsg.js_add,cls:'x-btn-icon x-grid-new', _grid:grid, handler:fnRowInsert2});
		}
		if(grid.crudFlags.remove){
			buttons.push({tooltip:_localeMsg.js_delete,cls:'x-btn-icon x-grid-delete', _grid:grid, handler:fnRowDelete2});
			grid._deletedItems=[];
		}
	}
	
	if(buttons.length>0){
		if(toExtraButtonsFlag){
			if(grid.extraButtons){
				buttons.push('-');
				buttons.push(grid.extraButtons);
			}
			grid.extraButtons=buttons;			
		} else {
			if(grid.extraButtons && grid.extraButtons.length>0){
				buttons.push('-');
				buttons.push(grid.extraButtons);
			}
			add_menu();
			if(grid.gridReport)addDefaultReportButtons(buttons, grid);
			addDefaultPrivilegeButtons(buttons,grid);
			grid.tbar=buttons;
		}
	} else if(!toExtraButtonsFlag){
		if(grid.extraButtons && grid.extraButtons.length>0)buttons.push(grid.extraButtons);
		add_menu();
		if(grid.gridReport)addDefaultReportButtons(buttons, grid);
		addDefaultPrivilegeButtons(buttons,grid);
		grid.tbar=buttons;
	}
/*	grid.ds.on('beforeload',function(){
		
	});*/
}


//Multi Main Grid
function addTab4DetailGridsWSearchForm(obj){
	var mainGrid = obj.detailGrids[0].grid,detailGridTabPanel=null;
	var searchFormPanel=  new Ext.FormPanel(Ext.apply(mainGrid.searchForm.getExtDef(),{
		region: 'north', bodyStyle:'padding:3px',
//		height: mainGrid.searchForm.defaultHeight||120,
		autoHeight:true,
		anchor: '100%',
		collapsible:true,
		title:mainGrid.searchForm.name,
		border: false,
		keys:{key:13,fn:function(ax,bx,cx){		
		detailGridTabPanel.getActiveTab().store.reload();		
		}}
	}));
	
    //--standart beforeload, ondbliclick, onrowcontextmenu


	//detail tabs
	var detailGridPanels = [];
	for(var i=0;i<obj.detailGrids.length;i++){
		if(obj.detailGrids[i].detailGrids){//master/detail olacak
			obj.detailGrids[0].grid.searchForm=undefined;
			var xmxm=addTab4GridWSearchFormWithDetailGrids(obj.detailGrids[i]);
			if(xmxm.items.items[0].xtype=='form'){ //ilk sıradaki gridin ,detail gridi varsa Search Formunu yok ediyor
				xmxm.items.items[0].destroy();	
			}
	
			var detailGridPanel=xmxm.items.items[0].items.items[0];
			xmxm.store=detailGridPanel.store;			
	        detailGridPanel.store._formPanel = searchFormPanel;
	        detailGridPanel.store._grid = mainGrid;
	        detailGridPanel.store.on('beforeload',function(a,b){
				if(a._grid.editMode)a._grid._deletedItems=[];
				if(a && a._formPanel.getForm())a.baseParams = Ext.apply(a._grid._baseParams || {},a._formPanel.getForm().getValues());//a._formPanel.getForm().getValues();
			});	        
	        xmxm.closable=false;	     
			detailGridPanels.push(xmxm);
			
		} else {
			var detailGrid = obj.detailGrids[i].grid;
			if(!detailGrid || !detailGrid.gridId)continue;
			detailGrid._masterGrid = mainGrid;
			if(detailGrid._ready){
				detailGridPanels.push(detailGrid);
				continue;
			}
			if(obj.detailGrids[i].pk)detailGrid._pk = obj.detailGrids[i].pk;
			var grdExtra={
				title:obj.detailGrids[i]._title_||detailGrid.name,stripeRows: true,id:'gr'+Math.random(),
				autoScroll:true, clicksToEdit: 1*_app.edit_grid_clicks_to_edit};
			var buttons=[];
	
	   		if(detailGrid.editGrid)addDefaultCommitButtons(buttons, detailGrid);	
			
			if(detailGrid.detailView && detailGrid.detailDlg){ //selectionMode:5
	            buttons.push({tooltip:_localeMsg.js_detay_penceresi, cls:'x-btn-icon x-grid-dpencere', _grid:detailGrid, handler: fnShowDetailDialog});
			}
			
			if(detailGrid.hasFilter){
				    if(buttons.length>0)buttons.push('-');
				    buttons.push({tooltip:_localeMsg.js_filtreyi_kaldir,cls:'x-btn-icon x-grid-funnel',_grid:detailGrid, handler:fnClearFilters});
			}
			
			if(detailGrid.crudFlags)addDefaultCrudButtons(buttons, detailGrid);
			if(detailGrid.moveUpDown)addMoveUpDownButtons(buttons, detailGrid);
			addDefaultSpecialButtons(buttons, detailGrid);
	
	
	         if(detailGrid.extraButtons){
	             if(buttons.length>0)buttons.push('-');
	             for(var j=0;j<detailGrid.extraButtons.length;j++){
	            	 detailGrid.extraButtons[j]._grid=detailGrid;
	            	 detailGrid.extraButtons[j].disabled=detailGrid.extraButtons[j]._activeOnSelection;
	             }
	             buttons.push(detailGrid.extraButtons);
	         }
	         if(detailGrid.menuButtons){
	         	for(var j=0;j<detailGrid.menuButtons.length;j++){
	         		detailGrid.menuButtons[j]._grid=detailGrid;
	             }
	         	detailGrid.menuButtons = new Ext.menu.Menu({enableScrolling:false, items:detailGrid.menuButtons});
	             if(1*_app.toolbar_edit_btn){
	             	if(buttons.length>0)buttons.push('-');
	             	buttons.push({tooltip:_localeMsg.js_islemler,cls:'x-btn-icon x-grid-menu', disabled:true, _activeOnSelection:true, menu: detailGrid.menuButtons});
	             }
	         }
	 		if(detailGrid.gridReport)addDefaultReportButtons(buttons, detailGrid);
	 		addDefaultPrivilegeButtons(buttons,detailGrid);

	 		
	         if(detailGrid.pageSize){ // paging'li toolbar
	             var tbarExtra = {xtype:'paging', store: detailGrid.ds, pageSize: detailGrid.pageSize, displayInfo: detailGrid.displayInfo};
	             if(buttons.length>0)tbarExtra.items=organizeButtons(buttons);
	             grdExtra.tbar=tbarExtra;
	         } else if(buttons.length>0){//standart toolbar
	             grdExtra.tbar=organizeButtons(buttons);
	         }
	
	        var eg = detailGrid.master_column_id ? (detailGrid.editGrid ? Ext.ux.maximgb.tg.EditorGridPanel : Ext.ux.maximgb.tg.GridPanel) : (detailGrid.editGrid ? Ext.grid.EditorGridPanel : Ext.grid.GridPanel);
			var detailGridPanel = new eg(Ext.apply(detailGrid,grdExtra));
			detailGrid._gp=detailGridPanel;
		    if(detailGrid.editGrid){
		    	detailGridPanel.getColumnModel()._grid=detailGrid;
		    	if(!detailGrid.onlyCommitBtn){
		    		detailGridPanel.getColumnModel().isCellEditable=function(colIndex,rowIndex){
		    			if(this._grid._isCellEditable && this._grid._isCellEditable(colIndex,rowIndex,this._grid)===false)return false;
		    			return this._grid.editMode;
		    		};
		    	} else if(detailGrid._isCellEditable)mainGridPanel.getColumnModel().isCellEditable=function(colIndex,rowIndex){
					return this._grid._isCellEditable(colIndex,rowIndex,this._grid);
				};
		    }
	
			if(detailGrid.menuButtons/* && !1*_app.toolbar_edit_btn*/){
				detailGridPanel.messageContextMenu = detailGrid.menuButtons;
				detailGridPanel.on('rowcontextmenu', fnRightClick);
	        }
	/*	    if(detailGrid.saveUserInfo)detailGridPanel.on("afterrender",function(a,b,c){
				detailGridPanel.getView().hmenu.add('-',{text: 'Mazgal Ayarları',cls:'grid-options1',menu: {items:[{text:'Mazgal Ayarlarını Kaydet',handler:function(){saveGridColumnInfo(grid.getColumnModel(),mainGrid.gridId)}},
			    	                                                                                       {text:'Varsayılan Ayarlara Dön',handler:function(){resetGridColumnInfo(mainGrid.gridId)}}]}});
		    }); */
			if(detailGridPanel.crudFlags && detailGridPanel.crudFlags.edit/* && 1*_app.toolbar_edit_btn*/){detailGridPanel.on('rowdblclick',fnRowEditDblClick);}
	//		grid2grid(mainGridPanel,detailGridPanel,obj.detailGrids[i].params);
			detailGridPanel.store._formPanel = searchFormPanel;
			detailGridPanel.store.on('beforeload',function(a,b){
				if(a._grid.editMode)a._grid._deletedItems=[];
				if(a && a._formPanel.getForm())a.baseParams = Ext.apply(a._grid._baseParams || {},a._formPanel.getForm().getValues());//a._formPanel.getForm().getValues();
			});
	
			
	        if(buttons.length>0){
	        	detailGridPanel.getSelectionModel().on("selectionchange",function(a,b,c){
	        		if(!a || !a.grid)return;
	        		var titems = a.grid.getTopToolbar().items.items;
	        		for(var ti=0;ti<titems.length;ti++){
	        			if(titems[ti]._activeOnSelection)
	        				titems[ti].setDisabled(!a.hasSelection());
	        		}
	        	});
	        }
	        detailGridPanel.store._formPanel = searchFormPanel;
	        detailGridPanel.store._grid = mainGrid;
	        detailGridPanel.store.on('beforeload',function(a,b){
				if(a._grid.editMode)a._grid._deletedItems=[];
				if(a && a._formPanel.getForm())a.baseParams = Ext.apply(a._grid._baseParams || {},a._formPanel.getForm().getValues());//a._formPanel.getForm().getValues();
			});

			detailGridPanels.push(detailGridPanel);
		}
	} 
	detailGridTabPanel =new Ext.TabPanel({
		region:'center',
		enableTabScroll:true,
		activeTab:0,
		visible:false,
		items:detailGridPanels
	});
    var p = {
	    layout: 'border',
        title: obj._title_||mainGrid.name,
//        id: obj.id||'x'+new Date().getTime(),
        border: false,
        closable:true,
        items:[searchFormPanel,detailGridTabPanel]
    };
//    p.iconCls='icon-cmp';
    p = new Ext.Panel(p);
	p._windowCfg = {layout:'border'};
    return p;
}

var lastNotificationCount = 0;
function promisUpdateNotifications(obj){
	if(!obj || typeof obj.new_notification_count=='undefined')return;
	var newCount=1*obj.new_notification_count;
	if(lastNotificationCount!=newCount){
		var ctrl=Ext.getCmp('id_not_label');
		if(ctrl){
			if(newCount==0)ctrl.hide();
			else {ctrl.show();ctrl.setText(newCount);}
			lastNotificationCount=newCount;
		}
	}
}
/* DEPRECATED
function check4Notifications(nt){
	promisRequest({
		url:'ajaxQueryData?_qid=1488', 
		requestWaitMsg:false, 
		timeout:120000,
		params:{},
		successCallback: function(json){
			promisUpdateNotifications({new_notification_count:json.data[0].new_notifications});	
		}
	});
}
*/

function check4Feeds(fd){
    var psayfam=Ext.get("promis_sayfam");
    if(psayfam && psayfam.isVisible() && _feedSearchStore){
        _feedSearchStore.reload();
    } else {
//        Ext.infoMsg.msg('Feed',objProp(json.feed),2);//TODO
    }
}

function renderParentRecords(rs,sp){
	var ss='',r=null;
	if (!sp)sp=1;
	if(rs && rs.length && rs.length>(sp)){
		for(var qi=rs.length-1;qi>=0;qi--){
			r=rs[qi];
			if(qi!=rs.length-1)ss+='<br>';
			for(var zi=rs.length-1;zi>qi;zi--)ss+=' &nbsp; &nbsp;';
			ss+='&gt '+(qi!=0 ? (r.tdsc) : ('<b>'+r.tdsc+'</b>'));
			if(r.dsc)ss+=(qi!=0 ? ': <a href=# onclick="return fnTblRecEdit('+r.tid+','+r.tpk+');">'+r.dsc+'</a>':': <b><a href=# onclick="return fnTblRecEdit('+r.tid+','+r.tpk+');">'+r.dsc+'</a></b>');// else ss+=': (...)';
		}
	}
	if(ss){
		ss='<div class="dfeed">'+ss+'</div>';
		if(r && r.tcc && r.tcc>0)ss+=' · <a href=# onclick="return fnTblRecComment('+r.tid+','+r.tpk+');">Yorumlar ('+r.tcc+')</a>';
	}
	return ss;
}


function renderParentRecords2(rs,sp){//TODO: bizzat java 'ya gore
	var ss='',r=null;
	if (!sp)sp=1;
	if(rs && rs.length && rs.length>(sp)){
		for(var qi=rs.length-1;qi>=0;qi--){
			r=rs[qi];
			if(qi!=rs.length-1)ss+='<br>';
			for(var zi=rs.length-1;zi>qi;zi--)ss+=' &nbsp; &nbsp;';
			ss+='&gt '+(qi!=0 ? (r._tableStr) : ('<b>'+r._tableStr+'</b>'));
			if(r.recordDsc)ss+=(qi!=0 ? ': <a href=# onclick="return fnTblRecEdit('+r.tableId+','+r.tablePk+');">'+r.recordDsc+'</a>':': <b><a href=# onclick="return fnTblRecEdit('+r.tableId+','+r.tablePk+');">'+r.recordDsc+'</a></b>');// else ss+=': (...)';
		}
	}
	if(ss){
		ss='<div class="dfeed">'+ss+'</div>';
		if(r && r.commentCount && r.commentCount>0)ss+=' · <a href=# onclick="return fnTblRecComment('+r.tableId+','+r.tablePk+');">Yorumlar ('+r.commentCount+')</a>';
	}
	return ss;
}

/*//DEPRECATED
var queuedThreads = {};
var checkQueuedThreads = new Ext.util.DelayedTask(function(){
	var params='';
	for(var qi in queuedThreads)if(queuedThreads[qi]){
		params+=','+qi;
	}
	if(params)params=params.substring(1);
	promisRequest({
		url: 'ajaxCheckThreads',
		params:{thread_ids:params},
		successCallback: function(js){
			if(js.success && js.data){
				var newQueuedThreads={}, bx=false;
				for(var zi=0;zi<js.data.length;zi++)switch(js.data[zi].type){
				case	'thread':bx=true;newQueuedThreads[js.data[zi].thread_id]=js.data[zi].is_alive;break;
				case	'db_func':
					var msgx='';
					switch(js.data[zi].db_func.db_func_id){
					case	348://mail
						msgx=js.data[zi].db_func.success && !js.data[zi].db_func.errorMsg ?  _localeMsg.js_eposta_basariyla_iletildi : _localeMsg.js_eposta_iletilemedi_giden_kutusuna_bakin + '<br>HATA: '+ js.data[zi].db_func.errorMsg || 'tanimli degil';
						break;
					case	328://sms
						msgx=js.data[zi].db_func.success && !js.data[zi].db_func.errorMsg ? 'SMS Basariyla iletildi' :  'SMS Gondermede Hata: '+ js.data[zi].db_func.errorMsg || 'tanimli degil';
						break;
					}
					Ext.infoMsg.msg(_localeMsg.js_tamam,msgx);
					break;
				case	'exception':Ext.infoMsg.msg(_localeMsg.js_hata,js.data[zi].exception.error);break;
				default:
					Ext.infoMsg.msg(_localeMsg.js_hata,objProp(js.data[zi]));
				}
				queuedThreads=newQueuedThreads;
				if(bx)checkQueuedThreads.delay(_app.thread_check_interval ? 1*_app.thread_check_interval:2000);
			}
		}
	});
});
*/
function manuelDateValidation(date1, date2, blankControl, dateControl){
	if (blankControl){
		//tarih alanlarının boş olup olmadığı kontrol ediliyor
		if (typeof date1!='undefined'){
			if (date1.allowBlank == false && date1.getValue() == ''){
				Ext.Msg.alert(_localeMsg.js_hata, _localeMsg.js_blank_text +' (' + date1.fieldLabel + ')');
				return false;
			}
		}
		
		if (typeof date2!='undefined'){
			if (date2.allowBlank == false && date2.getValue() == ''){
				Ext.Msg.alert(_localeMsg.js_hata, _localeMsg.js_blank_text + ' (' + date2.fieldLabel + ')');
				return false;
			}
		}
	}
		
	if (dateControl && typeof date1!='undefined' && typeof date2!='undefined'){
		//birinci tarih ikinci tarihten küçük yada eşit olup olmadığı kontrol ediliyor
		if (date1.getValue()>date2.getValue()){
			Ext.Msg.alert(_localeMsg.js_hata, 'İlk Tarih İkinci Tarihten Küçük Olamaz');
			return false;		
		}		
	}
	return true;
}

/*
 * LovCombo içerisinde aranan değerler seçili mi ?
 */

function checkIncludedLovCombo(search_ids, checked_ids){
	var result = false;
	var xsearch_ids = search_ids.split(',');
	var xchecked_ids = checked_ids.split(',');
	
	for(var i=0; i<xchecked_ids.length; i++){
		for(var j=0; j<xsearch_ids.length; j++){
			if(xchecked_ids[i] == xsearch_ids[j]){
				result = true;
				break;
			}
		}		
	}
	return result;
}

/*
 * Field value böyle alınmalı
 */

function getFieldValue(field){
	if(field)return field._controlTip != 101 ? field.getValue() : field.hiddenValue;
	else return null;
}

function setFieldValue(field,value){
	if(field){
		if(field._controlTip != 101)field.setValue(value); 
		else {field.hiddenValue=value;field.setRawValue(value);}
	}
}

/*
 * Eğer displayfield ise event tetiklenmeyecek ama fonksiyon çalışacak,
 * Displayfield değil ise event tetiklenerek fonksiyon çalışacak
 */

function applyEvent2Field(field, event, func, triggerOnRender){
	if(field){
		if(field._controlTip == 101){
			func();
		}
		else{
			field.on(event, function(){func();});
			if(triggerOnRender)field.fireEvent(event);
		}
	}
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

function findAddressFromStr(adr){
	var geocoder = new google.maps.Geocoder();
	var request = {address: adr};
	var callBack = function(geocoderResults, geocoderStatus) {
	    if(geocoderStatus === 'OK') {
	        var location = geocoderResults[0].geometry.location;
	        showGMap(_localeMsg.js_harita_konumu,{lat:location.lat(), lng:location.lng()},null);
	    }
	    else{
	    	Ext.Msg.show({title:_localeMsg.js_bilgi,msg:_localeMsg.js_adres_bulunamadi,icon:Ext.MessageBox.WARNING})
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

function findInvalidFields(bf){
    var result = [], it = bf.items.items, l = it.length, i, f;
    for (i = 0; i < l; i++) {
        if(!(f = it[i]).disabled && !f.isValid()){
            result.push(f);
        }
    }
    return result;
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

function getSimpleCellMap(cells){
	var jsMap = {}
	if(!cells || !cells.length)return jsMap;
	for(var qi=0;qi<cells.length;qi++){
		jsMap[cells[qi].id]=cells[qi];
	}
	return jsMap;
}

function timeDifDt(cd, timeTip, timeDif){
	if(timeDif){
        var tq = timeDif.split(":");
        if(tq.length>1){
            var tz = tq[0]*60*60*1000 + tq[1]*60*1000;
            if(tq.length>2)tz+=tq[2]*1000;
            switch(timeTip){
            case    0:cd.setHours(0,0,0,0);cd = new Date(cd.getTime() + tz);break;
            case    1:cd = new Date(cd.getTime() + tz);break;
            case    2:cd = new Date(cd.getTime() - tz);break;
            }
        }
    }
	return cd;
}

function	fileNameRender(a,b,c){
	if( _app.file_attach_view_access_flag && 1*_app.file_attach_view_access_flag && _app.file_attach_view_file_tips && _app.file_attach_view_roles && c.data.file_type_id
	&& (","+_app.file_attach_view_file_tips+",").indexOf(","+c.data.file_type_id+",") > -1 && (","+_app.file_attach_view_roles+",").indexOf(","+_scd.roleId+",") > -1){
//		return '<a target=_blank href="showPage?_tid=975&_fai='+c.data.file_attachment_id+'"><b style="color:#F00">'+a+'</b></a>'
		return '<a target=_blank href=# onclick="return openPopup(\'showPage?_tid=975&_fai='+c.data.file_attachment_id+'\',\'1\',800,600,1);"><b style="color:#F00">'+a+'</b></a>'
	} else
		return '<a target=_blank href="dl/'+encodeURIComponent(a)+'?_fai='+c.data.file_attachment_id+'"><b>'+a+'</b></a>';
}

function getUsers4Chat(users){
	if(!users || users.length==0)return "";
    var si = "";
    for(var qi=0;qi<users.length;qi++)si+=", <a href=# onclick=\"return openChatWindow("+users[qi].userId+",'"+users[qi].userDsc+"',true)\"><b>"+users[qi].userDsc+"</b></a>";
    return si.substr(2);
}

function safeIsEqual(v1,v2){
	if(v1==null){
		return v2==null;
	} else return v1==v2;
}