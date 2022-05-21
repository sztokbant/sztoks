<div class="col col-cell-${deviceType} short-${deviceType} ${regularClass}">
    <div class="delete-icon delete-icon-${deviceType}">
        <a href="#" onclick="removeTransaction(${entity.snapshotId}, ${entity.id}, '${entity.type}', '${fn:replace(entity.description, '\'', '\\\'')}');"
           style="text-decoration: none;">&#128465;&#65039;</a>
    </div>
</div>
