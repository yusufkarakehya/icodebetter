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
