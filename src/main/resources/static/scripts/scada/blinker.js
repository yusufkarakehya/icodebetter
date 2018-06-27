var scada = scada || {};

scada.Blinker = function(domEl, colors, duration) {
    this.domEl = domEl;
    this.colors = colors;
    this.duration = duration;
    this.isRunning = false;
    this.index = 0;
};

scada.Blinker.prototype = function() {
    var start = function() {
            var self = this;
            this.isRunning = true;
            (function blink(index) {
            	  var fillColor = self.colors[index], strokeColor = self.colors[index];
            	  
                  if (self.domEl.style.fill == "none") {
               	     fillColor = "none";
                  }
                  
                  if (self.domEl.style.stroke == "none") {
               	     strokeColor = "none";
                  }
            	
            	Velocity(self.domEl, { fill: fillColor, stroke: strokeColor }, self.duration, function() {
                    if (isRunning.call(self)) {
                        if (self.index == self.colors.length) {
                            self.index = 0;
                        }
                        blink.call(self, self.index);
                        self.index++;
                    } else {
                        if (self.exitCb && typeof(self.exitCb) === 'function') {
                            self.exitCb();
                        }
                    }
                });
            })(self.index);
        },
        stop = function(cb) {
            this.exitCb = cb;
            this.isRunning = false;
        },
        isRunning = function() {
            return this.isRunning;
        },
        changeColors = function(colors) {
            var self = this;
            this.exitCb = function() {
                self.index = 0;
                self.colors = colors;
                self.start();
            };
            this.isRunning = false;
        };

    return {
        start: start,
        stop: stop,
        changeColors: changeColors
    };
}();