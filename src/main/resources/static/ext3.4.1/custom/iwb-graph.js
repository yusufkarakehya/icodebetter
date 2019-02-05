iwb.graph={};

iwb.graph.diagrams={};
iwb.graph.treeInit=function(id, data) { //"diag-" + _page_tab_id
	

	function doubleTreeLayout(diagram) {
	  // Within this function override the definition of '$$' from jQuery:
	  var $$ = go.GraphObject.make;  // for conciseness in defining templates
	  diagram.startTransaction("Double Tree Layout");
	  // split the nodes and links into two Sets, depending on direction
	  var leftParts = new go.Set(go.Part);
	  var rightParts = new go.Set(go.Part);
	  separatePartsByLayout(diagram, leftParts, rightParts);
	  // but the ROOT node will be in both collections
	  // create and perform two TreeLayouts, one in each direction,
	  // without moving the ROOT node, on the different subsets of nodes and links
	  var layout1 =
	    $$(go.TreeLayout,
	      {
	        angle: 180,
	        arrangement: go.TreeLayout.ArrangementFixedRoots,
	        setsPortSpot: false
	      });
	  var layout2 =
	    $$(go.TreeLayout,
	      {
	        angle: 0,
	        arrangement: go.TreeLayout.ArrangementFixedRoots,
	        setsPortSpot: false
	      });
	  layout1.doLayout(leftParts);
	  layout2.doLayout(rightParts);
	  diagram.commitTransaction("Double Tree Layout");
	}
	function separatePartsByLayout(diagram, leftParts, rightParts) {
	  var root = diagram.findNodeForKey("root");
	  if (root === null) return;
	  // the ROOT node is shared by both subtrees!
	  leftParts.add(root);
	  rightParts.add(root);
	  // look at all of the immediate children of the ROOT node
	  root.findTreeChildrenNodes().each(function (child) {
	    // in what direction is this child growing?
	    var dir = child.data.dir;
	    var coll = (dir === "left") ? leftParts : rightParts;
	    // add the whole subtree starting with this child node
	    coll.addAll(child.findTreeParts());
	    // and also add the link from the ROOT node to this child node
	    coll.add(child.findTreeParentLink());
	  });
	}

	function onClick() {
	}

	function nodeDoubleClick(e, obj) {
	  var clicked = obj.part;
	  if (clicked !== null) {
	    var thisEntity = clicked.data;
	    if (thisEntity.edit_url) {
	      //      win.minimize();
	      mainPanel.loadTab({ attributes: { href: 'showForm?a=1&_fid=' + thisEntity.edit_url } });
	    } else if(thisEntity.json){
	    	console.log(thisEntity);
	    	var xid='ww-' + thisEntity.key;
	    	var w = Ext.getCmp(xid);
	    	if(!w){
	    		w = new Ext.Window({id:xid,cls:'icb-opacity-hover',autoScroll:!0,title:thisEntity.object + ': ' +thisEntity.text, width: 500, height: 500, html:'<div style="width:100% !important;height:100% !important;font-size: 12px;" id="'+xid+'x"></div>'})
	    		w.show();
				$('#'+xid+'x').jsonViewer(thisEntity.json, { collapsedLevel: 1 });
	    	}
	    }
	  }
	}
	
  var myDiagram = null;
  var $$ = go.GraphObject.make;  // for conciseness in defining templates in this function
  if (!iwb.graph.diagrams[id]) {
	  myDiagram = $$(go.Diagram, id,   // must be the ID or reference to div
      {
        // when the user drags a node, also move/copy/delete the whole subtree starting with that node
        "commandHandler.copiesTree": false,
        "commandHandler.deletesTree": false,
        "draggingTool.dragsTree": true, "toolManager.mouseWheelBehavior": go.ToolManager.WheelZoom,
        initialContentAlignment: go.Spot.Center,  // center the whole graph
        "undoManager.isEnabled": false
      });


    myDiagram.nodeTemplate =
      // the outer shape for the node, surrounding the Table{ doubleClick: nodeDoubleClick },
      $$(go.Node, "Vertical",
        {
         /* isShadowed: true, shadowOffset: new go.Point(1.1, 1.1), shadowColor: "gray", shadowBlur: 2, */doubleClick: nodeDoubleClick
        },
        // define the node's outer shape
        $$(go.TextBlock,
          {
            stroke: "#aaa", alignment: go.Spot.Left, margin: 2, font: "normal 11px 'Open Sans', sans-serif"
          },
          new go.Binding("text", "object")),
        $$(go.Panel, "Auto", //{ isShadowed: false},
          $$(go.Shape, "RoundedRectangle",
            { fill: "white", stroke: "steelblue" }
            , new go.Binding("stroke", "borderColor")
            , new go.Binding("fill", "bgColor")
          ),

          // a table to contain the different parts of the node
          $$(go.TextBlock,
            {
              stroke: "steelblue",
              margin: 5,
              font: "bold 15px 'Open Sans', sans-serif",
              alignment: go.Spot.Center
            },
            new go.Binding("text", "text")
            , new go.Binding("stroke", "color")
          )
        ),
        {
          selectionAdornmentTemplate:
            $$(go.Adornment, "Auto",
              $$(go.Shape, "RoundedRectangle",
                { fill: null, stroke: "dodgerblue", strokeWidth: .5 }),
              $$(go.Placeholder)
            )  // end Adornment
        }
      );  // end Node


    // define the Link template
    myDiagram.linkTemplate =
      $$(go.Link,  // the whole link panel
        { selectable: false, curve: go.Link.Bezier },
        $$(go.Shape, { stroke: "#777", strokeWidth: 2 }), $$(go.Shape,  // the arrowhead
          { toArrow: "standard", fill: "#777", stroke: "#777" }));  // the link shape
    iwb.graph.diagrams[id] =  myDiagram;
  } else myDiagram = iwb.graph.diagrams[id];


  myDiagram.model = new go.TreeModel(data);
  doubleTreeLayout(myDiagram);
  return myDiagram;
}


iwb.graphAmchart = function(dg, gid) {
	  var newStat = 1 * dg.funcTip ? dg.funcFields : "";
	  var params = {};
	  if (newStat) params._ffids = newStat;
	  if (1 * dg.graphTip >= 5) params._sfid = dg.stackedFieldId;
	  iwb.request({
	    url:
	      "ajaxQueryData4StatTree?_gid=" +
	      dg.gridId +
	      "&_stat=" +
	      dg.funcTip +
	      "&_qfid=" +
	      dg.groupBy +
	      "&_dtt=" +
	      dg.dtTip,
	    params: Object.assign(params, dg.queryParams),
	    successCallback: function(az) {
	      if (!az.data) az.data = [];
	      for (var qi = 0; qi < az.data.length; qi++)
	        az.data[qi].dsc = az.data[qi].dsc || "(empty)";
	      var resc = 1;
	      if (1 * dg.graphTip < 5) {
	        if (newStat.indexOf(",") > 0) {
	          resc = newStat.split(",").length;
	          if (resc > 4) resc = 4;
	          for (var qi = 0; qi < az.data.length; qi++) {
	            az.data[qi].xres = Math.round(1 * (az.data[qi].xres || 0));
	            for (var zi = 2; zi <= resc; zi++) {
	              az.data[qi]["xres" + zi] = Math.round(
	                1 * (az.data[qi]["xres" + zi] || 0)
	              );
	            }
	          }
	        } else {
	          var colors = [
	            "#FF0F00",
	            "#FF6600",
	            "#FF9E01",
	            "#FCD202",
	            "#F8FF01",
	            "#B0DE09",
	            "#04D215",
	            "#0D8ECF",
	            "#0D52D1",
	            "#2A0CD0",
	            "#8A0CCF",
	            "#CD0D74"
	          ];
	          for (var qi = 0; qi < az.data.length; qi++) {
	            az.data[qi].xres = Math.round(1 * (az.data[qi].xres || 0));
	            az.data[qi].color = colors[qi % colors.length];
	          }
	        }
	      } else {
	        for (var ki in az.lookUp) {
	          if (1 * dg.graphTip == 6)
	            for (var qi = 0; qi < az.data.length; qi++) {
	              az.data[qi]["xres_" + ki] = Math.round(
	                1 * (az.data[qi]["xres_" + ki] || 0)
	              );
	            }
	          if (!az.lookUp[ki]) az.lookUp[ki] = "*" + ki;
	        }
	      }
	      var extraCfg =
	        dg.is3d && 1 * dg.graphTip < 6 ? { depth3D: 20, angle: 30 } : {};
	      switch (1 * dg.graphTip) {
	        case 6: //stacked area
	          var graphs = [];
	          for (var k in az.lookUp)
	            graphs.push({
	              balloonText: "<b>" + az.lookUp[k] + ": [[xres_" + k + "]]</b>",
	              fillAlphas: 0.6,
	              lineAlpha: 0.2,
	              color: "#000000",
	              title: az.lookUp[k] || "(empty)",
	              valueField: "xres_" + k
	            });
	          if (dg.legend)
	            extraCfg.legend = {
	              equalWidths: false,
	              periodValueText: "total: [[value.sum]]",
	              position: "top",
	              valueAlign: "left",
	              valueWidth: 100
	            };
	          AmCharts.makeChart(
	            gid,
	            Object.assign(extraCfg, {
	              type: "serial",
	              theme: "black",
	              graphs: graphs,
	              valueAxes: [
	                {
	                  stackType: "regular",
	                  axisAlpha: 0.3,
	                  gridAlpha: 0.2
	                }
	              ],
	              chartCursor: {
	                categoryBalloonEnabled: false,
	                cursorAlpha: 0,
	                zoomable: false
	              },
	              categoryField: "dsc",
	              categoryAxis: {
	                labelRotation: 45,
	                gridPosition: "start",
	                axisAlpha: 0,
	                gridAlpha: 0,
	                position: "left"
	              },
	              dataProvider: az.data
	            })
	          );

	          break;

	        case 5: //stacked column
	          var graphs = [];
	          for (var k in az.lookUp)
	            graphs.push({
	              balloonText: "<b>" + az.lookUp[k] + ": [[xres_" + k + "]]</b>",
	              fillAlphas: 0.9,
	              lineAlpha: 0.2,
	              type: "column",
	              color: "#000000",
	              title: az.lookUp[k] || "(empty)",
	              valueField: "xres_" + k
	            });
	          if (dg.legend)
	            extraCfg.legend = {
	              equalWidths: false,
	              periodValueText: "total: [[value.sum]]",
	              position: "top",
	              valueAlign: "left",
	              valueWidth: 100
	            };
	          AmCharts.makeChart(
	            gid,
	            Object.assign(extraCfg, {
	              type: "serial",
	              theme: "black",
	              graphs: graphs,
	              valueAxes: [
	                {
	                  stackType: "regular",
	                  axisAlpha: 0.3,
	                  gridAlpha: 0.2
	                }
	              ],
	              chartCursor: {
	                categoryBalloonEnabled: false,
	                cursorAlpha: 0,
	                zoomable: false
	              },
	              categoryField: "dsc",
	              categoryAxis: {
	                labelRotation: 45,
	                gridPosition: "start",
	                axisAlpha: 0,
	                gridAlpha: 0,
	                position: "left"
	              },
	              dataProvider: az.data
	            })
	          );

	          break;

	        case 3:
	          if (dg.legend) extraCfg.legend = { position: "left" };
	          AmCharts.makeChart(
	            gid,
	            Object.assign(extraCfg, {
	              type: "pie",
	              theme: "black",
	              startDuration: 1,
	              labelRadius: 15,
	              labelText: dg.legend ? "[[percents]]%" : "[[dsc]]: [[value]]",
	              innerRadius: "50%",

	              //							     "legend":{"position":"left"}, //","marginRight":100,"autoMargins":false
	              dataProvider: az.data,
	              balloonText: "[[dsc]]: [[value]]",
	              valueField: "xres",
	              titleField: "dsc"
	            })
	          );
	          break;
	        case 1:
	          if (resc > 1) {
	            //pek cok var
	            var qq = newStat.split(",");
	            var graphs = [
	              {
	                balloonText:
	                  "<b>" +
	                  "TODO" /*mf._func_query_field_ids.store.getById(qq[0].split('=')[1]).get('dsc')*/ +
	                  ": [[xres]]</b>",
	                fillAlphas: 0.9,
	                lineAlpha: 0.2,
	                type: "column",
	                valueField: "xres"
	              }
	            ];
	            for (var zi = 2; zi <= resc; zi++)
	              graphs.push({
	                balloonText:
	                  "<b>" +
	                  "TODO" /*mf._func_query_field_ids.store.getById(qq[zi-1]).get('dsc')*/ +
	                  ": [[xres" +
	                  zi +
	                  "]]</b>",
	                fillAlphas: 0.9,
	                lineAlpha: 0.2,
	                type: "column",
	                valueField: "xres" + zi
	              });
	            AmCharts.makeChart(
	              gid,
	              Object.assign(extraCfg, {
	                type: "serial",
	                theme: "black",
	                startDuration: 1,
	                graphs: graphs,
	                chartCursor: {
	                  categoryBalloonEnabled: false,
	                  cursorAlpha: 0,
	                  zoomable: false
	                },
	                categoryField: "dsc",
	                categoryAxis: {
	                  gridPosition: "start",
	                  labelRotation: 45
	                },
	                dataProvider: az.data
	              })
	            );
	          } else
	            AmCharts.makeChart(
	              gid,
	              Object.assign(extraCfg, {
	                type: "serial",
	                theme: "black",
	                startDuration: 1,
	                graphs: [
	                  {
	                    balloonText: "<b>[[dsc]]: [[xres]]</b>",
	                    fillColorsField: "color",
	                    fillAlphas: 0.9,
	                    lineAlpha: 0.2,
	                    type: "column",
	                    valueField: "xres"
	                  }
	                ],
	                chartCursor: {
	                  categoryBalloonEnabled: false,
	                  cursorAlpha: 0,
	                  zoomable: false
	                },
	                categoryField: "dsc",
	                categoryAxis: {
	                  gridPosition: "start",
	                  labelRotation: 45
	                },
	                dataProvider: az.data
	              })
	            );
	          break;
	      }
	    }
	  });
	};
