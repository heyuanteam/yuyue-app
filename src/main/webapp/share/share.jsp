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
</head>
<body>
    <h1>hello,world!</h1>
<body>


    <div class="row">
        <div class="col-md-12">
            <h2>这是id：${uploadFile.id}</h2>


            <div style="text-align:center">
            <video id = "video" width="700" height="400" controls >
                <source src="${uploadFile.filesPath}" type="video/mp4" play()>
                <source src="${uploadFile.filesPath}" type="video/ogg">
            </video>
            <button onclick="playPause()">播放/暂停</button>
            </div>
            <script>
                var myVideo=document.getElementById("video");
                function playPause()
                {
                    if (myVideo.paused)
                        myVideo.play();
                    else
                        myVideo.pause();
                }
            </script>


            <h4>这是视频名：${uploadFile.filesName}</h4>
            <h4>这是路径：${uploadFile.filesPath}</h4>
            <h4>这是文件类型：${uploadFile.filesType}</h4>
            <div class="alert alert-success">
                <table border="2" align="center">
                    <tr>
                        <td>id</td>
                        <td>filesName</td>
                        <td>filesPath</td>
                        <td>filesType</td>
                    </tr>
                    <tr>
                    <td>${uploadFile.filesName}</td>
                    <td>${uploadFile.filesName}</td>
                        <td><a href ="${uploadFile.videoAddress}">视频图片路径</a></td>
                    <td> ${uploadFile.filesType}</td>
                    </tr>

                </table>
            </div>
        </div>

    </div>



    <!--页尾结束 -->
    </body>
</body>

</html>
