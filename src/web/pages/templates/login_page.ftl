<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <link rel="stylesheet" href="${baseURL}/css/ftl/login-page-style.css" type="text/css">
</head>

<body>
    <div id='floater' > </div>
    <div id='login-panel'>
        <form method='post'>
            <fieldset>
                <table>
                    <tr><td colspan='2' id='error-message'>${errorMessage?default("")}</td></tr>
                    <tr>
                        <td><label>Username</label></td>
                        <td><input name='username' type='text' autofocus/></td>
                    </tr>
                    <tr>
                        <td><label>Password</label></td>
                        <td><input name='password' type='password'/></td>
                    </tr>
                </table>
            </fieldset>
            <fieldset>
                <button type='submit'>Login</button>
            </fieldset>
        </form>
    </div>
</body>

</html>
