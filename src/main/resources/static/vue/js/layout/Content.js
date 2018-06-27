var DashboardContent =  Vue.component('dashboard-content', {
	template:`<div class="content">
    <FadeTransition :duration="200" mode="out-in">
      <!-- your content here -->
      <router-view></router-view>
    </FadeTransition>
  </div>`
  });