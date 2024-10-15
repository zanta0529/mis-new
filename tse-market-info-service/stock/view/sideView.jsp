<!--[if lte IE 9]>
<script type='text/javascript' src='//cdnjs.cloudflare.com/ajax/libs/jquery-ajaxtransport-xdomainrequest/1.0.0/jquery.xdomainrequest.min.js'></script>
<![endif]-->

<script>
	$(document).ready(function() 
	{ 
            $('#search').autocomplete({
                position: { at: 'left top' ,of :'#search_bar' }
            });

		if ($.browser.msie) 
		{
    	add_placeholder('search', '<%=SIDEVIEW_INPUTCODE%>');
   	}
  });	
</script>


<div id="news">
			<form class="form-wrapper" action="fibest.jsp">
				<h3><%=SIDEVIEW_GET_STOCK%></h3>
                <input type="text" id="search" name="stock" placeholder="<%=SIDEVIEW_INPUTCODE%>"  required>
                <input type="submit" value="<%=SIDEVIEW_SEARCH%>" id="submit" class="qSearch" />
                
                <span id="search_bar" style='position: relative;left:-158px;top:18px'>&nbsp;</span>
			</form>
			
			<div class="form-wrapper">				
				<table width="100%">
					<thead>
    					<td width="40%"><h3><a href="frmsa.jsp"><%=INDEX_FRMSAEX%></a> </h3></td>
    					<td width="30%" id="side_FRMSA_z" align="right">-</td>
    					<td width="30%" id="side_FRMSA_t" align="right"></td>
					</thead>
					<tr>
						<td nowrap align="left"><%=PRICE_CHANGE%></td>
						<td nowrap align="right"><%=HIGHEST_PRICE%></td>
						<td nowrap align="right"><%=LOWEST_PRICE%></td>
					</tr>
					<tr>
						<td nowrap align="left">
							<label id="side_FRMSA_diff">-</label>
							<label id="side_FRMSA_pre"></label>	
						</td>
						<td nowrap id="side_FRMSA_h" align="right">-</td>
						<td nowrap id="side_FRMSA_l" align="right">-</td>
					</tr>
				</table>
			</div>


			<div class="form-wrapper">				
				<table width="100%">
					<tr>
						<td width="40%"><h3><a href="index.jsp"><%=INDEX_TAIEX%></a> </h3></td>
						<td width="30%" id="side_t00_z" align="right">-</td>
						<td width="30%" id="side_t00_t" align="right"></td>
					</tr>
					<tr>
					</tr>
						<td nowrap align="left"><%=PRICE_CHANGE%></td>
						<td nowrap align="right"><%=HIGHEST_PRICE%></td>
						<td nowrap align="right"><%=LOWEST_PRICE%></td>
    				</tr>
					<tr>
						<td nowrap align="left">
							<label id="side_t00_diff">-</label>	
							<label id="side_t00_pre"></label>	
						</td>
						<td nowrap id="side_t00_h" align="right">-</td>
						<td nowrap id="side_t00_l" align="right">-</td>
				    </tr>
				</table>
			</div>
			
			<div class="form-wrapper">				
				<table width="100%">
					<tr>
						<td width="40%"><h3><a href="index.jsp"><%=INDEX_GTSM%></a> </h3></td>
						<td width="30%" id="side_o00_z" align="right">-</td>
						<td width="30%" id="side_o00_t" align="right"></td>
					</tr>
					<tr>
						<td nowrap align="left"><%=PRICE_CHANGE%></td>
						<td nowrap align="right"><%=HIGHEST_PRICE%></td>
						<td nowrap align="right"><%=LOWEST_PRICE%></td>
					</tr>
					<tr>
						<td nowrap align="left">
							<label id="side_o00_diff">-</label>
							<label id="side_o00_pre"></label>	
						</td>
						<td nowrap id="side_o00_h" align="right">-</td>
						<td nowrap id="side_o00_l" align="right">-</td>
					</tr>
				</table>
			</div>
			


			<div class="form-wrapper">
				<table width="100%">
					<tr>
						<td width="40%" id="side_TX_n"><h3><a href="futures.jsp"><%=SIDEVIEW_FUTURES%></a> </h3></td>
						<td width="30%" id="side_TX_z" align="right">-</td>
						<td width="30%" id="side_TX_t" align="right"></td>
					</tr>
					<tr>
						<td nowrap align="left"><%=PRICE_CHANGE%></td>
						<td nowrap align="right"><%=HIGHEST_PRICE%></td>
						<td nowrap align="right"><%=LOWEST_PRICE%></td>
					</tr>
					<tr>
						<td nowrap align="left">
							<label id="side_TX_diff">-</label>
							<label id="side_TX_pre"></label>	
						</td>
						<td nowrap id="side_TX_h" align="right">-</td>
						<td nowrap id="side_TX_l" align="right">-</td>
					</tr>
				</table>
			</div>


<!--

			<div class="form-wrapper">
				<div>
					<table width="100%">
							<tr>
							<td><h3><a href="frmsa.jsp"><%=INDEX_FRMSAEX%></a> </h3></td>
							<td id="side_FRMSA_t" align="right"></td>
							</tr>
					</table>
				</div>


				
				<table width="100%" id="o00Table" class="o00Table">
					<thead>
						<td align="center" style="white-space:nowrap"><%=INDEX_FRMSAEX%></td>
						<td align="center"><%=PRICE_CHANGE%></td>
						<td align="center"><%=HIGHEST_PRICE%></td>
						<td align="center"><%=LOWEST_PRICE%></td>
					</thead>
					<tbody>
						<tr>
							<td id="side_FRMSA_z" align="center">-</td>
							<td align="center">
								<label id="side_FRMSA_diff">-</label><br>
								<label id="side_FRMSA_pre"></label>	
							</td>
							<td id="side_FRMSA_h" align="center">-</td>
							<td id="side_FRMSA_l" align="center">-</td>
						</td>
					</tbody>
				</table>
			</div>

			<div class="form-wrapper">
				<div>
					<table width="100%">
							<tr>
							<td><h3><a href="index.jsp"><%=SIDEVIEW_TWSE%></a> </h3></td>
							<td id="side_t00_t" align="right"></td>
							</tr>
					</table>
				</div>
				<table width="100%" id="t00Table" class="t00Table">
					<thead>
						<td align="center" style="white-space:nowrap"><%=INDEX_TAIEX%></td>
						<td align="center"><%=PRICE_CHANGE%></td>
						<td align="center"><%=HIGHEST_PRICE%></td>
						<td align="center"><%=LOWEST_PRICE%></td>
					</thead>
					<tbody>
						<tr>
							<td id="side_t00_z" align="center">-</td>
							<td align="center">
								<label id="side_t00_diff">-</label>	<br>
								<label id="side_t00_pre"></label>	
							</td>
							<td id="side_t00_h" align="center">-</td>
							<td id="side_t00_l" align="center">-</td>
						</td>
					</tbody>
				</table>
			</div>
			
			<div class="form-wrapper">
				<div>
					<table width="100%">
							<tr>
							<td><h3><a href="index.jsp"><%=SIDEVIEW_GTSM%></a> </h3></td>
							<td id="side_o00_t" align="right"></td>
							</tr>
					</table>
				</div>
				
				<table width="100%" id="o00Table" class="o00Table">
					<thead>
						<td align="center" style="white-space:nowrap"><%=INDEX_GTSM%></td>
						<td align="center"><%=PRICE_CHANGE%></td>
						<td align="center"><%=HIGHEST_PRICE%></td>
						<td align="center"><%=LOWEST_PRICE%></td>
					</thead>
					<tbody>
						<tr>
							<td id="side_o00_z" align="center">-</td>
							<td align="center">
								<label id="side_o00_diff">-</label><br>
								<label id="side_o00_pre"></label>	
							</td>
							<td id="side_o00_h" align="center">-</td>
							<td id="side_o00_l" align="center">-</td>
						</td>
					</tbody>
				</table>
			</div>
			


			<div class="form-wrapper">
				<div>
					<table width="100%">
							<tr>
							<td id="side_TX_n"><h3><a href="futures.jsp"><%=SIDEVIEW_FUTURES%></a> </h3></td>
							<td id="side_TX_t" align="right"></td>
							</tr>
					</table>
				</div>
				
				<table width="100%" id="f00Table" class="f00Table">
					<thead>
						<td align="center" style="white-space:nowrap"><%=INDEX_FUTURES%></td>
						<td align="center"><%=PRICE_CHANGE%></td>
						<td align="center"><%=HIGHEST_PRICE%></td>
						<td align="center"><%=LOWEST_PRICE%></td>
					</thead>
					<tbody>
						<tr>
							<td id="side_TX_z" align="center">-</td>
							<td align="center">
								<label id="side_TX_diff">-</label><br>
								<label id="side_TX_pre"></label>	
							</td>
							<td id="side_TX_h" align="center">-</td>
							<td id="side_TX_l" align="center">-</td>
						</td>
					</tbody>
				</table>
			</div>


-->

			<div id="newsItem" title="公告事項"> <!--class="newsItem"-->
				<ul id="ticker_01" class="ticker" > <% // 須配合訊息長度調整/css/ticker.css 的 height 值 %>
				
					<li>	
					<b><%=SIDEVIEW_ANNOUN_TITLE%></b><BR><%=SIDEVIEW_ANNOUN_CONTENT%>
					</li>
					<li>
					<b><%=SIDEVIEW_ANNOUN_TITLE1%></b><BR><%=SIDEVIEW_ANNOUN_CONTENT1%>
					</li>
					</ul>
			</div>
			<div class="newsItem">
				<h1><%=SIDEVIEW_LINK%></h1>
				<p><a href="<%=SIDEVIEW_LINK0_URL%>" target="_blank"><%=SIDEVIEW_LINK0%></a><BR>
					<a href="<%=SIDEVIEW_LINK1_URL%>" target="_blank"><%=SIDEVIEW_LINK1%></a><BR>
					<a href="<%=SIDEVIEW_LINK2_URL%>" target="_blank"><%=SIDEVIEW_LINK2%></a><BR>
					<a href="<%=SIDEVIEW_LINK3_URL%>" target="_blank"><%=SIDEVIEW_LINK3%></a></p>
			</div>
	  </div>
	</div>
	
	
	
	<script>
	function tick(){
		$('#ticker_01 li:first').slideUp( function () { $(this).appendTo($('#ticker_01')).slideDown(); });
	}
	setInterval(function(){ tick () }, 5000);
	</script>
