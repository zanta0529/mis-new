var closeTime = "";
var isWait = false;
var userDelay0 = -1;
var isShowChart = false;
var bp = '0';
var gShortStock = '';
var gStock = '';
var gDate = '';
var gEx = '';
var gInd = '';
var gName = '';

function initStock(stock) {
    var gQueryParams = $.parseQuery();
    gIsOdd = gQueryParams.oddFlag == undefined ? false : (gQueryParams.oddFlag === 'true');
    bp = gQueryParams.bp == undefined ? bp : gQueryParams.bp;
    gShortStock = stock;
    gInd = gQueryParams.ind;
    //--------------------------------------------------------------------------
    function initForOdd() {
        $('#btnChangeToContinues').show();
        $('#btnChangeToOdd').hide();
        $('#labContinuesQuotation').hide();
        $('#labOddQuotation').show();
        $('#latestTradePrice').show();
        $('#' + stock + '_z_odd').show();
        $('#priceChange').show();
        $('#' + stock + '_diff_pre').show();
        $('#preTradeVolume').show();
        $('#' + stock + '_tv_odd').show();
        /*$('#bidPrice').show();
        $('#'+stock+'_b').show();
        $('#bidVolume').show();
        $('#'+stock+'_g').show();
        $('#askPrice').show();
        $('#'+stock+'_a').show();
        $('#askVolume').show();
        $('#'+stock+'_f').show();*/
        $('#currentTime').show();
        $('#' + stock + '_t_odd').show();
        $('#fibestrow').css('background-color', '#CEFFCE');
        $('#tabTrade').hide();
        $('#thOpenPrice').hide();
        $('#' + stock + '_o').hide();
        if (lang == 'zh_tw') {
            $('#labTN').text('最佳五檔價量資訊揭示');
            $('#thHightPrice').text('最高');
            $('#thLowestPrice').text('最低');
        }
        $('#CNT_TRADE_NOTE').text(CNT_TRADE_NOTE_ODD);
        $('#refPrice').hide();
        $('#' + stock + '_Reference_price').hide();
        $('#refVolume').hide();
        $('#' + stock + '_Reference_volume').hide();
        $('#tabTradeOdd').show();
        $('#divRefOdd').show();
        insertFiBest("#" + stock + "_g", undefined);
        insertFiBest("#" + stock + "_b", undefined);
        insertFiBest("#" + stock + "_a", undefined);
        insertFiBest("#" + stock + "_f", undefined);
        gIsOdd = true;
    }

    function initForContinues() {
        $('#btnChangeToContinues').hide();
        $('#btnChangeToOdd').show();
        $('#labContinuesQuotation').show();
        $('#labOddQuotation').hide();
        $('#latestTradePrice').hide();
        $('#' + stock + '_z_odd').hide();
        $('#priceChange').hide();
        $('#' + stock + '_diff_pre').hide();
        $('#preTradeVolume').hide();
        $('#' + stock + '_tv_odd').hide();
        /*$('#bidPrice').hide();
        $('#'+stock+'_b').hide();
        $('#bidVolume').hide();
        $('#'+stock+'_g').hide();
        $('#askPrice').hide();
        $('#'+stock+'_a').hide();
        $('#askVolume').hide();
        $('#'+stock+'_f').hide();*/
        $('#currentTime').hide();
        $('#' + stock + '_t_odd').hide();
        $('#fibestrow').css('background-color', '');
        $('#tabTrade').show();
        $('#thOpenPrice').show();
        $('#' + stock + '_o').show();
        if (lang == 'zh_tw') {
            $('#labTN').text('成交價量及最佳五檔價量資訊揭示');
            $('#thHightPrice').text('當日最高');
            $('#thLowestPrice').text('當日最低');
        }
        $('#CNT_TRADE_NOTE').text(CNT_TRADE_NOTE);
        $('#refPrice').show();
        $('#' + stock + '_Reference_price').show();
        $('#refVolume').show();
        $('#' + stock + '_Reference_volume').show();
        $('#tabTradeOdd').hide();
        $('#divRefOdd').hide();
        gIsOdd = false;
    }
    //--------------------------------------------------------------------------
    if (!gIsOdd) {
        initForContinues();
    } else {
        initForOdd();
    }
    $("#btnChangeToOdd").click(function() {
        initForOdd();
        loadStockInfo(gStock, gDate);
    });
    $("#btnChangeToContinues").click(function() {
        initForContinues();
        loadStockInfo(gStock, gDate);
    });
    //--------------------------------------------------------------------------
    $("#goback").click(function() {
        if (gQueryParams.type == 'all' || gQueryParams.type == 'warrant' || gQueryParams.type == 'fixed') {
            document.location.href = 'group.jsp?ind=' + gInd + '&ex=' + gEx + '&currPage=' + gQueryParams.currPage + '&noPage=' + gQueryParams.noPage + '&type=' + gQueryParams.type + '&oddFlag=' + gIsOdd + '&bp=' + bp;
        } else if (gQueryParams.type == 'oddTrade') {
            document.location.href = 'oddTrade.jsp?ind=' + gInd + '&ex=' + gEx + '&currPage=' + gQueryParams.currPage + '&noPage=' + gQueryParams.noPage + '&bp=' + bp + '&st=' + gQueryParams.st;
        } else {
            parent.history.back();
        }
    });
    //--------------------------------------------------------------------------
    $("#print").click(function() {
        window.print();
    });
    /*
        $("#chart").tabs();

        $.getJSON(apiBaseUrl + "/getShowChart.jsp", function(data) {
            if (data.showchart) {
                $("#stockChart").css('visibility', 'visible');
                $("#stockChart").show();
                isShowChart = data.showchart;
            }
        });
    */
    $.getJSON(apiBaseUrl + "getStock.jsp?ch=" + stock + ".tw&json=1", function(data) {
        if (data.rtcode == "0000") {
            // alert(data.msgArray.length);
            if (data.msgArray.length == 0) {
                alert("查無股票名稱或代號");
            } else if (data.msgArray.length >= 1) {
                // alert(data.msgArray[0].key);
                gStock = data.msgArray[0].key;
                gDate = data.msgArray[0].d;
                gEx = data.msgArray[0].ex;
                //gInd = data.msgArray[0].i;ETF, 權證等，沒有i 值
                gName = data.msgArray[0].n;
                //i=tidx.tw 或 i=oidx.tw 不顯示零股切換
                if (gInd == 'TIDX' || gInd == 'OIDX'
                    //|| gQueryParams.type == 'warrant'
                    //|| gQueryParams.type == 'fixed'
                    //|| gQueryParams.type == 'postponedOpening'
                    //|| gQueryParams.type == 'postponed'
                ) {
                    $('#btnChangeToContinues').hide();
                    $('#btnChangeToOdd').hide();
                    $('#labContinuesQuotation').hide();
                    $('#labOddQuotation').hide();
                }
                if(data.msgArray[0].bp == '3'){
                    $('#btnChangeToContinues').hide();
                    $('#btnChangeToOdd').hide();
                }
                loadStockInfo(gStock, gDate);
            }
        }
    });
}

var localRefId = -1;
var tmpLocalRefId = -1;

function loadStockInfo(stock, d0) {
    /*
     * if(isWait) { return; } isWait=true;
     */
    // param= (userDelay0==-1)? "":"&delay="+userDelay0;
    var url = '';
    if (gIsOdd) {
        url = apiBaseUrl + "getOddInfo.jsp?ex_ch=" + stock + "&json=1&delay=0";
    } else {
        url = apiBaseUrl + "getStockInfo.jsp?ex_ch=" + stock + "&json=1&delay=0";
    }
    $.getJSON(url, function(data) {
        if (data.rtcode == "0000") {
            if (tmpLocalRefId != -1) {
                clearInterval(tmpLocalRefId);
                tmpLocalRefId = -1;
            }

            if (userDelay0 == -1) {
                userDelay0 = (data.userDelay);
                $("#userDelay0").text(userDelay0 / 1000);
            }
            if (data.userDelay == 0) {
                $("#userDelayView").hide();
            }
            if (localRefId == -1) {
                if (parseInt(data.userDelay) == 0) {
                    data.userDelay = 125;
                }
                localRefId = setInterval(function() {
                    loadStockInfo(stock, d0)
                }, data.userDelay);
                if (data.userDelay < 2000) {
                    tmpLocalRefId = localRefId;
                    userDelay0 = -1;
                    localRefId = -1;
                }

            }
            var upper = 0;
            var lower = 0;
            var yesPrice = 0;
            $('.oddObj').text('-');
            if (data.msgArray.length == 0) {
                exStr = (gEx == "tse") ? FIBEST_TSE : FIBEST_OTC;
                $("#" + gShortStock + "_n").text("[" + exStr + "]" + "  " + gShortStock + "  " + gName);
            }
            $.each(data.msgArray, function(i, item) {
                // --- 價格欄位顯示到小數點後2位 ---
                if (!isNaN(item.z)) {
                    item.z = parseFloat(item.z).toFixed(2);
                }
                if (!isNaN(item.pz)) {
                    item.pz = parseFloat(item.pz).toFixed(2);
                }
                if (!isNaN(item.o)) {
                    item.o = parseFloat(item.o).toFixed(2);
                }
                if (!isNaN(item.h)) {
                    item.h = parseFloat(item.h).toFixed(2);
                }
                if (!isNaN(item.l)) {
                    item.l = parseFloat(item.l).toFixed(2);
                }

                if (item.a && item.a != "-" && item.a.indexOf("_") > 0) { // "6.3800_6.3900_6.4000_6.4100_6.4200_"
                    var finalA = "";
                    var aryA = item.a.split("_");
                    for (var i = 0; i < aryA.length; i++) {
                        if (aryA[i] == null || aryA[i] == "") {
                            continue;
                        }
                        var price = parseFloat(aryA[i]).toFixed(2);
                        finalA += (price == 0) ? MARKET_ORDER + "_" : price + "_";
                    }
                    item.a = finalA;
                }

                if (item.b && item.b != "-" && item.b.indexOf("_") > 0) { // "6.3800_6.3900_6.4000_6.4100_6.4200_"
                    var finalB = "";
                    var aryB = item.b.split("_");
                    for (var i = 0; i < aryB.length; i++) {
                        if (aryB[i] == null || aryB[i] == "") {
                            continue;
                        }
                        var price = parseFloat(aryB[i]).toFixed(2);
                        finalB += (price == 0) ? MARKET_ORDER + "_" : price + "_";
                    }
                    item.b = finalB;
                }
                // --- 價格欄位顯示到小數點後2位 ---

                if (item.i == "oidx.tw" || item.i == "tidx.tw") {
                    $("#bestFiveDiv").hide();
                }
                cStr = item.c;
                // title name
                if ($("#" + cStr + "_n").text() == "") {
                    var exStr = item.ex;
                    if(item.bp == '3'){
                        exStr = (item.ex == "tse") ? FIBEST_TIB : FIBEST_PSB;
                    }else{
                        if (item.xm == "1")
                            exStr = FIBEST_XM;
                        else
                            exStr = (item.ex == "tse") ? FIBEST_TSE : FIBEST_OTC;
                    }
                    $("#" + cStr + "_n").text("[" + exStr + "]" + "  " + item.c + "  " + item.n);
                }

                // 最高
                if ($("#" + cStr + "_h").text() != item.h) {
                    $("#" + cStr + "_h").text(item.h);
                }
                // 最低
                if ($("#" + cStr + "_l").text() != item.l) {
                    $("#" + cStr + "_l").text(item.l);
                }
                if (typeof(item.st) != "undefined" && typeof(item.rt) != "undefined") {
                    var stStr = item.st;
                    var rtStr = item.rt;
                    var tStr = item.t + "";
                    stStr = parseInt(stStr.replace(/\:/g, ""));
                    rtStr = parseInt(rtStr.replace(/\:/g, ""));
                    tStr = parseInt(tStr.replace(/\:/g, ""));
                    if (stStr == 80000 && rtStr == 999999) {
                        $("#fibestrow").css("background-color", "#aaa");
                        $("#title_note").text(" (" + TRADING_HALT + ")");
                    } else if (tStr >= stStr && tStr <= rtStr) {
                        $("#fibestrow").css("background-color", "#aaa");
                        $("#title_note").text(" (" + TRADING_HALT + ")");
                    } else {
                        $("#fibestrow").css("background-color", "#f93");
                        $("#title_note").text(" (" + TRADING_RESUME + ")");
                    }
                    if (item.rt == "99:99:99") {
                        item.rt = "13:30:00"
                    }
                    $("#" + cStr + "_comment").html(
                        "<img src='images/info.png' class='linkTip' title='" +
                        TRADING_HALT + item.st + "\n" +
                        TRADING_RESUME + item.rt + "'>"
                    );
                }

                // 開盤
                $("#" + cStr + "_o").text(item.o);
                // 沒顯示
                $("#" + cStr + "_y").text(item.y);
                // 累積成交量
                $("#" + cStr + "_v").text(item.v);
                // 揭示時間
                $("#" + cStr + "_t").text(item.t);
                //零股成交時間
                $("#" + cStr + "_t_odd").text(item.tt);

                //先填初始值，資料進來如果有值再更新。
                $("#" + cStr + "_z").text(item.z);
                $("#" + cStr + "_tv").text(item.s);
                $("#" + cStr + "_diff").text('-');
                $("#" + cStr + "_pre").text('-');
                $("#" + cStr + "_z_odd").text(item.z);
                $("#" + cStr + "_tv_odd").text(item.s);
                $("#" + cStr + "_diff_odd").text('-');
                $("#" + cStr + "_pre_odd").text('-');
                $("#" + cStr + "_Reference_price").text('-');
                $("#" + cStr + "_Reference_volume").text('-');
                $("#" + cStr + "_Reference_price_odd").text('-');
                $("#" + cStr + "_Reference_volume_odd").text('-');
                //零股試算時間
                $("#" + cStr + "_Reference_time_odd").text('-');

                if (typeof(item.z) != 'undefined' && item.y != "-" && item.z != "-") {
                    var diffStr = (item.z - item.y).toFixed(2);
                    var preStr = (diffStr / item.y * 100).toFixed(2);
                    // preStr = (isNaN(preStr)) ? "-" : preStr;
                    diffStr = (isNaN(diffStr)) ? "-" : diffStr;
                    // 漲跌價差
                    $("#" + cStr + "_diff").text(diffStr);
                    $("#" + cStr + "_diff_odd").text(diffStr);
                    // 漲跌價差百分比
                    // $("#" + cStr + "_pre").text(preStr + "%");
                    $("#" + cStr + "_pre").text("(" + preStr + "%)");
                    $("#" + cStr + "_pre_odd").text("(" + preStr + "%)");
                    highlightPriceChange("#" + cStr + "_z", item.z);
                    highlightPriceChange("#" + cStr + "_z_odd", item.z);
                    // 最近成交價
                    $("#" + cStr + "_z").text(item.z);
                    $("#" + cStr + "_z_odd").text(item.z);
                    if (item.z * 1 < item.y * 1) {
                        $("#" + cStr + "_z").css("color", "#105010");
                        $("#" + cStr + "_diff").css("color", "#105010");
                        $("#" + cStr + "_pre").css("color", "#105010");
                        $("#" + cStr + "_diff").text("▼" + $("#" + cStr + "_diff").text());
                        $("#" + cStr + "_z_odd").css("color", "#105010");
                        $("#" + cStr + "_diff_odd").css("color", "#105010");
                        $("#" + cStr + "_pre_odd").css("color", "#105010");
                        $("#" + cStr + "_diff_odd").text("▼" + $("#" + cStr + "_diff_odd").text());
                    } else if (item.z * 1 > item.y * 1) {
                        $("#" + cStr + "_z").css("color", "#ff0000")
                        $("#" + cStr + "_diff").css("color", "#ff0000");
                        $("#" + cStr + "_pre").css("color", "#ff0000");
                        $("#" + cStr + "_diff").text("▲" + $("#" + cStr + "_diff").text());
                        $("#" + cStr + "_z_odd").css("color", "#ff0000")
                        $("#" + cStr + "_diff_odd").css("color", "#ff0000");
                        $("#" + cStr + "_pre_odd").css("color", "#ff0000");
                        $("#" + cStr + "_diff_odd").text("▲" + $("#" + cStr + "_diff_odd").text());
                    } else if (item.z == item.y) {
                        $("#" + cStr + "_z").css("color", "#000000")
                        $("#" + cStr + "_diff").css("color", "#000000");
                        $("#" + cStr + "_pre").css("color", "#000000");
                        $("#" + cStr + "_z_odd").css("color", "#000000")
                        $("#" + cStr + "_diff_odd").css("color", "#000000");
                        $("#" + cStr + "_pre_odd").css("color", "#000000");
                    }
                    if (item.z == item.u) {
                        $("#" + cStr + "_z").css("color", "#ffffff");
                        $("#" + cStr + "_z").css("background-color", "#ff0000");
                        $("#" + cStr + "_z_odd").css("color", "#ffffff");
                        $("#" + cStr + "_z_odd").css("background-color", "#ff0000");
                        // $("#"+cStr+"_z").text("▲"+item.z);
                    }
                    if (item.z == item.w) {
                        $("#" + cStr + "_z").css("color", "#ffffff");
                        $("#" + cStr + "_z").css("background-color", "#105010");
                        $("#" + cStr + "_z_odd").css("color", "#ffffff");
                        $("#" + cStr + "_z_odd").css("background-color", "#105010");
                        // $("#"+cStr+"_z").text("▼"+item.z);
                    }
                    // 當盤成交量
                    if (item.s != undefined) {
                        $("#" + cStr + "_tv").text(item.s);
                        $("#" + cStr + "_tv_odd").text(item.s);
                    }
                }
                // 趨漲↑
                if (item.ip == 2) {
                    $("#" + cStr + "_pre").text($("#" + cStr + "_pre").text() + "↑");
                    $("#" + cStr + "_pre_odd").text($("#" + cStr + "_pre_odd").text() + "↑");
                }
                // 趨跌↓
                if (item.ip == 1) {
                    $("#" + cStr + "_pre").text($("#" + cStr + "_pre").text() + "↓");
                    $("#" + cStr + "_pre_odd").text($("#" + cStr + "_pre_odd").text() + "↓");
                }

                // alert("item.g="+item.g);
                if (item.i != "tidx.tw" && item.i != "oidx.tw") {
                    // 揭示買量
                    insertFiBest("#" + cStr + "_g", item.g, item.y);
                    // 揭示買價
                    insertFiBest("#" + cStr + "_b", item.b, item.y, item.h, item.l, item.u, item.w);
                    // 揭示賣價
                    insertFiBest("#" + cStr + "_a", item.a, item.y, item.h, item.l, item.u, item.w);
                    // 揭示賣量
                    insertFiBest("#" + cStr + "_f", item.f, item.y);
                } else {
                    $(".title5").hide();
                    $("#fiBestDiv").hide();
                    $("#fiBestDivTitle").hide();
                }
                // 試算標示
                if (item.ts == '1') {
                    if (gIsOdd) {
                        $("#title_note_odd").html(" <span style='color: red; font-size: 80%;'>(" + REFERENCE_ODD + ")</span>");
                        $("#fibest_title_note").html(" <span style='color: red; font-size: 80%;'>(" + REFERENCE_ODD + ")</span>");
                    } else {
                        $("#title_note").html(" <span style='color: red; font-size: 80%;'>(" + REFERENCE + ")</span>");
                        $("#fibest_title_note").html(" <span style='color: red; font-size: 80%;'>(" + REFERENCE + ")</span>");
                    }

                    // 暫緩收盤
                    if (item.ip == 4) {
                        $("#fibestrow").css("background-color", "#E7C1FF");
                        $("#hor-minimalist-tb").css("background-color", "#E7C1FF");
                        $("#" + cStr + "_comment").html(
                            "<img src='images/info.png' class='linkTip' title='" + POSTPONED_STRING + "'>"
                        );
                        $("#title_note").text(" (" + POSTPONED_STRING + ")");
                    }
                    // 暫緩開盤
                    if (item.ip == 5) {
                        $("#fibestrow").css("background-color", "#E7C1FF");
                        $("#hor-minimalist-tb").css("background-color", "#E7C1FF");
                        $("#" + cStr + "_comment").html(
                            "<img src='images/info.png' class='linkTip' title='" + POSTPONED_OPENING + "'>"
                        );
                        $("#title_note").text(" (" + POSTPONED_OPENING + ")");
                    }

                    if (item.ps != undefined) {
                        $("#" + cStr + "_Reference_volume").text(item.ps);
                        $("#" + cStr + "_Reference_volume_odd").text(item.ps);
                    }
                    if (item.pz != undefined) {
                        $("#" + cStr + "_Reference_price").text(item.pz);
                        $("#" + cStr + "_Reference_price_odd").text(item.pz);
                    }
                    //零股試算時間
                    $("#" + cStr + "_Reference_time_odd").text(item.qt);
                } else if (item.ts == '0') {
                    if (gIsOdd) {
                        $("#fibestrow").css("background-color", "#CEFFCE");
                        $("#fibest_title_note").html(" <span style='color: red; font-size: 80%;'>(" + ODD_LATEST + ")</span>");
                    } else {
                        $("#fibestrow").css("background-color", "#FFFFFF");
                        $("#fibest_title_note").text('');
                    }
                    $("#hor-minimalist-tb").css("background-color", "#FFFFFF");
                    $("#" + cStr + "_comment").html('');
                    $("#title_note").text('');
                    $("#title_note_odd").text('');
                    $("#" + cStr + "_Reference_volume").text("-");
                    $("#" + cStr + "_Reference_price").text("-");
                    $("#" + cStr + "_Reference_volume_odd").text("-");
                    $("#" + cStr + "_Reference_price_odd").text("-");
                    //零股試算時間
                    $("#" + cStr + "_Reference_time_odd").text("-");
                } else {
                    if (gIsOdd) {
                        $("#fibest_title_note").html(" <span style='color: red; font-size: 80%;'>(" + ODD_LATEST + ")</span>");
                    } else {
                        $("#fibest_title_note").text('');
                    }
                }
                upper = (item.u);
                lower = (item.w);
                if (upper == undefined) {
                    upper = item.h;
                }
                if (lower == undefined) {
                    lower = item.l;
                }
                yesPrice = (item.y);
            });
            if (isShowChart) {
                loadChart(stock, d0, upper, lower, yesPrice);
                loadTechChart(stock);
            }
        } else {
            setTimeout(function() {
                self.location.reload()
            }, 500);
        }
        // isWait=false;
    });
}

function insertFiBest(htmlView, values, y, h, l, u, w) {
    var valArr;
    if (values == undefined) {
        valArr = ['-'];
    } else {
        var valArr = values.split("_");
    }
    for (i = 0; i < valArr.length - 1 || i < 5; i++) {
        // 揭示價量
        if (i == 0) {
            $(htmlView).text(valArr[0]);
        }
        // 5 檔資訊
        if (i < valArr.length - 1) {
            $(htmlView + i).text(valArr[i]);
        } else if (i >= valArr.length - 1 && i < 5) {
            $(htmlView + i).text("-");
        }
        if (htmlView.indexOf("b") > 0) {
            leftHtmlView = htmlView.substring(0, htmlView.length - 2) + "_l" + i;
            $(leftHtmlView).text("");
        }
        if (htmlView.indexOf("a") > 0) {
            leftHtmlView = htmlView.substring(0, htmlView.length - 2) + "_r" + i;
            $(leftHtmlView).text("");
        }
        if (h > 0 && valArr[i] == h && htmlView.indexOf("b") > 0) {
            leftHtmlView = htmlView.substring(0, htmlView.length - 2) + "_l" + i;
            $(leftHtmlView).text(HIGHEST);
        }
        if (l > 0 && valArr[i] == l && htmlView.indexOf("b") > 0) {
            leftHtmlView = htmlView.substring(0, htmlView.length - 2) + "_l" + i;
            $(leftHtmlView).text(LOWEST);
        }
        if (h > 0 && valArr[i] == h && htmlView.indexOf("a") > 0) {
            leftHtmlView = htmlView.substring(0, htmlView.length - 2) + "_r" + i;
            $(leftHtmlView).text(HIGHEST);
        }
        if (l > 0 && valArr[i] == l && htmlView.indexOf("a") > 0) {
            leftHtmlView = htmlView.substring(0, htmlView.length - 2) + "_r" + i;
            $(leftHtmlView).text(LOWEST);
        }
        if (htmlView.indexOf("b") > 0 || htmlView.indexOf("a") > 0) {
            $(htmlView + i).css('background-color', '');
            if (valArr[i] != undefined) {
                if (valArr[i] == u) {
                    $(htmlView + i).css("color", "#ffffff");
                    $(htmlView + i).css("background-color", "#ff0000");
                } else if (valArr[i] == w) {
                    $(htmlView + i).css("color", "#ffffff");
                    $(htmlView + i).css("background-color", "#105010");
                } else if (y * 1 > valArr[i] * 1) {
                    $(htmlView + i).css("color", "#105010");
                } else if (y * 1 < valArr[i] * 1) {
                    $(htmlView + i).css("color", "#ff0000");
                } else if (y == valArr[i]) {
                    $(htmlView + i).css("color", "#000000");
                }
            }
        }
    }
}

var chart;
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
    // series.data.push(parseFloat(item));
    // alert("loadChart="+channel);
    $.getJSON(apiBaseUrl + "getOhlc.jsp?ex_ch=" + channel + "&d0=" + d0, function(data) {
        var ohlc = [],
            volume = [],
            cPrice = [],
            dataLength = data.msgArray.length;
        var maxValume = 0;
        for (i = 0; i < dataLength; i++) {
            var currTime = parseInt(data.msgArray[i].tlong);
            if (i == 0) {
                var date = new Date(currTime);
                var minutes = date.getMinutes();
                var hours = date.getHours();
                var seconds = date.getSeconds();
                if (!(hours == 9 && minutes == 0)) {
                    date.setMinutes(0);
                    date.setHours(9);
                    cPrice.push([date.getTime(), // currentTime, // the date
                        yesPrice * 1 // the volume
                    ]);
                }
            }
            if (closeTime == "" && i == dataLength - 1) {
                var dt = new Date();
                dt.setTime(currTime);
                var dtB = new Date(dt.getFullYear(), dt.getMonth(), dt.getDate(), 13, 31, 0);
                closeTime = dtB.getTime();
            }
            cPrice.push([
                currTime, // currentTime, // the date
                data.msgArray[i].c * 1 // the volume
            ]);
            if (data.msgArray[i].s > maxValume) {
                maxValume = data.msgArray[i].s * 1;
            }
            volume.push([
                currTime, // currentTime, // the date
                data.msgArray[i].s * 1 // the volume
            ]);
        }
        loadOhcl(channel, cPrice, volume, upper, lower, maxValume, yesPrice);
    });
}

function loadTechChart(channel) {
    // series.data.push(parseFloat(item));
    // alert("loadChart="+channel);
    $.getJSON(apiBaseUrl + "getDailyRangeWithMA.jsp?ex_ch=" + channel + "&d0=" + techD0 + "&d1=" + techD1, function(data) {
        var ohlc = [],
            ma05 = [],
            ma10 = [],
            ma20 = [],
            volume = [],
            cPrice = [],
            dataLength = data.msgArray.length;
        var maxValume = 0;
        for (i = 0; i < dataLength; i++) {
            ohlc.push([(
                    data.msgArray[i].tlong) * 1,
                data.msgArray[i].o * 1, // open
                data.msgArray[i].h * 1, // high
                data.msgArray[i].l * 1, // low
                data.msgArray[i].z * 1 // close
            ]);
            if (data.msgArray[i].ma05 != 0) {
                ma05.push([(data.msgArray[i].tlong) * 1, data.msgArray[i].ma05 * 1]);
            }
            if (data.msgArray[i].ma10 != 0) {
                ma10.push([(data.msgArray[i].tlong) * 1, data.msgArray[i].ma10 * 1]);
            }
            if (data.msgArray[i].ma20 != 0) {
                ma20.push([(data.msgArray[i].tlong) * 1, data.msgArray[i].ma20 * 1]);
            }
            cPrice.push([(data.msgArray[i].tlong) * 1, data.msgArray[i].z * 1]);
            if (data.msgArray[i].s > maxValume) {
                maxValume = data.msgArray[i].s * 1;
            }
            volume.push([(data.msgArray[i].tlong) * 1, data.msgArray[i].v * 1]);
        }
        // alert("done");
        loadTechOhcl(channel, ohlc, ma05, ma10, ma20, volume);
    });
}

function loadOhcl(channel, cPrice, volume, upper, lower, maxValume, yesPrice) {
    var tkInt = parseFloat(((upper - lower) / 4).toFixed(2));
    upper = parseFloat(upper);
    lower = parseFloat(lower);
    var lower1 = parseFloat(lower + tkInt);
    var upper1 = parseFloat(upper - tkInt);
    upper = upper.toFixed(2);
    lower = lower.toFixed(2);
    lower1 = lower1.toFixed(2);
    upper1 = upper1.toFixed(2);
    if (chart == undefined) {
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
                    animation: false
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
                tickPositions: [lower, lower1, upper1, upper],
                plotLines: [{
                        value: yesPrice,
                        color: 'blue',
                        dashStyle: 'shortdash',
                        width: 1
                    }
                    /*
                     * ,{ value : upper, color : 'red', dashStyle : 'shortdash',
                     * width : 1 },{ value : lower, color : 'green', dashStyle :
                     * 'shortdash', width : 1 }
                     */
                ]
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
                type: 'line',
                name: "成交價",
                data: cPrice,
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
        // chart.redraw(true) ;
    } else {
        // alert(chart);
        // chart.series[0].series.hide();
        // chart.series[1].series.hide();
        chart.series[0].setData(cPrice, false);
        chart.series[1].setData(volume, false);
        chart.redraw();
        // chart.redraw(true) ;
        // chart.redraw();
        // chart.series[0].series.show();
        // chart.series[1].series.show();
    }
}

function loadKD(channel) {
    $.getJSON(apiBaseUrl + "getDailyRangeOnlyKD.jsp?ex_ch=" + channel + "&d0=" + techD0 + "&d1=" + techD1, function(data) {
        var kValue = [],
            dValue = [],
            dataLength = data.msgArray.length;
        for (i = 0; i < dataLength; i++) {
            kValue.push([data.msgArray[i].tlong * 1, data.msgArray[i].k * 1]);
            dValue.push([(data.msgArray[i].tlong) * 1, data.msgArray[i].d * 1]);
        }
        if (teChart != undefined) {
            // teChart.series[2].addPoint(kValue);
            /*
             * teChart.series.push({ type: 'line', name: "KD", data: kValue,
             * yAxis: 2 });
             */
            // alert("add done");
        }

    });
}

function loadTechOhcl(channel, ohlc, ma05, ma10, ma20, volume) {
    // alert(yesPrice);
    // set the allowed units for data grouping
    // alert("maxValume"+maxValume);
    // maxValume=maxValume;
    if (teChart == undefined) {
        teChart = new Highcharts.StockChart({
            chart: {
                renderTo: 'techChart',
                zoomType: 'x'
            },
            plotOptions: {
                candlestick: {
                    animation: false,
                    color: 'green',
                    upColor: 'red',
                    gapSize: 0
                        // dataGrouping:[['day',[1]]]
                },
                column: {
                    animation: false,
                    color: 'red',
                    gapSize: 0
                        // dataGrouping:[['day',[1]]]
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
            /*
             * plotOptions: { candlestick:{ dataGrouping:[['day',[1]]] } },
             */
            xAxis: {
                // max:closeTime,
                // minorTickInterval: 30*60*1000
            },
            yAxis: [{
                title: {
                    text: '成交價'
                },
                height: 300
                    // min:lower,
                    // max:upper,
                    // minRange:parseInt((upper-lower)/4),
                    // maxRange:parseInt((upper-lower)/4),
                    // tickInterval: parseInt((upper-lower)/4),
            }, {
                title: {
                    text: '成交量'
                },
                // min:0,
                top: 330,
                height: 80,
                offset: 0,
                tickColor: '#0000ff'
            }],
            series: [{
                type: 'candlestick',
                name: "成交價",
                data: ohlc
                    // dataGrouping:[['day',[1]]]
            }, {
                type: 'line',
                data: ma05,
                name: "5MA"
                    // dataGrouping:[['day',[1]]]
            }, {
                type: 'line',
                data: ma10,
                name: "10MA"
                    // dataGrouping:[['day',[1]]]
            }, {
                type: 'line',
                data: ma20,
                name: "20MA"
                    // dataGrouping:[['day',[1]]]
            }, {
                type: 'column',
                name: '成交量',
                data: volume,
                // dataGrouping:[['day',[1]]],
                yAxis: 1
            }]
        });
        // loadKD(channel);
        teChart.redraw(true);
    } else {
        teChart.series[0].setData(ohlc, false);
        teChart.series[1].setData(volume, false);
        teChart.redraw();
    }
}