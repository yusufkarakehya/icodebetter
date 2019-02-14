iwb.JSON2URI = function(j) {
  if (!j) return "";
  var s = "";
  for (key in j)
    s +=
      encodeURIComponent(key) +
      "=" +
      (j[key] === null || j[key] === false ? "" : encodeURIComponent(j[key])) +
      "&";
  return s;
};
iwb.showLoginDialog = function() {
  alert("TODO: showLoginDialog");
  return false;
};

iwb.requestErrorHandler = function(obj) {
  if (obj.errorType) {
    switch (obj.errorType) {
      case "session":
        return iwb.showLoginDialog();
      case "validation":
        iwb.notifyVue("error", obj.errors.join("<br/>"), "Validation Error");
        break;
      default:
        iwb.notifyVue(
          "error",
          obj.errorMsg || "Unknown ERROR",
          obj.errorType + " Error"
        );
    }
  } else {
    iwb.notifyVue("error", obj.errorMsg || "Unknown ERROR", "Request Error");
    //		alert(obj.errorMsg || 'Bilinmeyen ERROR');
  }
};
var ajaxErrorHandler = iwb.requestErrorHandler;

iwb.request = function(cfg) {
  if (!window.fetch) {
    alert("ERROR! window.fetch not supported");
    return false;
  }
  if (!cfg || !cfg.url) {
    alert("ERROR! config missing");
    return false;
  }
  fetch(cfg.url, {
    body: JSON.stringify(cfg.params || {}), // must match 'Content-Type' header
    cache: "no-cache", // *default, no-cache, reload, force-cache, only-if-cached
    credentials: "same-origin", // include, same-origin, *omit
    headers: {
      "content-type": "application/json"
    },
    method: "POST", // *GET, POST, PUT, DELETE, etc.
    mode: "cors", // no-cors, cors, *same-origin
    redirect: "follow", // *manual, follow, error
    referrer: "no-referrer" // *client, no-referrer
  })
    .then(function(response) {
      // status "0" to handle local files fetching (e.g. Cordova/Phonegap etc.)
      if (response.status === 200 || response.status === 0) {
        return response.json();
      } else {
        return Promise.reject(
          new Error(response.text() || response.statusText)
        );
      }
    })
    .then(
      function(result) {
        if (cfg.callback && cfg.callback(result, cfg) === false) return;
        if (result.success) {
          if (cfg.successCallback) cfg.successCallback(result, cfg);
        } else {
          if (cfg.errorCallback && cfg.errorCallback(result, cfg) === false)
            return;
          iwb.requestErrorHandler(result);
        }
      },
      function(error) {
        if (
          cfg.errorCallback &&
          cfg.errorCallback({ error: error }, cfg) === false
        )
          return;
        iwb.notifyVue("error", error || "Unknown ERROR", "Request Error");
        //	    	alert('ERROR! ' + error);
      }
    );
};

function disabledCheckBoxHtml(row, cell) {
  //TODO
  //		return h('img',{border:0,src:'../images/custom/'+(f ?'':'un')+'checked.gif'});
  return row[cell] && 1 * row[cell]
    ? h("i", { class: "fa fa-check", style: { color: "green" } })
    : null;
}
function gridUserRenderer(row, cell) {
  //TODO
  return row[cell + "_qw_"];
}

function gridQwRendererWithLink(t) {
  //tableId
  return function(row, cell) {
    return row[cell.property + "_qw_"];
  };
}
function editGridComboRenderer(cell, combo) {
  if (!combo || !combo.options)
    return function(row) {
      return "?x?: " + row[cell];
    };
  var moptions = {};
  combo.options.map(function(o) {
    moptions[o.id] = o;
  });
  combo.moptions = moptions;
  return function(row) {
    var c = row[cell];
    if (!c) return "";
    var o = combo.moptions[c];
    return o ? o.dsc : "???: " + row[cell];
  };
}

function editGridLovComboRenderer(cell, combo) {
  if (!combo || !combo.options)
    return function(row) {
      return "?x?: " + row[cell];
    };
  var moptions = {};
  combo.options.map(function(o) {
    moptions[o.id] = o;
  });
  combo.moptions = moptions;
  return function(row) {
    var c = row[cell];
    if (!c) return "";
    c = c.split(",");
    return c.map(function(o) {
      return combo.moptions[o];
    });
  };
}

function fileAttachmentHtml(row, cell) {
  //TODO
  return row[cell] && 1 * row[cell]
    ? h("i", { class: "icon-paper-clip" })
    : null;
}

function vcsHtml(row, cell) {
  return row[cell] && 1 * row[cell]
    ? h("i", { class: "icon-social-github" })
    : null;
}
function pictureHtml(row, cell) {
  return row[cell] && 1 * row[cell] ? h("i", { class: "icon-picture" }) : null;
}

function mailBoxRenderer(row, cell) {
  return row[cell] && 1 * row[cell] ? h("i", { class: "icon-envelope" }) : null;
}

function fmtDateTime(x) {
  return x ? moment(x).format("DD/MM/YYYY HH:ss") : "";
}

function fmtShortDate(x) {
  return x ? moment(x).format("DD/MM/YYYY") : "";
}

function strShortDate(x) {
  return x ? x.substr(0, 10) : "";
}

function strDateTime(x) {
  return x || "";
}

function strDateTimeAgo(x) {
  return x || "";
}

function getStrapSize(w) {
  if (w >= 700) return "lg";
  if (w >= 400) return "md";
  return "sm";
}
function getMasterGridSel(a, sel) {
  return sel;
}

function _() {
  return "";
}
iwb.ui = {};
iwb.ui.buildPanel = function(c) {
  if (c.grid) {
    if (!c.grid.pk) c.grid.pk = c.pk || c._pk;
    if (!c.grid.detailGrids) c.grid.detailGrids = c.detailGrids || false;
  } else if (c.card) {
    if (!c.card.pk) c.card.pk = c.pk || c._pk;
    if (!c.card.detailGrids) c.card.detailGrids = c.detailGrids || false;
  }
  return XPage(c);
};
function buildParams2(params, map) {
  var bp = "";
  for (var key in params) {
    var newKey = params[key];
    if (typeof newKey == "function") {
      bp += "&" + key + "=" + newKey(params);
    } else if (newKey.charAt(0) == "!")
      bp += "&" + key + "=" + newKey.substring(1);
    else bp += "&" + key + "=" + map[params[key]];
  }
  return bp;
}

iwb.getFieldRawValue = function(h, field, extraOptions) {
  if (!field || !field.value)
    return h("i", { style: "display:block;width:100%;color:gray;" }, "(boş)");
  var options = extraOptions || field.options;
  if (!options || !options.length) {
    var value = field.value;
    if (typeof value == "undefined" || value == "")
      return h("i", { style: "display:block;width:100%;color:gray;" }, "(boş)");
    return h("div", { class: "el-input" }, [
      h("b", { style: "font-weight:bold", class: "el-input__inner " }, value)
    ]);
  }
  var optionsMap = {};
  options.map(function(o) {
    optionsMap[o.id] = o.dsc;
  });
  var value = field.value;
  if (value.id) value = value.id;
  value = optionsMap[value];
  if (value == undefined || value == "")
    return h("i", { style: "display:block;width:100%;color:gray;" }, "(boş)");
  return h("div", { class: "el-input" }, [
    h("b", { style: "font-weight:bold", class: "el-input__inner " }, value)
  ]);
};

var XGrid = Vue.component("x-grid", {
  props: ["grid", "showForm", "row"],
  data() {
    var g = this.grid;
    var columns = Object.assign([], g.columns);
    if (g.multiselect) {
      columns.unshift({ type: "selection" });
    }
    if (g.detailGrids) {
      columns.unshift({ type: "expand" });
    }

    if (g.pageSize)
      columns.map(function(o) {
        if (o.sortable) o.sortable = "custom";
      });
    var f = false;
    columns.map(function(o) {
      if (o.minWidth) f = !0;
    });
    if (!f) {
      columns[columns.length - 1].minWidth = columns[columns.length - 1].width;
      delete columns[columns.length - 1].width;
    }
    return {
      loading: true,
      rows: [],
      sorting: [],
      totalCount: 0,
      columns: columns,
      lastQuery: "",
      currentPage: 0,
      pageSize: g.pageSize
    };
  },
  methods: {
    onNewRecord(e, grid, row) {
      var g = this.grid;
      if (g.crudFlags && g.crudFlags.insert && this.showForm) {
        var url = "showForm?a=2&_fid=" + g.crudFormId;
        if (g._postInsert) {
          url = g._postInsert(row || {}, url, grid);
          if (!url) return;
        }
        this.showForm(url, this.loadData);
      }
    },
    dblClick(row) {
      var g = this.grid;
      if (g.crudFlags && g.crudFlags.edit && this.showForm) {
        var pkz = buildParams2(this.grid.pk, row);
        var url = "showForm?a=1&_fid=" + this.grid.crudFormId + pkz;
        if (this.grid._postUpdate) {
          var url = this.grid._postUpdate(row, url, this.grid);
          if (!url) return;
        }
        this.showForm(url, this.loadData);
      }
    },
    expandChange(row, expandedRows) {
      console.log("expandChange", row);
    },
    sortChange(column) {
      this.sorting[0] = { columnName: column.prop, direction: column.order };
      this.loadData(!0);
    },
    queryString() {
      let queryString =
        this.grid._url + buildParams2(this.grid.params, this.row);
      if (this.pageSize > 0) {
        queryString +=
          "&limit=" +
          this.pageSize +
          "&start=" +
          this.pageSize * this.currentPage;
        const columnSorting = this.sorting[0];
        if (columnSorting) {
          const sortingDirectionString =
            columnSorting.direction === "descending" ? " desc" : "";
          queryString +=
            "&sort=" + columnSorting.columnName + sortingDirectionString;
        }
      }

      return queryString;
    },
    loadData(force, params) {
      const queryString = this.queryString();
      //			    if (!force && queryString === this.lastQuery) {return;}
      var params = Object.assign(
        {},
        params || {},
        this.form ? this.form.getValues() : {}
      );
      //var ll = ELEMENT.Loading.service();
      this.loading = true;
      iwb.request({
        url: queryString,
        params: params,
        successCallback: (result, cfg) => {
          this.rows = result.data;
          this.totalCount = result.total_count;
          this.loading = false;
        },
        errorCallback: (error, cfg) => {
          this.rows = [];
          this.totalCount = 0;
          this.loading = false;
        }
      });
      this.lastQuery = queryString;
    },
    currentPageChange(page) {
      console.log("currentPageChange", page);
      this.currentPage = page - 1;
      this.loadData(!0);
    }
  },
  mounted() {
    this.loadData(!0);
  },
  render(h) {
    var columns = this.columns;
    var g = this.grid;
    return h("div", {}, [
      h(
        "el-table",
        {
          style: "width: 100%",
          props: { stripe: !0, data: this.rows },
          directives: [{ name: "loading", value: this.loading }],
          on: {
            "row-dblclick": this.dblClick,
            "expand-change": this.expandChange,
            "sort-change": this.sortChange
          }
        },
        columns.map(function(p) {
          return h("el-table-column", { props: p });
        })
      ),
      g.pageSize &&
        h("el-row", { style: "padding-top: 10px" }, [
          h("el-pagination", {
            on: { "current-change": this.currentPageChange },
            props: {
              background: !0,
              currentPage: this.currentPage + 1,
              pageSize: g.pageSize,
              layout: "total, prev, pager, next",
              total: this.totalCount
            }
          })
        ])
    ]);
  }
});

var dgColors = ["warning", "secondary", "danger", "primary", "success", "info"];
var XMainGrid = Vue.component("x-main-grid", {
  props: ["grid", "showForm"],
  data() {
    var g = this.grid;
    var columns = Object.assign([], g.columns);
    if (g.multiselect) {
      columns.unshift({ type: "selection" });
    }
    if (g.detailGrids) {
      for (var qi = 0; qi < g.detailGrids.length; qi++) {
        if (!g.detailGrids[qi].grid.pk)
          g.detailGrids[qi].grid.pk =
            g.detailGrids[qi].pk || g.detailGrids[qi]._pk;
        if (!g.detailGrids[qi].grid.params)
          g.detailGrids[qi].grid.params = g.detailGrids[qi].params;
      }
      columns.unshift({ type: "expand" });
    }

    if (g.pageSize)
      columns.map(function(o) {
        if (o.sortable) o.sortable = "custom";
      });
    var f = false;
    columns.map(function(o) {
      if (o.minWidth) f = !0;
    });
    if (!f) {
      columns[columns.length - 1].minWidth = columns[columns.length - 1].width;
      delete columns[columns.length - 1].width;
    }
    return {
      loading: true,
      rows: [],
      sorting: [],
      totalCount: 0,
      columns: columns,
      lastQuery: "",
      currentPage: 0,
      pageSize: g.pageSize,
      expandedRowIds: {}
    };
  },
  methods: {
    onNewRecord(e, grid, row) {
      var g = grid; //this.grid;
      if (g.crudFlags && g.crudFlags.insert && this.showForm) {
        var url = "showForm?a=2&_fid=" + g.crudFormId;
        if (g._postInsert) {
          url = g._postInsert(row || {}, url, grid);
          if (!url) return;
        }
        this.showForm(url, this.loadData);
      }
    },
    dblClick(row) {
      var g = this.grid;
      if (g.crudFlags && g.crudFlags.edit && this.showForm) {
        var pkz = buildParams2(this.grid.pk, row);
        var url = "showForm?a=1&_fid=" + this.grid.crudFormId + pkz;
        if (this.grid._postUpdate) {
          var url = this.grid._postUpdate(row, url, this.grid);
          if (!url) return;
        }
        this.showForm(url, this.loadData);
      }
    },
    expandChange(row, expandedRows) {
      console.log("expandChange", row, expandedRows);
      return "Ali";
    },
    sortChange(column) {
      this.sorting[0] = { columnName: column.prop, direction: column.order };
      this.loadData(!0);
    },
    queryString() {
      let queryString =
        this.grid._url +
        "&limit=" +
        this.pageSize +
        "&start=" +
        this.pageSize * this.currentPage;
      const columnSorting = this.sorting[0];
      if (columnSorting) {
        const sortingDirectionString =
          columnSorting.direction === "descending" ? " desc" : "";
        queryString +=
          "&sort=" + columnSorting.columnName + sortingDirectionString;
      }

      return queryString;
    },
    loadData(force, params) {
      const queryString = this.queryString();
      //		    if (!force && queryString === this.lastQuery) {return;}
      var params = Object.assign(
        {},
        params || {},
        this.form ? this.form.getValues() : {}
      );
      //var ll = ELEMENT.Loading.service();
      this.loading = true;
      iwb.request({
        url: queryString,
        params: params,
        successCallback: (result, cfg) => {
          this.rows = result.data;
          this.totalCount = result.total_count;
          this.loading = false;
        },
        errorCallback: (error, cfg) => {
          this.rows = [];
          this.totalCount = 0;
          this.loading = false;
        }
      });
      this.lastQuery = queryString;
    },
    currentPageChange(page) {
      this.currentPage = page - 1;
      this.loadData(!0);
    }
  },
  mounted() {
    this.loadData(!0);
  },
  render(h) {
    var columns = this.columns,
      self = this;
    var g = this.grid;
    return h("card", {}, [
      h("el-row", { style: "padding: 5px" }, [
        (!0 || g.searchForm) &&
          h("el-button", { props: { icon: "el-icon-search", circle: !0 } }),
        h("el-button", {
          props: { icon: "el-icon-refresh", circle: !0 },
          on: { click: this.loadData }
        }),
        g.crudFlags &&
          g.crudFlags.insert &&
          h(
            "el-button",
            {
              class: "float-right",
              props: { type: "danger", icon: "el-icon-plus", round: !0 },
              on: { click: () => this.onNewRecord(false, g, {}) }
            },
            "NEW RECORD"
          )
        //   			                  ,h('el-button',{props:{icon:"el-icon-menu", circle:!0}})]),
      ]),
      h("hr", { style: "margin-bottom:0" }),
      h(
        "el-table",
        {
          style: "width: 100%",
          props: { stripe: !0, data: this.rows },
          directives: [{ name: "loading", value: this.loading }],
          on: {
            "row-dblclick": this.dblClick,
            "expand-change": this.expandChange,
            "sort-change": this.sortChange
          }
        },
        columns.map(function(p) {
          return p.type && p.type == "expand"
            ? h("el-table-column", {
                props: p,
                scopedSlots: {
                  default: function(props) {
                    return h(
                      "time-line",
                      { type: "simple" },
                      g.detailGrids.map(function(o, key) {
                        return h(
                          "time-line-item",
                          {
                            props: {
                              iconClick: () => {
                                self.onNewRecord(false, o.grid, props.row);
                              },
                              inverted: true,
                              badgeType: dgColors[key % dgColors.length],
                              badgeIcon: "now-ui-icons ui-1_simple-add"
                            }
                          },
                          [
                            h("h5", { slot: "header" }, o.grid.name),
                            h("p", { slot: "content" }, [
                              h(XGrid, {
                                props: {
                                  grid: o.grid,
                                  showForm: self.showForm,
                                  row: props.row
                                }
                              })
                            ])
                          ]
                        );
                      })
                    );
                  }
                }
              })
            : h("el-table-column", { props: p });
        })
      ),
      g.pageSize &&
        h("el-row", { style: "padding-top: 10px" }, [
          h("el-pagination", {
            class: "float-right",
            on: { "current-change": this.currentPageChange },
            props: {
              background: !0,
              currentPage: this.currentPage + 1,
              pageSize: g.pageSize,
              layout: "total, prev, pager, next",
              total: this.totalCount
            }
          })
        ])
    ]);
  }
});

function XPage(c) {
  var g = c.grid || c.card;
  return {
    render(h) {
      return h("div", { class: "container-fluid", style: "" }, [
        h("div", { class: "row" }, [
          h("div", { class: "col-12" }, [
            g.gridId
              ? h(XMainGrid, { props: { grid: g, showForm: this.showForm } })
              : h(XMainCard, { props: { card: g, showForm: this.showForm } })
          ])
        ])
      ]);
    },
    data() {
      return { bodyForm: false };
    },
    methods: {
      showForm(url, scallback) {
        fetch(url + "&_modal=1", {
          body: JSON.stringify({}), // must match 'Content-Type' header
          cache: "no-cache", // *default, no-cache, reload, force-cache, only-if-cached
          credentials: "same-origin", // include, same-origin, *omit
          headers: { "content-type": "application/json" },
          method: "POST", // *GET, POST, PUT, DELETE, etc.
          mode: "cors", // no-cors, cors, *same-origin
          redirect: "follow", // *manual, follow, error
          referrer: "no-referrer" // *client, no-referrer
        })
          .then(response => {
            // status "0" to handle local files fetching (e.g. Cordova/Phonegap etc.)
            if (response.status === 200 || response.status === 0) {
              return response.text();
            } else {
              return Promise.reject(
                new Error(response.text() || response.statusText)
              );
            }
          })
          .then(
            result => {
              if (result) {
                var f;
                eval(
                  "f=function(callAttributes, parentCt){\n" + result + "\n}"
                );
                var r = f({}, this);
                if (r) {
                  var bodyForm = r.body,
                    self = this;
                  iwb.showModal({
                    onSubmit: scfg => {
                      if (self.form)
                        self.form.submit({
                          callback: function(json, cfg) {
                            iwb.closeModal();
                            if (scallback) scallback(json);
                          }
                        });
                    },
                    subHeader:
                      url.indexOf("a=1") > 0 ? " · Update" : " · New Record",
                    header: r.cfg.name,
                    body: bodyForm,
                    closable: true,
                    viewMode: url.indexOf("a=1") > 0,
                    closableOutside: !1,
                    footer: true,
                    type: r.cfg.size || "lg"
                  });
                }
              } else {
                iwb.notifyVue("error", "Sonuc Gelmedi", " Error");
              }
            },
            error => {
              iwb.notifyVue("error", "Connection Error");
            }
          );
      }
    },
    mounted() {}
  };
}

iwb.pages = {};
var XMainPanel = Vue.component("x-main-panel", {
  data() {
    console.log("aha", this.$route.path);
    var path = this.$route.path;
    var ix = path.indexOf("showPage");
    if (ix > -1) {
      var tid = path.substr(ix + "showPage".length);
      var t = iwb.pages[tid] || false;
      return t ? { t: t } : { t: false, tid: 1 * tid };
    } else return {};
  },
  beforeCreate() {
    //		console.log('ShowPage.beforeCreate');
  },
  created() {
    //		console.log('ShowPage.created');
  },
  beforeMount() {
    //		console.log('ShowPage.beforeMount');
  },
  mounted() {
    console.log("ShowPage.mounted", this.$route);
    //this.t = this.$route.query._tid;
    if (!this.t) {
      var url = "showPage?_tid=" + this.tid,
        params = this.$route.query || {},
        callAttributes = false;
      fetch(url, {
        body: JSON.stringify(params), // must match 'Content-Type' header
        cache: "no-cache", // *default, no-cache, reload, force-cache, only-if-cached
        credentials: "same-origin", // include, same-origin, *omit
        headers: { "content-type": "application/json" },
        method: "POST", // *GET, POST, PUT, DELETE, etc.
        mode: "cors", // no-cors, cors, *same-origin
        redirect: "follow", // *manual, follow, error
        referrer: "no-referrer" // *client, no-referrer
      })
        .then(response => {
          // status "0" to handle local files fetching (e.g. Cordova/Phonegap etc.)
          if (response.status === 200 || response.status === 0) {
            return response.text();
          } else {
            return Promise.reject(
              new Error(response.text() || response.statusText)
            );
          }
        })
        .then(
          result => {
            if (result) {
              var f;
              eval("f=function(callAttributes,parentCt){\n" + result + "\n}");
              if (f) {
                this.t = iwb.pages[this.tid] = f({ params: params }, this);
              }
            } else {
              iwb.notifyVue("error", "Hata! Sonuc Gelmedi");
            }
          },
          error => {
            iwb.notifyVue("error", "Hata! " + error);
          }
        );
    }
  },
  beforeUpdate() {
    //		console.log('ShowPage.beforeUpdate',this.t);
  },
  updated() {
    //		console.log('ShowPage.updated',this.t);
  },
  beforeDestroy() {
    //		console.log('ShowPage.beforeDestroy',this.t);
  },
  render(h) {
    console.log("baha", this.$route.path);
    if (this.t) return h(this.t);
    else return false;
  }
});

iwb.notifyVue = function(xtype, msg, header, timeout) {
  xtype = xtype || "info";
  if (xtype == "error") xtype = "danger";
  //['', 'info', 'success', 'warning', 'danger']
  var color = Math.floor(Math.random() * 4 + 1);
  iwb.$notify({
    message: msg,
    timeout: timeout || 3000,
    icon: "now-ui-icons ui-1_bell-53",
    horizontalAlign: "right",
    verticalAlign: "top",
    type: xtype
  });
};
function cls2str(c) {
  if (!c) return "";
  if (Array.isArray(c)) return " " + c.join(" ");
  return " " + c;
}

iwb.changeRole = function(ur) {
  return false;
};

var SlideYUpTransition = vue2Transitions.SlideYUpTransition;
var XModal = Vue.component("modal", {
  render(h) {
    return (
      this.show &&
      h(SlideYUpTransition, { props: { duration: 500 } }, [
        h(
          "div",
          {
            class:
              "modal fade " +
              (this.show ? "show d-block" : "d-none") +
              (this.type === "mini" ? " modal-mini" : "") +
              cls2str(this.modalClasses),
            //		    	,role:'dialog',tabindex:'-1' TODO
            //,directives:[{name:'show',value:this.show}]
            on: this.closableOutside ? { click: this.closeModal } : undefined
          },
          [
            h(
              "div",
              {
                class:
                  "modal-dialog " +
                  (this.type && this.type.length == 2
                    ? "modal-" + this.type
                    : "") +
                  " " +
                  (this.type === "notice" ? "modal-notice" : "")
              },
              [
                h(
                  "div",
                  {
                    on: {
                      click: e => {
                        if (e && e.preventDefault) e.preventDefault();
                      }
                    },
                    class: "modal-content"
                  },
                  [
                    this.header &&
                      h(
                        "div",
                        { class: "modal-header" + cls2str(this.headerClasses) },
                        [
                          this.closable &&
                            h(
                              "button",
                              {
                                type: "button",
                                class: "close",
                                on: { click: this.closeModal }
                              },
                              [
                                h("i", {
                                  class: "now-ui-icons ui-1_simple-remove"
                                })
                              ]
                            ),
                          h("h3", { class: "modal-title" }, [
                            this.header,
                            !this.viewMode &&
                              this.subHeader &&
                              h("span", this.subHeader),
                            this.viewMode &&
                              h("el-button", {
                                style:
                                  "position: relative;top: -3px;right: -12px;",
                                props: { icon: "el-icon-edit", circle: !0 },
                                on: { click: this.toggleViewMode }
                              })
                          ])
                        ]
                      ),
                    //	                         this.header && h('hr',{style:'width:100%'}),
                    h(
                      "div",
                      { class: "modal-body" + cls2str(this.bodyClasses) },
                      [h(this.body, { props: { viewMode: this.viewMode } })]
                    ),
                    !this.viewMode &&
                      this.footer !== false &&
                      h(
                        "div",
                        { class: "modal-footer" + cls2str(this.footerClasses) },
                        this.footer === true
                          ? [
                              h(
                                "el-button",
                                {
                                  props: { type: "danger" },
                                  on: { click: this.submit }
                                },
                                "SAVE"
                              ),
                              h(
                                "el-button",
                                {
                                  props: { type: "info" },
                                  on: { click: this.closeModal }
                                },
                                "CLOSE"
                              )
                            ]
                          : [h(this.footer)]
                      )
                  ]
                )
              ]
            )
          ]
        )
      ])
    );
  },
  components: { SlideYUpTransition },
  data() {
    return {
      show: false,
      closable: true,
      closableOutside: true,
      type: "",
      header: false,
      subHeader: false,
      body: false,
      footer: false,
      footerClasses: "",
      modalClasses: "",
      headerClasses: "",
      bodyClasses: "",
      submit: false,
      viewMode: false
    };
  },
  methods: {
    toggleViewMode(e) {
      if (e && e.preventDefault) e.preventDefault();
      this.viewMode = !this.viewMode;
      this.closableOutside = this.viewMode;
    },
    closeModal(e) {
      if (e && e.preventDefault) e.preventDefault();
      this.show = false;
    },
    showModal(cfg) {
      this.show = !0;
      this.type = cfg.type || "";
      this.submit = cfg.onSubmit || false;
      this.body = cfg.body;
      this.closable =
        typeof cfg.closable == "undefined" ? true : !!cfg.closable;
      this.closableOutside =
        typeof cfg.closableOutside == "undefined"
          ? true
          : !!cfg.closableOutside;
      this.modalClasses = cfg.modalClasses || "";
      this.headerClasses = cfg.headerClasses || "";
      this.bodyClasses = cfg.bodyClasses || "";
      this.footerClasses = cfg.footerClasses || "";
      this.header = cfg.header || false;
      this.subHeader = cfg.subHeader || false;
      this.footer = cfg.footer || false;
      this.viewMode = cfg.viewMode || false;
    }
  },
  watch: {
    show(val) {
      let documentClasses = document.body.classList;
      if (val) {
        documentClasses.add("modal-open");
      } else {
        documentClasses.remove("modal-open");
      }
    }
  },
  mounted() {
    iwb.showModal = this.showModal;
    iwb.closeModal = this.closeModal;
  }
});

Vue.component("time-line", {
  template: `<card class="card-timeline" plain><ul class="timeline" :class="{'timeline-simple': type === 'simple'}"><slot></slot></ul></card>`,
  props: {
    type: {
      type: String,
      default: ""
    }
  }
});

Vue.component("time-line-item", {
  template: `<li :class="{'timeline-inverted': inverted}">
    <slot name="badge">
      <div class="timeline-badge" :class="badgeType" v-on:click="iconClick">
        <i :class="badgeIcon"></i>
      </div>
    </slot>
    <div class="timeline-panel">
      <div class="timeline-heading">
        <slot name="header"></slot>
      </div>
      <div class="timeline-body" v-if="$slots.content">
        <slot name="content"></slot>
      </div>
      <h6 v-if="$slots.footer">
        <slot name="footer"></slot>
      </h6>
    </div>
  </li>`,
  props: {
    inverted: Boolean,
    badgeType: {
      type: String,
      default: "success"
    },
    iconClick: Function,
    badgeIcon: {
      type: String,
      default: ""
    }
  }
});

var XGraph = Vue.component("xgraph", {
  props: ["o"],
  data() {
    return {};
  },
  mounted() {
    var dg = this.o.graph;
    var gid = "idG" + dg.graphId;
    iwb.graphAmchart(dg, gid);
  },
  render(h) {
    return h("div", {
      style: "width:100%;height:" + (this.o.props.height || "20vw"),
      attrs: { id: "idG" + this.o.graph.graphId }
    });
  }
});

var XPortlet = Vue.component("xportlet", {
  props: ["o"],
  data() {
    return {};
  },
  render(h) {
    var o = this.o;
    var name = o.graph || o.grid || o.card || o.query;
    if (!name) return h("div", null, "not portlet");
    if (o.query) {
      var q = o.query.data;
      if (!q || !q.length) return h("div", null, "not data");
      q = q[0];
      return h(
        "card",
        {
          class:
            "card-portlet bg-white text-" +
            (o.props.color || this.o.props.color || "success")
        },
        [
          //					h("i", {class: "big-icon "+(q.icon || "icon-settings")}),
          h("card-block", { class: "pb-0" }, [
            h("div", { class: "kpi-portlet-val float-right" }, q.xvalue),
            h("h1", { class: "mb-0", style: "font-size:40px" }, q.dsc),
            h("hr", {
              style:
                "border-color: rgb(238, 238, 238);width: 70%;position: absolute;top: 50px;"
            }),
            h(
              "div",
              { style: "color: #aaa;font-size: .9rem;margin-top:15px" },
              o.props.longDsc || "updated a minute ago"
            )
          ])
        ]
      );
    }
    name = name.name;
    var cmp = null;
    if (o.graph) {
      return h(
        "card",
        {
          class: "card-portlet " + (o.props.color ? "bg-" + o.props.color : "")
        },
        [
          h(
            "h3",
            {
              class: "form-header",
              style: "padding: 2px 2px 0px;margin-bottom:0px"
            },
            name
          ),
          h("div", { style: "position: absolute;top: 20px;right: 20px;" }, [
            h("i", { class: "now-ui-icons loader_refresh" })
          ]),
          o.props.longDsc && h("p", { style: "color:gray" }, o.props.longDsc),
          h(XGraph, { props: { o: o } })
        ]
      );
    } else if (o.grid) {
      o.grid.crudFlags = false;
      return h(
        "card",
        {
          class: "card-portlet " + (o.props.color ? "bg-" + o.props.color : ""),
          style: o.props.height ? "height:" + o.props.height : ""
        },
        [
          h(
            "h3",
            {
              class: "form-header",
              style: "padding: 2px 2px 0px;;margin-bottom:0px "
            },
            name
          ),
          h("div", { style: "position: absolute;top: 20px;right: 20px;" }, [
            h("i", { class: "now-ui-icons loader_refresh" })
          ]),
          o.props.longDsc && h("p", { style: "color:gray" }, o.props.longDsc),
          h(XGrid, { props: o })
        ]
      );
    } else if (o.card) {
      return h(
        "card",
        {
          class: "card-portlet " + (o.props.color ? "bg-" + o.props.color : ""),
          style: o.props.height ? "height:" + o.props.height : ""
        },
        [
          h(
            "h3",
            {
              class: "form-header",
              style: "padding: 2px 2px 0px;;margin-bottom:0px "
            },
            name
          ),
          h("div", { style: "position: absolute;top: 20px;right: 20px;" }, [
            h("i", { class: "now-ui-icons loader_refresh" })
          ]),
          o.props.longDsc && h("p", { style: "color:gray" }, o.props.longDsc),
          h(XCard, { props: o })
        ]
      );
    } else return h("div", null, "not recognized portlet");
  }
});

function props2css(x) {
  if (!x) return "";
  var s = "";
  for (var k in x)
    switch (k) {
      case "xs":
      case "sm":
      case "md":
      case "lg":
      case "xl":
        s += " col-" + k + "-" + x[k];
    }
  return s;
}
iwb.ui.buildDashboard = function(o) {
  return {
    data() {
      return {};
    },
    render(h) {
      if (!o || !o.rows || !o.rows.length)
        return h("div", { class: "container-fluid", style: "" }, [
          h("div", { class: "row" }, [
            h("div", { class: "col-12" }, [h("div", {}, "No portlets defined")])
          ])
        ]);

      return h(
        "div",
        { class: "container-fluid", style: "" },
        o.rows.map(rowItem => {
          return h(
            "div",
            { class: "row" },
            rowItem.map(colItem =>
              h("div", { class: props2css(colItem.props) }, [
                h(XPortlet, { props: { o: colItem } })
              ])
            )
          ); //iwb.createPortlet(colItem)
        })
      );
    }
  };
};

var XCard = Vue.component("x-card", {
  props: ["card", "showForm"],
  data() {
    var g = this.card;
    return {
      loading: true,
      rows: [],
      totalCount: 0,
      lastQuery: "",
      currentPage: 0,
      pageSize: g.pageSize
    };
  },
  methods: {
    onNewRecord(e, card, row) {
      var g = this.card;
      if (g.crudFlags && g.crudFlags.insert && this.showForm) {
        var url = "showForm?a=2&_fid=" + g.crudFormId;
        if (g._postInsert) {
          url = g._postInsert(row || {}, url, card);
          if (!url) return;
        }
        this.showForm(url, this.loadData);
      }
    },
    dblClick(row) {
      //			this.$message({type: 'success',message: 'Your email is:'});
      var g = this.card;
      if (g.crudFlags && g.crudFlags.edit && this.showForm) {
        var pkz = buildParams2(this.card.pk, row);
        var url = "showForm?a=1&_fid=" + this.card.crudFormId + pkz;
        if (this.card._postUpdate) {
          var url = this.card._postUpdate(row, url, this.card);
          if (!url) return;
        }
        this.showForm(url, this.loadData);
      }
    },
    queryString() {
      let queryString = this.card._url;
      if (this.pageSize)
        queryString +=
          "&limit=" +
          this.pageSize +
          "&start=" +
          this.pageSize * this.currentPage;
      return queryString;
    },
    loadData(force, params) {
      const queryString = this.queryString();
      //		    if (!force && queryString === this.lastQuery) {return;}
      var params = Object.assign(
        {},
        params || {},
        this.form ? this.form.getValues() : {}
      );
      //var ll = ELEMENT.Loading.service();
      this.loading = true;
      iwb.request({
        url: queryString,
        params: params,
        successCallback: (result, cfg) => {
          this.rows = result.data;
          this.totalCount = result.total_count;
          this.loading = false;
        },
        errorCallback: (error, cfg) => {
          this.rows = [];
          this.totalCount = 0;
          this.loading = false;
        }
      });
      this.lastQuery = queryString;
    },
    currentPageChange(page) {
      this.currentPage = page - 1;
      this.loadData(!0);
    }
  },
  mounted() {
    var cmp = "x-icb-card-" + this.card.cardId;
    Vue.component(cmp, {
      props: ["row"],
      template: this.card.tpl
    });
    this.card._url && this.loadData(!0);
  },
  render(h) {
    var rows = this.rows;
    var g = this.card;
    return h("div", {}, [
      ,
      h("hr", { style: "margin-bottom:0" }),
      h(
        "el-row",
        { style: "padding: 5px" },
        g._url
          ? rows.length &&
            rows.map(o => {
              return h("x-icb-card-" + g.cardId, { props: { row: o } });
            })
          : [h("x-icb-card-" + g.cardId, {})]
      ),
      g._url &&
        g.pageSize &&
        rows.length > g.pageSize &&
        h("el-row", { style: "padding-top: 10px" }, [
          h("el-pagination", {
            class: "float-right",
            on: { "current-change": this.currentPageChange },
            props: {
              background: !0,
              currentPage: this.currentPage + 1,
              pageSize: g.pageSize,
              layout: "total, prev, pager, next",
              total: this.totalCount
            }
          })
        ])
    ]);
  }
});

var XMainCard = Vue.component("x-main-card", {
  props: ["card", "showForm"],
  data() {
    var g = this.card;
    return {
      loading: true,
      rows: [],
      totalCount: 0,
      lastQuery: "",
      currentPage: 0,
      pageSize: g.pageSize
    };
  },
  methods: {
    onNewRecord(e, card, row) {
      var g = this.card;
      if (g.crudFlags && g.crudFlags.insert && this.showForm) {
        var url = "showForm?a=2&_fid=" + g.crudFormId;
        if (g._postInsert) {
          url = g._postInsert(row || {}, url, card);
          if (!url) return;
        }
        this.showForm(url, this.loadData);
      }
    },
    dblClick(row) {
      //			this.$message({type: 'success',message: 'Your email is:'});
      var g = this.card;
      if (g.crudFlags && g.crudFlags.edit && this.showForm) {
        var pkz = buildParams2(this.card.pk, row);
        var url = "showForm?a=1&_fid=" + this.card.crudFormId + pkz;
        if (this.card._postUpdate) {
          var url = this.card._postUpdate(row, url, this.card);
          if (!url) return;
        }
        this.showForm(url, this.loadData);
      }
    },
    queryString() {
      let queryString = this.card._url;
      if (this.pageSize)
        queryString +=
          "&limit=" +
          this.pageSize +
          "&start=" +
          this.pageSize * this.currentPage;
      return queryString;
    },
    loadData(force, params) {
      const queryString = this.queryString();
      //		    if (!force && queryString === this.lastQuery) {return;}
      var params = Object.assign(
        {},
        params || {},
        this.form ? this.form.getValues() : {}
      );
      //var ll = ELEMENT.Loading.service();
      this.loading = true;
      iwb.request({
        url: queryString,
        params: params,
        successCallback: (result, cfg) => {
          this.rows = result.data;
          this.totalCount = result.total_count;
          this.loading = false;
        },
        errorCallback: (error, cfg) => {
          this.rows = [];
          this.totalCount = 0;
          this.loading = false;
        }
      });
      this.lastQuery = queryString;
    },
    currentPageChange(page) {
      this.currentPage = page - 1;
      this.loadData(!0);
    }
  },
  mounted() {
    var cmp = "x-icb-card-" + this.card.cardId;
    console.log(cmp);
    Vue.component(cmp, {
      props: ["row"],
      template: this.card.tpl
    });
    this.loadData(!0);
  },
  render(h) {
    var rows = this.rows;
    var g = this.card;
    return h("card", {}, [
      h("row", { style: "padding: 5px" }, [
        h("el-button", {
          props: { icon: "el-icon-refresh", circle: !0 },
          on: { click: this.loadData }
        }),
        g.crudFlags &&
          g.crudFlags.insert &&
          h(
            "el-button",
            {
              class: "float-right",
              props: { type: "danger", icon: "el-icon-plus", round: !0 },
              on: { click: this.onNewRecord }
            },
            "NEW RECORD"
          )
      ]),
      h("hr", { style: "margin-bottom:0" }),
      rows.length &&
        rows.map(o => {
          return h("x-icb-card-" + g.cardId, { props: { row: o } });
        }),

      g.pageSize &&
        rows.length > g.pageSize &&
        h("row", { style: "padding-top: 10px" }, [
          h("el-pagination", {
            class: "float-right",
            on: { "current-change": this.currentPageChange },
            props: {
              background: !0,
              currentPage: this.currentPage + 1,
              pageSize: g.pageSize,
              layout: "total, prev, pager, next",
              total: this.totalCount
            }
          })
        ])
    ]);
  }
});

iwb.ajax={}
iwb.ajax.query=function(qid,params,callback){
	iwb.request({url:'ajaxQueryData?_qid='+qid,params:params||{},successCallback:callback||false})
}
iwb.ajax.postForm=function(fid,action,params,callback){
	iwb.request({url:'ajaxPostForm?_fid='+fid+'&a='+action,params:params||{},successCallback:callback||false})
}
iwb.ajax.execFunc=function(did,params,callback){
	iwb.request({url:'ajaxExecDbFunc?_did='+did,params:params||{},successCallback:callback||false})
}