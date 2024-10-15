var userDelay0 = -1;
var ex = "";
var ind = "";
var indName = "";
var p = 0;
var noPage = 10;
var currList = "";
var currPage = 0;
var noStock = 0;
var allStockList = [];
var tseFist = [];
var otcFist = [];
var isWait = false;

function loadPageChangeFunction() {
	$("#prePage").change(function() {
		switch(parseInt($(this).val())) {
			case 0:
				noPage = 10;
				break;
			case 1:
				noPage = 20;
				break;
			case 2:
				noPage = 50;
				break;
			case 3:
				noPage = 100;
				break;
			case 4:
				noPage = -1;
				break;
		}
		loadStockList();
	});
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

function pageselectCallback(page_index, jq) {
	url = "?ind=" + ind + "&ex=" + ex + "&currPage=" + page_index;
	if(type != "undefined") {
		url = url + "&type=" + type;
	} else {
		url = url + "&type=all";
	}
	try {
		window.history.pushState({}, 0, 'https://' + window.location.host + '/stock/group_etf.jsp' + url);
	} catch(err) {}
	clearInterval(tmpLocalRefId);
	clearInterval(localRefId);
	userDelay0 = -1;
	localRefId = -1;
	if(page_index != p) {
		var newList = "";
		$("#group tbody tr").each(function() {
			this.parentNode.removeChild(this);
		});
		var offset = 0;
		for(i = page_index * noPage; i < (page_index + 1) * noPage + offset && i < allStockList.length; i++) {
			newList += allStockList[i][0] + "_" + allStockList[i][1] + "|";
			cStr = allStockList[i][1].substring(0, allStockList[i][1].indexOf("."));
			var stkName = allStockList[i][2];
			if(stkName.length > 16) stkName = stkName.substring(0, 16) + '...';
			var $tr = $(document.createElement("tr")).appendTo("#group tbody");
			$tr.attr("id", cStr + "_tr");
			if(type == "postponed" || type == "postponedOpening") {
				var $td = $(document.createElement("td")).appendTo($tr);
				$td.attr("id", cStr + "_ex").text("-");
			}
			var $td = $(document.createElement("td")).appendTo($tr);
			$td.attr("id", cStr + "_n");
			$td.css("text-align", "left");
			var $a = $(document.createElement("a")).appendTo($td);
			$a.attr("title", (ind == "TIDX" || ind == "OIDX") ? "" : cStr + " " + allStockList[i][2]);
			$a.attr("href", "fibest.jsp?goback=1&stock=" + cStr);
			$a.text((ind == "TIDX" || ind == "OIDX") ? "" : cStr + " " + stkName);
			var $td = $(document.createElement("td")).appendTo($tr);
			$td.attr("id", cStr + "_z").text("-");
			var $td = $(document.createElement("td")).appendTo($tr);
			var $label = $(document.createElement("label")).appendTo($td);
			$label.attr("id", cStr + "_diff");
			$label.text("-");
			var $label = $(document.createElement("label")).appendTo($td);
			$label.attr("id", cStr + "_pre");
			$label.text("-");
			var $td = $(document.createElement("td")).appendTo($tr);
			$td.attr("id", cStr + "_tv").text("-");
			var $td = $(document.createElement("td")).appendTo($tr);
			$td.attr("id", cStr + "_v").text("-");
			var $td = $(document.createElement("td")).appendTo($tr);
			$td.attr("id", cStr + "_Reference_price").text("-");
			var $td = $(document.createElement("td")).appendTo($tr);
			$td.attr("id", cStr + "_Reference_volume").text("-");
			var $td = $(document.createElement("td")).appendTo($tr);
			$td.attr("id", cStr + "_t").text("-");
			if((ind == "B0") || (ind == "B1")) {
				var $td = $(document.createElement("td")).appendTo($tr);
				$td.attr("id", cStr + "_link").text("-");
				$td.text((ind == "B0") ? GROUP_ETF_ESTIMATED_LINK : GROUP_ETN_ESTIMATED_LINK);
			}
			$("#group").show();
		}
		p = page_index;
		currList = newList;
		loadStockInfo();
	}
}

function loadStockList() {
	if(ex == "tse") {
		$("#marketName").text("[" + FIBEST_TSE + "]");
	} else if(ex == "otc") {
		$("#marketName").text("[" + FIBEST_OTC + "]");
	}
	$.getJSON(apiBaseUrl + "getCategory.jsp?ex=" + ex + "&i=" + ind, function(data) {
		if(data.rtcode == "0000") {
			// alert("noPage="+noPage);
			noStock = data.msgArray.length;
			noPage = (noPage == -1) ? noStock : noPage;
			$("#Pagination").pagination(noStock, {
				callback: pageselectCallback,
				current_page: currPage * 1,
				items_per_page: noPage,
				next_text: NEXT_PAGE,
				prev_text: PREV_PAGE,
				first_text: FIRST_PAGE,
				last_text: LAST_PAGE
			});
			if(indName != "") {
				$("#indName").text(indName);
			}
			$("#totlaRecords").text(noStock);
			currList = "";
			allStockList = [];
			$("#group tbody tr").each(function() {
				this.parentNode.removeChild(this);
			});
			if(data.msgArray.length == 0) {
				var tblRow = "<tr><td colspan=13 style='text-align:center;color:#ff0000'><b><h2>" + GROUP_NOSTOCK + "</h2></b></td></tr>";
				var tdataElement = jQuery(tblRow);
				jQuery('#group tbody').append(tdataElement);
			}
			$.each(data.msgArray, function(i, item) {
				allStockList.push([item.ex, item.ch, item.n]);
				if(i >= noPage * currPage && i < (noPage * currPage * 1) + noPage) {
					cStr = item.ch.substring(0, item.ch.indexOf("."));
					var stkName = item.n;
					if(stkName.length > 16) stkName = item.n.substring(0, 16) + '...';
					var $tr = $(document.createElement("tr"));
					$tr.attr("id", cStr + "_tr");
					var $td = $(document.createElement("td")).appendTo($tr);
					$td.attr("id", cStr + "_n");
					$td.css("text-align", "left");
					var $a = $(document.createElement("a")).appendTo($td).addClass("linkTip");
					$a.attr("title", ((ind == "TIDX" || ind == "OIDX") ? "" : cStr) + allStockList[i][2]);
					$a.attr("href", "fibest.jsp?goback=1&stock=" + cStr);
					$a.text((ind == "TIDX" || ind == "OIDX") ? "" : cStr + " " + stkName);
					var $td = $(document.createElement("td")).appendTo($tr);
					$td.attr("id", cStr + "_z").text("-");
					var $td = $(document.createElement("td")).appendTo($tr);
					var $label = $(document.createElement("label")).appendTo($td);
					$label.attr("id", cStr + "_diff");
					$label.text("-");
					var $label = $(document.createElement("label")).appendTo($td);
					$label.attr("id", cStr + "_pre");
					$label.text("-");
					var $td = $(document.createElement("td")).appendTo($tr);
					$td.attr("id", cStr + "_tv").text("-");
					var $td = $(document.createElement("td")).appendTo($tr);
					$td.attr("id", cStr + "_v").text("-");
					var $td = $(document.createElement("td")).appendTo($tr);
					$td.attr("id", cStr + "_Reference_price").text("-");
					var $td = $(document.createElement("td")).appendTo($tr);
					$td.attr("id", cStr + "_Reference_volume").text("-");
					var $td = $(document.createElement("td")).appendTo($tr);
					$td.attr("id", cStr + "_t").text("-");
					if((ind == "B0") || (ind == "B1")) {
						var $td = $(document.createElement("td")).appendTo($tr);
						$td.attr("id", cStr + "_link").text("-");
						$td.text((ind == "B0") ? GROUP_ETF_ESTIMATED_LINK : GROUP_ETN_ESTIMATED_LINK);
					}
					currList += item.ex + "_" + item.ch + "|";
					jQuery('#group tbody').append($tr);
				}
			});
			$("#group").show();
			loadStockInfo();
		} else {}
	});
}

function insertFiBest(htmlView, values) {
	var valArr = values.split("_");
	for(i = 0; i < valArr.length - 1 || i < 5; i++) {
		if(i == 0) {
			$(htmlView).text(valArr[0]);
		}
	}
}
var localRefId = -1;
var tmpLocalRefId = -1;

function loadStockInfo() {
	if(currList.length == 0) {
		return;
	}
	if(tmpLocalRefId != -1) {
		clearInterval(tmpLocalRefId);
		tmpLocalRefId = -1;
		userDelay0 = -1;
		localRefId = -1;
	}
	$.getJSON(apiBaseUrl + "getStockInfo.jsp?ex_ch=" + currList + "&json=1&delay=0", function(data) {
		if(data.rtcode == "9999") {
			setTimeout(function() {
				self.location.reload()
			}, 1000);
		} else if(data.rtcode == "0000") {
			if(data.userDelay == 500) setTimeout(function() {
				self.location.reload()
			}, 1000);
			if(localRefId == -1) {
				var userDelay = 15000;
				if(typeof(data.userDelay) != 'undefined') {
					userDelay = data.userDelay;
				}
				localRefId = setInterval(function() {
					loadStockInfo()
				}, userDelay);
			}
			if(userDelay0 == -1) {
				userDelay0 = (data.userDelay);
				$("#userDelay0").text(userDelay0 / 1000);
			}
			if(data.userDelay == 0) {
				$("#userDelayView").hide();
			}
			if(data.userDelay < 2000) tmpLocalRefId = localRefId;
			$.each(data.msgArray, function(i, item) {
				// --- 價格欄位顯示到小數點後2位 ---
				if(!isNaN(item.z)) {
					item.z = parseFloat(item.z).toFixed(2);
				}
				if(!isNaN(item.pz)) {
					item.pz = parseFloat(item.pz).toFixed(2);
				}
				if(!isNaN(item.o)) {
					item.o = parseFloat(item.o).toFixed(2);
				}
				if(!isNaN(item.h)) {
					item.h = parseFloat(item.h).toFixed(2);
				}
				if(!isNaN(item.l)) {
					item.l = parseFloat(item.l).toFixed(2);
				}
				if(item.b) { // "6.3800_6.3900_6.4000_6.4100_6.4200_"
					var finalB = "";
					var aryB = item.b.split("_");
					for(var i = 0; i < aryB.length; i++) {
						if(aryB[i] == null || aryB[i] == "") {
							continue;
						}
						finalB += (parseFloat(aryB[i]).toFixed(2) + "_");
					}
					item.b = finalB;
				}
				if(item.a) { // "6.3800_6.3900_6.4000_6.4100_6.4200_"
					var finalA = "";
					var aryA = item.a.split("_");
					for(var i = 0; i < aryA.length; i++) {
						if(aryA[i] == null || aryA[i] == "") {
							continue;
						}
						finalA += (parseFloat(aryA[i]).toFixed(2) + "_");
					}
					item.a = finalA;
				}
				// --- 價格欄位顯示到小數點後2位 ---
				cStr = item.c;
				if($("#" + cStr + "_h").text() != item.h) {
					$("#" + cStr + "_h").text(item.h);
				}
				if($("#" + cStr + "_l").text() != item.l) {
					$("#" + cStr + "_l").text(item.l);
				}
				if(typeof(item.st) != "undefined" && typeof(item.rt) != "undefined") {
					var stStr = item.st;
					var rtStr = item.rt;
					// var tStr=item.t+"";
					var tStr = data.queryTime.sysTime; // item.t+"";
					stStr = parseInt(stStr.replace(/\:/g, ""));
					rtStr = parseInt(rtStr.replace(/\:/g, ""));
					tStr = parseInt(tStr.replace(/\:/g, ""));
					if(stStr == 80000 && rtStr == 999999) {
						$("#" + cStr + "_tr").css("background-color", "#aaa");
					} else if(tStr >= stStr && tStr <= rtStr) {
						$("#" + cStr + "_tr").css("background-color", "#aaa");
					} else {
						$("#" + cStr + "_tr").css("background-color", "#f93");
					}
				}
				$("#" + cStr + "_o").text(item.o);
				$("#" + cStr + "_y").text(item.y);
				$("#" + cStr + "_v").text(item.v);
				$("#" + cStr + "_t").text(item.t);
				if(item.y != "-" && item.z != "-" && item.z != undefined) {
					var diffStr = (item.z - item.y).toFixed(2);
					var preStr = (diffStr / item.y * 100).toFixed(2);
					highlightPriceChange("#" + cStr + "_z", item.z);
					if(item.z * 1 < item.y * 1) {
						$("#" + cStr + "_z").css("color", DOWN_COLOR);
						$("#" + cStr + "_diff").css("color", DOWN_COLOR);
						$("#" + cStr + "_pre").css("color", DOWN_COLOR);
						$("#" + cStr + "_diff").text("▼" + (diffStr * -1));
					} else if(item.z * 1 > item.y * 1) {
						$("#" + cStr + "_z").css("color", UP_COLOR)
						$("#" + cStr + "_diff").css("color", UP_COLOR);
						$("#" + cStr + "_pre").css("color", UP_COLOR);
						$("#" + cStr + "_diff").text("▲" + diffStr);
					} else if(item.z == item.y) {
						$("#" + cStr + "_z").css("color", "#000000")
						$("#" + cStr + "_diff").css("color", "#000000");
						$("#" + cStr + "_pre").css("color", "#000000");
						$("#" + cStr + "_diff").text(diffStr);
					}
					if(item.tv != undefined) {
						$("#" + cStr + "_tv").text(item.tv);
					}
					$("#" + cStr + "_z").css("background-color", "");
					if(item.z == item.u) {
						$("#" + cStr + "_z").css("color", "#ffffff");
						$("#" + cStr + "_z").css("background-color", UP_COLOR)
					}
					if(item.z == item.w) {
						$("#" + cStr + "_z").css("color", "#ffffff");
						$("#" + cStr + "_z").css("background-color", "#105010")
					}
					$("#" + cStr + "_z").text(item.z);
					$("#" + cStr + "_pre").text(" (" + preStr + "%)");
				}
				if(typeof(item.z) == "undefined") {
					$("#" + cStr + "_z").text(item.y);
				}
				// 暫緩收盤
				if(item.ip == 4 || item.ip == 5 || item.ip == 6) {
					$("#" + cStr + "_tr").css("background-color", "#E7C1FF");
				}
				// 趨漲↑
				if(item.ip == 2) {
					$("#" + cStr + "_pre").text($("#" + cStr + "_pre").text() + "↑");
				}
				// 趨跌↓
				if(item.ip == 1) {
					$("#" + cStr + "_pre").text($("#" + cStr + "_pre").text() + "↓");
				}
				// 暫緩收盤，開盤
				if(type == "postponed" || type == "postponedOpening") {
					var exStr = item.ex;
					exStr = (exStr == "tse") ? POSTPONED_TSE : POSTPONED_OTC;
					$("#" + cStr + "_ex").text(exStr);
				}
				// 試算標示
				if(item.ts == '1') {
					// TODO 試算標示
					$("#" + cStr + "_tr").css("background-color", "#D9FFFF");
					// TODO 暫緩收盤，開盤
					if(item.ip == 4 || item.ip == 5 || item.ip == 6) {
						$("#" + cStr + "_tr").css("background-color", "#E7C1FF");
					}
					// TODO 試算價格與欄位
					if(item.ps != undefined) {
						$("#" + cStr + "_Reference_volume").text(item.ps);
					}
					if(item.pz != undefined) {
						$("#" + cStr + "_Reference_price").text(item.pz);
					}
				} else if(item.ts == '0') {
					// TODO 取消試算標示
					if(type != "postponed" && type != "postponedOpening") {
						$("#" + cStr + "_tr").css("background-color", "");
					}
				}
				if(item.g != undefined) {
					insertFiBest("#" + cStr + "_g", item.g, item.y);
				}
				if(item.b != undefined) {
					insertFiBest("#" + cStr + "_b", item.b, item.y, item.h, item.l, item.u, item.w);
				}
				if(item.a != undefined) {
					insertFiBest("#" + cStr + "_a", item.a, item.y, item.h, item.l, item.u, item.w);
				}
				if(item.f != undefined) {
					insertFiBest("#" + cStr + "_f", item.f, item.y);
				}
				if(ind == "TIDX" || ind == "OIDX") {
					$("#marketIndexTime").text(item.t + ")");
				}
				if(ind == "B0") {
					if(item.nu != undefined) {
						$("#" + cStr + "_link").html("<a href='" + item.nu + "' target='_blank'>" + GROUP_ETF_ESTIMATED_LINK + "</a>");
					} else {
						$("#" + cStr + "_link").text("-");
					}
				} else if(ind == "B1") {
					if(item.nu != undefined) {
						$("#" + cStr + "_link").html("<a href='" + item.nu + "' target='_blank'>" + GROUP_ETN_ESTIMATED_LINK + "</a>");
					} else {
						$("#" + cStr + "_link").text("-");
					}
				}
			});
			$("#group").trigger("update");
			$("#group").tablesorter();
		}
	});
}