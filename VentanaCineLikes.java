import javax.swing.*;
import java.util.ArrayList;

public class VentanaCineLikes extends JFrame {
    private JPanel panelPrincipal;
    private JTabbedPane tabbedPanePrincipal;
    private JPanel panelTab1;
    private JLabel lblTotalPeliculas;
    private JList<String> listaPeliculasTab1;
    private JPanel panelFormularioRegistro;
    private JTextField txtRegCodigo;
    private JTextField txtRegTitulo;
    private JTextField txtRegDirector;
    private JComboBox<String> cmbRegGenero;
    private JTextField txtRegAnio;
    private JButton btnRegistrar;

    private JPanel panelTab2;
    private JPanel panelBusquedaTexto;
    private JTextField txtBuscarTexto;
    private JButton btnBuscarTexto;
    private JTextArea txtAreaResultados;
    private JPanel panelGestionCodigo;
    private JTextField txtBuscarCodigo;
    private JButton btnBuscarCodigo;
    private JTextField txtEditCodigo;
    private JLabel lblLikesActuales;
    private JTextField txtEditTitulo;
    private JTextField txtEditDirector;
    private JComboBox<String> cmbEditGenero;
    private JTextField txtEditAnio;
    private JButton btnGuardarCambios;
    private JButton btnMeGusta;
    private JButton btnEliminarPelicula;

    private final CatalogoPeliculas catalogo;
    private final DefaultListModel<String> modeloListaTab1;
    private int codigoSeleccionado = -1;
    private String ultimaConsultaTexto = null;

    public VentanaCineLikes() {
        setTitle("CineLikes - Administración de Catálogo");
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 620);
        setLocationRelativeTo(null);

        catalogo = new CatalogoPeliculas();
        modeloListaTab1 = new DefaultListModel<>();
        listaPeliculasTab1.setModel(modeloListaTab1);

        cargarGeneros(cmbRegGenero);
        cargarGeneros(cmbEditGenero);

        catalogo.cargarVeintePeliculas();
        refrescarVistaTab1();
        limpiarSeleccionEdicion();

        btnRegistrar.addActionListener(e -> registrarPelicula());
        btnBuscarTexto.addActionListener(e -> buscarPorTexto(true));
        btnBuscarCodigo.addActionListener(e -> buscarPorCodigo());
        btnGuardarCambios.addActionListener(e -> guardarEdicion());
        btnMeGusta.addActionListener(e -> darMeGustaSeleccionada());
        btnEliminarPelicula.addActionListener(e -> eliminarSeleccionada());
    }

    private void cargarGeneros(JComboBox<String> combo) {
        combo.removeAllItems();
        for (String genero : CatalogoPeliculas.GENEROS) {
            combo.addItem(genero);
        }
    }

    private void refrescarVistaTab1() {
        modeloListaTab1.clear();
        for (Pelicula p : catalogo.getListaPeliculas()) {
            modeloListaTab1.addElement(p.toString());
        }
        lblTotalPeliculas.setText("Total de películas registradas: " + catalogo.getCantidadPeliculas());
    }

    private void registrarPelicula() {
        try {
            String codigoStr = txtRegCodigo.getText().trim();
            String anioStr = txtRegAnio.getText().trim();

            if (codigoStr.isEmpty() || anioStr.isEmpty()) {
                throw new IllegalArgumentException("El código y el año son obligatorios y deben ser numéricos.");
            }

            int codigo = Integer.parseInt(codigoStr);
            int anio = Integer.parseInt(anioStr);
            String titulo = txtRegTitulo.getText();
            String director = txtRegDirector.getText();
            String genero = (String) cmbRegGenero.getSelectedItem();

            catalogo.registrarPelicula(codigo, titulo, director, genero, anio);

            refrescarVistaTab1();
            if (ultimaConsultaTexto != null) {
                recalcularBusquedaTextoActiva();
            }
            limpiarFormularioRegistro();
            JOptionPane.showMessageDialog(this, "Película registrada correctamente.", "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El código y el año deben ser números enteros válidos.", "Error de formato", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de validación", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormularioRegistro() {
        txtRegCodigo.setText("");
        txtRegTitulo.setText("");
        txtRegDirector.setText("");
        txtRegAnio.setText("");
        if (cmbRegGenero.getItemCount() > 0) {
            cmbRegGenero.setSelectedIndex(0);
        }
    }

    private void buscarPorTexto(boolean mostrarErrores) {
        try {
            String consulta = txtBuscarTexto.getText();
            ArrayList<Pelicula> resultados = catalogo.buscarCoincidenciaTexto(consulta);
            ultimaConsultaTexto = consulta;

            if (resultados.isEmpty()) {
                txtAreaResultados.setText("No se encontraron coincidencias exactas para: \"" + consulta + "\"");
            } else {
                StringBuilder sb = new StringBuilder();
                for (Pelicula p : resultados) {
                    sb.append(p.toString()).append("\n");
                }
                txtAreaResultados.setText(sb.toString());
            }
        } catch (IllegalArgumentException ex) {
            if (mostrarErrores) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Consulta inválida", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void recalcularBusquedaTextoActiva() {
        if (ultimaConsultaTexto == null || ultimaConsultaTexto.trim().isEmpty()) {
            return;
        }
        ArrayList<Pelicula> resultados = catalogo.buscarCoincidenciaTexto(ultimaConsultaTexto);
        if (resultados.isEmpty()) {
            txtAreaResultados.setText("No se encontraron coincidencias exactas para: \"" + ultimaConsultaTexto + "\"");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Pelicula p : resultados) {
                sb.append(p.toString()).append("\n");
            }
            txtAreaResultados.setText(sb.toString());
        }

        if (codigoSeleccionado != -1) {
            boolean sigueCoincidiendo = false;
            for (Pelicula p : resultados) {
                if (p.getNumeroID() == codigoSeleccionado) {
                    sigueCoincidiendo = true;
                    break;
                }
            }
            if (!sigueCoincidiendo) {
                limpiarSeleccionEdicion();
            }
        }
    }

    private void buscarPorCodigo() {
        try {
            String codigoStr = txtBuscarCodigo.getText().trim();
            if (codigoStr.isEmpty()) {
                limpiarSeleccionEdicion();
                JOptionPane.showMessageDialog(this, "Ingrese un código numérico positivo.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int codigo = Integer.parseInt(codigoStr);
            if (codigo <= 0) {
                limpiarSeleccionEdicion();
                JOptionPane.showMessageDialog(this, "El código debe ser un número positivo.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int indice = catalogo.buscarBinariaPorNumero(codigo);
            if (indice == -1) {
                limpiarSeleccionEdicion();
                JOptionPane.showMessageDialog(this, "No existe ninguna película con el código " + codigo + ".", "No encontrado", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            Pelicula p = catalogo.getListaPeliculas().get(indice);
            codigoSeleccionado = p.getNumeroID();
            txtEditCodigo.setText(String.valueOf(p.getNumeroID()));
            txtEditTitulo.setText(p.getTitulo());
            txtEditDirector.setText(p.getDirector());
            cmbEditGenero.setSelectedItem(p.getGenero());
            txtEditAnio.setText(String.valueOf(p.getAnio()));
            lblLikesActuales.setText("Me gusta: " + p.getMeGusta());

            habilitarControlesEdicion(true);

        } catch (NumberFormatException ex) {
            limpiarSeleccionEdicion();
            JOptionPane.showMessageDialog(this, "El código debe ser un número entero válido.", "Error de formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarEdicion() {
        if (codigoSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Primero busque y seleccione una película válida.", "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String anioStr = txtEditAnio.getText().trim();
            if (anioStr.isEmpty()) {
                throw new IllegalArgumentException("El año es obligatorio.");
            }
            int nuevoAnio = Integer.parseInt(anioStr);
            String nuevoTitulo = txtEditTitulo.getText();
            String nuevoDirector = txtEditDirector.getText();
            String nuevoGenero = (String) cmbEditGenero.getSelectedItem();

            catalogo.editarPelicula(codigoSeleccionado, nuevoTitulo, nuevoDirector, nuevoGenero, nuevoAnio);

            refrescarVistaTab1();
            recalcularBusquedaTextoActiva();
            JOptionPane.showMessageDialog(this, "Datos de la película actualizados correctamente.", "Actualización exitosa", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El año debe ser un número entero válido.", "Error de formato", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de validación", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void darMeGustaSeleccionada() {
        if (codigoSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Primero busque y seleccione una película válida para dar Me gusta.", "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int nuevosLikes = catalogo.darMeGusta(codigoSeleccionado);
            lblLikesActuales.setText("Me gusta: " + nuevosLikes);
            refrescarVistaTab1();
            recalcularBusquedaTextoActiva();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void eliminarSeleccionada() {
        if (codigoSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Primero busque y seleccione una película válida para eliminar.", "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar por completo la película con código " + codigoSeleccionado + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );
        if (confirmacion == JOptionPane.YES_OPTION) {
            boolean eliminada = catalogo.eliminarPelicula(codigoSeleccionado);
            if (eliminada) {
                limpiarSeleccionEdicion();
                refrescarVistaTab1();
                recalcularBusquedaTextoActiva();
                JOptionPane.showMessageDialog(this, "Película eliminada del catálogo.", "Eliminada", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void limpiarSeleccionEdicion() {
        codigoSeleccionado = -1;
        txtEditCodigo.setText("");
        txtEditTitulo.setText("");
        txtEditDirector.setText("");
        txtEditAnio.setText("");
        lblLikesActuales.setText("Me gusta: -");
        if (cmbEditGenero.getItemCount() > 0) {
            cmbEditGenero.setSelectedIndex(0);
        }
        habilitarControlesEdicion(false);
    }

    private void habilitarControlesEdicion(boolean habilitar) {
        txtEditTitulo.setEnabled(habilitar);
        txtEditDirector.setEnabled(habilitar);
        cmbEditGenero.setEnabled(habilitar);
        txtEditAnio.setEnabled(habilitar);
        btnGuardarCambios.setEnabled(habilitar);
        btnMeGusta.setEnabled(habilitar);
        btnEliminarPelicula.setEnabled(habilitar);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaCineLikes().setVisible(true));
    }
}
