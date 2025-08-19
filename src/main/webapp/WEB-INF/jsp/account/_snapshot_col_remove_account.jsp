<div class="col col-cell-${deviceType} short-${deviceType} ${regularClass}">
    <span style="cursor: pointer;"
          title="Delete Account"
          onclick="removeAccountFromSnapshot(${snapshot.id}, ${snapshot.old}, ${entity.accountId}, '${fn:replace(entity.name, '\'', '\\\'')}');">
        &#128465;&#65039;
    </span>
</div>
