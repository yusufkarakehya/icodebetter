/**
 * Created by muhammed on 14.9.2015.
 * This code is mostly based on introjs and you can find it here -> https://github.com/usablica/intro.js
 * I changed names to 'Tut' things because i don't like the original.
 * I removed the parts that we don't need for now and i added some parts influenced from other libraries. Like Trip.js.
 * You can find it here -> http://eragonj.github.io/Trip.js/
 * If you intend to modify this code, i strongly advise you to read the code of above projects.
 * Lastly the copyright thing is here.
 * Copyright (C) 2012 Afshin Mehrabani (afshin.meh@gmail.com)
 * 
 * Modified by muhammed until 13.10.2015.
 * Lots of changes specific to Promis Company added.
 */

(function (exports, Ext) {
    
    function Tut() {
        this.options = {
            nextLabel: 'Ileri &rarr;',
            prevLabel: '&larr; Geri',
            doneLabel: 'Bitir',
            position: 'right',
            enableKeyBinding: true,
            enableTooltipDrag: true
        };

        this._constants = {
            ESC: 27,
            LEFT_ARROW: 37,
            RIGHT_ARROW: 39
        };

        var that = this;

        that._keyDown = function(e) {
            keyDown.call(that, e);
        };

        that._onWindowResize = function(e) {
            onWindowResize.call(that, e);
        };
    }

    function bindKeys() {
        var that = this;
        if (window.addEventListener) {
            window.addEventListener('keydown', that._keyDown);
        }
        else if (document.attachEvent) { //--IE
            document.attachEvent('onkeydown', that._keyDown);
        }
    }

    function keyDown(e) {
        switch (e.keyCode) {
            case this._constants.ESC:
                this.end();
                break;

            case this._constants.LEFT_ARROW:
                this.prev();
                break;

            case this._constants.RIGHT_ARROW:
                this.next();
                break;
        }
    }

    function bindWindowResize() {
        var that = this;
        if (window.addEventListener) {
            window.addEventListener('resize', that._onWindowResize);
        }
        else if (document.attachEvent) {
            document.attachEvent('onresize', that._onWindowResize);
        }
    }

    function onWindowResize(e) {
        var that = this;
        setTimeout(function() {
            setHelperLayerPosition.call(that, document.querySelector('.tut-helperLayer'));
            setHelperLayerPosition.call(that, document.querySelector('.tut-referenceLayer'));
        
            var step = that.getCurrentStep();
            var tooltip = document.querySelector('.tut-tooltip');
            placeTooltip(step.el, autoPosition(step.el, step.position, tooltip), tooltip);
        }, 350);
    }

    //-- Message boxes generated during tutorial must have high z-index not the default.
    function increaseExtMsgBoxZIndex() {
        Ext.Msg.getDialog().addClass('tut-highZIndex');
    }

    //-- Give the message box default z-index.
    function decreaseExtMsgBoxZIndex() {
        Ext.Msg.getDialog().removeClass('tut-highZIndex');
    }

    function startTut() {
        arrangeSteps.call(this);

        this.currentStepIndex = 0;
        this.direction = 'forward';

        if (this.options.enableKeyBinding) {
            bindKeys.call(this);
        }

        bindWindowResize.call(this);
        increaseExtMsgBoxZIndex();
        addOverlay.call(this);
        showStep.call(this, this.steps[0]);
    }

    function cleanup() {
        if (this.onCompleteCb && this.currentStepIndex == this.steps.length - 1) {
            this.onCompleteCb();
        }

        if (this.onEndCb) {
            this.onEndCb();
        }

        var overlayLayer   = document.querySelector(".tut-overlay"),
            helperLayer    = document.querySelector(".tut-helperLayer"),
            referenceLayer = document.querySelector(".tut-referenceLayer"),
            floatingEl     = document.querySelector(".tut-floatingElement"),
            fixParents     = document.querySelectorAll(".tut-fixParent"),
            showEl         = document.querySelector(".tut-showElement");

        removeElement(overlayLayer);
        removeElement(helperLayer);
        removeElement(referenceLayer);
        removeElement(floatingEl);

        if (fixParents && fixParents.length > 0) {
            for (var i = fixParents.length - 1; i >= 0; i--) {
                fixParents[i].className = fixParents[i].className.replace(/tut-fixParent/g, '').replace(/^\s+|\s+$/g, '');
            }
        }

        if (showEl) {
            showEl.className = showEl.className.replace(/tut-[a-zA-Z]+/g, '').replace(/^\s+|\s+$/g, '');
        }

        decreaseExtMsgBoxZIndex();
        cleanEvents.call(this);
        this.currentStepIndex = 0;
    }

    function cleanEvents() {
        var that = this;
        unregisterStepEvents(that.getCurrentStep());
        mainPanel.un('tabchange', that._tabChanged);

        if (window.removeEventListener) {
            window.removeEventListener('mouseup', mouseUp);
            window.removeEventListener('keydown', that._keyDown);
            window.removeEventListener('resize', that._onWindowResize);
        }
        else if (document.detachEvent) {
            document.detachEvent('onmouseup', mouseUp);
            document.detachEvent('onkeydown', that._keyDown);
            document.detachEvent('onresize', that._onWindowResize);
        }
    }

    function arrangeSteps() {
        var that = this;

        if (this.steps && Object.prototype.toString.call(this.steps) === '[object Array]' && this.steps.length > 0) {
            var steps = [];
            for (var i = 0; i < this.steps.length; i++) {
                var curr = this.steps[i];
                
                if (curr.isGenerated) {
                    continue;
                }
                
                if ((curr.type === 'open-tab' || curr.type === 'modal') && curr.trigger) {
                    var triggerStep = {
                        element: curr.trigger.element,
                        content: curr.trigger.content,
                        position: curr.trigger.position || curr.position || this.options.position,
                        index: steps.length,
                        canGoNext: false,
                        canGoPrev: typeof(curr.trigger.canGoPrev === 'undefined') ? true : curr.trigger.canGoPrev,
                        type: curr.trigger.type,
                        menuId: curr.trigger.menuId,
                        events: [{ //--Overrides trigger events!
                            name: 'click',
                            handler: function() {
                                that.getCurrentStep().canGoNext = true;
                                that.next();
                            }
                        }],
                        isGenerated: true
                    };

                    steps.push(triggerStep);
                }

                curr.index = steps.length;
                curr.position = curr.position || this.options.position;

                if (typeof(curr.canGoNext) === 'undefined') {
                    curr.canGoNext = true;
                }
                if (typeof(curr.canGoPrev) === 'undefined') {
                    curr.canGoPrev = true;
                }

                //Sanitize step properties with position floating.
                if (curr.position === 'floating') {
                    curr = {
                        index: curr.index,
                        position: curr.position,
                        content: curr.content,
                        canGoPrev: curr.canGoPrev,
                        canGoNext: curr.canGoNext
                    };
                }

                steps.push(curr);
            }
            this.steps = steps;
            updateProgress.call(this);
        }
        else {
            throw new Error("No step is defined!");
        }
    }

    function addStepEvent(step, evt) {
        if (!step.events) {
            step.events = [];
        }

        if (Object.prototype.toString.call(step.events) !== '[object Array]') {
            return;
        }

        for (var i = step.events.length - 1; i >= 0; i--) {
            if (step.events[i].target === evt.target && step.events[i].name === evt.name) {
                return;
            }
        };

        step.events.push(evt);  
    }
    
    function registerStepEvents(step) {
        if (step.events) {
            for (var i = 0; i < step.events.length; i++) {
                var evt = step.events[i];
                if (!evt.target) {
                    if (step.element instanceof Ext.Component || step.element instanceof Ext.Element || step.element instanceof Ext.tree.TreeNode) {
                        evt.target = step.element;
                    }
                    else if (typeof(step.element) === 'string') {
                        evt.target = step.el;
                    }
                    else if (step.element.nodeType && step.element.nodeType == 1) {
                        evt.target = step.el;
                    }
                }

                if (!evt.target) {
                    continue;
                }

                if (evt.target instanceof Ext.Component || evt.target instanceof Ext.Element || evt.target instanceof Ext.tree.TreeNode) {
                    evt.target.on(evt.name, evt.handler);
                }
                else {
                    evt.target.addEventListener(evt.name, evt.handler); //TODO: Maybe needs old IE Check?
                }
            }
        }
    }
    
    function unregisterStepEvents(step) {
        if (step.events) {
            for (var i = 0; i < step.events.length; i++) {
                var evt = step.events[i];

                if (!evt.target) {
                    return;
                }

                if (typeof(evt.unhandler) === 'function') {
                    evt.unhandler();
                }

                if (evt.target instanceof Ext.Element || evt.target instanceof Ext.Component || evt.target instanceof Ext.tree.TreeNode) {
                    evt.target.un(evt.name, evt.handler);
                }
                else {
                    evt.target.removeEventListener(evt.name, evt.handler); //TODO: Maybe needs old IE Check?
                }
            }
        }
    }

    function showStep(step) {
        unregisterStepEvents(this.getCurrentStep());
        controlStepType(this, step);
    }

    function controlStepType(tut, step) {
        if (step.type && step.position !== 'floating') {
            switch (step.type) {
                case 'menu-item':
                    ensureMenuItemVisible(tut, step);
                    break;

                case 'open-tab':
                    ensureTabIsOpenAndVisible(tut, step);
                    break;

                case 'tab-item':
                    ensureTabIsVisible(tut, step);
                    break;

                case 'modal':
                    ensureModalWindowIsVisible(tut, step);
                    break;

                default:
                    controlStepElement(tut, step);
                    break;
            }
        }
        else {
            controlStepElement(tut, step);
        }
    }

    function ensureMenuItemVisible(tut, step) {
        if (step.menuId) {
            var leftPanel = Ext.getCmp('left-panel');
            if (leftPanel.collapsed) {
                leftPanel.expand();
            }

            var menuTreePanel = Ext.getCmp('api-tree-role');
            if (menuTreePanel.collapsed) {
                menuTreePanel.expand();
            }

            var treeSearch = Ext.getCmp('api-tree-search');
            treeSearch.setValue('');
            treeSearch.fireEvent('keydown', treeSearch);

            setTimeout(function() {
                var menuTree = Ext.getCmp('api-tree');
                var node = menuTree.getNodeById(step.menuId);
                if (node) {
                    node.ensureVisible(function () {
                        step.element = node;
                        step.el = document.querySelector("[ext\\:tree-node-id='" + step.menuId + "']"); //-- selector: [ext\\:tree-node-id='mnu_id']
                        controlStepElement(tut, step);
                    });
                }
                else {
                    controlStepElement(tut, step);
                }
            }, 500);
        }
        else {
            controlStepElement(tut, step);
        }
    }

    function ensureTabIsOpenAndVisible(tut, step) {
        if (tut.direction === 'forward') {
            if (!step.el) {
                addStepEvent(step, {
                    name: 'tabchange',
                    target: mainPanel,
                    handler: function() {
                        tut._tabChanged = function(tabContainer, tab, tut, step) {
                            step.element = tab;
                            step.el = document.querySelector("#" + tab.id);
                            step.tabId = tab.id;
                            controlStepElement(tut, step);
                            unregisterStepEvents(step);
                        }.createDelegate(mainPanel, [tut, step], true);
                        return tut._tabChanged;
                    }() //--IIFE    
                });
            }
            registerStepEvents(step);
        }
        else if (tut.direction === 'backward') {
            ensureTabIsVisible(tut, step);
        }
    }

    function ensureTabIsVisible(tut, step) {
        if (step.tabId) {
            var tabItemCmp = Ext.getCmp(step.tabId);
            var tabPanel = tabItemCmp.ownerCt;
            if (tabItemCmp && tabPanel) {
            	tabPanel.setActiveTab(tabItemCmp);
                step.element = tabItemCmp;
                step.el = document.querySelector('#' + tabItemCmp.id.split('.').join('\\.'));
                tabItemCmp.el.scrollIntoView(tabPanel.container);
                controlStepElement(tut, step);
            }
            else {
                controlStepElement(tut, step);
            }
        }
        else {
            controlStepElement(tut, step);
        }
    }

    function ensureModalWindowIsVisible(tut, step) {
        if (!step.el) {
            addStepEvent(step, {
                name: 'modalWindowShown',
                target: mainPanel,
                handler: function(win) {
                    step.element = win;
                    step.el = document.querySelector('#' + win.id);
                    step.canGoPrev = false;
                    controlStepElement(tut, step);
                    unregisterStepEvents(step);
                }
            });
            registerStepEvents(step);
        }
        else {
            controlStepElement(tut, step);
        }
    }

    function controlStepElement(tut, step) {
        if (!step.el && !step.type && step.element) {
            if (step.element.nodeType && step.element.nodeType == 1) { //-- Already Dom Element
                step.el = step.element;
            }

            if (typeof(step.element) === 'string') {
                step.el = step.element.indexOf('#') > -1 ? 
                          document.querySelector(step.element.split('.').join('\\.')) :
                          document.querySelector(step.element); //Consider ids that contains . character
            }

            if (step.element instanceof Ext.Component) {
                step.el = document.querySelector('#' + step.element.getEl().id.split('.').join('\\.'));
                if (step.element._controlTip) {
                    switch (step.element._controlTip) {
                        case 2: //--Date Edit
                            step.el = step.el.parentNode;
                            addStepEvent(step, {
                                name: 'click',
                                target: step.element.trigger,
                                handler: function() {
                                    step.element.menu.addClass('tut-highZIndex');
                                },
                                unhandler: function() {
                                    if (step.element.menu) {
                                        step.element.menu.removeClass('tut-highZIndex');
                                    }
                                }
                            });

                            addStepEvent(step, {
                                name: 'specialkey',
                                target: step.element,
                                handler: function() {
                                    step.element.menu.addClass('tut-highZIndex');
                                },
                                unhandler: function() {
                                    if (step.element.menu) {
                                        step.element.menu.removeClass('tut-highZIndex');
                                    }
                                }
                            });
                            break;
                        
                        case 6:  //--Combo Static Local
                        case 7:  //--Combo Query Local
                        case 8:  //--Lov Combo Static Local
                        case 9:  //--Combo Query Remote
                        case 10: //--Advance Select
                        case 15: //--Lov Combo Query Local
                        case 16: //--Lov Combo Query Remote
                            step.el = step.el.parentNode;
                            addStepEvent(step, {
                                name: 'expand',
                                target: step.element,
                                handler: function() {
                                    step.element.list.addClass('tut-highZIndex');
                                },
                                unhandler: function() {
                                    if (step.element.list) {
                                        step.element.list.removeClass('tut-highZIndex');
                                    }
                                }
                            });
                            break;

                        case 18: //--Timestamp
                            step.el = step.element.el.dom.nextElementSibling.firstElementChild;
                            addStepEvent(step, {
                                name: 'click',
                                target: step.element.df.trigger,
                                handler: function() {
                                    step.element.df.menu.addClass('tut-highZIndex');
                                },
                                unhandler: function() {
                                    if (step.element.df.menu) {
                                        step.element.df.menu.removeClass('tut-highZIndex');
                                    }
                                }
                            });

                            addStepEvent(step, {
                                name: 'specialkey',
                                target: step.element.df,
                                handler: function() {
                                    step.element.df.menu.addClass('tut-highZIndex');
                                },
                                unhandler: function() {
                                    if (step.element.df.menu) {
                                        step.element.df.menu.removeClass('tut-highZIndex');
                                    }
                                }
                            });

                            addStepEvent(step, {
                                name: 'click',
                                target: step.element.tf.trigger,
                                handler: function() {
                                    step.element.tf.list.addClass('tut-highZIndex');
                                },
                                unhandler: function() {
                                    if (step.element.tf.list) {
                                        step.element.tf.list.removeClass('tut-highZIndex');
                                    }
                                }
                            });

                            addStepEvent(step, {
                                name: 'specialkey',
                                target: step.element.tf,
                                handler: function() {
                                    step.element.tf.list.addClass('tut-highZIndex');
                                },
                                unhandler: function() {
                                    if (step.element.tf.list) {
                                        step.element.tf.list.removeClass('tut-highZIndex');
                                    }
                                }
                            });
                            break;

                        case 58: //--SuperBox Select Static
                        case 59: //--SuperBox Select Query
                        case 60: //--SuperBox Select Remote
                        case 61: //--SuperBox Select Query  Advanced
                        case 62: //--SuperBox Select Remote Advanced
                            step.el = step.element.container.dom.firstElementChild;
                            addStepEvent(step, {
                                name: 'click',
                                target: step.element.trigger,
                                handler: function() {
                                    step.element.list.addClass('tut-highZIndex');
                                },
                                unhandler: function() {
                                    if (step.element.list) {
                                        step.element.list.removeClass('tut-highZIndex');
                                    }
                                }
                            });

                            addStepEvent(step, {
                                name: 'specialkey',
                                target: step.element,
                                handler: function() {
                                    step.element.list.addClass('tut-highZIndex');
                                },
                                unhandler: function() {
                                    if (step.element.list) {
                                        step.element.list.removeClass('tut-highZIndex');
                                    }
                                }
                            });
                            break;
                    }
                }
            }
        }

        if (step.position === 'floating') {
            var floatingElement = document.querySelector(".floatingElement");
            if (floatingElement == null) {
                floatingElement = document.createElement('div');
                floatingElement.className = 'floatingElement';
                document.body.appendChild(floatingElement);
            }
            step.el = floatingElement;
        }

        registerStepEvents(step);
        showImpl.call(tut, step);
    }

    function showImpl(step) {
        var hasJustStarted = step.index === 0 && this.direction === "forward";
        if (hasJustStarted) {
            if (this.onStartCb) {
                this.onStartCb();
            }
        }

        if (typeof(step.onBeforeChange) === 'function') {
            step.onBeforeChange(step);
        }

        var that = this;
        var previousCurrentStepIndex = this.currentStepIndex;
        that.currentStepIndex = step.index;

        if (this.onChangeCb) {
            this.onChangeCb(step);
        }

        var skipStep = false;
        
        if (!step.el) {
            skipStep = true;
        }
        else if (step.element instanceof Ext.Component || step.element instanceof Ext.Element) {
            skipStep = !step.element.isVisible();
        }

        if (skipStep) {
            unregisterStepEvents(step);

            if (that.removeStep(step)) {
                that.currentStepIndex = previousCurrentStepIndex;
            }

            if (that.direction === "forward") {
                that.next();
            }

            if (that.direction === 'backward') {
                that.prev();
            }
            return;
        }

        scrollWinIfNeeded(step.el);

        if (hasJustStarted) {
            createTutDom.call(that);
        }

        var oldShowElement = document.querySelector('.tut-showElement'); //-- Remove old classes
        if (oldShowElement) {
            oldShowElement.className = oldShowElement.className.replace(/tut-[a-zA-Z]+/g, '').replace(/^\s+|\s+$/g, '');
        }

        step.el.className += ' tut-showElement';

        var elPosition = getCssPropValue(step.el, 'position');
        if (elPosition !== 'absolute' && elPosition !== 'relative') {
            step.el.className += ' tut-relativePosition';
        }

        var fixParents = document.querySelectorAll('.tut-fixParent'); //-- Remove tut-fixParent class from the elements
        if (fixParents && fixParents.length > 0) {
            for (var i = fixParents.length - 1; i >= 0; i--) {
                fixParents[i].className = fixParents[i].className.replace(/tut-fixParent/g, '').replace(/^\s+|\s+$/g, '');
            }
        }

        var parentElm = step.el.parentNode;
        while (parentElm != null) {
            if (parentElm.tagName.toLowerCase() === 'body') {
                break;
            }

            //fix The Stacking Context problem.
            //More detail: https://developer.mozilla.org/en-US/docs/Web/Guide/CSS/Understanding_z_index/The_stacking_context
            var zIndex = getCssPropValue(parentElm, 'z-index');
            var opacity = parseFloat(getCssPropValue(parentElm, 'opacity'));
            var transform = getCssPropValue(parentElm, 'transform') || getCssPropValue(parentElm, '-webkit-transform') || getCssPropValue(parentElm, '-moz-transform') || getCssPropValue(parentElm, '-ms-transform') || getCssPropValue(parentElm, '-o-transform');
            if (/[0-9]+/.test(zIndex) || opacity < 1 || (transform !== 'none' && transform !== undefined)) {
                parentElm.className += ' tut-fixParent';
            }

            parentElm = parentElm.parentNode;
        }

        var oldHelperLayer       = document.querySelector('.tut-helperLayer'),
            oldReferenceLayer    = document.querySelector('.tut-referenceLayer'),
            oldHelperNumberLayer = oldReferenceLayer.querySelector('.tut-helperNumberLayer'),
            oldTooltipContainer  = oldReferenceLayer.querySelector('.tut-tooltip'),
            oldTooltipLayer      = oldReferenceLayer.querySelector('.tut-tooltipText'),
            oldProgressBar       = oldReferenceLayer.querySelector('.tut-progressBar');

        oldReferenceLayer.style.opacity = 0; //-- Hide the HelperNumber, Tooltip, Buttons.

        setTimeout(function() {
            setHelperLayerPosition.call(that, oldHelperLayer);
            setHelperLayerPosition.call(that, oldReferenceLayer);

            oldTooltipLayer.innerHTML = step.content;
            oldHelperNumberLayer.innerHTML = step.index + 1;
            oldTooltipContainer.style.opacity = 1;
            oldHelperNumberLayer.style.opacity = 1;

            updateProgress.call(that);
            placeTooltip(step.el, autoPosition(step.el, step.position, oldTooltipContainer), oldTooltipContainer);

            if (step.onAfterChange && typeof(step.onAfterChange) === 'function') {
                step.onAfterChange(step);
            }

        }, hasJustStarted ? 0 : 350);
    }

    function scrollWinIfNeeded(el) {
        if (!isElementInViewport(el)) {
            var rect = el.getBoundingClientRect();
            var winHeight = getWindowSize().height;
            var top = rect.bottom - (rect.bottom - rect.top);
            var bottom = rect.bottom - winHeight;

            //-- Scroll up
            if (top < 0 || el.clientHeight > winHeight) {
                window.scrollBy(0, top - 30); // 30px padding from edge to look nice

                //-- Scroll down
            } else {
                window.scrollBy(0, bottom + 100); // 70px + 30px padding from edge to look nice
            }
        }
    }
    
    function updateProgress() {
        var progressBar = document.querySelector('.tut-progressBar');
        if (progressBar) {
            progressBar.setAttribute('style', 'width:' + ((this.currentStepIndex + 1) / this.steps.length * 100) + '%;');
        }
    }

    function addOverlay() {
        var that = this;
        var overlay = document.createElement('div');
        overlay.className = 'tut-overlay';
        document.body.appendChild(overlay);

        overlay.onclick = function() {
            that.end();
        };
    }

    function createTutDom() {
        var that = this;
        var current = that.getCurrentStep();

        var helperLayer = document.createElement('div');
        helperLayer.className = " tut-helperLayer";
        document.body.appendChild(helperLayer);

        var referenceLayer = document.createElement('div');
        referenceLayer.className = " tut-referenceLayer";
        document.body.appendChild(referenceLayer);

        var tooltipLayer = document.createElement('div');
        tooltipLayer.className = " tut-tooltip";
        referenceLayer.appendChild(tooltipLayer);

        var helperNumberLayer = document.createElement('span');
        helperNumberLayer.className = ' tut-helperNumberLayer';
        tooltipLayer.appendChild(helperNumberLayer);

        var tooltipTextLayer = document.createElement('div');
        tooltipTextLayer.className = " tut-tooltipText";
        tooltipLayer.appendChild(tooltipTextLayer);

        var progressLayer = document.createElement('div');
        progressLayer.className = ' tut-progress';
        tooltipLayer.appendChild(progressLayer);

        var progressBar = document.createElement('div');
        progressBar.className = ' tut-progressBar';
        progressLayer.appendChild(progressBar);
        updateProgress.call(that);

        var buttonsLayer = document.createElement('div');
        buttonsLayer.className = ' tut-tooltipButtons';
        tooltipLayer.appendChild(buttonsLayer);

        var prevButton = document.createElement('a');
        prevButton.className = ' tut-button tut-prevButton';
        prevButton.href = 'javascript:void(0);';
        prevButton.innerHTML = this.options.prevLabel;
        prevButton.onclick = function() {
            that.prev();
        };
        buttonsLayer.appendChild(prevButton);

        var nextButton =  document.createElement('a');
        nextButton.className = ' tut-button tut-nextButton';
        nextButton.href = 'javascript:void(0);';
        nextButton.innerHTML = this.options.nextLabel;
        nextButton.onclick = function() {
            that.next();
        };
        buttonsLayer.appendChild(nextButton);

        var doneButton = document.createElement('a');
        doneButton.className = ' tut-button tut-doneButton';
        doneButton.href = 'javascript:void(0);';
        doneButton.innerHTML = this.options.doneLabel;
        doneButton.onclick = function() {
            that.end();
        };
        buttonsLayer.appendChild(doneButton);

        if (that.options.enableTooltipDrag) {
            makeTooltipDraggable(tooltipLayer);
        }
    }

    function makeTooltipDraggable(tooltip) {
        if (window.addEventListener) {
            window.addEventListener('mouseup', mouseUp);
            tooltip.addEventListener('mousedown', tooltipMouseDown);
        }
        else if (document.attachEvent) {
            document.attachEvent('onmouseup', mouseUp);
            tooltip.attachEvent('onmousedown', tooltipMouseDown);
        }
    }

    function mouseUp() {
        if (window.removeEventListener) {
            window.removeEventListener('mousemove', moveTooltip);
        }
        else if (document.detachEvent) {
            document.detachEvent('onmousemove', moveTooltip);
        }
    }

    function tooltipMouseDown(e) {
        var tooltip = document.querySelector('.tut-tooltip');
        if (tooltip) {
            tooltip._offY = e.clientY - parseInt(tooltip.offsetTop, 10);
            tooltip._offX = e.clientX - parseInt(tooltip.offsetLeft, 10);
            tooltip._width = tooltip._width || tooltip.offsetWidth; //--Initial width after tooltip shown, used to prevent resizing while moving
            tooltip._height = tooltip._height || tooltip.offsetHeight; //--Initial height after tooltip shown, used to prevent resizing while moving
            
            if (window.addEventListener) {
                window.addEventListener('mousemove', moveTooltip);
            }
            else if (document.attachEvent) {
                document.attachEvent('onmousemove', moveTooltip);
            }
        }
    }

    function moveTooltip(e) {
        var tooltip = document.querySelector('.tut-tooltip');
        if (tooltip) {
            tooltip.style.top = (e.clientY - tooltip._offY) + 'px';
            tooltip.style.left = (e.clientX - tooltip._offX) + 'px';
            tooltip.style.width = tooltip._width + 'px';
            tooltip.style.height = tooltip._height + 'px';
        }
    }

    function placeTooltip(element, tooltipPosition, tooltipLayer) {
        tooltipLayer.style.top         = null;
        tooltipLayer.style.right       = null;
        tooltipLayer.style.bottom      = null;
        tooltipLayer.style.left        = null;
        tooltipLayer.style.marginLeft  = null;
        tooltipLayer.style.marginTop   = null;
        tooltipLayer.style.width       = null;
        tooltipLayer.style.height      = null;
        tooltipLayer._height           = null; //-- Used to prevent resizing while moving tooltipLayer 
        tooltipLayer._width            = null; //-- Used to prevent resizing while moving tooltipLayer

        var elementOffset = getOffset(element);
        var tooltipOffset = getOffset(tooltipLayer);
        var windowSize = getWindowSize();
        switch (tooltipPosition) {
            case 'top':
                tooltipLayer.style.bottom = (elementOffset.height + 20) + 'px';
                if (elementOffset.left + tooltipOffset.width > windowSize.width) {
                    tooltipLayer.style.left = (windowSize.width - tooltipOffset.width - elementOffset.left) + 'px';
                }
                break;

            case 'left':
                tooltipLayer.style.right = (elementOffset.width + 20) + 'px';
                break;

            case 'right':
                tooltipLayer.style.left = (elementOffset.width + 20) + 'px';
                break;

            case 'bottom':
                tooltipLayer.style.top = (elementOffset.height +  20) + 'px';
                if (elementOffset.left + tooltipOffset.width > windowSize.width) {
                    tooltipLayer.style.left = (windowSize.width - tooltipOffset.width - elementOffset.left) + 'px';
                }
                break;

            case 'floating':
                tooltipLayer.style.left = '50%';
                tooltipLayer.style.top = '50%';
                tooltipLayer.style.marginLeft = '-' + (tooltipOffset.width / 2)  + 'px';
                tooltipLayer.style.marginTop  = '-' + (tooltipOffset.height / 2) + 'px';
                break;
        }
    }

    function autoPosition(element, desiredPosition, tooltipLayer) {
        var windowSize = getWindowSize();
        var elementOffset = getOffset(element);
        var tooltipHeight = getOffset(tooltipLayer).height;
        var tooltipWidth = getOffset(tooltipLayer).width;
        var possiblePositions = ["bottom", "top", "right", "left", "floating"]; // This order is some kind of precedence when auto positioning.
        var selectedPosition = null;

        if (desiredPosition === 'floating') {
            selectedPosition = 'floating';
        }
        else {
            // Check for space to the right
            if (elementOffset.width + elementOffset.left + tooltipWidth > windowSize.width) {
                removeFromArray(possiblePositions, "right");
            }

            // Check for space to the left
            if (elementOffset.left - tooltipWidth < 0) {
                removeFromArray(possiblePositions, "left");
            }

            // Check for space above
            if (elementOffset.top - tooltipHeight < 0) {
                removeFromArray(possiblePositions, "top");
            }

            // Check for space bottom
            if ((elementOffset.height + elementOffset.top + tooltipHeight) > windowSize.height) {
                removeFromArray(possiblePositions, "bottom");
            }

            if (possiblePositions.indexOf(desiredPosition) > -1) {
                selectedPosition = desiredPosition;
            }
            else {
                selectedPosition = possiblePositions[0];
            }
        }
        return selectedPosition;
    }

    function setHelperLayerPosition(helperLayer) {
        var current = this.getCurrentStep();
        if (current.position === 'floating') {
            helperLayer.setAttribute('style', 'left:50%;top:50%');
        }
        else {
            var currentElPos = getOffset(current.el);
            helperLayer.setAttribute('style', 'width: ' + (currentElPos.width  + 10)  + 'px; ' +
            'height:' + (currentElPos.height + 10)  + 'px; ' +
            'top:'    + (currentElPos.top  - 5) + 'px;' +
            'left: '  + (currentElPos.left - 5) + 'px;');
        }
    }

    //-- http://stackoverflow.com/questions/123999/how-to-tell-if-a-dom-element-is-visible-in-the-current-viewport
    function isElementInViewport(el) {
        var rect = el.getBoundingClientRect();
        return (rect.top >= 0 &&
                rect.left >= 0 &&
                rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) &&
                rect.right <= (window.innerWidth || document.documentElement.clientWidth)
        );
    }

    //-- http://stackoverflow.com/questions/5864467/internet-explorer-innerheight
    function getWindowSize() {
        if (window.innerWidth != undefined) {
            return { width: window.innerWidth, height: window.innerHeight };
        }
        else {
            var D = document.documentElement;
            return { width: D.clientWidth, height: D.clientHeight };
        }
    }

    //-- http://stackoverflow.com/questions/442404/retrieve-the-position-x-y-of-an-html-element
    function getOffset(element) {
        var rect = element.getBoundingClientRect();
        var elementPosition = {};
        elementPosition.width = rect.width || element.offsetWidth;
        elementPosition.height = rect.height || element.offsetHeight;
        elementPosition.top = rect.top + window.scrollX;
        elementPosition.left = rect.left + window.scrollY;
        return elementPosition;
    }

    //-- http://www.javascriptkit.com/dhtmltutors/dhtmlcascade4.shtml
    function getCssPropValue(element, propName) {
        var propValue = '';
        if (element.currentStyle) { //IE
            propValue = element.currentStyle[propName];
        }
        else if (document.defaultView && document.defaultView.getComputedStyle) { //Others
            propValue = document.defaultView.getComputedStyle(element, null).getPropertyValue(propName);
        }

        //Prevent exception in IE
        if (propValue && propValue.toLowerCase) {
            return propValue.toLowerCase();
        }
        else {
            return propValue;
        }
    }

    function removeFromArray(arr, el) {
        var index = arr.indexOf(el); //--IE7 & IE8 not supports.
        if (index > -1) {
            arr.splice(index, 1);
            return true;
        }
        return false;
    }

    function removeElement(el) {
        if (el) {
            el.parentNode.removeChild(el);
        }
    }

    Tut.prototype = {
        setSteps: function(steps) {
            this.steps = steps;
            return this;
        },

        addNextStep: function(step, newStep) {
            if (this.steps.indexOf(newStep) === -1) {
                this.steps.splice(step.index + 1, 0, newStep);
                arrangeSteps.call(this);             
            }
            return this;
        },

        addPrevStep: function(step, newStep) {
            if (this.steps.indexOf(newStep) === -1) {
                this.steps.splice(step.index, 0, newStep);
                arrangeSteps.call(this);
            }
            return this;
        },

        removeStep: function(step) {
            if (step) {
                if (removeFromArray(this.steps, step)) {
                    arrangeSteps.call(this);
                    return true;
                }
            }
        },

        setOption: function(option, value) {
            this.options[option] = value;
            return this;
        },

        setOptions: function(options) {
            for (var attr in options) {
                this.options[attr] = options[attr];
            }
            return this;
        },

        start: function() {
            startTut.call(this);
        },

        end: function() {
            cleanup.call(this);
        },

        next: function() {
            if (!this.canGoNext()) {
                return;
            }

            if (this.currentStepIndex <= this.steps.length - 2) {
                this.direction = 'forward';
                showStep.call(this, this.steps[this.currentStepIndex + 1]);
            }
            return this;
        },

        prev: function() {
            if (!this.canGoPrev()) {
                return;
            }

            if (this.currentStepIndex > 0) {
                this.direction = "backward";
                showStep.call(this, this.steps[this.currentStepIndex - 1]);
            }
            return this;
        },

        goToStep: function(stepNumber) {
            if (stepNumber === this.currentStepIndex) {
                return;
            }

            if (stepNumber < 1) {
                stepNumber = 1;
            }
            else if (stepNumber > this.steps.length) {
                stepNumber = this.steps.length;
            }

            if (this.currentStepIndex > stepNumber) {
                if (!this.canGoPrev()) {
                    return;
                }
                this.direction = "backward";
            }
            else {
                if (!this.canGoNext()) {
                    return;
                }
                this.direction = "forward";
            }

            showStep.call(this, this.steps[stepNumber - 1]);
            return this;
        },

        canGoNext: function() {
            var curr = this.steps[this.currentStepIndex];
            if (typeof(curr.canGoNext) === 'function') {
                return curr.canGoNext();
            }
            return curr.canGoNext;
        },

        canGoPrev: function() {
            var curr = this.steps[this.currentStepIndex];
            if (typeof(curr.canGoPrev) === 'function') {
                return curr.canGoPrev();
            }
            return curr.canGoPrev;
        },

        getCurrentStep: function() {
            return this.steps[this.currentStepIndex];
        },

        onStart: function(cb) {
            if (typeof(cb) === 'function') {
                this.onStartCb = cb;
            }
            return this;
        },

        onChange: function(cb) {
            if (typeof(cb) === 'function') {
                this.onChangeCb = cb;
            }
            return this;
        },

        onComplete: function(cb) {
            if (typeof(cb) === 'function') {
                this.onCompleteCb = cb;
            }
        },

        onEnd: function(cb) {
            if (typeof(cb) === 'function') {
                this.onEndCb = cb;
            }
            return this;
        }
    };

    exports.Tut = Tut;
    return Tut;
})(window, Ext);