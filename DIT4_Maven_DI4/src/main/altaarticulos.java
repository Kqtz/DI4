import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class altaarticulos extends JFrame {
    private static final long serialVersionUID = 1L; // Identificador único.

    private JPanel panelContenido; // Panel principal de la ventana.
    private JTextField campoDesc; // Campo para ingresar la descripción.
    private JTextField campoCoste; // Campo para ingresar el precio.
    private JTextField campoStock; // Campo para ingresar la cantidad.

    private static final String URL_BD = "jdbc:mysql://localhost:3306/tiendecitaAMC";
    private static final String USUARIO_BD = "root";
    private static final String CLAVE_BD = "alvaro123";

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                altaarticulos ventana = new altaarticulos(); // Inicializa la ventana principal.
                ventana.setVisible(true); // Hace visible la interfaz gráfica.
            } catch (Exception error) {
                error.printStackTrace(); // Imprime errores si ocurren.
            }
        });
    }

    public altaarticulos() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Solo cierra esta ventana.
        setBounds(100, 100, 450, 300); // Establece posición y tamaño.
        panelContenido = new JPanel(); // Panel principal.
        panelContenido.setBorder(new EmptyBorder(5, 5, 5, 5)); // Margen.
        setContentPane(panelContenido);
        panelContenido.setLayout(null); // Sin diseño automático.

        JLabel etiquetaDescripcion = new JLabel("Descripción:");
        etiquetaDescripcion.setBounds(26, 81, 100, 20); // Posición y tamaño.
        panelContenido.add(etiquetaDescripcion);

        campoDesc = new JTextField(); // Campo para la descripción.
        campoDesc.setBounds(110, 81, 200, 20);
        panelContenido.add(campoDesc);

        JLabel etiquetaPrecio = new JLabel("Precio:");
        etiquetaPrecio.setBounds(50, 50, 100, 20);
        panelContenido.add(etiquetaPrecio);

        campoCoste = new JTextField(); // Campo para el precio.
        campoCoste.setBounds(110, 50, 200, 20);
        panelContenido.add(campoCoste);

        JLabel etiquetaCantidad = new JLabel("Cantidad:");
        etiquetaCantidad.setBounds(36, 112, 100, 20);
        panelContenido.add(etiquetaCantidad);

        campoStock = new JTextField(); // Campo para la cantidad.
        campoStock.setBounds(110, 112, 200, 20);
        panelContenido.add(campoStock);

        JButton botonGuardar = new JButton("Aceptar");
        botonGuardar.setBounds(77, 156, 100, 30);
        botonGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                // Captura los datos introducidos por el usuario.
                String descripcion = campoDesc.getText();
                String precioTexto = campoCoste.getText();
                String cantidadTexto = campoStock.getText();

                if (descripcion.isEmpty() || precioTexto.isEmpty() || cantidadTexto.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Completa todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
                    return; // Detiene el flujo si hay errores.
                }

                try {
                    int precio = Integer.parseInt(precioTexto);
                    int cantidad = Integer.parseInt(cantidadTexto);
                    guardarArticulo(descripcion, precio, cantidad); // Llama al método que guarda en la base de datos.
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Precio y cantidad deben ser números válidos", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panelContenido.add(botonGuardar);

        JButton botonLimpiar = new JButton("Limpiar");
        botonLimpiar.setBounds(261, 156, 100, 30);
        botonLimpiar.addActionListener(e -> {
            campoDesc.setText(""); // Limpia la descripción.
            campoCoste.setText(""); // Limpia el precio.
            campoStock.setText(""); // Limpia la cantidad.
        });
        panelContenido.add(botonLimpiar);

        JButton botonSalir = new JButton("Volver");
        botonSalir.setBounds(361, 230, 63, 20);
        botonSalir.addActionListener(e -> dispose()); // Cierra la ventana.
        panelContenido.add(botonSalir);
    }

    private void guardarArticulo(String descripcion, int precio, int cantidad) {
        try (Connection conexion = DriverManager.getConnection(URL_BD, USUARIO_BD, CLAVE_BD);
             PreparedStatement consultaPreparada = conexion.prepareStatement(
                     "INSERT INTO Articulos (descripcion, precio, cantidad) VALUES (?, ?, ?)")) {
            consultaPreparada.setString(1, descripcion); // Configura descripción.
            consultaPreparada.setInt(2, precio); // Configura precio.
            consultaPreparada.setInt(3, cantidad); // Configura cantidad.

            int filasInsertadas = consultaPreparada.executeUpdate();
            if (filasInsertadas > 0) {
                JOptionPane.showMessageDialog(null, "Artículo añadido correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException error) {
            error.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al agregar artículo: " + error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
