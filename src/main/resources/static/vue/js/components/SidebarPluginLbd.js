var SidebarItem = Vue.component("sidebar-item", {
  template: `<component :is="baseComponent"
        :to="link.path ? link.path : '/'"
            :class="{active : isActive}"
            tag="li">
   <a v-if="isMenu"
      href="#"
      class="nav-link sidebar-menu-item"
      :aria-expanded="!collapsed"
      data-toggle="collapse"
      @click.prevent="collapseMenu">
     <i :class="link.icon"></i>
     <p>
       {{link.name}}
       <b class="caret"></b>
     </p>

   </a>

   <collapse-transition>
     <div v-if="$slots.default || this.isMenu" v-show="!collapsed">
       <ul class="nav" >
         <slot></slot>
       </ul>
     </div>
   </collapse-transition>

   <slot name="title" v-if="children.length === 0 && !$slots.default && link.path">
     <component
       :to="link.path"
       @click.native="linkClick"
       :is="elementType(link, false)"
       :class="{active: link.active}"
       class="nav-link"
       :target="link.target"
       :href="link.path">
       <template v-if="addLink">
         <span class="sidebar-mini-icon">{{linkPrefix}}</span>
         <span class="sidebar-normal">{{link.name}}</span>
       </template>
       <template v-else>
         <i :class="link.icon"></i>
         <p>{{link.name}}</p>
       </template>
     </component>
   </slot>
 </component>`,
  props: {
    menu: {
      type: Boolean,
      default: false
    },
    link: {
      type: Object,
      default: () => {
        return {
          name: "",
          path: "",
          children: []
        };
      }
    }
  },
  provide() {
    return {
      addLink: this.addChild,
      removeLink: this.removeChild
    };
  },
  inject: {
    addLink: { default: null },
    removeLink: { default: null },
    autoClose: {
      default: true
    }
  },
  data() {
    return {
      children: [],
      collapsed: true
    };
  },
  computed: {
    baseComponent() {
      return this.isMenu || this.link.isRoute ? "li" : "router-link";
    },
    linkPrefix() {
      if (this.link.name) {
        let words = this.link.name.split(" ");
        return words.map(word => word.substring(0, 1)).join("");
      }
    },
    isMenu() {
      return this.children.length > 0 || this.menu === true;
    },
    isActive() {
      if (this.$route && this.$route.path) {
        let matchingRoute = this.children.find(c =>
          this.$route.path.startsWith(c.link.path)
        );
        if (matchingRoute !== undefined) {
          return true;
        }
      }
      return false;
    }
  },
  methods: {
    addChild(item) {
      const index = this.$slots.default.indexOf(item.$vnode);
      this.children.splice(index, 0, item);
    },
    removeChild(item) {
      const tabs = this.children;
      const index = tabs.indexOf(item);
      tabs.splice(index, 1);
    },
    elementType(link, isParent = true) {
      if (link.isRoute === false) {
        return isParent ? "li" : "a";
      } else {
        return "router-link";
      }
    },
    linkAbbreviation(name) {
      const matches = name.match(/\b(\w)/g);
      return matches.join("");
    },
    linkClick() {
      if (
        this.autoClose &&
        this.$sidebar &&
        this.$sidebar.showSidebar === true
      ) {
        this.$sidebar.displaySidebar(false);
      }
    },
    collapseMenu() {
      this.collapsed = !this.collapsed;
    },
    collapseSubMenu(link) {
      link.collapsed = !link.collapsed;
    }
  },
  mounted() {
    if (this.addLink) {
      this.addLink(this);
    }
    if (this.link.collapsed !== undefined) {
      this.collapsed = this.link.collapsed;
    }
    if (this.isActive && this.isMenu) {
      this.collapsed = false;
    }
  },
  destroyed() {
    if (this.$el && this.$el.parentNode) {
      this.$el.parentNode.removeChild(this.$el);
    }
    if (this.removeLink) {
      this.removeLink(this);
    }
  }
});

var SideBar = Vue.component("side-bar", {
  template:
    `<div class="sidebar"
       :data-color="backgroundColor">

    <div class="logo">
      <a href="http://www.icodebetter.com" class="simple-text logo-mini">
        <div class="logo-image">
          ` +
    iwb.logo +
    `
        </div>
      </a>

      <a href="http://www.icodebetter.com" class="simple-text logo-normal">
        {{title}}
      </a>
      <div class="navbar-minimize">
        <button id="minimizeSidebar" class="btn btn-simple btn-icon btn-neutral btn-round" style="border-color:rgba(255,255,255,.2);" @click="minimizeSidebar">
          <i class="now-ui-icons text_align-center visible-on-sidebar-regular"></i>
          <i class="now-ui-icons design_bullet-list-67 visible-on-sidebar-mini"></i>
        </button>
      </div>
    </div>
    <div class="sidebar-wrapper" ref="sidebarScrollArea">
    
	    <div class="user"><div class="photo"><img src="sf/pic` +
    _scd.userId +
    `.png" alt="avatar"></div><div class="info"><a data-toggle="collapse" href="#"><span>
	    ` +
    _scd.completeName +
    `
	    </span></a><div class="clearfix"></div></div></div>
    
      <slot></slot>
      <ul class="nav">
      
      
        <slot name="links">
          <sidebar-item v-for="(link, index) in sidebarLinks"
                        :key="link.name + index"
                        :link="link">

            <sidebar-item v-for="(subLink, index) in link.children"
                          :key="subLink.name + index"
                          :link="subLink">
            </sidebar-item>
          </sidebar-item>
        </slot>
      </ul>
    </div>
    <div class="menu-bg-image"></div>
  </div>`,
  props: {
    title: {
      type: String,
      default: _scd.projectName || "iCodeBetter"
    },
    backgroundColor: {
      type: String,
      default: "black",
      validator: value => {
        let acceptedValues = [
          "",
          "blue",
          "azure",
          "green",
          "orange",
          "red",
          "purple",
          "black"
        ];
        return acceptedValues.indexOf(value) !== -1;
      }
    },
    //        logo: {type: String,default: iwb.logo},
    sidebarLinks: {
      type: Array,
      default: () => []
    },
    autoClose: {
      type: Boolean,
      default: true
    }
  },
  provide() {
    return {
      autoClose: this.autoClose
    };
  },
  methods: {
    minimizeSidebar() {
      if (this.$sidebar) {
        this.$sidebar.toggleMinimize();
      }
    }
  },
  beforeDestroy() {
    if (this.$sidebar.showSidebar) {
      this.$sidebar.showSidebar = false;
    }
  }
});

const SidebarStore = {
  showSidebar: false,
  sidebarLinks: [],
  isMinimized: true,
  displaySidebar(value) {
    this.showSidebar = value;
  },
  toggleMinimize() {
    document.body.classList.toggle("sidebar-mini");
    // we simulate the window Resize so the charts will get updated in realtime.
    const simulateWindowResize = setInterval(() => {
      window.dispatchEvent(new Event("resize"));
    }, 180);

    // we stop the simulation of Window Resize after the animations are completed
    setTimeout(() => {
      clearInterval(simulateWindowResize);
    }, 1000);

    this.isMinimized = !this.isMinimized;
  }
};

const SidebarPlugin = {
  install(Vue, options) {
    if (options && options.sidebarLinks) {
      SidebarStore.sidebarLinks = options.sidebarLinks;
    }
    Vue.mixin({
      data() {
        return {
          sidebarStore: SidebarStore
        };
      }
    });

    Object.defineProperty(Vue.prototype, "$sidebar", {
      get() {
        return this.$root.sidebarStore;
      }
    });
    Vue.component("side-bar", SideBar);
    Vue.component("sidebar-item", SidebarItem);
  }
};
