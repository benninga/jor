<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <link rel="stylesheet" href="${baseURL}/css/ftl/single-object-style.css" type="text/css">
    
    <title>${object.name} - User</title>
</head>

<body>
    ${common_header?default("")}
    
    <h1>${object.name} - User</h1>
    
    <form id="update-object" method="post">
        <fieldset>
            <legend>User Info</legend>
            <ol>
                <li>
                    <label>ID</label>
                    <input name='id' type="text" value='${object.id}' readonly='readonly'/>
                </li>
                <li>
                    <label>Name</label>
                    <input name='name' type='text' value='${object.name}' autofocus required/>
                </li>
                <li>
                    <label>Username</label>
                    <input name='username' type='text' value='${object.username}' autofocus required/>
                </li>
                <li>
                    <label>Email</label>
                    <input name='email' type='text' value='${object.email}' autofocus required/>
                </li>
                <li>
                    <label>Company</label>
                    <input name='company' type='text' value='${object.company}' autofocus/>
                </li>
                <li>
                    <label>Website</label>
                    <input name='website' type='text' value='${object.website}' autofocus/>
                </li>
                <li>
                    <label>Phone</label>
                    <input name='phone' type='text' value='${object.phone}' autofocus/>
                </li>
                <li>
                    <label>Subscribe to Magazine?</label>
                    <input name='isSubscribeToMagazine' type='text' value='${object.isSubscribeToMagazine}' autofocus required/>
                </li>
                <li>
                    <label>Tags</label>
                    <input name='tags' type='text' value='${object.tags}' autofocus/>
                </li>
                <li>
                    <label>How found out about us?</label>
                    <input name='howFoundUs' type='text' value='${object.howFoundUs}' autofocus/>
                </li>
                <li>
                    <label>Is Administrator</label>
                    <input name='isAdmin' type='text' value='${object.isAdmin}' autofocus required/>
                </li>
                <li>
                    <label>Estimator Expiration</label>
                    <input name='estimatorExpiration' type='text' value='${object.estimatorExpiration}' autofocus required/>
                </li>
                <li>
                    <label>Is EULA Accepted</label>
                    <input name='isEulaAccepted' type='text' value='${object.isEulaAccepted}' autofocus required/>
                </li>
                <li>
                    <label>Last Updated</label>
                    <input name='lastUpdateAt' type='text' value='${object.lastUpdateAt}' readonly='readonly'/>
                </li>
                <li>
                    <label>Site License Expiration</label>
                    <input name='siteLicenseExpiration' type='text' value='${object.siteLicenseExpiration}' autofocus required/>
                </li>
                <li>
                    <label>Date Created</label>
                    <input name='dateCreated' type='text' value='${object.dateCreated}' readonly='readonly'/>
                </li>
            </ol>
        </fieldset>
        <fieldset>
            <ol>
                <li>
                    <label>Old Password</label>
                    <input name='oldPassword' type='password' value=''/>
                </li>
                <li>
                    <label>Password</label>
                    <input name='password' type='password' value=''/>
                </li>
                <li>
                    <label>Verify Password</label>
                    <input name='verifyPassword' type='password' value=''/>
                </li>
            </ol>
        </fieldset>
        <fieldset>
            <button type='submit'>Update</button>
        </fieldset>
    </form>
</body>

</html>
