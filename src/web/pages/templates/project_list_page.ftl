<html>
    <head>
        <title>Projects</title>
        ${common_css?default("")}
    </head>

    <body>
    ${common_header?default("")}
    <h1>Projects</h1>
    
    <h2><a href="project/single/create">Create New Project</a></h2>
    <a href="${nextPageUri}">Next Page</a>
    
    <div class="object-list">
        <table>
            <tr>
                <th>ID</th> <th>Name</th> <th>Description</th> <th>Last Update</th> <th>Created By</th>
                <th>Component Library ID</th> <th>Analyst Expiration</th>
            </tr>
            <#list objects as object>
                <tr>
                    <td><a href="project/single/${object.id}">${object.id}</a></td>
                    <td>${object.name}</td>
                    <td>${object.description}</td>
                    <td>${object.lastUpdateAt}</td>
                    <td>${object.createdBy}</td>
                    <td>${object.componentLibrary}</td>
                    <td>${object.analystExpiration}</td>
                </tr>
            </#list>
        </table>
    </div>

    </body>

</html>
