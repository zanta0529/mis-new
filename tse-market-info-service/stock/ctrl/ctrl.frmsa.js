var apiBaseUrl = "/stock/api/";
var userDelay = -1;
var startTime = "";
var closeTime = "";
var timezoneOffset = 0;
var hourOffset = 0;
var msecOffset = 0;
$.curCSS = function(element, attrib, val) {
	$(element).css(attrib, val);
};
$(document).ready(function() {
	$.ajaxSetup({
		cache: false
	});
	procIndexPageFRMSA();
});
var refreshTseId = -1;

function procIndexPageFRMSA() {
	//$.getJSON(apiBaseUrl+"getChartOhlcStatis.jsp?ex=tse&ch=FRMSA.tw&fqy=1",function(data)
	$.getJSON("data/mis_ohlc_FRMSA.txt", function(data) {
		if(data.rtcode == "0000") {
			if(typeof(data.infoArray) != 'undefined' && data.infoArray.length > 0) {
				exeYear = data.infoArray[0].d.substring(0, 4);
				exeMonth = data.infoArray[0].d.substring(4, 6) - 1;
				exeDay = data.infoArray[0].d.substring(6);
				//initInfoArray(data.infoArray);
				var dtB = new Date(exeYear, exeMonth, exeDay, 13, 33, 0);
				closeTime = dtB.getTime();
				var dtA = new Date(exeYear, exeMonth, exeDay, 9, 00, 0);
				startTime = dtA.getTime();
				procFRMSAIndexPage(data);
			}
			if(refreshTseId == -1) {
				var userDelay = 15000;
				if(typeof(data.userDelay) != 'undefined') {
					userDelay = data.userDelay;
				}
				refreshTseId = setInterval(function() {
					procIndexPageFRMSA();
				}, userDelay);
			}
		}
	});
}
var refreshOtcId = -1;
var exeYear = "";
var exeMonth = "";
var exeDay = "";
var refreshId = -1;

function setPriceColor(refPrice, currPrice, item) {
	if(currPrice * 1 < refPrice * 1) {
		$(item).css("color", DOWN_COLOR);
	} else if(currPrice * 1 > refPrice * 1) {
		$(item).css("color", UP_COLOR)
	} else if(currPrice == refPrice) {
		$(item).css("color", "#000000")
	}
}

function procFRMSAIndexPage(data) {
	var item = data.infoArray[0];
	var channel = item.ex + "_" + item.ch;
	var d0 = item.d;
	var upper = item.u;
	var lower = item.w;
	var yesPrice = (item.y);
	var ch = "#INDEX_";
	ch += "FRMSA";
	//alert(ch);
	if(typeof(item.z) != 'undefined') {
		var zHtmlStr = item.z;
		var diff = (item.z - item.y).toFixed(2);
		var diffPre = (Math.round(diff / item.y * 10000) / 100).toFixed(2);
		var newColor = "#000000";
		diff = (diff > 0) ? "+" + diff : diff;
		newColor = (diff > 0) ? UP_COLOR : DOWN_COLOR;
		zHtmlStr = "<font color=" + newColor + ">(" + diff + "/" + diffPre + "%)</font>";
		$(ch).html(item.z);
		$(ch + "_DIFF").html(zHtmlStr);
		$(ch + "_HIGH").text(item.h);
		$(ch + "_LOW").text(item.l);
		$(ch + "_VALUE").text(addCommas((data.staticObj.tz / 100000000).toFixed(2)));
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
	} else {
		$(ch).text(item.y);
	}
	$(ch + "_TIME").text(item.t);
	if(item.t.indexOf("13:33") >= 0 || item.t.indexOf("13:31") >= 0) {
		var dateStr = item.d;
		dateStr = dateStr.substring(0, 4) + "/" + dateStr.substring(4, 6) + "/" + dateStr.substring(6);
		$(ch + "_TIME").text(dateStr);
	}
	if(upper == undefined) {
		upper = item.h;
	}
	if(lower == undefined) {
		lower = item.l;
	}
	loadChart(channel, d0, upper * 1, lower * 1, yesPrice * 1, data);
}
var chart;
var chartChannel;
var otcChart;
var otcChartChannel;
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
var tseLastTime = "";
var tseLastTimeStamp = "";
var otcLastTime = "";
var otcLastTimeStamp = "";

function loadChart(channel, d0, upper, lower, yesPrice, data) {
	upper = (upper < yesPrice) ? yesPrice : upper;
	lower = (lower > yesPrice) ? yesPrice : lower;
	var ohlc = [],
		volume = [],
		cPrice = [],
		cTemp = [],
		dataLength = data.ohlcArray.length;
	var maxValume = 0;
	//tseLastTime="";
	//otcLastTime="";
	for(i = 0; i < dataLength; i++) {
		var currTime = parseInt(data.ohlcArray[i].t) + msecOffset;
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
		if(i == dataLength - 1) {
			if(channel.indexOf("FRMSA") > 0) {
				tseLastTime = data.ohlcArray[i].ts;
				tseLastTimeStamp = data.ohlcArray[i].t;
				//alert("tseLastTime="+tseLastTime);
			}
		}
		data.ohlcArray[i].s = (data.ohlcArray[i].s / 100).toFixed(2);
		if(data.ohlcArray[i].s >= 0) {
			cPrice.push([
				currTime, //currentTime, // the date
				data.ohlcArray[i].c * 1 // the volume
			]);
			if(data.ohlcArray[i].s > maxValume) {
				maxValume = data.ohlcArray[i].s * 1;
			}
			volume.push([
				currTime, //currentTime, // the date
				data.ohlcArray[i].s * 1 // the volume
			]);
		}
	}
	loadOhcl(channel, cPrice, volume, upper, lower, maxValume, yesPrice);
}
var sPoint = 200;

function loadOhcl(channel, cPrice, volume, upper, lower, maxValume, yesPrice) {
	if(channel.indexOf("FRMSA") > 0) {
		var gap = parseInt(yesPrice * 0.015);
		var upGap = upper - yesPrice;
		var lowGap = yesPrice - lower;
		var tkInt = parseInt((upper + 10 - lower) / 10);
		chartChannel = channel;
		var topTk = parseInt(lower + tkInt * 10);
		var yes = (Math.round(yesPrice / 10) * 10);
		var tkInt = 10;
		//alert(upper-yesPrice);
		//alert(yesPrice - lower);
		var vGap = 0;
		if(upper - yes > yes - lower) vGap = upper - yes;
		else vGap = yes - lower;
		if(vGap < 20) tkInt = 5;
		else {
			for(i = 1; i < 200; i++) {
				if(vGap * 1.05 < 25 * i) {
					tkInt = 5 * (i);
					break;
				}
			}
		}
		for(i = 1; i < 300; i++) {
			if(maxValume < 10 * i) {
				valumeInt = i;
				break;
			}
		}
		if(upGap > gap || lowGap > gap) {
			gap = lowGap;
			if(upGap > lowGap) {
				gap = upGap;
			}
			if(chart != undefined) {
				chart = undefined;
			}
		}
		if(chart != undefined && maxValume > chart.yAxis[1].max) {
			chart.yAxis[1].max = maxValume;
			if(chart != undefined) {
				chart = undefined;
			}
		}
		//if(chart==undefined || chartChannel!=channel)
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
			chartChannel = channel;
			chart = new Highcharts.StockChart({
				chart: {
					animation: false,
					renderTo: 'frmsaChart',
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
						//alert(typeof(tseLastTime));
						//alert(tseLastTimeStamp);
						if(typeof(tseLastTime) != "undefined" && tseLastTime.length > 0 && tseLastTimeStamp > 0) {
							var tempTime = Highcharts.dateFormat('%H%M', this.x);
							//alert(tseLastTimeStamp-this.x);
							if(tseLastTimeStamp == this.x) {
								var showTime = tseLastTime.substring(0, 2) + ":" + tseLastTime.substring(2, 4) + ":" + tseLastTime.substring(4);
								s = '<b>' + Highcharts.dateFormat('%Y/%m/%d   ', this.x) + showTime + '</b>';
							}
						}
						$.each(this.points, function(i, point) {
							if(i == 1) {
								s += "<br/><font color=" + this.series.color + ">" + this.series.name + ": " + point.y + INDEX_UNIT + "</font>";
							} else if(i == 0) {
								var tip_diff = (point.y - yesPrice).toFixed(2);
								if(point.y >= yesPrice) {
									p_color = UP_COLOR;
									tip_diff = '+' + tip_diff;
								} else p_color = DOWN_COLOR;
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
					width: 350,
					height: 280,
					type: 'datetime',
					tickInterval: 60 * 60 * 1000,
					//minTickInterval: 60*60*1000,
					minorTickInterval: 60 * 60 * 1000,
					dateTimeLabelFormats: {
						hour: '%H'
					},
					//min:startTime,
					min: startTime,
					max: closeTime,
					startOnTick: false,
					endOnTick: false,
					maxPadding: 0,
					plotLines: [{
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
						x: -5,
						y: 4,
						formatter: function() {
							return this.value;
						}
					},
					width: 350,
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
					height: 280,
					width: 350,
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
						x: 350 + 2,
						y: 4
					}
				}],
				series: [{
					type: 'line',
					name: INDEX_FRMSA,
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
					name: CHART_VOLUME,
					data: volume,
					zIndex: 1,
					yAxis: 1,
					dataGrouping: {
						enabled: false
					}
				}]
			});
			chart.redraw(true);
		} else {
			chart.series[0].setData(cPrice, false);
			chart.series[1].setData(volume, false);
			chart.redraw();
		}
	}
}

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