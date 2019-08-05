<%@ page language="java" contentType="text/html; charset=UTF-8" isELIgnored="false"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<!-- saved from url=(0062)http://www.woodare.com/zhonlinepay/mf/page/opened/casherManage -->
<html><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0">
<meta name="msapplication-tap-highlight" content="no">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" content="no-cache">
<meta http-equiv="Expires" content="0">
<meta http-equiv="Page-Enter" content="blendTrans(duration=1)">
<meta http-equiv="Page-Exit" content="blendTrans(duration=1)">
<meta name="format-detection" content="telephone=no">
<meta name="format-detection" content="email=no">
<meta name="format-detection" content="address=no">

<meta name="renderer" content="webkit">
<meta http-equiv="X-UA-Compatible" content="IE=edge">

<link rel="stylesheet" type="text/css" href="resources/css/default.css">

<!--必要样式-->
<link rel="stylesheet" type="text/css" href="resources/css/styles.css">

</head>
<body>
<div class='login'>
<div style="text-align:center;margin:auto;margin-top:10px;"><img src="resources/img/pic_logo.png" width="187" height="42"></div>
  <br/>
  <p>消费金额：</p>
    <input id='money' class='input' type="text" name="fname" /><br>
    <div class="form-group input-group">
             <span class="input-group-addon" ><i class="iconfont">&#xe644;</i></span>
            <p>商品名:</p> <input type="text" class="input" id="shopName" name="shopName" placeholder="请输入商品名" error="请输入商品名">
         </div>
  <br/>
  <br/>
  <br/>
  
    <button class="sk_button" type="button">收款</button>
  <br/>

  
</div>

<script type="text/javascript" src='resources/js/stopExecutionOnTimeout.js?t=1'></script>
<script type="text/javascript" src="resources/js/jquery-2.1.1.min.js"></script>
<script type="text/javascript" src="resources/js/jquery-ui.min.js"></script>
<script>

$(document).ready(function(){
   $("button.sz_button").click(function(){
	   $("input#money")[0].value = $("input#money")[0].value + $(this).context.innerHTML;
   });
   
   $("button.sc_button").click(function(){
	   $("input#money")[0].value = $("input#money")[0].value.substring(0,$("input#money")[0].value.length - 1);
   });
   
    $("button.sk_button").click(function(){
    	var money =$("#money").val();
    	var shopName =$("#shopName").val();
    	var terminalNo =<%=request.getParameter("terminalNo")%>;
    	var client =window.navigator.userAgent;
	   $.ajax({
			type: "POST",
           url:   "/lly-posp-proxy/LLyPayController.app",
           data: {	            	
        	   shopName: shopName,	 
        	   money: money,
        	   terminalNo:terminalNo
           },
           dataType: "json",
           success: function(result){
           	   if(typeof(result.url)==undefined){
           	   		alert(result.msg);
           	   }else{
           	    var fdStart = result.url.indexOf("http");
	        	   if(fdStart==0){
	        	   		window.location.href=result.url
	        	   } else {     
	        	   		callpay(result)
					}  
           	   }
			},
			error : function(XMLHttpRequest, textStatus) {  
				alert("生成订单失败，请重试");
           }
		});
   });
});

</script>

<div style="text-align:center;">

</div>

</body>
</html>
