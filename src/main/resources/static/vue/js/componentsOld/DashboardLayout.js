var DashboardLayout= Vue.component('dashboard-layout', {
	template:'  <div class="wrapper">    <side-bar>      <mobile-menu slot="content"></mobile-menu>      <sidebar-link to="/admin/overview">        <i class="nc-icon nc-chart-pie-35"></i>        <p>Dashboard</p>      </sidebar-link>      <sidebar-link  to="/admin/showPage?_tid=1">        <i class="nc-icon nc-circle-09"></i>        <p>User Profile</p>      </sidebar-link>      <sidebar-link to="/admin/showPage?_tid=2">        <i class="nc-icon nc-notes"></i>        <p>Table list</p>      </sidebar-link>      <sidebar-link to="/admin/showPage?_tid=3">        <i class="nc-icon nc-paper-2"></i>        <p>Typography</p>      </sidebar-link>      <sidebar-link to="/admin/icons">        <i class="nc-icon nc-atom"></i>        <p>Icons</p>      </sidebar-link>      <sidebar-link to="/admin/maps">        <i class="nc-icon nc-pin-3"></i>        <p>Maps</p>      </sidebar-link>      <sidebar-link to="/admin/notifications">        <i class="nc-icon nc-bell-55"></i>        <p>Notifications</p>      </sidebar-link>    </side-bar>    <div class="main-panel">      <top-navbar></top-navbar>      <dashboard-content @click="toggleSidebar">      </dashboard-content>    </div>  </div>'
//	,components: {      TopNavbar,      DashboardContent,      MobileMenu    },
    ,methods: {
      toggleSidebar () {
    	  alert("1")
        if (this.$sidebar.showSidebar) {
          this.$sidebar.displaySidebar(false)
        }
      }
    }
  });