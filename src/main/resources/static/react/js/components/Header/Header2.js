
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
//  				Ext.Msg.wait('Signing into system', 'Please wait...');
  				document.location = 'main.htm?.r=' + new Date().getTime();
  			}});
	}
	componentDidMount(){
		var logo = document.getElementById('id-iwb-logo');
		if(logo)logo.innerHTML=iwb.logo+' <span>'+(iwb.logoLabel || 'iCodeBetter')+'</span>';
	}
	render() {
		var self = this;
		return _("div", { id:'id-header', className: "app-header navbar" },
    		  _(NavbarToggler, { className: "d-lg-none", onClick: iwb.mobileSidebarToggle }, "\u2630"),
    		  _(NavbarBrand, { href: "#", id:'id-iwb-logo' }),
    		  _(NavbarToggler, { className: "d-md-down-none", onClick: iwb.sidebarMinimize },"\u2630"),
   		      _(Breadcrumb2,  { className: "d-md-down-none"}),
    		  _(Nav,{ className: "d-md-down-none mr-auto", navbar: true })
    		  ,_scd.session && _scd.sessionId!='nosession' && _("ul",{ "className": "ml-auto navbar-nav" }
    		  
    		     ,(NavItem,null
		    		 ,_(Dropdown,{isOpen:this.state.rolesOpen, toggle: this.toggleRoles}
		    			,_(DropdownToggle, {tag:'span',className: "nav-link dropdown-toggle", style:{cursor:'pointer', textAlign:'right'} },_scd.userName, _("br", null), _scd.roleDsc)
		    			,qry_select_user_role1.data.length>0 && _(DropdownMenu,{className: this.state.rolesOpen ? 'show' : ''}
		    				,qry_select_user_role1.data.map(function(o,qi){
		    					return _(DropdownItem,{ur:o.user_role_id,onClick:self.selectNewRole},o.role_id_qw_);		    					
		    				}) 
		    			) 
		    		 )
    		     )
			  	 ,_(NavItem,null
				    ,_("a",{ href: "#", "className": "nav-link"},
					      _("img", { src: "sf/pic"+_scd.userId+".png", "className": "img-avatar", style:{border: ".5px solid #cecece"}, alt: _scd.email})
					    )
					  )
				  ,_(NavItem,{ "className": "d-md-down-none" },
				    _("a",{ "aria-haspopup": "true", href: "#", "className": "nav-link",onClick: iwb.asideToggle},
				      _("i", { style:{fontSize:"18px", color:"#333"},"className": "icon-bubbles" }),
				      _("span",{ "className": "badge badge-danger badge-pill" },"7")
				    )
				  )
				)
    		  	,_(NavbarToggler, {className: "d-md-down-none",onClick: iwb.asideToggle}
    		  			, _('i', {className:'oi oi-grid-three-up', style:{color: '#4e4e4e'}})
//    		  			, _('span',{className:'navbar-toggler-icon'})
    		  			)
    		  	,' '
//    		 , _(NavbarBrand, { href: "#" }),
//    		  ,_(Breadcrumb2, null)
    		);
  }
}

/*export default Header;*/
