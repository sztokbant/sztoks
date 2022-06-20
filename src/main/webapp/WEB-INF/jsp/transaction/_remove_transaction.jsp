<div class="col col-cell-${deviceType} short-${deviceType} ${regularClass}">
    <span style="cursor: pointer;"
          onclick="removeTransaction(${entity.snapshotId}, ${entity.id}, '${entity.type}', '${fn:replace(entity.description, '\'', '\\\'')}');"
       style="text-decoration: none;">&#128465;&#65039;
    </span>
</div>
