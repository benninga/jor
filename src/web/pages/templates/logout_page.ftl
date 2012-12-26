<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <link rel="stylesheet" href="${baseURL}/css/ftl/single-object-style.css" type="text/css">
    <link rel="stylesheet" href="${baseURL}/css/ftl/logout-page-style.css" type="text/css">
</head>

<body>
    ${common_header?default("")}
    <div id='floater' > </div>
    <div id='logout-panel'>
        <table border='1'>
            <tr><td>Username</td><td>${Username}</td></tr>
            <tr><td>User Key</td><td>${UserKey}</td></tr>
            <tr><td>Session Id</td><td>${SessionId}</td></tr>
        </table>
        <form method='post'>
            <fieldset>
                <button type='submit'>Logout</button>
            </fieldset>
        </form>
    </div>
</body>

</html>
