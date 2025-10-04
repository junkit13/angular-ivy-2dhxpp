document.addEventListener('DOMContentLoaded', () => {
    const addButton = document.getElementById('addEnvironment');
    const tableBody = document.getElementById('environmentTableBody');

    function refreshRemoveButtons() {
        const rows = tableBody.querySelectorAll('tr');
        rows.forEach(row => {
            const button = row.querySelector('.remove-row');
            if (button) {
                button.disabled = rows.length === 1;
            }
        });
    }

    function createRow(value = '') {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td><input type="text" name="envIds" value="${value}" required></td>
            <td><button type="button" class="remove-row">Remove</button></td>
        `;
        row.querySelector('.remove-row').addEventListener('click', () => {
            if (tableBody.querySelectorAll('tr').length > 1) {
                row.remove();
                refreshRemoveButtons();
            }
        });
        tableBody.appendChild(row);
        refreshRemoveButtons();
    }

    if (addButton) {
        addButton.addEventListener('click', () => createRow());
    }

    tableBody.querySelectorAll('.remove-row').forEach(button => {
        button.addEventListener('click', (event) => {
            const rows = tableBody.querySelectorAll('tr');
            if (rows.length <= 1) {
                return;
            }
            event.target.closest('tr').remove();
            refreshRemoveButtons();
        });
    });

    refreshRemoveButtons();
});
