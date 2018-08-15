function findRouteName(url) {
  return routes[url];
}

const getPaths = pathname => {
  const paths = ["/"];

  if (pathname === "/") return paths;

  pathname.split("/").reduce((prev, curr, index) => {
    const currPath = prev + "/" + curr;
    paths.push(currPath);
    return currPath;
  });
  return paths;
};

const BreadcrumbsItem = ({ match }) => {
  var routeName = findRouteName(match.url);
  if (routeName) {
    //	  if(routeName=='Home')routeName=_('i',{className:'icon-home'});
    if (routeName == "Home")
      routeName = _(
        Button,
        {
          className: "btn-round-shadow",
          color: "home",
          style: { marginTop: "-8px" }
        },
        _("i", { className: "icon-home" })
      );
    return match.isExact
      ? _(BreadcrumbItem, { active: true }, routeName)
      : _(BreadcrumbItem, null, _(Link, { to: match.url || "" }, routeName));
  }
  return null;
};

const Breadcrumbs = ({ location: { pathname }, match }) => {
  const paths = getPaths(pathname);
  const items = paths.map((path, i) =>
    _(Route, { key: i++, path: path, component: BreadcrumbsItem })
  );
  return _(
    Breadcrumb,
    null,
    _("h3", { id: "id-breed" }, _scd.projectName || "iCodeBetter"),
    _("span", { style: { width: "20px" } }, " "),
    items
  );
};

const Breadcrumb2 = props => {
  return _(
    "div",
    { className: "d-md-down-none" },
    _(Route, Object.assign({ path: "/:path", component: Breadcrumbs }, props))
  );
};
/*
export default props => (
  <div>
    <Route path="/:path" component={Breadcrumbs} {...props} />
  </div>
);
*/
