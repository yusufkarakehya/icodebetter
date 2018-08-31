var TopNavbar = Vue.component("top-navbar", {
  template:
    `<header class="header-global" style="height: 62px;background: linear-gradient(90deg, #7795f8, #626ddc);">
        <base-nav class="navbar-main" transparent type="" effect="light" expand>
        <a slot="brand" class="navbar-brand mr-lg-5" target=_blank href="http://www.icodebetter.com">
        <img src="/images/rabbit-head-w.png" style="vertical-align:middle;margin-top:-7px"> ` +
    _scd.projectName +
    `
    </a>

    <div class="row" slot="content-header" slot-scope="{closeMenu}">
        <div class="col-6 collapse-brand">
            <a href="http://www.icodebetter.com">
                <img src="/images/rabbit-head-w.png" target=_blank style="vertical-align:middle;margin-top:-7px"> ` +
    _scd.projectName +
    `
            </a>
        </div>
        <div class="col-6 collapse-close">
            <close-button @click="closeMenu"></close-button>
        </div>
    </div>
            <ul class="navbar-nav navbar-nav-hover align-items-lg-center">` +
    sbm +
    `</ul>
        </base-nav>
    </header>`,
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
