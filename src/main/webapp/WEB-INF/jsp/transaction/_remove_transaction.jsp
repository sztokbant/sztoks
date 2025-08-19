<div class="col col-cell-${deviceType} short-${deviceType} ${regularClass}">
    <span style="cursor: pointer;"
          title="Delete Transaction"
          onclick="removeTransaction(${entity.snapshotId}, ${snapshot.old}, ${entity.id}, '${entity.type}', '${fn:replace(entity.description, '\'', '\\\'')}');"
       style="text-decoration: none;">&#128465;&#65039;
    </span>
</div>
