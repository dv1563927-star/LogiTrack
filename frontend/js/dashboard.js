/* LogiTrack — panel administrativo */

const API = "http://localhost:8080";

const token = localStorage.getItem("logitrack_token");
const usuario = localStorage.getItem("logitrack_usuario");
const rol = localStorage.getItem("logitrack_rol");

if (!token) { window.location.href = "index.html"; }

const esAdmin = rol === "ADMIN";

/* ================= Utilidades ================= */

function esc(valor) {
  return String(valor ?? "").replace(/[&<>"']/g, (c) => ({
    "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
  }[c]));
}

function dinero(valor) {
  if (valor === null || valor === undefined) return "—";
  return "$" + Number(valor).toLocaleString("es-CO", { maximumFractionDigits: 0 });
}

function fecha(valor) {
  if (!valor) return "—";
  return String(valor).replace("T", " ").slice(0, 16);
}

function salir() {
  localStorage.clear();
  window.location.href = "index.html";
}

async function pedir(ruta, opciones = {}) {
  const respuesta = await fetch(API + ruta, {
    ...opciones,
    headers: {
      "Content-Type": "application/json",
      "Authorization": "Bearer " + token,
      ...(opciones.headers || {})
    }
  });

  if (!respuesta.ok) {
    let mensaje = "Error " + respuesta.status;
    try {
      const cuerpo = await respuesta.json();
      mensaje = cuerpo.errors ? Object.values(cuerpo.errors).join(" · ") : (cuerpo.message || mensaje);
    } catch (e) { /* respuesta sin cuerpo */ }

    if (respuesta.status === 403) {
      mensaje = "No tenés permisos para esta operación.";
    }
    throw new Error(mensaje);
  }

  if (respuesta.status === 204) return null;
  return respuesta.json();
}

function panel(titulo, subtitulo, contenido) {
  return `<div class="panel">
    <div class="panel__cabecera"><div><h3>${esc(titulo)}</h3><p>${esc(subtitulo)}</p></div></div>
    ${contenido}
  </div>`;
}

function tabla(cabeceras, filas) {
  if (!filas.length) {
    return `<div class="celda-vacia"><strong>Sin registros todavía</strong>
      ${esAdmin ? "Creá el primero con el formulario de arriba." : "Cuando existan datos van a aparecer acá."}</div>`;
  }
  const th = cabeceras.map((c) => `<th>${esc(c)}</th>`).join("");
  const tr = filas.map((fila) => "<tr>" + fila.map((c) => `<td>${c}</td>`).join("") + "</tr>").join("");
  return `<div class="tabla-envoltura"><table><thead><tr>${th}</tr></thead><tbody>${tr}</tbody></table></div>`;
}

function mensaje(texto, tipo) {
  return `<div class="aviso aviso--${tipo}">${esc(texto)}</div>`;
}

/* ================= Vistas ================= */

async function vistaBodegas() {
  const bodegas = await pedir("/api/bodega");

  const formulario = esAdmin ? panel("Nueva bodega", "Los campos son obligatorios", `
    <form id="form-bodega" style="padding:18px">
      <div class="form-fila">
        <div class="campo"><label for="b-nombre">Nombre</label><input id="b-nombre" required placeholder="Bodega Occidente"></div>
        <div class="campo"><label for="b-ubicacion">Ubicación</label><input id="b-ubicacion" required placeholder="Pereira"></div>
        <div class="campo"><label for="b-capacidad">Capacidad</label><input id="b-capacidad" type="number" min="1" required placeholder="1500"></div>
        <div class="campo"><label for="b-encargado">Encargado</label><input id="b-encargado" required placeholder="Ana Torres"></div>
      </div>
      <div style="margin-top:16px;display:flex;align-items:center;gap:12px">
        <button type="submit" class="boton boton--primario">Guardar bodega</button>
        <span id="b-aviso" class="ayuda"></span>
      </div>
    </form>`) : "";

  return formulario + panel("Bodegas registradas", bodegas.length + " en total", tabla(
    ["ID", "Nombre", "Ubicación", "Capacidad", "Encargado"],
    bodegas.map((b) => [esc(b.id), esc(b.nombre), esc(b.ubicacion), esc(b.capacidad), esc(b.encargado)])
  ));
}

async function vistaProductos() {
  const productos = await pedir("/api/producto");

  const formulario = esAdmin ? panel("Nuevo producto", "El stock se actualiza solo con los movimientos", `
    <form id="form-producto" style="padding:18px">
      <div class="form-fila">
        <div class="campo"><label for="p-nombre">Nombre</label><input id="p-nombre" required placeholder="Monitor 27"></div>
        <div class="campo"><label for="p-categoria">Categoría</label><input id="p-categoria" required placeholder="Pantallas"></div>
        <div class="campo"><label for="p-stock">Stock inicial</label><input id="p-stock" type="number" min="0" required placeholder="10"></div>
        <div class="campo"><label for="p-precio">Precio</label><input id="p-precio" type="number" min="1" step="0.01" required placeholder="450000"></div>
      </div>
      <div style="margin-top:16px;display:flex;align-items:center;gap:12px">
        <button type="submit" class="boton boton--primario">Guardar producto</button>
        <span id="p-aviso" class="ayuda"></span>
      </div>
    </form>`) : "";

  return formulario + panel("Catálogo", productos.length + " productos", tabla(
    ["ID", "Nombre", "Categoría", "Stock", "Precio"],
    productos.map((p) => [esc(p.id), esc(p.nombre), esc(p.categoria),
      `<span class="dato-num">${esc(p.stock)}</span>`,
      `<span class="dato-num">${dinero(p.precio)}</span>`])
  ));
}

async function vistaInventario() {
  const filas = await pedir("/api/inventario");

  return panel("Stock por bodega", filas.length + " registros", tabla(
    ["Bodega", "Producto", "Cantidad"],
    filas.map((f) => [esc(f.bodega?.nombre), esc(f.producto?.nombre),
      `<span class="dato-num">${esc(f.cantidad)}</span>`])
  ));
}

async function vistaMovimiento() {
  const movimientos = await pedir("/api/movimiento");

  const formulario = panel("Nuevo movimiento", "El usuario responsable se toma del token", `
    <form id="form-movimiento" style="padding:18px">
      <div class="form-fila">
        <div class="campo"><label for="m-tipo">Tipo</label>
          <select id="m-tipo">
            <option value="ENTRADA">ENTRADA</option>
            <option value="SALIDA">SALIDA</option>
            <option value="TRANSFERENCIA">TRANSFERENCIA</option>
          </select>
        </div>
        <div class="campo"><label for="m-origen">Bodega origen</label><input id="m-origen" type="number" min="1" placeholder="Solo SALIDA y TRANSFERENCIA"></div>
        <div class="campo"><label for="m-destino">Bodega destino</label><input id="m-destino" type="number" min="1" placeholder="Solo ENTRADA y TRANSFERENCIA"></div>
      </div>
      <div style="margin-top:16px;display:flex;align-items:center;gap:12px">
        <button type="submit" class="boton boton--primario">Registrar movimiento</button>
        <span id="m-aviso" class="ayuda"></span>
      </div>
    </form>`);

  return formulario + panel("Movimientos", movimientos.length + " registrados", tabla(
    ["ID", "Fecha", "Tipo", "Usuario", "Origen", "Destino"],
    movimientos.map((m) => [esc(m.id), fecha(m.fecha),
      `<span class="eti eti--neutra">${esc(m.tipo)}</span>`,
      esc(m.usuario?.username),
      esc(m.bodegaOrigen?.nombre ?? "—"),
      esc(m.bodegaDestino?.nombre ?? "—")])
  ));
}

async function vistaDetalle() {
  const detalles = await pedir("/api/detalle");

  const formulario = panel("Agregar producto al movimiento", "Acá es donde cambia el stock", `
    <form id="form-detalle" style="padding:18px">
      <div class="form-fila">
        <div class="campo"><label for="d-movimiento">ID del movimiento</label><input id="d-movimiento" type="number" min="1" required placeholder="1"></div>
        <div class="campo"><label for="d-producto">ID del producto</label><input id="d-producto" type="number" min="1" required placeholder="3"></div>
        <div class="campo"><label for="d-cantidad">Cantidad</label><input id="d-cantidad" type="number" min="1" required placeholder="10"></div>
      </div>
      <div style="margin-top:16px;display:flex;align-items:center;gap:12px">
        <button type="submit" class="boton boton--primario">Agregar detalle</button>
        <span id="d-aviso" class="ayuda"></span>
      </div>
    </form>`);

  return formulario + panel("Detalles de movimiento", detalles.length + " registrados", tabla(
    ["ID", "Movimiento", "Producto", "Cantidad"],
    detalles.map((d) => [esc(d.id),
      "#" + esc(d.movimiento?.id) + " · " + esc(d.movimiento?.tipo),
      esc(d.producto?.nombre),
      `<span class="dato-num">${esc(d.cantidad)}</span>`])
  ));
}

async function vistaSolicitudes() {
  const pendientes = await pedir("/auth/pendientes");

  const filas = pendientes.map((u) => [
    esc(u.id), esc(u.username),
    `<span class="eti eti--${u.estado === "PENDIENTE" ? "pendiente" : u.estado === "ACTIVO" ? "activo" : "rechazado"}">${esc(u.estado)}</span>`,
    `<span style="display:flex;gap:6px">
       <button class="boton boton--primario boton--sm" data-aprobar="${esc(u.id)}">Aprobar</button>
       <button class="boton boton--peligro boton--sm" data-rechazar="${esc(u.id)}">Rechazar</button>
     </span>`
  ]);

  return panel("Solicitudes de registro", pendientes.length + " pendientes",
    `<div id="sol-aviso" style="padding:14px 18px 0"></div>` +
    tabla(["ID", "Usuario", "Estado", "Acciones"], filas));
}

async function vistaAuditoria() {
  const registros = await pedir("/api/auditoria");
  const ultimos = registros.slice().reverse().slice(0, 100);

  const clase = { INSERT: "insert", UPDATE: "update", DELETE: "delete" };

  return panel("Registro de auditoría", registros.length + " eventos (mostrando los últimos 100)", tabla(
    ["Fecha", "Operación", "Entidad", "Usuario", "Antes", "Después"],
    ultimos.map((a) => [
      fecha(a.fechaHora),
      `<span class="eti eti--${clase[a.tipoOperacion] || "neutra"}">${esc(a.tipoOperacion)}</span>`,
      esc(a.entidadAfectada),
      esc(a.usuario),
      `<span style="color:var(--tinta-suave);font-size:var(--t-xs)">${esc(a.valoresAnteriores ?? "—")}</span>`,
      `<span style="color:var(--tinta-suave);font-size:var(--t-xs)">${esc(a.valoresNuevos ?? "—")}</span>`
    ])
  ));
}

async function vistaReporte() {
  const reporte = await pedir("/api/reporte");

  const totalUnidades = reporte.stockPorBodega.reduce((s, b) => s + (b.totalUnidades || 0), 0);
  const totalValor = reporte.stockPorBodega.reduce((s, b) => s + Number(b.valorTotal || 0), 0);

  const metricas = `<div class="rejilla">
    <div class="metrica"><span class="metrica__rotulo">Bodegas activas</span><span class="metrica__valor">${reporte.stockPorBodega.length}</span><span class="metrica__nota">con inventario registrado</span></div>
    <div class="metrica"><span class="metrica__rotulo">Unidades en total</span><span class="metrica__valor">${totalUnidades}</span><span class="metrica__nota">suma de todas las bodegas</span></div>
    <div class="metrica"><span class="metrica__rotulo">Valor del inventario</span><span class="metrica__valor">${dinero(totalValor)}</span><span class="metrica__nota">stock × precio</span></div>
  </div>`;

  return metricas +
    panel("Stock por bodega", "Unidades y valor de cada bodega", tabla(
      ["Bodega", "Unidades", "Valor"],
      reporte.stockPorBodega.map((b) => [esc(b.bodega),
        `<span class="dato-num">${esc(b.totalUnidades)}</span>`,
        `<span class="dato-num">${dinero(b.valorTotal)}</span>`])
    )) +
    panel("Productos más movidos", "Los 5 con más unidades movidas", tabla(
      ["Producto", "Unidades movidas"],
      reporte.productosMasMovidos.map((p) => [esc(p.producto),
        `<span class="dato-num">${esc(p.totalMovido)}</span>`])
    ));
}

const VISTAS = {
  bodegas:     { titulo: "Bodegas",     desc: "Cada bodega con su ubicación, capacidad y encargado.", render: vistaBodegas },
  productos:   { titulo: "Productos",   desc: "Catálogo con el stock total y el precio de cada producto.", render: vistaProductos },
  inventario:  { titulo: "Inventario",  desc: "Cuántas unidades de cada producto hay en cada bodega.", render: vistaInventario },
  movimiento:  { titulo: "Movimientos", desc: "Entradas, salidas y transferencias entre bodegas.", render: vistaMovimiento },
  detalle:     { titulo: "Detalle",     desc: "Productos y cantidades de cada movimiento. Acá se mueve el stock.", render: vistaDetalle },
  solicitudes: { titulo: "Solicitudes", desc: "Pedidos de registro esperando aprobación.", render: vistaSolicitudes, soloAdmin: true },
  auditoria:   { titulo: "Auditoría",   desc: "Cada cambio sobre la base, con su autor y los valores antes y después.", render: vistaAuditoria, soloAdmin: true },
  reporte:     { titulo: "Reporte",     desc: "Resumen general del inventario y de los productos más movidos.", render: vistaReporte, soloAdmin: true }
};

/* ================= Navegación ================= */

let vistaActual = null;

async function mostrar(nombre) {
  const vista = VISTAS[nombre];
  if (!vista) return;
  if (vista.soloAdmin && !esAdmin) return;

  vistaActual = nombre;

  document.querySelectorAll(".barra__enlace").forEach((b) => {
    if (b.dataset.vista === nombre) b.setAttribute("aria-current", "page");
    else b.removeAttribute("aria-current");
  });

  document.getElementById("titulo-vista").textContent = vista.titulo;
  document.getElementById("desc-vista").textContent = vista.desc;

  const contenedor = document.getElementById("vista");
  contenedor.innerHTML = `<div class="panel"><div class="panel__cabecera"><div><h3>Cargando</h3><p>Un momento…</p></div></div><div style="padding:18px"><div class="esqueleto" style="width:60%"></div></div></div>`;

  try {
    contenedor.innerHTML = await vista.render();
    conectarFormularios(nombre);
  } catch (error) {
    contenedor.innerHTML = panel(vista.titulo, "No se pudo cargar", `<div style="padding:18px">${mensaje(error.message, "error")}</div>`);
  }
}

/* ================= Formularios ================= */

function avisar(idAviso, texto, tipo) {
  const el = document.getElementById(idAviso);
  if (!el) return;
  if (!texto) { el.textContent = ""; el.className = "ayuda"; return; }
  el.textContent = texto;
  el.className = tipo === "error" ? "error-campo" : "ayuda";
}

function conectarFormularios(nombre) {

  if (nombre === "bodegas" && esAdmin) {
    document.getElementById("form-bodega").addEventListener("submit", async (e) => {
      e.preventDefault();
      const boton = e.target.querySelector("button");
      boton.disabled = true;
      avisar("b-aviso", "Guardando…");
      try {
        await pedir("/api/bodega", {
          method: "POST",
          body: JSON.stringify({
            nombre: document.getElementById("b-nombre").value.trim(),
            ubicacion: document.getElementById("b-ubicacion").value.trim(),
            capacidad: Number(document.getElementById("b-capacidad").value),
            encargado: document.getElementById("b-encargado").value.trim()
          })
        });
        mostrar("bodegas");
      } catch (error) {
        avisar("b-aviso", error.message, "error");
        boton.disabled = false;
      }
    });
  }

  if (nombre === "productos" && esAdmin) {
    document.getElementById("form-producto").addEventListener("submit", async (e) => {
      e.preventDefault();
      const boton = e.target.querySelector("button");
      boton.disabled = true;
      avisar("p-aviso", "Guardando…");
      try {
        await pedir("/api/producto", {
          method: "POST",
          body: JSON.stringify({
            nombre: document.getElementById("p-nombre").value.trim(),
            categoria: document.getElementById("p-categoria").value.trim(),
            stock: Number(document.getElementById("p-stock").value),
            precio: Number(document.getElementById("p-precio").value)
          })
        });
        mostrar("productos");
      } catch (error) {
        avisar("p-aviso", error.message, "error");
        boton.disabled = false;
      }
    });
  }

  if (nombre === "movimiento") {
    document.getElementById("form-movimiento").addEventListener("submit", async (e) => {
      e.preventDefault();
      const boton = e.target.querySelector("button");
      boton.disabled = true;
      avisar("m-aviso", "Registrando…");

      const origen = document.getElementById("m-origen").value;
      const destino = document.getElementById("m-destino").value;

      const cuerpo = { tipo: document.getElementById("m-tipo").value };
      if (origen) cuerpo.bodegaOrigenId = Number(origen);
      if (destino) cuerpo.bodegaDestinoId = Number(destino);

      try {
        await pedir("/api/movimiento", { method: "POST", body: JSON.stringify(cuerpo) });
        mostrar("movimiento");
      } catch (error) {
        avisar("m-aviso", error.message, "error");
        boton.disabled = false;
      }
    });
  }

  if (nombre === "detalle") {
    document.getElementById("form-detalle").addEventListener("submit", async (e) => {
      e.preventDefault();
      const boton = e.target.querySelector("button");
      boton.disabled = true;
      avisar("d-aviso", "Agregando…");
      try {
        await pedir("/api/detalle", {
          method: "POST",
          body: JSON.stringify({
            movimientoId: Number(document.getElementById("d-movimiento").value),
            productoId: Number(document.getElementById("d-producto").value),
            cantidad: Number(document.getElementById("d-cantidad").value)
          })
        });
        mostrar("detalle");
      } catch (error) {
        avisar("d-aviso", error.message, "error");
        boton.disabled = false;
      }
    });
  }

  if (nombre === "solicitudes") {
    document.querySelectorAll("[data-aprobar]").forEach((b) => {
      b.addEventListener("click", () => resolverSolicitud(b.dataset.aprobar, "aprobar", b));
    });
    document.querySelectorAll("[data-rechazar]").forEach((b) => {
      b.addEventListener("click", () => resolverSolicitud(b.dataset.rechazar, "rechazar", b));
    });
  }
}

async function resolverSolicitud(id, accion, boton) {
  boton.disabled = true;
  try {
    await pedir(`/auth/${accion}/${id}`, { method: "POST" });
    mostrar("solicitudes");
  } catch (error) {
    const aviso = document.getElementById("sol-aviso");
    if (aviso) aviso.innerHTML = mensaje(error.message, "error");
    boton.disabled = false;
  }
}

/* ================= Arranque ================= */

document.querySelectorAll(".barra__enlace").forEach((boton) => {
  boton.addEventListener("click", () => mostrar(boton.dataset.vista));
});

document.getElementById("btn-salir").addEventListener("click", salir);

document.getElementById("usuario-nombre").textContent = usuario || "—";
document.getElementById("usuario-rol").textContent = rol || "—";
document.getElementById("inicial").textContent = (usuario || "?").charAt(0).toUpperCase();

if (!esAdmin) {
  document.querySelectorAll(".solo-admin").forEach((el) => el.remove());
}

mostrar(esAdmin ? "reporte" : "movimiento");
