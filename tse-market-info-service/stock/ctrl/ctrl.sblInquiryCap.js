var sblCapJson;
var showArray = [];
var noPage = 10;
var p = 0;
var stockNo = "";
var userDelay0 = 5000;
var volSort = 0;
var timeSort = 0;
var stockSort = 0;
$(document).ready(function() {
	$('#sblStockNo').focus();
	$.getJSON(apiBaseUrl + "getStockSblsCap.jsp", function(data) {
		if(data.rtcode == "0000") {
			userDelay0 = (data.userDelay);
			if(userDelay0 < 5000) {
				userDelay0 = 5000;
			}
			sblCapJson = data.msgArray;
			$("#Pagination").pagination(data.msgArray.length, {
				next_text: NEXT_PAGE,
				first_text: FIRST_PAGE,
				last_text: LAST_PAGE,
				prev_text: PREV_PAGE
			});
			showTable("");
		}
	});
	$('#sblStockNo').keyup(function() {
		stockNo = this.value;
		showTable();
		$('#sblStockNo').focus();
	});
	$('#sblStockNo').keydown(function(event) {
		if(event.keyCode == 13) {
			event.preventDefault();
			return false;
		}
	});
	$("#volSort").click(function() {
		p = -1;
		if(volSort == 0) {
			showArray.sort(function(a, b) {
				return parseInt(a[1]) - parseInt(b[1])
			});
			volSort = 1;
		} else {
			showArray.sort(function(a, b) {
				return parseInt(b[1]) - parseInt(a[1])
			});
			volSort = 0;
		}
		pageselectCallback(0, null);
	});
	$("#stockSort").click(function() {
		// alert("time sort");
		p = -1;
		if(stockSort == 0) {
			showArray.sort(function(a, b) {
				return parseInt(a[0]) - parseInt(b[0])
			});
			stockSort = 1;
		} else {
			showArray.sort(function(a, b) {
				return parseInt(b[0]) - parseInt(a[0])
			});
			stockSort = 0;
		}
		pageselectCallback(0, null);
	});
	$("#timeSort").click(function() {
		// alert("time sort");
		p = -1;
		if(timeSort == 0) {
			showArray.sort(function(a, b) {
				return parseInt(a[3]) - parseInt(b[3])
			});
			timeSort = 1;
		} else {
			showArray.sort(function(a, b) {
				return parseInt(b[3]) - parseInt(a[3])
			});
			timeSort = 0;
		}
		pageselectCallback(0, null);
	});
	loadPageChangeFunction();
	loadStockSblsCap();
});
var localRefId = -1;

function loadStockSblsCap() {
	param = (userDelay0 == -1) ? "" : "?delay=" + userDelay0;
	$.getJSON(apiBaseUrl + "getStockSblsCap.jsp" + param, function(data) {
		if(data.rtcode == "0000") {
			$.each(data.msgArray, function(i, item) {
				if($("#" + item.stkno + "_no").length > 0) {
					$("#" + item.stkno + "_no").text(addCommas(item.slblimit));
					$("#" + item.stkno + "_time").text(item.txtime)
				}
			});
			if(localRefId == -1) {
				var userDelay = 15000;
				if(typeof(data.userDelay) != 'undefined') {
					userDelay = data.userDelay;
				}
				if(userDelay < 5000) {
					userDelay = 5000;
				}
				localRefId = setInterval(function() {
					loadStockSblsCap()
				}, userDelay);
			}
		}
	});
}

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
		showTable(stockNo);
	});
}

function showTable() {
	$("#sblCapTable tbody tr").each(function() {
		this.parentNode.removeChild(this);
	});
	var htmlStr = "";
	showArray = [];
	$.each(sblCapJson, function(i, item) {
		if(stockNo.indexOf(" ") >= 0) {
			var stockNoArr = stockNo.split(" ");
			for(j = 0; j < stockNoArr.length; j++) {
				if(item.stkno.indexOf(stockNoArr[j]) == 0) {
					showArray.push([item.stkno, item.slblimit, item.txtime, item.ts]);
					break;
				}
			}
		} else {
			if(stockNo.length == 0 || item.stkno.indexOf(stockNo) == 0) {
				showArray.push([item.stkno, item.slblimit, item.txtime, item.ts]);
			}
		}
	});
	for(i = 0; i < noPage && i < showArray.length; i++) {
		htmlStr += "<tr>";
		htmlStr += "<td align='right'>" + (i + 1) + "</td>";
		htmlStr += "<td align='center'><a href='sblInquiry.jsp?stock=" + showArray[i][0] + "'>" + showArray[i][0] + "</a></td>";
		htmlStr += "<td align='right' id='" + showArray[i][0] + "_no" + "'>" + addCommas(showArray[i][1]) + "</td>";
		htmlStr += "<td align='right' id='" + showArray[i][0] + "_time" + "'>" + showArray[i][2] + "</td>";
		htmlStr += "<td>" + "" + "</td>";
		htmlStr += "</tr>";
	}
	$("#totlaRecords").text(showArray.length);
	var tdataElement = jQuery(htmlStr);
	jQuery('#sblCapTable tbody').append(tdataElement);
	$("#Pagination").pagination(showArray.length, {
		items_per_page: noPage,
		callback: pageselectCallback,
		first_text: FIRST_PAGE,
		last_text: LAST_PAGE,
		next_text: NEXT_PAGE,
		prev_text: PREV_PAGE
	});
}

function pageselectCallback(page_index, jq) {
	if(page_index != p) {
		$("#sblCapTable tbody tr").each(function() {
			this.parentNode.removeChild(this);
		});
		var htmlStr = "";
		for(i = page_index * noPage; i < (page_index + 1) * noPage && i < showArray.length; i++) {
			htmlStr += "<tr>";
			htmlStr += "<td align='right'>" + (i + 1) + "</td>";
			htmlStr += "<td align='center'><a href='sblInquiry.jsp?stock=" + showArray[i][0] + "'>" + showArray[i][0] + "</a></td>";
			htmlStr += "<td align='right'  id='" + showArray[i][0] + "_no" + "'>" + addCommas(showArray[i][1]) + "</td>";
			htmlStr += "<td align='right'  id='" + showArray[i][0] + "_time" + "'>" + showArray[i][2] + "</td>";
			htmlStr += "<td>" + "" + "</td>";
			htmlStr += "</tr>";
		}
		var tdataElement = jQuery(htmlStr);
		jQuery('#sblCapTable tbody').append(tdataElement);
		p = page_index;
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