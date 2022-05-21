<div class="col col-cell-${deviceType} short-${deviceType} ${regularClass}">
    <div class="delete-icon">
        <a href="#" onclick="removeAccountFromSnapshot(${snapshot.id}, ${entity.accountId}, '${fn:replace(entity.name, '\'', '\\\'')}');"
           style="text-decoration: none;">&#128465;&#65039;</a>
    </div>
</div>
