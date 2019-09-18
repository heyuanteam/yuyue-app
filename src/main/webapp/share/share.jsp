<%--
  Created by IntelliJ IDEA.
  User: ly
  Date: 2019/8/12
  Time: 15:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>娱悦视频分享网页</title>
    <style type="text/css">
        .newImg{border-radius: 50%; }
    </style>
    <link rel="stylesheet" type="text/css" href="../share/pagination.css">
    <script type="text/javascript" src="../share/jquery.min.js"></script>
    <script type="text/javascript" src="../share/jquery.pagination.js"></script>
    <script type="text/javascript">
        <%--$(function() {--%>

        <%--    $("#previousPage").click(function() {--%>
        <%--        $.ajax({--%>
        <%--            url: "../share/wxAppShare?authorId="+"${authorId}"+"&videoId="+"${videoId}"+"&pageSize="+"${pageSize-1}",--%>
        <%--            type: 'GET',--%>

        <%--            success: function (comments) {--%>

        <%--            },--%>
        <%--            error: function (xhr) { alert('服务器出错，返回内容：'+xhr.responseText)}--%>
        <%--        });--%>
        <%--        return true;--%>
        <%--    });--%>
        <%--});--%>


    </script>
</head>
<body >

    <%--<font color="#5f9ea0" size="6">娱悦专用视频分享网页</font>--%>


    <div class="row">
        <div class="col-md-12" style="text-align:left;background-color:black ; color:#FFF">

            <img src='http://101.37.252.177:8888/group1/M00/00/00/rBDoeV1c78uADfE0AABrp7l7dwU286.png'
                 style="vertical-align:middle ;border-radius:99%" width="60" height="60" >&emsp;<font color="#87cefa" size="2">娱悦</font>
            <br>

            <div style="text-align:center;background-color:black ; color:#FFF" >
                <video id = "video" width="100%" controls="controls" allowFullScreen='true' x5-video-player-fullscreen="true" autoplay="autoplay" loop="loop">
                    <source src="${uploadFile.filesPath}" type="video/mp4" autoplay="autoplay">
                    <source src="${uploadFile.filesPath}" type="video/ogg" autoplay="autoplay">
                    您的浏览器不支持播放该视频！
                </video>
            </div>
            <h6 color="f5f5dc" size="1" align="right">上传时间：${uploadFile.uploadTime}</h6>
            <font color="f5f5dc" size="2">视频名：${uploadFile.filesName}</font><br>
            <font color="f5f5dc" size="2">视频标题：${uploadFile.title}</font><br>
            <font color="#9932cc" size="2">${uploadFile.likeAmount}<font color="f5f5dc">&nbsp; 次点赞</font></font>
            <br>
        </div>

        <div style="text-align:left" >

            &emsp;&emsp;<img src='${appUserMsg.headpUrl}' style="vertical-align:middle" class = "newImg" border-radius= "50%" width="40" height="40">&emsp;
            <font color="black" size="2">${appUserMsg.nickName}</font>
            <br>
            &emsp;&emsp;<font color="black" size="3">${uploadFile.description}</font>
        </div>
        <div style="text-align:right">
            <a href="http://www.baidu.com"><img src="../share/down.jpg"  width="150" height="150" alt=""></a>
        </div>

    </div>

<%--            <div class="alert alert-success">--%>
<%--                <table border="2" align="center">--%>
<%--                    <tr>--%>
<%--                        <td>id</td>--%>
<%--                        <td>filesName</td>--%>
<%--                        <td>filesPath</td>--%>
<%--                        <td>filesType</td>--%>
<%--                    </tr>--%>
<%--                    <tr>--%>
<%--                    <td>${uploadFile.filesName}</td>--%>
<%--                    <td>${uploadFile.filesName}</td>--%>
<%--                        <td><a href ="${uploadFile.videoAddress}">视频图片路径</a></td>--%>
<%--                    <td> ${uploadFile.filesType}</td>--%>
<%--                    </tr>--%>

<%--                </table>--%>
<%--            </div>--%>




    <h2>用户评论</h2>

    <c:forEach items="${comments}" var="comment" varStatus="s" >
        &emsp;<img src='${comment.headUrl}' style="vertical-align:middle" class="newImg"  border-radius= "50%" width="45" height="45">&emsp;
        <font color="black" size="1">${comment.userName}</font>
        <br>
        &emsp;&emsp;<font color="black" size="2">${comment.text}</font>
        <HR style="border:3px;double:#987cb9" align="left" width="80%" color=#987cb9 SIZE=3>
        <br>
    </c:forEach>
    <div class="OpenAjax">
        <!--分页  -->
        <c:choose>
            <c:when test="${pageSize > 1}" >
                <a href="../share/wxAppShare?authorId=${authorId}&videoId=${videoId}&pageSize=${pageSize-1}" id="previousPage" >上一页</a>
            </c:when>
            <c:otherwise>
                上一页
            </c:otherwise>
        </c:choose>

        <c:choose>
            <c:when test="${pageSize  < totalPage}">
                <a href="../share/wxAppShare?authorId=${authorId}&videoId=${videoId}&pageSize=${pageSize+1}" id="nextPage">下一页</a>
            </c:when>
            <c:otherwise>
                下一页
            </c:otherwise>
        </c:choose>

        <!-- 显示记录数 -->
        共<font color="red">${total}</font>条记录

        <!-- 显示分页数 -->
        共<font color="red">${totalPage}</font>页
    </div>


    <!--页尾结束 -->

</body>

</html>
