var closeTime = "";
var isWait = false;
var marketNo = "t00";
var refreshId;

function initStock(stock) {
	$("#marketTab").tabs();
	$("#marketTab").bind("tabsselect", function(e, tab) {
		var tempFist = [];
		if(tab.index == 0) {
			stock = "t00";
		} else if(tab.index == 1) {
			stock = "o00";
		}
		chart = null;
		resetSession();
		clearInterval(refreshId);
		initMarket(stock);
	});
	initMarket(stock);
}

function initMarket(stock) {
	if(stock == ("t00")) {
		$('#marketTab ul').tabs('select', 0);
	}
	if(stock == ("o00")) {
		//alert("stock="+stock+"--");
		$('#marketTab ul').tabs('select', 1);
	}
	$.getJSON(apiBaseUrl + "getStock.jsp?ch=" + stock + ".tw&json=1", function(data) {
		if(data.rtcode == "0000") {
			if(data.msgArray.length == 0) {
				alert("查無股票名稱或代號");
			} else if(data.msgArray.length == 1) {
				marketNo = data.msgArray[0].key;
				chart = null;
				teChart = null;
				loadTechChart(marketNo);
				loadStockInfo(marketNo, data.msgArray[0].d);
				refreshId = setInterval(function() {
					loadStockInfo(marketNo, data.msgArray[0].d);
				}, 300);
			}
		}
	});
}

function loadStockInfo(stock, d0) {
	if(isWait) {
		return;
	}
	isWait = true;
	$.getJSON(apiBaseUrl + "getStockInfo.jsp?ex_ch=" + stock + "&json=1", function(data) {
		if(data.rtcode == "0000") {
			var upper = 0;
			var lower = 0;
			var yesPrice = 0;
			$.each(data.msgArray, function(i, item) {
				//alert("item.c"+item.c);
				//var cStr=item.ex+"_"+item.ch;
				if(item.i == "oidx.tw" || item.i == "tidx.tw") {
					//alert("aa");
					$("#bestFiveDiv").hide();
				}
				cStr = item.c;
				if($("#market_n").text() == "") {
					$("#market_n").text(item.n + "[" + item.c + "]");
				}
				if($("#market_h").text() != item.h) {
					$("#market_h").text(item.h);
				}
				if($("#market_l").text() != item.l) {
					$("#market_l").text(item.l);
				}
				$("#market_o").text(item.o);
				$("#market_y").text(item.y);
				$("#market_v").text(item.v);
				$("#market_t").text(item.t);
				if(typeof(item.z) != 'undefined' && item.y != "-" && item.z != "-") {
					var diffStr = (item.z - item.y).toFixed(2);
					var preStr = (diffStr / item.y * 100).toFixed(2);
					highlightPriceChange("#market_z", item.z);
					if(item.z * 1 < item.y * 1) {
						$("#market_z").css("color", "#105010");
						$("#market_diff").css("color", "#105010");
						$("#market_pre").css("color", "#105010");
						$("#market_diff").text("▼" + diffStr * -1);
					} else if(item.z * 1 > item.y * 1) {
						$("#market_z").css("color", "#ff0000")
						$("#market_diff").css("color", "#ff0000");
						$("#market_pre").css("color", "#ff0000");
						$("#market_diff").text("▲" + diffStr);
					} else if(item.z == item.y) {
						$("#market_z").css("color", "#FFA500")
						$("#market_diff").css("color", "#FFA500");
						$("#market_pre").css("color", "#FFA500");
						$("#market_diff").text(diffStr);
					}
					if(item.z == item.u) {
						$("#market_z").css("color", "#ffffff");
						$("#market_z").css("background-color", "#ff0000")
					}
					if(item.z == item.w) {
						$("#market_z").css("color", "#ffffff");
						$("#market_z").css("background-color", "#105010");
					}
					$("#market_z").text(item.z);
					$("#market_pre").text(preStr + "%");
				}
				upper = (item.u);
				lower = (item.w);
				if(upper == undefined) {
					upper = item.h;
				}
				if(lower == undefined) {
					lower = item.l;
				}
				yesPrice = (item.y);
			});
			loadChart(stock, d0, upper, lower, yesPrice);
		} else {
			alert(data.rtmessage);
		}
		isWait = false;
	});
}
var chart;
var chartChannel;
var teChart;
var groupingUnits = [
	['minute', [1]]
];
var groupingUnits1 = [
	['minute', [1, 5, 10, 30, 6]],
	['hour', [1]]
];
Highcharts.setOptions({
	global: {
		useUTC: false
	}
});

function loadChart(channel, d0, upper, lower, yesPrice) {
	upper = (upper < yesPrice) ? yesPrice : upper;
	lower = (lower > yesPrice) ? yesPrice : lower;
	$.getJSON(apiBaseUrl + "getOhlc.jsp?ex_ch=" + channel + "&d0=" + d0, function(data) {
		var ohlc = [],
			volume = [],
			cPrice = [],
			dataLength = data.msgArray.length;
		var maxValume = 0;
		for(i = 0; i < dataLength; i++) {
			var currTime = parseInt(data.msgArray[i].tlong);
			if(i == 0) {
				var date = new Date(currTime);
				var minutes = date.getMinutes();
				var hours = date.getHours();
				var seconds = date.getSeconds();
				if(!(hours == 9 && minutes == 0)) {
					date.setMinutes(0);
					date.setHours(9);
					cPrice.push([
						date.getTime(), //currentTime, // the date
						yesPrice * 1 // the volume
					]);
				}
			}
			if(closeTime == "" && i == dataLength - 1) {
				var dt = new Date();
				dt.setTime(currTime);
				var dtB = new Date(dt.getFullYear(), dt.getMonth(), dt.getDate(), 13, 31, 0);
				closeTime = dtB.getTime();
			}
			cPrice.push([
				currTime, //currentTime, // the date
				data.msgArray[i].c * 1 // the volume
			]);
			if(data.msgArray[i].s > maxValume) {
				maxValume = data.msgArray[i].s * 1;
			}
			volume.push([
				currTime, //currentTime, // the date
				data.msgArray[i].s * 1 // the volume
			]);
		}
		loadOhcl(channel, cPrice, volume, upper, lower, maxValume, yesPrice);
	});
}

function loadTechChart(channel) {
	// series.data.push(parseFloat(item));
	//alert("loadChart="+channel);
	$.getJSON(apiBaseUrl + "getDailyRangeWithMA.jsp?ex_ch=" + channel + "&d0=" + techD0 + "&d1=" + techD1, function(data) {
		var ohlc = [],
			ma05 = [],
			ma10 = [],
			ma20 = [],
			volume = [],
			cPrice = [],
			dataLength = data.msgArray.length;
		var maxValume = 0;
		for(i = 0; i < dataLength; i++) {
			ohlc.push([
				(data.msgArray[i].tlong) * 1, //currentTime, // the date
				data.msgArray[i].o * 1, // open
				data.msgArray[i].h * 1, // high
				data.msgArray[i].l * 1, // low
				data.msgArray[i].z * 1 // close
			]);
			if(data.msgArray[i].ma05 != 0) {
				ma05.push([
					(data.msgArray[i].tlong) * 1, //currentTime, // the date
					data.msgArray[i].ma05 * 1 // the volume
				]);
			}
			if(data.msgArray[i].ma10 != 0) {
				ma10.push([
					(data.msgArray[i].tlong) * 1, //currentTime, // the date
					data.msgArray[i].ma10 * 1 // the volume
				]);
			}
			if(data.msgArray[i].ma20 != 0) {
				ma20.push([
					(data.msgArray[i].tlong) * 1, //currentTime, // the date
					data.msgArray[i].ma20 * 1 // the volume
				]);
			}
			cPrice.push([
				(data.msgArray[i].tlong) * 1, //currentTime, // the date
				data.msgArray[i].z * 1 // the volume
			]);
			if(data.msgArray[i].s > maxValume) {
				maxValume = data.msgArray[i].s * 1;
			}
			volume.push([
				(data.msgArray[i].tlong) * 1, //currentTime, // the date
				data.msgArray[i].v * 1 // the volume
			]);
		}
		//alert("done");    
		loadTechOhcl(channel, ohlc, ma05, ma10, ma20, volume);
	});
}

function loadOhcl(channel, cPrice, volume, upper, lower, maxValume, yesPrice) {
	var gap = parseInt(yesPrice * 0.015);
	upper = parseInt(yesPrice) + gap;
	lower = parseInt(yesPrice - gap);
	var tkInt = parseInt((upper - lower) / 4);
	if(yesPrice < 150) {
		gap = parseFloat(yesPrice * 0.015);
		upper = parseFloat(yesPrice) + gap;
		lower = parseFloat(yesPrice - gap);
		tkInt = parseFloat((upper - lower) / 4);
	}
	chartChannel = channel;
	//alert("gap="+gap+" upper="+upper+" lower="+lower+" tkInt="+tkInt+"  channel="+channel);
	//alert("chart="+chart+"  chartChannel="+chartChannel+ "  channel="+channel);
	if(chart == null || chart == undefined || chartChannel != channel) {
		chartChannel = channel;
		chart = new Highcharts.StockChart({
			chart: {
				renderTo: 'stockChart',
				alignTicks: false,
				plotBorderColor: '#000000',
				plotBorderWidth: 1
			},
			scrollbar: {
				enabled: false
			},
			credits: {
				text: ""
			},
			navigator: {
				enabled: false
			},
			tooltip: {
				borderColor: 'gray',
				borderWidth: 1
			},
			rangeSelector: {
				enabled: false
			},
			plotOptions: {
				column: {
					animation: false,
					color: 'red'
				},
				line: {
					animation: false,
				}
			},
			xAxis: {
				max: closeTime,
				minorTickInterval: 60 * 60 * 1000,
				type: 'datetime',
				dateTimeLabelFormats: {
					hour: '%H'
				}
			},
			yAxis: [{
				title: {
					text: '成交價'
				},
				labels: {
					style: {
						fontSize: '9px'
					},
					align: 'right',
					x: -2,
					y: 4
				},
				height: 180,
				minPadding: 0,
				min: lower,
				max: upper,
				startOnTick: false,
				endOnTick: false,
				showLastLabel: true,
				//tickPositions:[parseFloat(lower).toFixed(1),parseFloat(lower+tkInt*1).toFixed(1),yesPrice,parseFloat(lower+tkInt*3),parseFloat(lower+tkInt*4)],
				tickPositions: [lower.toFixed(1), (lower + tkInt * 1).toFixed(1), yesPrice, (lower + tkInt * 3).toFixed(1), (lower + tkInt * 4).toFixed(1)],
				plotLines: [{
					value: yesPrice,
					color: 'blue',
					dashStyle: 'shortdash',
					width: 2
				}]
			}, {
				title: {
					text: '成交量'
				},
				max: parseInt(maxValume),
				tickInterval: parseInt((maxValume / 4)),
				min: 0,
				top: 200,
				height: 80,
				offset: 0,
				tickColor: '#0000ff',
				labels: {
					style: {
						fontSize: '9px'
					},
					align: 'right',
					x: -2,
					y: 4
				}
			}],
			series: [{
				type: 'twseline',
				name: "成交價",
				colorup: UP_COLOR,
				colordown: DOWN_COLOR,
				data: cPrice,
				refprice: yesPrice,
				dataGrouping: {
					enabled: false
				}
			}, {
				type: 'column',
				name: '成交量',
				data: volume,
				yAxis: 1,
				dataGrouping: {
					enabled: false
				}
			}]
		});
		chart.redraw(true);
	} else {
		//alert(chart); 
		//chart.series[0].series.hide();
		//chart.series[1].series.hide();
		chart.series[0].setData(cPrice, false);
		chart.series[1].setData(volume, false);
		chart.redraw();
		//chart.redraw(true) ;
		//chart.redraw();
		//chart.series[0].series.show();
		//chart.series[1].series.show();
	}
}

function loadKD(channel) {
	$.getJSON(apiBaseUrl + "getDailyRangeOnlyKD.jsp?ex_ch=" + channel + "&d0=" + techD0 + "&d1=" + techD1, function(data) {
		var kValue = [],
			dValue = [],
			dataLength = data.msgArray.length;
		for(i = 0; i < dataLength; i++) {
			kValue.push([
				data.msgArray[i].tlong * 1,
				data.msgArray[i].k * 1
			]);
			dValue.push([
				(data.msgArray[i].tlong) * 1,
				data.msgArray[i].d * 1
			]);
		}
		if(teChart != undefined) {
			//teChart.series[2].addPoint(kValue);
			/*
			                    teChart.series.push({
			                        type: 'line',
			                    name: "KD",
			                    data: kValue,
			                     yAxis: 2
			                    });*/
			//alert("add done");
		}
	});
}

function loadTechOhcl(channel, ohlc, ma05, ma10, ma20, volume) {
	//alert(yesPrice);
	// set the allowed units for data grouping
	//alert("maxValume"+maxValume);
	//maxValume=maxValume;
	if(teChart == undefined) {
		teChart = new Highcharts.StockChart({
			chart: {
				renderTo: 'techChart',
				zoomType: 'x'
			},
			plotOptions: {
				candlestick: {
					color: 'green',
					upColor: 'red',
					gapSize: 0,
					animation: false
						//dataGrouping:[['day',[1]]]
				},
				column: {
					color: 'red',
					gapSize: 0,
					animation: false
						//dataGrouping:[['day',[1]]]
				},
				line: {
					animation: false,
					gapSize: 0
				}
			},
			scrollbar: {
				enabled: true
			},
			credits: {
				text: ""
			},
			navigator: {
				enabled: true
			},
			tooltip: {
				borderColor: 'gray',
				borderWidth: 1
			},
			rangeSelector: {
				selected: 1,
				enabled: false
			},
			/*plotOptions: 
			{
			    candlestick:
			    {
			        dataGrouping:[['day',[1]]]
			            
			    } 
			},*/
			xAxis: {
				//max:closeTime,
				//minorTickInterval: 30*60*1000
			},
			yAxis: [{
				title: {
					text: '成交價'
				},
				height: 300
					//min:lower,
					//max:upper,
					//minRange:parseInt((upper-lower)/4),
					//maxRange:parseInt((upper-lower)/4),
					//tickInterval: parseInt((upper-lower)/4),
			}, {
				title: {
					text: '成交量'
				},
				//min:0,
				top: 330,
				height: 80,
				offset: 0,
				tickColor: '#0000ff'
			}],
			series: [{
				type: 'candlestick',
				name: "成交價",
				data: ohlc
			}, {
				type: 'line',
				data: ma05,
				name: "5MA"
			}, {
				type: 'line',
				data: ma10,
				name: "10MA"
			}, {
				type: 'line',
				data: ma20,
				name: "20MA"
			}, {
				type: 'column',
				name: '成交量',
				data: volume,
				yAxis: 1
			}]
		});
		//loadKD(channel);
		teChart.redraw(true);
	} else {
		teChart.series[0].setData(ohlc, false);
		teChart.series[1].setData(volume, false);
		teChart.redraw();
	}
}