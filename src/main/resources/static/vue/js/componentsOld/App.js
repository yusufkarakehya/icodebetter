var App =  Vue.component('app', {
	template:'<div :class="{\'nav-open\': $sidebar.showSidebar}">    <notifications></notifications>    <router-view></router-view>  </div>'
});
