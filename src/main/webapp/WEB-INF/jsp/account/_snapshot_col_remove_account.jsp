<div class="col col-cell-${deviceType} short-${deviceType}">
    <div class="delete-icon">
        <a href="#" onclick="removeAccountFromSnapshot(${snapshot.id}, ${entity.accountId}, '${fn:replace(entity.name, '\'', '\\\'')}');"
           style="text-decoration: none;">&#x26D4;</a>
    </div>
</div>
