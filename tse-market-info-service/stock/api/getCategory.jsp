<%@ page contentType="text/html; charset=UTF-8" errorPage="/api/error.jsp"%>
<%@ page import="com.ecloudlife.util.Utility"%>
<%@ page import="com.proco.datautil.*"%>
<%@ page import="com.ecloudlife.cass.logicutil.ExLastTradeDate"%>
<%@ page import="org.json.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.text.*"%>
<%! private static DateFormat df = new SimpleDateFormat("yyyyMMdd"); 
    static java.util.concurrent.ConcurrentHashMap<String, JSONObject> resCacheObject =  new java.util.concurrent.ConcurrentHashMap<String, JSONObject>();
    static java.util.concurrent.ConcurrentHashMap<String, Long> resCacheTime =  new java.util.concurrent.ConcurrentHashMap<String, Long>();
%><%
    response.setHeader("Pragma", "no-cache");
    response.setHeader("Cache-Control", "no-cache");
    response.setDateHeader("Expires", 0);
    com.ecloudlife.cass.logicutil.StockCategory.setQueryConf(queryConf,"JSP_Run");
    
    String ex = Utility.getHttpParameter(request, "ex"); //request.getParameter("ex");
    String is = Utility.getHttpParameter(request, "i"); //request.getParameter("i");
    String bp = Utility.getHttpParameter(request, "bp"); //request.getParameter("i");
    String date = Utility.getHttpParameter(request, "d"); //request.getParameter("d");
    String odd = Utility.getHttpParameter(request, "odd"); //request.getParameter("odd");
    String lang = Utility.getHttpParameter(request, "lang"); //request.getParameter("lang");

    if (lang == null) {
        lang = (String) session.getAttribute("lang");
    }
    if (lang == null) {
        lang = purifyString(request.getHeader("Accept-Language"), "zh-tw", 5);
        if (lang == null || "".equals(lang)) {
            lang = "zh-TW";
        }
        lang = lang.toLowerCase();
        if (lang.indexOf("zh-tw") != -1){
            lang = "zh-tw";
        } else if (lang.indexOf(",") != -1) {
            lang = lang.split(",")[0];
        }
    }
    lang = lang.split(",")[0];
    lang = lang.replaceAll("_", "-");
    session.setAttribute("lang", lang);

    if (ex == null || (is == null && bp==null)) {
        JSONObject j = new JSONObject();
        j.put("rtcode", "9999");
        j.put("rtmessage", "參數不足");
        out.print(j.toString());
        return;
    }
    if (is == null) is="";
    if (bp == null) bp="";
    if (is.equals("") && bp.equals("")) {
      JSONObject j = new JSONObject();
      j.put("rtcode", "9999");
      j.put("rtmessage", "參數不足");
      out.print(j.toString());
      return;
    }

    if (odd == null)
        odd = "0";
    if (!odd.equals("0"))
        odd = "1";
	if(date==null) date = ExLastTradeDate.getLastTradeDate(null, "tse");
	
    String ex_key = "gc_" + ex + "_" + lang + "." + is + "." + bp + "." + date + "." + odd;
    Long ct0 = resCacheTime.get(ex_key);
    if (ct0 != null) {
        JSONObject j1 = resCacheObject.get(ex_key);
        if ((System.currentTimeMillis() - ct0.longValue()) < 15000) {
            if (j1 != null) {
                j1.put("cachedAlive", (System.currentTimeMillis() - ct0.longValue()));
                j1.put("exKey",ex_key);
                out.print(j1.toString());
                return;
            }
        } else {
            if (j1 != null) {
                j1.put("cachedAlive", (System.currentTimeMillis() - ct0.longValue()));
                j1.put("exKey",ex_key);
                final String ex_key0 = ex_key;
                final String ex0 = ex;
                final String date0 = date;
                final String lang0 = lang;
                final String is0 = is;
                final String bp0 = bp;
                final String odd0 = odd;
                Thread th = new Thread(){
                	public void run(){
                		exec(ex_key0, ex0, date0, lang0, is0, bp0, odd0);
                	}
                };
                th.start();
                out.print(j1.toString());
                return;
            }   
        	//StockInfoManager.resCacheTime.remove(ex_key);
            //StockInfoManager.resCacheObject.remove(ex_key);
        }
    }

    JSONObject j = exec(ex_key, ex, date, lang, is, bp, odd);
    out.print(j.toString());
    if (true) return;
%><%!
    static JSONObject queryConf = new JSONObject();
    static JSONObject queryNewBoard = new JSONObject();
    static {
        try {
            {
                JSONArray ja = new JSONArray();
                JSONObject sub1 = new JSONObject();
                sub1.put("bp", "3");
                ja.put(sub1);
                queryNewBoard.put("03", ja);
            }
            {
                JSONArray ja = new JSONArray();
                JSONObject sub1 = new JSONObject();
                sub1.put("it", "00");
                ja.put(sub1);
                queryConf.put("A0", ja);
            }
            {
                JSONArray ja = new JSONArray();
                JSONObject sub1 = new JSONObject();
                sub1.put("it", "02");
                ja.put(sub1);
                queryConf.put("B0", ja);
            }
            {
                JSONArray ja = new JSONArray();
                JSONObject sub1 = new JSONObject();
                sub1.put("it", "27");
                ja.put(sub1);
                queryConf.put("B1", ja);
            }
            {
                JSONArray ja = new JSONArray();
                JSONObject sub1 = new JSONObject();
                sub1.put("it", "03");
                JSONObject sub2 = new JSONObject();
                sub2.put("it", "04");
                JSONObject sub3 = new JSONObject();
                sub3.put("it", "05");
                ja.put(sub1);
                ja.put(sub2);
                ja.put(sub3);
                queryConf.put("C0", ja);
            }
            {
                JSONArray ja = new JSONArray();
                JSONObject sub1 = new JSONObject();
                sub1.put("it", "06");
                JSONObject sub2 = new JSONObject();
                sub2.put("it", "08");
                ja.put(sub2);
                ja.put(sub1);
                queryConf.put("D1", ja);
            }
            {
                JSONArray ja = new JSONArray();
                JSONObject sub1 = new JSONObject();
                sub1.put("it", "07");
                JSONObject sub3 = new JSONObject();
                sub3.put("it", "09");
                ja.put(sub3);
                ja.put(sub1);
                queryConf.put("D2", ja);
            }
            {
                JSONArray ja = new JSONArray();
                JSONObject sub1 = new JSONObject();
                sub1.put("it", "25");
                ja.put(sub1);
                queryConf.put("DX", ja);
            }
            {
                JSONArray ja = new JSONArray();
                JSONObject sub1 = new JSONObject();
                sub1.put("it", "26");
                ja.put(sub1);
                queryConf.put("DY", ja);
            }
            {
                JSONArray ja = new JSONArray();
                JSONObject sub1 = new JSONObject();
                sub1.put("it", "10");
                ja.put(sub1);
                queryConf.put("DC", ja);
            }
            {
                JSONArray ja = new JSONArray();
                JSONObject sub1 = new JSONObject();
                sub1.put("it", "11");
                ja.put(sub1);
                queryConf.put("DB", ja);
            }
            for (int i = 1; i <= 99; i++) {
                JSONArray ja = new JSONArray();
                JSONObject sub1 = new JSONObject();
                sub1.put("i", String.format("%02d", i));
                ja.put(sub1);
                queryConf.put(String.format("%02d", i), ja);
            }
            {
                JSONArray ja = new JSONArray();
                JSONObject sub1 = new JSONObject();
                sub1.put("i", String.format("%02d", 80));
                ja.put(sub1);
                queryConf.put(String.format("%02d", 80), ja);
            }
            {
                JSONArray ja = new JSONArray();
                JSONObject sub1 = new JSONObject();
                sub1.put("it", "13");
                ja.put(sub1);
                queryConf.put("E0", ja);
            }
            {
                JSONArray ja = new JSONArray();
                JSONObject sub1 = new JSONObject();
                sub1.put("it", "14");
                ja.put(sub1);
                queryConf.put("F0", ja);
            }
            {
                JSONArray ja = new JSONArray();
                JSONObject sub1 = new JSONObject();
                sub1.put("it", "15");
                ja.put(sub1);
                queryConf.put("G0", ja);
            }

            {
                JSONArray ja = new JSONArray();
                JSONObject sub1 = new JSONObject();
                sub1.put("it", "16");
                ja.put(sub1);
                queryConf.put("H0", ja);
            }
            {
                JSONArray ja = new JSONArray();
                JSONObject sub1 = new JSONObject();
                sub1.put("it", "23");
                ja.put(sub1);
                queryConf.put("O0", ja);
            }
            {
                JSONArray ja = new JSONArray();
                JSONObject sub1 = new JSONObject();
                sub1.put("io", "RR");
                JSONObject sub2 = new JSONObject();
                sub2.put("io", "RB");
                JSONObject sub3 = new JSONObject();
                sub3.put("io", "RS");
                ja.put(sub1);
                ja.put(sub2);
                ja.put(sub3);
                queryConf.put("Q0", ja);
            }

            //暫緩或延遲收盤
            {
                JSONArray ja = new JSONArray();
                JSONObject sub1 = new JSONObject();
                sub1.put("ip", "1");
                JSONObject sub2 = new JSONObject();
                sub2.put("ip", "2");
                ja.put(sub1);
                ja.put(sub2);
                queryConf.put("Z0", ja);
            }
            {
                JSONArray ja = new JSONArray();
                JSONObject sub1 = new JSONObject();
                sub1.put("ip", "1");
                ja.put(sub1);
                queryConf.put("Z1", ja);
            }
            {
                JSONArray ja = new JSONArray();
                JSONObject sub1 = new JSONObject();
                sub1.put("ip", "2");
                ja.put(sub1);
                queryConf.put("Z2", ja);
            }
            {
                JSONArray ja = new JSONArray();
                JSONObject sub1 = new JSONObject();
                sub1.put("ip", "3");
                ja.put(sub1);
                queryConf.put("Z3", ja);
            }
            {
                JSONArray ja = new JSONArray();
                JSONObject sub1 = new JSONObject();
                sub1.put("ip", "4");
                ja.put(sub1);
                queryConf.put("Z4", ja);
            }
            {
                JSONArray ja = new JSONArray();
                JSONObject sub1 = new JSONObject();
                sub1.put("ip", "5");
                ja.put(sub1);
                queryConf.put("Z5", ja);
            }
            {
                JSONArray ja = new JSONArray();
                JSONObject sub1 = new JSONObject();
                sub1.put("ip", "4");
                JSONObject sub2 = new JSONObject();
                sub2.put("ip", "5");
                ja.put(sub1);
                ja.put(sub2);
                queryConf.put("Z6", ja);
            }
            //指數
            {
                JSONArray ja = new JSONArray();
                JSONObject sub1 = new JSONObject();
                sub1.put("i", "tidx.tw");
                ja.put(sub1);
                queryConf.put("TIDX", ja);
            }
            {
                JSONArray ja = new JSONArray();
                JSONObject sub1 = new JSONObject();
                sub1.put("i", "oidx.tw");
                ja.put(sub1);
                queryConf.put("OIDX", ja);
            }
        } catch (JSONException ex) {
        }
        com.ecloudlife.cass.logicutil.StockCategory.setQueryConf(queryConf,"JSP_Init");
    }

    public static JSONArray getStockNewboardCond(String key) {
        JSONArray cates = null;
        try {
            cates = queryNewBoard.getJSONArray(key);
        } catch (JSONException ex) {
            cates = new JSONArray();
        }
        return cates;
    }

    public static JSONArray getStockCategoryCond(String key) {
        JSONArray cates = null;
        try {
            cates = queryConf.getJSONArray(key);
        } catch (JSONException ex) {
            cates = new JSONArray();
        }
        return cates;
    }

    // XXX [上市] 價格指數、報酬指數
    public static Vector<String> getTidxSymbolList() {
        Vector<String> idxList = new Vector<String>();

        // 上市價格指數
        idxList.add("t00.tw"); // 發行量加權股價指數
        idxList.add("TW50.tw"); // 臺灣50指數
        idxList.add("TW50C.tw"); // 臺灣50權重上限30%指數
        idxList.add("TWMC.tw"); // 臺灣中型100指數
        idxList.add("TWIT.tw"); // 臺灣資訊科技指數
        idxList.add("TWEI.tw"); // 臺灣發達指數
        idxList.add("TWDP.tw"); // 臺灣高股息指數
        idxList.add("EMP99.tw"); // 臺灣就業99指數
        idxList.add("HC100.tw"); // 臺灣高薪100指數
        idxList.add("CG100.tw"); // 臺灣公司治理100指數
        idxList.add("FRMSA.tw"); // 寶島股價指數
        idxList.add("t001.tw"); // 未含金融保險股指數
        idxList.add("t002.tw"); // 未含電子股指數
        idxList.add("t003.tw"); // 未含金融電子股指數
        idxList.add("SC300.tw"); // 小型股300指數
        idxList.add("t011.tw"); // 水泥窯製類指數
        idxList.add("t031.tw"); // 塑膠化工類指數
        idxList.add("t051.tw"); // 機電類指數
        idxList.add("t01.tw"); // 水泥類指數
        idxList.add("t02.tw"); // 食品類指數
        idxList.add("t03.tw"); // 塑膠類指數
        idxList.add("t04.tw"); // 紡織纖維類指數
        idxList.add("t05.tw"); // 電機機械類指數
        idxList.add("t06.tw"); // 電器電纜類指數
        idxList.add("t07.tw"); // 化學生技醫療類指數
        idxList.add("t21.tw"); // 化學類指數
        idxList.add("t22.tw"); // 生技醫療類指數
        idxList.add("t08.tw"); // 玻璃陶瓷類指數
        idxList.add("t09.tw"); // 造紙類指數
        idxList.add("t10.tw"); // 鋼鐵類指數
        idxList.add("t11.tw"); // 橡膠類指數
        idxList.add("t12.tw"); // 汽車類指數
        idxList.add("t13.tw"); // 電子類指數
        idxList.add("t24.tw"); // 半導體類指數
        idxList.add("t25.tw"); // 電腦及週邊設備類指數
        idxList.add("t26.tw"); // 光電類指數
        idxList.add("t27.tw"); // 通信網路類指數
        idxList.add("t28.tw"); // 電子零組件類指數
        idxList.add("t29.tw"); // 電子通路類指數
        idxList.add("t30.tw"); // 資訊服務類指數
        idxList.add("t31.tw"); // 其他電子類指數
        idxList.add("t14.tw"); // 建材營造類指數
        idxList.add("t15.tw"); // 航運類指數
        idxList.add("t16.tw"); // 觀光餐旅類指數 20230703
        idxList.add("t17.tw"); // 金融保險類指數
        idxList.add("t18.tw"); // 貿易百貨類指數
        idxList.add("t23.tw"); // 油電燃氣類指數
        idxList.add("IX0185.tw"); // 綠能環保類指數 20230703
        idxList.add("IX0186.tw"); // 數位雲端類指數 20230703
        idxList.add("IX0187.tw"); // 運動休閒類指數 20230703
        idxList.add("IX0188.tw"); // 居家生活類指數 20230703
        idxList.add("t20.tw"); // 其他類指數
        idxList.add("TTDRL2.tw"); // 臺指日報酬兩倍指數
        idxList.add("TTDRIN.tw"); // 臺指反向一倍指數
        idxList.add("EDRL2.tw"); // 電子類兩倍槓桿指數
        idxList.add("EDRIN.tw"); // 電子類反向指數

        // 上市報酬指數
        idxList.add("IR0001.tw");
        idxList.add("IR0009.tw");
        idxList.add("IR0010.tw");
        idxList.add("IR0011.tw");
        idxList.add("IR0012.tw");
        idxList.add("IR0016.tw");
        idxList.add("IR0017.tw");
        idxList.add("IR0018.tw");
        idxList.add("IR0019.tw");
        idxList.add("IR0020.tw");
        idxList.add("IR0021.tw");
        idxList.add("IR0022.tw");
        idxList.add("IR0023.tw");
        idxList.add("IR0024.tw");
        idxList.add("IR0025.tw");
        idxList.add("IR0026.tw");
        idxList.add("IR0027.tw");
        idxList.add("IR0028.tw");
        idxList.add("IR0029.tw");
        idxList.add("IR0030.tw");
        idxList.add("IR0031.tw");
        idxList.add("IR0032.tw");
        idxList.add("IR0033.tw");
        idxList.add("IR0034.tw");
        idxList.add("IR0035.tw");
        idxList.add("IR0036.tw");
        idxList.add("IR0037.tw");
        idxList.add("IR0038.tw");
        idxList.add("IR0039.tw");
        idxList.add("IR0040.tw");
        idxList.add("IR0041.tw");
        idxList.add("IR0185.tw"); // 綠能環保類報酬指數 20230703
        idxList.add("IR0186.tw"); // 數位雲端類報酬指數 20230703
        idxList.add("IR0187.tw"); // 運動休閒類報酬指數 20230703
        idxList.add("IR0188.tw"); // 居家生活類報酬指數 20230703
        idxList.add("IR0042.tw");

        return idxList;
    }

    // XXX [上櫃] 價格指數、報酬指數
    public static Vector<String> getOidxSymbolList() {
        Vector<String> idxList = new Vector<String>();

        // 上櫃價格指數
        idxList.add("o00.tw"); // 櫃買指數
        idxList.add("IX0118.tw"); // 富櫃200指數
        idxList.add("IX0201.tw"); // TPEx FactSet 氣候韌性指數 20230925
        idxList.add("IX0202.tw"); // TPEx FactSet 半導體氣候韌性指數 20230925
        idxList.add("IX0138.tw"); // 櫃買銳聯 Quality 50 指數
        idxList.add("GTSM50.tw"); // 富櫃50指數
        idxList.add("GTHD.tw"); // 高殖利率指數
        idxList.add("EMP88.tw"); // 勞工就業88指數
        idxList.add("GTCI.tw"); // 櫃買薪酬指數
        idxList.add("TPCGI.tw"); // 櫃買公司治理指數
        idxList.add("IX0134.tw"); // 臺灣上櫃永續指數
        idxList.add("IX0177.tw"); // 上櫃ESG 30指數
        idxList.add("o13.tw"); // 電子工業類指數
        idxList.add("o04.tw"); // 紡織纖維類指數
        idxList.add("o05.tw"); // 電機機械類指數
        idxList.add("o21.tw"); // 化學工業類指數
        idxList.add("o22.tw"); // 生技醫療類指數
        idxList.add("o10.tw"); // 鋼鐵工業類指數
        idxList.add("o24.tw"); // 半導體業指數
        idxList.add("o25.tw"); // 電腦及週邊設備業指數
        idxList.add("o26.tw"); // 光電業指數
        idxList.add("o27.tw"); // 通信網路業指數
        idxList.add("o28.tw"); // 電子零組件業指數
        idxList.add("o29.tw"); // 電子通路業指數
        idxList.add("o30.tw"); // 資訊服務業指數
        idxList.add("o31.tw"); // 其他電子類指數
        idxList.add("o32.tw"); // 文化創意業類指數
        idxList.add("o14.tw"); // 建材營造類指數
        idxList.add("o15.tw"); // 航運業指數
        idxList.add("o16.tw"); // 觀光餐旅類指數 20230703
        idxList.add("o20.tw"); // 其他類指數
        idxList.add("IX0189.tw"); // 櫃檯綠能環保類指數 20230703
        idxList.add("IX0190.tw"); // 櫃檯數位雲端類指數 20230703
        idxList.add("IX0191.tw"); // 櫃檯運動休閒類指數 20230703
        idxList.add("TWTBI.tw"); // 臺灣指標公債指數

        // 上櫃報酬指數
        idxList.add("IR0118.tw");
        idxList.add("IR0138.tw");
        idxList.add("IR0140.tw");
        idxList.add("IR0141.tw");
        idxList.add("IR0148.tw");
        idxList.add("IR0149.tw");
        idxList.add("IR0173.tw");
        idxList.add("IR0189.tw"); // 櫃檯綠能環保類報酬指數 20230703
        idxList.add("IR0190.tw"); // 櫃檯數位雲端類報酬指數 20230703
        idxList.add("IR0191.tw"); // 櫃檯運動休閒類報酬指數 20230703
        idxList.add("IR0200.tw"); // 特選上櫃ESG龍頭報酬指數 20230918
        idxList.add("IR0201.tw"); // TPEx FactSet 氣候韌性報酬指數 20230925
        idxList.add("IR0202.tw"); // TPEx FactSet 半導體氣候韌性報酬指數 20230925
        idxList.add("IR0203.tw"); // TPEx FactSet 半導體氣候淨零優選報酬指數 20230925

        return idxList;
    }

    public static String purifyString(String inputStr, String defaultStr, int maxLength) {
        if (inputStr == null || ! inputStr.matches("[_a-zA-Z0-9-]*") || inputStr.contains("0x")) {
            return defaultStr;
        }
        if (inputStr.length() < maxLength) {
            maxLength = inputStr.length();
        }
        return inputStr.substring(0, maxLength).replaceAll("(?i)script", "").replaceAll("(?i)alert", "").replaceAll("(?i)prompt", "");
    }

    public static boolean isDateActivated(String activatedDate) {
        try {
            Date activated = df.parse(activatedDate);
            Date today = new Date();
            return today.after(activated);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public static JSONObject exec(String ex_key,String ex,String date,String lang,String is,String bp,String odd){

        JSONObject oddObject = new JSONObject();
        if (odd.equals("1")) {
            String key = "";
            if (ex.equals("tse"))
                key = "TH30";
            if (ex.equals("otc"))
                key = "OH30";
            StockIoFile sio = new StockIoFile(date,key);
            List<Map.Entry<String, String>> sList = sio.get();
            if (!sList.isEmpty()) {
            	Map.Entry<String, String> hcol = sList.get(0);
                oddObject = new JSONObject(hcol.getValue());
            } else {
                odd = "0";
            }
        }

        long qt = System.nanoTime();
        //JSONArray conds = com.ecloudlife.cass.logicutil.StockCategory.getStockCategoryCond(is);
        JSONArray conds = null;
        if(!is.equals("")) conds = getStockCategoryCond(is);
        if(!bp.equals("")) conds = getStockNewboardCond(bp);
        JSONArray ja = new JSONArray();
        for (int inx = 0; inx < conds.length(); inx++) {
            JSONObject sub = conds.getJSONObject(inx);
            java.util.Iterator it = sub.keys();
            while (it.hasNext()) {
                String key = (String) it.next();
                String value = sub.getString(key);

                List<Map<String, String>> rList = null;

                if(!is.equals(""))
                rList = com.ecloudlife.cass.logicutil.StockCategory
                        .getCategoryIndex1(ex, key, value, date, "jsp");

                if(!bp.equals(""))
                rList = com.ecloudlife.cass.logicutil.StockNewboard
                        .getNewboardIndex1(ex, key, value, date);

                Hashtable<String, Map<String, String>> symbolsRows = new Hashtable<String, Map<String, String>>();
                //sd.getIndex(ks,aa,null,null,null,null);
                //if(!lang.equals("zh-tw"))
                {
                    for (Map<String, String> row : rList) {
                        String ch = row.get("ch");
                        if(ch==null) continue;
                        if (ch.endsWith(".tw"))
                            ch = ch.substring(0, ch.length() - 3);
                        
                        Map<String, String> snsMap = com.ecloudlife.cass.logicutil.StockCategory.symbolsRows.get(ch);
                        if(snsMap==null){
                            StockNameStore sns = new StockNameStore(date,ch);
                            snsMap = sns.getMap();   
                            com.ecloudlife.cass.logicutil.StockCategory.symbolsRows.put(ch, snsMap);
                        }
                        if(snsMap!=null && !snsMap.isEmpty()) symbolsRows.put(ch, snsMap);   
                    }
                    //StockNameStore sns = new StockNameStore(date,ch);
                    //symbolsRows = sns.getRowKeysHash(ks, symbols);
                }

                if (is.equals("TIDX") || is.equals("OIDX")) { //idx special sort
                    java.util.Hashtable<String, Map<String, String>> rHash = new Hashtable<String, Map<String, String>>();
                    for (Map<String, String> row : rList) {
                    	String ch = row.get("ch");
                    	if(ch==null) continue;
                    	if (ch.endsWith(".tw"))
                            ch = ch.substring(0, ch.length() - 3);
                    	rHash.put(ch, row);
                    	
                    }
                    Vector<String> sortList = new Vector<String>();
                    Vector<Map<String, String>> sortrList = new Vector<Map<String, String>>();
                    if (is.equals("TIDX"))
                        sortList = getTidxSymbolList();
                    if (is.equals("OIDX"))
                        sortList = getOidxSymbolList();
                    for (String sch : sortList) {
                        //out.println(sch);
                    	if (sch.endsWith(".tw"))
                            sch = sch.substring(0, sch.length() - 3);
                        Map<String, String> row = rHash.get(sch);
                        if (row != null)
                            sortrList.add(row);
                        //out.println(sch);
                    }
                    rList = sortrList;
                }

                
                for (int i = 0; i < rList.size(); i++) {
                  String ex0 = "";
                  String bp0 = "";

                    JSONObject j1 = new JSONObject();
                    Map<String, String> row = rList.get(i);
                    
                    String key0 = row.get("key");
                    String ch0 = row.get("ch");
                    String n0 = row.get("n");
                    bp0 = row.get("bp");
                    if(bp0==null) bp0="";
                    ex0 = row.get("ex");
                    
                    j1.put("key", key0);
                    j1.put("ch", ch0);
                    j1.put("n", n0);
                    j1.put("bp", bp0);
                    j1.put("ex", ex0);

                    if(bp.equals("") && ex0.equals("otc") && bp0.equals("3")) continue; 
                    if (!lang.equals("zh-tw")) {
                        String ch = j1.getString("ch");
                        if (ch.endsWith(".tw"))
                            ch = ch.substring(0, ch.length() - 3);
                        if (ch != null) {
                            ch = Utility.filiterString(ch, ".tw");
                            Map<String, String> symbolRow = symbolsRows.get(ch);
                            if (symbolRow != null) {
                                String hcol = symbolRow.get("en");
                                if (hcol != null) {
                                    j1.put("n", hcol);
                                }
                                hcol = symbolRow.get("ef");
                                if (hcol != null) {
                                    j1.put("nf", hcol);
                                } else {
                                    String n = j1.optString("n");
                                    j1.put("nf", n);
                                }
                            }
                        };
                    } else {
                        String ch = j1.getString("ch");
                        if (ch.endsWith(".tw"))
                            ch = ch.substring(0, ch.length() - 3);
                        if (ch != null) {
                            ch = Utility.filiterString(ch, ".tw");
                            Map<String, String> symbolRow = symbolsRows.get(ch);
                            if (symbolRow != null) {
                                String hcol = symbolRow.get("cf");
                                if (hcol != null) {
                                    j1.put("nf", hcol);
                                } else {
                                    String n = j1.optString("n");
                                    j1.put("nf", n);
                                }
                            }
                        }
                    }
                    if (odd.equals("1")) {
                        String ch = j1.getString("ch");
                        if (ch.endsWith(".tw"))
                            ch = ch.substring(0, ch.length() - 3);
                        if (oddObject.has(ch)) {
                            ja.put(j1);
                        }
                        //oddObject
                    } else
                        ja.put(j1);
                    //out.println(j1); // {"ex":"tse","ch":"t00.tw","nf":"發行量加權股價指數","key":"tse_t00.tw_20220413","n":"發行量加權股價指數"}
                    // 加上報酬指數的屬性以便網頁識別，集中市場=2，上櫃市場=3
                    if (j1.optString("ch").startsWith("IR")) {
                        String xm = is.endsWith("TIDX") ? "2" : "3";
                        j1.put("xm", xm);
                    }                
                }
            }
        }
        qt = (System.nanoTime() - qt) / 1000;

        JSONObject jqueryTime = new JSONObject();
        jqueryTime.put("stockDetail", qt);
        jqueryTime.put("totalMicroTime", qt);

        // XXX 跨市場指數
        if (is.endsWith("IDX")) {
            boolean isTWSE = is.endsWith("TIDX");
            if (lang.equals("zh-tw")) { // 中文指數名稱使用「簡稱」
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0103.tw', 'nf':'臺灣生技指數', 'n':'臺灣生技指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0108.tw', 'nf':'臺灣中小型公司治理指數', 'n':'臺灣中小型公司治理指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0109.tw', 'nf':'臺灣IPO指數', 'n':'臺灣IPO指數'}"));

                if (isTWSE) { // 集中市場 only!
                    ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0125.tw', 'nf':'智慧中立指數', 'n':'智慧中立指數'}"));
                }

                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0133.tw', 'nf':'臺灣企業社會責任中小型指數', 'n':'臺灣企業社會責任中小型指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0139.tw', 'nf':'臺灣5G+通訊指數', 'n':'臺灣5G+通訊指數'}"));

                if (isTWSE) { // 集中市場 only!
                    ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IR0129.tw', 'nf':'加權指數掩護性臺指買權價外5%報酬指數', 'n':'加權指數掩護性臺指買權價外5%報酬指數'}"));
                }

                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IR0131.tw', 'nf':'臺灣5G報酬指數', 'n':'臺灣5G報酬指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IR0135.tw', 'nf':'特選臺灣電動車代表報酬指數', 'n':'特選臺灣電動車代表報酬指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0142.tw', 'nf':'臺灣全市場指數', 'n':'臺灣全市場指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0143.tw', 'nf':'臺灣全市場半導體指數', 'n':'臺灣全市場半導體指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IR0146.tw', 'nf':'臺灣智慧移動與電動車報酬指數', 'n':'臺灣智慧移動與電動車報酬指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0150.tw', 'nf':'特選小資高價30指數', 'n':'特選小資高價30指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0152.tw', 'nf':'特選臺灣綠能及電動車指數', 'n':'特選臺灣綠能及電動車指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0153.tw', 'nf':'特選臺灣智能車供應鏈聯盟指數', 'n':'特選臺灣智能車供應鏈聯盟指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0154.tw', 'nf':'臺灣全市場半導體精選30指數', 'n':'臺灣全市場半導體精選30指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0155.tw', 'nf':'特選臺灣高股息30指數', 'n':'特選臺灣高股息30指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0156.tw', 'nf':'臺灣友善環境指數', 'n':'臺灣友善環境指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0158.tw', 'nf':'臺灣永續價值指數', 'n':'臺灣永續價值指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IR0159.tw', 'nf':'特選臺灣IC設計報酬指數', 'n':'特選臺灣IC設計報酬指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0161.tw', 'nf':'特選臺灣環境永續高股息指數', 'n':'特選臺灣環境永續高股息指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0162.tw', 'nf':'臺灣友善環境50指數', 'n':'臺灣友善環境50指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IR0164.tw', 'nf':'特選臺灣IC設計產業代表報酬指數', 'n':'特選臺灣IC設計產業代表報酬指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IR0165.tw', 'nf':'特選臺灣綠色能源報酬指數', 'n':'特選臺灣綠色能源報酬指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0167.tw', 'nf':'特選臺灣智慧50指數', 'n':'特選臺灣智慧50指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0169.tw', 'nf':'臺灣晶圓製造指數', 'n':'臺灣晶圓製造指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0170.tw', 'nf':'臺灣精選高息指數', 'n':'臺灣精選高息指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0171.tw', 'nf':'臺灣多因子優選高股息30指數', 'n':'臺灣多因子優選高股息30指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0172.tw', 'nf':'特選臺灣優利高填息30指數', 'n':'特選臺灣優利高填息30指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0179.tw', 'nf':'特選臺灣科技優息指數', 'n':'特選臺灣科技優息指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IR0179.tw', 'nf':'特選臺灣科技優息報酬指數', 'n':'特選臺灣科技優息報酬指數'}"));                
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0181.tw', 'nf':'臺灣半導體收益指數', 'n':'臺灣半導體收益指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0184.tw', 'nf':'臺灣優選成長高股息指數', 'n':'臺灣優選成長高股息指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0192.tw', 'nf':'特選臺灣ESG低碳高息40指數', 'n':'特選臺灣ESG低碳高息40指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0193.tw', 'nf':'臺灣優選多因子30指數', 'n':'臺灣優選多因子30指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0194.tw', 'nf':'臺灣創新科技50指數', 'n':'臺灣創新科技50指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0195.tw', 'nf':'臺灣全市場新經濟產業指數', 'n':'臺灣全市場新經濟產業指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0196.tw', 'nf':'臺灣ESG永續高股息等權重指數', 'n':'臺灣ESG永續高股息等權重指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0198.tw', 'nf':'臺灣永續高息中小型指數', 'n':'臺灣永續高息中小型指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0204.tw', 'nf':'特選臺灣高息優質成長指數', 'n':'特選臺灣高息優質成長指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0205.tw', 'nf':'特選臺灣高息動能指數', 'n':'特選臺灣高息動能指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0206.tw', 'nf':'臺灣電子成長高息等權重指數', 'n':'臺灣電子成長高息等權重指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0207.tw', 'nf':'特選臺灣優利成長指數', 'n':'特選臺灣優利成長指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0208.tw', 'nf':'臺灣價值高息指數', 'n':'臺灣價值高息指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0209.tw', 'nf':'特選臺灣IC設計動能指數', 'n':'特選臺灣IC設計動能指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0210.tw', 'nf':'臺灣科技高股息指數', 'n':'臺灣科技高股息指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0211.tw', 'nf':'臺灣趨勢動能高股息指數', 'n':'臺灣趨勢動能高股息指數'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0214.tw', 'nf':'特選臺灣科技高息成長指數', 'n':'特選臺灣科技高息成長指數'}"));

                if (isDateActivated("20240701")) {
                    ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0216.tw', 'nf':'特選臺灣AI優息動能指數', 'n':'特選臺灣AI優息動能指數'}"));
                    ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0217.tw', 'nf':'臺灣優選AI 50指數', 'n':'臺灣優選AI 50指數'}"));
                }
            } else { // 英文指數名稱使用「全稱」
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0103.tw', 'nf':'TIP Taiwan BIO Index', 'n':'TIP Taiwan BIO Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0108.tw', 'nf':'TIP Taiwan SMCG Index', 'n':'TIP Taiwan SMCG Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0109.tw', 'nf':'TIP Taiwan IPO Index', 'n':'TIP Taiwan IPO Index'}"));

                if (isTWSE) { // 集中市場 only!
                    ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0125.tw', 'nf':'TIP TAIFEX Smart Market Neutral Index', 'n':'TIP TAIFEX Smart Market Neutral Index'}"));
                }

                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0133.tw', 'nf':'TIP Taiwan CSR SMC Index', 'n':'TIP Taiwan CSR SMC Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0139.tw', 'nf':'TIP FactSet Taiwan 5G Plus Communications Index', 'n':'TIP FactSet Taiwan 5G Plus Communications Index'}"));

                if (isTWSE) { // 集中市場 only!
                    ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IR0129.tw', 'nf':'TIP TAIFEX TAIEX Covered Call OTM5% TR Index', 'n':'TIP TAIFEX TAIEX Covered Call OTM5% TR Index'}"));
                }

                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IR0131.tw', 'nf':'TIP FactSet Taiwan 5G Total Return Index', 'n':'TIP FactSet Taiwan 5G Total Return Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IR0135.tw', 'nf':'TIP EVR Total Return Index', 'n':'TIP EVR Total Return Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0142.tw', 'nf':'TIP Taiwan Total Market Index', 'n':'TIP Taiwan Total Market Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0143.tw', 'nf':'TIP Taiwan Semiconductor Total Market Index', 'n':'TIP Taiwan Semiconductor Total Market Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IR0146.tw', 'nf':'TIP FactSet Taiwan Smart Mobility and Electric Vehicles Total Return Index', 'n':'TIP FactSet Taiwan Smart Mobility and Electric Vehicles Total Return Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0150.tw', 'nf':'TIP Customized Investor Movement Aristocrats 30 Index', 'n':'TIP Customized Investor Movement Aristocrats 30 Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0152.tw', 'nf':'TIP Customized Taiwan Green Energy and Electric Vehicles Index', 'n':'TIP Customized Taiwan Green Energy and Electric Vehicles Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0153.tw', 'nf':'TIP Customized Taiwan Smart Vehicles and Supplier Alliances Index', 'n':'TIP Customized Taiwan Smart Vehicles and Supplier Alliances Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0154.tw', 'nf':'TIP Taiwan Semiconductor Total Market Select 30 Index', 'n':'TIP Taiwan Semiconductor Total Market Select 30 Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0155.tw', 'nf':'TIP Customized Taiwan High Dividend 30 Index', 'n':'TIP Customized Taiwan High Dividend 30 Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0156.tw', 'nf':'TIP Taiwan Environmental Sustainability Index', 'n':'TIP Taiwan Environmental Sustainability Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0158.tw', 'nf':'TIP Taiwan Sustainability Value Index', 'n':'TIP Taiwan Sustainability Value Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IR0159.tw', 'nf':'TIP Customized Taiwan IC Design Total Return Index', 'n':'TIP Customized Taiwan IC Design Total Return Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0161.tw', 'nf':'TIP Customized Environmental Substainability Dividend+ Index', 'n':'TIP Customized Environmental Substainability Dividend+ Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0162.tw', 'nf':'TIP Taiwan Environmental Substainability 50 Index', 'n':'TIP Taiwan Environmental Substainability 50 Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IR0164.tw', 'nf':'TIP Customized Taiwan IC Design Representatives Total Return Index', 'n':'TIP Customized Taiwan IC Design Representatives Total Return Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IR0165.tw', 'nf':'TIP Customized Taiwan Green Energy Total Return Index', 'n':'TIP Customized Taiwan Green Energy Total Return Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0167.tw', 'nf':'TIP Customized Taiwan Smart Factor 50 Index', 'n':'TIP Customized Taiwan Smart Factor 50 Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0169.tw', 'nf':'TIP Customized Taiwan Wafer Manufacture Index', 'n':'TIP Customized Taiwan Wafer Manufacture Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0170.tw', 'nf':'TIP Customized Taiwan Select High Dividend Index', 'n':'TIP Customized Taiwan Select High Dividend Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0171.tw', 'nf':'TIP Customized Taiwan Multi-factor High Dividend 30 Index', 'n':'TIP Customized Taiwan Multi-factor High Dividend 30 Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0172.tw', 'nf':'TIP Customized Taiwan High Dividend Recovery 30 Index', 'n':'TIP Customized Taiwan High Dividend Recovery 30 Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0179.tw', 'nf':'TIP Taiwan Technology Dividend Highlight Index', 'n':'TIP Taiwan Technology Dividend Highlight Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IR0179.tw', 'nf':'TIP Taiwan Technology Dividend Highlight Total Return Index', 'n':'TIP Taiwan Technology Dividend Highlight Total Return Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0181.tw', 'nf':'TIP Customized Taiwan Semiconductor Dividend Yield Index', 'n':'TIP Customized Taiwan Semiconductor Dividend Yield Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0184.tw', 'nf':'TIP Customized Taiwan Growth and High Dividend Index', 'n':'TIP Customized Taiwan Growth and High Dividend Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0192.tw', 'nf':'TIP Customized Taiwan ESG Low Carbon High Dividend 40 Index', 'n':'TIP Customized Taiwan ESG Low Carbon High Dividend 40 Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0193.tw', 'nf':'TIP Customized Taiwan Multi-factor 30 Index', 'n':'TIP Customized Taiwan Multi-factor 30 Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0194.tw', 'nf':'TIP FactSet Taiwan Innovative Technology 50 Index', 'n':'TIP FactSet Taiwan Innovative Technology 50 Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0195.tw', 'nf':'TIP Taiwan New Economy Industry Total Market Index', 'n':'TIP Taiwan New Economy Industry Total Market Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0196.tw', 'nf':'TIP Customized Taiwan ESG High Dividend Equal Weight Index', 'n':'TIP Customized Taiwan ESG High Dividend Equal Weight Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0198.tw', 'nf':'TIP Customized Taiwan ESG High Dividend Small/Mid-Cap Index', 'n':'TIP Customized Taiwan ESG High Dividend Small/Mid-Cap Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0204.tw', 'nf':'TIP Customized Taiwan High Dividend Quality Growth Index', 'n':'TIP Customized Taiwan High Dividend Quality Growth Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0205.tw', 'nf':'TIP Customized Taiwan High Dividend and Momentum Index', 'n':'TIP Customized Taiwan High Dividend and Momentum Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0206.tw', 'nf':'TIP Customized Taiwan IT Growth and High Dividend Equal Weight Index', 'n':'TIP Customized Taiwan IT Growth and High Dividend Equal Weight Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0207.tw', 'nf':'TIP Customized Taiwan Dividend and Growth Index', 'n':'TIP Customized Taiwan Dividend and Growth Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0208.tw', 'nf':'TIP Taiwan Value High Dividend Index', 'n':'TIP Taiwan Value High Dividend Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0209.tw', 'nf':'TIP Customized Taiwan IC Design and Momentum Index', 'n':'TIP Customized Taiwan IC Design and Momentum Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0210.tw', 'nf':'TIP FactSet Taiwan Technology and High Dividend Index', 'n':'TIP FactSet Taiwan Technology and High Dividend Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0211.tw', 'nf':'TIP FactSet Taiwan Momentum High Dividend Index', 'n':'TIP FactSet Taiwan Momentum High Dividend Index'}"));
                ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0214.tw', 'nf':'TIP Customized Taiwan Tech High Dividend and Growth Index', 'n':'TIP Customized Taiwan Tech High Dividend and Growth Index'}"));

                if (isDateActivated("20240701")) {
                    ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0216.tw', 'nf':'TIP Customized Taiwan AI High Dividend and Momentum Index', 'n':'TIP Customized Taiwan AI High Dividend and Momentum Index'}"));
                    ja.put(new JSONObject("{'ex':'tse', 'xm':'1', 'ch':'IX0217.tw', 'nf':'TIP FactSet Taiwan AI 50 Index', 'n':'TIP FactSet Taiwan AI 50 Index'}"));
                }
            }
        }
    /*
        // XXX [上市] 報酬指數
        if (is.endsWith("TIDX")) {
            if (lang.equals("zh-tw")) {
            } else {
            }
        }

        // XXX [上櫃] 報酬指數
        if (is.endsWith("OIDX")) {
            if (lang.equals("zh-tw")) {
            } else {
            }
        }
    */
        // XXX 新 TSE ETF 測試
        if (is.equals("B0") && ex.equals("tse")) {
            //ja.put(new JSONObject("{'ex':'tse', 'nf':'第一金工業30反1', 'n':'第一金工業30反1', 'ch':'00729R.tw'}"));
        }

        // XXX 新 OTC ETF 測試
        if (is.equals("B0") && ex.equals("otc")) {
            //ja.put(new JSONObject("{'ex':'otc', 'nf':'永豐美國500大', 'n':'永豐美國500大', 'ch':'00858.tw'}"));
        }

        // XXX 新 TSE ETN 測試
        if (is.equals("B1") && ex.equals("tse")) {
            //ja.put(new JSONObject("{'ex':'tse', 'nf':'富邦特選蘋果N', 'n':'富邦特選蘋果N', 'ch':'020000.tw'}"));
        }

        // XXX 新 OTC ETN 測試
        if (is.equals("B1") && ex.equals("otc")) {
            //ja.put(new JSONObject("{'ex':'otc', 'nf':'富邦存股雙十N', 'n':'富邦存股雙十N', 'ch':'020001.tw'}"));
        }

        JSONObject j = new JSONObject();
        j.put("size", ja.length());
        j.put("rtcode", "0000");
        j.put("rtmessage", "OK");
        j.put("msgArray", ja);
        j.put("queryTime", jqueryTime);
        if (!ja.isEmpty()) {
            resCacheTime.put(ex_key, new Long(System.currentTimeMillis()));
            resCacheObject.put(ex_key,j);
        }
        return j;
    }
    
%>
