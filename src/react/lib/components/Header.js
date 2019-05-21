import { Component, createElement } from "react";
export class Header extends Component {
  constructor(props) {
    super(props);
    this.toggleRoles = this.toggleRoles.bind(this);
    this.selectNewRole = this.selectNewRole.bind(this);
    this.state = { rolesOpen: false };
  }
  toggleRoles() {
    this.setState({
      rolesOpen: !this.state.rolesOpen
    });
  }
  selectNewRole(e) {
    var userRoleId = e.target.getAttribute("ur");
    if (userRoleId && 1 * userRoleId)
      iwb.request({
        url:
          "ajaxSelectUserRole?userRoleId=" +
          userRoleId +
          "&userCustomizationId=" +
          _scd.userCustomizationId,
        successCallback: function() {
          document.location = "main.htm?.r=" + new Date().getTime();
        }
      });
  }
  componentDidMount() {
    document.getElementById("id-iwb-logo").innerHTML =
      iwb.logo + " <span>iCodeBetter</span>";
  }
  render() {
    var self = this;
    return createElement(
      "div",
      { id: "id-header", className: "app-header navbar" },
      createElement(
        NavbarToggler,
        { className: "d-lg-none", onClick: iwb.mobileSidebarToggle },
        createElement("i", {
          className: "oi oi-menu",
          style: { fontSize: "16px" }
        })
      ),
      createElement(NavbarBrand, { href: "#", id: "id-iwb-logo" }),
      createElement(
        NavbarToggler,
        { className: "d-md-down-none", onClick: iwb.sidebarMinimize },
        createElement("i", {
          className: "oi oi-menu",
          style: { fontSize: "16px" }
        })
      ),
      createElement(Breadcrumb2, { className: "d-md-down-none" }),
      createElement(Nav, { className: "d-md-down-none mr-auto", navbar: true }),
      _scd.session &&
        _scd.sessionId != "nosession" &&
        createElement(
          "ul",
          { className: "ml-auto navbar-nav" },
          createElement(
            NavItem,
            { className: "d-md-down-none nav-link" },
            createElement("input", {
              type: "text",
              autoComplete: "off",
              id: "id-global-search",
              className: "global-search",
              onChange: iwb.onGlobalSearch,
              placeholder: "Quick search...",
              defaultValue: ""
            }),
            createElement("i", {
              onClick: function() {
                var c = document.getElementById("id-global-search");
                c.focus();
              },
              style: {
                cursor: "pointer",
                fontSize: "17px",
                fontWeight: "bold",
                position: "absolute",
                top: "17px",
                right: "10px"
              },
              className: "icon-magnifier"
            })
          ),
          createElement(
            NavItem,
            null,
            createElement(
              "a",
              {
                href: "#",
                className: "nav-link",
                title: _scd.completeName + "\n" + _scd.roleDsc
              },
              createElement("img", {
                src: "sf/pic" + _scd.userId + ".png",
                className: "img-avatar",
                style: { border: ".5px solid #cecece" },
                alt: _scd.email
              })
            )
          ),
          createElement(
            NavItem,
            { className: "d-md-down-none" },
            createElement(
              "a",
              {
                "aria-haspopup": "true",
                href: "#",
                className: "nav-link",
                onClick: iwb.asideToggle
              },
              createElement("i", {
                style: { fontSize: "18px" },
                className: "icon-bubbles"
              }),
              createElement(
                "span",
                { className: "badge badge-danger badge-pill" },
                "7"
              )
            )
          ),
          createElement(
            NavItem,
            null,
            createElement(
              Dropdown,
              { isOpen: this.state.rolesOpen, toggle: this.toggleRoles },
              createElement(DropdownToggle, {
                tag: "i",
                className: "nav-link oi oi-grid-three-up",
                style: { fontSize: "18px", cursor: "pointer" }
              }),
              this.state.rolesOpen &&
                qry_select_user_role1.data.length > 0 &&
                createElement(
                  DropdownMenu,
                  { className: this.state.rolesOpen ? "show" : "" },
                  this.state.rolesOpen &&
                    createElement(
                      "div",
                      {
                        style: {
                          padding: "7px 13px",
                          background: "#afafaf",
                          color: "#44423f",
                          fontWeight: "500",
                          fontSize: " 16px"
                        }
                      },
                      "Roller"
                    ),
                  qry_select_user_role1.data.map(function(o, qi) {
                    return createElement(
                      DropdownItem,
                      { ur: o.user_role_id, onClick: self.selectNewRole },
                      createElement("i", {
                        className: "icon-drop",
                        style: {
                          marginRight: "5px",
                          fontSize: "12px",
                          color: "#777"
                        }
                      }),
                      o.role_id_qw_
                    );
                  })
                )
            )
          )
        ),
      " "
    );
  }
}
