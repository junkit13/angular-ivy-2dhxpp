document.addEventListener('DOMContentLoaded', () => {
    const modal = document.getElementById('addModal');
    const openModalBtn = document.getElementById('openAddModal');
    const closeModalBtn = document.getElementById('closeAddModal');
    const modeRadios = document.querySelectorAll('input[name="mode"]');
    const singleGroup = document.getElementById('singleAccountGroup');
    const fileGroup = document.getElementById('fileUploadGroup');
    const addForm = document.getElementById('addForm');
    const submitAdd = document.getElementById('submitAdd');
    const modalEnv = document.getElementById('modalEnv');
    const modalAccount = document.getElementById('modalAccountId');
    const fileInput = document.getElementById('fileInput');
    const dropZone = document.getElementById('dropZone');
    const selectAll = document.getElementById('selectAll');
    const rowCheckboxes = document.querySelectorAll('.row-checkbox');
    const deleteButton = document.getElementById('deleteButton');

    function toggleModal(show) {
        if (!modal) {
            return;
        }
        modal.hidden = !show;
        if (show) {
            modal.classList.add('visible');
        } else {
            modal.classList.remove('visible');
            if (addForm) {
                addForm.reset();
            }
            if (singleGroup) {
                singleGroup.hidden = false;
            }
            if (fileGroup) {
                fileGroup.hidden = true;
            }
            submitAdd.disabled = true;
        }
    }

    function updateModeVisibility() {
        const selected = document.querySelector('input[name="mode"]:checked');
        if (!selected) {
            return;
        }
        if (selected.value === 'single') {
            singleGroup.hidden = false;
            fileGroup.hidden = true;
        } else {
            singleGroup.hidden = true;
            fileGroup.hidden = false;
        }
        validateAddForm();
    }

    function validateAddForm() {
        const envSelected = modalEnv && modalEnv.value;
        const mode = document.querySelector('input[name="mode"]:checked');
        let valid = !!envSelected && !!mode;
        if (valid && mode.value === 'single') {
            valid = modalAccount && modalAccount.value.trim().length > 0;
        }
        if (valid && mode.value === 'file') {
            valid = fileInput && fileInput.files && fileInput.files.length > 0;
        }
        if (submitAdd) {
            submitAdd.disabled = !valid;
        }
    }

    if (openModalBtn) {
        openModalBtn.addEventListener('click', () => toggleModal(true));
    }
    if (closeModalBtn) {
        closeModalBtn.addEventListener('click', () => toggleModal(false));
    }
    if (modal) {
        modal.addEventListener('click', (event) => {
            if (event.target === modal) {
                toggleModal(false);
            }
        });
    }

    modeRadios.forEach(radio => radio.addEventListener('change', updateModeVisibility));
    if (modalEnv) {
        modalEnv.addEventListener('change', validateAddForm);
    }
    if (modalAccount) {
        modalAccount.addEventListener('input', validateAddForm);
    }
    if (fileInput) {
        fileInput.addEventListener('change', validateAddForm);
    }

    if (dropZone) {
        dropZone.addEventListener('click', () => fileInput && fileInput.click());
        dropZone.addEventListener('dragover', (event) => {
            event.preventDefault();
            dropZone.classList.add('dragging');
        });
        dropZone.addEventListener('dragleave', () => dropZone.classList.remove('dragging'));
        dropZone.addEventListener('drop', (event) => {
            event.preventDefault();
            dropZone.classList.remove('dragging');
            if (event.dataTransfer && event.dataTransfer.files.length > 0) {
                fileInput.files = event.dataTransfer.files;
                validateAddForm();
            }
        });
    }

    function updateDeleteButton() {
        if (!deleteButton) {
            return;
        }
        const selected = Array.from(document.querySelectorAll('.row-checkbox')).some(cb => cb.checked);
        deleteButton.disabled = !selected;
        if (openModalBtn) {
            openModalBtn.disabled = selected;
        }
    }

    if (selectAll) {
        selectAll.addEventListener('change', (event) => {
            const checked = event.target.checked;
            document.querySelectorAll('.row-checkbox').forEach(cb => {
                cb.checked = checked;
            });
            updateDeleteButton();
        });
    }

    rowCheckboxes.forEach(cb => cb.addEventListener('change', () => {
        if (!cb.checked && selectAll) {
            selectAll.checked = false;
        }
        updateDeleteButton();
    }));

    updateModeVisibility();
    updateDeleteButton();
});

function submitEnvUpdate(accountId, envId) {
    const form = document.getElementById('envUpdateForm');
    if (!form) {
        return;
    }
    document.getElementById('envAccountId').value = accountId;
    document.getElementById('envValue').value = envId;
    form.submit();
}
