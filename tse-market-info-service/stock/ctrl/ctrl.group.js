var userDelay0 = -1;
var ex = "";
var ind = "";
var bp = '0';
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
today = new Date();
$.ajaxSetup({
    cache: false
});
var localRefId = -1;
var localRefId_list = -1;
var tmpLocalRefId = -1;
var gQueryParams = $.parseQuery();
//--------------------------------------------------------------------------
function changeToOdd() {
    $('#CNT_TRADE_NOTE').text(CNT_TRADE_NOTE_ODD);
    $('#btnChangeToContinues').show();
    $('#btnChangeToOdd').hide();
    $('#labContinuesQuotation').hide();
    $('#labOddQuotation').show();
    $('#group').hide();
    $('#group_odd').show();
    if (lang == 'zh_tw') {
        $('#thHightPrice').text('最高');
        $('#thLowestPrice').text('最低');
    }
    gIsOdd = true;
}

function changeToContinues() {
    $('#CNT_TRADE_NOTE').text(CNT_TRADE_NOTE);
    $('#btnChangeToContinues').hide();
    $('#btnChangeToOdd').show();
    $('#labContinuesQuotation').show();
    $('#labOddQuotation').hide();
    $('#group_odd').hide();
    $('#group').show();
    if (lang == 'zh_tw') {
        $('#thHightPrice').text('當日最高');
        $('#thLowestPrice').text('當日最低');
    }
    gIsOdd = false;
}
//--------------------------------------------------------------------------
ex = gQueryParams.ex;
ind = gQueryParams.ind;
bp = gQueryParams.bp == undefined ? bp : gQueryParams.bp;
currPage = gQueryParams.currPage == undefined ? currPage : parseInt(gQueryParams.currPage);
noPage = gQueryParams.noPage == undefined ? noPage : parseInt(gQueryParams.noPage);
gIsOdd = gQueryParams.oddFlag == undefined ? gIsOdd : (gQueryParams.oddFlag === 'true');
type = gQueryParams.type;
//--------------------------------------------------------------------------
function loadGroupList() {
    //--------------------------------------------------------------------------
    if (type == 'all') {
        if (gIsOdd) {
            $('#group_odd').show();
            changeToOdd();
        } else {
            $('#group').show();
            changeToContinues();
        }
        $('#btnChangeToOdd').click(function() {
            changeToOdd();
            loadStockList();
        });
        $('#btnChangeToContinues').click(function() {
            changeToContinues();
            loadStockList();
        });
    }
    //--------------------------------------------------------------------------
    $("#exMarket").tabs();
    $("#exMarket").bind("tabsselect", function(e, tab) {
        var tempFist = [];
        if (tab.index == 0) {
            ex = "tse";
            tempFist = tseFist;
        } else if (tab.index == 1) {
            ex = "otc";
            tempFist = otcFist;
        }
        ind = tempFist[0];
        indName = tempFist[1];
        loadStockList();
    });

    $("#tseSelect").change(function() {
        var tempStr = $(this).val();
        $('#otcSelect option:eq(0)').attr('selected', 'selected')
        p = 0;
        ex = "tse";
        currList = "";
        currPage = 0;
        allStockList = [];
        ind = tempStr.substring(0, tempStr.indexOf("-"));
        indName = tempStr.substring(tempStr.indexOf("-") + 1);
        loadStockList();
    });

    $("#otcSelect").change(function() {
        var tempStr = $(this).val();
        $('#tseSelect option:eq(0)').attr('selected', 'selected')
        p = 0;
        ex = "otc";
        currList = "";
        currPage = 0;
        allStockList = [];
        ind = tempStr.substring(0, tempStr.indexOf("-"));
        indName = tempStr.substring(tempStr.indexOf("-") + 1);
        loadStockList();
    });

    $("#otcWarrantSelect").change(function() {
        var tempStr = $(this).val();
        $('#tseWarrantSelect option:eq(0)').attr('selected', 'selected')
        p = 0;
        ex = "otc";
        currList = "";
        currPage = 0;
        allStockList = [];
        ind = tempStr.substring(0, tempStr.indexOf("-"));
        indName = tempStr.substring(tempStr.indexOf("-") + 1);
        loadStockList();
    });

    $("#tseWarrantSelect").change(function() {
        var tempStr = $(this).val();
        $('#otcWarrantSelect option:eq(0)').attr('selected', 'selected')
        p = 0;
        ex = "tse";
        currList = "";
        currPage = 0;
        allStockList = [];
        ind = tempStr.substring(0, tempStr.indexOf("-"));
        indName = tempStr.substring(tempStr.indexOf("-") + 1);
        loadStockList();
    });
    $.getJSON(apiBaseUrl + "getIndustry.jsp", function(data) {
        $.each(data.tse, function(i, item) {
            if (type == "null" || type == "all") {
                if (item.code == "Z3") {
                    return;
                }
                ind = (ind == "" || ind == "null") ? "01" : ind;

                if (ex == "tse") {
                    opt = genOpt(item, ind);
                } else {
                    opt = genOpt(item, "");
                }
                $("#tseSelect").append(opt);
                if ((ex == "tse") && item.code == ind) {
                    tseFist[0] = item.code;
                    tseFist[1] = item.name;
                    indName = item.name;
                    loadStockList();
                }
            } else if (type == "fixed") {
                if ((ex == "tse") && item.code == ind) {
                    tseFist[0] = item.code;
                    tseFist[1] = item.name;
                    ind = item.code;
                    indName = item.name;
                    loadStockList();
                }
            }
            if (type == "warrant") {
                if (item.code.charAt(0) == "D") {
                    if (ex == "tse")
                        opt = genOpt(item, ind);
                    else
                        opt = genOpt(item, "");
                    $("#tseWarrantSelect").append(opt);
                    if ((ex == "tse") && item.code == ind) {
                        tseFist[0] = item.code;
                        tseFist[1] = item.name;
                        indName = item.name;
                        loadStockList();
                    }
                }
            }
        });
        $.each(data.otc, function(i, item) {
            if (type == "null" || type == "all") {
                if (item.code == "Z3") {
                    return;
                }
                if (ex == "otc") {
                    opt = genOpt(item, ind);
                } else {
                    opt = genOpt(item, "");
                }
                $("#otcSelect").append(opt);
                if ((ex == "otc") && item.code == ind) {
                    tseFist[0] = item.code;
                    tseFist[1] = item.name;
                    indName = item.name;
                    loadStockList();
                }
            } else if (type == "fixed") {
                if (item.code == "Z3") {
                    return;
                }
                if ((ex == "otc") && item.code == ind) {
                    otcFist[0] = item.code;
                    otcFist[1] = item.name;
                }
            } else if (type == "warrant") {
                if (item.code.charAt(0) == "D") {
                    if (ex == "otc") {
                        opt = genOpt(item, ind);
                    } else {
                        opt = genOpt(item, "");
                    }
                    $("#otcWarrantSelect").append(opt);
                    if ((ex == "otc") && item.code == ind) {
                        ex = "otc";
                        tseFist[0] = item.code;
                        tseFist[1] = item.name;
                        ind = item.code;
                        indName = item.name;
                        loadStockList();
                    }
                }
            }
        });
    });
    $("#Pagination").pagination(0, {
        next_text: "下一頁",
        prev_text: "上一頁"
    });
}

function loadPageChangeFunction() {
    $("#prePage").change(function() {
        switch (parseInt($(this).val())) {
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
        // 暫緩收盤
        if (type == "postponed") {
            newLoadPostponedStockList('4');
        } else if (type == "postponedOpening") {
            newLoadPostponedStockList('5');
        } else {
            loadStockList();
        }
    });
}

function loadSummaryMarket() {
    loadSummaryMarketJson();
}

var localSumRefId = -1;

function loadSummaryMarketJson() {
    // 移除興櫃戰略新板（參數n）
    var seqArr1;
    if(ex == "tse") {
        seqArr1 = new Array("t", "f", "n", "s", "c", "b");
    } else {
        seqArr1 = new Array("t", "f", "s", "c", "b");
    }

    var seqArr2 = new Array("z", "v", "r", "4", "2", "3", "1", "u4", "u2", "u3", "u1", "w4", "w2", "w3", "w1");
    $.getJSON(apiBaseUrl + "getStatis.jsp?ex=" + ex + "&delay=0", function(data) {
        if (data.rtcode == "0000") {
            for ($i = 0; $i < seqArr1.length; $i++) {
                for ($j = 0; $j < seqArr2.length; $j++) {
                    var k = seqArr1[$i] + seqArr2[$j];
                    if (typeof(data.detail[k]) != "undefined") {
                        $("#" + k).text(addCommas(data.detail[k]));
                    }
                }
            }
            $("#tradeTime").text(data.detail["%"]);
            $("#orderTime").text(data.detail["%"]);
            if (localSumRefId == -1) {
                localSumRefId = setInterval(function() {
                    loadSummaryMarketJson()
                }, data.userDelay);
            }
        }
    });
}

function pageselectCallback(page_index, jq) {
    url = "?ex=" + ex + "&currPage=" + page_index;

    if (ind != "undefined") {
        url = url + "&ind=" + ind;
    }
    if (bp != "undefined") {
        url = url + "&bp=" + bp;
    }
    if (type != "undefined") {
        url = url + "&type=" + type;
    } else {
        url = url + "&type=all";
    }

    try {
        window.history.pushState({}, 0, 'https://' + window.location.host + '/stock/group.jsp' + url);
    } catch (err) {}

    clearInterval(tmpLocalRefId);
    clearInterval(localRefId);
    userDelay0 = -1;
    localRefId = -1;

    if (page_index != p) {
        var newList = "";
        $("#group tbody tr").each(function() {
            this.parentNode.removeChild(this);
        });
        $("#group_odd tbody tr").each(function() {
            this.parentNode.removeChild(this);
        });

        var offset = 0;
        for (i = page_index * noPage; i < (page_index * 1 + 1) * noPage && i < allStockList.length; i++) {
            newList += allStockList[i][0] + "_" + allStockList[i][1] + "|";
            cStr = allStockList[i][1].substring(0, allStockList[i][1].indexOf("."));

            if (allStockList[i].length < 3) {
                abbrName = '';
            } else {
                abbrName = allStockList[i][2];
            }
            if (type == 'all' && gIsOdd) {
                var $tr = $(document.createElement("tr")).appendTo("#group_odd tbody");
            } else {
                var $tr = $(document.createElement("tr")).appendTo("#group tbody");
            }

            $tr.attr("id", cStr + "_tr");

            if (type == "postponed" || type == "postponedOpening") {
                var $td = $(document.createElement("td")).appendTo($tr);
                $td.attr("id", cStr + "_ex").text("");
            }

            var $td = $(document.createElement("td")).appendTo($tr);
            $td.attr("id", cStr + "_n");
            $td.css("text-align", "left");
            var $a = $(document.createElement("a")).appendTo($td).addClass("linkTip");
            $a.text((ind == "TIDX" || ind == "OIDX") ? "" : cStr + " " + abbrName);
            $a.attr("href", "fibest.jsp?goback=1&stock=" + cStr + "&ind=" + ind + "&oddFlag=" + gIsOdd + "&currPage=" + page_index + "&noPage=" + noPage + "&type=" + type + "&bp=" + bp);
            $a.attr("title", ((ind == "TIDX" || ind == "OIDX") ? "" : cStr) + allStockList[i][3]);

            var $td = $(document.createElement("td")).appendTo($tr);
            $td.attr("id", cStr + "_z").text("-");

            var $td = $(document.createElement("td")).appendTo($tr);
            var $label = $(document.createElement("label")).appendTo($td);
            $label.attr("id", cStr + "_diff");
            $label.text("-");
            var $label = $(document.createElement("label")).appendTo($td);
            $label.attr("id", cStr + "_pre");
            $label.text("-");

            if (ind != "TIDX" && ind != "OIDX") {
                var $td = $(document.createElement("td")).appendTo($tr);
                $td.attr("id", cStr + "_tv").text("-");
                var $td = $(document.createElement("td")).appendTo($tr);
                $td.attr("id", cStr + "_v").text("-");

                if (type != "postponed" && type != "postponedOpening") {
                    var $td = $(document.createElement("td")).appendTo($tr);
                    $td.attr("id", cStr + "_Reference_price").text("-");
                    var $td = $(document.createElement("td")).appendTo($tr);
                    $td.attr("id", cStr + "_Reference_volume").text("-");
                }
                if (type == 'all' && gIsOdd) {
                    var $td = $(document.createElement("td")).appendTo($tr);
                    $td.attr("id", cStr + "_b").text("-");
                    var $td = $(document.createElement("td")).appendTo($tr);
                    $td.attr("id", cStr + "_g").text("-");
                    var $td = $(document.createElement("td")).appendTo($tr);
                    $td.attr("id", cStr + "_a").text("-");
                    var $td = $(document.createElement("td")).appendTo($tr);
                    $td.attr("id", cStr + "_f").text("-");
                }
            }
            if (!gIsOdd) {
                var $td = $(document.createElement("td")).appendTo($tr);
                $td.attr("id", cStr + "_o").text("-");
            }

            var $td = $(document.createElement("td")).appendTo($tr);
            $td.attr("id", cStr + "_h").text("-");

            var $td = $(document.createElement("td")).appendTo($tr);
            $td.attr("id", cStr + "_l").text("-");

            if ((ind == "TIDX" || ind == "OIDX")) {
                if (item.xm == "1") { // 跨市場指數
                    var $td = $(document.createElement("td")).appendTo($tr);
                    $td.attr("id", cStr + "_t").text("-");
                }
            } else {
                var $td = $(document.createElement("td")).appendTo($tr);
                $td.attr("id", cStr + "_t").text("-");
            }

            var $td = $(document.createElement("td")).appendTo($tr);
            $td.attr("id", cStr + "_comment");
            if (type == 'all' && gIsOdd) {
                $("#group_odd").show();
            } else {
                $("#group").show();
            }
        }
        p = page_index;
        currList = newList;
        loadStockInfo();
    }
}

function newLoadPostponedStockList(ip, isReloadDetail) {
    $.getJSON(apiBaseUrl + "getStockDelay.jsp?ex=tse&ip=" + ip, function(tseData) {
        if (tseData.rtcode == "0000") {
            $('#group tbody').empty();
            $('#group_odd tbody').empty();
            if (tseData.userDelay != '999999') {
                if (localRefId_list == -1) {
                    var userDelay = 5000;
                    if (tseData.userDelay != '999999') {
                        userDelay = tseData.userDelay;
                    }
                    localRefId_list = setInterval(function() {
                        newLoadPostponedStockList(ip, true)
                    }, userDelay);
                }
                // if(userDelay0==-1)
                {
                    userDelay0 = (tseData.userDelay);
                    if (userDelay0 < 5000) {
                        userDelay0 = 5000;
                    }
                    $("#userDelay0").text(userDelay0 / 1000);
                }
            } else {
                clearInterval(localRefId_list);
                localRefId_list = -1;
                isReloadDetail = false;
            }

            tseArray = [];
            otcArray = [];
            allArray = [];
            allStockList = [];
            $.each(tseData.result, function(key, value) {
                ex_ch = key.substring(0, key.indexOf(".tw") + 3);
                ex_ch_arr = ex_ch.split('_');
                msg = {
                    ch: ex_ch_arr[1],
                    ex: ex_ch_arr[0],
                    key: key
                };
                tseArray.push(msg);
            });
            tseArray.sort(function(a, b) {
                if (a.ch < b.ch)
                    return -1;
                if (a.ch > b.ch)
                    return 1;
                return 0;
            });

            $.getJSON(apiBaseUrl + "getStockDelay.jsp?ex=otc&ip=" + ip, function(otcData) {
                if (otcData.rtcode == "0000") {
                    $('#group tbody').empty();
                    $('#group_odd tbody').empty();
                    $.each(otcData.result, function(key, value) {
                        ex_ch = key.substring(0, key.indexOf(".tw") + 3);
                        ex_ch_arr = ex_ch.split('_');
                        msg = {
                            ch: ex_ch_arr[1],
                            ex: ex_ch_arr[0],
                            key: key
                        };
                        otcArray.push(msg);
                    });
                    otcArray.sort(function(a, b) {
                        if (a.ch < b.ch)
                            return -1;
                        if (a.ch > b.ch)
                            return 1;
                        return 0;
                    });
                    allArray = tseArray.concat(otcArray);
                    noStock = allArray.length;
                    $("#totlaRecords").text(noStock);
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
                    if (noStock == 0) {
                        $("#userDelayView").hide();
                    } else {
                        $("#userDelayView").show();
                    }

                    if (ip == '4') { // 暫緩收盤股票
                        $("#indName").text(POSTPONED_STOCK);
                    } else if (ip == '5') { // 暫緩開盤股票
                        $("#indName").text(POSTPONED_OPENING);
                    }

                    currList = "";

                    $.each(allArray, function(i, item) {
                        allStockList.push([item.ex, item.ch]);
                        if (i >= noPage * currPage && i < (noPage * currPage * 1) + noPage) {
                            cStr = item.ch.substring(0, item.ch.indexOf("."));

                            if (type == 'all' && gIsOdd) {
                                var $tr = $(document.createElement("tr")).appendTo("#group_odd tbody");
                            } else {
                                var $tr = $(document.createElement("tr")).appendTo("#group tbody");
                            }

                            $tr.attr("id", cStr + "_tr");
                            $tr.css("background-color", "#E7C1FF");

                            var $td = $(document.createElement("td")).appendTo($tr);
                            $td.attr("id", cStr + "_ex");
                            //$td.text(exStr);

                            var $td = $(document.createElement("td")).appendTo($tr);
                            $td.attr("id", cStr + "_n");
                            $td.css("text-align", "left");

                            var $a = $(document.createElement("a")).appendTo($td);
                            $a.attr("title", (ind == "TIDX" || ind == "OIDX") ? "" : cStr);
                            $a.attr("href", "fibest.jsp?goback=1&stock=" + cStr + "&ind=" + ind + "&type=" + type);
                            $a.text((ind == "TIDX" || ind == "OIDX") ? "" : cStr);

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

                            if (type == 'all' && gIsOdd) {
                                var $td = $(document.createElement("td")).appendTo($tr);
                                $td.attr("id", cStr + "_b").text("-");
                                var $td = $(document.createElement("td")).appendTo($tr);
                                $td.attr("id", cStr + "_g").text("-");
                                var $td = $(document.createElement("td")).appendTo($tr);
                                $td.attr("id", cStr + "_a").text("-");
                                var $td = $(document.createElement("td")).appendTo($tr);
                                $td.attr("id", cStr + "_f").text("-");
                            }
                            if (!gIsOdd) {
                                var $td = $(document.createElement("td")).appendTo($tr);
                                $td.attr("id", cStr + "_o").text("-");
                            }

                            var $td = $(document.createElement("td")).appendTo($tr);
                            $td.attr("id", cStr + "_h").text("-");

                            var $td = $(document.createElement("td")).appendTo($tr);
                            $td.attr("id", cStr + "_l").text("-");

                            var $td = $(document.createElement("td")).appendTo($tr);
                            $td.attr("id", cStr + "_t").text("-");

                            var $td = $(document.createElement("td")).appendTo($tr);
                            $td.attr("id", cStr + "_comment");

                            currList += item.ex + "_" + item.ch + "|";
                            if (type == 'all' && gIsOdd)
                                $("#group_odd").show();
                            else
                                $("#group").show();
                        }
                    });
                    if (type == 'all' && gIsOdd) {
                        $("#group_odd").trigger("update");
                        $("#group_odd").tablesorter();
                    } else {
                        $("#group").trigger("update");
                        $("#group").tablesorter();
                    }
                    loadStockInfo(isReloadDetail);
                }
            });
        }
    });
}

function loadStockList() {
    //--------------------------------------------------------------------------
    if (type != 'all') {
        gIsOdd = false;
    }
    switch (noPage) {
        case 10:
            $('#prePage').val('0');
            $('#selectBox option[value=0]').attr('selected', 'selected');
            break;
        case 20:
            $('#prePage').val('1');
            $('#selectBox option[value=1]').attr('selected', 'selected');
            break;
        case 50:
            $('#prePage').val('2');
            $('#selectBox option[value=2]').attr('selected', 'selected');
            break;
        case 100:
            $('#prePage').val('3');
            $('#selectBox option[value=3]').attr('selected', 'selected');
            break;
    }
    //--------------------------------------------------------------------------
    userDelay0 = -1;

    if (ex == "tse") {
        $(".marketName").text("[" + FIBEST_TSE + "]");
    } else if (ex == "otc") {
        if(bp == '03'){
            $(".marketName").text("[" + MARKET_PSB + "]");
        }else{
            $(".marketName").text("[" + FIBEST_OTC + "]");
        }
    }

    if ((ind == "TIDX" || ind == "OIDX")) {
        noPage = 100;
        $("#Pagination").hide();
        $("#Pagination1").hide();
        $(".group-footer").hide();
    }

    var url;
    if(bp == '03')
        url = "getCategory.jsp?ex=" + ex + "&bp=" + bp;
    else
        url = "getCategory.jsp?ex=" + ex + "&i=" + ind;
    $.getJSON(apiBaseUrl + url, function(data) {
        if (data.rtcode == "0000") {
            noStock = data.msgArray.length;
            if (noStock == 0) {
                $("#userDelayView").hide();
            } else {
                $("#userDelayView").show();
            }
            if (currPage == 'undefined') {
                currPage = 0;
            }
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
            if (indName != "") {
                $("#indName").text(indName);
            }
            $("#totlaRecords").text(noStock);
            currList = "";
            allStockList = [];
            $("#group tbody tr").each(function() {
                this.parentNode.removeChild(this);
            });
            $("#group_odd tbody tr").each(function() {
                this.parentNode.removeChild(this);
            });
            if (data.msgArray.length == 0) {
                if (type == 'all' && gIsOdd) {
                    var $tr = $(document.createElement("tr")).appendTo("#group_odd tbody");
                } else {
                    var $tr = $(document.createElement("tr")).appendTo("#group tbody");
                }
                var $td = $(document.createElement("td")).appendTo($tr);
                $td.attr("colspan", "16").css({
                    "text-align": "center",
                    "color": "#ff0000"
                });
                $td.html("<b><h2>" + GROUP_NOSTOCK + "</h2></b>");
            }

            $.each(data.msgArray, function(i, item) {
                var sName = item.n;
                if (ind != "TIDX" && ind != "OIDX") {
                    if (sName.length > 12) {
                        sName = sName.substring(0, 10) + "...";
                    }
                }

                allStockList.push([item.ex, item.ch, sName, item.nf]);
                if ((ind == "TIDX" || ind == "OIDX") || (i >= noPage * currPage && i < ((noPage * currPage * 1) + noPage))) {
                    cStr = item.ch.substring(0, item.ch.indexOf("."));

                    var $tr = $(document.createElement("tr"));
                    $tr.attr("id", cStr + "_tr");

                    var $td = $(document.createElement("td")).appendTo($tr);
                    $td.attr("id", cStr + "_n");
                    $td.css("text-align", "left");
                    var $a = $(document.createElement("a")).appendTo($td).addClass("linkTip");
                    $a.text((ind == "TIDX" || ind == "OIDX") ? sName : cStr + " " + sName);
                    $a.attr("href", "fibest.jsp?goback=1&stock=" + cStr + "&ind=" + ind + "&oddFlag=" + gIsOdd + "&currPage=" + currPage + "&noPage=" + noPage + "&type=" + type + "&bp=" + bp);
                    $a.attr("title", ((ind == "TIDX" || ind == "OIDX") ? "" : cStr) + allStockList[i][3]);

                    var $td = $(document.createElement("td")).appendTo($tr);
                    $td.attr("id", cStr + "_z").text("-");

                    var $td = $(document.createElement("td")).appendTo($tr);
                    var $label = $(document.createElement("label")).appendTo($td);
                    $label.attr("id", cStr + "_diff");
                    $label.text("-");
                    var $label = $(document.createElement("label")).appendTo($td);
                    $label.attr("id", cStr + "_pre");
                    $label.text("-");

                    if (ind != "TIDX" && ind != "OIDX") {
                        var $td = $(document.createElement("td")).appendTo($tr);
                        $td.attr("id", cStr + "_tv").text("-");
                        var $td = $(document.createElement("td")).appendTo($tr);
                        $td.attr("id", cStr + "_v").text("-");
                        var $td = $(document.createElement("td")).appendTo($tr);
                        $td.attr("id", cStr + "_Reference_price").text("-");
                        var $td = $(document.createElement("td")).appendTo($tr);
                        $td.attr("id", cStr + "_Reference_volume").text("-");
                        if (type == 'all' && gIsOdd) {
                            var $td = $(document.createElement("td")).appendTo($tr);
                            $td.attr("id", cStr + "_b").text("-");
                            var $td = $(document.createElement("td")).appendTo($tr);
                            $td.attr("id", cStr + "_g").text("-");
                            var $td = $(document.createElement("td")).appendTo($tr);
                            $td.attr("id", cStr + "_a").text("-");
                            var $td = $(document.createElement("td")).appendTo($tr);
                            $td.attr("id", cStr + "_f").text("-");
                        }
                    }
                    if (!gIsOdd) {
                        var $td = $(document.createElement("td")).appendTo($tr);
                        $td.attr("id", cStr + "_o").text("-");
                    }

                    var $td = $(document.createElement("td")).appendTo($tr);
                    $td.attr("id", cStr + "_h").text("-");

                    var $td = $(document.createElement("td")).appendTo($tr);
                    $td.attr("id", cStr + "_l").text("-");

                    if ((ind == "TIDX" || ind == "OIDX")) {
                        if (item.xm == "1") { // 跨市場指數
                            var $td = $(document.createElement("td")).appendTo($tr);
                            $td.attr("id", cStr + "_t").text("-");
                        }
                    } else {
                        var $td = $(document.createElement("td")).appendTo($tr);
                        $td.attr("id", cStr + "_t").text("-");
                    }

                    var $td = $(document.createElement("td")).appendTo($tr);
                    $td.attr("id", cStr + "_comment");

                    currList += item.ex + "_" + item.ch + "|";
                    if (cStr == ("TWTBI")) {
                        jQuery('#group_bonds tbody').append($tr);
                    } else if (item.xm == ("1")) { // [跨市場]指數
                        jQuery('#group_bonds2 tbody').append($tr);
                    } else if (item.xm == ("2")) { // TSE報酬指數
                        jQuery('#group_bonds3 tbody').append($tr);
                    } else if (item.xm == ("3")) { // OTC報酬指數
                        jQuery('#group_bonds4 tbody').append($tr);
                    } else {
                        if (type == 'all' && gIsOdd)
                            jQuery('#group_odd tbody').append($tr);
                        else
                            jQuery('#group tbody').append($tr);
                    }
                }
            });
            if (type == 'all' && gIsOdd)
                $("#group_odd").show();
            else
                $("#group").show();

            loadStockInfo();
        } else {}
    });
}

function insertFiBest(htmlView, values) {
    var valArr = values.split("_");
    for (var i = 0; i < valArr.length - 1 || i < 5; i++) {
        if (i == 0) {
            $(htmlView).text(valArr[0] == 'NaN' ? '-' : valArr[0]);
        }
    }
}

function loadStockInfo(isReloadDetail) {
    if (currList.length == 0) {
        return;
    }

    if (tmpLocalRefId != -1) {
        clearInterval(tmpLocalRefId);
        tmpLocalRefId = -1;
        userDelay0 = -1;
        localRefId = -1;
    }
    var url = '';
    if (type == 'all' && gIsOdd) {
        url = apiBaseUrl + "getOddInfo.jsp?ex_ch=" + currList + "&json=1&delay=0";
    } else {
        url = apiBaseUrl + "getStockInfo.jsp?ex_ch=" + currList + "&json=1&delay=0";
    }
    $.getJSON(url, function(data) {
        if (data.rtcode == "9999") {
            self.location.reload();
        } else if (data.rtcode == "0000") {
            if (data.userDelay == 500)
                self.location.reload();

            if (data.userDelay < 5000 && data.userDelay != 0) {
                if (!localRefId == -1) {
                    clearInterval(localRefId);
                }
                userDelay0 = -1;
                localRefId = -1;
            }

            if (!isReloadDetail) {
                if (localRefId == -1) {
                    if (tmpLocalRefId != -1) {
                        clearInterval(tmpLocalRefId);
                        tmpLocalRefId = -1;
                    }

                    var userDelay = 5000;
                    if (typeof(data.userDelay) != 'undefined' && data.userDelay != 0) {
                        userDelay = data.userDelay;
                    }

                    if (data.userDelay < 5000) {
                        setTimeout(function() {
                            loadStockInfo()
                        }, userDelay);
                    } else {
                        localRefId = setInterval(function() {
                            loadStockInfo()
                        }, userDelay);
                    }
                }
                // if(userDelay0==-1)
                {
                    userDelay0 = (data.userDelay);
                    if (userDelay0 < 5000) {
                        userDelay0 = 5000;
                    }
                    $("#userDelay0").text(userDelay0 / 1000);
                }
                if (data.userDelay == 0) {
                    $("#userDelayView").hide();
                } else {
                    $("#userDelayView").show();
                }
            }

            if (data.userDelay < 5000) {
                tmpLocalRefId = localRefId;
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

                if (item.b) { // "6.3800_6.3900_6.4000_6.4100_6.4200_"
                    var finalB = "";
                    var aryB = item.b.split("_");
                    for (var i = 0; i < aryB.length; i++) {
                        if (aryB[i] == null || aryB[i] == "") {
                            continue;
                        }
                        finalB += (parseFloat(aryB[i]).toFixed(2) + "_");
                    }
                    item.b = finalB;
                }
                if (item.a) { // "6.3800_6.3900_6.4000_6.4100_6.4200_"
                    var finalA = "";
                    var aryA = item.a.split("_");
                    for (var i = 0; i < aryA.length; i++) {
                        if (aryA[i] == null || aryA[i] == "") {
                            continue;
                        }
                        finalA += (parseFloat(aryA[i]).toFixed(2) + "_");
                    }
                    item.a = finalA;
                }
                // --- 價格欄位顯示到小數點後2位 ---

                cStr = item.c;
                // 暫緩收盤，開盤的市場別標示。
                if (type == "postponed" || type == "postponedOpening") {
                    var exStr = item.ex;
                    if(item.bp == '3'){
                        exStr = (exStr == "tse") ? POSTPONED_TIB : POSTPONED_PSB;
                    }else{
                        exStr = (exStr == "tse") ? POSTPONED_TSE : POSTPONED_OTC; 
                    }
                    $("#" + cStr + "_ex").text(exStr);
                }
                if ($("#" + cStr + "_n").text() == "-") {
                    var sName = item.n;
                    if ((ind != "TIDX" && ind != "OIDX")) {
                        if (sName.length > 10) {
                            sName = sName.substring(0, 10) + "...";
                        }
                    }
                    $("#" + cStr + "_n").text(item.c + " " + sName);
                }
                if ($("#" + cStr + "_h").text() != item.h) {
                    $("#" + cStr + "_h").text(item.h);
                }
                if ($("#" + cStr + "_l").text() != item.l) {
                    $("#" + cStr + "_l").text(item.l);
                }
                if (typeof(item.st) != "undefined" && typeof(item.rt) != "undefined") {
                    var stStr = item.st;
                    var rtStr = item.rt;
                    var tStr = data.queryTime.sysTime;

                    stStr = parseInt(stStr.replace(/\:/g, ""));
                    rtStr = parseInt(rtStr.replace(/\:/g, ""));
                    tStr = parseInt(tStr.replace(/\:/g, ""));
                    if (stStr == 80000 && rtStr == 999999) {
                        $("#" + cStr + "_tr").css("background-color", "#aaa");
                    } else if (tStr >= stStr && tStr <= rtStr) {
                        $("#" + cStr + "_tr").css("background-color", "#aaa");
                    } else {
                        $("#" + cStr + "_tr").css("background-color", "#f93");
                    }
                    if (item.rt == "99:99:99") {
                        item.rt = "13:30:00"
                    }
                    $("#" + cStr + "_comment").html(
                        "<img src='images/info.png' class='linkTip' title='" + TRADING_HALT + item.st + "\n" + TRADING_RESUME + item.rt + "'>"
                    );
                }
                $("#" + cStr + "_o").text(item.o);
                $("#" + cStr + "_y").text(item.y);
                $("#" + cStr + "_v").text(item.v);
                $("#" + cStr + "_t").text(item.t);

                // 先填初始值，資料進來如果有值再更新。
                $("#" + cStr + "_z").text('-');
                //$("#" + cStr + "_tv").text('-');
                $("#" + cStr + "_tv").text(item.s);
                $("#" + cStr + "_diff").text('-');
                $("#" + cStr + "_pre").text('-');
                $("#" + cStr + "_Reference_price").text('-');
                $("#" + cStr + "_Reference_volume").text('-');

                if (item.y != "-" && item.z != "-" && item.z != undefined) {
                    var diffStr = (item.z - item.y).toFixed(2);
                    var preStr = (diffStr / item.y * 100).toFixed(2);
                    highlightPriceChange("#" + cStr + "_z", item.z);
                    if (item.z * 1 < item.y * 1) {
                        $("#" + cStr + "_z").css("color", DOWN_COLOR);
                        $("#" + cStr + "_diff").css("color", DOWN_COLOR);
                        $("#" + cStr + "_pre").css("color", DOWN_COLOR);
                        $("#" + cStr + "_diff").text("▼" + (diffStr * -1));
                    } else if (item.z * 1 > item.y * 1) {
                        $("#" + cStr + "_z").css("color", UP_COLOR)
                        $("#" + cStr + "_diff").css("color", UP_COLOR);
                        $("#" + cStr + "_pre").css("color", UP_COLOR);
                        $("#" + cStr + "_diff").text("▲" + diffStr);
                    } else if (item.z == item.y) {
                        $("#" + cStr + "_z").css("color", "#000000")
                        $("#" + cStr + "_diff").css("color", "#000000");
                        $("#" + cStr + "_pre").css("color", "#000000");
                        $("#" + cStr + "_diff").text(diffStr);
                    }
                    if (item.tv != '-' && item.tv != undefined) {
                        $("#" + cStr + "_tv").text(item.tv);
                    }
                    if (item.z == item.u) {
                        $("#" + cStr + "_z").css("color", "#ffffff");
                        $("#" + cStr + "_z").css("background-color", UP_COLOR);
                    }
                    if (item.z == item.w) {
                        $("#" + cStr + "_z").css("color", "#ffffff");
                        $("#" + cStr + "_z").css("background-color", DOWN_COLOR);
                    }
                    $("#" + cStr + "_z").text(item.z);
                    $("#" + cStr + "_pre").text(" (" + preStr + "%)");
                }
                if (ind == "TIDX" || ind == "OIDX") {
                    if (typeof(item.z) == "undefined") {
                        $("#" + cStr + "_z").text(item.y);
                    }
                }

                // 趨漲↑
                if (item.ip == 2) {
                    $("#" + cStr + "_pre").text($("#" + cStr + "_pre").text() + "↑");
                }
                // 趨跌↓
                if (item.ip == 1) {
                    $("#" + cStr + "_pre").text($("#" + cStr + "_pre").text() + "↓");
                }
                // 暫緩收盤，開盤
                if (type == "postponed" || type == "postponedOpening") {
                    if (type == "postponed") {
                        $("#" + cStr + "_comment").text(POSTPONED_STRING);
                    } else if (type == "postponedOpening") {
                        $("#" + cStr + "_comment").text(POSTPONED_OPENING_STRING);
                    }
                    $("#" + cStr + "_tr").css("background-color", "#E7C1FF");
                }
                // 試算標示
                if (item.ts == '1') {
                    // TODO 試算標示
                    $("#" + cStr + "_tr").css("background-color", "#D9FFFF");

                    // TODO 暫緩收盤，開盤
                    if (item.ip == 4 || item.ip == 5 || item.ip == 6) {
                        $("#" + cStr + "_tr").css("background-color", "#E7C1FF");
                    }

                    // TODO 試算價格與欄位
                    if (item.ps != undefined) {
                        $("#" + cStr + "_Reference_volume").text(item.ps);
                    }
                    if (item.pz != undefined) {
                        $("#" + cStr + "_Reference_price").text(item.pz);
                    }
                } else if (item.ts == '0') {
                    // TODO 取消試算標示
                    if (type != "postponed" && type != "postponedOpening") {
                        $("#" + cStr + "_tr").css("background-color", "");
                    }
                    $("#" + cStr + "_Reference_volume").text('-');
                    $("#" + cStr + "_Reference_price").text('-');
                }
                if (item.g != undefined) {
                    insertFiBest("#" + cStr + "_g", item.g, item.y);
                }
                if (item.b != undefined) {
                    insertFiBest("#" + cStr + "_b", item.b, item.y, item.h, item.l, item.u, item.w);
                }
                if (item.a != undefined) {
                    insertFiBest("#" + cStr + "_a", item.a, item.y, item.h, item.l, item.u, item.w);
                }
                if (item.f != undefined) {
                    insertFiBest("#" + cStr + "_f", item.f, item.y);
                }

                if (ind == "TIDX" || ind == "OIDX") {
                    if (item.c == "o00" || item.c == "t00") {
                        $("#marketIndexTime").text(item.t + ")");
                        $(".marketIndexBondsTime").text(item.t + ")");
                    }
                }

                $("#" + cStr + "_comment").addClass("comment");
                if (type == "warrant") {
                    if (item.rch != undefined) {
                        if (item.rn.length > 6) {
                            $("#" + cStr + "_comment").html(
                                "<a href='fibest.jsp?goback=1&stock=" +
                                item.rch + "&ind=" + ind + "&type=" + type + "' title= " + item.rn + ">" +
                                item.rn.substring(0, 6) + "...</a>"
                            );
                        } else {
                            $("#" + cStr + "_comment").html(
                                "<a href='fibest.jsp?goback=1&stock=" +
                                item.rch + "&ind=" + ind + "&type=" + type + "'>" + item.rn + "</a>"
                            );
                        }
                    }
                }

                if (ind == "B0") {
                    if (item.nu != undefined) {
                        $("#" + cStr + "_link").html(
                            "<a href='" + item.nu + "' target='_blank'>" +
                            GROUP_ETF_ESTIMATED_LINK + "</a>"
                        );
                    } else {
                        $("#" + cStr + "_link").text("-");
                    }
                }

                if (type == "postponed" || type == "postponedOpening") {
                    $("#" + cStr + "_n").html(
                        "<a href='fibest.jsp?goback=1&stock=" + cStr + "&ind=" + ind + "&type=" + type + "' title='" + ((ind == "TIDX" || ind == "OIDX") ? "" : cStr) + item.nf + "'>" + ((ind == "TIDX" || ind == "OIDX") ? "" : cStr) + " " + item.n + "</td>"
                    );
                }
            });
            if (type == 'all' && gIsOdd) {
                $("#group_odd").trigger("update");
                $("#group_odd").tablesorter();
            } else {
                $("#group").trigger("update");
                $("#group").tablesorter();
            }
        }
        // isWait=false;
    });
}

function genOpt(item, selectItem) {
    if (item.code == selectItem) {
        opt = "<option value='" + item.code + "-" + item.name + "' selected>" + item.name + "</option>";
    } else {
        opt = "<option value='" + item.code + "-" + item.name + "'>" + item.name + "</option>";
    }
    return opt;
}

function createColumn() {

}