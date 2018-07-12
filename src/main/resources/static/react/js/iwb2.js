/**array color name */
const dgColors				= ['warning','secondary','danger','primary','success','info']
const dgColors2				= ["primary","info","secondary","gray-700","gray-500","gray-400","gray-700"];
const detailSpinnerColors2	= ["#187da0","#2eadd3","darkorange","#187da0","#4d6672","#626a70","#66767d"];
const dBGColors2			= [,,"#de9338","#222",,,,,];
const dgColors3				= ["gray-700","danger","gray-500","gray-400","gray-700","info","secondary","secondary","secondary","warning"];
/**ReactRouterDOM for routing*/
const HashRouter 			= ReactRouterDOM.HashRouter;
const Route 				= ReactRouterDOM.Route;
const Switch 				= ReactRouterDOM.Switch;
const Link 					= ReactRouterDOM.Link;
const NavLink 				= ReactRouterDOM.NavLink;
const Redirect 				= ReactRouterDOM.Redirect;
/**Reactstrap components*/
const Container 			= Reactstrap.Container;
const Row 					= Reactstrap.Row;
const Col 					= Reactstrap.Col;
const Card 					= Reactstrap.Card;
const CardHeader 			= Reactstrap.CardHeader;
const CardFooter			= Reactstrap.CardFooter;
const CardBlock 			= Reactstrap.CardBody;
const CardBody 				= Reactstrap.CardBody;
const Button 				= Reactstrap.Button;
const ButtonGroup 			= Reactstrap.ButtonGroup;
const Breadcrumb 			= Reactstrap.Breadcrumb;
const BreadcrumbItem		= Reactstrap.BreadcrumbItem;
const Badge 				= Reactstrap.Badge;
const Label 				= Reactstrap.Label;
const Input 				= Reactstrap.Input;
const InputGroup 			= Reactstrap.InputGroup;
const InputGroupAddon		= Reactstrap.InputGroupAddon;
const InputGroupButton		= Reactstrap.InputGroupButton;
const ButtonDropdown		= Reactstrap.ButtonDropdown;
const Dropdown 				= Reactstrap.Dropdown;
const DropdownToggle		= Reactstrap.DropdownToggle;
const DropdownMenu 			= Reactstrap.DropdownMenu;
const DropdownItem 			= Reactstrap.DropdownItem;
const Form 					= Reactstrap.Form;
const FormGroup 			= Reactstrap.FormGroup;
const FormText 				= Reactstrap.FormText;
const Table 				= Reactstrap.Table;
const Pagination 			= Reactstrap.Pagination;
const PaginationItem		= Reactstrap.PaginationItem;
const PaginationLink		= Reactstrap.PaginationLink;
const Nav 					= Reactstrap.Nav;
const NavItem 				= Reactstrap.NavItem;
const NavLinkS 				= Reactstrap.NavLink;
const NavbarToggler 		= Reactstrap.NavbarToggler;
const NavbarBrand 			= Reactstrap.NavbarBrand;
const Modal 				= Reactstrap.Modal;
const ModalHeader 			= Reactstrap.ModalHeader;
const ModalBody 			= Reactstrap.ModalBody;
const ModalFooter 			= Reactstrap.ModalFooter;
const TabContent 			= Reactstrap.TabContent;
const TabPane 				= Reactstrap.TabPane;
const ListGroup 			= Reactstrap.ListGroup;
const ListGroupItem 		= Reactstrap.ListGroupItem;
/**FW Customization */
const Select 				= window.Select;
const Popper 				= window.Popper;
const findDOMNode 			= ReactDOM.findDOMNode;
/**React  */
var _						= React.createElement;
/** DXReactCore imports */
const Getter 				= DXReactCore.Getter;
const Template				= DXReactCore.Template;
const TemplateConnector		= DXReactCore.TemplateConnector; 
const TemplatePlaceholder	= DXReactCore.TemplatePlaceholder;
const Plugin				= DXReactCore.Plugin;

var _dxgrb					= DXReactGridBootstrap4;
var _dxrg					= DXReactGrid;
/** iwb object */
var iwb={
	grids:{},
	forms:{},
	pages:{},
	debug:false,
	debugRender:false,
	debugConstructor:false,
	detailPageSize:10,
	logo:'<svg width="32" height="22" xmlns="http://www.w3.org/2000/svg" x="0px" y="0px" viewBox="0 0 300 202.576" enable-background="new 0 0 300 202.576" class="white-logo standard-logo middle-content"><g id="svg_14"><path id="svg_15" d="m46.536,31.08c0,10.178 -8.251,18.429 -18.429,18.429c-10.179,0 -18.429,-8.251 -18.429,-18.429c0,-10.179 8.25,-18.43 18.429,-18.43c10.177,0 18.429,8.251 18.429,18.43" fill="darkorange"></path><path id="svg_16" d="m220.043,62.603c-0.859,0 -1.696,0.082 -2.542,0.128c-0.222,-0.007 -0.429,-0.065 -0.654,-0.065c-0.674,0 -1.314,0.128 -1.969,0.198c-0.032,0.003 -0.064,0.003 -0.096,0.005l0,0.005c-9.241,1.04 -16.451,8.79 -16.451,18.309c0,9.555 7.263,17.326 16.554,18.319c0,0.03 0,0.063 0,0.094c0.482,0.027 0.953,0.035 1.428,0.05c0.182,0.006 0.351,0.055 0.534,0.055c0.088,0 0.17,-0.025 0.258,-0.026c0.96,0.02 1.927,0.026 2.938,0.026c16.543,0 29.956,13.021 29.956,29.564c0,16.545 -13.412,29.956 -29.956,29.956c-15.521,0 -28.283,-11.804 -29.803,-26.924l0,-107.75l-0.054,0c-0.289,-9.926 -8.379,-17.896 -18.375,-17.896c-9.995,0 -18.086,7.971 -18.375,17.896l-0.053,0l0,118.529c0,10.175 11.796,52.85 66.661,52.85c36.815,0 66.661,-29.846 66.661,-66.662c-0.001,-36.816 -29.847,-66.661 -66.662,-66.661" fill="#20a8d8"></path><path id="svg_17" d="m153.381,143.076l-0.049,0c-0.805,8.967 -8.252,16.021 -17.428,16.021s-16.624,-7.054 -17.428,-16.021l-0.048,0l0,-66.298l-0.045,0c-0.245,-9.965 -8.36,-17.979 -18.384,-17.979s-18.139,8.014 -18.384,17.979l-0.045,0l0,66.298l-0.05,0c-0.805,8.967 -8.252,16.021 -17.428,16.021c-9.176,0 -16.624,-7.054 -17.429,-16.021l-0.048,0l0,-66.298l-0.045,0c-0.246,-9.965 -8.361,-17.978 -18.384,-17.978c-10.024,0 -18.139,8.014 -18.384,17.979l-0.046,0l0,66.298c0.836,29.321 24.811,52.849 54.335,52.849c13.79,0 26.33,-5.178 35.906,-13.636c9.577,8.458 22.116,13.636 35.906,13.636c14.604,0 27.85,-5.759 37.61,-15.128c-15.765,-13.32 -20.132,-31.532 -20.132,-37.722" fill="#bbb"></path></g></svg>',
	detailSearch:()=>false,
	openForm:(url)=>{
		if(url)iwb.openTab('1-'+Math.random(),url);
		return false;
	},
	sidebarToggle:(e)=>{
    	e.preventDefault();
		document.body.classList.toggle('sidebar-hidden');
	},
	sidebarMinimize:(e)=>{
		e.preventDefault();
		document.body.classList.toggle('sidebar-minimized');
		document.body.classList.toggle('brand-minimized');
	},
	mobileSidebarToggle:(e)=>{
		e.preventDefault();
		document.body.classList.toggle('sidebar-mobile-show');
	},
	asideToggle:(e)=>{
		e.preventDefault();
		if(iwb.asideToggleX)iwb.asideToggleX(e)
		else document.body.classList.toggle('aside-menu-hidden');
	},
	killGlobalSearch:()=>{
		iwb.onGlobalSearch2=false;
		var c=document.getElementById('id-global-search');
		if(!c)return;
		c.value='';
		c.classList.remove('global-search-active');	
	},
	JSON2URI:(j)=>{
		if(!j)return '';
		var s=''
		for (key in j)s += encodeURIComponent(key)+"="+(j[key]===null || j[key]===false ? '':encodeURIComponent(j[key]))+"&";
		return s;
		
	},
	onGlobalSearch:(v)=>{
		var c=document.getElementById('id-global-search');
		var cc=c.classList.contains('global-search-active');
		if((c.value && !cc) || (!c.value && cc))c.classList.toggle('global-search-active');
		if(iwb.onGlobalSearch2)iwb.onGlobalSearch2(v);
	},
	getFieldRawValue:(field, extraOptions)=>{
		if(!field || !field.value)return iwb.emptyField;
		var options = extraOptions || field.options;
		if(!options || !options.length){
			var value = field.value;
			if(typeof value=='undefined' || value=='')return iwb.emptyField;
			return _('b',{className:'form-control'},value);
			 
		}
		var optionsMap = {}
		options.map((o)=>{optionsMap[o.id]=o.dsc;})
		if(field.multi){
			var value = [], vs = field.value;
			if(!Array.isArray(vs))vs=vs.split(',');
			vs.map((v)=>{value.push(optionsMap[v])});
			if(!value.length)return iwb.emptyField;
			return _('b',{className:'form-control'},value.join(', '));
		}
		var value = field.value;
		if(value.id)value = value.id;
		value = optionsMap[value];	
		if(value==undefined || value=='')return iwb.emptyField;
		return _('b',{className:'form-control'},value);
	},
	request:(cfg)=>{
		if(!window.fetch){alert('ERROR! window.fetch not supported');return false;}
		if(!cfg || !cfg.url){alert('ERROR! config missing');return false;}
		fetch(cfg.url, {
			body: JSON.stringify(cfg.params||{}), // must match 'Content-Type' header
			cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
			credentials: 'same-origin', // include, same-origin, *omit
			headers: {
			  'content-type': 'application/json'
			},
			method: 'POST', // *GET, POST, PUT, DELETE, etc.
			mode: 'cors', // no-cors, cors, *same-origin
			redirect: 'follow', // *manual, follow, error
			referrer: 'no-referrer', // *client, no-referrer
		}).then(function(response){
			// status "0" to handle local files fetching (e.g. Cordova/Phonegap etc.)
			if (response.status === 200 || response.status === 0) {
				return response.json();
			} else {
				return Promise.reject(new Error(response.text() || response.statusText))
			}})
		.then(
			function(result){
				if(cfg.callback && cfg.callback(result, cfg)===false)return;
				if(result.success){
					if(cfg.successCallback)cfg.successCallback(result, cfg);
				} else {
					if(cfg.errorCallback && cfg.errorCallback(result, cfg)===false)return;
					iwb.requestErrorHandler(result);
				}
			},
			function(error){
				if(cfg.errorCallback && cfg.errorCallback({error:error}, cfg)===false)return;
				toastr.error(error || 'Unknown ERROR','Request Error');
	//	    	alert('ERROR! ' + error);
			}
		);
	},
	requestErrorHandler:(obj)=>{
		if(obj.errorType){
			switch(obj.errorType){
			case	'session':return iwb.showLoginDialog();
			case	'validation':
				toastr.error(obj.errors.join('<br/>'),'Validation Error');
				break;
			default:
				toastr.error(obj.errorMsg || 'Unknown ERROR',obj.errorType+' Error');
			}
		} else {
			toastr.error(obj.errorMsg || 'Unknown ERROR','Request Error');
		}
	},
	getFormValues:(f)=>{
		if(!f || !f.elements)return {}
		var e = f.elements, p={};
		for(var qi=0;qi<e.length;qi++){
			if(e[qi].name)switch(e[qi].type){
			case	'checkbox': p[e[qi].name] = e[qi].checked;break;
			case	'hidden': p[e[qi].name] = p[e[qi].name]===undefined ? e[qi].value : p[e[qi].name] +','+ e[qi].value;break;
			default:p[e[qi].name]=e[qi].value;
			}
		}
		return p;
	},
};
var ajaxErrorHandler= iwb.requestErrorHandler;
iwb.emptyField 		= _('i',{className:'raw-field-empty'},_('br'),' ','(boş)'),
iwb.loadPage		= function(cfg){}
iwb.ui				= {
	buildPanel		: function(c){
	if(!c.grid.pk)c.grid.pk=c.pk||c._pk;
	if(!c.grid.detailGrids)c.grid.detailGrids=c.detailGrids||false;
	return _(XPage,c);
}}
function disabledCheckBoxHtml(row, cell){ //TODO
//		return _('img',{border:0,src:'../images/custom/'+(f ?'':'un')+'checked.gif'});
	return row[cell] && 1*row[cell] ? _('i',{className:'fa fa-check', style:{color: 'white',background: '#4dbd74', padding: 5, borderRadius: 25}}) : null;// _('i',{className:'fa fa-check', style:{color: 'white',background: 'red', padding: 5, borderRadius: 25}});
}
function gridUserRenderer(row, cell){ //TODO
	return row[cell+'_qw_'];
}
function gridQwRendererWithLink(t){//tableId
	return	function(row, cell){
		return row[cell+'_qw_'];
	}
}
function editGridComboRenderer(cell, combo){
	if(!combo || !combo.options)return	function(row){
		return '?x?: ' +row[cell];
	}
	var moptions={};
	combo.options.map(function(o){moptions[o.id]=o;})
	combo.moptions=moptions;
	return	function(row){
		var c = row[cell];
		if(!c)return '';
		var  o = combo.moptions[c];
		return o ? o.dsc:('???: ' +row[cell]);
	}	
}
function editGridLovComboRenderer(cell, combo){
	if(!combo || !combo.options)return	function(row){
		return '?x?: ' +row[cell];
	}
	var moptions={};
	combo.options.map(function(o){moptions[o.id]=o;})
	combo.moptions=moptions;
	return	function(row){
		var c = row[cell];
		if(!c)return '';
		c = c.split(',');
		return c.map(function(o){return combo.moptions[o];});
	}	
}
function fileAttachmentHtml(row,cell){//TODO
	return row[cell] && 1*row[cell] ? _('i',{className:'icon-paper-clip'}):null;
} 
function vcsHtml(row, cell){
	return row[cell] && 1*row[cell] ? _('i',{className:'icon-social-github'}):null;
}
function pictureHtml(row, cell){	
	return row[cell] && 1*row[cell] ? _('i',{className:'icon-picture'}):null;
}
function mailBoxRenderer(row, cell){
	return row[cell] && 1*row[cell] ? _('i',{className:'icon-envelope'}):null;
} 
function fmtDateTime(x){
	return x ? moment(x).format('DD/MM/YYYY HH:ss') : "";
}
function fmtShortDate(x){
	return x ? moment(x).format('DD/MM/YYYY') : "";
}
function strShortDate(x){
	return x ? x.substr(0,10) : "";
}
function accessControlHtml(){
	return null;
}
function strDateTime(x){
	return x || "";
}
function strDateTimeAgo(x){
	return x || "";
}
function getStrapSize(w){
	if(w>=700)return 'lg';
	if(w>=400)return 'md';
	return 'sm';
}
function getMasterGridSel(a,sel){
	return sel;
}
class XTabForm extends React.PureComponent{
	constructor(props){
		if(iwb.debugConstructor)if(iwb.debug)console.log('XTabForm.constructor',props);
		super(props);
		this.state = {viewMode:this.props.cfg.a==1};
		this.onSubmit = this.onSubmit.bind(this);
		this.toggleViewMode = this.toggleViewMode.bind(this);
	}
	toggleViewMode(){
		this.setState({viewMode:!this.state.viewMode});
	}
	onSubmit(e){
		if(e && e.preventDefault)e.preventDefault();
		var selfie = this;
		if(this.form)this.form.submit({callback:function(json,cfg){
			var url='showForm';
			if(json.outs){
				url+='?a=1&_fid='+json.formId;
				for(var k in json.outs)url+='&t'+k+'='+json.outs[k];
			} else{
				url+=cfg.url.substring('ajaxPostForm'.length);
			}
//	    	var url = 'showForm?_renderer=react16&a=1&_fid='+props.crudFormId+pkz; 


			toastr.success('İlgili kaydı görmek için <a href=# onClick="return iwb.openForm(\''+url+'\')">tıklayınız</a>','Başarıyla Kaydedildi',{timeOut:3000});
			var parent = selfie.props.parentCt;
			if(parent){
				parent.closeTab();
				iwb.onGlobalSearch2('');
			}
		}});
		else alert('this.form not set');
		return false;
	}
	render() {
		if(iwb.debugRender)if(iwb.debug)console.log('XTabForm.render',this.props)
		var formBody = _(this.props.body,{parentCt:this, viewMode:this.state.viewMode});
		if(!formBody)return null;
		return _(Form, {onSubmit:function(e){e.preventDefault();}}, 
           		 _(CardBlock, {className: 'card-body'},
       	    		  _("h3", { className: "form-header" }, /*_("i",{className:"icon-star form-icon"})," ",*/this.props.cfg.name, ' ', this.state.viewMode && _(Button,{color:'light', className:'btn-form-edit',onClick:this.toggleViewMode},_("i",{className:"icon-pencil"})," ",'Düzenle'), ' ', this.state.viewMode && _(Button,{color:'light', className:'btn-form-edit',onClick:this.props.parentCt.closeTab},'Kapat')
     	    				  	, _(Button,{className:'float-right btn-round-shadow hover-shake',color:'danger'},_('i',{className:'icon-options'}))
       	    				  	, ' ', _(Button,{className:'float-right btn-round-shadow mr-1',color:'light'},_('i',{className:'icon-bubbles'}))
       	    				  	, ' ',  _(Button,{className:'float-right btn-round-shadow mr-1',color:'light'},_('i',{className:'icon-paper-clip'}))
       	    				  	)
       	    		  ,_("hr")
       	    		  , formBody),
       	    		!this.state.viewMode && _(CardFooter, {style:{padding: "1.1rem 1.25rem"}},_(Button,{type:'submit',color:'submit', className:'btn-form mr-1', onClick:this.onSubmit}
       	    			,' ','KAYDET',' '),' ',_(Button,{color:"light", style:{border: ".5px solid #e6e6e6"}, className:'btn-form', onClick:this.props.parentCt.closeTab},'VAZGEÇ'))
       	);
	} 
}

class XModal extends React.Component {
	constructor(props){
		super(props);
		this.open = cfg => {
			this.setState({
				modal			: true, 
				title			: cfg.title||'Form', 
				color			: cfg.color||'primary',
				size			: cfg.size||'lg',
				body			: cfg.body,
				style			: cfg.style,
				footer			: cfg.footer,
				modalBodyProps	: cfg.modalBodyProps||{},props:cfg.props||{}});
			return false;
		}; 
		this.close= () => { this.setState({modal:false}); } 
		this.toggle = () => { this.setState({ modal: !this.state.modal }); }
		this.state={modal:false};
		iwb.showModal=this.open;
		iwb.closeModal=this.close;
	}
		
    render() {
		const { modal, footer, size, style, color, title, modalBodyProps, body } = this.state;
    	return modal && _(Modal, {
			keyboard:true,
			backdrop:footer!==false ? 'static':true, 
			toggle: this.toggle, 
			isOpen: modal, 
			className: 'modal-'+size+' primary',
			style
			},
				_(ModalHeader, {
					toggle:this.toggle,
					className:'bg-'+color
					}, title),
				_(ModalBody,modalBodyProps, body),
				
				// !footer && _(ModalFooter, null,
				// 	_(Button, { 
				// 		className:'btn-form',
				// 		color: 'teal',
				// 		onClick: this.toggle
				// 		},"KAYDET"),
				// 	' ',
				// 	_(Button, {
				// 		className:'btn-form',
				// 		color: "light",
				// 		style:{border: ".5px solid #e6e6e6"},
				// 		onClick: this.toggle
				// 	}, "VAZGEÇ")
				// ),

				footer && _(React.Fragment, null,
					footer
            )
				
          );
    }
}

class	XLoginDialog extends React.Component {
	constructor(props){
		super(props);
		this.state={modal:false, msg:false};
		this.open=this.open.bind(this); 
		this.login=this.login.bind(this);
		iwb.showLoginDialog = this.open;
	}
	open(){
	    this.setState({modal: !0});
	}
	login(){
		var self = this;
		var passWord = document.getElementById('id-password').value;
		if(!passWord){
			self.setState({msg: 'Önce Şifre Giriniz' });
			return false;
		}
		iwb.request({url: 'ajaxAuthenticateUser?userRoleId='+_scd.userRoleId+'&locale='+_scd.locale+(_scd.projectId ? '&projectId='+_scd.projectId:''),
		params:{customizationId:_scd.customizationId, userName: _scd.userName, passWord: passWord, locale: _scd.locale},
		callback:function(j){
			if(j.success){
				if(!j.waitFor){
					if(j.session)_scd=j.session;
					self.setState({modal: false, msg:false});
				} else {
					self.setState({msg: 'TODO! ' + j.waitFor });
				}
				return false;
			} else {
				self.setState({msg: j.errorMsg });
			}
		},errorCallback:function(j){
		    this.setState({msg: 'Olmadi'});
		}});
	}
    render() {
    	return _(Modal, { keyboard:false ,backdrop:'static', toggle: this.toggle, isOpen: this.state.modal, centered:true, className: 'modal-sm primary' },
               _(ModalBody,null,
   	                  _("h1", null, "Login"), 
   	                  _("p", {className: "text-muted", style:{color:(this.state.msg? "red !important":"")}}, this.state.msg || "Session Timeout"), 
   	              	_(InputGroup, {
   	              	    className: "mb-3"
   	              	}, _("div", {
   	              	    className: "input-group-prepend"
   	              	}, _("span", {
   	              	    className: "input-group-text"
   	              	}, _("i", {
   	              	    className: "icon-user"
   	              	}))), _(Input, {
   	              	    type: "text", readOnly:true, value:_scd.userName,
   	              	    placeholder: "Username"
   	              	})), _(InputGroup, {
   	              	    className: "mb-4"
   	              	}, _("div", {
   	              	    className: "input-group-prepend"
   	              	}, _("span", {
   	              	    className: "input-group-text"
   	              	}, _("i", {
   	              	    className: "icon-lock"
   	              	}))), _(Input, {
   	              	    type: "password",id:'id-password',
   	              	    placeholder: "Password"
   	              	}))
   	                ),
	                _(ModalFooter, null,
	                  _(Button, { color: "primary", onClick: this.login }, "Login"),
	                  ' ',
	                  _(Button, { color: "secondary", onClick: function(){document.location='login.htm?.r='+Math.random();} },"Exit"
	                  )
	                )
	              );
    }
}

function buildParams2(params, map){
	var bp='';
	for(var key in params){
		var newKey = params[key];
		if(typeof newKey == 'function'){
			bp+='&'+key+'='+newKey(params);
		}else if(newKey.charAt(0)=='!')
			bp+='&'+key+'='+newKey.substring(1);
		else
			bp+='&'+key+'='+map[params[key]];
	}
	return bp;
}

function buildParams4transfer(params, map){
	var bp='';
	for(var key in params){
			bp+='&'+key+'='+map[key];
	}
	return bp;
}

/**
 * used to open dropDown 
 * make edit and delete from main and detail grid 
 * when the Grid is not in edit mode
 */
class XGridRowAction extends React.PureComponent {
	  constructor(props) {
		    super(props);
		    //state setter
			this.state={
				isOpen		:false,
				menuButtons :props.menuButtons,
				crudFlags	:props.crudFlags,
				rowData 	:props.rowData
			};
			//methods
			this.toggle= ()=>this.setState({isOpen:!this.state.isOpen});
	  }

	  render()
	  {
		//state getter && constants
		const { isOpen, menuButtons, crudFlags, rowData } = this.state;
		const { edit,remove } = crudFlags;
		const defstyle = { marginRight: 5,marginLeft: -2, fontSize: 12, color: '#777'};

		return _(Dropdown, { isOpen, toggle: this.toggle }
			, _(DropdownToggle, { tag: 'i', className: "icon-options-vertical column-action"})
			, isOpen &&
			_(DropdownMenu, { className: this.state.isOpen ? 'show' : ''}
				, edit && _(DropdownItem, { key: '123', onClick:(event)=>{this.props.onEditClick({event,rowData})} }
					, _('i', { className: 'icon-pencil', style:{...defstyle} })
						,'Güncelle')
				, remove && _(DropdownItem, { key: '1223', onClick:(event)=>{this.props.onDeleteClick({event,rowData})} }
					, _('i', { className: 'icon-minus text-danger', style:{...defstyle} })
						, 'Sil')
				,menuButtons && menuButtons.map(
					({ text, handler, cls })=>
					{
					return _(DropdownItem, { key:text , onClick:handler.bind(this.state) }
								, _('i', { className:cls, style:{...defstyle} })
								, text)
					}
				)
			)
		)
	}
}
/**
 * @deprecated
 */
class XGridAction extends React.PureComponent {
	  constructor(props) {
		    super(props);
		    this.toggle= this.toggle.bind(this);
		    this.state={isOpen:false};
	  }
	  toggle(){
		  this.setState({isOpen:!this.state.isOpen});
	  }
	  
	  render(){
		return _(Dropdown,{isOpen:this.state.isOpen, toggle: this.toggle}
//				,_('i',{className:'icon-options-vertical column-action', onClick:qqq.toggleGridAction})
				,_(DropdownToggle, {tag:"div", className: "timeline-badge hover-shake "+this.props.color, onClick:function(){alert('hehey')}}, _("i", { className: "icon-grid", style:{fontSize:17} }))
				//{tag:'i',className: "icon-grid", color:this.props.color||'danger'}
				,this.state.isOpen && _(DropdownMenu,{className: this.state.isOpen ? 'show' : ''} 
//				,_('div',{style:{padding: "7px 13px",background: "gray",  color: "darkorange", fontWeight: "500", fontSize:" 16px"}},'İşlemler')
				,_(DropdownItem,{ur:'123',onClick:false},_('i',{className:'icon-plus',style:{marginRight:5, marginLeft:-2, fontSize:12,color:'#777'}}),'Yeni Kayıt')
				,_('hr')
				,_(DropdownItem,{ur:'1223',onClick:false},_('i',{className:'icon-equalizer',style:{marginRight:5, marginLeft:-2, fontSize:12,color:'#777'}}),'Raporlar/BI')		    					
//				,_(DropdownItem,{ur:'1223',onClick:false},_('i',{className:'icon-drop',style:{marginRight:5, marginLeft:-2, fontSize:12,color:'#777'}}),'Diğer İşlemler')		    					
				) 
			)
	  }
}

class XGrid extends React.PureComponent {
	constructor(props) {
		super(props);
			var columns=[], tableColumnExtensions=[];
			var onEditClick = ({event,rowData})=>{
				var props=this.props;	
				var pkz = buildParams2(props.pk,rowData);
				var url = 'showForm?a=1&_fid='+props.crudFormId+pkz;
				if(props._postUpdate){ 
					var url=this.props._postUpdate(rowData, url, props); 
					if(!url)return;
				}
				var modal=event.ctrlKey && !!event.ctrlKey;
				props.openTab(
					'1-'+pkz,url+(modal?'&_modal=1':'')
					,{ds:{reload:function(){alert('geldim')}}}
					,{modal:modal}
				);
			};
			var onDeleteClick = ({event,rowData}) => {
				var props=this.props;	
				var pkz = buildParams2(props.pk,rowData);
				var url = 'ajaxPostForm?a=3&_fid='+props.crudFormId+pkz;
				yesNoDialog({ text:"Are you Sure!", callback:(success)=>{
					if(success){
						iwb.request({ url, successCallback:()=>this.loadData(true)});
			}
				}})
			}
			const canIOpenActions = (props.crudFlags && (props.crudFlags.edit || props.crudFlags.remove)) || props.menuButtons;
			if(canIOpenActions){
				columns.push({name:'_qw_',title:'.',getCellValue:function(rowData){
					return _(XGridRowAction,{ 
						...{ rowData }, 
						...{ menuButtons : props.menuButtons },
						...{ crudFlags: props.crudFlags },
						...{ onEditClick, onDeleteClick}
					});
				}});
				tableColumnExtensions.push({columnName:'_qw_',width:50, align:'right',sortingEnabled:false});
			}
			var c=props.columns;
			for(var qi=0;qi<c.length;qi++){
				var v={name:c[qi].name, title:c[qi].title};
				switch(c[qi].name){
				case	'pkpkpk_faf':v.title=_('i',{className:'icon-paper-clip'});break;
				case	'pkpkpk_ms':v.title=_('i',{className:'icon-envelope'});break; 
				case	'pkpkpk_cf':v.title=_('i',{className:'icon-bubble'});break;
				case	'pkpkpk_apf':v.title=_('i',{className:'icon-picture'});break;
				case	'pkpkpk_vcsf':v.title=_('i',{className:'icon-social-github'});break;
				}
				if(c[qi].formatter)v.getCellValue=c[qi].formatter;
				columns.push(v);
				tableColumnExtensions.push({columnName:c[qi].name, align:c[qi].align||'left', width:1*c[qi].width,sortingEnabled:!!c[qi].sort});
			}
			this.state = {
			columns: columns, columnOrder:columns.map(function(a){return a.name}), 
			tableColumnExtensions: tableColumnExtensions, columnWidths: tableColumnExtensions.map(function(a){return {columnName:a.columnName, width:a.width}}), 
			rows: props.rows || [],
			sorting: [],
			totalCount: 0,
			pageSize: props.pageSize || iwb.detailPageSize,
			pageSizes: props.pageSize>1 ? [parseInt(props.pageSize/2), props.pageSize, 3*props.pageSize]:[5,10,25,100],
			currentPage: 0,
			loading: false,
			gridActionOpen: false
			};
		//methods
		this.onSortingChange 	 = sorting		 => this.setState({loading: true, sorting});
		this.onCurrentPageChange = currentPage   => this.setState({loading: true, currentPage});
		this.onColumnWidthsChange= columnWidths	 => this.setState({columnWidths});
		this.onColumnOrderChange = columnOrder	 => this.setState({columnOrder}); 
		this.onPageSizeChange    = pageSize		 =>{
			var totalPages = Math.ceil(this.state.totalCount / pageSize);
			var currentPage = Math.min(this.state.currentPage, totalPages - 1);
			this.setState({ loading: true, pageSize, currentPage });
		}
		this.TableRow = tableRowData =>{
			var props=this.props;
			return _(_dxgrb.Table.Row, props.openTab && props.crudFlags && props.crudFlags.edit && props.pk && props.crudFormId ? 
				{...tableRowData, ...{ onDoubleClick:(event)=>{ onEditClick({event,rowData:tableRowData.row})}, style:{cursor:'pointer'} }}
				: tableRowData);
		}
	/**
	 * prerpares url with query
	 */
		this.queryString 		= ()			=>{
		const { sorting, pageSize, currentPage } = this.state;
		let queryString = this.props._url+'&limit='+pageSize+'&start='+(pageSize * currentPage);
		const columnSorting = sorting[0];
		if (columnSorting) {
			const sortingDirectionString = columnSorting.direction === 'desc' ? ' desc' : '';
			queryString += '&sort='+columnSorting.columnName+sortingDirectionString;
		}
		return queryString;
	}
	/**
	 * used to make request and fill the grid
	 * @param {boolean} force 
	 */
		this.loadData 			= force 		=>{
		if(this.props.rows)return;
		const queryString = this.queryString();
		if (!force && queryString === this.lastQuery) { return; }
		this.setState({loading: true});
			iwb.request({
				url:queryString, self:this,
				params:this.props.searchForm && iwb.getFormValues(document.getElementById(this.props.searchForm.id)), 
				successCallback: (result, cfg)=>{
			cfg.self.setState({
				rows: result.data,
				totalCount: result.total_count,
				loading: false,
			});
			},errorCallback:(error,cfg)=>{
			cfg.self.setState({
				rows: [],
				totalCount: 0,
				loading: false,
				});
			}});
		this.lastQuery = queryString;
	}
	/**
	 * will open new page with
	 * @param {event} event 
	 * @param {state/props} grid 
	 * @param {Array} row 
	 */
		this.onOnNewRecord 		=(event,grid, row) =>{
		if(!grid)grid=this.props;
		if(grid.crudFlags && grid.crudFlags.insert && this.props.openTab){
			var url = 'showForm?a=2&_fid='+grid.crudFormId;
			if(grid._postInsert){
				url=grid._postInsert(row||{}, url, grid);
				if(!url)return;
			}
			var modal=!!event.ctrlKey;
			this.props.openTab('2-'+grid.gridId,url+(modal?'&_modal=1':''),{},{modal:modal})
		}
	}
	}
	componentDidMount() { if(!this.dontRefresh)this.loadData(); this.dontRefresh=false; }
	componentDidUpdate() { this.loadData(); this.dontRefresh=false; }
	componentWillUnmount(){ iwb.grids[this.props.id]=Object.assign({},this.state); }
	render() {
		//state
		const {rows, columns, tableColumnExtensions, sorting,
			pageSize, pageSizes, currentPage, totalCount, loading, columnWidths, columnOrder
		} = this.state;
		//props
		const {
			_disableIntegratedGrouping,
			_disableIntegratedSorting,
			_disableSearchPanel,
			_importClicked,
			multiselect,
			showDetail,
			crudFlags,
			pageSize,
			keyField
		} = this.props;

		if(!rows || !rows.length) return null;
		return _(_dxgrb.Grid, {
				rows,
				columns, 
				getRowId : row => row[keyField]
			},
			!!_disableIntegratedSorting && _(_dxrg.SortingState, !pageSize ? null : {
					sorting,
					onSortingChange: this.onSortingChange,
					columnExtensions:tableColumnExtensions
				}),
			multiselect && _(_dxrg.SelectionState,null),
			!pageSize ? _(_dxrg.SearchState, null) : null,
			!pageSize ?  _(_dxrg.RowDetailState,null) : null,
			!pageSize 	&& rows.length>1  ? _(_dxrg.IntegratedFiltering, null) 		: null,
			!pageSize 	&& rows.length>1  ? _(_dxrg.GroupingState,null) 			: null,		  
			!pageSize 	&& rows.length>1  ? _(_dxrg.IntegratedGrouping ,null) 		: null,
			!pageSize 	&& rows.length>1  ? _(_dxrg.IntegratedSorting,null) 		: null,
			showDetail 	&&  _(_dxrg.RowDetailState,null),
			rows.length>iwb.detailPageSize || pageSize>1 ? _(_dxrg.PagingState, pageSize>1 ? {
				currentPage,
				pageSize,
				onCurrentPageChange	: this.onCurrentPageChange,
				onPageSizeChange	: this.onPageSizeChange}:{}) : null,
			pageSize>1 && rows.length>1  ? _(_dxrg.CustomPaging, {totalCount}) : null,
			
			multiselect && _(_dxrg.IntegratedSelection,null),
			_(_dxgrb.DragDropProvider,null),
			_(_dxgrb.Table, {
				columnExtensions: tableColumnExtensions,
				rowComponent:this.TableRow
			}),
			
			multiselect && _(_dxgrb.TableSelection,{showSelectAll:!0}),
			
			_(_dxgrb.TableColumnReordering, {
				order:columnOrder,
				onOrderChange:this.onColumnOrderChange
			}),
			_(_dxgrb.TableColumnResizing, {
				columnWidths, 
				onColumnWidthsChange:this.onColumnWidthsChange
			}),		  
			_(_dxgrb.TableHeaderRow, { showSortingControls: true }),
			showDetail?  _(_dxgrb.TableRowDetail, {contentComponent:showDetail}):null,
			rows.length>iwb.detailPageSize || pageSize>1 ?  _(_dxgrb.PagingPanel, {pageSizes: pageSizes || iwb.detailPageSize}) : null,
			!pageSize && rows.length>1 && _(_dxgrb.TableGroupRow,null),
			!pageSize && rows.length>1 && _(_dxgrb.Toolbar,null),
			!pageSize && rows.length>1 && _(_dxgrb.SearchPanel, {
				messages:{searchPlaceholder:'Hızlı Arama...'},
				changeSearchValue: ax=>{
					if(iwb.debug)console.log('onValueChange',ax);
				}
			}),//TODO
			!pageSize && rows.length>1 && _(_dxgrb.GroupingPanel,{showSortingControls:true})
//		   	,loading && iwb.loading()
		);
	}
}

const commandComponentProps = {
	add: {
		icon: 'plus',
		hint: 'Create new row',
	},
	edit: {
		icon: 'pencil',
		hint: 'Edit row',
		color: 'text-warning',
	},
	delete: {
		icon: 'trash',
		hint: 'Delete row',
		color: 'text-danger',
	},
	cancel: {
		icon: 'x',
		hint: 'Cancel changes',
		color: 'text-danger',
	},
	import: {
		icon: 'target',
		hint: 'Import'
	}
};
const CommandButton = ({onExecute, icon, text, hint, color, row}) =>{
	let button =_("button",{className: "btn btn-link",style: { padding: "11px" },
	    onClick: e => {
	      onExecute();
	      e.stopPropagation();
	    },
	    title: hint
	  },
	  _("span",{ className: color || 'undefined' },
	    icon ? _("i", { className: 'oi oi-'+icon, style: { marginRight: text ? 5 : 0 } }) : null,
	    text
	  )
	);
	return button;
}
const Command = ({ id, onExecute}) => {
	var c = commandComponentProps[id];
	return c ? _(CommandButton,Object.assign({},c,{onExecute:onExecute})):null;
}

iwb.prepareParams4grid=function(grid, prefix, values){//sadece master-insert durumunda cagir. farki _postMap ve hic bir zaman _insertedItems,_deletedItems dikkate almamasi
  	var dirtyCount=0;
  	var params={};
	var items = values.deleted;
	var pk = grid._pk || grid.pk;
  	if(items)for(var bjk=0;bjk<items.length;bjk++){ //deleted
  		dirtyCount++;
  		for(var key in pk){
  			var val=pk[key];
			if(typeof val == 'function'){
				params[key+prefix+"."+dirtyCount] = val(items[bjk]);
			} else {
				params[key+prefix+"."+dirtyCount]=(val.charAt(0)=='!') ? val.substring(1):items[bjk][val];
			}
  		}
  		params["a"+prefix+"."+dirtyCount]=3;
  	}
	items = values.changed;
	if(items)for(var bjk=0;bjk<items.length;bjk++){ // edited
		dirtyCount++;
		params["a"+prefix+"."+dirtyCount]=1;
		var changes=items[bjk]._new;
		for(var key in changes)params[key+prefix+"."+dirtyCount]=changes[key];
		if(grid._postMap)for(var key in grid._postMap){
			var val = grid._postMap[key];
			if(typeof val == 'function'){ 
				params[key+prefix+"."+dirtyCount] = val(changes);
			} else {
				params[key+prefix+"."+dirtyCount]=(val.charAt(0)=='!') ? val.substring(1):changes[val];
			}
		}
		
		for(var key in pk){
			var val=pk[key];
			if(typeof val == 'function'){
				params[key+prefix+"."+dirtyCount] = val(items[bjk]);
			} else {
				params[key+prefix+"."+dirtyCount]=(val.charAt(0)=='!') ? val.substring(1):items[bjk][val];
			}
			
		}
	}
	items = values.inserted;
	if(items)for(var bjk=0;bjk<items.length;bjk++){ // inserted
		dirtyCount++;
		params["a"+prefix+"."+dirtyCount]=2;
		var changes=items[bjk]._new;
		for(var key in changes)params[key+prefix+"."+dirtyCount]=changes[key];

		if(grid._postMap)for(var key in grid._postMap){
			var val = grid._postMap[key];
			if(typeof val == 'function'){
				params[key+prefix+"."+dirtyCount] = val(changes);
			} else {
				params[key+prefix+"."+dirtyCount]=(val.charAt(0)=='!') ? val.substring(1):changes[val];
			}
		}
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
/**
 * Class for making GRIDROW Edit + Multiselect
 */
class SelectableStubCell extends React.PureComponent {
	  render() {
	    return _(Plugin,null,
	      _(Template, { 
			  name: 'tableCell', predicate: ({ tableRow, tableColumn}) => {
        	  if (tableRow.key !== 'heading' && tableColumn.key === 'select' && tableRow.type === 'edit') {
              return true;
            }}},
            params => _(TemplateConnector,null,
                  ({ selection }, { toggleSelection }) => {
                    return _(_dxgrb.TableSelection.Cell, {
                      row: params.tableRow.row,
                      selected: selection.indexOf(params.tableRow.rowId) !== -1,
                      onToggle: () => toggleSelection({ rowIds: [params.tableRow.rowId] })
                    });
                  })
          ));
	    }
}
/**
 * used for sf grid in popup Modal
 */
class XEditGridSF extends React.PureComponent {
	  constructor(props) {
	    if(iwb.debug)console.log('XEditGridSF.constructor', props);
	    super(props);
	    var oldGridState = iwb.grids[props.id];
	    if(iwb.debug)console.log('oldGridState', oldGridState);
	    if(oldGridState){
	    	this.dontRefresh = !0;
	    	this.state = oldGridState;
		    var c=props.columns;
		    this.editors={};
		    for(var qi=0;qi<c.length;qi++){
		    	var editor=c[qi].editor||false;
		    	if(editor){
		    		this.editors[c[qi].name]=editor;
		    	}
		    }
	    } else {
		    var columns=[], tableColumnExtensions=[];
		    var c=props.columns;
		    this.editors={};
		    for(var qi=0;qi<c.length;qi++){
		    	switch(c[qi].name){
		    	case	'pkpkpk_faf':case	'pkpkpk_ms':case	'pkpkpk_cf':case	'pkpkpk_apf':case	'pkpkpk_vcsf':break;
		    	default:
			    	var v={name:c[qi].name, title:c[qi].title};
			    	if(c[qi].formatter)v.getCellValue=c[qi].formatter;
			    	columns.push(v);
			    	var editor=c[qi].editor||false;
			    	if(editor){
			    		editor.autoComplete='off';
			    		if(!editor.style)editor.style={};
			    		editor.style.width='100%';
			    		switch(1*editor._control){
			    		case	6:case	8:	case	58:
			    		case	7:case	15:case	59:
			    		case	9:	case	10: //combos
			    			break;
			    		default:
			    			editor.style.textAlign=c[qi].align||'left';
			    		}
			    		this.editors[c[qi].name]=editor;
			    	}
			    	tableColumnExtensions.push({columnName:c[qi].name, editingEnabled:!!editor, align:c[qi].align||'left', width:1*c[qi].width,sortingEnabled:!!c[qi].sort});
		    	}
		    }
		    this.state = {
		      viewMode:!props.editable && (props.viewMode||true), 
		      columns: columns, columnOrder:columns.map(function(a){return a.name}), 
		      tableColumnExtensions: tableColumnExtensions, columnWidths: tableColumnExtensions.map(function(a){return {columnName:a.columnName, width:a.width}}), 
		      rows: [], sorting: [],
		      totalCount: 0,
		      pageSize: props.pageSize || iwb.detailPageSize,
		      pageSizes: props.pageSize>1 ? [parseInt(props.pageSize/2), props.pageSize, 3*props.pageSize]:[5,10,25,100],
		      currentPage: 0,
		      loading: false,deletingRows: [],addedRows: [],editingRowIds: [],rowChanges: {}, deletedRows:[]
		      ,pkInsert:0
		      ,selection:[]
		    };
	    }
//		this._pk4insert = 0; //state te olmasi lazim: TODO
	    this.onSortingChange 		= sorting 		=> this.setState({ sorting });
	    this.onCurrentPageChange 	= currentPage 	=> this.setState({ currentPage });
	    this.onColumnWidthsChange 	= columnWidths 	=> this.setState({columnWidths});
	    this.onColumnOrderChange 	= columnOrder 	=> this.setState({columnOrder}); 
		this.changeEditingRowIds 	= editingRowIds => this.setState({ editingRowIds });
		this.changeSelection 		= selection 	=> this.setState({ selection });
		this.changeRowChanges 		= rowChanges 	=> this.setState({ rowChanges });
		this.onPageSizeChange 		= (pageSize) 	=> {
			var totalPages 			= Math.ceil(this.state.totalCount / pageSize);
			var currentPage 		= Math.min(this.state.currentPage, totalPages - 1);
			this.setState({ pageSize,currentPage});
		};

		

	    this.getValues = () => {
	    	  let {rows,addedRows,deletedRows, editingRowIds, selection} = this.state;
	    	  rows = rows.slice();
	    	  
	    	  selection.forEach((rowId) => {
	    		  if(rowId>0){
			          const index = rows.findIndex(row => row[this.props.keyField] === rowId);
			          if (index > -1){
			        	  addedRows.push(Object.assign({},rows[index]));
			          }
	    		  }
		        });
	    	  
	    	  var searchFormData = this.props.searchForm && iwb.getFormValues(document.getElementById('s-'+this.props.id));
			  return {searchFormData, inserted:addedRows, deleted:deletedRows, _state: this.state};
		}
	   
	    this.commitChanges = ({ added, changed, deleted }) => {
	        let { rows, deletedRows } = this.state;
	        if(deleted && deleted.length && confirm('Are you sure?')){
	        	rows = rows.slice();
		        deleted.forEach((rowId) => {
		          const index = rows.findIndex(row => row[this.props.keyField] === rowId);
		          if (index > -1) {
		        	  if(rowId>0){
		        		  deletedRows.push(Object.assign({},rows[index]));
		        	  }
		              rows.splice(index, 1);
		          }
		        });
		        this.setState({ rows, deletingRows: [], deletedRows });
	        }
	    };
	    this.changeAddedRows = addedRows => {
	    	var newRecord = Object.assign({}, this.props.newRecord||{});
	    	var pk=this.state.pk4insert;
	    	--pk;
	    	newRecord[this.props.keyField]=pk;
	    	this.setState({pk4insert:pk,
	    		addedRows: addedRows.map(row => (Object.keys(row).length ? row : newRecord)),
	        });
	    }
	    this.deleteRows = () =>{
	        const rows = this.state.rows.slice();
	        this.state.deletingRows.forEach((rowId) => {
	          const index = rows.findIndex(row => row.id === rowId);
	          if (index > -1) {
	            rows.splice(index, 1);
	          }
	        });
	        this.setState({ rows, deletingRows: [] });
		};
	    
	    if(props.parentCt && props.parentCt.egrids)props.parentCt.egrids[props.gridId]=this;
	    if(this.props.searchForm){//hidden:!!this.props.grid.globalSearch
	    	this.searchForm = _(Nav, {style:{}},_('div',{className:'hr-text'},_('h6',null,'Arama Kriterleri'))
		    	,_('div',{style:{zoom:'.9'}},_(this.props.searchForm,{parentCt:this}),_('div',{className:'form-group',style:{paddingTop:10}},_(Button, {color: "danger", style:{width:'100%', borderRadius:2},onClick:() => {this.loadData(!0);} },"ARA")))
	    	);
		}
		this.queryString = ()=>{
			const { sorting, pageSize, currentPage } = this.state;
			let queryString = this.props._url+'&limit='+pageSize+'&start='+(pageSize * currentPage);
			const columnSorting = sorting[0];
			if (columnSorting) {
				const sortingDirectionString = columnSorting.direction === 'desc' ? ' desc' : '';
				queryString += '&sort='+columnSorting.columnName+sortingDirectionString;
			}
			return queryString;
		}
		this.loadData = force =>{
			const queryString = this.props._url;
			const t_props=this.props;
			this.setState({loading: true});
			iwb.request({url:queryString, self:this, params:this.props.searchForm && iwb.getFormValues(document.getElementById('s-'+this.props.id)), successCallback:function(result, cfg){
				var state={
					rows: result.data,
					totalCount: result.total_count,
					loading: false,
					};
				if(true || t_props.multiselect){
					state.editingRowIds=state.rows.map((row) => row[t_props.keyField])
				}
				cfg.self.setState(state);
			},errorCallback:function(error,cfg){
				cfg.self.setState({
					rows: [],
					totalCount: 0,
					loading: false,
					});
				}});
			this.lastQuery = queryString;
	    }
		this.EditCell = (xprops)=>{
			var editor = this.editors[xprops.column.name];
			if(!editor)return _(_dxgrb.TableEditRow.Cell, xprops);
			
			editor = Object.assign({},editor);
			if(!xprops.row._new)xprops.row._new={};//Object.assign({},xprops.row);
			if(!xprops.row._new.hasOwnProperty(xprops.column.name))xprops.row._new[xprops.column.name]=xprops.row[xprops.column.name];
			delete editor.defaultValue;
			switch(1*editor._control){
			case	3:case	4://number
				editor.value=xprops.value || 0;//xprops.row._new[xprops.column.name];
				editor.onValueChange=function(o){
				xprops.row._new[xprops.column.name] = o.value;
				xprops.onValueChange(o.value);
				};
				break;
			case	6:case	8:	case	58:
			case	7:case	15:case	59:
			case	9:	case	10: //combos
				editor.value=xprops.row._new[xprops.column.name] || 0 || ''; //TODO. ilk edit ettigini aliyor
				editor.onChange=function(o){
					xprops.row._new[xprops.column.name] = o.id;
					xprops.onValueChange(o.id);
				};
				break;
			default:
				editor.value=xprops.value||'';//xprops.row._new[xprops.column.name];
				editor.onChange=function(o){
					xprops.row._new[xprops.column.name] = o.target.value;
					xprops.onValueChange(o.target.value);
				};
				break;
			}
			var cmp=Input;
			if(editor.$){cmp = editor.$; delete editor.$;}
			return _('td',{style:{verticalAlign: 'middle',padding: 1}}, _(cmp,editor))
		}
	  }
	  componentDidMount() { if(!this.dontRefresh)this.loadData();}
	  componentDidUpdate() {if(this.props.editable && this.props.viewMode!=this.state.viewMode){ this.setState({viewMode:this.props.viewMode}); }}
	  componentWillUnmount(){ iwb.grids[this.props.id]=Object.assign({},this.state); }
	  render() {
		if(iwb.debug)console.log('XEditGrid:render')
		const {
			viewMode, rows, columns, tableColumnExtensions, sorting,pageSize,
			pageSizes, currentPage, totalCount, loading, columnWidths, columnOrder,
			editingRowIds, rowChanges, addedRows, selection
		    } = this.state;
		//props
		const {
			_disableIntegratedGrouping,
			_disableIntegratedSorting,
			_disableSearchPanel,
			_importClicked,
			multiselect,
			crudFlags,
			keyField,
			selectRow
		} = this.props;
		// method
		const {
			onCurrentPageChange,
			onPageSizeChange,
			onColumnWidthsChange
		} = this;
	    		
		var g = _(_dxgrb.Grid, { 
				rows, columns,
				getRowId : row => row[keyField] 
			},
			!_disableIntegratedSorting ? _(_dxrg.SortingState, null):null,
			
			multiselect && _(_dxrg.SelectionState,{
				selection, 
				onSelectionChange:this.changeSelection
				}),
	    		_(_dxrg.SearchState, null),
			!_disableSearchPanel		 	?_(_dxrg.IntegratedFiltering, null)	: null, //was used for panel search(@dependency)
			!_disableIntegratedGrouping  	? _(_dxrg.GroupingState, null)	    : null,		  
			!_disableIntegratedGrouping  	? _(_dxrg.IntegratedGrouping, null)	: null,
			!_disableIntegratedSorting   	? _(_dxrg.IntegratedSorting, null)	: null,
				
			rows.length>iwb.detailPageSize ?  _(_dxrg.PagingState, 
				pageSize>1 ? {
				pageSize,				
				currentPage,
				onCurrentPageChange,
				onPageSizeChange
			}:{}) : null,
			
			multiselect && _(_dxrg.IntegratedSelection ,null),
				!viewMode && _(_dxrg.EditingState,{
					addedRows,
					rowChanges,
					editingRowIds,
					onEditingRowIdsChange		: this.changeEditingRowIds,
					onRowChangesChange			: this.changeRowChanges,
					columnExtensions			: tableColumnExtensions,
					onAddedRowsChange			: this.changeAddedRows,
					onCommitChanges				: this.commitChanges
			    		}),
		    			
				_(_dxgrb.DragDropProvider,null),
			_(_dxgrb.Table, {
				columnExtensions				: tableColumnExtensions
				}),
			multiselect && _(_dxgrb.TableSelection,{
				showSelectAll:true
				}),
			_(_dxgrb.TableColumnReordering, {
				order:columnOrder,
				onOrderChange					:this.onColumnOrderChange
			}),
			_(_dxgrb.TableColumnResizing, {
				columnWidths,
				onColumnWidthsChange
			}),		  
			_(_dxgrb.TableHeaderRow,  { 
				showSortingControls				: !0 
				}),
			selectRow.mode === 'checkbox' && _(SelectableStubCell,null), //select box
				
			!viewMode && _(_dxgrb.TableEditRow, {
				cellComponent					: this.EditCell
			}),
			
			!multiselect && !viewMode && _(_dxgrb.TableEditColumn, { 
				showAddCommand					: crudFlags && crudFlags.insert, 
				showEditCommand					: crudFlags && crudFlags.edit,
				showDeleteCommand				: crudFlags && crudFlags.remove,
				commandComponent				: Command
			}),

			rows.length>iwb.detailPageSize 		? _(_dxgrb.PagingPanel, {pageSizes: pageSizes || iwb.detailPageSize}) 		: null,
			!_disableIntegratedGrouping 		? _(_dxgrb.TableGroupRow,null)										   		: null,
			!_disableIntegratedGrouping || !_disableIntegratedSorting || !_disableSearchPanel ? _(_dxgrb.Toolbar,null)		: null,			
			!_disableSearchPanel 				? _(_dxgrb.SearchPanel, {messages:{searchPlaceholder:'Hızlı Arama...'}}) 	: null,
			!_disableIntegratedGrouping 		? _(_dxgrb.GroupingPanel,{showSortingControls:true})						: null,
			);
		    
		    var footer = _(ModalFooter, null,
		    		_(Button, { className:'btn-form',color: 'teal', onClick: ()=>{ this.commitChanges(this.state); if(this.props.callback(this.getValues())===true) iwb.closeModal()} }, "KAYDET")
		    		,' '
		    		,_(Button, { className:'btn-form',color: "light", style:{border: ".5px solid #e6e6e6"}, onClick:iwb.closeModal}, "VAZGEÇ")
            );
		    
		return !this.searchForm ? g:_('div',{
			className:'tab-grid mb-4'
		},[
			_('nav',{
				id:'sf-'+this.props.id,
				key: 'sf-'+this.props.id
				}, this.searchForm)
			,_('main',{
				className: "inbox",
				key:'inbox'
				}, g, footer)
		    ])
		  }
}
/**
 * used to extend template of the grid!
 * @param { name, children, predicate, position }
 * @example
 * overloading template example located in XEditGrid render
 */	
const extendGrid = ({ name, children, predicate, position }) => {
	return _(Plugin, null, _(Template, {
			name,
			predicate:rest=>predicate(rest)
		},
		params => {
			return _(React.Fragment, null,
				position === 'before' && children,
				position && _(TemplatePlaceholder, null),
				position === 'after' && children,
				position !== true && children
			);
		}
	));
};
/**
 * used for making popup dialog
 * @param {text,callback} obj
 * @example 
 * yesNoDialog({ text:"Are you Sure!", callback:(success)=>{ logic here }});
 */	
yesNoDialog = ({text,callback}) => {
	iwb.showModal({
		title	:text,
		footer	:_(ModalFooter, null,
			_(Button, { 
				className:'btn-form',
				color: 'teal',
				onClick: ()=>{callback(true); iwb.closeModal()}
				},"TAMAM"),
			' ',
			_(Button, {
				className:'btn-form',
				color: "light",
				style:{border: ".5px solid #e6e6e6"},
				onClick: ()=>{callback(false);  iwb.closeModal()}
			}, "VAZGEÇ")
		),
		color	: 'danger', 
		size	: 'sm', 
		body	: text	
	});
		}
/**
 * component for edit Detail Grid
 */
class XEditGrid extends React.PureComponent {
	  constructor(props) {
	    if(iwb.debug)console.log('XEditGrid.constructor', props);
	    super(props);
		// state
	    var oldGridState = iwb.grids[props.id];
	    if(iwb.debug)console.log('oldGridState', oldGridState);
	    if(oldGridState){
	    	this.dontRefresh = !0;
	    	this.state = oldGridState;
		    var c=props.columns;
		    this.editors={};
		    for(var qi=0;qi<c.length;qi++){
		    	var editor=c[qi].editor||false;
		    	if(editor){
		    		this.editors[c[qi].name]=editor;
		    	}
		    }
	    } else {
		    var columns=[], tableColumnExtensions=[];
		    var c=props.columns;
		    this.editors={};
		    for(var qi=0;qi<c.length;qi++){
		    	switch(c[qi].name){
		    	case	'pkpkpk_faf':case	'pkpkpk_ms':case	'pkpkpk_cf':case	'pkpkpk_apf':case	'pkpkpk_vcsf':break;
		    	default:
			    	var v={name:c[qi].name, title:c[qi].title};
			    	if(c[qi].formatter)v.getCellValue=c[qi].formatter;
			    	columns.push(v);
			    	var editor=c[qi].editor||false;
			    	if(editor){
			    		editor.autoComplete='off';
			    		if(!editor.style)editor.style={};
			    		editor.style.width='100%';
			    		editor.style.position = 'inherit';
			    		switch(1*editor._control){
			    		case	6:case	8:	case	58:
			    		case	7:case	15:case	59:
			    		case	9:	case	10: //combos
			    			break;
			    		default:
			    			editor.style.textAlign=c[qi].align||'left';
			    		}
			    		this.editors[c[qi].name]=editor;
			    	}
			    	tableColumnExtensions.push({columnName:c[qi].name, editingEnabled:!!editor, align:c[qi].align||'left', width:1*c[qi].width,sortingEnabled:!!c[qi].sort});
		    	}
		    }
		    this.state = {
		      viewMode:!props.editable && (props.viewMode||true), 
				columns: columns,
				columnOrder:columns.map(function(a){return a.name}), 
				tableColumnExtensions: tableColumnExtensions,
				columnWidths: tableColumnExtensions.map(function(a){return {columnName:a.columnName, width:a.width}}), 
				rows: [], 
				sorting: [],
		      totalCount: 0,
		      pageSize: props.pageSize || iwb.detailPageSize,
		      pageSizes: props.pageSize>1 ? [parseInt(props.pageSize/2), props.pageSize, 3*props.pageSize]:[5,10,25,100],
		      currentPage: 0,
				loading: false,deletingRows: [],addedRows: [],editingRowIds: [],rowChanges: {}, deletedRows:[],
				pkInsert:0
		    };
	    }
		//methods
	    this.onSortingChange		= sorting 		=> this.setState({ sorting });
	    this.onCurrentPageChange 	= currentPage 	=> this.setState({ currentPage });
	    this.onColumnWidthsChange 	= columnWidths 	=> this.setState({columnWidths});
	    this.onColumnOrderChange 	= columnOrder 	=> this.setState({columnOrder}); 
		this.changeEditingRowIds 	= editingRowIds => this.setState({ editingRowIds });
		this.changeRowChanges 		= rowChanges 	=> this.setState({ rowChanges });
		this.onPageSizeChange		= (pageSize) 	=> {
			var totalPages = Math.ceil(this.state.totalCount / pageSize);
			var currentPage = Math.min(this.state.currentPage, totalPages - 1);
			this.setState({pageSize, currentPage});
		}
	    this.getValues 				= () 			=> {
			let {rows,addedRows,deletedRows, editingRowIds} = this.state;
			rows = rows.slice();
			var changedRows = [];
			editingRowIds.forEach((rowId) => {
				if(rowId>0){
					const index = rows.findIndex(row => row[this.props.keyField] === rowId);
					if (index > -1)changedRows.push(Object.assign({},rows[index]));
				}
			});
			return {inserted:addedRows, deleted:deletedRows, changed: changedRows};
		}
	    this.commitChanges 			= ({ deleted }) => {
	        let { rows, deletedRows } = this.state;
	        if(deleted && deleted.length){
				yesNoDialog({
					text:"Are you Sure!",
					callback:(success)=>{
						if(success){
	        	rows = rows.slice();
		        deleted.forEach((rowId) => {
		          const index = rows.findIndex(row => row[this.props.keyField] === rowId);
		          if (index > -1) {
		        	  if(rowId>0){
		        		  deletedRows.push(Object.assign({},rows[index]));
		        	  }
		              rows.splice(index, 1);
		          }
		        });
		        this.setState({ rows, deletingRows: [], deletedRows });
	        }
	    }
				})
	        }
	    }
	    this.changeAddedRows 		= addedRows 	=> {
	    	var newRecord = Object.assign({}, this.props.newRecord||{});
	    	var pk=this.state.pk4insert;
	    	--pk;
	    	newRecord[this.props.keyField]=pk;
	    	this.setState({
				pk4insert:pk,
	    		addedRows: addedRows.map(row => (Object.keys(row).length ? row : newRecord)),
	        });
	    }
	    this.deleteRows 			= () 			=> {
	        const rows = this.state.rows.slice();
	        this.state.deletingRows.forEach((rowId) => {
	          const index = rows.findIndex(row => row.id === rowId);
	          if (index > -1) {	rows.splice(index, 1); }
	        });
	        this.setState({ rows, deletingRows: [] });
	    }
		/**
		 * bind with parent Element
		 */
		if(props.parentCt && props.parentCt.egrids)props.parentCt.egrids[props.gridId]=this;
		/**
		 * used to collect data from form and make url with rearch params
		 */
		this.queryString = ()=>{
			const { sorting, pageSize, currentPage } = this.state;
			let queryString = this.props._url+'&limit='+pageSize+'&start='+(pageSize * currentPage);
			const columnSorting = sorting[0];
			if (columnSorting) {
			  const sortingDirectionString = columnSorting.direction === 'desc' ? ' desc' : '';
			  queryString += '&sort='+columnSorting.columnName+sortingDirectionString;
			}
			return queryString;
		}
		/**
		 * used to make data request to fill the frid with related data
		 * @param {boolean} force 
		 */
		this.loadData = (force) => {
			const queryString = this.props._url;
			const t_props=this.props;
			this.setState({loading: true});
			iwb.request({
				url:queryString,
				self:this,
				params:this.props.searchForm && iwb.getFormValues(document.getElementById('s-'+this.props.id)),
				successCallback:(result, cfg) => {
					var state = {
							rows: result.data,
							totalCount: result.total_count,
							loading: false,
						};
					if(t_props.multiselect){ state.editingRowIds=state.rows.map((row) => row[t_props.keyField]) }
			  		cfg.self.setState(state);
				},
				errorCallback:function(error,cfg){
					cfg.self.setState({
						rows: [],
						totalCount: 0,
						loading: false,
					});
			  	}});
			this.lastQuery = queryString;
	    }
	    /**
	     * used for import data from the popup 
	     */
	    this.BulkyImport = ({searchFormData, inserted, deleted, _state})=>{
	    	const {rows, addedRows} = this.state;
	    	let tempRow = [];
			let max;
	    	//find max tab_order from grid
			if((rows['0']&&rows['0'].tab_order) || (addedRows['0']&&addedRows['0'].tab_order)){
				 max =Math.max(...rows.map(d => +d.tab_order), ...addedRows.map(d => +d.tab_order))+10;
			}else{
				max =(rows.length+addedRows.length)*10;
			}
			if(max === '-Infinity' || +max === 0){ max = 10;}
	    	//xsample_id to sample_id converter could be written as helper function
	    	Object.keys(searchFormData).forEach(function(key,index) {
	    		if( key.charAt( 0 ) === 'x' ){
	    			searchFormData[key.slice( 1 )] = searchFormData[key];
	    			delete searchFormData[key];
	    			}
	    		});
	    	//merge new imported data
	    	inserted.forEach((data)=>{
	    		var merged = { ...searchFormData, ...data};
	    		merged = { ...merged, ...merged._new};
	    		merged.tab_order = max;
				merged.max = max;
	    		tempRow.push(merged);
	    		max+=10;
	    	});
	    	//Adds data to the grit from the popup
	    	this.setState((prev)=>{ {addedRows:[...prev.addedRows , ...tempRow]} });
		}
		/**
		 * to get all data from grid editing + noneEdited at current time 
		 */	
		this.getAllData = ()=>{ 
			let tempRowData = [];
			this.state.rows.forEach((data)=>{ tempRowData.push({ ...data, ...data._new});});
			return tempRowData;
		}
		/**
		 * used for Cell Editing
		 * @param {Object} xprops 
		 */
		this.EditCell = (xprops)=>{
			var editor = this.editors[xprops.column.name];
			if(this.props.isCellEditable && this.props.isCellEditable(xprops.row,xprops.column.name)===false)return _(_dxgrb.TableEditRow.Cell, {...xprops, ...{editingEnabled:false}});
			if(!editor)return _(_dxgrb.TableEditRow.Cell, xprops);
			editor = Object.assign({},editor);
			if(!xprops.row._new)xprops.row._new={};//Object.assign({},xprops.row);
			if(!xprops.row._new.hasOwnProperty(xprops.column.name))xprops.row._new[xprops.column.name]=xprops.row[xprops.column.name];
			delete editor.defaultValue;
			switch(1*editor._control){
			case	3:case	4://number
				editor.value=xprops.value;//xprops.row._new[xprops.column.name];
				editor.onValueChange=function(o){
				xprops.row._new[xprops.column.name] = o.value;
				xprops.onValueChange(o.value);
				};
				break;
			case	6:case	8:	case	58:
			case	7:case	15:case	59:
			case	9:	case	10: //combos
				editor.value=xprops.row._new[xprops.column.name]; //TODO. ilk edit ettigini aliyor
				editor.onChange=function(o){
					xprops.row._new[xprops.column.name] = o.id;
					xprops.onValueChange(o.id);
				};
				break;
			case 5:
				editor.checked=+xprops.row._new[xprops.column.name];
				editor.onChange=function(o){
					xprops.row._new[xprops.column.name] = o.target.checked;
					xprops.onValueChange(o.target.checked);
				};
				break;
			default:
				editor.value=xprops.value;//xprops.row._new[xprops.column.name];
				editor.onChange=function(o){
					xprops.row._new[xprops.column.name] = o.target.value;
					xprops.onValueChange(o.target.value);
				};
				break;
			}
			var cmp=Input;
			if(editor.$){cmp = editor.$; delete editor.$;}
			return _('td',{style:{verticalAlign: 'middle',padding: 1}}, _(cmp,editor))
		}
	}
	componentDidMount() 	{ if(!this.dontRefresh)this.loadData(); }
	componentDidUpdate() 	{if(this.props.editable && this.props.viewMode!=this.state.viewMode){ this.setState({viewMode:this.props.viewMode}); } }
	componentWillUnmount()	{ iwb.grids[this.props.id]=Object.assign({},this.state); }
	render() {
		//state:
		const {
			viewMode, rows, columns, tableColumnExtensions, sorting, pageSize,
			pageSizes, currentPage, totalCount, loading, columnWidths, columnOrder,
			editingRowIds, rowChanges, addedRows
		    } = this.state;
		//props
		const {
			_disableIntegratedGrouping,
			_disableIntegratedSorting,
			_disableSearchPanel,
			_importClicked,
			multiselect,
			crudFlags,
			keyField
		} = this.props;
		//methods:
		const {onColumnWidthsChange} = this;
		return _(_dxgrb.Grid,{
				rows,
				columns,
				getRowId : row => row[keyField]
			},
			!_disableIntegratedSorting   	? _(_dxrg.SortingState, null)		: null,
			multiselect && _(_dxrg.SelectionState,null),
	    		_(_dxrg.SearchState, null),
			!_disableSearchPanel		 	?_(_dxrg.IntegratedFiltering, null)	: null, //was used for panel search(@dependency)
			!_disableIntegratedGrouping  	? _(_dxrg.GroupingState, null)	    : null,		  
			!_disableIntegratedGrouping  	? _(_dxrg.IntegratedGrouping, null)	: null,
			!_disableIntegratedSorting   	? _(_dxrg.IntegratedSorting, null)	: null,
			rows.length>iwb.detailPageSize 	? _(_dxrg.PagingState, 
				pageSize>1 ? {
				pageSize,
				currentPage,
				onPageSizeChange: this.onPageSizeChange,
				onCurrentPageChange: this.onCurrentPageChange, 
			}:{}) : null,
			multiselect && _(_dxrg.IntegratedSelection ,null),
			!viewMode && _(_dxrg.EditingState,{
				addedRows,
				rowChanges,
				editingRowIds,
				onCommitChanges				: this.commitChanges,
				columnExtensions			: tableColumnExtensions,
				onAddedRowsChange			: this.changeAddedRows,
				onRowChangesChange			: this.changeRowChanges,
				onEditingRowIdsChange		: this.changeEditingRowIds, 
			}),
		    	_(_dxgrb.DragDropProvider,null),
			_(_dxgrb.Table,{
					columnExtensions		: tableColumnExtensions
				}),
			multiselect && _(_dxgrb.TableSelection,{
					showSelectAll:!0
				}),
			_(_dxgrb.TableColumnReordering, {
					order:columnOrder,
					onOrderChange			:this.onColumnOrderChange
				}),
			_(_dxgrb.TableColumnResizing, {
					columnWidths,
					onColumnWidthsChange
				}),		  
			_(_dxgrb.TableHeaderRow,  { 
					showSortingControls: !_disableIntegratedSorting
				}),
			!viewMode && _(_dxgrb.TableEditRow, { 
					cellComponent			:this.EditCell
				}),
			!multiselect && !viewMode && _(_dxgrb.TableEditColumn, { 
				showAddCommand		: crudFlags &&  crudFlags.insert, 
				showEditCommand		: crudFlags &&  crudFlags.edit, 
				showDeleteCommand	: crudFlags &&  crudFlags.remove, 
					commandComponent	: Command
				}),

			_importClicked && _(extendGrid,{ 
					name: "tableCell", 
					predicate: rest => {if (rest.tableRow.key === "heading" && rest.tableColumn.key === "editCommand" && rest.tableRow.type === "heading") { return true;}} 
				}, _(TemplateConnector, {} ,(getters, actions)=>{
					return _(_dxgrb.TableEditColumn.HeaderCell, {}, 
						crudFlags &&  crudFlags.insert && _(Command, {
							id: 'add',
							onExecute:()=>actions.addRow()
  					}),
						_importClicked && _(Command, {
							id: 'import',
							onExecute:()=>_importClicked()
						}));
				})
			),

			rows.length>iwb.detailPageSize	?  _(_dxgrb.PagingPanel, {pageSizes: pageSizes || iwb.detailPageSize}) 		: null,
			!_disableIntegratedGrouping 	? _(_dxgrb.TableGroupRow,null)   											: null,
			!_disableIntegratedGrouping || !_disableIntegratedSorting || !_disableSearchPanel ? _(_dxgrb.Toolbar,null)	: null,
			!_disableSearchPanel 			? _(_dxgrb.SearchPanel, {messages:{searchPlaceholder:'Hızlı Arama...'}}) 	: null,
			!_disableIntegratedGrouping 	? _(_dxgrb.GroupingPanel,{showSortingControls:true})			   			: null,
		);
	}
}

class XMainGrid extends React.PureComponent {
	  constructor(props) {
		    super(props);
		    var oldGridState = iwb.grids[props.id];
			if(iwb.debug)console.log('XMainGrid', props);
			var onEditClick = ({event,rowData})=>{
				var props=this.props;	
				var pkz = buildParams2(props.pk,rowData);
				var url = 'showForm?a=1&_fid='+props.crudFormId+pkz;
				if(props._postUpdate){ 
					var url=this.props._postUpdate(rowData, url, props); 
					if(!url)return;
				}
				var modal=event.ctrlKey && !!event.ctrlKey;
				props.openTab('1-'+pkz,url+(modal?'&_modal=1':''),{},{modal:modal})
			};
			var afterCrud = function(){
				if(iwb.debug)console.log('hello fro console')
				this.loadData();
			}.bind(this);

			var onDeleteClick = ({event,rowData}) => {
				var props=this.props;	
				var pkz = buildParams2(props.pk,rowData);
				var url = 'ajaxPostForm?a=3&_fid='+props.crudFormId+pkz;
				yesNoDialog({ text:"Are you Sure!", callback:(success)=>{
					if(success){
						iwb.request({ url, successCallback:()=>this.loadData(true)});
					}
				}});
			}
		
			if(oldGridState){
		    	this.state = oldGridState;
		    	this.dontRefresh = true;
		    } else {
				var columns=[], tableColumnExtensions=[];
				const canIOpenActions =(props.crudFlags && (props.crudFlags.edit || props.crudFlags.remove)) || props.menuButtons;
				if(canIOpenActions){
					columns.push({name:'_qw_',title:'.',getCellValue:function(rowData){
						return _(XGridRowAction ,{ 
							...{ rowData }, 
							...{ menuButtons : props.menuButtons },
							...{ crudFlags: props.crudFlags },
							...{ onEditClick, onDeleteClick, afterCrud}
						});
					}});
			    	tableColumnExtensions.push({columnName:'_qw_',width:60, align:'right',sortingEnabled:false});
			    }
			    var c=props.columns;
			    for(var qi=0;qi<c.length;qi++){
			    	var v={name:c[qi].name, title:c[qi].title};
			    	switch(c[qi].name){
			    	case	'pkpkpk_faf':v.title=_('i',{className:'icon-paper-clip'});break;
			    	case	'pkpkpk_ms':v.title=_('i',{className:'icon-envelope'});break;
			    	case	'pkpkpk_cf':v.title=_('i',{className:'icon-bubble'});break;
			    	case	'pkpkpk_apf':v.title=_('i',{className:'icon-picture'});break;
			    	case	'pkpkpk_vcsf':v.title=_('i',{className:'icon-social-github'});break;
			    	}
			    	//if(c[qi].formatter)if(iwb.debug)console.log('c[qi].formatter',c[qi].formatter)
			    	if(c[qi].formatter)v.getCellValue=c[qi].formatter;
			    	columns.push(v);
			    	tableColumnExtensions.push({columnName:c[qi].name, align:c[qi].align||'left', width:1*c[qi].width,sortingEnabled:!!c[qi].sort});
			    }
			    var state = {
			      columns: columns, columnOrder:columns.map(function(a){return a.name}), 
			      tableColumnExtensions: tableColumnExtensions, columnWidths: tableColumnExtensions.map(function(a){return {columnName:a.columnName, width:a.width}}), 
			      rows: [],
			      sorting: [],
			      totalCount: 0,
			      pageSize: props.pageSize || iwb.detailPageSize,
			      pageSizes: props.pageSize>1 ? [parseInt(props.pageSize/2), props.pageSize, 3*props.pageSize]:[5,10,25,100],
			      currentPage: 0,
			      hideSF:true, loading: false
			    };
			    props.detailGrids && props.detailGrids.length>1 && props.detailGrids.map(function(a,key){
			    	if(key<2)state['dg-'+a.grid.gridId] = key<2;

			    });
			    this.state = state;
			}
			//methods
			this.onColumnWidthsChange 	= columnWidths		=>this.setState({columnWidths});
			this.onColumnOrderChange 	= columnOrder 		=>this.setState({columnOrder});
			this.onSortingChange 		= sorting			=>this.setState({loading: true,sorting});
			this.onCurrentPageChange 	= currentPage		=>this.setState({ loading: true, currentPage});
			this.onGlobalSearch 		= v 				=>this.loadData(!0, {xsearch:v && v.target ? v.target.value:v});
			iwb.onGlobalSearch2 		= this.onGlobalSearch;
			this.onPageSizeChange 		= pageSize 			=>{
				var totalPages 			= Math.ceil(this.state.totalCount / pageSize);
				var currentPage 		= Math.min(this.state.currentPage, totalPages - 1);
				this.setState({ loading: true,pageSize,currentPage });
			}
			this.toggleDetailGrid		 = e 				=> {
				var c = e.target;
				var s = {};
				s[c.name] = c.checked;
				this.setState(s);
			}
			this.TableRow = tableRowData =>{
				var props=this.props;
				return _(_dxgrb.Table.Row, props.openTab && props.crudFlags && props.crudFlags.edit && props.pk && props.crudFormId ? 
					{...tableRowData, ...{ onDoubleClick:(event)=>{ onEditClick({event,rowData:tableRowData.row})}, style:{cursor:'pointer'} }}
					: tableRowData);
			}
			this.toggleSearch 			= ()				=>{				
				var sf = document.getElementById('sf-'+this.props.id);
				if(sf){
					var eq = document.getElementById('eq-'+this.props.id);
					if(sf.classList.contains('sf-hidden')){ 
						eq.classList.add('rotate-90deg');
					} else { 
						eq.classList.remove('rotate-90deg'); }
					sf.classList.toggle('sf-hidden');
				}
				return false;
			}
		    this.openBI 				= ()				=>{
				var props=this.props, columns=this.state.tableColumnExtensions, columnOrder = this.state.columnOrder, cmap={};
				var url='grd/'+props.name+'.';
				var params='?_gid='+props.gridId+'&_columns=';
				columns.map(function(oo){cmap[oo.columnName]=oo.width});
				columnOrder.map(function(oo){params+=oo+','+(cmap[oo]||100)+';'});
				iwb.showModal({title:'RAPORLAR',footer:false, color:'danger', size:'sm', 
					body:_(ListGroup,{style:{fontSize: "1.0rem"}}
							,_('b',null,'Exports')
							,_(ListGroupItem,{tag:'a',href:url+'xls'+params, target:'_blank', action:!0},_('i',{className:'float-right text-success fa fa-file-excel'}),' ', 'Export to Excel')
							,_(ListGroupItem,{tag:'a',href:url+'pdf'+params, target:'_blank', action:!0},_('i',{className:'float-right text-danger fa fa-file-pdf'}),' ','Export to PDF')
							,_(ListGroupItem,{tag:'a',href:url+'csv'+params, target:'_blank', action:!0},_('i',{className:'float-right text-secondary fa fa-file-alt'}),' ','Export to CSV File')
							,_(ListGroupItem,{tag:'a',href:url+'txt'+params, target:'_blank', action:!0},_('i',{className:'float-right text-secondary fa fa-file-word'}),' ','Export to Text File')
							,_('hr')
							,_('b',null,'BI')
							,_(ListGroupItem,{tag:'a',href:'showPage?_tid=' + (props.crudTableId ? '1200&xtable_id='+props.crudTableId:'2395&xquery_id='+props.queryId), target:'_blank', action:!0/*, className:'list-group-item-danger2'*/},_('i',{className:'float-right text-primary fa fa-th'}),' ','Pivot Table')
							,_(ListGroupItem,{tag:'a',href:'showPage?_tid='+ (props.crudTableId ? '784&xtable_id='+props.crudTableId:'2413&xquery_id='+props.queryId), target:'_blank', action:!0},_('i',{className:'float-right text-primary fa fa-table'}),' ','Data List')
						)
				});
			}
		    if(this.props.searchForm || (this.props.detailGrids && this.props.detailGrids.length>1)){//hidden:!!this.props.grid.globalSearch
		    	var self = this;
		    	this.searchForm = _(Nav, {style:{}},this.props.searchForm && _('span',null,_('div',{className:'hr-text'},_('h6',null,'Arama Kriterleri'))
			    	,_('div',{style:{zoom:'.9'}},_(this.props.searchForm,{parentCt:this}),_('div',{className:'form-group',style:{paddingTop:10}},_(Button, {color: "danger", style:{width:'100%', borderRadius:2},onClick:() => {this.loadData(!0);} },"ARA")))
			    	,_('div',{style:{height:10}}),_('div',{className:'hr-text'},_('h6',null,'Şablonlar'))
			    	,_(Link,{style:{padding:2},to:''},_('i',{className:'icon-star'}),' ',' Yıllık Faturalar') //TODO
			   // 	,_(Link,{style:{padding:'2px'},to:''},_('i',{className:'icon-star'}),' ',' Ankara')
			    	,_(Link,{style:{padding:2,color:'#a0a0a0'},to:''},_('i',{className:'icon-plus'}),' ',' Yeni Şablon Ekle')
			    	,_('div',{style:{height:20}}))
			    	,this.props.detailGrids && this.props.detailGrids.length>1 && _('div',{className:'hr-text'},_('h6',null,'DETAY KAYITLAR'))
			    	,this.props.detailGrids && this.props.detailGrids.length>1 && this.props.detailGrids.map(function(a,key){
			    		return _('div',{key:key,style:{padding: "3px 0px 2px 3px", color: "#6d7284", fontSize:".9rem"}},a.grid.name,_("label",{ "className": "float-right switch switch-xs switch-3d switch-"+dgColors[key % dgColors.length]+" form-control-label" },
			    				  _("input", { name:'dg-'+a.grid.gridId, type: "checkbox", "className": "switch-input form-check-input", onChange:self.toggleDetailGrid,defaultChecked: self.state['dg-'+a.grid.gridId] }),
			    				  _("span", { "className": "switch-label" }), _("span", { "className": "switch-handle" })
			    				));
			    	})
		    	);
			}
			this.showDetail2 = (dgs)=>{
				var xxx=this;
				return function(row){
					if(row){
						var r=[];
						for(var qi=0;qi<dgs.length;qi++)if(dgs.length==1 ||xxx.state['dg-'+dgs[qi].grid.gridId]){
							var g2 = Object.assign({pk:dgs[qi].pk||{}},dgs[qi].grid); //buildParams2(obj.detailGrids[i].params, sel);
							if(g2._url)g2._url+=buildParams2(dgs[qi].params, row.row);
							else g2.rows=row.row[g2.detailRowsFieldName];
							g2.detailFlag=true; 
							r.push(_("li",{key:qi, className: "timeline-inverted" },
  //								  	_(XGridAction,{color:dgColors[qi%dgColors.length]}),
									  _("div", { className: "timeline-badge hover-shake "+dgColors[qi%dgColors.length], i:qi, onClick:function(e){var i=1*e.target.getAttribute('i');if(iwb.debug)console.log('dasss',i,dgs[i].grid); xxx.onOnNewRecord(e,dgs[i].grid,row.row);} , style:{cursor:"pointer"}}, _("i", { className: "icon-grid", style:{fontSize:17} })),
									  _("div", { className: "timeline-panel" },_("div",{ className: "timeline-heading" },
									  _("h5",{ /*style:{paddingBottom: '10px'},*/className: "timeline-title" },g2.name)
  //									,_('span',{className: "float-right", style:{marginTop:'-23px', marginRight:'15px'}},_('i',{ className: "icon-arrow-up", style:{marginRight: '12px'}}),' ',_('i',{ className: "icon-close"}),' ')
										),_(XGrid, Object.assign({responsive:true, openTab:xxx.props.openTab, showDetail:dgs[qi].detailGrids?xxx.showDetail2(dgs[qi].detailGrids):false},g2)))));
						}
						return r.length>0 && _("ul",{ className: "timeline" },r);
					} else return null;
				}
			}
			this.onOnNewRecord = (e,grid, row) =>{
				if(iwb.debug)console.log("XMainGrid.onOnNewRecord");
				if(!grid)grid=this.props;
				if(grid.crudFlags && grid.crudFlags.insert && this.props.openTab){
					var url = 'showForm?a=2&_fid='+grid.crudFormId;
					if(grid._postInsert){ url=grid._postInsert(row||{}, url, grid); if(!url)return;}
					var modal=!!e.ctrlKey;
					this.props.openTab('2-'+grid.gridId,url+(modal?'&_modal=1':''),{},{modal:modal})
				}
			}
			this.queryString=() => {
				const { sorting, pageSize, currentPage } = this.state;
				let queryString = this.props._url+'&limit='+pageSize+'&start='+(pageSize * currentPage);
				const columnSorting = sorting[0];
				if (columnSorting) {
				const sortingDirectionString = columnSorting.direction === 'desc' ? ' desc' : '';
				queryString += '&sort='+columnSorting.columnName+sortingDirectionString;
				}
				return queryString;
			}
			this.loadData = (force, params) => {
				const queryString = this.queryString();
				if (!force && queryString === this.lastQuery) { return; }
				this.setState({loading: true});
				var params= Object.assign({},params||{},this.form ? this.form.getValues():{});
				iwb.request({url:queryString, self:this, params, successCallback:function(result, cfg){
				cfg.self.setState({
					rows: result.data,
					totalCount: result.total_count,
					loading: false,
				});
				},errorCallback:function(error,cfg){
					cfg.self.setState({
						rows: [],
						totalCount: 0,
						loading: false,
					});
				}});
				this.lastQuery = queryString;
			}
		}
		componentDidMount() 	{ if(!this.dontRefresh)this.loadData(); this.dontRefresh=false; }
		componentDidUpdate() 	{ this.loadData(); this.dontRefresh=false;}
		componentWillUnmount()	{
			var state = Object.assign({},this.state);
			var sf = document.getElementById('sf-'+this.props.id);
			if(sf){ state.hideSF = sf.classList.contains('sf-hidden');}
			iwb.grids[this.props.id]=state;
		}
		render() {
			const {
				rows, columns, tableColumnExtensions,
				sorting, pageSize, pageSizes, 
				currentPage, totalCount, loading, 
				columnWidths, columnOrder
			} = this.state;
			var showDetail = this.props.detailGrids && this.props.detailGrids.length>0;
			var g = _(_dxgrb.Grid,{rows: rows, columns: columns, getRowId : (row) => row[this.props.keyField]},
					_(_dxrg.SortingState, !pageSize ? null : {sorting: sorting,onSortingChange: this.onSortingChange, columnExtensions:tableColumnExtensions}),
					!pageSize ? _(_dxrg.SearchState, null) : null,
					!pageSize ?  _(_dxrg.RowDetailState,null) : null,
					!pageSize && rows.length>1  ? _(_dxrg.IntegratedFiltering, null) : null,
					!pageSize && rows.length>1  ?  _(_dxrg.GroupingState,null) : null,		   
					!pageSize && rows.length>1  ? _(_dxrg.IntegratedGrouping ,null) : null,
					!pageSize && rows.length>1  ? _(_dxrg.IntegratedSorting,null) : null,
					showDetail?  _(_dxrg.RowDetailState,null):null,
					rows.length>iwb.detailPageSize || pageSize>1 ?  _(_dxrg.PagingState, pageSize>1 ? {currentPage: currentPage, onCurrentPageChange: this.onCurrentPageChange, pageSize: pageSize, onPageSizeChange: this.onPageSizeChange}:{}) : null,
					pageSize>1 && rows.length>1  ? _(_dxrg.CustomPaging, {totalCount: totalCount}) : null,
					_(_dxgrb.DragDropProvider,null),
					_(_dxgrb.Table, {columnExtensions: tableColumnExtensions, rowComponent:this.TableRow}),//,cellComponent: Cell
					_(_dxgrb.TableColumnReordering, {order:columnOrder,onOrderChange:this.onColumnOrderChange}),
					_(_dxgrb.TableColumnResizing, {columnWidths:columnWidths, onColumnWidthsChange:this.onColumnWidthsChange}),		  
					_(_dxgrb.TableHeaderRow, { showSortingControls: true }),
					showDetail?  _(_dxgrb.TableRowDetail, {contentComponent:this.showDetail2(this.props.detailGrids)}):null,
					rows.length>iwb.detailPageSize || pageSize>1 ?  _(_dxgrb.PagingPanel, {pageSizes: pageSizes || iwb.detailPageSize}) : null,
					!pageSize && rows.length>1  ? _(_dxgrb.TableGroupRow,null) : null,
					(!pageSize && rows.length>1)  ? _(_dxgrb.Toolbar,null):null,
					(!pageSize && rows.length>1) ? _(_dxgrb.SearchPanel, {messages:{searchPlaceholder:'Hızlı Arama...'},changeSearchValue:function(ax){if(iwb.debug)console.log('onValueChange',ax);}}) : null,//TODO
					!pageSize && rows.length>1  ? _(_dxgrb.GroupingPanel,{showSortingControls:true}) : null
//			    		,loading && iwb.loading()
			);
			
			return _('div',{className:'tab-grid mb-4'},this.searchForm && _('nav',{id:'sf-'+this.props.id,className:this.state.hideSF ? 'sf-hidden':''}, this.searchForm)
					,_('main',{className: "inbox"}, _(CardHeader, {}
									, this.searchForm && _(Button, {className:'btn-round-shadow', color: "secondary", onClick:this.toggleSearch},_('i',{id:'eq-'+this.props.id,className:'icon-magnifier'})), this.searchForm && " "
									, !this.searchForm &&_(Button, {className:'btn-round-shadow', disabled:loading, color: "secondary", onClick:() => {this.loadData(!0);} },_('i',{className:'icon-refresh'}))
									," ", this.props.crudFlags && this.props.crudFlags.insert ? _(Button, {className:'btn-round-shadow', color: "primary", onClick:(e) => {this.onOnNewRecord(e,this.props)} },_('i',{className:'icon-plus'})," YENİ KAYIT"):null
//										,_(Button,{className:'float-right btn-round-shadow hover-shake',color:'danger', onClick:this.toggleSearch},_('i',{style:{transition: "transform .2s"},id:'eq-'+this.props.id,className:'icon-equalizer'+(this.state.hideSF?'':' rotate-90deg')}))
									,_(Button,{className:'float-right btn-round-shadow hover-shake',color:'danger', onClick:this.openBI},_('i',{className:'icon-equalizer'}))
//										, this.props.globalSearch && _(Input,{type:"text", className:"float-right form-control w-25", onChange:this.onGlobalSearch, placeholder:"Hızlı Arama...", defaultValue:"", style:{marginTop: '-0.355rem', marginRight:'.4rem'}})
									)
						,g))
//			        {loading && <Loading />}
		}
}

class XPage extends React.Component {
	constructor(props){
		if(iwb.debugConstructor)if(iwb.debug)console.log('XPage.constructor',props);
		super(props);
	    document.getElementById('id-breed').innerHTML = this.props.grid.name;//+(detailSearch ? '<a href="#" onClick="return iwb.detailSearch();"><i class="icon-magnifier" title="Detaylı Arama" style="color: gray;font-size: 1.1rem;padding-left: 10px;"></i></a>':'');
	    iwb.killGlobalSearch();
	    var oldPageState = iwb.pages[props.grid.id];
	    if(oldPageState){
	    	this.state = oldPageState;
	    	this.dontRefresh = true;
	    } else {
		    this.state = {activeTab: 'x', tabs:[{k:'x',i:"icon-list", title:"Liste", v:props.grid}]}
		}
		this.toggle = (e)=>{
			var tab=false;
			if(e.target){ tab = e.target.getAttribute("k") } else { tab=e; }
			if (this.state.activeTab !== tab) {
				var tabs = this.state.tabs;
				for(var qi=0;qi<tabs.length;qi++)if(tabs[qi].k===tab){
					this.setState({activeTab:tab});
					return true;
				}
			}
			return false;
		};
	    this.openTab = (action,url,params, callAttributes)=>{
			if (this.state.activeTab !== action) {
					var tabs=this.state.tabs;
					for(var qi=1;qi<tabs.length;qi++)if(tabs[qi].k===action){
						this.toggle(action);
						return;
					}
				fetch(url,{
					body: JSON.stringify(params||{}), // must match 'Content-Type' header
					cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
					credentials: 'same-origin', // include, same-origin, *omit
					headers: {
						'content-type': 'application/json'
					},
					method: 'POST', // *GET, POST, PUT, DELETE, etc.
					mode: 'cors', // no-cors, cors, *same-origin
					redirect: 'follow', // *manual, follow, error
					referrer: 'no-referrer', // *client, no-referrer
				})
				.then((response) => {
					// status "0" to handle local files fetching (e.g. Cordova/Phonegap etc.)
					if (response.status === 200 || response.status === 0) {
						return response.text();
					} else {
						return Promise.reject(new Error(response.text() || response.statusText))
					}})
				.then(
					(result) => {
						if(result){
							var f;
							if(iwb.debug)console.log('openTab.callAttributes2', callAttributes)
							eval("f=function(callAttributes, parentCt){\n"+result+"\n}");
							var r=f(callAttributes || {}, this);
							if(r){
								var state = this.state;
								var plus = action.substr(0,1)=='2';
								tabs.push({k:action, i:plus ? "icon-plus":"icon-doc", title:plus ? " Yeni":" Düzenle",  v:r});
								state.activeTab=action;
								this.setState(state);
							}
						} else { toastr.error('Sonuc Gelmedi',' Error') }
					},
					(error) => { toastr.error(error,'Connection Error') }
				)
			}
		};
		iwb.openTab = this.openTab;
	    this.closeTab = ()=>{
			var state = this.state;
			var tab=state.activeTab;
			if(tab=='x')return;
			var tabs = state.tabs;
			for(var qi=1;qi<tabs.length;qi++)if(tabs[qi].k===tab){
				state.activeTab='x';
				tabs.splice(qi,1);
				state.tabs = tabs;
				this.setState(state);
				return;
			}
		};
	    this.showDetail2 = (dgs)=>{
			var self=this;
			return function(row){
				if(row){
						var r=[];
						for(var qi=0;qi<dgs.length;qi++)if(self.state['dg-'+dgs[qi].grid.gridId]){
							var g2 = Object.assign({pk:dgs[qi].pk||{}},dgs[qi].grid); //buildParams2(obj.detailGrids[i].params, sel);
							g2._url+=buildParams2(dgs[qi].params, row.row);
							g2.detailFlag=true;
							r.push(_("li",{key:qi, className: "timeline-inverted" },
										_("div", { className: "timeline-badge "+dgColors[qi%dgColors.length] }, _("i", { className: "icon-grid", style:{fontSize:17} })),
										_("div", { className: "timeline-panel" },_("div",{ className: "timeline-heading" },
										_("h5",{ /*style:{paddingBottom: '10px'},*/className: "timeline-title" },g2.name)
		//								,_('span',{className: "float-right", style:{marginTop:'-23px', marginRight:'15px'}},_('i',{ className: "icon-arrow-up", style:{marginRight: '12px'}}),' ',_('i',{ className: "icon-close"}),' ')
										),_(XGrid, Object.assign({responsive:true, openTab:self.openTab, showDetail:dgs[qi].detailGrids?self.showDetail2(dgs[qi].detailGrids):false},g2)))));
						  }
					return r.length>0 && _("ul",{ className: "timeline" },r);
				} else return null;
			}
		};
		this.openForm = (url)=>{
			if(url)this.openTab('1-'+Math.random(),url);
			return false;
		}
		iwb.openForm=this.openForm
	}
	componentWillUnmount(){ iwb.killGlobalSearch(); iwb.pages[this.props.grid.id]=Object.assign({},this.state); }
	
	render(){
		if(iwb.debugRender)if(iwb.debug)console.log('XPage.render');
		return _("div",{},
			_(Row,null,
				_(Col,{ className: "mb-4" },
					_(Nav,{ tabs: true, hidden:this.state.tabs.length==1 }
						, this.state.tabs.map((o,qi) => {
							return _(NavItem, {key:qi},_(NavLinkS,{className: classNames({active: this.state.activeTab === o.k}), k:o.k, onClick: (e) => {this.toggle(e);} },_('i',{className:o.i, k:o.k, title:o.title, onClick: (e) => {this.toggle(e);}}),o.title && o.k!='x' && this.state.activeTab === o.k ? o.title:null));
						})
					),
					_(TabContent,{ activeTab: this.state.activeTab }
						, this.state.tabs.map((o,qi) => {
							return _(TabPane, {key:qi, tabId: o.k }, o.v.gridId ? _(XMainGrid, Object.assign({openTab:this.openTab, closeTab:this.closeTab},o.v)): o.v);
						})
					)
				)
			)
		);
	}
}

class XCardMenu  extends React.PureComponent {
	render (){
 		return _(Col, {xs: "12",sm: "6",md:"6",lg: "6",xl: "4"}
 		, _(Link,{to:this.props.node.url},
				_(Card, {//url:this.props.node.url,onClick:(e)=>{if(iwb.debug)console.log(this.props.history);if(iwb.debug)console.log('this.props.router',this.props.router);this.props.history.push(this.props.node.url)},
		    className: "card-menu text-white bg-"+this.props.color,style:this.props.fadeOut ? {opacity:0, transform:"scale(.9)"}:(this.props.fadeOut===false?{transform: "scale(1.1)"}:{})
		},  _("i", {className: "big-icon "+(this.props.node.icon || "icon-settings"), style:this.props.color3 ? {color:this.props.color3}:{}})
		,_(CardBlock, {className: "pb-0"}, this.props.fadeOut===false? _("div",{className: "float-right",style:{
		        height: "56px", width:"56px",
			    background: "white",
			    padding: "0px",
			    borderRadius: "55px"
		    }},iwb.loaders.puff(56,56,this.props.color2)):_("i", {
		    className: "float-right "+(this.props.node.icon || "icon-settings"), style:{
		        fontSize: "30px", background: "white", padding: "13px", borderRadius: "55px", color:this.props.color2
		    }
		})
		, _("h1", {className: "mb-0"}, this.props.node.name), _("p", null,this.props.node.name+  " ile ilgili işlemler")))
		));
	}
}

class XCardMiniMenu  extends React.PureComponent {
	render (){
 		return _(Col, {xs: "4",sm: "3",md:"2",lg: "2",xl: "1"}
 		, _(Link,{to:this.props.node.url},
				_(Card, {//url:this.props.node.url,onClick:(e)=>{if(iwb.debug)console.log(this.props.history);if(iwb.debug)console.log('this.props.router',this.props.router);this.props.history.push(this.props.node.url)},
		    className: "card-mini-menu text-white bg-"+this.props.color,style:this.props.fadeOut ? {opacity:0, transform:"scale(.9)"}:(this.props.fadeOut===false?{transform: "scale(1.1)"}:{})
		}
		,_(CardBlock, {className: "pb-1", style:{textAlign:"center", padding:"0"}}, _("i", {
		    className: (this.props.node.icon || "icon-settings"), style:{
		        fontSize: "28px", padding: "12px", color:"white"
		    }
		})
		))), _("h6", {style:{textAlign:"center"}}, this.props.node.name));
	}
}

class XMainNav extends React.PureComponent {
	constructor(props){
		if(iwb.debugConstructor)if(iwb.debug)console.log('XMainNav.constructor',props);
		super(props);
		this.onGlobalSearch = v=> this.setState({xsearch:v&&v.target ? v.target.value:v});
		iwb.onGlobalSearch2 = this.onGlobalSearch;
		this.state = {xsearch:''};
	}
	componentWillUnmount()	{ if(iwb.debug)console.log('XMainNav.componentWillUnmount'); iwb.killGlobalSearch(); }
	componentDidUpdate() 	{ if(iwb.debug)console.log('XMainNav.componentDidUpdate'); }
	render (){
		if(iwb.debug)console.log('this.state.xsearch',this.state.xsearch);
		if(this.state.xsearch){
			var nodes=iwb.nav.findNodes(this.state.xsearch.toLowerCase(), {name:'Home',children:iwb.nav.items});
			if(iwb.debug)console.log('nodes', nodes);
			if(!nodes || !nodes.length)return 'Bulunamadı :(';
			return _('div', {className: 'animated fadeIn'}
		      ,_('div',{style:{height: '1.45rem'}})
		      ,"Arama Sonuçları",_("hr",{style: {marginTop: "0.4rem"}})
			  ,_(Row, {style:{maxWidth:"1300px"}},
					  nodes.map(function(o,qi){return _(XCardMiniMenu,{color:dgColors3[qi%dgColors3.length],node:o})})
			));
		}
		var path = this.props.path, node = this.props.node;
		var vi = false, siri=false;
	    if(path=='/' || path=='/iwb-home'){
	    	vi=[], siri=[];
	    	var qi=0, si=0;
	    	for(var k in iwb.nav.visitedItems){
	    		var o=iwb.nav.visitedItems[k];
	    		vi.push(_(XCardMiniMenu,{color:dgColors3[qi%dgColors3.length],node:o}));
	    		qi++;
	    		if(o.visitCnt>2){
		    		siri.push(_(XCardMiniMenu,{color:dgColors2[si%dgColors2.length],node:o}));
		    		si++;			    			
	    		}
	    	}
	    	if(qi==0)vi=false;
	    	else {
	    		vi=[_("div", { style: {height: "1.5rem"}}),"Açık Ekranlar",_("hr",{style: {marginTop: "0.4rem"}}),_(Row, {style:{maxWidth:"1300px"}}, vi)];
	    		if(si>0){
	    			if(siri.length>4){
	    				siri.splice(4,1000);
	    			}
	    			vi.push(_("div", { style: {height: "1.5rem"}}),"iWB Öneriler",_("hr",{style: {marginTop: "0.4rem"}}),_(Row, {style:{maxWidth:"1300px"}}, siri));
	    		}
	    	}
	    }
	    
		return _('div', {className: 'animated fadeIn'}
	      ,_('div',{style:{height: '1.45rem'}})
		  ,_(Row, {style:{maxWidth:"1300px"}},
			node.children.map(function(a,i){return _(XCardMenu,{key:i,color:dgColors2[i%dgColors2.length], color2:detailSpinnerColors2[i%detailSpinnerColors2.length], color3:dBGColors2[i%dBGColors2.length],node:a})})
		),vi);
	}
}

class XMainPanel extends React.PureComponent {
	constructor(props){
		if(iwb.debugConstructor)console.log('XMainPanel.constructor',props);
		super(props);
		this.state = {t:-1};
		//methods
	    this.loadPage = () => {
	    	var t = this.t;
	    	if (!iwb['t-' + t]) {
	    		fetch("showPage?_tid=" + t, {
	    				cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
	    				credentials: 'same-origin', // include, same-origin, *omit
	    				headers: {
	    					'content-type': 'application/json'
	    				},
	    				method: 'POST', // *GET, POST, PUT, DELETE, etc.
	    				mode: 'cors', // no-cors, cors, *same-origin
	    				redirect: 'follow', // *manual, follow, error
	    				referrer: 'no-referrer', // *client, no-referrer
	    			})
	    			.then((response) => {
	    				if (response.status === 200 || response.status === 0) {
	    					return response.text();
	    				} else {
	    					return Promise.reject(new Error(response.statusText))
	    				}
	    			})
	    			.then(
	    				(result) => {
	    					if (result) {
	    						var f;
	    						eval("f=function(callAttributes, parentCt){\n" + result + "\n}");
	    						var r = f(false, this);
	    						if (r) {
	    							r = _('div', {
	    								className: 'animated fadeIn'
	    							}, r);
	    							iwb['t-' + t] = r;
	    							this.setState({
	    								t: t
	    							});
	    							iwb.nav.visitItem(this.props.match.path);
	    						}
	    					} else {
	    						toastr.error('Sonuc Gelmedi', ' Error');
	    					}
	    				},
	    				(error) => {
	    					toastr.error(error, 'Connection Error');
	    				}
	    			)
	    	} else if (t != this.state.t) this.setState({
	    		t: t
	    	});
	    }
	}	
	componentDidMount() 	{	if(iwb.debug)console.log('XMainPanel.componentDidMount',this.props.match.path);if(!this.l)this.loadPage();}
	componentDidUpdate() 	{ 	if(iwb.debug)console.log('XMainPanel.componentDidUpdate',this.props.match.path); if(!this.l)this.loadPage();}
	componentDidCatch()		{	if(iwb.debug)console.log('XMainPanel.componentDidCatch',this);}
	componentWillUnmount()	{ 	if(iwb.debug)console.log('XMainPanel.componentWillUnmount',this.props.match.path);}
	render() {
		var path = this.props.match.path;
		var children = {name:'Home',children:iwb.nav.items};
		var node = path=='/' || path=='/iwb-home'? children: iwb.nav.findNode(this.props.match.path, children);
		if(iwb.debug)console.log('XMainPanel:render:',path, node);
		if(node){
			var ix= path.indexOf("showPage");
			if(ix>-1){
				var t = 1*path.substr(ix+"showPage".length);
				this.t=t;
				if(t!=this.state.t){
					if(this.l){
						var ll = this.l;
						this.l=false;
						return _('div', {className: 'animated fadeIn'}
					      ,_('div',{style:{height: '1.45rem'}})
						  ,_(Row, {style:{maxWidth:"1300px"}},
							ll.children.map(function(a,i){
								return _(XCardMenu,{key:i,color:dgColors2[i%dgColors2.length],color2:detailSpinnerColors2[i%detailSpinnerColors2.length], color3:dBGColors2[i%dBGColors2.length], node:a, fadeOut:a.url!=node.url})})
						));
					}
					return _(XLoading, null);
				}
				var r = iwb.nav.visitedItems[path];
				if(r)r.visitCnt++;
				return iwb['t-'+t];// || null;
			} else {
			    var d = document.getElementById('id-breed');
			    if(d)d.innerHTML =node.name || 'Home';
			    this.l = node;
			    return _(XMainNav,{path:path, node:node});
			}
		} else {
			this.l=false;
			return 'ERROR! Wrong Page';
		}
	}
}
/**
 * will work on first index.htm request
 */
class XLoading extends React.Component {
	render(){
		return _("span",{style:{position:"fixed",left:"48%",top:"45%"}},iwb.loading);
	}
}