
// Vista previa de la imagen elegida y límite de 512 KB
function mostrarImagen(input) {
    if (input.files && input.files[0]) {
        const file = input.files[0];
        if (file.size > 512 * 1024) {
            alert("La imagen no debe superar los 512 KB");
            input.value = "";
            return;
        }
        const reader = new FileReader();
        reader.onload = function (e) {
            document.getElementById("blah").src = e.target.result;
        };
        reader.readAsDataURL(file);
    }
}

// Ventana de confirmación para eliminar: recibe id y descripción del botón pulsado
document.addEventListener("DOMContentLoaded", function () {
    const confirmModal = document.getElementById("confirmModal");
    if (confirmModal) {
        confirmModal.addEventListener("show.bs.modal", function (event) {
            const button = event.relatedTarget;
            document.getElementById("modalId").value = button.getAttribute("data-bs-id");
            document.getElementById("modalDescripcion").textContent =
                button.getAttribute("data-bs-descripcion");
        });
    }
});