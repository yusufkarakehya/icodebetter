iwb.graph={};

iwb.graph.diagrams={};
iwb.graph.treeInit=function(id, data) { //"diag-" + _page_tab_id
	

	function doubleTreeLayout(diagram) {
	  // Within this function override the definition of '$' from jQuery:
	  var $ = go.GraphObject.make;  // for conciseness in defining templates
	  diagram.startTransaction("Double Tree Layout");
	  // split the nodes and links into two Sets, depending on direction
	  var leftParts = new go.Set(go.Part);
	  var rightParts = new go.Set(go.Part);
	  separatePartsByLayout(diagram, leftParts, rightParts);
	  // but the ROOT node will be in both collections
	  // create and perform two TreeLayouts, one in each direction,
	  // without moving the ROOT node, on the different subsets of nodes and links
	  var layout1 =
	    $(go.TreeLayout,
	      {
	        angle: 180,
	        arrangement: go.TreeLayout.ArrangementFixedRoots,
	        setsPortSpot: false
	      });
	  var layout2 =
	    $(go.TreeLayout,
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
	    }
	  }
	}
	
  var myDiagram = null;
  var $ = go.GraphObject.make;  // for conciseness in defining templates in this function
  if (!iwb.graph.diagrams[id]) {
	  myDiagram = $(go.Diagram, id,   // must be the ID or reference to div
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
      $(go.Node, "Vertical",
        {
          isShadowed: true, shadowOffset: new go.Point(1.5, 1.5), shadowColor: "gray", shadowBlur: 1.5, doubleClick: nodeDoubleClick
        },
        // define the node's outer shape
        $(go.TextBlock,
          {
            stroke: "lightgray", alignment: go.Spot.Left, margin: 2, font: "12px verdana"
          },
          new go.Binding("text", "object")),
        $(go.Panel, "Auto", //{ isShadowed: false},
          $(go.Shape, "RoundedRectangle",
            { fill: "white", strokeWidth: .5, parameter1: 15, stroke: "steelblue" }
            , new go.Binding("stroke", "borderColor")
            , new go.Binding("fill", "bgColor")
          ),

          // a table to contain the different parts of the node
          $(go.TextBlock,
            {
              stroke: "steelblue",
              margin: 2,
              font: "bold 20px verdana",
              alignment: go.Spot.Center
            },
            new go.Binding("text", "text")
            , new go.Binding("stroke", "color")
          )
        ),
        {
          selectionAdornmentTemplate:
            $(go.Adornment, "Auto",
              $(go.Shape, "RoundedRectangle",
                { fill: null, stroke: "dodgerblue", strokeWidth: .5 }),
              $(go.Placeholder)
            )  // end Adornment
        }
      );  // end Node


    // define the Link template
    myDiagram.linkTemplate =
      $(go.Link,  // the whole link panel
        { selectable: false, curve: go.Link.Bezier },
        $(go.Shape, { stroke: "lightgray", strokeWidth: 1 }), $(go.Shape,  // the arrowhead
          { toArrow: "standard", fill: "lightgray", stroke: "lightgray" }));  // the link shape
    iwb.graph.diagrams[id] =  myDiagram;
  } else myDiagram = iwb.graph.diagrams[id];


  myDiagram.model = new go.TreeModel(data);
  doubleTreeLayout(myDiagram);
  return myDiagram;
}
