<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Simple jsp page</title></head>
<body>

<h1>File Upload with Jersey</h1>

<form action="${it.binaryUri}" method="post" enctype="multipart/form-data">

    <p>
        Select a file : <input type="file" name="file" size="45"/>
    </p>

    <input type="submit" value="Upload It"/>
</form>

</body>
</html>