package Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Main {
    private static final String DATABASE_NAME = "VentaCursoOnline";

    public static Connection ConectarBD() throws SQLException {
        String host = "jdbc:mysql://localhost:3307/" + DATABASE_NAME;
        String user = "root";
        String pass = "";
        return DriverManager.getConnection(host, user, pass);
    }

    // Agregar un nuevo curso
    public static void agregarCurso(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("\n--- Agregar Nuevo Curso ---");
        System.out.print("Título del curso: ");
        String titulo = scanner.nextLine();
        System.out.print("Descripción del curso: ");
        String descripcion = scanner.nextLine();
        System.out.print("Precio del curso: ");
        double precio = scanner.nextDouble();
        scanner.nextLine(); // Consumir la nueva línea pendiente
        System.out.print("Enlace del contenido (opcional): ");
        String enlace = scanner.nextLine();

        String sql = "INSERT INTO Curso (titulo, descripcion, precio, enlace_contenido) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, titulo);
            pstmt.setString(2, descripcion);
            pstmt.setDouble(3, precio);
            pstmt.setString(4, enlace.isEmpty() ? null : enlace);
            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Curso agregado exitosamente.");
            } else {
                System.out.println("No se pudo agregar el curso.");
            }
        }
    }

    // Listar todos los cursos
    public static void listarCursos(Connection conn) throws SQLException {
        System.out.println("\n--- Listado de Cursos ---");
        String sql = "SELECT id_curso, titulo, descripcion, precio, enlace_contenido, estado FROM Curso";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id_curso"));
                System.out.println("Título: " + rs.getString("titulo"));
                System.out.println("Descripción: " + rs.getString("descripcion"));
                System.out.println("Precio: " + rs.getDouble("precio"));
                System.out.println("Enlace: " + (rs.getString("enlace_contenido") != null ? rs.getString("enlace_contenido") : "N/A"));
                System.out.println("Estado: " + rs.getString("estado"));
                System.out.println("-----------------------");
            }
        }
    }

    // Editar un curso existente
    public static void editarCurso(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("\n--- Editar Curso ---");
        System.out.print("Ingrese el ID del curso a editar: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consumir la nueva línea pendiente

        // Primero verificar si el curso existe
        String sqlSelect = "SELECT titulo, descripcion, precio, enlace_contenido, estado FROM Curso WHERE id_curso = ?";
        try (PreparedStatement pstmtSelect = conn.prepareStatement(sqlSelect)) {
            pstmtSelect.setInt(1, id);
            ResultSet rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                System.out.println("Información actual del curso:");
                System.out.println("Título: " + rs.getString("titulo"));
                System.out.println("Descripción: " + rs.getString("descripcion"));
                System.out.println("Precio: " + rs.getDouble("precio"));
                System.out.println("Enlace: " + (rs.getString("enlace_contenido") != null ? rs.getString("enlace_contenido") : "N/A"));
                System.out.println("Estado: " + rs.getString("estado"));

                System.out.print("Nuevo título del curso (" + rs.getString("titulo") + "): ");
                String nuevoTitulo = scanner.nextLine();
                System.out.print("Nueva descripción del curso (" + rs.getString("descripcion") + "): ");
                String nuevaDescripcion = scanner.nextLine();
                System.out.print("Nuevo precio del curso (" + rs.getDouble("precio") + "): ");
                double nuevoPrecio = scanner.nextDouble();
                scanner.nextLine(); // Consumir la nueva línea pendiente
                System.out.print("Nuevo enlace del contenido (" + (rs.getString("enlace_contenido") != null ? rs.getString("enlace_contenido") : "N/A") + "): ");
                String nuevoEnlace = scanner.nextLine();
                System.out.print("Nuevo estado del curso (activo/inactivo) (" + rs.getString("estado") + "): ");
                String nuevoEstado = scanner.nextLine().toLowerCase();
                if (!nuevoEstado.equals("activo") && !nuevoEstado.equals("inactivo")) {
                    System.out.println("Estado inválido. Se mantendrá el estado actual.");
                    nuevoEstado = rs.getString("estado");
                }

                String sqlUpdate = "UPDATE Curso SET titulo = ?, descripcion = ?, precio = ?, enlace_contenido = ?, estado = ? WHERE id_curso = ?";
                try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
                    pstmtUpdate.setString(1, nuevoTitulo);
                    pstmtUpdate.setString(2, nuevaDescripcion);
                    pstmtUpdate.setDouble(3, nuevoPrecio);
                    pstmtUpdate.setString(4, nuevoEnlace.isEmpty() ? null : nuevoEnlace);
                    pstmtUpdate.setString(5, nuevoEstado);
                    pstmtUpdate.setInt(6, id);
                    int filasAfectadas = pstmtUpdate.executeUpdate();
                    if (filasAfectadas > 0) {
                        System.out.println("Curso con ID " + id + " editado exitosamente.");
                    } else {
                        System.out.println("No se pudo editar el curso con ID: " + id + ".");
                    }
                }
            } else {
                System.out.println("No se encontró ningún curso con el ID: " + id + ".");
            }
        }
    }

    // Eliminar un curso por su ID
    public static void eliminarCurso(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("\n--- Eliminar Curso ---");
        System.out.print("Ingrese el ID del curso a eliminar: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consumir la nueva línea pendiente

        String sql = "DELETE FROM Curso WHERE id_curso = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Curso con ID " + id + " eliminado exitosamente.");
            } else {
                System.out.println("No se encontró ningún curso con el ID: " + id + " para eliminar.");
            }
        }
    }

    public static void main(String[] args) {
        Connection bd = null;
        Scanner scanner = new Scanner(System.in);

        try {
            bd = ConectarBD();
            int opcion;
            do {
                System.out.println("\n--- Menú ---");
                System.out.println("1. Agregar Curso");
                System.out.println("2. Listar Cursos");
                System.out.println("3. Editar Curso");
                System.out.println("4. Eliminar Curso");
                System.out.println("0. Salir");
                System.out.print("Seleccione una opción: ");
                opcion = scanner.nextInt();
                scanner.nextLine(); // Consumir la nueva línea pendiente

                switch (opcion) {
                    case 1:
                        agregarCurso(bd, scanner);
                        break;
                    case 2:
                        listarCursos(bd);
                        break;
                    case 3:
                        editarCurso(bd, scanner);
                        break;
                    case 4:
                        eliminarCurso(bd, scanner);
                        break;
                    case 0:
                        System.out.println("Saliendo del programa.");
                        break;
                    default:
                        System.out.println("Opción no válida.");
                }
            } while (opcion != 0);

        } catch (SQLException e) {
            System.err.println("Error durante la operación: " + e.getMessage());
        } finally {
            try {
                if (bd != null) {
                    bd.close();
                    System.out.println("Conexion cerrada.");
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
            scanner.close();
        }
    }
}