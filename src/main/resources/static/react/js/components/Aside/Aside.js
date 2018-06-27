class Aside extends React.Component {
	constructor(props){
		super(props);
	    this.toggle = this.toggle.bind(this);
	    this.state = {users:false}
		iwb.asideToggleX = this.toggle;		
	}
	toggle(e){
		var c=document.body.classList;
		var v = c.contains('aside-menu-hidden');
	    c.toggle('aside-menu-hidden');
		if(v){
			var self = this;
			iwb.request({url:'../app/ajaxQueryData?_qid=142&.r='+Math.random(), params:{}, successCallback:function(result, cfg){
				self.setState({users:result.data});
			}});			
		}
	}
  render() {
	  var users=this.state.users;

    return _("aside", { className: "aside-menu", style:{background: "transparent", borderLeft: "none"} }
//    		,_("div",{ style: {height:"23px"} })
    		,_("div",{ className: "row" },
    				_("div",{ className: "mb-4 col" },
    				  /*  _("ul",{ className: "nav nav-tabs" },
    					_("li",{ className: "nav-item", style: {borderTop: "1px solid #ccc", borderLeft: "1px solid #ccc"} },
    					    _("a",{ className: "active nav-link", style: {background: "rgba(0,0,0,.05)", borderBottomColor: "#f2f4f6"} },
    						_("i", { className: "icon-bubbles" })
    					    )
    					),
    					_("li",{ className: "nav-item" },
    					    _("a",{ k: "2", className: "nav-link" },
    						_("i", { className: "icon-settings" })
    					    )
    					)
    				    ),*/
    					_('div',{style:{position: "relative", top: "0px", left: "122px", width: "51px", height: "55px", background: "#e7e8ea", marginTop: "-55px", borderBottom: "1px #e7e8ea solid", zIndex: "2", borderLeft: "1px solid rgb(204, 204, 204)", borderRight: "1px solid rgb(204, 204, 204)"}}),
    				    _("div",{ className: "tab-content", style: {boxShadow: "none", background: "rgba(0,0,0,.05)", height: "calc(100vh - 2.375rem)", borderLeft: "1px solid #ccc", borderTop: "1px solid #ccc"} },
    					_("div",{ className: "tab-pane pt-2 active" },
    					    _("div",{ className: "row" },
    						_("div",{ className: "col", style: {padding: "2px 17px"} }
//    							,_('div',{className: "p-1"},_('h6',null,'KİŞİLER'))
    						    ,users && users.map(function(u,qi){return _("div",{ className: "card card-user"},
    									_("div",{ className: "clearfix p-0 card-body" },
    										    _("i",{ className: "p-1-5 mr-1 float-left" },
    											_("img", { src: "../app/sf/pic"+u.user_id+".png", className: "img-avatar", style: {border: "none", width: "36px", height: "36px"} })
    										    ),
    										    _("div",{ className: "pt-3", style: {color: "#777 !important"} },
    											u.adi_soyadi
    										    )
    										    ,1*u.chat_status_tip>0 && _('i',{className:"float-right user-online"})
    										));}
    									)
    						)
    					    )
    					)
    				    )
    				)
    			));
  }
}
