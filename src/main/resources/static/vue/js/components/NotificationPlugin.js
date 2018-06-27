var Notification =  Vue.component('notification', {
	template:'<div @click="tryClose" data-notify="container" class="alert open" :class="[{\'alert-with-icon\': icon}, verticalAlign, horizontalAlign, alertType]" role="alert" :style="customPosition" data-notify-position="top-center">  <button    v-if="showClose"    type="button"    aria-hidden="true"    class="close col-xs-1"    data-notify="dismiss"    @click="close">    <i class="now-ui-icons ui-1_simple-remove"></i>  </button>  <span v-if="icon" data-notify="icon" :class="[\'alert-icon\', icon]"></span>  <span data-notify="message"><span v-if="title" class="title"><b>{{title}}<br/></b></span><span v-if="message" v-html="message"></span><content-render v-if="!message && component" :component="component"></content-render></span></div>'
	,components: {
	      contentRender: {
	          props: ['component'],
	          render: h => h(this.component)
	        }
	      },
      props: {
        message: String,
        title: String,
        icon: String,
        verticalAlign: {
          type: String,
          default: 'top',
          validator: (value) => {
            let acceptedValues = ['top', 'bottom']
            return acceptedValues.indexOf(value) !== -1
          }
        },
        horizontalAlign: {
          type: String,
          default: 'right',
          validator: (value) => {
            let acceptedValues = ['left', 'center', 'right']
            return acceptedValues.indexOf(value) !== -1
          }
        },
        type: {
          type: String,
          default: 'info',
          validator: (value) => {
            let acceptedValues = ['info', 'primary', 'danger', 'warning', 'success']
            return acceptedValues.indexOf(value) !== -1
          }
        },
        timeout: {
          type: Number,
          default: 5000,
          validator: (value) => {
            return value >= 0
          }
        },
        timestamp: {
          type: Date,
          default: () => new Date()
        },
        component: {
          type: [Object, Function]
        },
        showClose: {
          type: Boolean,
          default: true
        },
        closeOnClick: {
          type: Boolean,
          default: true
        },
        clickHandler: Function,
      },
      data() {
        return {
          elmHeight: 0
        }
      },
      computed: {
        hasIcon() {
          return this.icon && this.icon.length > 0
        },
        alertType() {
          return `alert-${this.type}`
        },
        customPosition() {
          let initialMargin = 20
          let alertHeight = this.elmHeight + 10;
          let sameAlertsCount = this.$notifications.state.filter((alert) => {
            return alert.horizontalAlign === this.horizontalAlign && alert.verticalAlign === this.verticalAlign && alert.timestamp <= this.timestamp
          }).length
          if (this.$notifications.settings.overlap) {
            sameAlertsCount = 1
          }
          let pixels = (sameAlertsCount - 1) * alertHeight + initialMargin
          let styles = {}
          if (this.verticalAlign === 'top') {
            styles.top = `${pixels}px`
          } else {
            styles.bottom = `${pixels}px`
          }
          return styles
        }
      },
      methods: {
        close() {
          this.$emit('close', this.timestamp)
        },
        tryClose(evt) {
          if (this.clickHandler) {
            this.clickHandler(evt, this)
          }
          if (this.closeOnClick) {
            this.close()
          }
        }
      },
      mounted() {
        this.elmHeight = this.$el.clientHeight
        if (this.timeout) {
          setTimeout(this.close, this.timeout)
        }
      }
});

var Notifications =  Vue.component('notifications', {
	template:'<div class="notifications"><transition-group :name="transitionName" :mode="transitionMode"><notification v-for="notification in notifications" v-bind="notification" :clickHandler="notification.onClick" :key="notification.timestamp.getTime()"  @close="removeNotification"></notification></transition-group></div>'
		,props: {
		      transitionName: {
		          type: String,
		          default: 'list'
		        },
		        transitionMode: {
		          type: String,
		          default: 'in-out'
		        },
		        overlap: {
		          type: Boolean,
		          default: false
		        }
	      },
	      data() {
	        return {
	          notifications: this.$notifications.state
	        }
	      },
	      methods: {
	        removeNotification(timestamp) {
	          this.$notifications.removeNotification(timestamp)
	        }
	      },
	      created() {
	        this.$notifications.settings.overlap = this.overlap
	      },
	      watch: {
	        overlap: function (newVal) {
	          this.$notifications.settings.overlap = newVal
	        }
	      }
});		
		
const NotificationStore = {
  state: [], // here the notifications will be added
  settings: {
    overlap: false,
    verticalAlign: 'top',
    horizontalAlign: 'right',
    type: 'info',
    timeout: 5000,
    closeOnClick: true,
    showClose: true
  },
  setOptions(options) {
    this.settings = Object.assign(this.settings, options)
  },
  removeNotification(timestamp) {
    const indexToDelete = this.state.findIndex(n => n.timestamp === timestamp)
    if (indexToDelete !== -1) {
      this.state.splice(indexToDelete, 1)
    }
  },
  addNotification(notification) {
    if (typeof notification === 'string' || notification instanceof String) {
      notification = {message: notification}
    }
    notification.timestamp = new Date()
    notification.timestamp.setMilliseconds(notification.timestamp.getMilliseconds() + this.state.length)
    notification = Object.assign({}, this.settings, notification)
    this.state.push(notification)
  },
  notify(notification) {
    if (Array.isArray(notification)) {
      notification.forEach((notificationInstance) => {
        this.addNotification(notificationInstance)
      })
    } else {
      this.addNotification(notification)
    }

  }
}

const NotificationPlugin = {
  install(Vue, options) {
    Vue.mixin({
      data() {
        return {
          notificationStore: NotificationStore
        }
      },
      methods: {
        notify(notification) {
          this.notificationStore.notify(notification);
        }
      }
    })
    Object.defineProperty(Vue.prototype, '$notify', {
      get() {
        return this.$root.notify
      }
    })
    Object.defineProperty(Vue.prototype, '$notifications', {
      get() {
        return this.$root.notificationStore
      }
    })
    Vue.component('Notifications', Notifications)
    if (options) {
      NotificationStore.setOptions(options)
    }
  }
}

//export default NotificationsPlugin
