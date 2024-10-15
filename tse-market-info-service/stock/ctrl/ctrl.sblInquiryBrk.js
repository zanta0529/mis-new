var sblBrkJson;
var showArray = [];
var noPage = 10;
var p = 0;
var stockNo = "";
var brokerNo = "";
$(document).ready(function() {
	$('#sblBrokerNo').focus();
	$.getJSON(apiBaseUrl + "getStockSblsBrk.jsp", function(data) {
		if(data.rtcode == "0000") {
			sblBrkJson = data.msgArray;
			$("#Pagination").pagination(data.msgArray.length, {
				next_text: NEXT_PAGE,
				first_text: FIRST_PAGE,
				last_text: LAST_PAGE,
				prev_text: PREV_PAGE
			});
			showTable();
		}
	});
	$('#sblStockNo').keyup(function() {
		stockNo = this.value;
		showTable();
		$('#sblStockNo').focus();
	});
	$('#sblBrokerNo').keyup(function() {
		brokerNo = this.value;
		showTable();
		$('#sblBrokerNo').focus();
	});
	$('#sblBrokerNo').keydown(function(event) {
		if(event.keyCode == 13) {
			event.preventDefault();
			return false;
		}
	});
	$('#sblStockNo').keydown(function(event) {
		if(event.keyCode == 13) {
			event.preventDefault();
			return false;
		}
	});
	loadPageChangeFunction();
});

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
	$("#sblBrkTable tbody tr").each(function() {
		this.parentNode.removeChild(this);
	});
	var idx = 1;
	showTempArray = [];
	showArray = [];
	$.each(sblBrkJson, function(i, itemArr) {
		$.each(itemArr, function(k, item) {
			if(stockNo.indexOf(" ") > 0) {
				var stockNoArr = stockNo.split(" ");
				for(j = 0; j < stockNoArr.length; j++) {
					if(item.stkno.indexOf(stockNoArr[j]) >= 0 || item.stkname.indexOf(stockNoArr[j]) >= 0 /*|| item.brkid.indexOf(stockNoArr[j])==0 || item.brkname.indexOf(stockNoArr[j])==0*/ ) {
						if(brokerNo.indexOf(" ") > 0) {
							var brokerNoArr = brokerNo.split(" ");
							for(k = 0; k < brokerNoArr.length; k++) {
								if(item.brkid.indexOf(brokerNoArr[k]) >= 0 || item.brkname.indexOf(brokerNoArr[k]) >= 0) {
									showArray.push([item.brkid + item.brkname, item.stkno, item.stkname, item.avishr, item.lonrate, item.feerate, item.mark]);
								}
							}
						} else {
							if(brokerNo.length == 0 || item.brkid.indexOf(brokerNo) >= 0 || item.brkname.indexOf(brokerNo) >= 0) {
								showArray.push([item.brkid + item.brkname, item.stkno, item.stkname, item.avishr, item.lonrate, item.feerate, item.mark]);
							}
						}
						break;
					}
				}
			} else {
				if(stockNo.length == 0 || item.stkno.indexOf(stockNo) >= 0 || item.stkname.indexOf(stockNo) >= 0 /*|| item.brkid.indexOf(stockNo)==0 || item.brkname.indexOf(stockNo)==0*/ ) {
					if(brokerNo.indexOf(" ") > 0) {
						var brokerNoArr = brokerNo.split(" ");
						for(k = 0; k < brokerNoArr.length; k++) {
							if(item.brkid.indexOf(brokerNoArr[k]) >= 0 || item.brkname.indexOf(brokerNoArr[k]) >= 0) {
								showArray.push([item.brkid + item.brkname, item.stkno, item.stkname, item.avishr, item.lonrate, item.feerate, item.mark]);
								break;
							}
						}
					} else {
						if(brokerNo.length == 0 || item.brkid.indexOf(brokerNo) >= 0 || item.brkname.indexOf(brokerNo) >= 0) {
							showArray.push([item.brkid + item.brkname, item.stkno, item.stkname, item.avishr, item.lonrate, item.feerate, item.mark]);
						}
					}
				}
			}
		});
	});
	var htmlStr = "";
	for(i = 0; i < noPage && i < showArray.length; i++) {
		htmlStr += "<tr>";
		htmlStr += "<td align='right'>" + (i + 1) + "</td>";
		htmlStr += "<td align='center'>" + showArray[i][0] + "</td>";
		htmlStr += "<td align='center'><a href='sblInquiry.jsp?stock=" + showArray[i][1] + "'>" + showArray[i][1] + "</a></td>";
		htmlStr += "<td align='center'><a href='sblInquiry.jsp?stock=" + showArray[i][1] + "'>" + showArray[i][2] + "</a></td>";
		htmlStr += "<td align='right'>" + addCommas(showArray[i][3]) + "</td>";
		htmlStr += "<td align='right'>" + showArray[i][4] + "</td>";
		htmlStr += "<td align='right'>" + showArray[i][5] + "</td>";
		htmlStr += "<td>" + showArray[i][6] + "</td>";
		htmlStr += "</tr>";
	}
	$("#totlaRecords").text(showArray.length);
	var tdataElement = jQuery(htmlStr);
	jQuery('#sblBrkTable tbody').append(tdataElement);
	$("#sblBrkTable").show();
	$("#Pagination").pagination(showArray.length, {
		items_per_page: noPage,
		callback: pageselectCallback,
		next_text: NEXT_PAGE,
		first_text: FIRST_PAGE,
		last_text: LAST_PAGE,
		prev_text: PREV_PAGE
	});
}

function pageselectCallback(page_index, jq) {
	if(page_index != p) {
		$("#sblBrkTable tbody tr").each(function() {
			this.parentNode.removeChild(this);
		});
		var htmlStr = "";
		for(i = page_index * noPage; i < (page_index + 1) * noPage && i < showArray.length; i++) {
			htmlStr += "<tr>";
			htmlStr += "<td align='right'>" + (i + 1) + "</td>";
			htmlStr += "<td align='center'>" + showArray[i][0] + "</td>";
			htmlStr += "<td align='center'><a href='sblInquiry.jsp?stock=" + showArray[i][1] + "'>" + showArray[i][1] + "</a></td>";
			htmlStr += "<td align='center'><a href='sblInquiry.jsp?stock=" + showArray[i][1] + "'>" + showArray[i][2] + "</a></td>";
			htmlStr += "<td align='right'>" + addCommas(showArray[i][3]) + "</td>";
			htmlStr += "<td align='right'>" + showArray[i][4] + "</td>";
			htmlStr += "<td align='right'>" + showArray[i][5] + "</td>";
			htmlStr += "<td>" + showArray[i][6] + "</td>";
			htmlStr += "</tr>";
		}
		var tdataElement = jQuery(htmlStr);
		jQuery('#sblBrkTable tbody').append(tdataElement);
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