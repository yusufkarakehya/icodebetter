var BreadCrumb = Vue.component("breadcrumb", {
  template: `<nav aria-label="breadcrumb" role="navigation">
    <ul class="breadcrumb">
      <slot>

      </slot>
    </ul>
  </nav>`
});
var BreadCrumbItem = Vue.component("breadcrumb-item", {
  template: `<li class="breadcrumb-item" :class="{active: active}">
    <slot></slot>
  </li>`,
  props: {
    active: {
      type: Boolean,
      default: false
    }
  }
});

var RouteBreadCrumb = Vue.component("route-breadcrumb", {
  template: `<bread-crumb>
    <BreadCrumbItem style="display:inline-block"><router-link to="/"  style="background:#353535;color:white !important;border:none;" class="breadcrumb-link el-button el-button--home is-circle">
      <i class="fa fa-home"></i></router-link></BreadCrumbItem> 
    <BreadCrumbItem v-for="(route, index) in $route.matched.slice()" :key="route.name" style="display:inline-block">
      <router-link :to="{name: route.name}" v-if="index < $route.matched.length - 1" class="breadcrumb-link">
        {{ route.name }}
      </router-link>
      <span v-else class="breadcrumb-current">{{route.name}}</span>
    </BreadCrumbItem>
  </bread-crumb>`,
  components: {
    BreadCrumb,
    BreadCrumbItem
  },
  props: { root: String },
  methods: {
    getBreadName(route) {
      return route.name;
    }
  }
});
