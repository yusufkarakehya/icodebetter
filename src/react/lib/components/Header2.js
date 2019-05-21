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
    var logo = document.getElementById("id-iwb-logo");
    if (logo)
      logo.innerHTML =
        iwb.logo + " <span>" + (iwb.logoLabel || "iCodeBetter") + "</span>";
  }
  render() {
    var self = this;
    return createElement(
      "div",
      { id: "id-header", className: "app-header navbar" },
      createElement(
        NavbarToggler,
        { className: "d-lg-none", onClick: iwb.mobileSidebarToggle },
        "\u2630"
      ),
      createElement(NavbarBrand, { href: "#", id: "id-iwb-logo" }),
      createElement(
        NavbarToggler,
        { className: "d-md-down-none", onClick: iwb.sidebarMinimize },
        "\u2630"
      ),
      createElement(Breadcrumb2, { className: "d-md-down-none" }),
      createElement(Nav, { className: "d-md-down-none mr-auto", navbar: true }),
      _scd.session &&
        _scd.sessionId != "nosession" &&
        createElement(
          "ul",
          { className: "ml-auto navbar-nav" },
          (NavItem,
          null,
          createElement(
            Dropdown,
            { isOpen: this.state.rolesOpen, toggle: this.toggleRoles },
            createElement(
              DropdownToggle,
              {
                tag: "span",
                className: "nav-link dropdown-toggle",
                style: { cursor: "pointer", textAlign: "right" }
              },
              _scd.userName,
              createElement("br", null),
              _scd.roleDsc
            ),
            qry_select_user_role1.data.length > 0 &&
              createElement(
                DropdownMenu,
                { className: this.state.rolesOpen ? "show" : "" },
                qry_select_user_role1.data.map(function(o, qi) {
                  return createElement(
                    DropdownItem,
                    { ur: o.user_role_id, onClick: self.selectNewRole },
                    o.role_id_qw_
                  );
                })
              )
          )),
          createElement(
            NavItem,
            null,
            createElement(
              "a",
              { href: "#", className: "nav-link" },
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
                style: { fontSize: "18px", color: "#333" },
                className: "icon-bubbles"
              }),
              createElement(
                "span",
                { className: "badge badge-danger badge-pill" },
                "7"
              )
            )
          )
        ),
      createElement(
        NavbarToggler,
        { className: "d-md-down-none", onClick: iwb.asideToggle },
        createElement("i", {
          className: "oi oi-grid-three-up",
          style: { color: "#4e4e4e" }
        })
      ),
      " "
    );
  }
}
