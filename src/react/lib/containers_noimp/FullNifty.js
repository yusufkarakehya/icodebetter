var froutes = [],
  fr = false;
for (var k in routes) {
  if (k.indexOf("showPage") > -1) {
    froutes.push(
      _(Route, {
        path: k,
        name: routes[k],
        component: XMainPanel
      })
    );
  } else if (k.length > 2) {
    //folder menu demek
    froutes.push(
      _(Route, {
        path: k,
        name: routes[k],
        component: XMainPanel
      })
    );
  }
}
froutes.push(
  _(Route, {
    path: "/iwb-home",
    name: "Home",
    component: XMainPanel
  })
);
fr = _(Redirect, {
  from: "/",
  to: "/iwb-home"
});
froutes.push(fr);
class Full extends React.Component {
  componentDidMount() {
    iwb.niftyBar = document.getElementById("id-nifty-bar");
    window.onscroll = function() {
      var s = iwb.niftyBar.style;
      if (window.pageYOffset < 240) {
        s.height = 120 - parseInt(window.pageYOffset / 2) + "px";
        s.opacity = 1 - window.pageYOffset / 240;
      } else {
        s.height = "0px";
      }
    };
  }
  render() {
    return _(
      "div",
      {
        className: "app"
      },
      _(Header, null),
      _(
        "div",
        {
          className: "app-body"
        },
        _(Sidebar, this.props), //this.props
        _(
          "main",
          {
            className: "main"
          },
          _(Breadcrumb2, null),
          _("div", {
            id: "id-nifty-bar",
            style: {
              height: 120
            },
            className: "nifty-bar"
          }),
          _(
            Container,
            {
              fluid: true
            },
            _(Switch, {
              children: froutes
            })
          )
        ),
        _(Aside, null)
      ),
      _(XLoginDialog, null),
      _(XModal, null),
      _(XLoadingSpinner, null)
    );
  }
}
/*export default Full;*/
