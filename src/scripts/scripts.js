export function showError(message) {
    const errorMessageElement = document.getElementById("error-message");
    const errorText = document.getElementById("error-text");

    errorText.textContent = message;
    errorMessageElement.style.display = "block";
}

export function hideError() {
    const errorMessageElement = document.getElementById("error-message");
    errorMessageElement.style.display = "none";
}

const UI = {
    showError: (msg) => {
        DOM.setText(DOM.get("error-text"), msg);
        DOM.show(DOM.get("error-message"));
    },
    hideError: () => DOM.hide(DOM.get("error-message")),

    toast: (message, duration = 3000) => {
        const toast = DOM.get("toast");
        toast.textContent = message;
        toast.style.display = "block";
        toast.style.opacity = "1";

        setTimeout(() => {
            toast.style.opacity = "0";
            setTimeout(() => {
                toast.style.display = "none";
            }, 500); // tempo para desaparecer suavemente
        }, duration);
    },
};

