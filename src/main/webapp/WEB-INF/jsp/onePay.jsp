<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>支付</title>
    <meta name="viewport" content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />
    <meta name="format-detection" content="telephone=no">
    <link rel="stylesheet" href="resources/css/pay.css" type="text/css">
    <script src="resources/js/fastclick.js"></script>
    <script src="resources/js/jquery/jquery.min.js"></script>
    <script type="text/javascript">
    var terminalId=${terminalId};
    alert(terminalId)
    </script>
</head>
<body>
    <div class="pay">
        <h1>
            <span class="icon"></span>
            <b>${merchantName }</b>
        </h1>
        <div class="input-item">
            <span class="info">金额</span>
            <span class="money">￥<b id="money"></b></span>
        </div>
    </div>
    <div class="pay-btn">
        <div class="number">
            <ul>
                <li>1</li>
                <li>2</li>
                <li>3</li>
                <li>4</li>
                <li>5</li>
                <li>6</li>
                <li>7</li>
                <li>8</li>
                <li>9</li>
                <li style="width: 66%">0</li>
                <li style="width: 34%">.</li>
            </ul>
        </div>
        <div class="btn">
            <ul>
                <li class="delete"><span></span></li>
                <li class="confirm-pay"><span>确认<br>支付</span></li>
            </ul>
        </div>
    </div>
     <form name="alipayment" action="${action }" >
		<input type="hidden" id="trxAmt" name="trxAmt">
		<input type="hidden" id="merchantNo" name="merchantNo" value="${merchantNo }">
		<input type="hidden" id="terminalId" name="terminalId" value="${terminalId }">
     </form>
    <script>
        try{
            // 解决ios 某些版本不能兼容问题
            FastClick.attach(document.body);
        }catch (e){

        }
        $(function () {
            var money = '';
            var reg = /^\+?(\d*\.\d{2})$/
            $('.number li').bind('click',function () {
                if(money.indexOf('.')!==-1){
                    if(reg.test(money)){
                        return;
                    }
                    if($(this).html()=='.'){
                        return;
                    }
                }
                money+=$(this).html();
                $('#money').html(money);
            });

            $('.delete').bind('click',function () {
                if(money.length<=1){
                    money = '';
                    $('#money').html('');
                }else{
                    money = money.substring(0,money.length-1);
                    $('#money').html(money);
                }

            })
            $('.confirm-pay').bind('click',function () {
                $('#trxAmt').val($('#money').html());
                $("form").submit();
            })
        });
    </script>
</body>
</html>