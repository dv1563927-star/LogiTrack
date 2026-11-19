/* LogiTrack — login y solicitud de registro */

const API = "http://localhost:8080";

/* ---------- Pestañas ---------- */
const tabLogin = document.getElementById("tab-login");
const tabRegistro = document.getElementById("tab-registro");
const panelLogin = document.getElementById("panel-login");
const panelRegistro = document.getElementById("panel-registro");

function activarPestana(cual) {
  const esLogin = cual === "login";

  tabLogin.setAttribute("aria-selected", String(esLogin));
  tabRegistro.setAttribute("aria-selected", String(!esLogin));
  tabLogin.tabIndex = esLogin ? 0 : -1;
  tabRegistro.tabIndex = esLogin ? -1 : 0;

  panelLogin.hidden = !esLogin;
  panelRegistro.hidden = esLogin;
}

tabLogin.addEventListener("click", () => activarPestana("login"));
tabRegistro.addEventListener("click", () => activarPestana("registro"));

/* ---------- Utilidades ---------- */
function mostrarAviso(idAviso, idTexto, mensaje, tipo) {
  const aviso = document.getElementById(idAviso);
  const texto = document.getElementById(idTexto);

  if (!mensaje) {
    aviso.hidden = true;
    return;
  }
  texto.textContent = mensaje;
  aviso.className = "aviso aviso--" + tipo;
  aviso.hidden = false;
}

function cargando(boton, estado) {
  boton.disabled = estado;
  boton.dataset.cargando = estado ? "true" : "false";
}

async function leerError(respuesta) {
  try {
    const cuerpo = await respuesta.json();
    if (cuerpo.errors) {
      return Object.values(cuerpo.errors).join(" · ");
    }
    return cuerpo.message || "Error " + respuesta.status;
  } catch (e) {
    return "Error " + respuesta.status;
  }
}

/* ---------- Login ---------- */
document.getElementById("form-login").addEventListener("submit", async (evento) => {
  evento.preventDefault();

  const boton = document.getElementById("btn-login");
  const usuario = document.getElementById("login-usuario").value.trim();
  const clave = document.getElementById("login-clave").value;

  mostrarAviso("login-aviso", "login-aviso-texto", "", "");

  if (!usuario || !clave) {
    mostrarAviso("login-aviso", "login-aviso-texto", "Completá usuario y contraseña.", "error");
    return;
  }

  cargando(boton, true);
  try {
    const respuesta = await fetch(API + "/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username: usuario, password: clave })
    });

    if (!respuesta.ok) {
      mostrarAviso("login-aviso", "login-aviso-texto", await leerError(respuesta), "error");
      return;
    }

    const datos = await respuesta.json();
    localStorage.setItem("logitrack_token", datos.token);
    localStorage.setItem("logitrack_usuario", datos.username);
    localStorage.setItem("logitrack_rol", datos.rol);

    window.location.href = "dashboard.html";

  } catch (e) {
    mostrarAviso("login-aviso", "login-aviso-texto",
      "No se pudo conectar con la API. Verificá que esté corriendo en localhost:8080.", "error");
  } finally {
    cargando(boton, false);
  }
});

/* ---------- Registro ---------- */
document.getElementById("form-registro").addEventListener("submit", async (evento) => {
  evento.preventDefault();

  const boton = document.getElementById("btn-registro");
  const usuario = document.getElementById("reg-usuario").value.trim();
  const clave = document.getElementById("reg-clave").value;
  const clave2 = document.getElementById("reg-clave2").value;
  const errorClave = document.getElementById("reg-error-clave");

  mostrarAviso("registro-aviso", "registro-aviso-texto", "", "");
  mostrarAviso("registro-exito", "registro-exito-texto", "", "");
  errorClave.hidden = true;

  if (!usuario || !clave || !clave2) {
    mostrarAviso("registro-aviso", "registro-aviso-texto", "Completá todos los campos.", "error");
    return;
  }
  if (clave !== clave2) {
    errorClave.hidden = false;
    document.getElementById("reg-clave2").setAttribute("aria-invalid", "true");
    return;
  }
  document.getElementById("reg-clave2").removeAttribute("aria-invalid");

  cargando(boton, true);
  try {
    const respuesta = await fetch(API + "/auth/register", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username: usuario, password: clave })
    });

    if (!respuesta.ok) {
      mostrarAviso("registro-aviso", "registro-aviso-texto", await leerError(respuesta), "error");
      return;
    }

    const datos = await respuesta.json();
    mostrarAviso("registro-exito", "registro-exito-texto",
      datos.mensaje || "Solicitud enviada. Esperá la aprobación del administrador.", "exito");

    document.getElementById("form-registro").reset();

  } catch (e) {
    mostrarAviso("registro-aviso", "registro-aviso-texto",
      "No se pudo conectar con la API. Verificá que esté corriendo en localhost:8080.", "error");
  } finally {
    cargando(boton, false);
  }
});
