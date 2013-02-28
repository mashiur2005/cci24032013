<%@ page contentType="application/xhtml+xml;charset=UTF-8" language="java"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<title>Issue</title>
</head>
<body>
    <h1>Issue</h1>
    <h3>${it.issue.name} of '${it.publication.name}' Publication</h3>
    <ul class="issue">
        <li><a href="${it.binaryUri}">Epub</a></li>
        <li><a href="${it.containerUri}">Container</a></li>
    </ul>
</body>
</html>