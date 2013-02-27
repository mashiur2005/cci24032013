<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head><title>Publication</title></head>
<body>
<h1>Publication</h1>

<h3>${it.publication}</h3>
<div>
    <span>Issue Search:</span>

    <form action="${it.contextPath}${it.organization}/${it.publication}/issue">
        <dl>
            <dt><label for="limit">Limit</label></dt>
            <dd><input type="text" name="limit" id="limit"/></dd>
        </dl>
        <dl>
            <dt><label for="offset">Offset</label></dt>
            <dd><input type="text" name="offset" id="offset"/></dd>
        </dl>
        <dl>
            <dd><input type="submit" name="submit"/></dd>
        </dl>
    </form>
</div>
</body>
</html>