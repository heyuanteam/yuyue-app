<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@page isELIgnored="false" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0">
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
    <title>身份认证</title>
    <link rel="stylesheet" href="resources/css/common.css">
    <link rel="stylesheet" href="resources/css/mobile/bootstrap/bootstrap.min.css" type="text/css">
    <link rel="stylesheet" href="resources/css/mobile/mobile.css" type="text/css">
    <!-- load included css files -->
    <script>
        window.CMS_URL = '/lly-posp-proxy/';
        window.PLATFORM_CODE = '';
    </script>
    <script type="text/javascript" src="resources/js/jquery-2.1.4.min.js"></script>
    <script type="text/javascript" src="resources/js/common.js"></script>
    <script type="text/javascript" src="resources/js/dao.js"></script>

    <!-- load included js files -->
    <script type="text/javascript" src="resources/js/jquery.md5.js"></script>
</head>
<body unselectable="on" onselectstart="return false;">

<div class="mobile-register container-fluid">
    <div class="logo">
        <input type="hidden" id="logoVal" name="logoVal" value="${product}">
        <img id="logo" src="resources/images/mobile-register-logo.png"/>
    </div>
    <div class="container-fluid form-register">
        <!-- <div class="alert alert-danger hide">请输入手机号</div> -->
        <div class="form-group input-group">
            <span class="input-group-addon"><i class="iconfont">&#xe644;</i></span>
            <input readonly="readonly" type="tel" class="form-control" value="${recomphone }  (推广商电话号码)">
        </div>
        <div class="form-group input-group">
            <span class="input-group-addon"><i class="iconfont">&#xe644;</i></span>
            <input type="tel" class="form-control" id="phone" placeholder="请输入手机号" error="请输入手机号">
        </div>
        <div class="form-group input-group">
            <span class="input-group-addon"><i class="iconfont">&#xe614;</i></span>
            <input type="tel" class="form-control" id="valiCode" placeholder="验 证 码" error="请输验证码">
            <span class="input-group-btn pr-10">
                 <button type="button" class="btn  verification btn-danger" id="btnSendCode" onclick="sendMessage()">获取验证码</button>
             </span>
        </div>
        <div class="form-group input-group">
            <span class="input-group-addon"><i class="iconfont">&#xe65e;</i></span>
            <input type="password" class="form-control" id="password" placeholder="密  码" error="请输入密码">
        </div>
        <div class="form-group input-group">
            <span class="input-group-addon"><i class="iconfont">&#xe65e;</i></span>
            <input type="password" class="form-control" id="rePassword" placeholder="确认密码" error="两次密码不一致">
        </div>
        <%--        	<p><span>我的口碑费率：${hbRate},${product}</span><span>我的POS费率：${posRate}</span></p> --%>
        <%--        	<p><span>我的分期费率：${stagesRate}</span><span>我的代还费率：${ykRate}</span></p> --%>
        <div class="form-group mt-30">
            <button type="submit" class="btn btn-danger btn-lg btn-block" onclick="register()">注册</button>
        </div>
        <div class="form-group mt-30">
            <a id="download" href="javascript:void(0)" style="background: #239fdb;"
               class="btn btn-danger btn-lg btn-block">app下载</a>
        </div>
    </div>
    <input type="hidden" id="recomphone" value="${recomphone}">
</div>


<script>
    var interValObj; //timer变量，控制时间
    var count = 60; //间隔函数，1秒执行
    var curCount; //当前剩余秒数
    var __phone = null;
    var logoVal = $("#logoVal").val();
    var logo = $("#logo");
    var path = 'resources/images/' + logoVal + '.png';
    var MGKB = "http://mangguokabao.llyzf.cn/down/down.html";
    var link;
    $(function () {
        getDownloadLink();
        getLogo();
    });

    function getLogo() {
        logo.attr('src', path);
    }

    function getDownloadLink() {
        if (logoVal === "MGKB") {
            link = MGKB;
        }
        if (!logoVal) {
            link = MGKB;
            path = 'resources/images/MGKB.png';
        }
    }

    //注册成功才能下载
    $("#download").click(function () {
        //	_WOO.modal.alert("注册成功才能下载！");
        window.location.href = link;
    })

    function sendMessage() {
        var $el = $("#btnSendCode");
        if ($el.is(".disabled")) {
            return;
        }
        var phone = $("#phone").val();
        var product = $("#logoVal").val();
        if (!phone) {
            _WOO.modal.alert("请正确输入手机号");
            return;
        }
        curCount = count; //设置button效果，开始计时
        $el.addClass("disabled").val("剩余" + curCount + "秒");
        interValObj = window.setInterval(SetRemainTime, 1000); //启动计时器，1秒执行一次
        __phone = phone;
        //向后台发送处理数据.
        _WOO.dao.sendSmsCode(phone, product).done(function () {
            __phone = phone;
            product = product;
        }).fail(function (error) {
            if (error) {
                _WOO.modal.alert(error);
            }
        });
    }

    //timer处理函数
    function SetRemainTime() {
        if (curCount == 0) {
            window.clearInterval(interValObj); //停止计时器
            $("#btnSendCode").removeClass("disabled").html("重新发送");
        } else {
            curCount--;
            $("#btnSendCode").html("剩余" + curCount + "秒");
        }
    }

    function register() {
        var phone = $("#phone").val();
        var valiCode = $("#valiCode").val();
        var password = $("#password").val();
        var rePassword = $("#rePassword").val();
        var referPhone = $("#recomphone").val();
        var product = $("#logoVal").val();
        if (phone.length === 0) {
            _WOO.modal.alert("手机号不能为空");
            return;

        }

        if (valiCode.length === 0) {
            _WOO.modal.alert("验证码不能为空");
            return;

        }

        if (password.length === 0) {
            _WOO.modal.alert("密码不能为空");
            return;

        }

        if (rePassword.length === 0) {
            _WOO.modal.alert("确认密码不能为空");
            return;

        }

        if (password != rePassword) {
            _WOO.modal.alert("两次密码不一致");
            return;

        }
        //向后台发送处理数据.
        _WOO.dao.appregister({
            phone: phone,
            smsCode: valiCode,
            pwd: $.md5(password),
            referPhone: referPhone,
            product: product
        }).done(function () {
            _WOO.modal.alert("帐号注册成功").done(function () {
                /* window.location.href = "http://tyjf.llyzf.cn:8080/hatchet-lly/down.html"; */
                window.location.href = link;
            });
        }).fail(function (error) {
            //_WOO.modal.alert(error);
            if (error == "ZC") {
                _WOO.modal.alert("验证码错误");
            } else if (error == "ZD") {
                _WOO.modal.alert("手机号码重复注册，请直接登陆").done(function () {
                    /* window.location.href="http://tyjf.llyzf.cn:8080/hatchet-lly/down.html"; */
                    window.location.href = link;
                });
            } else if (error == "ZZ0") {
                _WOO.modal.alert("版本号不对");
            } else if (error == "ZZ31") {
                _WOO.modal.alert("推荐人不存在");
            } else {
                var str = error;
                _WOO.modal.alert(str);
            }
        });
    }

</script>

</body>
</html>