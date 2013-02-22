<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head><title>Publication</title></head>
<body>
<h1>Publication</h1>
<h3>${it.publication}</h3>
<ul class="publication">
    <li><a href="${it.organization}/${it.publication}/issueList">Issue List For '${it.publication}' in '${it.organization}' Organization</a></li>
</ul>
</body>
</html>