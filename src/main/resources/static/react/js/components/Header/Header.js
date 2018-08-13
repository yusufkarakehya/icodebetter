
class Header extends React.Component {
	constructor(props) {
	    super(props);
	    this.toggleRoles = this.toggleRoles.bind(this);
	    this.selectNewRole = this.selectNewRole.bind(this);
	    this.state = {rolesOpen: false};
	}
  	toggleRoles() {
	    this.setState({
	    	rolesOpen: !this.state.rolesOpen
	    });
	}
  	selectNewRole(e) {
  		var userRoleId = e.target.getAttribute('ur');
  		if(userRoleId && 1*userRoleId)iwb.request({
  			url: "ajaxSelectUserRole?userRoleId=" + userRoleId + "&userCustomizationId=" + _scd.userCustomizationId,
  			successCallback: function() {
  				document.location = 'main.htm?.r=' + new Date().getTime();
  			}});
	}
	componentDidMount(){
		document.getElementById('id-iwb-logo').innerHTML=iwb.logo+' <span>iCodeBetter</span>';
	}
	render() {
		var self = this;
		return _("div", { id:'id-header', className: "app-header navbar" },
    		  _(NavbarToggler, { className: "d-lg-none", onClick: iwb.mobileSidebarToggle }, _('i',{className:'oi oi-menu', style:{fontSize:'16px'}})),
    		  _(NavbarBrand, { href: "#", id:'id-iwb-logo' }),
    		  _(NavbarToggler, { className: "d-md-down-none", onClick: iwb.sidebarMinimize },_('i',{className:'oi oi-menu', style:{fontSize:'16px'}})),
   		      _(Breadcrumb2,  { className: "d-md-down-none"}),
    		  _(Nav,{ className: "d-md-down-none mr-auto", navbar: true })
    		  ,_scd.session && _scd.sessionId!='nosession' && _("ul",{ "className": "ml-auto navbar-nav" }
    		  
    		 /*    ,(NavItem,null
		    		 ,_(Dropdown,{isOpen:this.state.rolesOpen, toggle: this.toggleRoles}
		    			,_(DropdownToggle, {tag:'span',className: "nav-link dropdown-toggle", style:{cursor:'pointer', textAlign:'right'} },_scd.userName, _("br", null), _scd.roleDsc)
		    			,qry_select_user_role1.data.length>0 && _(DropdownMenu,{className: this.state.rolesOpen ? 'show' : ''}
		    				,qry_select_user_role1.data.map(function(o,qi){
		    					return _(DropdownItem,{ur:o.user_role_id,onClick:self.selectNewRole},o.role_id_qw_);		    					
		    				}) 
		    			) 
		    		 )
    		     )*/
			/*	 ,_(NavItem,{ "className": "d-md-down-none" },
				    _("a",{ href: "#", "className": "nav-link",onClick: function(e){e.preventDefault();alert('TODO');return false;}},
				      _("i", { style:{fontSize:"17px", color:"#888", fontWeight:'bold'},"className": "icon-magnifier" })
				    )
				 )	*/
				 ,_(NavItem,{ "className": "d-md-down-none nav-link" },
					_('input',{type:"text", autoComplete:'off',id:'id-global-search', className:"global-search", onChange:iwb.onGlobalSearch, placeholder:"Quick search...", defaultValue:""})
				    ,_("i", { onClick:function(){var c=document.getElementById('id-global-search');c.focus();}, style:{cursor:'pointer',  fontSize:"17px", fontWeight:'bold', position: 'absolute', top: '17px', right: '10px'},"className": "icon-magnifier" })
				 )	
		/*		 
				 
				 ,_(NavItem,{ "className": "d-md-down-none", style:{textAlign:'right'} }
					,_scd.completeName//, _("br", null), _scd.roleDsc 

				 )*/
				 ,_(NavItem,null
				    ,_("a",{ href: "#", "className": "nav-link", title:_scd.completeName+"\n"+_scd.roleDsc },
					      _("img", { src: "sf/pic"+_scd.userId+".png", "className": "img-avatar", style:{border: ".5px solid #cecece"}, alt: _scd.email})
					    )
				 )

				 ,_(NavItem,{ "className": "d-md-down-none" },
				    _("a",{ "aria-haspopup": "true", href: "#", "className": "nav-link",onClick: iwb.asideToggle},
				      _("i", { style:{fontSize:"18px"},"className": "icon-bubbles" }),
				      _("span",{ "className": "badge badge-danger badge-pill" },"7")
				    )
				 )				 
				 ,_(NavItem,null
		    		 ,_(Dropdown,{isOpen:this.state.rolesOpen, toggle: this.toggleRoles}
		    			,_(DropdownToggle, {tag:'i',className: "nav-link oi oi-grid-three-up", style:{fontSize:'18px', cursor:'pointer'} })
		    			,this.state.rolesOpen && qry_select_user_role1.data.length>0 && _(DropdownMenu,{className: this.state.rolesOpen ? 'show' : ''}  
		    				,this.state.rolesOpen && _('div',{style:{padding: "7px 13px",background: "#afafaf",  color: "#44423f", fontWeight: "500", fontSize:" 16px"}},'Roller')
		    				,qry_select_user_role1.data.map(function(o,qi){
		    					return _(DropdownItem,{ur:o.user_role_id,onClick:self.selectNewRole},_('i',{className:'icon-drop',style:{marginRight:'5px', fontSize:'12px',color:'#777'}}),o.role_id_qw_);		    					
		    				}) 
		    			) 
		    		 )
    		     )
				)
    		  /*	,_(NavbarToggler, {className: "d-md-down-none",onClick: iwb.asideToggle}
    		  			, _('i', {className:'oi oi-grid-three-up', style:{color: '#4e4e4e'}})
//    		  			, _('span',{className:'navbar-toggler-icon'})
    		  			)*/
    		  	,' '
//    		 , _(NavbarBrand, { href: "#" }),
//    		  ,_(Breadcrumb2, null)
    		);
  }
}

/*export default Header;*/
