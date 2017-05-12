<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="multipart/form-data; charset=utf-8" />
</head>
<body>
<h2>Hello World!</h2>
普通上传
<form name="formId" action="/manage/product/upload.do" enctype="multipart/form-data" method="post">
    <input type="file" name="upload_file">
    <input type="submit" value="文件上传">
</form>
富文本上传
<form name="formId" action="/manage/product/richTextImgUpload.do" enctype="multipart/form-data" method="post">
    <input type="file" name="upload_file">
    <input type="submit" value="文件上传">
</form>
</body>
</html>
