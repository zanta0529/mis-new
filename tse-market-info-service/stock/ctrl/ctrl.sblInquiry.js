var refreshId;
var isWait = false;
var stock;
$(document).ready(function() {
	$.getJSON(apiBaseUrl + "getStockSblsList.jsp", function(data) {
		if(data.rtcode == "0000") {
			$('#stock').find('option').remove();
			$.each(data.msgArray, function(i, item) {
				var opt = "<option value='" + item.stkno + "'" + ((item.stkno == stock) ? " selected=\"selected\"" : "") + " >" + item.stkname + "(" + item.stkno + ")</option>";
				$("#stock").append(opt);
				if(i == 0 && stock == null) {
					initStock(item.stkno);
				}
			});
		}
	});
	$("select#stock").change(function() {
		initStock($(this).val());
	});
});

function initStock(stockNo) {
	stock = stockNo;
	if(stock == "null") {
		return;
	}
	clearInterval(refreshId);
	loadSBLInfo(stock);
}
var localRefId = -1;

function loadSBLInfo(stock0) {
	$.getJSON(apiBaseUrl + "getStockSbls.jsp?ch=" + stock0, function(data) {
		if(localRefId == -1) {
			var userDelay = 15000;
			if(typeof(data.userDelay) != 'undefined') {
				userDelay = data.userDelay;
			}
			localRefId = setInterval(function() {
				loadSBLInfo(stock)
			}, userDelay);
		}
		highlightItemChange($("#stkname"), data.detail.stkname);
		highlightItemChange($("#stkno"), data.detail.stkno);
		$("#stkname").text(data.detail.stkname);
		$("#stkno").text(data.detail.stkno);
		//if(data.rtcode!=9000)
		{
			showFixPrice("FA", data.FA.detail);
			showFixPrice("F3", data.F3.detail);
			showFixPrice("F1", data.F1.detail);
			showFixPrice("CA", data.CA.detail);
			showFixPrice("C3", data.C3.detail);
			showFixPrice("C1", data.C1.detail);
			showBorrowLend("CA5", data.CA_5);
			showBorrowLend("C35", data.C3_5);
			showBorrowLend("C15", data.C1_5);
			$("#N_qty").text(data.N.totmatchqty);
			$("#FA_TIME").text(data.FA.time);
			$("#CA_TIME").text(data.CA.time);
			$("#C3_TIME").text(data.C3.time);
			$("#C1_TIME").text(data.C1.time);
			$("#N_TIME").text(data.N.time);
		}
		//isWait=false;
	});
}

function showFixPrice(key, value) {
	for(var i = 0; i < value.length; i++) {
		highlightItemChange($("#" + key + "_" + i), value[i]);
		$("#" + key + "_" + i).text(value[i]);
	}
}

function showBorrowLend(key, value) {
	for(var i = 1; i <= 5; i++) {
		//alert($("#"+key+"_DF"+i)+"   =="+value["df"+i]);
		highlightItemChange($("#" + key + "_DF" + i), value["df" + i]);
		highlightItemChange($("#" + key + "_DQ" + i), value["dq" + i]);
		highlightItemChange($("#" + key + "_CF" + i), value["cf" + i]);
		highlightItemChange($("#" + key + "_CQ" + i), value["cq" + i]);
		$("#" + key + "_DF" + i).text(value["df" + i]);
		$("#" + key + "_DQ" + i).text(value["dq" + i]);
		$("#" + key + "_CF" + i).text(value["cf" + i]);
		$("#" + key + "_CQ" + i).text(value["cq" + i]);
	}
}