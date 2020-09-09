function remove_account_from_snapshot(accountName, accountId, snapshotId) {
    var doRemove = confirm('Are you sure you want to remove "' + accountName + '" from this snapshot?');
    if (doRemove) {
        document.forms['remove_account_' + snapshotId + '_' + accountId].submit();
    }
}
