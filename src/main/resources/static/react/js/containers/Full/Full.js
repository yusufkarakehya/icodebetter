var froutes = [],
	fr = false;
for (var k in routes) {
	if (k.indexOf('showPage') > -1) {
		froutes.push(_(Route, {
			path: k,
			name: routes[k],
			component: XMainPanel
		}));
	} else if (k.length > 2) { //folder menu demek
		froutes.push(_(Route, {
			path: k,
			name: routes[k],
			component: XMainPanel
		}));
	}
}
froutes.push(_(Route, {
	path: '/iwb-home',
	name: 'Home',
	component: XMainPanel
}));
fr = _(Redirect, {
	from: "/",
	to: '/iwb-home'
});
froutes.push(fr);

class Full extends React.Component {
	componentDidMount() {
		iwb.navbar = document.getElementById('id-header');
		window.onscroll = function () {
			if (window.pageYOffset > 30) iwb.navbar.classList.add("sticky");
			else iwb.navbar.classList.remove("sticky");
		};
	}
	render() {
		return _("div", {
				className: "app"
			},
			_(Header, null),
			_("div", {
					className: "app-body"
				},
				_(Sidebar, this.props), //this.props
				_("main", {
						className: "main"
					},
					//    		      _(Breadcrumb2, null),
					_('div', {
						style: {
							marginBottom: '1.5rem'
						}
					}),
					_(Container, {
							fluid: true
						},
						_(Switch, {
							children: froutes
						})
					)
				),
				_(Aside, null)
			), _(XLoginDialog, null), _(XModal, null),
			_(XLoadingSpinner, null)
		);
	}
}
/*export default Full;*/
