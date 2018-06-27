/*
 * Ext Gantt 1.1
 * Copyright(c) 2009-2010 Mats Bryntse Consulting
 * mats@ext-scheduler.com
 * http://www.ext-scheduler.com/license.html
 *
 */
 
Ext.ns('Sch');

/**
 * @class Sch.DependencyPainter
 * @extends Ext.util.Observable
 * <p>Internal class handling the drawing of inter-task dependencies.</p>
 */
Sch.DependencyPainter = Ext.extend(Ext.util.Observable, {
    
    // private, true to check for visibility (needed when using grouping view, since we should only draw lines between two visible elements)
    checkVisible : false,
    
    constructor : function (g, cfg) {    
        cfg = cfg || {};
        
        this.grid = g;
        
        Ext.apply(this, cfg, {
            xOffset : 8,
            midRowOffset : 4,
            yOffset : 7,
            horizontalArrowWidthOffset : 8,
            horizontalArrowHeightOffset : 4,
            verticalArrowWidthOffset : 4,
            verticalArrowHeightOffset : 8,
            lineWidth : Ext.isBorderBox ? 2 : 0
        });
    },
    
    drawSingleDependency : function(depRecord) {
        
        var toEl = this.grid.getElementFromEventId(depRecord.get('To')),
            fromEl = this.grid.getElementFromEventId(depRecord.get('From'));
        
        if (!toEl || !fromEl || (this.checkVisible && (!fromEl.isVisible(true) || !toEl.isVisible(true)))) {
            return;
        }
        
        var type = depRecord.get('Type');
    
        switch(type) {
            case Sch.Dependency.StartToEnd:
                this.drawStartToEnd(depRecord.id, fromEl, toEl);
            break;
            
            case Sch.Dependency.StartToStart:
                this.drawStartToStart(depRecord.id, fromEl, toEl);
            break;
            
            case Sch.Dependency.EndToStart:
                this.drawEndToStart(depRecord.id, fromEl, toEl);
            break;
            
            case Sch.Dependency.EndToEnd:
                this.drawEndToEnd(depRecord.id, fromEl, toEl);
            break;
            
            default:
                throw 'Invalid case statement';
            break;
        }
    },
    
    drawEndToStart : function(dependencyId, fromEl, toEl) {
        
        var fromTop = fromEl.getTop(),
            toTop = toEl.getTop(),
            container = fromTop < toTop ? fromEl : toEl,
            containerX = container.getX(),
            containerY = container.getY(),
            x1 = fromEl.getRight() - containerX, 
            y1 = fromTop - containerY + this.yOffset, 
            x2 = toEl.getLeft() - containerX, 
            y2Top = toTop - containerY,
            y2 = y2Top + this.yOffset,
            y2offset = fromTop < toTop ? (y2 - this.yOffset - this.midRowOffset) : (y2 + this.yOffset + this.midRowOffset);
         
         if (x2 >= (x1 - 6) && (toEl.getRight() - containerX > (x1 + 10)) && y2 > y1)
         { 
            var leftOffset = Math.max(x1, x2) + this.xOffset;
            // To after from
            // ---
            //   |
            this.drawLines(container, dependencyId, [
                {
                    x1 : x1, 
                    y1 : y1, 
                    x2 : leftOffset,
                    y2 : y1
                },
                {
                    x1 : leftOffset, 
                    y1 : y1, 
                    x2 : leftOffset, 
                    y2 : y2Top - this.verticalArrowHeightOffset
                },
                {
                    x1 : leftOffset - this.verticalArrowWidthOffset, 
                    y1 : y2Top - this.verticalArrowHeightOffset,
                    direction : 'down'
                }
            ]);
         }
         else {
            // From after to
            //        -
            //         |
            //     ----
            //    |-> 
            //     
            var leftOffset = x1 + this.xOffset + this.horizontalArrowWidthOffset;
            
            this.drawLines(container, dependencyId, [
                {
                    x1 : x1, 
                    y1 : y1, 
                    x2 : leftOffset,
                    y2 : y1
                },
                {
                   x1 : leftOffset, 
                   y1 : y1, 
                   x2 : leftOffset, 
                   y2 : y2offset
                },
                {
                    x1 : leftOffset, 
                    y1 : y2offset, 
                    x2 : x2 - this.xOffset - this.horizontalArrowWidthOffset, 
                    y2 : y2offset
                },
                {
                    x1 : x2 - this.xOffset - this.horizontalArrowWidthOffset, 
                    y1 : y2offset, 
                    x2 : x2 - this.xOffset - this.horizontalArrowWidthOffset, 
                    y2 : y2
                },
                {
                    x1 : x2 - this.xOffset - this.horizontalArrowWidthOffset, 
                    y1 : y2, 
                    x2 : x2 - this.horizontalArrowWidthOffset, 
                    y2 : y2
                },
                {
                    x1 : x2 - this.horizontalArrowWidthOffset, 
                    y1 : y2 - this.horizontalArrowHeightOffset,
                    direction : 'right'
                }
            ]);
        }
    },
    
    drawStartToEnd : function(dependencyId, fromEl, toEl) {
        
        var fromTop = fromEl.getTop(),
            toTop = toEl.getTop(),
            container = fromTop < toTop ? fromEl : toEl,
            containerX = container.getX(),
            containerY = container.getY(),
            x1 = fromEl.getLeft() - containerX - 1, 
            y1 = fromTop - containerY + this.yOffset, 
            x2 = toEl.getRight() - containerX, 
            y2Top = toTop - containerY,
            y2 = y2Top + this.yOffset,
            y2offset = fromTop < toTop ? (y2 - this.yOffset - this.midRowOffset) : (y2 + this.yOffset + this.midRowOffset);
         
         if (x2 > (x1 + this.xOffset - this.horizontalArrowWidthOffset) ||
             Math.abs(x2 - x1) < (2*(this.xOffset + this.horizontalArrowWidthOffset)))
         { 
            var leftOffset = x1 - this.xOffset - this.horizontalArrowWidthOffset;
            // To after from
            // --|
            // |-----------
            //             |
            //          <--|
            this.drawLines(container, dependencyId, [
                {
                    x1 : x1, 
                    y1 : y1, 
                    x2 : leftOffset,
                    y2 : y1
                },
                {
                   x1 : leftOffset, 
                   y1 : y1, 
                   x2 : leftOffset, 
                   y2 : y2offset
                },
                {
                    x1 : leftOffset, 
                    y1 : y2offset, 
                    x2 : x2 + this.xOffset + this.horizontalArrowWidthOffset, 
                    y2 : y2offset
                },
                {
                    x1 : x2 + this.xOffset + this.horizontalArrowWidthOffset, 
                    y1 : y2offset, 
                    x2 : x2 + this.xOffset + this.horizontalArrowWidthOffset, 
                    y2 : y2
                },
                {
                    x1 : x2 + this.xOffset + this.horizontalArrowWidthOffset, 
                    y1 : y2, 
                    x2 : x2 + this.horizontalArrowWidthOffset, 
                    y2 : y2
                },
                {
                    x1 : x2, 
                    y1 : y2 - this.horizontalArrowHeightOffset,
                    direction : 'left'
                }
            ]);
         }
         else {
            // From after to
            //    -----|
            // <--|
            //     
            var leftOffset = x1 - this.xOffset - this.horizontalArrowWidthOffset;
            
            this.drawLines(container, dependencyId, [
                {
                    x1 : x1, 
                    y1 : y1, 
                    x2 : leftOffset,
                    y2 : y1
                },
                {
                   x1 : leftOffset, 
                   y1 : y1, 
                   x2 : leftOffset, 
                   y2 : y2
                },
                {
                    x1 : leftOffset, 
                    y1 : y2, 
                    x2 : x2 + this.horizontalArrowWidthOffset, 
                    y2 : y2
                },
                {
                    x1 : x2, 
                    y1 : y2 - this.horizontalArrowHeightOffset,
                    direction : 'left'
                }
            ]);
        }
    },
    
    drawEndToEnd : function(dependencyId, fromEl, toEl) {
        
        var fromTop = fromEl.getTop(),
            toTop = toEl.getTop(),
            container = fromTop < toTop ? fromEl : toEl,
            containerX = container.getX(),
            containerY = container.getY(),
            x1 = fromEl.getRight() - containerX, 
            y1 = fromTop - containerY + this.yOffset, 
            x2 = toEl.getRight() - containerX, 
            y2Top = toTop - containerY,
            y2 = y2Top + this.yOffset,
            rightPointOffset = x2 + this.xOffset + this.horizontalArrowWidthOffset;
        
        if (x1 > (x2 + this.xOffset)) {
            rightPointOffset += x1 - x2;
        }
         
        this.drawLines(container, dependencyId, [
            {
                x1 : x1, 
                y1 : y1, 
                x2 : rightPointOffset,
                y2 : y1
            },
            {
               x1 : rightPointOffset, 
               y1 : y1, 
               x2 : rightPointOffset, 
               y2 : y2
            },
            {
                x1 : rightPointOffset, 
                y1 : y2, 
                x2 : x2, 
                y2 : y2
            },
            {
                x1 : x2, 
                y1 : y2 - this.horizontalArrowHeightOffset,
                direction : 'left'
            }
        ]);
    },
   
    drawStartToStart : function(dependencyId, fromEl, toEl) {
        
        var fromTop = fromEl.getTop(),
            toTop = toEl.getTop(),
            container = fromTop < toTop ? fromEl : toEl,
            containerX = container.getX(),
            containerY = container.getY(),
            x1 = fromEl.getLeft() - containerX - 2, 
            y1 = fromTop - containerY + this.yOffset, 
            x2 = toEl.getLeft() - containerX, 
            y2Top = toTop - containerY,
            y2 = y2Top + this.yOffset,
            leftPointOffset = this.xOffset + this.horizontalArrowWidthOffset;
        
        if (x1 > (x2 + this.xOffset)) {
            leftPointOffset += (x1 - x2);
        }
        
        this.drawLines(container, dependencyId, [
            {
                x1 : x1, 
                y1 : y1, 
                x2 : x1 - leftPointOffset,
                y2 : y1
            },
            {
               x1 : x1 - leftPointOffset, 
               y1 : y1, 
               x2 : x1 - leftPointOffset, 
               y2 : y2
            },
            {
                x1 : x1 - leftPointOffset, 
                y1 : y2, 
                x2 : x2 - this.horizontalArrowWidthOffset, 
                y2 : y2
            },
            {
                x1 : x2 - this.horizontalArrowWidthOffset, 
                y1 : y2 - this.horizontalArrowHeightOffset,
                direction : 'right'
            }
        ]);
    },
      
   /**
    * Draw a line
    * @method drawLines
    * @param {String} fromEventId, the dependency "origin"
    * @param {String} toEventId, the dependency "destination"
    * @param {Array} coordinatesArray, the array of start/end coordinates to draw
    */  
    drawLines : function(ct, depId, coordinatesArray) {
        var x1, y1, x2, y2, els = [];
        
        // Use the immediate parent as container
        ct = ct.up('', 1);
        
        for (var i = 0, l = coordinatesArray.length; i < l; i++) {
            if (i === l - 1) {
                // Last item is the arrow
                var arrow = Ext.DomHelper.createDom({
		            tag : 'div',
		            cls : String.format('sch-dependency sch-dep-{0} sch-dependency-arrow sch-dependency-arrow-{1}', depId, coordinatesArray[i].direction),
		            style : {
		                left : coordinatesArray[i].x1 + "px",
		                top : coordinatesArray[i].y1 + "px"
		            }
		         });
		         
		         els.push(arrow);
            } else {
                // Create arrow el
                x1 = coordinatesArray[i].x1;
                x2 = coordinatesArray[i].x2;
                y1 = coordinatesArray[i].y1;
                y2 = coordinatesArray[i].y2;
                
                var left = Math.min(x1, x2),
                    top  = Math.min(y1, y2),
                    width  = Math.abs(x2 - x1) + this.lineWidth,
                    height  = Math.abs(y2 - y1) + this.lineWidth,
                    el = Ext.DomHelper.createDom({
		                tag : 'div',
		                cls : String.format('sch-dependency sch-dep-{0} sch-dependency-line', depId), 
		                style : {
		                    left : left + "px",
		                    top : top + "px",
		                    width : width + "px",
		                    height : height + "px"
		                }
		             });
                 
		         els.push(el);
		    }
		}
		ct.appendChild(els);
    }
});
