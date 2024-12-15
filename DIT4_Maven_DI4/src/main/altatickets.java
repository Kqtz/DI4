import java.awt.Choice;
import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.HashMap;

public class altatickets extends JFrame {

    private static final long serialVersionUID = 1L;

    private JPanel panelPrincipal;
    private JTextField campoCodigo;
    private JTextField campoFecha;
    private JTextField campoPrecioTotal;
    private Choice menuArticulos;
    private HashMap<String, Integer> mapaArticulos;

    private static final String URL_BD = "jdbc:mysql://localhost:3306/tiendecitaAMC";
    private static final String USUARIO_BD = "root";
    private static final String CLAVE_BD = "alvaro123";

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                altatickets ventana = new altatickets();
                ventana.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public altatickets() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 500, 400);

        panelPrincipal = new JPanel();
        panelPrincipal.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(panelPrincipal);
        panelPrincipal.setLayout(null);

        JLabel etiquetaCodigo = new JLabel("Código:");
        etiquetaCodigo.setBounds(50, 50, 100, 20);
        panelPrincipal.add(etiquetaCodigo);

        campoCodigo = new JTextField();
        campoCodigo.setBounds(104, 50, 200, 20);
        panelPrincipal.add(campoCodigo);

        JLabel etiquetaFecha = new JLabel("Fecha:");
        etiquetaFecha.setBounds(50, 138, 100, 20);
        panelPrincipal.add(etiquetaFecha);

        campoFecha = new JTextField();
        campoFecha.setBounds(104, 138, 200, 20);
        panelPrincipal.add(campoFecha);

        JLabel etiquetaTotal = new JLabel("Total Precio:");
        etiquetaTotal.setBounds(26, 107, 100, 20);
        panelPrincipal.add(etiquetaTotal);

        campoPrecioTotal = new JTextField();
        campoPrecioTotal.setBounds(104, 107, 200, 20);
        panelPrincipal.add(campoPrecioTotal);

        JLabel etiquetaArticulo = new JLabel("Artículo:");
        etiquetaArticulo.setBounds(50, 76, 100, 20);
        panelPrincipal.add(etiquetaArticulo);

        menuArticulos = new Choice();
        menuArticulos.setBounds(104, 76, 200, 20);
        panelPrincipal.add(menuArticulos);

        cargarListaArticulos();

        JButton botonGuardar = new JButton("Aceptar");
        botonGuardar.setBounds(156, 185, 88, 20);
        botonGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                String codigo = campoCodigo.getText();
                String fecha = campoFecha.getText();
                String totalPrecioTexto = campoPrecioTotal.getText();
                String articuloSeleccionado = menuArticulos.getSelectedItem();

                if (codigo.isEmpty() || fecha.isEmpty() || totalPrecioTexto.isEmpty() || articuloSeleccionado == null) {
                    JOptionPane.showMessageDialog(null, "Por favor, completa todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    int totalPrecio = Integer.parseInt(totalPrecioTexto);
                    int idArticulo = mapaArticulos.get(articuloSeleccionado);

                    insertarDatosTicket(codigo, fecha, totalPrecio, idArticulo);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "El campo 'Total Precio' debe ser un número válido", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panelPrincipal.add(botonGuardar);

        JButton botonReset = new JButton("Limpiar");
        botonReset.setBounds(379, 41, 76, 38);
        botonReset.addActionListener(evento -> {
            campoCodigo.setText("");
            campoFecha.setText("");
            campoPrecioTotal.setText("");
            menuArticulos.select(0);
        });
        panelPrincipal.add(botonReset);

        JButton botonCerrar = new JButton("Volver");
        botonCerrar.setBounds(411, 330, 63, 20);
        botonCerrar.addActionListener(evento -> dispose());
        panelPrincipal.add(botonCerrar);
    }

    private void cargarListaArticulos() {
        mapaArticulos = new HashMap<>();
        try (Connection conexion = DriverManager.getConnection(URL_BD, USUARIO_BD, CLAVE_BD);
             Statement consulta = conexion.createStatement();
             ResultSet resultados = consulta.executeQuery("SELECT idArticulo, descripcion FROM Articulos")) {

            while (resultados.next()) {
                int idArticulo = resultados.getInt("idArticulo");
                String descripcion = resultados.getString("descripcion");

                menuArticulos.add(descripcion);
                mapaArticulos.put(descripcion, idArticulo);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al cargar los artículos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void insertarDatosTicket(String codigo, String fecha, int totalPrecio, int idArticulo) {
        try (Connection conexion = DriverManager.getConnection(URL_BD, USUARIO_BD, CLAVE_BD)) {
            String sqlTicket = "INSERT INTO Tickets (codigo, fecha, totalprecio) VALUES (?, ?, ?)";
            PreparedStatement consultaTicket = conexion.prepareStatement(sqlTicket, Statement.RETURN_GENERATED_KEYS);
            consultaTicket.setString(1, codigo);
            consultaTicket.setString(2, fecha);
            consultaTicket.setInt(3, totalPrecio);
            consultaTicket.executeUpdate();

            ResultSet clavesGeneradas = consultaTicket.getGeneratedKeys();
            if (clavesGeneradas.next()) {
                int idTicket = clavesGeneradas.getInt(1);

                String sqlArticuloTicket = "INSERT INTO ArticulosTickets (idArticuloFk, idTicketFk) VALUES (?, ?)";
                PreparedStatement consultaArticuloTicket = conexion.prepareStatement(sqlArticuloTicket);
                consultaArticuloTicket.setInt(1, idArticulo);
                consultaArticuloTicket.setInt(2, idTicket);
                consultaArticuloTicket.executeUpdate();

                JOptionPane.showMessageDialog(null, "Ticket agregado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al insertar datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
