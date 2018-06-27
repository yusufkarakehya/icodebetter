var scada = scada || {};

scada.IFrameWindow = function(container, src, width, height, x, y) {
    var self = this;
    this.container = container;
    this.src = src;
    this.width = width || 500;
    this.height = height || 400;
    this.x = x || 100;
    this.y = y || 100;
    this.window = new Ext.Window({
        bodyCfg: {
            tag: 'iframe',
            src: this.src,
            style: {
                border: '0px none'
            }
        },
        width: this.width,
        height: this.height,
        x: this.x,
        y: this.y,
        title: this.src,
        constrain: true,
        renderTo: this.container,
        tools: [{
            id: 'restore',
            qtip: '${restore}',
            handler: function() {
                self.restore();
            }
        }],
        minimizable: true,
        listeners: {
            "collapse": function() {
                self.alignTopRight();
            },
            "minimize": function() {
                self.minimize();
            }
        }
    });
};

scada.IFrameWindow.prototype = function() {
    var show = function() {
            this.window.show();
        },
        close = function() {
            this.window.close();
        },
        restore = function() {
            if (this.window.collapsed) {
                this.window.setSize(this.width, this.height);
                this.window.expand();
                this.window.setPosition([this.x, this.y]);
            }
        },
        alignTopRight = function() {
            this.window.alignTo(this.container, 'tr-tr', [-10, 30]);
        },
        defaultMinWidth = 200,
        defaultMinHeight = 20,
        minimize = function() {
            if (this.window.collapsed) {
                return;
            }

            this.x = this.window.x;
            this.y = this.window.y;
            this.width = this.window.width;
            this.height = this.window.height;
            this.window.collapse();
            this.window.setSize(defaultMinWidth, defaultMinHeight);
        },
        isDestroyed = function() {
            return this.window.isDestroyed;
        };

   return {
       show: show,
       close: close,
       restore: restore,
       alignTopRight: alignTopRight,
       minimize: minimize,
       isDestroyed: isDestroyed
   };
}();