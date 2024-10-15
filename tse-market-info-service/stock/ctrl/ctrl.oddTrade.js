var ex = '';
var ind = '';
var bp = '0';
var st = 'i';
var indName = '';
var noPage = 10;
var currList = '';
var currPage = 0;
var noStock = 0;
var allStockList = [];
var tseFist = [];
var otcFist = [];
var isCat = false;
var allOddList = [];
var userDelay0 = -1;

var gQueryParams = $.parseQuery();
ex = gQueryParams.ex;
ind = gQueryParams.ind;
bp = gQueryParams.bp == undefined ? bp : gQueryParams.bp;
st = gQueryParams.st == undefined ? st : gQueryParams.st;
currPage = gQueryParams.currPage == undefined ? currPage : parseInt(gQueryParams.currPage);
noPage = gQueryParams.noPage == undefined ? noPage : parseInt(gQueryParams.noPage);
// =============================================================
function loadGroupList() {
	$('#exMarket').tabs();
	// =============================================================
	$.getJSON(apiBaseUrl + 'getIndustry.jsp?type=odd', function(data) {
		$.each(data.tse, function(i, item) {
			ind = (ind == "" || ind == "null") ? "01" : ind;
            st = (st == 'undefined') ? 'i' : st;
            if(ind == '01' && ex =='otc'){
                ex='tse';
            }
			if(ex == "tse")
                opt = genOpt(item, ind);
			else
                opt = genOpt(item, "");
			$('#tseSelect').append(opt);
			if((ex == "tse") && item.code == ind && item.st == st) {
				tseFist[0] = item.code;
				tseFist[1] = item.name;
				indName = item.name;
				loadStockList();
			}
		});
		$.each(data.otc, function(i, item) {
			if(ex == "otc")
                opt = genOpt(item, ind);
			else
                opt = genOpt(item, "");
			$("#otcSelect").append(opt);
			if((ex == "otc") && item.code == ind && item.st == st) {
				tseFist[0] = item.code;
				tseFist[1] = item.name;
				indName = item.name;
				loadStockList();
			}
		});
	});
	// =============================================================
	$('#exMarket').bind('tabsselect', function(e, tab) {
		var tempFist = [];
		if(tab.index == 0) {
			ex = 'tse';
			tempFist = tseFist;
		} else if(tab.index == 1) {
			ex = 'otc';
			tempFist = otcFist;
		}
		ind = tempFist[0];
		indName = tempFist[1];
		loadStockList();
	});
	// =============================================================
	$('#Pagination').pagination(0, {
		callback: pageselectCallback,
		current_page: currPage * 1,
		first_text: FIRST_PAGE,
		last_text: LAST_PAGE,
		next_text: NEXT_PAGE,
		prev_text: PREV_PAGE
	});
	// =============================================================
	$('#tseSelect').change(function() {
		isCat = true;
		var tempStr = $(this).val();
		$('#otcSelect').val('');
        $('#txtStockId').val('');
		ex = 'tse';
		currPage = 0;
		currList = '';
		allStockList = [];
        var strAry = tempStr.split(',');
        ind = strAry[0];
        indName = strAry[1];
        st = strAry[2];
        if(st == 'bp')
            bp = '03';
        else
            bp = '0';
		loadStockList();
	});
	// =============================================================
	$('#otcSelect').change(function() {
		isCat = true;
		var tempStr = $(this).val();
        $('#tseSelect').val('');
		$('#txtStockId').val('');
		ex = 'otc';
		currPage = 0;
		currList = '';
		allStockList = [];
        var strAry = tempStr.split(',');
        ind = strAry[0];
        indName = strAry[1];
        st = strAry[2];
        if(st == 'bp')
            bp = '03';
        else
            bp = '0';
		loadStockList();
	});
	// 單一股票零股資訊, 2014/12
	// =============================================================
	$('#getStock').click(function() {
		isCat = false;
		var stockId = filterXSS($.trim($('#txtStockId').val()));
		if(stockId.length > 0) {
			getStockId(stockId);
		}
	});
	// =============================================================
	$('#txtStockId').focus();
	$('#txtStockId').autocomplete({
		position: {
			at: 'left top',
			of: '#txtStockId_bar'
		}
	});
	$('#txtStockId').keyup(function(event) {
		isCat = false;
		var qryStr = $('#txtStockId').val();
        var clean = filterXSS($.trim(qryStr));
        if (clean && clean.length > 0) {
            searchStockForOdd(clean);
        }
	});
	$('#formOdd').submit(function(event) {
		event.preventDefault();
	});
	// =============================================================
	$('#prePage').change(function() {
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
		if(isCat) loadStockList();
	});
}
// =============================================================
function searchStockForOdd(qryStr) {
	$.getJSON(apiBaseUrl + 'getStockNames.jsp?n=' + encodeURIComponent(qryStr), function(data) {
		if(data.rtcode != '0000') return;
		var availableTags = new Array();
		$.each(data.datas, function(i, item) {
			availableTags.push({
				label: item.n + '[' + item.c + '] ',
				value: item.c
			});
		});
		if(availableTags.length > 0) {
			$('#txtStockId').autocomplete({
				source: availableTags,
				select: function(event, ui) {
					getStockId(ui.item.value);
					return true;
				}
			});
		}
	});
}
// =============================================================
function getStockId(stockId) {
    currPage = 0;
	/*
	 * isOdd = false; for (i = 0; i < allOddList.length; i++) { if
	 * (allOddList[i] == stockId + '.tw') isOdd = true; } if (!isOdd) {
	 * alert('此檔股票 [' + stockId + '] 無零股交易!!'); return; }
	 */
	$.getJSON(apiBaseUrl + 'getStock.jsp?ch=' + stockId + '.tw' + '&json=1', function(data) {
        data = JSON.parse(filterXSS(JSON.stringify(data)));
		if(data.rtcode == '0000') {
			if(data.msgArray.length == 0) {
				alert('查無股票名稱或代號');
			} else if(data.msgArray.length == 1) {
				currList = data.msgArray[0].ex + '_' + data.msgArray[0].ch;
				ex = data.msgArray[0].ex;
                bp = data.msgArray[0].bp;
				if (ex == 'tse') {
                    var market = (bp == '3') ? MARKET_TIB : FIBEST_TSE;
					$('#marketName').text('[' + market + ']');
				} else if (ex == 'otc') {
                    var market = (bp == '3') ? MARKET_PSB : FIBEST_OTC;
					$('#marketName').text('[' + market + ']');
				}
				$('#indName').text(data.msgArray[0].n);
				$('#totlaRecords').text(1);
				$('#group tbody tr').each(function() {
					this.parentNode.removeChild(this);
				});
				allStockList.push([data.msgArray[0].ex, data.msgArray[0].ch, data.msgArray[0].n]);
				var tblRow = printTable(stockId, data.msgArray[0].n);
				var tdataElement = jQuery(tblRow);
				jQuery('#group tbody').append(tdataElement);
				$('#group').show();
                $('#tseSelect').val('');
                $('#otcSelect').val('');
				ex = data.msgArray[0].ex;
				//ind = "01";
				$('#Pagination').pagination(1, {
					callback: pageselectCallback,
					items_per_page: 1,
					current_page: currPage * 1,
					next_text: NEXT_PAGE,
					prev_text: PREV_PAGE,
					first_text: FIRST_PAGE,
					last_text: LAST_PAGE
				});
				loadStockInfo();
			}
		}
	});
}
// =============================================================
function pageselectCallback(page_index, jq) {
	url = "?ex=" + ex + "&currPage=" + page_index;
    if (ind != "undefined") {
        url = url + "&ind=" + ind;
    }
    if (bp != "undefined") {
        url = url + "&bp=" + bp;
    }
    if (st != "undefined") {
        url = url + "&st=" + st;
    }
	try {
        window.history.pushState({}, 0, 'https://' + window.location.host + '/stock/oddTrade.jsp' + url);
	} catch(err) {};
	clearInterval(tmpLocalRefId);
	clearInterval(localRefId);
	userDelay0 = -1;
	localRefId = -1;
	// resetSession();
    if(page_index != currPage) {
		var newList = '';
		$('#group tbody tr').each(function() {
			this.parentNode.removeChild(this);
		});
        currPage = page_index;
		for(i = page_index * noPage; i < (page_index * 1 + 1) * noPage && i < allStockList.length; i++) {
			newList += allStockList[i][0] + '_' + allStockList[i][1] + '|';
			cStr = allStockList[i][1].substring(0, allStockList[i][1].indexOf('.'));
			var tblRow = printTable(cStr, allStockList[i][2]);
			var tdataElement = jQuery(tblRow);
			jQuery('#group tbody').append(tdataElement);
			$('#group').show();
		}
		currList = newList;
		loadStockInfo();
	}
}
// =============================================================
function loadStockList() {
	if(ex == 'tse')
        $('#marketName').text('[' + FIBEST_TSE + ']');
	else if(ex == 'otc'){
        if(bp == '03'){
            $("#marketName").text("[" + MARKET_PSB + "]");
        }else{
            $('#marketName').text('[' + FIBEST_OTC + ']');
        }
    }
	// resetSession();
    if(bp == '03')
        url = "getCategory.jsp?ex=" + ex + "&bp=" + bp + '&odd=1';
    else
        url = 'getCategory.jsp?ex=' + ex + '&i=' + ind + '&odd=1';
	$.getJSON(apiBaseUrl + url, function(data) {
        data = JSON.parse(filterXSS(JSON.stringify(data)));
		if(data.rtcode == '0000') {
			noStock = data.msgArray.length;
			noPage = (noPage == -1) ? noStock : noPage;
			// alert('noStock = ' + noStock + ' noPage = ' + noPage);
			$('#Pagination').pagination(noStock, {
				callback: pageselectCallback,
				items_per_page: noPage,
				current_page: currPage * 1,
				next_text: NEXT_PAGE,
				prev_text: PREV_PAGE,
				first_text: FIRST_PAGE,
				last_text: LAST_PAGE
			});
			$('#indName').text(indName);
			$('#totlaRecords').text(noStock);
			currList = '';
			allStockList = [];
			$('#group tbody tr').each(function() {
				this.parentNode.removeChild(this);
			});
			if(data.msgArray.length == 0) {
				var tblRow = '<tr ><td colspan=12  style="text-align:center;color:#ff0000"><b><h2>' + GROUP_NOSTOCK + '</h2></b></td></tr>';
				var tdataElement = jQuery(tblRow);
				jQuery('#group tbody').append(tdataElement);
			}
			$.each(data.msgArray, function(i, item) {
				allStockList.push([item.ex, item.ch, item.n]);
				if(i >= noPage * currPage && i < (noPage * currPage * 1) + noPage) {
					cStr = item.ch.substring(0, item.ch.indexOf('.'));
					var tblRow = printTable(cStr, item.n);
					currList += item.ex + '_' + item.ch + '|';
					var tdataElement = jQuery(tblRow);
					jQuery('#group tbody').append(tdataElement);
					$('#group').show();
				}
			});
			loadStockInfo();
		}
	});
}
// =============================================================
function printTable(stockId, stockName) {
	return '<tr id="' + stockId + '_tr">' +
        '<td id="' + stockId + '_n" style="text-align:left;"><a href="fibest.jsp?goback=1&stock=' + stockId + '&ind=' + ind + '&currPage=' + currPage + '&noPage=' + noPage + '&bp=' + bp + '&type=oddTrade' + '&st=' + st + '&lang=' + lang + '">' + '[' + stockId + ']' + stockName + '</td>' +
        '<td id="' + stockId + '_o">-</td>' +
        '<td id="' + stockId + '_h">-</td>' +
        '<td id="' + stockId + '_l">-</td>' +
        '<td id="' + stockId + '_z">-</td>' +
        '<td id="' + stockId + '_u">-</td>' +
        '<td id="' + stockId + '_w">-</td>' +
        '<td id="' + stockId + '_ot"  style="text-align:center">-</td>' +
        '<td id="' + stockId + '_oa">-</td>' +
        '<td id="' + stockId + '_ob">-</td>' +
        '<td id="' + stockId + '_oz">-</td>' +
        '<td id="' + stockId + '_ov">-</td>' +
        '</tr>';
}
// =============================================================
// var isWait=false;
var localRefId = -1;
var tmpLocalRefId = -1;

function loadStockInfo() {
	if(currList.length == 0) return;
	if(tmpLocalRefId != -1) {
		clearInterval(tmpLocalRefId);
		tmpLocalRefId = -1;
		userDelay0 = -1;
		localRefId = -1;
	}
	$.getJSON(apiBaseUrl + 'getStockInfo.jsp?ex_ch=' + currList + '&json=1', function(data) {
        data = JSON.parse(filterXSS(JSON.stringify(data)));
		if(data.rtcode == '0000') {
			if(tmpLocalRefId != -1) {
				clearInterval(tmpLocalRefId);
				tmpLocalRefId = -1;
			}
			if(localRefId == -1) {
				var userDelay = 15000;
				if(typeof(data.userDelay) != 'undefined') userDelay = data.userDelay;
				localRefId = setInterval(function() {
					loadStockInfo()
				}, userDelay);
			}
			if(userDelay0 == -1) {
				userDelay0 = (data.userDelay);
				$('#userDelay0').text(userDelay0 / 1000);
			}
			if(data.userDelay == 0) $('#userDelayView').hide();
			if(data.userDelay < 2000) {
				tmpLocalRefId = localRefId;
			}
			$.each(data.msgArray, function(i, item) {
				// --- 價格欄位顯示到小數點後2位 ---
				if(!isNaN(item.o)) {
					item.o = parseFloat(item.o).toFixed(2);
				}
				if(!isNaN(item.h)) {
					item.h = parseFloat(item.h).toFixed(2);
				}
				if(!isNaN(item.l)) {
					item.l = parseFloat(item.l).toFixed(2);
				}
				if(!isNaN(item.z)) {
					item.z = parseFloat(item.z).toFixed(2);
				}
				if(!isNaN(item.u)) {
					item.u = parseFloat(item.u).toFixed(2);
				}
				if(!isNaN(item.w)) {
					item.w = parseFloat(item.w).toFixed(2);
				}
				if(!isNaN(item.oa)) {
					item.oa = parseFloat(item.oa).toFixed(2);
				}
				if(!isNaN(item.ob)) {
					item.ob = parseFloat(item.ob).toFixed(2);
				}
				if(!isNaN(item.oz)) {
					item.oz = parseFloat(item.oz).toFixed(2);
				}
				// --- 價格欄位顯示到小數點後2位 ---
				cStr = item.c;
				if($('#' + cStr + '_n').text() == '-') $('#' + cStr + '_n').text(item.n + '[' + item.c + ']');
				if($('#' + cStr + '_h').text() != item.h) $('#' + cStr + '_h').text(item.h);
				if($('#' + cStr + '_l').text() != item.l) $('#' + cStr + '_l').text(item.l);
				if(typeof(item.st) != 'undefined' && typeof(item.rt) != 'undefined') {
					var stStr = item.st;
					var rtStr = item.rt;
					var tStr = item.t + '';
					stStr = parseInt(stStr.replace(/\:/g, ''));
					rtStr = parseInt(rtStr.replace(/\:/g, ''));
					tStr = parseInt(tStr.replace(/\:/g, ''));
					if(stStr == 80000 && rtStr == 999999) $('#' + cStr + '_tr').css('background-color', '#aaa');
					else if(tStr >= stStr && tStr <= rtStr) $('#' + cStr + '_tr').css('background-color', '#aaa');
					else $('#' + cStr + '_tr').css('background-color', '#f93');
				}
				$('#' + cStr + '_oa').text(item.ob);
				$('#' + cStr + '_ob').text(item.oa);
				$('#' + cStr + '_oz').text(item.oz);
				$('#' + cStr + '_ov').text(item.ov);
				$('#' + cStr + '_ot').text(item.ot);
				$('#' + cStr + '_u').text(item.u);
				$('#' + cStr + '_w').text(item.w);
				$('#' + cStr + '_o').text(item.o);
				$('#' + cStr + '_y').text(item.y);
				$('#' + cStr + '_v').text(item.v);
				$('#' + cStr + '_t').text(item.t);
				// alert(item.z);
				if(item.y != '-' && item.z != '-' && item.z != undefined) {
					var diffStr = (item.z - item.y).toFixed(2);
					var preStr = (diffStr / item.y * 100).toFixed(2);
					highlightPriceChange('#' + cStr + '_z', item.z);
					if(item.z * 1 < item.y * 1) {
						$('#' + cStr + '_z').css('color', '#105010');
						$('#' + cStr + '_diff').css('color', '#105010');
						$('#' + cStr + '_pre').css('color', '#105010');
						$('#' + cStr + '_diff').text('▼' + (diffStr * -1));
					} else if(item.z * 1 > item.y * 1) {
						$('#' + cStr + '_z').css('color', '#ff0000')
						$('#' + cStr + '_diff').css('color', '#ff0000');
						$('#' + cStr + '_pre').css('color', '#ff0000');
						$('#' + cStr + '_diff').text('▲' + diffStr);
					} else if(item.z == item.y) {
						$('#' + cStr + '_z').css('color', '#000000')
						$('#' + cStr + '_diff').css('color', '#000000');
						$('#' + cStr + '_pre').css('color', '#000000');
						$('#' + cStr + '_diff').text(diffStr);
					}
					if(item.z == item.u) {
						$('#' + cStr + '_z').css('color', '#ffffff');
						$('#' + cStr + '_z').css('background-color', '#ff0000')
					}
					if(item.z == item.w) {
						$('#' + cStr + '_z').css('color', '#ffffff');
						$('#' + cStr + '_z').css('background-color', '#105010')
					}
					$('#' + cStr + '_z').text(item.z);
					$('#' + cStr + '_pre').text(preStr + '%');
				}
				// alert('item.g = ' + item.g);
			});
		}
		// isWait = false;
	});
}

function genOpt(item, selectItem) {
	if(item.code == selectItem && item.st == st) opt = "<option value='" + item.code + "," + item.name + "," + item.st + "' selected>" + item.name + "</option>";
	else opt = "<option value='" + item.code + "," + item.name + "," + item.st + "'>" + item.name + "</option>";
	return opt;
}