function hasElement (className) {
    return document.getElementsByClassName(className).length > 0;
}

function initScrollbar (className) {
    if (hasElement(className)) {
      new PerfectScrollbar(`.${className}`);
    } else {
      // try to init it later in case this component is loaded async
      setTimeout(() => {
        initScrollbar(className);
      }, 100);
    }
}

var DashboardLayout =  {
	template:`<div class="wrapper" :class="{'nav-open': $sidebar.showSidebar}">
<notifications></notifications>
<side-bar>
  <template slot-scope="props" slot="links">
  <xside-bar-menu></xside-bar-menu>
  </template>
</side-bar>
<div class="main-panel">
  <top-navbar></top-navbar>
  <div class="panel-header panel-header-sm"></div>

  <div :class="{content: !$route.meta.hideContent}" @click="toggleSidebar">
    <zoom-center-transition :duration="200" mode="out-in">
      <router-view :key="$route.fullPath"></router-view>
    </zoom-center-transition>
  </div>
  <modal></modal>
</div>
</div>`
	,methods: {
      toggleSidebar () {
        if (this.$sidebar.showSidebar) {
          this.$sidebar.displaySidebar(false)
        }
      }
    },
    mounted () {
		iwb.$notify = this.$notify;
		let docClasses = document.body.classList;
		let isWindows = navigator.platform.startsWith('Win');
		if (isWindows) {
			initScrollbar('sidebar');
			initScrollbar('sidebar-wrapper');
			docClasses.add('perfect-scrollbar-on')
		} else {
			docClasses.add('perfect-scrollbar-off')
		}
    }
};