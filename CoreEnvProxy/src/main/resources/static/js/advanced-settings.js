document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.toggle-control input[type="checkbox"]').forEach(input => {
        const switchEl = input.closest('.switch');
        const chip = document.querySelector(`.toggle-chip[data-toggle-target="${input.id}"]`);

        const updateState = () => {
            if (switchEl) {
                switchEl.classList.toggle('checked', input.checked);
            }
            if (chip) {
                chip.textContent = input.checked ? 'on' : 'off';
                chip.classList.toggle('on', input.checked);
                chip.classList.toggle('off', !input.checked);
            }
        };

        input.addEventListener('change', updateState);
        updateState();
    });
});
