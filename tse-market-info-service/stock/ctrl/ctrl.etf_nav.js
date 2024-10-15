var userDelay0 = -1;
var ex = "";
var ind = "";
var indName = "";
var p = 0;
var noPage = 500;
var currList = "";
var currPage = 0;
var noStock = 0;
// var allStockList = [];
var ETFType = [];
var tseFist = [];
var otcFist = [];
var isWait = false;
var srcUrl = "data/all_etf.txt";

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

function loadStockList() {
	clearTableContent();
	$.getJSON(srcUrl + "?" + $.now(), function(data) {
        data = JSON.parse(filterXSS(JSON.stringify(data)));
		for(idata = 0; idata <= data.a1.length - 1; idata++) {
			pdata = data.a1[idata];
			refURL = pdata.refURL;
			if(typeof(refURL) != 'undefined') {
				$.each(pdata.msgArray, function(i, item) {
					ETFType.push([item.a, item.k, refURL]);
				})
			}
		}
		$.getJSON(apiBaseUrl + "getCategory.jsp?ex=" + ex + "&i=" + ind, function(data) {
            data = JSON.parse(filterXSS(JSON.stringify(data)));
			if(data.rtcode == "0000") {
				// alert("noPage="+noPage);
				noStock = data.msgArray.length;
				noPage = (noPage == -1) ? noStock : noPage;
				if(indName != "") {
					$("#indName").text(indName);
				}
				$("#totlaRecords").text(noStock);
				currList = "";
				// allStockList = [];
				if(data.msgArray.length == 0) {
					var tblRow = "<tr><td colspan=13 style='text-align:center;color:#ff0000'><b><h2>" + GROUP_NOSTOCK + "</h2></b></td></tr>";
					var noData = jQuery(tblRow);
					jQuery('#group1 tbody').append(noData);
				}
				$.each(data.msgArray, function(i, item) {
					// allStockList.push([ item.ex, item.ch, item.n ]);
					// console.log(allStockList);
					if(i < noPage) {
						cStr = item.ch.substring(0, item.ch.indexOf("."));
						var vType = ''; // 設定預設分類為國內成分證券ETF
						var refURL = '';
						for(j = 0; j <= ETFType.length - 1; j++) {
							if(ETFType[j][0] == cStr) {
								// TODO 根據股票代號尾碼判斷是否為外幣 ETF
								vType = isForeignCurrency(cStr) ? ETFType[j][1] + "K" : ETFType[j][1];
								refURL = ETFType[j][2];
								break;
							}
						}
						// alert(cStr);
						// alert(vType);
						var stkName = item.n;
						if(stkName.length > 16) {
							stkName = item.n.substring(0, 16) + '...';
						}
						var tblRow = "<tr id='" + cStr + "_tr'><td id='" + cStr + "_n' style='text-align:left;'>" + "<a href='fibest.jsp?goback=1&stock=" + cStr + "' title='" + item.n + "' class='linkTip'>" + ((ind == "TIDX" || ind == "OIDX") ? "" : cStr) + " / " + stkName + "</a>";
						tblRow = tblRow + "</td><td id='" + cStr + "_c' >-</td>" + "<td id='" + cStr + "_d' >-</td>" + "<td id='" + cStr + "_e' >-</td>" + "<td id='" + cStr + "_f' >-</td>" + "<td id='" + cStr + "_g' >-</td>" + "<td id='" + cStr + "_h' >-</td>";
						if((typeof refURL != 'undefined') && (refURL != '')) {
							tblRow = tblRow + "<td><a href='" + refURL + "' title='" + GROUP_ETF_ESTIMATED_LINK + "' target='_blank'>" + ALL_ETF_LINK_STR + "</a></td>";
						} else {
							tblRow = tblRow + "<td>-</td>";
						}
						tblRow = tblRow + "<td id='" + cStr + "_i' >-</td></tr>";
						currList += item.ex + "_" + item.ch + "|";
						jQuery('#group' + vType + ' tbody').append(tblRow);
					}
				});
				$("#group1").show();
				$("#group2").show();
				$("#group3").show();
				$("#group4").show();
				$("#group1K").show();
				$("#group2K").show();
				$("#group3K").show();
				$("#group4K").show();
				loadStockInfo();
			} else {}
		});
	});
	// FIXME it was here
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

function loadStockInfo() {
	if(currList.length == 0) {
		return;
	}
	$.getJSON(srcUrl + "?" + $.now(), function(data) {
        data = JSON.parse(filterXSS(JSON.stringify(data)));
		for(idata = 0; idata <= data.a1.length - 1; idata++) {
			pdata = data.a1[idata];
			if(pdata.rtCode == "0000" || pdata.rtcode == "0000") {
				if(localRefId == -1) {
					var userDelay = 15000;
					if(typeof(pdata.userDelay) != 'undefined') {
						userDelay = pdata.userDelay;
					}
					localRefId = setInterval(function() {
						loadStockInfo()
					}, userDelay);
				}
				if(userDelay0 == -1) {
					userDelay0 = (pdata.userDelay);
					$("#userDelay0").text(userDelay0 / 1000);
				}
				if(pdata.userDelay == 0) {
					$("#userDelayView").hide();
				}
				$.each(pdata.msgArray, function(i, item) {
					try {
						cStr = $.trim(item.a);
						if(typeof item.c != 'undefined') {
							$("#" + cStr + "_c").text(addCommas(item.c));
						}
						if(typeof item.d != 'undefined') {
							$("#" + cStr + "_d").text(addCommas(item.d));
						}
						if(!isNaN(item.e)) {
							$("#" + cStr + "_e").text((item.e / 1).toFixed(2));
						} else {
							$("#" + cStr + "_e").text((item.e));
						}
						if(!isNaN(item.f)) {
							$("#" + cStr + "_f").text((item.f / 1).toFixed(2));
						} else {
							$("#" + cStr + "_f").text((item.f));
						}
						if(!isNaN(item.g)) {
							$("#" + cStr + "_g").text((item.g / 1).toFixed(2) + "%");
						} else {
							$("#" + cStr + "_g").text((item.g));
						}
						if(typeof item.h != 'undefined') {
							if(!isNaN(item.h)) {
								var _h = item.h / 1;
								_h = _h.toFixed(2);
								$("#" + cStr + "_h").text((item.h / 1).toFixed(2));
							} else {
								$("#" + cStr + "_h").text((item.h));
							}
						}
						if(item.i != '') {
							$("#" + cStr + "_i").text(item.i.substring(0, 4) + '/' + item.i.substring(4, 6) + '/' + item.i.substring(6, 8) + '-' + item.j);
						}
					} catch(err) {}
				});
			}
			$("#group1").trigger("update");
			$("#group2").trigger("update");
			$("#group3").trigger("update");
			$("#group4").trigger("update");
			$("#group1K").trigger("update");
			$("#group2K").trigger("update");
			$("#group3K").trigger("update");
			$("#group4K").trigger("update");
			// $("#group1").tablesorter();
			// $("#group2").tablesorter();
			// $("#group3").tablesorter();
			// $("#group4").tablesorter();
		}
	});
}

function clearTableContent() {
	var container = ["group1", "group2", "group3", "group4", "group1K", "group2K", "group3K", "group4K"];
	for(var i = 0; i < container.length; i++) {
		$("#" + container[i] + " tbody tr").each(function() {
			this.parentNode.removeChild(this);
		});
	}
}

function isForeignCurrency(stockId) {
	// FIXME 舊版 IE 不支援 endsWith()...
	var lastChar = stockId.substr(stockId.length - 1, 1).toUpperCase();
	return(lastChar == "K") || (lastChar == "M") || (lastChar == "S") || (lastChar == "V") || (lastChar == "C");
}