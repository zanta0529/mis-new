package com.proco.util;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.json.JSONObject;

import com.ecloudlife.cass.logicutil.ExLastTradeDate;
import com.ecloudlife.cass.logicutil.OddInfo;
import com.ecloudlife.cass.logicutil.OddInfoManager;
import com.ecloudlife.cass.logicutil.OtOhlcData;
import com.ecloudlife.cass.logicutil.OtOhlcManager;
import com.ecloudlife.cass.logicutil.StockCategory;
import com.ecloudlife.cass.logicutil.StockInfo;
import com.ecloudlife.cass.logicutil.StockInfoManager;
import com.ecloudlife.cass.logicutil.StockNewboard;
import com.ecloudlife.cass.logicutil.StockStatisManager;
import com.ecloudlife.util.UserSession;
import com.proco.cache.JedisManager;
import com.proco.datautil.StockIoFile;

public class StockQueryTest {

	public static void main(String[] args) {
		System.out.println(Utility.getFullyDateTimeStr()+" StockQueryTest STG 0.1 20231022_3");
		// TODO Auto-generated method stub
		String ip = "127.0.0.1";
		String port = "10001";
		String pw = null;
		if(args.length==2) {
			ip = args[0];
			port = args[1];
		} else 	if(args.length==3) {
			ip = args[0];
			port = args[1];
			pw = args[2];
		}
		
		
		JedisManager.init(ip, port,pw);
				
		StockInfoManager.main(args);
		OddInfoManager.main(args);
		StockStatisManager.main(args);
		long ft = 0;
		int c = 0;
		while(true) {
			c++;
			String sessionKey = String.valueOf(System.nanoTime());
			UserSession us = new UserSession(sessionKey);
			List<String> slist = new java.util.ArrayList<>();
			String qString = "tse_2302.tw|tse_2303.tw|tse_2329.tw|tse_2330.tw|tse_2337.tw|tse_2338.tw|tse_2340.tw|tse_2342.tw|tse_2344.tw|tse_2351.tw|tse_2363.tw|tse_2369.tw|tse_2379.tw|tse_2388.tw|tse_2401.tw|tse_2408.tw|tse_2434.tw|tse_2436.tw|tse_2441.tw|tse_2449.tw|tse_2451.tw|tse_2454.tw|tse_2458.tw|tse_2481.tw|tse_3006.tw|tse_3014.tw|tse_3016.tw|tse_3034.tw|tse_3035.tw|tse_3041.tw|tse_3094.tw|tse_3189.tw|tse_3257.tw|tse_3413.tw|tse_3443.tw|tse_3450.tw|tse_3530.tw|tse_3532.tw|tse_3545.tw|tse_3583.tw|tse_3588.tw|tse_3592.tw|tse_3661.tw|tse_3686.tw|tse_3711.tw|tse_4919.tw|tse_4952.tw|tse_4961.tw|tse_4967.tw|tse_4968.tw|tse_5222.tw|tse_5269.tw|tse_5285.tw|tse_5471.tw|tse_6202.tw|tse_6239.tw|tse_6243.tw|tse_6257.tw|tse_6271.tw|tse_6415.tw|tse_6451.tw|tse_6515.tw|tse_6525.tw|tse_6531.tw|tse_6533.tw|tse_6552.tw|tse_6573.tw|tse_6695.tw|tse_6719.tw|tse_6756.tw|tse_6770.tw|tse_6789.tw|tse_6799.tw|tse_6854.tw|tse_8016.tw|tse_8028.tw|tse_8081.tw|tse_8110.tw|tse_8131.tw|tse_8150.tw|tse_8261.tw|tse_8271.tw|";
			qString += "tse_1471.tw|tse_1582.tw|tse_2059.tw|tse_2308.tw|tse_2313.tw|tse_2316.tw|tse_2327.tw|tse_2328.tw|tse_2355.tw|tse_2367.tw|tse_2368.tw|tse_2375.tw|tse_2383.tw|tse_2385.tw|tse_2392.tw|tse_2402.tw|tse_2413.tw|tse_2415.tw|tse_2420.tw|tse_2421.tw|tse_2428.tw|tse_2431.tw|tse_2440.tw|tse_2457.tw|tse_2460.tw|tse_2462.tw|tse_2467.tw|tse_2472.tw|tse_2476.tw|tse_2478.tw|tse_2483.tw|tse_2484.tw|tse_2492.tw|tse_2493.tw|tse_3003.tw|tse_3011.tw|tse_3015.tw|tse_3021.tw|tse_3023.tw|tse_3026.tw|tse_3032.tw|tse_3037.tw|tse_3042.tw|tse_3044.tw|tse_3058.tw|tse_3090.tw|tse_3092.tw|tse_3229.tw|tse_3296.tw|tse_3308.tw|tse_3321.tw|tse_3338.tw|tse_3376.tw|tse_3432.tw|tse_3501.tw|tse_3533.tw|tse_3550.tw|tse_3593.tw|tse_3605.tw|tse_3607.tw|tse_3645.tw|tse_3653.tw|tse_3679.tw|tse_3715.tw|tse_4545.tw|tse_4912.tw|tse_4915.tw|tse_4927.tw|tse_4943.tw|tse_4958.tw|tse_4989.tw|tse_4999.tw|tse_5469.tw|tse_6108.tw|tse_6115.tw|tse_6133.tw|tse_6141.tw|tse_6153.tw|tse_6155.tw|tse_6191.tw|tse_6197.tw|tse_6205.tw|tse_6213.tw|tse_6224.tw|tse_6269.tw|tse_6282.tw|tse_6412.tw|tse_6449.tw|tse_6672.tw|tse_6715.tw|tse_6781.tw|tse_6834.tw|tse_6835.tw|tse_8039.tw|tse_8046.tw|tse_8103.tw|tse_8213.tw|tse_8249.tw|";
			qString += "tse_2323.tw|tse_2349.tw|tse_2374.tw|tse_2393.tw|tse_2406.tw|tse_2409.tw|tse_2426.tw|tse_2429.tw|tse_2438.tw|tse_2466.tw|tse_2486.tw|tse_2491.tw|tse_3008.tw|tse_3019.tw|tse_3024.tw|tse_3031.tw|tse_3038.tw|tse_3049.tw|tse_3050.tw|tse_3051.tw|tse_3059.tw|tse_3149.tw|tse_3356.tw|tse_3406.tw|tse_3437.tw|tse_3454.tw|tse_3481.tw|tse_3504.tw|tse_3535.tw|tse_3543.tw|tse_3563.tw|tse_3576.tw|tse_3591.tw|tse_3622.tw|tse_3673.tw|tse_3714.tw|tse_4934.tw|tse_4935.tw|tse_4942.tw|tse_4956.tw|tse_4960.tw|tse_4976.tw|tse_5234.tw|tse_5243.tw|tse_5244.tw|tse_5484.tw|tse_6116.tw|tse_6120.tw|tse_6164.tw|tse_6168.tw|tse_6176.tw|tse_6209.tw|tse_6225.tw|tse_6226.tw|tse_6278.tw|tse_6405.tw|tse_6443.tw|tse_6456.tw|tse_6477.tw|tse_6668.tw|tse_6706.tw|tse_8104.tw|tse_8105.tw|tse_8215.tw|";
			qString += "tse_2314.tw|tse_2321.tw|tse_2332.tw|tse_2345.tw|tse_2412.tw|tse_2419.tw|tse_2424.tw|tse_2439.tw|tse_2444.tw|tse_2450.tw|tse_2455.tw|tse_2485.tw|tse_2498.tw|tse_3025.tw|tse_3027.tw|tse_3045.tw|tse_3047.tw|tse_3062.tw|tse_3138.tw|tse_3311.tw|tse_3380.tw|tse_3419.tw|tse_3447.tw|tse_3596.tw|tse_3669.tw|tse_3682.tw|tse_3694.tw|tse_3704.tw|tse_4904.tw|tse_4906.tw|tse_4977.tw|tse_5388.tw|tse_6136.tw|tse_6142.tw|tse_6152.tw|tse_6216.tw|tse_6285.tw|tse_6416.tw|tse_6426.tw|tse_6442.tw|tse_6674.tw|tse_6792.tw|tse_6863.tw|tse_8011.tw|tse_8101.tw|";
			qString += "tse_03084F.tw|tse_03085F.tw|tse_03086F.tw|tse_03087F.tw|tse_03088F.tw|tse_03089F.tw|tse_03090F.tw|tse_03091F.tw|tse_03092F.tw|tse_03093F.tw|tse_03094F.tw|tse_03095F.tw|tse_03096F.tw|tse_03097F.tw|tse_03098F.tw|tse_03099F.tw|tse_030004.tw|tse_030041.tw|tse_030042.tw|tse_030044.tw|tse_030045.tw|tse_030049.tw|tse_030053.tw|tse_030054.tw|tse_030057.tw|tse_030068.tw|tse_030080.tw|tse_030130.tw|tse_030133.tw|tse_030136.tw|tse_030137.tw|tse_030139.tw|tse_030143.tw|tse_030145.tw|tse_030150.tw|tse_030152.tw|tse_030156.tw|tse_030251.tw|tse_030252.tw|tse_030256.tw|tse_030257.tw|tse_030260.tw|tse_030265.tw|tse_030266.tw|tse_030267.tw|tse_030407.tw|tse_030408.tw|tse_030425.tw|tse_030426.tw|tse_030449.tw|tse_030452.tw|tse_030477.tw|tse_030479.tw|tse_030561.tw|tse_030585.tw|tse_030605.tw|tse_030646.tw|tse_030651.tw|tse_030664.tw|tse_030678.tw|tse_030680.tw|tse_030740.tw|tse_030782.tw|tse_030825.tw|tse_030857.tw|tse_030863.tw|tse_030866.tw|tse_030867.tw|tse_030868.tw|tse_030878.tw|tse_030892.tw|tse_030893.tw|tse_030900.tw|tse_030914.tw|tse_030915.tw|tse_030957.tw|tse_030958.tw|tse_030959.tw|tse_030960.tw|tse_030961.tw|tse_030971.tw|tse_030983.tw|tse_030998.tw|tse_031034.tw|tse_031035.tw|tse_031042.tw|tse_031043.tw|tse_031120.tw|tse_031121.tw|tse_031123.tw|tse_031124.tw|tse_031125.tw|tse_031126.tw|tse_031128.tw|tse_031129.tw|tse_031130.tw|tse_031150.tw|tse_031156.tw|tse_031253.tw|tse_031254.tw|";
			qString += "tse_03041Q.tw|tse_03042Q.tw|tse_03043Q.tw|tse_03044Q.tw|tse_03045Q.tw|tse_03046Q.tw|tse_03047Q.tw|tse_03048Q.tw|tse_03049Q.tw|tse_03050Q.tw|tse_03051Q.tw|tse_03052Q.tw|tse_03053Q.tw|tse_03054Q.tw|tse_03055Q.tw|tse_03001T.tw|tse_03002T.tw|tse_03003T.tw|tse_03004T.tw|tse_03005T.tw|tse_03006T.tw|tse_03007T.tw|tse_03008T.tw|tse_03009T.tw|tse_03010T.tw|tse_03011T.tw|tse_03012T.tw|tse_03013T.tw|tse_03014T.tw|tse_03015T.tw|tse_03016T.tw|tse_03017T.tw|tse_03018T.tw|tse_03019T.tw|tse_03020T.tw|tse_03021T.tw|tse_03022T.tw|tse_03023T.tw|tse_03024T.tw|tse_03025T.tw|tse_03026T.tw|tse_03027T.tw|tse_03028T.tw|tse_03029T.tw|tse_03030T.tw|tse_03031T.tw|tse_03032T.tw|tse_03033T.tw|tse_03034T.tw|tse_03034U.tw|tse_03035T.tw|tse_03036T.tw|tse_03037T.tw|tse_03038T.tw|tse_03039T.tw|tse_03040T.tw|tse_03041T.tw|tse_03042T.tw|tse_03042U.tw|tse_03043T.tw|tse_03043U.tw|tse_03044T.tw|tse_03045T.tw|tse_03046T.tw|tse_03047T.tw|tse_03048T.tw|tse_03048U.tw|tse_03049T.tw|tse_03050T.tw|tse_03051T.tw|tse_03052T.tw|tse_03053T.tw|tse_03054T.tw|tse_03055T.tw|tse_03056T.tw|tse_03057T.tw|tse_03058T.tw|tse_03059T.tw|tse_03060T.tw|tse_03061T.tw|tse_03061U.tw|tse_03062T.tw|tse_03063T.tw|tse_03064T.tw|tse_03065T.tw|tse_03066T.tw|tse_03067T.tw|tse_03068T.tw|tse_03069T.tw|tse_03070T.tw|tse_03071T.tw|tse_03072T.tw|tse_03073T.tw|tse_03074T.tw|tse_03075T.tw|tse_03076T.tw|tse_03077T.tw|tse_03078T.tw|tse_03079T.tw|tse_03080T.tw|";
			qString += "tse_0050.tw|tse_0051.tw|tse_0052.tw|tse_0053.tw|tse_0055.tw|tse_0056.tw|tse_0057.tw|tse_0061.tw|tse_006203.tw|tse_006204.tw|tse_006205.tw|tse_006206.tw|tse_006207.tw|tse_006208.tw|tse_00625K.tw|tse_00631L.tw|tse_00632R.tw|tse_00633L.tw|tse_00634R.tw|tse_00635U.tw|tse_00636.tw|tse_00636K.tw|tse_00637L.tw|tse_00638R.tw|tse_00639.tw|tse_00640L.tw|tse_00641R.tw|tse_00642U.tw|tse_00643.tw|tse_00643K.tw|tse_00645.tw|tse_00646.tw|tse_00647L.tw|tse_00648R.tw|tse_00650L.tw|tse_00651R.tw|tse_00652.tw|tse_00653L.tw|tse_00654R.tw|tse_00655L.tw|tse_00656R.tw|tse_00657.tw|tse_00657K.tw|tse_00660.tw|tse_00661.tw|tse_00662.tw|tse_00663L.tw|tse_00664R.tw|tse_00665L.tw|tse_00666R.tw|tse_00668.tw|tse_00668K.tw|tse_00669R.tw|tse_00670L.tw|tse_00671R.tw|tse_00673R.tw|tse_00674R.tw|tse_00675L.tw|tse_00676R.tw|tse_00678.tw|tse_00680L.tw|tse_00681R.tw|tse_00682U.tw|tse_00683L.tw|tse_00684R.tw|tse_00685L.tw|tse_00686R.tw|tse_00688L.tw|tse_00689R.tw|tse_00690.tw|tse_00692.tw|tse_00693U.tw|tse_00700.tw|tse_00701.tw|tse_00702.tw|tse_00703.tw|tse_00706L.tw|tse_00707R.tw|tse_00708L.tw|tse_00709.tw|tse_00710B.tw|tse_00711B.tw|tse_00712.tw|tse_00713.tw|tse_00714.tw|tse_00715L.tw|tse_00717.tw|tse_00728.tw|tse_00730.tw|tse_00731.tw|tse_00733.tw|tse_00735.tw|tse_00736.tw|tse_00737.tw|tse_00738U.tw|tse_00739.tw|tse_00752.tw|tse_00753L.tw|tse_00757.tw|tse_00762.tw|";
			
			qString += "otc_700001.tw|otc_700002.tw|otc_700014.tw|otc_700017.tw|otc_700027.tw|otc_700049.tw|otc_700051.tw|otc_700052.tw|otc_700053.tw|otc_700054.tw|otc_700055.tw|otc_700056.tw|otc_700071.tw|otc_700077.tw|otc_700079.tw|otc_700097.tw|otc_700098.tw|otc_700105.tw|otc_700106.tw|otc_700107.tw|otc_700108.tw|otc_700109.tw|otc_700110.tw|otc_700111.tw|otc_700122.tw|otc_700123.tw|otc_700130.tw|otc_700134.tw|otc_700136.tw|otc_700138.tw|otc_700139.tw|otc_700144.tw|otc_700147.tw|otc_700149.tw|otc_700160.tw|otc_700161.tw|otc_700162.tw|otc_700163.tw|otc_700164.tw|otc_700170.tw|otc_700171.tw|otc_700184.tw|otc_700186.tw|otc_700191.tw|otc_700193.tw|otc_700195.tw|otc_700204.tw|otc_700208.tw|otc_700215.tw|otc_700219.tw|otc_700220.tw|otc_700222.tw|otc_700223.tw|otc_700224.tw|otc_700225.tw|otc_700226.tw|otc_700227.tw|otc_700228.tw|otc_700229.tw|otc_700233.tw|otc_700236.tw|otc_700248.tw|otc_700264.tw|otc_700267.tw|otc_700274.tw|otc_700276.tw|otc_700287.tw|otc_700288.tw|otc_700291.tw|otc_700292.tw|otc_700296.tw|otc_700297.tw|otc_700298.tw|otc_700299.tw|otc_700300.tw|otc_700301.tw|otc_700302.tw|otc_700303.tw|otc_700304.tw|otc_700305.tw|otc_700306.tw|otc_700307.tw|otc_700308.tw|otc_700310.tw|otc_700324.tw|otc_700325.tw|otc_700326.tw|otc_700327.tw|otc_700328.tw|otc_700329.tw|otc_700330.tw|otc_700331.tw|otc_700332.tw|otc_700333.tw|otc_700335.tw|otc_700336.tw|otc_700337.tw|otc_700338.tw|otc_700339.tw|otc_700340.tw|";
			qString += "otc_1570.tw|otc_1580.tw|otc_1586.tw|otc_1591.tw|otc_1599.tw|otc_2066.tw|otc_2067.tw|otc_2070.tw|otc_2230.tw|otc_2235.tw|otc_3162.tw|otc_3226.tw|otc_3379.tw|otc_3426.tw|otc_3685.tw|otc_4502.tw|otc_4503.tw|otc_4506.tw|otc_4510.tw|otc_4513.tw|otc_4523.tw|otc_4527.tw|otc_4528.tw|otc_4533.tw|otc_4534.tw|otc_4535.tw|otc_4538.tw|otc_4543.tw|otc_4549.tw|otc_4550.tw|otc_4558.tw|otc_4561.tw|otc_4563.tw|otc_4568.tw|otc_4580.tw|otc_4584.tw|otc_6122.tw|otc_6425.tw|otc_6603.tw|otc_6609.tw|otc_6829.tw|otc_6843.tw|otc_8027.tw|otc_8083.tw|otc_8107.tw|otc_8255.tw|otc_9951.tw|";
			qString += "otc_3147.tw|otc_3570.tw|otc_4953.tw|otc_5201.tw|otc_5202.tw|otc_5210.tw|otc_5211.tw|otc_5212.tw|otc_5310.tw|otc_5403.tw|otc_5410.tw|otc_6123.tw|otc_6140.tw|otc_6148.tw|otc_6221.tw|otc_6231.tw|otc_6404.tw|otc_6516.tw|otc_6590.tw|otc_6593.tw|otc_6697.tw|otc_6751.tw|otc_6752.tw|otc_6791.tw|otc_6865.tw|otc_6874.tw|otc_8099.tw|otc_8284.tw|otc_8416.tw|";
			qString += "otc_1336.tw|otc_1595.tw|otc_1815.tw|otc_3078.tw|otc_3114.tw|otc_3115.tw|otc_3191.tw|otc_3202.tw|otc_3206.tw|otc_3207.tw|otc_3217.tw|otc_3236.tw|otc_3276.tw|otc_3288.tw|otc_3290.tw|otc_3294.tw|otc_3310.tw|otc_3322.tw|otc_3332.tw|otc_3354.tw|otc_3357.tw|otc_3388.tw|otc_3390.tw|otc_3465.tw|otc_3484.tw|otc_3492.tw|otc_3511.tw|otc_3520.tw|otc_3526.tw|otc_3537.tw|otc_3548.tw|otc_3597.tw|otc_3609.tw|otc_3624.tw|otc_3631.tw|otc_3646.tw|otc_3689.tw|otc_3710.tw|otc_4542.tw|otc_4939.tw|otc_4974.tw|otc_5227.tw|otc_5228.tw|otc_5291.tw|otc_5309.tw|otc_5328.tw|otc_5340.tw|otc_5355.tw|otc_5381.tw|otc_5439.tw|otc_5457.tw|otc_5460.tw|otc_5464.tw|otc_5475.tw|otc_5488.tw|otc_5498.tw|otc_6114.tw|otc_6124.tw|otc_6126.tw|otc_6127.tw|otc_6134.tw|otc_6156.tw|otc_6158.tw|otc_6173.tw|otc_6174.tw|otc_6175.tw|otc_6185.tw|otc_6194.tw|otc_6203.tw|otc_6204.tw|otc_6207.tw|otc_6208.tw|otc_6210.tw|otc_6217.tw|otc_6220.tw|otc_6259.tw|otc_6266.tw|otc_6274.tw|otc_6275.tw|otc_6279.tw|otc_6284.tw|otc_6290.tw|otc_6292.tw|otc_6418.tw|otc_6432.tw|otc_6538.tw|otc_6584.tw|otc_6642.tw|otc_6664.tw|otc_6727.tw|otc_6761.tw|otc_6821.tw|otc_8038.tw|otc_8042.tw|otc_8043.tw|otc_8071.tw|otc_8074.tw|otc_8091.tw|otc_8093.tw|otc_8109.tw|";
			qString += "otc_13381.tw|otc_13411.tw|otc_15893.tw|otc_15894.tw|otc_1591.tw|otc_16264.tw|otc_22362.tw|otc_22393.tw|otc_22431.tw|otc_2724.tw|otc_2726.tw|otc_2924.tw|otc_3664.tw|otc_4139.tw|otc_41481.tw|otc_4154.tw|otc_4157.tw|otc_41902.tw|otc_4745.tw|otc_47632.tw|otc_4804.tw|otc_48071.tw|otc_49124.tw|otc_4924.tw|otc_4966.tw|otc_4971.tw|otc_4991.tw|otc_5223.tw|otc_52231.tw|otc_52251.tw|otc_5227.tw|otc_5276.tw|otc_5281.tw|otc_52881.tw|otc_5543.tw|otc_55431.tw|otc_55432.tw|otc_55433.tw|otc_55434.tw|otc_55461.tw|otc_58711.tw|otc_6404.tw|otc_6514.tw|otc_65913.tw|otc_6616.tw|otc_66162.tw|otc_66163.tw|otc_6629.tw|otc_66291.tw|otc_66411.tw|otc_6741.tw|otc_84112.tw|otc_8418.tw|otc_8423.tw|otc_8426.tw|otc_8437.tw|otc_84421.tw|otc_8444.tw|otc_8455.tw|otc_84662.tw|";
			qString += "otc_006201.tw|otc_00679B.tw|otc_00687B.tw|otc_00694B.tw|otc_00695B.tw|otc_00696B.tw|otc_00697B.tw|otc_00718B.tw|otc_00719B.tw|otc_00720B.tw|otc_00721B.tw|otc_00722B.tw|otc_00723B.tw|otc_00724B.tw|otc_00725B.tw|otc_00726B.tw|otc_00727B.tw|otc_00734B.tw|otc_00740B.tw|otc_00741B.tw|otc_00744B.tw|otc_00746B.tw|otc_00749B.tw|otc_00750B.tw|otc_00751B.tw|otc_00754B.tw|otc_00755B.tw|otc_00756B.tw|otc_00758B.tw|otc_00759B.tw|otc_00760B.tw|otc_00761B.tw|otc_00764B.tw|otc_00768B.tw|otc_00772B.tw|otc_00773B.tw|otc_00777B.tw|otc_00778B.tw|otc_00779B.tw|otc_00780B.tw|otc_00781B.tw|otc_00782B.tw|otc_00784B.tw|otc_00785B.tw|otc_00786B.tw|otc_00787B.tw|otc_00788B.tw|otc_00789B.tw|otc_00790B.tw|otc_00791B.tw|otc_00792B.tw|otc_00793B.tw|otc_00794B.tw|otc_00795B.tw|otc_00799B.tw|otc_00831B.tw|otc_00834B.tw|otc_00836B.tw|otc_00840B.tw|otc_00841B.tw|otc_00842B.tw|otc_00844B.tw|otc_00845B.tw|otc_00846B.tw|otc_00847B.tw|otc_00848B.tw|otc_00849B.tw|otc_00853B.tw|otc_00856B.tw|otc_00857B.tw|otc_00858.tw|otc_00859B.tw|otc_00860B.tw|otc_00862B.tw|otc_00863B.tw|otc_00864B.tw|otc_00867B.tw|otc_00870B.tw|otc_00877.tw|otc_00883B.tw|otc_00884B.tw|otc_00886.tw|otc_00887.tw|otc_00888.tw|otc_00890B.tw|otc_00928.tw|otc_00931B.tw|";
			String[] qs = qString.split("\\|");
			for(String k : qs) {
				String[] q = k.split("_");
				String date = ExLastTradeDate.getLastTradeDate(null, q[0]); //DateManager.getExchangeDate(q[0]);
				if(!date.equals("")) slist.add(k+"_"+date);
			}
			long ct = System.currentTimeMillis();
			List<StockInfo> list = StockInfoManager.getStockInfoList(slist, sessionKey,  us, 3000 , 0);
			List<OddInfo> olist = OddInfoManager.getOddInfoList(slist, sessionKey,  us, 3000 , 0);
			
			ct = System.currentTimeMillis() - ct;
			if(ft==0) ft = ct;
			if(list.isEmpty()) {
				Utility.sleep(3000);
				continue;
			}
			int show = (int)(System.nanoTime()%list.size());
			System.out.println(Utility.getFullyDateTimeStr()+" c="+c+" "+ list.get(show).getKey()+" Qtime:"+ct+"ms FirstSeem:"+ ft+"ms");
			System.out.println(Utility.getFullyDateTimeStr()+" "+list.get(show).getKey()+" Detail:"+ list.get(show).getStockDetail().toString());
			System.out.println(Utility.getFullyDateTimeStr()+" "+list.get(show).getKey()+" Quote:"+ list.get(show).getStockQuote().toString());
			
			int oshow = (int)(System.nanoTime()%olist.size());
		
			System.out.println(Utility.getFullyDateTimeStr()+" "+olist.get(oshow).getKey()+" oDetail:"+ olist.get(oshow).getOddDetail().toString());
			System.out.println(Utility.getFullyDateTimeStr()+" "+olist.get(oshow).getKey()+" oQuote:"+ olist.get(oshow).getOddQuote().toString());
			Utility.sleep(3000);
			if(c>=8) {
				c=0;
				ft=0;
			    StockInfoManager.execJspSet.clear();
			    StockInfoManager.stockDelayHash.clear();
			    StockInfoManager.stockHash.clear();
			    StockInfoManager.resCacheObject.clear();
			    StockInfoManager.resCacheTime.clear();
			    
			    OddInfoManager.execJspSet.clear();
			    OddInfoManager.stockDelayHash.clear();
			    OddInfoManager.stockHash.clear();
			    OddInfoManager.resCacheObject.clear();
			    OddInfoManager.resCacheTime.clear();
			}
			
			{
				OtOhlcManager om = new OtOhlcManager();
				Vector<OtOhlcData> sList = om.getOHLC("tse","t00.tw",0,0,1);
				String ltime  = "";
				for(int i = 0 ; i < sList.size() ; i++){
					OtOhlcData od = sList.get(i);
					/*
					System.out.println(od.getDate() + " " + od.getTime() + " tse " +
							od.getOpen().toString()  + " " +
							od.getHigh().toString()  + " " +
							od.getLow().toString()  + " " +
							od.getCurrent().toString() + " " + od.getVolume() +" " + od.getSubvolume().toString());*/
					ltime = od.getTime();
				}
				System.out.println("tse_OtOhlcManager:"+sList.size()+" "+ltime);
			}
			{
				OtOhlcManager om = new OtOhlcManager();
				Vector<OtOhlcData> sList = om.getOHLC("otc","o00.tw",0,0,1);
				String ltime  = "";
				for(int i = 0 ; i < sList.size() ; i++){
					OtOhlcData od = sList.get(i);
					/*
					System.out.println(od.getDate() + " " + od.getTime() + " tse " +
							od.getOpen().toString()  + " " +
							od.getHigh().toString()  + " " +
							od.getLow().toString()  + " " +
							od.getCurrent().toString() + " " + od.getVolume() +" " + od.getSubvolume().toString());*/
					ltime = od.getTime();
				}
				System.out.println("otc_OtOhlcManager:"+sList.size()+" "+ltime);
			}
			{
				String date = ExLastTradeDate.getLastTradeDate(null, "tse");
				JSONObject j = new JSONObject();
				StockIoFile sioTh05 = new StockIoFile(date,"TH05");
				List<Map.Entry<String,String>> sList = sioTh05.get();
				for(int i = 0 ; i < sList.size() ; i++){
					Map.Entry<String,String> hcol = sList.get(i);
					JSONObject j1 = new JSONObject(hcol.getValue());
					if( j1.has("msgObject")){
						j= j1.getJSONObject("msgObject");
					}
				}
				System.out.println("StockIoFile(date,\"TH05\"):"+j.toString());
			}
			{
				String date = ExLastTradeDate.getLastTradeDate(null, "tse");
				List<Map<String, String>> sw = StockNewboard.getNewboardIndex1("tse", "bp", "3", date);
				System.out.println("StockNewboard(date,\"bp\"):"+sw.toString());
				
				sw = StockCategory.getCategoryIndex1("tse", "i", "01", date, "StockQueryTest");
				System.out.println("StockCategory(date,\"i\"):"+sw.toString());
				
				
				String tkey = "tse_" + date;
				List<Map.Entry<String, String>> sList = StockStatisManager.getStatisListNow(tkey);
				JSONObject j = new JSONObject();
				for(int i = 0 ; i < sList.size() ; i++){
					Map.Entry<String,String> hcol = sList.get(i);
					j.put(hcol.getKey(), hcol.getValue());
				}
				System.out.println("StockStatisManager_tse:"+j.toString()+" "+sList.size());
				
			}
		}
	}

}
