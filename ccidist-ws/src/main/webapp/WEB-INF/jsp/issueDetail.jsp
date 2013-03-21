<%@ page contentType="application/xhtml+xml;charset=UTF-8" language="java"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<title>${it.publication.name} ${it.issue.id} for ${it.issue.platform.id}</title>
</head>
<body>
    <dl class="issue">
        <dt>Name: </dt>
            <dd>${it.publication.name}</dd>
        <dt>Issue: </dt>
            <dd>${it.issue.id}</dd>
        <dt>Date: </dt>
            <dd>${it.issue.updated}</dd>
        <dt>Title: </dt>
            <dd>${it.publication.name}</dd>
        <dt>epub: </dt>
            <dd><a href="${it.binaryUri}">Epub</a></dd>
        <dt>container: </dt>
            <dd><a href="${it.containerUri}">Container</a></dd>
        <dt>events: </dt>
            <dd><a href="${it.eventsUri}">Events</a></dd>
    </dl>
</body>
</html>