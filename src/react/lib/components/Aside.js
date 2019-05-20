import React, { Component, createElement } from "react";

export class Aside extends Component {
  constructor(props) {
    super(props);
    this.toggle = this.toggle.bind(this);
    this.state = { users: false };
    iwb.asideToggleX = this.toggle;
  }
  toggle(e) {
    var c = document.body.classList;
    var v = c.contains("aside-menu-hidden");
    c.toggle("aside-menu-hidden");
    if (v) {
      var self = this;
      iwb.request({
        url: "../app/ajaxQueryData?_qid=142&.r=" + Math.random(),
        params: {},
        successCallback: function(result, cfg) {
          self.setState({ users: result.data });
        }
      });
    }
  }
  render() {
    var users = this.state.users;

    return createElement(
      "aside",
      {
        className: "aside-menu",
        style: { background: "transparent", borderLeft: "none" }
      },
      //    		,createElement("div",{ style: {height:"23px"} })
      createElement(
        "div",
        { className: "row" },
        createElement(
          "div",
          { className: "mb-4 col" },
          /*  createElement("ul",{ className: "nav nav-tabs" },
                      createElement("li",{ className: "nav-item", style: {borderTop: "1px solid #ccc", borderLeft: "1px solid #ccc"} },
                          createElement("a",{ className: "active nav-link", style: {background: "rgba(0,0,0,.05)", borderBottomColor: "#f2f4f6"} },
                          createElement("i", { className: "icon-bubbles" })
                          )
                      ),
                      createElement("li",{ className: "nav-item" },
                          createElement("a",{ k: "2", className: "nav-link" },
                          createElement("i", { className: "icon-settings" })
                          )
                      )
                      ),*/
          createElement("div", {
            style: {
              position: "relative",
              top: "0px",
              left: "122px",
              width: "51px",
              height: "55px",
              background: "#e7e8ea",
              marginTop: "-55px",
              borderBottom: "1px #e7e8ea solid",
              zIndex: "2",
              borderLeft: "1px solid rgb(204, 204, 204)",
              borderRight: "1px solid rgb(204, 204, 204)"
            }
          }),
          createElement(
            "div",
            {
              className: "tab-content",
              style: {
                boxShadow: "none",
                background: "rgba(0,0,0,.05)",
                height: "calc(100vh - 2.375rem)",
                borderLeft: "1px solid #ccc",
                borderTop: "1px solid #ccc"
              }
            },
            createElement(
              "div",
              { className: "tab-pane pt-2 active" },
              createElement(
                "div",
                { className: "row" },
                createElement(
                  "div",
                  { className: "col", style: { padding: "2px 17px" } },
                  //    							,createElement('div',{className: "p-1"},createElement('h6',null,'KİŞİLER'))
                  users &&
                    users.map(function(u, qi) {
                      return createElement(
                        "div",
                        { className: "card card-user" },
                        createElement(
                          "div",
                          { className: "clearfix p-0 card-body" },
                          createElement(
                            "i",
                            { className: "p-1-5 mr-1 float-left" },
                            createElement("img", {
                              src: "../app/sf/pic" + u.user_id + ".png",
                              className: "img-avatar",
                              style: {
                                border: "none",
                                width: "36px",
                                height: "36px"
                              }
                            })
                          ),
                          createElement(
                            "div",
                            {
                              className: "pt-3",
                              style: { color: "#777 !important" }
                            },
                            u.adi_soyadi
                          ),
                          1 * u.chat_status_tip > 0 &&
                            createElement("i", {
                              className: "float-right user-online"
                            })
                        )
                      );
                    })
                )
              )
            )
          )
        )
      )
    );
  }
}
