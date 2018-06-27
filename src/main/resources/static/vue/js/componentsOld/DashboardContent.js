var DashboardContent= Vue.component('dashboard-content', {
	template:'   <transition name="fade" mode="out-in">    <router-view></router-view>  </transition>'
});