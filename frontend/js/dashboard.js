/* LogiTrack — panel administrativo
 *
 * Flujo:
 *   Movimientos  -> se elige qué va a pasar y se registra todo junto
 *                   (tipo + bodegas + producto + cantidad).
 *   Detalle      -> solo lectura: se busca un movimiento por ID y se ve
 *                   qué se movió. No se crea ni se edita nada.
 */

const API = "http://localhost:8080";

const token = localStorage.getItem("logitrack_token");
const usuario = localStorage.getItem("logitrack_usuario");
const nombre = localStorage.getItem("logitrack_nombre");
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

function opcionesBodega(bodegas) {
  return bodegas.map((b) =>
    `<option value="${esc(b.id)}">${esc(b.nombre)} — ${esc(b.ubicacion)}</option>`).join("");
}

function opcionesProducto(productos) {
  return productos.map((p) =>
    `<option value="${esc(p.id)}">${esc(p.nombre)} — stock ${esc(p.stock)}</option>`).join("");
}

/* ================= Vistas ================= */

async function vistaBodegas() {
  const bodegas = await pedir("/api/bodega");

  const formulario = esAdmin ? panel("Nueva bodega", "Todos los campos son obligatorios", `
    <form id="form-bodega" class="form-cuerpo">
      <div class="form-fila">
        <div class="campo"><label for="b-nombre">Nombre</label><input id="b-nombre" required placeholder="Bodega Occidente"></div>
        <div class="campo"><label for="b-ubicacion">Ubicación</label><input id="b-ubicacion" required placeholder="Pereira"></div>
        <div class="campo"><label for="b-capacidad">Capacidad</label><input id="b-capacidad" type="number" min="1" required placeholder="1500"></div>
        <div class="campo"><label for="b-encargado">Encargado</label><input id="b-encargado" required placeholder="Ana Torres"></div>
      </div>
      <div class="form-pie">
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
    <form id="form-producto" class="form-cuerpo">
      <div class="form-fila">
        <div class="campo"><label for="p-nombre">Nombre</label><input id="p-nombre" required placeholder="Monitor 27"></div>
        <div class="campo"><label for="p-categoria">Categoría</label><input id="p-categoria" required placeholder="Pantallas"></div>
        <div class="campo"><label for="p-stock">Stock inicial</label><input id="p-stock" type="number" min="0" required placeholder="10"></div>
        <div class="campo"><label for="p-precio">Precio</label><input id="p-precio" type="number" min="1" step="0.01" required placeholder="450000"></div>
      </div>
      <div class="form-pie">
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

/* ---------- MOVIMIENTO: acá se registra todo ---------- */

async function vistaMovimiento() {
  const [bodegas, productos, movimientos] = await Promise.all([
    pedir("/api/bodega"), pedir("/api/producto"), pedir("/api/movimiento")
  ]);

  const sinBodegas = bodegas.length === 0 || productos.length === 0;

  const formulario = panel(
    "Registrar movimiento",
    "Elegí qué pasa, entre qué bodegas y con qué producto. Se registra todo junto.",
    sinBodegas
      ? `<div class="form-cuerpo">${mensaje("Primero necesitás al menos una bodega y un producto.", "alerta")}</div>`
      : `
    <form id="form-movimiento" class="form-cuerpo">
      <div class="form-fila">
        <div class="campo ancho-2">
          <label for="m-tipo">Qué va a pasar</label>
          <select id="m-tipo" data-tipo>
            <option value="ENTRADA">Entrada — ingresa mercancía a una bodega</option>
            <option value="SALIDA">Salida — sale mercancía de una bodega</option>
            <option value="TRANSFERENCIA">Transferencia — pasa de una bodega a otra</option>
          </select>
        </div>

        <div class="campo" id="c-origen">
          <label for="m-origen">Desde qué bodega sale</label>
          <select id="m-origen">${opcionesBodega(bodegas)}</select>
        </div>

        <div class="campo" id="c-destino">
          <label for="m-destino">A qué bodega entra</label>
          <select id="m-destino">${opcionesBodega(bodegas)}</select>
        </div>

        <div class="campo">
          <label for="m-producto">Producto</label>
          <select id="m-producto">${opcionesProducto(productos)}</select>
        </div>

        <div class="campo">
          <label for="m-cantidad">Cantidad</label>
          <input id="m-cantidad" type="number" min="1" value="1" required>
        </div>
      </div>

      <div class="form-pie">
        <button type="submit" class="boton boton--primario" id="btn-registrar">Registrar movimiento</button>
        <span id="m-aviso" class="ayuda"></span>
      </div>
    </form>`);

  const referencia = `<div class="rejilla rejilla--2">
    ${panel("Bodegas disponibles", "Para saber cuál es cuál",
      tabla(["ID", "Bodega", "Ubicación"], bodegas.map((b) => [esc(b.id), esc(b.nombre), esc(b.ubicacion)])))}
    ${panel("Productos disponibles", "Con el stock actual",
      tabla(["ID", "Producto", "Stock"], productos.map((p) => [esc(p.id), esc(p.nombre),
        `<span class="dato-num">${esc(p.stock)}</span>`])))}
  </div>`;

  const historial = panel("Movimientos registrados", movimientos.length + " en total", tabla(
    ["ID", "Fecha", "Tipo", "Registró", "Origen", "Destino", ""],
    movimientos.slice().reverse().map((m) => [esc(m.id), fecha(m.fecha),
      `<span class="eti eti--neutra">${esc(m.tipo)}</span>`,
      esc(m.usuario?.nombre ?? m.usuario?.username),
      esc(m.bodegaOrigen?.nombre ?? "—"),
      esc(m.bodegaDestino?.nombre ?? "—"),
      `<button class="boton boton--secundario boton--sm" data-ver="${esc(m.id)}">Ver detalle</button>`])
  ));

  return formulario + referencia + historial;
}

/* ---------- DETALLE: solo lectura ---------- */

async function vistaDetalle(idInicial) {
  const movimientos = await pedir("/api/movimiento");
  const idBuscado = idInicial ? String(idInicial) : "";

  const buscador = panel("Buscar un movimiento", "Escribí el ID o tocá «Ver detalle» en la lista de abajo", `
    <div class="form-cuerpo">
      <div class="form-fila">
        <div class="campo">
          <label for="d-id">ID del movimiento</label>
          <input id="d-id" type="number" min="1" placeholder="1" value="${esc(idBuscado)}">
        </div>
        <div class="campo" style="grid-column: span 1">
          <button type="button" class="boton boton--primario" id="btn-buscar-detalle">Consultar</button>
        </div>
      </div>
    </div>`);

  const resultado = `<div id="d-resultado">${
    movimientos.length
      ? `<div class="panel"><div class="celda-vacia">Elegí un movimiento para ver qué se movió.</div></div>`
      : `<div class="panel"><div class="celda-vacia"><strong>Todavía no hay movimientos</strong>
         Registrá uno desde la sección Movimientos.</div></div>`
  }</div>`;

  const lista = panel("Movimientos", movimientos.length + " en total", tabla(
    ["ID", "Fecha", "Tipo", "Registró", "Origen", "Destino", ""],
    movimientos.slice().reverse().map((m) => [esc(m.id), fecha(m.fecha),
      `<span class="eti eti--neutra">${esc(m.tipo)}</span>`,
      esc(m.usuario?.nombre ?? m.usuario?.username),
      esc(m.bodegaOrigen?.nombre ?? "—"),
      esc(m.bodegaDestino?.nombre ?? "—"),
      `<button class="boton boton--secundario boton--sm" data-ver="${esc(m.id)}">Ver detalle</button>`])
  ));

  return buscador + resultado + lista;
}

async function cargarDetalle(id) {
  const contenedor = document.getElementById("d-resultado");
  if (!contenedor) return;

  contenedor.innerHTML = `<div class="panel"><div style="padding:18px"><div class="esqueleto" style="width:60%"></div></div></div>`;

  try {
    const [movimiento, lineas] = await Promise.all([
      pedir("/api/movimiento/" + id),
      pedir("/api/detalle/movimiento/" + id)
    ]);

    const cabecera = `<div class="detalle-cabecera">
      <div><span class="etiqueta-campo">Movimiento</span><strong>#${esc(movimiento.id)}</strong></div>
      <div><span class="etiqueta-campo">Tipo</span><strong>${esc(movimiento.tipo)}</strong></div>
      <div><span class="etiqueta-campo">Fecha</span><strong>${esc(fecha(movimiento.fecha))}</strong></div>
      <div><span class="etiqueta-campo">Registró</span><strong>${esc(movimiento.usuario?.nombre ?? movimiento.usuario?.username ?? "—")}</strong></div>
      <div><span class="etiqueta-campo">Origen</span><strong>${esc(movimiento.bodegaOrigen?.nombre ?? "—")}</strong></div>
      <div><span class="etiqueta-campo">Destino</span><strong>${esc(movimiento.bodegaDestino?.nombre ?? "—")}</strong></div>
    </div>`;

    contenedor.innerHTML = panel(
      "Detalle del movimiento #" + movimiento.id,
      lineas.length + (lineas.length === 1 ? " producto movido" : " productos movidos"),
      cabecera + tabla(["Producto", "Cantidad"],
        lineas.map((d) => [esc(d.producto?.nombre),
          `<span class="dato-num">${esc(d.cantidad)}</span>`]))
    );
  } catch (error) {
    contenedor.innerHTML = panel("Detalle del movimiento", "No se pudo consultar",
      `<div class="form-cuerpo">${mensaje(error.message, "error")}</div>`);
  }
}

/* ---------- Solicitudes ---------- */

async function vistaSolicitudes() {
  const pendientes = await pedir("/auth/pendientes");

  const filas = pendientes.map((u) => [
    esc(u.id), esc(u.username), esc(u.nombre), esc(u.telefono),
    `<span class="eti eti--${u.estado === "PENDIENTE" ? "pendiente" : u.estado === "ACTIVO" ? "activo" : "rechazado"}">${esc(u.estado)}</span>`,
    `<span class="acciones-fila">
       <button class="boton boton--primario boton--sm" data-aprobar="${esc(u.id)}">Aprobar</button>
       <button class="boton boton--peligro boton--sm" data-rechazar="${esc(u.id)}">Rechazar</button>
     </span>`
  ]);

  return panel("Solicitudes de registro", pendientes.length + " pendientes",
    `<div id="sol-aviso" style="padding:14px 18px 0"></div>` +
    tabla(["ID", "Usuario", "Nombre", "Teléfono", "Estado", "Acciones"], filas));
}

/* ---------- Auditoría ---------- */

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
      `<span class="auditoria-valores">${esc(a.valoresAnteriores ?? "—")}</span>`,
      `<span class="auditoria-valores">${esc(a.valoresNuevos ?? "—")}</span>`
    ])
  ));
}

/* ---------- Reporte ---------- */

async function vistaReporte() {
  const reporte = await pedir("/api/reporte");

  const totalUnidades = reporte.stockPorBodega.reduce((s, b) => s + (b.totalUnidades || 0), 0);
  const totalValor = reporte.stockPorBodega.reduce((s, b) => s + Number(b.valorTotal || 0), 0);

  const metricas = `<div class="rejilla">
    <div class="metrica"><span class="metrica__rotulo">Bodegas con inventario</span><span class="metrica__valor">${reporte.stockPorBodega.length}</span><span class="metrica__nota">de todas las registradas</span></div>
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
  movimiento:  { titulo: "Registrar movimiento", desc: "Elegí qué producto entra, sale o se transfiere, y entre qué bodegas. Se registra todo junto.", render: vistaMovimiento },
  detalle:     { titulo: "Detalle de un movimiento", desc: "Solo lectura: buscá un movimiento por ID y mirá qué se movió.", render: vistaDetalle },
  solicitudes: { titulo: "Solicitudes", desc: "Pedidos de registro esperando aprobación.", render: vistaSolicitudes, soloAdmin: true },
  auditoria:   { titulo: "Auditoría",   desc: "Cada cambio sobre la base, con su autor y los valores antes y después.", render: vistaAuditoria, soloAdmin: true },
  reporte:     { titulo: "Reporte",     desc: "Resumen general del inventario y de los productos más movidos.", render: vistaReporte, soloAdmin: true }
};

/* ================= Navegación ================= */

async function mostrar(nombreVista, parametro) {
  const vista = VISTAS[nombreVista];
  if (!vista) return;
  if (vista.soloAdmin && !esAdmin) return;

  document.querySelectorAll(".barra__enlace").forEach((b) => {
    if (b.dataset.vista === nombreVista) b.setAttribute("aria-current", "page");
    else b.removeAttribute("aria-current");
  });

  document.getElementById("titulo-vista").textContent = vista.titulo;
  document.getElementById("desc-vista").textContent = vista.desc;

  const contenedor = document.getElementById("vista");
  contenedor.innerHTML = `<div class="panel"><div class="panel__cabecera"><div><h3>Cargando</h3><p>Un momento…</p></div></div><div style="padding:18px"><div class="esqueleto" style="width:60%"></div></div></div>`;

  try {
    contenedor.innerHTML = await vista.render(parametro);
    conectarVista(nombreVista, parametro);
  } catch (error) {
    contenedor.innerHTML = panel(vista.titulo, "No se pudo cargar",
      `<div class="form-cuerpo">${mensaje(error.message, "error")}</div>`);
  }
}

/* ================= Comportamiento ================= */

function avisar(idAviso, texto, tipo) {
  const el = document.getElementById(idAviso);
  if (!el) return;
  if (!texto) { el.textContent = ""; el.className = "ayuda"; return; }
  el.textContent = texto;
  el.className = tipo === "error" ? "error-campo" : "ayuda";
}

/* Muestra solo las bodegas que el tipo de movimiento necesita */
function ajustarCamposPorTipo() {
  const tipo = document.getElementById("m-tipo")?.value;
  const campoOrigen = document.getElementById("c-origen");
  const campoDestino = document.getElementById("c-destino");
  if (!tipo || !campoOrigen || !campoDestino) return;

  campoOrigen.hidden = (tipo === "ENTRADA");
  campoDestino.hidden = (tipo === "SALIDA");

  const boton = document.getElementById("btn-registrar");
  if (boton) {
    boton.textContent = tipo === "ENTRADA" ? "Registrar entrada"
      : tipo === "SALIDA" ? "Registrar salida"
      : "Registrar transferencia";
  }
}

function conectarVista(nombreVista) {

  /* ----- Bodegas ----- */
  if (nombreVista === "bodegas" && esAdmin) {
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

  /* ----- Productos ----- */
  if (nombreVista === "productos" && esAdmin) {
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

  /* ----- Movimiento: registra el movimiento Y su producto de una vez ----- */
  if (nombreVista === "movimiento") {
    const tipo = document.getElementById("m-tipo");
    if (tipo) {
      tipo.addEventListener("change", ajustarCamposPorTipo);
      ajustarCamposPorTipo();
    }

    const form = document.getElementById("form-movimiento");
    if (form) {
      form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const boton = document.getElementById("btn-registrar");
        const tipoValor = document.getElementById("m-tipo").value;
        const productoId = Number(document.getElementById("m-producto").value);
        const cantidad = Number(document.getElementById("m-cantidad").value);
        const origen = Number(document.getElementById("m-origen").value);
        const destino = Number(document.getElementById("m-destino").value);

        avisar("m-aviso", "");

        if (!cantidad || cantidad < 1) {
          avisar("m-aviso", "La cantidad tiene que ser mayor a cero.", "error");
          return;
        }
        if (tipoValor === "TRANSFERENCIA" && origen === destino) {
          avisar("m-aviso", "Elegí dos bodegas distintas para transferir.", "error");
          return;
        }

        boton.disabled = true;
        avisar("m-aviso", "Registrando…");

        try {
          const cuerpo = { tipo: tipoValor };
          if (tipoValor !== "ENTRADA") cuerpo.bodegaOrigenId = origen;
          if (tipoValor !== "SALIDA") cuerpo.bodegaDestinoId = destino;

          const movimiento = await pedir("/api/movimiento", {
            method: "POST", body: JSON.stringify(cuerpo)
          });

          try {
            await pedir("/api/detalle", {
              method: "POST",
              body: JSON.stringify({ movimientoId: movimiento.id, productoId: productoId, cantidad: cantidad })
            });
          } catch (error) {
            avisar("m-aviso",
              `Se creó el movimiento #${movimiento.id}, pero el producto no se movió: ${error.message}`, "error");
            boton.disabled = false;
            return;
          }

          mostrar("movimiento");
        } catch (error) {
          avisar("m-aviso", error.message, "error");
          boton.disabled = false;
        }
      });
    }

    conectarBotonesVer();
  }

  /* ----- Detalle: solo consulta ----- */
  if (nombreVista === "detalle") {
    const input = document.getElementById("d-id");
    const boton = document.getElementById("btn-buscar-detalle");

    const consultar = () => {
      const id = Number(input.value);
      if (!id || id < 1) {
        document.getElementById("d-resultado").innerHTML =
          `<div class="panel"><div class="form-cuerpo">${mensaje("Escribí un ID de movimiento válido.", "error")}</div></div>`;
        return;
      }
      cargarDetalle(id);
    };

    if (boton) boton.addEventListener("click", consultar);
    if (input) {
      input.addEventListener("keydown", (e) => { if (e.key === "Enter") { e.preventDefault(); consultar(); } });
    }

    conectarBotonesVer();

    // Si se llegó desde "Ver detalle", se consulta solo
    if (input && input.value) consultar();
  }

  /* ----- Solicitudes ----- */
  if (nombreVista === "solicitudes") {
    document.querySelectorAll("[data-aprobar]").forEach((b) => {
      b.addEventListener("click", () => resolverSolicitud(b.dataset.aprobar, "aprobar", b));
    });
    document.querySelectorAll("[data-rechazar]").forEach((b) => {
      b.addEventListener("click", () => resolverSolicitud(b.dataset.rechazar, "rechazar", b));
    });
  }
}

function conectarBotonesVer() {
  document.querySelectorAll("[data-ver]").forEach((b) => {
    b.addEventListener("click", () => mostrar("detalle", b.dataset.ver));
  });
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

document.getElementById("usuario-nombre").textContent = nombre || usuario || "—";
document.getElementById("usuario-rol").textContent = rol || "—";
document.getElementById("inicial").textContent = (nombre || usuario || "?").charAt(0).toUpperCase();

if (!esAdmin) {
  document.querySelectorAll(".solo-admin").forEach((el) => el.remove());
}

mostrar(esAdmin ? "reporte" : "movimiento");
