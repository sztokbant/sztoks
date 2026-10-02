// Sanitizes pasting of non-number characters in number fields
function stripDecimalForText(text) {
    return text
        .replace(/^[^\d-]+/g, '')
        .replaceAll(',', '')
        .replaceAll('%', '')
        .trim();
}

function sanitizeNumberString(text) {
    if (text == null) return '';
    return stripDecimalForText(String(text).replace(/\s/g, ''));
}

document.addEventListener('paste', function (e) {
    const el = e.target;
    if (el.tagName !== 'INPUT' || el.type !== 'number') return;

    e.preventDefault();
    const pasted = (e.clipboardData || window.clipboardData).getData('text');
    el.value = sanitizeNumberString(pasted);
    el.dispatchEvent(new Event('input', { bubbles: true }));
    el.dispatchEvent(new Event('change', { bubbles: true }));
}, true);
