import javax.swing.*;

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

    private final CatalogoPeliculas catalogo;
    private final DefaultListModel<String> modeloListaTab1;

    public VentanaCineLikes() {
        setTitle("CineLikes - Administración de Catálogo");
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 580);
        setLocationRelativeTo(null);

        catalogo = new CatalogoPeliculas();
        modeloListaTab1 = new DefaultListModel<>();
        listaPeliculasTab1.setModel(modeloListaTab1);

        cargarGeneros(cmbRegGenero);

        catalogo.cargarVeintePeliculas();
        refrescarVistaTab1();

        btnRegistrar.addActionListener(e -> registrarPelicula());
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaCineLikes().setVisible(true));
    }
}
