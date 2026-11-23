import java.time.Year;
import java.util.ArrayList;

public class CatalogoPeliculas {
    public static final String[] GENEROS = {
            "Drama", "Aventura", "Comedia", "Animación", "Documental"
    };

    private final ArrayList<Pelicula> listaPeliculas;
    private boolean datosCargados;

    public CatalogoPeliculas() {
        this.listaPeliculas = new ArrayList<>();
        this.datosCargados = false;
    }

    public ArrayList<Pelicula> getListaPeliculas() {
        return listaPeliculas;
    }

    public int getCantidadPeliculas() {
        return listaPeliculas.size();
    }

    public void cargarVeintePeliculas() {
        if (datosCargados) {
            return;
        }

        Object[][] iniciales = {
                {1010, "El Padrino", "Francis Ford Coppola", "Drama", 1972},
                {1025, "Interestelar", "Christopher Nolan", "Aventura", 2014},
                {1040, "Tiempos Modernos", "Charles Chaplin", "Comedia", 1936},
                {1055, "El Viaje de Chihiro", "Hayao Miyazaki", "Animación", 2001},
                {1070, "Planeta Tierra", "Alastair Fothergill", "Documental", 2006},
                {1085, "Parásitos", "Bong Joon-ho", "Drama", 2019},
                {1100, "Indiana Jones: En busca del arca perdida", "Steven Spielberg", "Aventura", 1981},
                {1115, "Truman Show", "Peter Weir", "Comedia", 1998},
                {1130, "Toy Story", "John Lasseter", "Animación", 1995},
                {1145, "Free Solo", "Elizabeth Chai Vasarhelyi", "Documental", 2018},
                {1160, "Cadena Perpetua", "Frank Darabont", "Drama", 1994},
                {1175, "El Señor de los Anillos: La Comunidad del Anillo", "Peter Jackson", "Aventura", 2001},
                {1190, "Relatos Salvajes", "Damián Szifron", "Comedia", 2014},
                {1205, "Spider-Man: Un nuevo universo", "Bob Persichetti", "Animación", 2018},
                {1220, "Nuestro Planeta", "Alastair Fothergill", "Documental", 2019},
                {1235, "La Lista de Schindler", "Steven Spielberg", "Drama", 1993},
                {1250, "Jurassic Park", "Steven Spielberg", "Aventura", 1993},
                {1265, "La Vida es Bella", "Roberto Benigni", "Comedia", 1997},
                {1280, "Coco", "Lee Unkrich", "Animación", 2017},
                {1295, "Oppenheimer", "Christopher Nolan", "Drama", 2023}
        };

        for (Object[] fila : iniciales) {
            int id = (int) fila[0];
            if (buscarBinariaPorNumero(id) == -1) {
                listaPeliculas.add(new Pelicula(
                        id,
                        (String) fila[1],
                        (String) fila[2],
                        (String) fila[3],
                        (int) fila[4]
                ));
                ordenarPorNumeroDescendente();
            }
        }

        datosCargados = true;
    }

    public void ordenarPorNumeroDescendente() {
        for (int i = 1; i < listaPeliculas.size(); i++) {
            Pelicula actual = listaPeliculas.get(i);
            int j = i - 1;
            while (j >= 0 && listaPeliculas.get(j).getNumeroID() < actual.getNumeroID()) {
                listaPeliculas.set(j + 1, listaPeliculas.get(j));
                j--;
            }
            listaPeliculas.set(j + 1, actual);
        }
    }

    public int buscarBinariaPorNumero(int numeroID) {
        if (numeroID <= 0) {
            return -1;
        }
        int inicio = 0;
        int fin = listaPeliculas.size() - 1;

        while (inicio <= fin) {
            int medio = inicio + (fin - inicio) / 2;
            int idMedio = listaPeliculas.get(medio).getNumeroID();

            if (idMedio == numeroID) {
                return medio;
            } else if (idMedio < numeroID) {
                fin = medio - 1;
            } else {
                inicio = medio + 1;
            }
        }
        return -1;
    }

    protected void validarDatos(int numeroID, String titulo, String director, String genero, int anio, boolean verificarIdUnico) {
        if (numeroID <= 0) {
            throw new IllegalArgumentException("El código debe ser un número entero positivo.");
        }
        if (verificarIdUnico && buscarBinariaPorNumero(numeroID) != -1) {
            throw new IllegalArgumentException("Ya existe una película con el código " + numeroID + ".");
        }
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El título es obligatorio.");
        }
        if (director == null || director.trim().isEmpty()) {
            throw new IllegalArgumentException("El director es obligatorio.");
        }
        boolean generoValido = false;
        if (genero != null) {
            for (String g : GENEROS) {
                if (g.equals(genero)) {
                    generoValido = true;
                    break;
                }
            }
        }
        if (!generoValido) {
            throw new IllegalArgumentException("Seleccione un género válido.");
        }
        int anioActual = Year.now().getValue();
        if (anio < 1930 || anio > anioActual) {
            throw new IllegalArgumentException("El año debe estar entre 1930 y " + anioActual + ".");
        }
    }

    public void registrarPelicula(int numeroID, String titulo, String director, String genero, int anio) {
        validarDatos(numeroID, titulo, director, genero, anio, true);
        Pelicula nueva = new Pelicula(numeroID, titulo.trim(), director.trim(), genero, anio);
        listaPeliculas.add(nueva);
        ordenarPorNumeroDescendente();
    }
}
