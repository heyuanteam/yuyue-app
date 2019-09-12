<%--
  Created by IntelliJ IDEA.
  User: ly
  Date: 2019/8/12
  Time: 15:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<div style="text-align: center;margin-top: 50px">
    <form action="uploadServer" method="post" enctype="multipart/form-data">
        <p><input type="file" name="file"/></p>
        <p><input type="submit" value="上传视频"/></p>
    </form>
</div>

<%--<div style="text-align: center;margin-top: 50px">
    <form action="upImage" method="post" enctype="multipart/form-data">
        <p><input type="file" name="file"/></p>
        <p><input type="submit" value="上传图片"/></p>
    </form>
</div>--%>
</body>
</html>
