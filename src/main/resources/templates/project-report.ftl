<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Project Report</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 40px;
        }

        h1, h2, h3 {
            color: #333;
        }

        .section {
            margin-bottom: 20px;
        }

        .bold {
            font-weight: bold;
        }

        .task {
            margin-left: 20px;
        }
    </style>
</head>
<body>

<h1>Project Presentation</h1>

<div class="section">
    <h2>General Information</h2>
    <p><span class="bold">Project Name:</span> ${project.name}</p>
    <p><span class="bold">Description:</span> ${project.description}</p>
    <p><span class="bold">Created At:</span> ${project.createdAt}</p>
    <p><span class="bold">Status:</span> ${project.status}</p>
    <p><span class="bold">Owner:</span> ${ownerName}</p>
</div>

<div class="section">
    <h2>Project Team</h2>
    <ul>
        <#list project.teams as team>
        <li><span class="bold">Team ${team.id}:</span>
            <ul>
                <#list team.teamMembers as member>
                <li class="task">${member.nickname} - ${member.roles}</li>
            </#list>
    </ul>
    </li>
</#list>
</ul>
</div>

<div class="section">
    <h2>Achievements</h2>
    <#if completedTasks?size == 0>
    <p>No achievements recorded.</p>
    <#else>
    <ul>
        <#list completedTasks as task>
        <li class="task">${task.name} <#if task.description??>(${task.description})</#if></li>
    </#list>
    </ul>
</#if>
</div>

<div class="section">
    <h2>Statistics</h2>
    <p><span class="bold">Completed Tasks:</span> ${completedTasks?size}</p>
    <p><span class="bold">Number of Teams:</span> ${project.teams?size}</p>
    <p><span class="bold">Total Team Members:</span> ${totalTeamMembers}</p>
</div>

<div class="section">
    <h2>Related Projects</h2>
    <p><span class="bold">Parent Project:</span> ${project.parentProject?ifExists.name! 'No Parent Project'}</p>
    <#if project.children?size == 0>
    <p>No Child Projects</p>
    <#else>
    <ul>
        <#list project.children as child>
        <li class="task">${child.name}</li>
    </#list>
    </ul>
</#if>
</div>

</body>
</html>

