<div class="col col-cell-${deviceType} short">
    <div class="delete-icon">
        <a href="#" onclick="removeTransaction(${entity.snapshotId}, ${entity.id}, '${entity.type}', '${fn:replace(entity.description, '\'', '\\\'')}');"
           style="text-decoration: none;">&#x26D4;</a>
    </div>
</div>
