<!doctype html>
<html lang="zh">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"> 
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<link rel="stylesheet" type="text/css" href="resources/css/default.css">

<!--必要样式-->
<link rel="stylesheet" type="text/css" href="resources/css/styles.css">

</head>
<body>
<div class='login'>
<div style="text-align:center;margin:auto;margin-top:10px;"><img src="resources/img/pic_logo.png" width="187" height="42"></div>
  <p style="margin-top:10px;">收款商户：${name}</p>
  <p style="margin-top:10px;">二维码编号：${qcode}</p>
  <br/>
  <p>消费金额：</p>
    <input id='money' class='input' type="text" name="fname" />
  <br/>
  <br/>
  <br/>
    <button class="sk_button" type="button">收款</button>
  <br/>
  <button class="sz_button left_button" type="button">1</button><button class="sz_button" type="button">2</button><button class="sz_button" type="button">3</button>
  
  <button class="sz_button left_button" type="button">4</button><button class="sz_button" type="button">5</button><button class="sz_button" type="button">6</button>
  
  <button class="sz_button left_button" type="button">7</button><button class="sz_button" type="button">8</button><button class="sz_button" type="button">9</button>
  
  <button class="sz_button bottom_button left_button" type="button">0</button><button class="sz_button bottom_button" type="button">.</button><button class="sc_button bottom_button" type="button">删除</button>
  
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
    	var phone =${phone};
    	var qcode =${qcode};
    	var name ="${name}";
//     	var openid= "${openid}";
    	var client =window.navigator.userAgent;
	   $.ajax({
			type: "POST",
           url:   "/lly-posp-proxy/jspay.app",
           data: {	            	
        	   phone: phone,	 
        	   money: money,
        	   qcode:qcode,
        	   name:name,
        	   client:client,
//         	   openid:openid,
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

function callpay(result){   
		   var orderno=result.order;
		   var reqcode="00";
		   var obj=eval('(' + result.url + ')');
		   WeixinJSBridge.invoke('getBrandWCPayRequest',{"appId" : obj.appId ,"timeStamp" : obj.timeStamp, "nonceStr" : obj.nonceStr, "package" : obj.package,"signType" : "MD5", "paySign" : obj.paySign },
		   function(res){
				WeixinJSBridge.log(res.err_msg);
	            if(res.err_msg == "get_brand_wcpay_request:ok"){  
	            	reqcode="01";
	            }else if(res.err_msg == "get_brand_wcpay_request:cancel"){ 
	            	reqcode="02"; 
	            }else{  
	            	reqcode="03";
	            }  
	       $.ajax({
			type: "POST",
            url: "/lly-posp-proxy/jsnotify.app",
            data: {	            	
        	   reqcode:reqcode,
        	   orderno:orderno,
           },
           dataType: "json",
           success: function(result){},
			error : function(XMLHttpRequest, textStatus) {}});
			})
		}
</script>

<div style="text-align:center;">

</div>

</body>
</html>
