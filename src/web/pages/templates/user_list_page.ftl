<html>
    <head>
        <title>Users</title>
        ${common_css?default("")}
    </head>

    <body>
    ${common_header?default("")}
    <h1>Users</h1>
    
    <h2><a href="user/single/create">Create New User</a></h2>
    <a href="${nextPageUri}">Next Page</a>
    
    <div class="object-list">
        <table>
            <tr>
                <th>User ID</th> <th>Name</th> <th>Username</th> <th>Email</th>
                <th>Estimator Expiration</th> <th>Site License Expiration</th>
                <th>Date Created</th>
            </tr>
            <#list objects as object>
                <tr>
                    <td><a href="user/single/${object.id}">${object.id}</a></td>
                    <td><a href="user/single/${object.id}">${object.name}</a></td>
                    <td>${object.username}</td>
                    <td>${object.email}</td>
                    <td>${object.estimatorExpiration}</td>
                    <td>${object.siteLicenseExpiration}</td>
                    <td>${object.dateCreated}</td>
                </tr>
            </#list>
        </table>
    </div>

    </body>

</html>
