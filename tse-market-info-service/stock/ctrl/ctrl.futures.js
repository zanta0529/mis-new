var apiBaseUrl = "/stock/api/";
var exeYear = "";
var exeMonth = "";
var exeDay = "";
var startTime = "";
var closeTime = "";
var timezoneOffset = 0;
var hourOffset = 0;
var msecOffset = 0;
var groupingUnits = [
	['minute', [1]]
];
var groupingUnits1 = [
	['minute', [1, 5, 10, 30, 6]],
	['hour', [1]]
];
var chartFutures = null;
var chartIX0126 = null;
Highcharts.setOptions({
	global: {
		useUTC: false
	}
});
var futuresLastTime = "";
var futuresLastTimeStamp = "";
var ix0126LastTime = "";
var ix0126LastTimeStamp = "";
$.curCSS = function(element, attrib, val) {
	$(element).css(attrib, val);
};
var refreshID = -1;
$(document).ready(function() {
	$.ajaxSetup({
		cache: false
	});
	initFuturesData();
	initIX0126Data(); // IX0126
	if(refreshID == -1) {
		var userDelay = (typeof(data) != 'undefined') ? data.userDelay : 5000;
		refreshID = setInterval(function() {
			initFuturesData();
			initIX0126Data(); // IX0126
		}, userDelay);
	}
});

function initFuturesData() {
	$.getJSON("data/futures_chart.txt", function(data) {
		if(data.rtcode == "0000") {
			if(typeof(data.infoArray) != 'undefined' && data.infoArray.length > 0) {
				exeYear = data.infoArray[0].d.substring(0, 4);
				exeMonth = data.infoArray[0].d.substring(4, 6) - 1;
				exeDay = data.infoArray[0].d.substring(6);
				var dtA = new Date(exeYear, exeMonth, exeDay, 08, 44, 0);
				startTime = dtA.getTime();
				var dtB = new Date(exeYear, exeMonth, exeDay, 13, 44, 0);
				closeTime = dtB.getTime();
				procFUTURESIndexPage(data);
			}
		}
	});
}

function procFUTURESIndexPage(data) {
	var item = data.infoArray[0];
	var channel = item.ex + "_" + item.ch;
	var d0 = item.d;
	var upper = item.u;
	var lower = item.w;
	var yesPrice = (item.y);
	var ch = "#INDEX_TX";
	if(typeof(item.z) != 'undefined') {
		if(item.z.length > 0) {
			var zHtmlStr = item.z;
			if(item.y != "-" && item.z != "-" && item.y != "" && item.z != "") {
				var diff = (item.z - item.y).toFixed(2);
				var diffPre = (Math.round(diff / item.y * 10000) / 100).toFixed(2);
				var newColor = "#000000";
				diff = (diff > 0) ? "+" + diff : diff;
			} else {
				var diff = "NA";
				var diffPre = "NA";
				var newColor = "#000000";
			}
			newColor = (diff > 0) ? UP_COLOR : DOWN_COLOR;
			zHtmlStr = "<font color=" + newColor + ">(" + diff + "/" + diffPre + "%)</font>";
			$(ch + "_DIFF").html(zHtmlStr);
		} else {
			$(ch + "_DIFF").html("");
		}
		$("#INDEX_FUTURES").html(item.z);
		$(ch + "_HIGH").text(item.h);
		$(ch + "_LOW").text(item.l);
		if(typeof(data.staticObj) != 'undefined') {
			if(typeof(data.staticObj.tz) != 'undefined') {
				$(ch + "_VALUE").text(addCommas((data.staticObj.tz / 100000000).toFixed(2)));
			}
			if(typeof(data.staticObj.tv) != 'undefined') {
				$(ch + "_VOLUME").text(addCommas(data.staticObj.tv));
			}
			if(typeof(data.staticObj.tr) != 'undefined') {
				$(ch + "_TRANS").text(addCommas(data.staticObj.tr));
			}
			if(typeof(data.staticObj.t4) != 'undefined') {
				$(ch + "_BID_VOLUME").text(addCommas(data.staticObj.t4));
			}
			if(typeof(data.staticObj.t2) != 'undefined') {
				$(ch + "_BID_ORDERS").text(addCommas(data.staticObj.t2));
			}
			if(typeof(data.staticObj.t3) != 'undefined') {
				$(ch + "_ASK_VOLUME").text(addCommas(data.staticObj.t3));
			}
			if(typeof(data.staticObj.t1) != 'undefined') {
				$(ch + "_ASK_ORDERS").text(addCommas(data.staticObj.t1));
			}
		}
	} else {
		$(ch).text(item.y);
	}
	$(ch + "_TIME").text(item.t);
	if(upper == undefined) {
		upper = item.h;
	}
	if(lower == undefined) {
		lower = item.l;
	}
	loadChartFutures(channel, d0, upper * 1, lower * 1, yesPrice * 1, data);
}

function loadChartFutures(channel, d0, upper, lower, yesPrice, data) {
	upper = (upper < yesPrice) ? yesPrice : upper;
	lower = (lower > yesPrice) ? yesPrice : lower;
	var ohlc = [],
		volume = [],
		cPrice = [],
		cTemp = [],
		dataLength = data.ohlcArray.length;
	var maxValume = 0;
	for(i = 0; i < dataLength; i++) {
		var currTime = parseInt(data.ohlcArray[i].t) + msecOffset;
		if(i == 0) {
			var date = new Date(currTime);
			var minutes = date.getMinutes();
			var hours = date.getHours();
			var seconds = date.getSeconds();
			if(!(hours == 8 && minutes == 45)) {
				date.setMinutes(45);
				date.setHours(8);
				cPrice.push([
					date.getTime(),
					yesPrice * 1
				]);
			}
		}
		if(i == dataLength - 1) {
			futuresLastTime = data.ohlcArray[i].ts;
			futuresLastTimeStamp = data.ohlcArray[i].t;
		}
		data.ohlcArray[i].s = data.ohlcArray[i].s;
		if(data.ohlcArray[i].s >= 0) {
			cPrice.push([
				currTime, // currentTime, // the date
				data.ohlcArray[i].c * 1 // the volume
			]);
			if(data.ohlcArray[i].s > maxValume) {
				maxValume = data.ohlcArray[i].s * 1;
			}
			volume.push([
				currTime, // currentTime, // the date
				data.ohlcArray[i].s * 1 // the volume
			]);
		}
	}
	loadOhclFutures(channel, cPrice, volume, upper, lower, maxValume, yesPrice);
}

function loadOhclFutures(channel, cPrice, volume, upper, lower, maxValume, yesPrice) {
	var gap = parseInt(yesPrice * 0.015);
	var upGap = upper - yesPrice;
	var lowGap = yesPrice - lower;
	var tkInt = parseInt((upper + 10 - lower) / 10);
	var topTk = parseInt(lower + tkInt * 10);
	var yes = (Math.round(yesPrice / 10) * 10);
	var tkInt = 10;
	var vGap = 0;
	if(upper - yes > yes - lower) {
		vGap = upper - yes;
	} else {
		vGap = yes - lower;
	}
	if(vGap < 20) {
		tkInt = 5;
	} else {
		for(i = 1; i < 100; i++) {
			if(vGap * 1.05 < 25 * i) {
				tkInt = 5 * (i);
				break;
			}
		}
	}
	var valumeInt = 1;
	for(i = 1; i < 100; i++) {
		if(maxValume / 1000 < i) {
			valumeInt = i;
			break;
		}
	}
	valumeInt = valumeInt * 100;
	if(upGap > gap || lowGap > gap) {
		gap = lowGap;
		if(upGap > lowGap) {
			gap = upGap;
		}
		if(chartFutures != undefined) {
			chartFutures = undefined;
		}
	}
	if(chartFutures != undefined && maxValume > chartFutures.yAxis[1].max) {
		chartFutures.yAxis[1].max = maxValume;
		if(chartFutures != undefined) {
			chartFutures = undefined;
		}
	}
	if(1 == 1) {
		var upper0 = parseInt(yesPrice) + gap;
		var lower0 = parseInt(yesPrice - gap);
		if(upper > upper0) {
			upper = Math.ceil(upper);
			lower = Math.ceil(yesPrice - (upper - yesPrice));
		} else if(lower < lower0) {
			lower = Math.floor(lower);
			upper = Math.ceil(yesPrice + (yesPrice - lower));
		} else {
			upper = upper0;
			lower = lower0;
		}
		chartFutures = new Highcharts.StockChart({
			chart: {
				animation: false,
				renderTo: "futuresChart",
				alignTicks: true,
				plotBorderColor: '#000000',
				plotBorderWidth: 0
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
				useHTML: true,
				borderColor: 'gray',
				borderWidth: 1,
				formatter: function() {
					var s = '<b>' + Highcharts.dateFormat('%Y/%m/%d &nbsp; %H:%M', this.x) + '</b>';
					if(typeof(futuresLastTime) != "undefined" && futuresLastTime.length > 0 && futuresLastTimeStamp > 0) {
						var tempTime = Highcharts.dateFormat('%H%M', this.x);
						if(futuresLastTimeStamp == this.x) {
							var showTime = futuresLastTime.substring(0, 2) + ":" + futuresLastTime.substring(2, 4) + ":" + futuresLastTime.substring(4);
							s = '<b>' + Highcharts.dateFormat('%Y/%m/%d   ', this.x) + showTime + '</b>';
						}
					}
					$.each(this.points, function(i, point) {
						if(i == 1) {
							s += "<br/><font color=" + this.series.color + ">" + this.series.name + ": " + point.y + "&nbsp;" + INDEX_UNIT_F + "</font>";
						} else if(i == 0) {
							var tip_diff = (point.y - yesPrice).toFixed(2);
							if(point.y >= yesPrice) {
								p_color = UP_COLOR;
								tip_diff = '+' + tip_diff;
							} else {
								p_color = DOWN_COLOR;
							}
							s += "<br/><font color=" + p_color + ">" + this.series.name + ": " + point.y + ' (' + tip_diff + ')' + "</font>";
						}
					});
					return s;
				}
			},
			rangeSelector: {
				enabled: false
			},
			plotOptions: {
				area: {
					fillColor: {
						linearGradient: [0, 200, 400],
						stops: [
							[0, Highcharts.getOptions().colors[0]],
							[1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
						]
					},
					animation: false,
					color: "#376FB9",
					fillOpacity: 0.6,
					lineWidth: 1.5,
					zIndex: 1,
					states: {
						hover: {
							enabled: true,
							lineWidth: 1.5
						}
					}
				},
				column: {
					animation: false,
					color: '#3a81ba',
					fillOpacity: 0.6,
					grouping: false
				},
				line: {
					animation: false,
					fillOpacity: 0.6,
					grouping: false,
					color: '#ff0000',
					zIndex: 2,
					lineWidth: 1.2,
					states: {
						hover: {
							enabled: true,
							lineWidth: 1.2
						}
					}
				}
			},
			xAxis: [{
				width: 360,
				height: 280,
				type: 'datetime',
				tickInterval: 60 * 60 * 1000,
				minorTickInterval: 60 * 60 * 1000,
				dateTimeLabelFormats: {
					hour: '%H'
				},
				min: startTime,
				max: closeTime,
				startOnTick: false,
				endOnTick: false,
				maxPadding: 0,
				plotLines: [{
					value: startTime,
					color: '#E4E4E4',
					width: 1
				}, {
					value: closeTime,
					color: '#E4E4E4',
					width: 1
				}]
			}],
			yAxis: [{
				labels: {
					style: {
						color: "#ff0000",
						fontSize: '10px'
					},
					align: 'right',
					x: -4,
					y: 4,
					formatter: function() {
						return this.value;
					}
				},
				width: 360,
				height: 280,
				min: yes - (tkInt * 5),
				max: yes + (tkInt * 5),
				gridLineColor: '#E4E4E4',
				gridLineWidth: 0,
				startOnTick: false,
				endOnTick: false,
				showLastLabel: true,
				tickPositions: [
					yes - (tkInt * 5),
					yes - (tkInt * 4),
					yes - (tkInt * 3),
					yes - (tkInt * 2),
					yes - (tkInt * 1),
					yes,
					yes + (tkInt * 1),
					yes + (tkInt * 2),
					yes + (tkInt * 3),
					yes + (tkInt * 4),
					yes + (tkInt * 5)
				],
				plotLines: [{
					value: yesPrice,
					color: '#A9A9A9',
					dashStyle: 'shortdash',
					width: 2
				}]
			}, {
				max: valumeInt * 10,
				tickInterval: valumeInt,
				gridLineColor: '#E4E4E4',
				width: 360,
				offset: 0,
				startOnTick: false,
				endOnTick: false,
				showLastLabel: true,
				labels: {
					style: {
						color: "#3a81ba",
						fontSize: '10px'
					},
					align: 'left',
					x: 360 + 2,
					y: 4,
					formatter: function() {
						return this.value;
					}
				}
			}],
			series: [{
				type: 'line',
				name: INDEX_FUTURES,
				data: cPrice,
				zIndex: 2,
				colorup: UP_COLOR,
				colordown: DOWN_COLOR,
				refprice: yesPrice,
				dataGrouping: {
					enabled: false
				}
			}, {
				type: 'area',
				name: CHART_VOLUME_F,
				data: volume,
				zIndex: 1,
				yAxis: 1,
				dataGrouping: {
					enabled: false
				}
			}]
		});
		chartFutures.redraw(true);
	} else {
		chartFutures.series[0].setData(cPrice, false);
		chartFutures.series[1].setData(volume, false);
		chartFutures.redraw();
	}
}
// IX0126
// -----------------------------------------------------------------------------
function initIX0126Data() {
	$.getJSON("data/mis_ohlc_IX0126.txt", function(data) {
		if(data.rtcode == "0000") {
			if(typeof(data.infoArray) != 'undefined' && data.infoArray.length > 0) {
				exeYear = data.infoArray[0].d.substring(0, 4);
				exeMonth = data.infoArray[0].d.substring(4, 6) - 1;
				exeDay = data.infoArray[0].d.substring(6);
				var dtA = new Date(exeYear, exeMonth, exeDay, 08, 44, 0);
				startTime = dtA.getTime();
				var dtB = new Date(exeYear, exeMonth, exeDay, 13, 44, 0);
				closeTime = dtB.getTime();
				procIX0126IndexPage(data);
			}
		}
	});
}

function procIX0126IndexPage(data) {
	var item = data.infoArray[0];
	var channel = item.ex + "_" + item.ch;
	var d0 = item.d;
	var upper = item.u;
	var lower = item.w;
	var yesPrice = (item.y);
	var ch = "#INDEX_IX0126";
	if(typeof(item.z) != 'undefined') {
		if(item.z.length > 0) {
			var zHtmlStr = item.z;
			if(item.y != "-" && item.z != "-" && item.y != "" && item.z != "") {
				var diff = (item.z - item.y).toFixed(2);
				var diffPre = (Math.round(diff / item.y * 10000) / 100).toFixed(2);
				var newColor = "#000000";
				diff = (diff > 0) ? "+" + diff : diff;
			} else {
				var diff = "NA";
				var diffPre = "NA";
				var newColor = "#000000";
			}
			newColor = (diff > 0) ? UP_COLOR : DOWN_COLOR;
			zHtmlStr = "<font color=" + newColor + ">(" + diff + "/" + diffPre + "%)</font>";
			$(ch + "_DIFF").html(zHtmlStr);
		} else {
			$(ch + "_DIFF").html("");
		}
		$("#IX0126").html(item.z);
		$(ch + "_HIGH").text(item.h);
		$(ch + "_LOW").text(item.l);
		if(typeof(data.staticObj) != 'undefined') {
			if(typeof(data.staticObj.tz) != 'undefined') {
				$(ch + "_VALUE").text(addCommas((data.staticObj.tz / 100000000).toFixed(2)));
			}
			if(typeof(data.staticObj.tv) != 'undefined') {
				$(ch + "_VOLUME").text(addCommas(data.staticObj.tv));
			}
			if(typeof(data.staticObj.tr) != 'undefined') {
				$(ch + "_TRANS").text(addCommas(data.staticObj.tr));
			}
			if(typeof(data.staticObj.t4) != 'undefined') {
				$(ch + "_BID_VOLUME").text(addCommas(data.staticObj.t4));
			}
			if(typeof(data.staticObj.t2) != 'undefined') {
				$(ch + "_BID_ORDERS").text(addCommas(data.staticObj.t2));
			}
			if(typeof(data.staticObj.t3) != 'undefined') {
				$(ch + "_ASK_VOLUME").text(addCommas(data.staticObj.t3));
			}
			if(typeof(data.staticObj.t1) != 'undefined') {
				$(ch + "_ASK_ORDERS").text(addCommas(data.staticObj.t1));
			}
		}
	} else {
		$(ch).text(item.y);
	}
	$(ch + "_TIME").text(item.t);
	if(upper == undefined) {
		upper = item.h;
	}
	if(lower == undefined) {
		lower = item.l;
	}
	loadChartIX0126(channel, d0, upper * 1, lower * 1, yesPrice * 1, data);
}

function loadChartIX0126(channel, d0, upper, lower, yesPrice, data) {
	upper = (upper < yesPrice) ? yesPrice : upper;
	lower = (lower > yesPrice) ? yesPrice : lower;
	var ohlc = [],
		volume = [],
		cPrice = [],
		cTemp = [],
		dataLength = data.ohlcArray.length;
	var maxValume = 0;
	for(i = 0; i < dataLength; i++) {
		var currTime = parseInt(data.ohlcArray[i].t) + msecOffset;
		if(i == 0) {
			var date = new Date(currTime);
			var minutes = date.getMinutes();
			var hours = date.getHours();
			var seconds = date.getSeconds();
			if(!(hours == 8 && minutes == 45)) {
				date.setMinutes(45);
				date.setHours(8);
				cPrice.push([
					date.getTime(),
					yesPrice * 1
				]);
			}
		}
		if(i == dataLength - 1) {
			ix0126LastTime = data.ohlcArray[i].ts;
			ix0126LastTimeStamp = data.ohlcArray[i].t;
		}
		data.ohlcArray[i].s = data.ohlcArray[i].s;
		if(data.ohlcArray[i].s >= 0) {
			cPrice.push([
				currTime, // currentTime, // the date
				data.ohlcArray[i].c * 1 // the volume
			]);
			if(data.ohlcArray[i].s > maxValume) {
				maxValume = data.ohlcArray[i].s * 1;
			}
			volume.push([
				currTime, // currentTime, // the date
				data.ohlcArray[i].s * 1 // the volume
			]);
		}
	}
	loadOhclIX0126(channel, cPrice, volume, upper, lower, maxValume, yesPrice);
}

function loadOhclIX0126(channel, cPrice, volume, upper, lower, maxValume, yesPrice) {
	var gap = parseInt(yesPrice * 0.015);
	var upGap = upper - yesPrice;
	var lowGap = yesPrice - lower;
	var tkInt = parseInt((upper + 10 - lower) / 10);
	var topTk = parseInt(lower + tkInt * 10);
	var yes = (Math.round(yesPrice / 10) * 10);
	var tkInt = 10;
	var vGap = 0;
	if(upper - yes > yes - lower) {
		vGap = upper - yes;
	} else {
		vGap = yes - lower;
	}
	if(vGap < 20) {
		tkInt = 5;
	} else {
		for(i = 1; i < 100; i++) {
			if(vGap * 1.05 < 25 * i) {
				tkInt = 5 * (i);
				break;
			}
		}
	}
	var valumeInt = 1;
	for(i = 1; i < 100; i++) {
		if(maxValume / 1000 < i) {
			valumeInt = i;
			break;
		}
	}
	valumeInt = valumeInt * 100;
	if(upGap > gap || lowGap > gap) {
		gap = lowGap;
		if(upGap > lowGap) {
			gap = upGap;
		}
		if(chartIX0126 != undefined) {
			chartIX0126 = undefined;
		}
	}
	if(chartIX0126 != undefined && maxValume > chartIX0126.yAxis[1].max) {
		chartIX0126.yAxis[1].max = maxValume;
		if(chartIX0126 != undefined) {
			chartIX0126 = undefined;
		}
	}
	if(1 == 1) {
		var upper0 = parseInt(yesPrice) + gap;
		var lower0 = parseInt(yesPrice - gap);
		if(upper > upper0) {
			upper = Math.ceil(upper);
			lower = Math.ceil(yesPrice - (upper - yesPrice));
		} else if(lower < lower0) {
			lower = Math.floor(lower);
			upper = Math.ceil(yesPrice + (yesPrice - lower));
		} else {
			upper = upper0;
			lower = lower0;
		}
		chartIX0126 = new Highcharts.StockChart({
			chart: {
				animation: false,
				renderTo: "ix0126Chart",
				alignTicks: true,
				plotBorderColor: '#000000',
				plotBorderWidth: 0
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
				useHTML: true,
				borderColor: 'gray',
				borderWidth: 1,
				formatter: function() {
					var s = '<b>' + Highcharts.dateFormat('%Y/%m/%d &nbsp; %H:%M', this.x) + '</b>';
					if(typeof(ix0126LastTime) != "undefined" && ix0126LastTime.length > 0 && ix0126LastTimeStamp > 0) {
						var tempTime = Highcharts.dateFormat('%H%M', this.x);
						if(ix0126LastTimeStamp == this.x) {
							var showTime = ix0126LastTime.substring(0, 2) + ":" + ix0126LastTime.substring(2, 4) + ":" + ix0126LastTime.substring(4);
							s = '<b>' + Highcharts.dateFormat('%Y/%m/%d   ', this.x) + showTime + '</b>';
						}
					}
					$.each(this.points, function(i, point) {
						if(i == 1) { // 臺指期指數沒有成交量，不顯示
						} else if(i == 0) {
							var tip_diff = (point.y - yesPrice).toFixed(2);
							if(point.y >= yesPrice) {
								p_color = UP_COLOR;
								tip_diff = '+' + tip_diff;
							} else {
								p_color = DOWN_COLOR;
							}
							s += "<br/><font color=" + p_color + ">" + this.series.name + ": " + point.y + ' (' + tip_diff + ')' + "</font>";
						}
					});
					return s;
				}
			},
			rangeSelector: {
				enabled: false
			},
			plotOptions: {
				area: {
					fillColor: {
						linearGradient: [0, 200, 400],
						stops: [
							[0, Highcharts.getOptions().colors[0]],
							[1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
						]
					},
					animation: false,
					color: "#376FB9",
					fillOpacity: 0.6,
					lineWidth: 1.5,
					zIndex: 1,
					states: {
						hover: {
							enabled: true,
							lineWidth: 1.5
						}
					}
				},
				column: {
					animation: false,
					color: '#3a81ba',
					fillOpacity: 0.6,
					grouping: false
				},
				line: {
					animation: false,
					fillOpacity: 0.6,
					grouping: false,
					color: '#ff0000',
					zIndex: 2,
					lineWidth: 1.2,
					states: {
						hover: {
							enabled: true,
							lineWidth: 1.2
						}
					}
				}
			},
			xAxis: [{
				width: 360,
				height: 280,
				type: 'datetime',
				tickInterval: 60 * 60 * 1000,
				minorTickInterval: 60 * 60 * 1000,
				dateTimeLabelFormats: {
					hour: '%H'
				},
				min: startTime,
				max: closeTime,
				startOnTick: false,
				endOnTick: false,
				maxPadding: 0,
				plotLines: [{
					value: startTime,
					color: '#E4E4E4',
					width: 1
				}, {
					value: closeTime,
					color: '#E4E4E4',
					width: 1
				}]
			}],
			yAxis: [{
				labels: {
					style: {
						color: "#ff0000",
						fontSize: '10px'
					},
					align: 'right',
					x: -4,
					y: 4,
					formatter: function() {
						return this.value;
					}
				},
				width: 360,
				height: 280,
				min: yes - (tkInt * 5),
				max: yes + (tkInt * 5),
				gridLineColor: '#E4E4E4',
				gridLineWidth: 0,
				startOnTick: false,
				endOnTick: false,
				showLastLabel: true,
				tickPositions: [
					yes - (tkInt * 5),
					yes - (tkInt * 4),
					yes - (tkInt * 3),
					yes - (tkInt * 2),
					yes - (tkInt * 1),
					yes,
					yes + (tkInt * 1),
					yes + (tkInt * 2),
					yes + (tkInt * 3),
					yes + (tkInt * 4),
					yes + (tkInt * 5)
				],
				plotLines: [{
					value: yesPrice,
					color: '#A9A9A9',
					dashStyle: 'shortdash',
					width: 2
				}]
			}, {
				max: valumeInt * 10,
				tickInterval: valumeInt,
				gridLineColor: '#E4E4E4',
				width: 360,
				offset: 0,
				startOnTick: false,
				endOnTick: false,
				showLastLabel: true,
				labels: {
					style: {
						color: "#3a81ba",
						fontSize: '10px'
					},
					align: 'left',
					x: 360 + 2,
					y: 4,
					formatter: function() {
						return this.value;
					}
				}
			}],
			series: [{
				type: 'line',
				name: IDX_IX0126,
				data: cPrice,
				zIndex: 2,
				colorup: UP_COLOR,
				colordown: DOWN_COLOR,
				refprice: yesPrice,
				dataGrouping: {
					enabled: false
				}
			}, {
				type: 'area',
				name: CHART_VOLUME_F,
				data: volume,
				zIndex: 1,
				yAxis: 1,
				dataGrouping: {
					enabled: false
				}
			}]
		});
		chartIX0126.redraw(true);
	} else {
		chartIX0126.series[0].setData(cPrice, false);
		chartIX0126.series[1].setData(volume, false);
		chartIX0126.redraw();
	}
}
// Utilities
// -----------------------------------------------------------------------------
function addCommas(nStr) {
	nStr += '';
	x = nStr.split('.');
	x1 = x[0];
	x2 = x.length > 1 ? '.' + x[1] : '';
	var rgx = /(\d+)(\d{3})/;
	while(rgx.test(x1)) {
		x1 = x1.replace(rgx, '$1' + ',' + '$2');
	}
	return x1 + x2;
}

function setPriceColor(refPrice, currPrice, item) {
	if(currPrice * 1 < refPrice * 1) {
		$(item).css("color", DOWN_COLOR);
	} else if(currPrice * 1 > refPrice * 1) {
		$(item).css("color", UP_COLOR)
	} else if(currPrice == refPrice) {
		$(item).css("color", "#000000")
	}
}