<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <link rel="stylesheet" href="${baseURL}/css/ftl/single-object-style.css" type="text/css">
    
    <title>${object.name} - Project</title>
</head>

<body>
    ${common_header?default("")}
    
    <h1>${object.name} - Project</h1>
    <p>
        <a href="./${object.id}/HersReport.pdf" target='_blank'>Generate Hers Report</a>
    </p>
    
    <form id="update-object" method="post">
        <fieldset>
            <legend>Project Information</legend>
            <ol>
                <li>
                    <label>ID</label>
                    <input name='id' type="text" value='${object.id}' readonly/>
                </li>
                <li>
                    <label>Name</label>
                    <input name='name' type='text' value='${object.name}' autofocus required/>
                </li>
                <li>
                    <label>Description</label>
                    <input name='description' type='text' value='${object.description}' autofocus required/>
                </li>
                <li>
                    <label>Last Update Date</label>
                    <input name='lastUpdateAt' type='text' value='${object.lastUpdateAt}' readonly/>
                </li>
                <li>
                    <label>Created By</label>
                    <input name='createdBy' type='text' value='${object.createdBy}' readonly/>
                </li>
                <li>
                    <label>Component Library ID</label>
                    <input name='componentLibrary' type='text' value='${object.componentLibrary}'  autofocus required/>
                </li>
                <li>
                    <label>Analyst Expiration</label>
                    <input name='analystExpiration' type='text' value='${object.analystExpiration}'  autofocus required/>
                </li>
            </ol>
        </fieldset>
        <fieldset>
            <button type='submit'>Update</button>
        </fieldset>
    </form>
</body>

</html>
