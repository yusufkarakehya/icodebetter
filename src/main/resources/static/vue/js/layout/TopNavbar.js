var TopNavbar = Vue.component("top-navbar", {
  template:
    `<navbar :show-navbar="showNavbar">
    <div class="navbar-wrapper">
      <div class="navbar-toggle" :class="{toggled: $sidebar.showSidebar}">
        <navbar-toggle-button @click.native="toggleSidebar">
        </navbar-toggle-button>
      </div>
      <span class="navbar-brand" style="margin-left:15px; text-transform: none;padding: 0px 20px 0px 0px; border-right: 1px solid rgb(194, 199, 212); font-size: 1.5rem" href="#">
        {{$route.name}}
      </span>
      <route-breadcrumb> </route-breadcrumb>
    </div>
    <button @click="toggleNavbar" class="navbar-toggler" type="button" data-toggle="collapse"
            data-target="#navigation"
            aria-controls="navigation-index" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-bar navbar-kebab"></span>
      <span class="navbar-toggler-bar navbar-kebab"></span>
      <span class="navbar-toggler-bar navbar-kebab"></span>
    </button>
 
    <template slot="navbar-menu">` +
    (false
      ? `<form>
        <div class="input-group no-border">
          <fg-input placeholder="Quick Search..." addon-right-icon="now-ui-icons ui-1_zoom-bold">
          </fg-input>
        </div>
      </form>

      <ul class="navbar-nav">
        <li class="nav-item">
          <a class="nav-link" href="#">
            <i class="now-ui-icons ui-2_chat-round"></i>
            <p>
              <span class="d-lg-none d-md-block">Stats</span>
            </p>
          </a>
        </li>
            
        <drop-down tag="li" 
                   position="right"
                   class="nav-item"
                   icon="now-ui-icons tech_tablet">` +
        iwb.roleMenu +
        `
        </drop-down>


      </ul>`
      : "") +
    `</template>
  </navbar>`,
  computed: {
    routeName() {
      const { name } = this.$route;
      return this.capitalizeFirstLetter(name);
    }
  },
  data() {
    return {
      activeNotifications: false,
      showNavbar: false
    };
  },

  methods: {
    capitalizeFirstLetter(string) {
      return string.charAt(0).toUpperCase() + string.slice(1);
    },
    toggleNotificationDropDown() {
      this.activeNotifications = !this.activeNotifications;
    },
    closeDropDown() {
      this.activeNotifications = false;
    },
    toggleSidebar() {
      this.$sidebar.displaySidebar(!this.$sidebar.showSidebar);
    },
    toggleNavbar() {
      this.showNavbar = !this.showNavbar;
    },
    hideSidebar() {
      this.$sidebar.displaySidebar(false);
    },
    changeRole(userRoleId) {
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
  }
});
