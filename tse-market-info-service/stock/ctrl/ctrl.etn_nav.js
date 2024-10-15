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
var srcUrl = "data/all_etn.txt";

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
			if((typeof(refURL) != 'undefined') && (typeof(pdata.msgArray) != 'undefined') && (pdata.msgArray != null)) {
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
					var tblRow = "<tr ><td colspan=13 style='text-align:center;color:#ff0000'><b><h2>" + GROUP_NOSTOCK + "</h2></b></td></tr>";
					var tdataElement = jQuery(tblRow);
					jQuery('#group1 tbody').append(tdataElement);
				}
				$.each(data.msgArray, function(i, item) {
					// allStockList.push([ item.ex, item.ch, item.n ]);
					// alert(allStockList);
					if(i < noPage) {
						cStr = item.ch.substring(0, item.ch.indexOf("."));
						var vType = '1'; // 設定預設分類為國內成分證券ETN
						var refURL = '';
						for(j = 0; j <= ETFType.length - 1; j++) {
							if(ETFType[j][0] == cStr) {
								vType = ETFType[j][1];
								refURL = ETFType[j][2];
								break;
							}
						}
						// alert(cStr);
						// alert(vType);
						var stkName = item.n;
						if(stkName.length > 16) stkName = item.n.substring(0, 16) + '...';
						var tblRow = "<tr id='" + cStr + "_tr'><td id='" + cStr + "_n' style='text-align:left;'>" + "<a  href='fibest.jsp?goback=1&stock=" + cStr + "' title='" + item.n + "' class='linkTip'>" + ((ind == "TIDX" || ind == "OIDX") ? "" : cStr) + " / " + stkName + "</a>";
						tblRow = tblRow + "</td><td id='" + cStr + "_c' >-</td>" + "<td id='" + cStr + "_d' >-</td>" + "<td id='" + cStr + "_e' >-</td>" + "<td id='" + cStr + "_f' >-</td>" + "<td id='" + cStr + "_g' >-</td>" + "<td id='" + cStr + "_h' >-</td>";
						if((typeof refURL != 'undefined') & (refURL != '')) {
							tblRow = tblRow + "<td><a href='" + refURL + "' title='" + GROUP_ETN_ESTIMATED_LINK + "' target='_blank'>" + ALL_ETN_LINK_STR + "</a></td>";
						} else {
							tblRow = tblRow + "<td>-</td>";
						}
						tblRow = tblRow + "<td id='" + cStr + "_i' >-</td></tr>";
						currList += item.ex + "_" + item.ch + "|";
						var tdataElement = jQuery(tblRow);
						jQuery('#group' + vType + ' tbody').append(tdataElement);
					}
				});
				$("#group1").show();
				$("#group2").show();
				$("#group3").show();
				$("#group4").show();
				loadStockInfo();
			} else {}
		});
	});
	// It was here
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
				if(pdata.msgArray != null) {
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
								$("#" + cStr + "_f").text((item.f / 1).toFixed(4));
							} else {
								$("#" + cStr + "_f").text((item.f));
							}
							if(!isNaN(item.g)) {
								$("#" + cStr + "_g").text((item.g / 1).toFixed(4) + "%");
							} else {
								$("#" + cStr + "_g").text((item.g));
							}
							if(typeof item.h != 'undefined') {
								if(!isNaN(item.h)) {
									var _h = item.h / 1;
									_h = _h.toFixed(4);
									$("#" + cStr + "_h").text((item.h / 1).toFixed(4));
								} else {
									$("#" + cStr + "_h").text((item.h));
								}
							}
							if(item.i != '') {
								$("#" + cStr + "_i").text(item.i.substring(0, 4) + '/' + item.i.substring(4, 6) + '/' + item.i.substring(6, 8) + '-' + item.j);
							}
						} catch(err) {
							// console.log(err);
						}
					});
				}
			}
			$("#group1").trigger("update");
			$("#group2").trigger("update");
			$("#group3").trigger("update");
			$("#group4").trigger("update");
			// $("#group1").tablesorter();
			// $("#group2").tablesorter();
			// $("#group3").tablesorter();
			// $("#group4").tablesorter();
		}
	});
}

function clearTableContent() {
	var container = ["group1", "group2", "group3", "group4"];
	for(var i = 0; i < container.length; i++) {
		$("#" + container[i] + " tbody tr").each(function() {
			this.parentNode.removeChild(this);
		});
	}
}