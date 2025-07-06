package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.*;
import com.aluracursos.screenmatch.repository.SerieRepository;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=117b0c4a";
    private ConvierteDatos conversor = new ConvierteDatos();
    //se crea el metodo buscarSeries() para buscar una serie en una lista de series
    private List<DatosSerie> datosSeries = new ArrayList<>();

    //se crea un atributo del tipo privado SerieRepository para poder usar el repositorio de series
    private SerieRepository repositorio;

    //se crea variabla global de lista serie
    private List<Serie> series;

    private Optional<Serie> serieBuscada;


    // Constructor que recibe el repositorio de series
    public Principal(SerieRepository repository) {
        this.repositorio = repository;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar series 
                    2 - Buscar episodios
                    3 - Mostrar series buscadas
                    4 - Buscar Serie por titulo
                    5 - Top 5 mejores series
                    6 - Buscar series por categoria
                    7 - Filtrar series por temporada y evaluación
                    8 - Buscar episodios por titulo
                    9 - top 5 episodios de una serie
                    
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    mostrarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriesPorTitulo();
                    break;

                case 5:
                    buscarTop5Series();
                    break;

                case 6:
                    buscarSeriesPorCategoria();
                    break;

                case 7:
                    filtrarSeriesPorTemporadaYEvaluacion();
                    break;

                case 8:
                    buscarEpisodiosPorTitulo();
                    break;

                case 9:
                    buscarTop5Episodios();
                    break;

                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }

    }

    private DatosSerie getDatosSerie() {
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
        System.out.println(json);
        DatosSerie datos = conversor.obtenerDatos(json, DatosSerie.class);
        return datos;
    }

    private void buscarEpisodioPorSerie() {
        mostrarSeriesBuscadas();//se muestra las series buscadas para que el usuario pueda elegir una desde la base de datos
        System.out.println("Escribe el nombre de la serie que deseas buscar sus episodios");
        var nombreSerie = teclado.nextLine();

        //se busca la serie en la base de datos
        Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitulo().toLowerCase().contains(nombreSerie.toLowerCase()))
                .findFirst();

        if (serie.isPresent()) {
            var serieEncontrada = serie.get();

            //se elimina por que se realiza busqueda en bdd DatosSerie datosSerie = getDatosSerie();
            List<DatosTemporadas> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumoApi.obtenerDatos(URL_BASE + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DatosTemporadas datosTemporada = conversor.obtenerDatos(json, DatosTemporadas.class);
                temporadas.add(datosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios); //se setea los episodios a la serie encontrada
            repositorio.save(serieEncontrada); //se guarda la serie con los episodios en la base de datos


        }


    }

    private void buscarSerieWeb() {
        //se crea el consumo del private del datosSerie con getDatosSerie()
        DatosSerie datos = getDatosSerie();
        //se crea una serie con los datos obtenidos de la serie
        Serie serie = new Serie(datos);
        repositorio.save(serie);//se guarda la serie en la base de datos

        // datosSeries.add(datos); //para colora los datos de la lista se crea el consumo datosSeries.add(datos);
        System.out.println(datos); //se imprime los datos de la serie
        //mostrar todas las series buscadas
    }

    private void mostrarSeriesBuscadas() {
        series = repositorio.findAll(); //se obtiene todas las series de la base de datos

        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);

    }

    private void buscarSeriesPorTitulo() {
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();
        serieBuscada = repositorio.findByTituloContainsIgnoreCase(nombreSerie); //se busca la serie por titulo ignorando mayusculas y minusculas

        if (serieBuscada.isPresent()) {
            System.out.println("Serie encontrada: " + serieBuscada.get());
        } else {
            System.out.println("No se encontró la serie con el título: " + nombreSerie);
        }

    }

    private void buscarTop5Series() {
        List<Serie> topSeries = repositorio.findTop5ByOrderByEvaluacionDesc(); //se obtiene las 5 mejores series ordenadas por evaluacion de forma descendente
        //se imprime las 5 mejores series
        topSeries.forEach(serie -> {
            System.out.println("Serie " + serie.getTitulo());
            System.out.println("Evaluación: " + serie.getEvaluacion());
            System.out.println("Género: " + serie.getGenero());
            System.out.println("Sinopsis: " + serie.getSinopsis());
            System.out.println("-----------------------------");
        });
    }

    private void buscarSeriesPorCategoria() {
        System.out.println("Escribe el genero/ categoría de la serie que deseas buscar");
        var genero = teclado.nextLine();
        //se busca la serie por genero
        var categoria = Categria.fromEspanol(genero); //se convierte el genero a categoria
        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria); //se busca la serie por genero
        System.out.println("Series encontradas en la categoría " + genero + ":");
        seriesPorCategoria.forEach(System.out::println);

    }

    public void filtrarSeriesPorTemporadaYEvaluacion() {
        System.out.println("¿Filtrar séries con cuántas temporadas? ");
        var totalTemporadas = teclado.nextInt();
        teclado.nextLine();
        System.out.println("¿Com evaluación apartir de cuál valor? ");
        var evaluacion = teclado.nextDouble();
        teclado.nextLine();
        List<Serie> filtroSeries = repositorio.seriesPorTemparadaYEvaluacion(totalTemporadas, evaluacion);
        System.out.println("*** Series filtradas ***");
        filtroSeries.forEach(s ->
                System.out.println(s.getTitulo() + "  - evaluacion: " + s.getEvaluacion()));
    }

    // Método para buscar episodios por título
    private void buscarEpisodiosPorTitulo() {
        System.out.println("Escribe el nombre del episodio que deseas buscar");
        var nombreEpisodio = teclado.nextLine();
        List<Episodio> episodiosEncontrados = repositorio.episodiosPorNombre(nombreEpisodio);
        episodiosEncontrados.forEach(e ->
                System.out.printf("Serie: %s Temporada %s Episodio %s Evaluación %s\n",
                        e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getEvaluacion()));

    }

    // Buscar top 5 episodios de una serie
    private void buscarTop5Episodios() {
        buscarSeriesPorTitulo();
        if (serieBuscada.isPresent()) {
            Serie serie = serieBuscada.get();
            List<Episodio> topEpisodios = repositorio.top5Episodios(serie);
            topEpisodios.forEach( e ->
            System.out.printf("- Serie: %s - Temporada %s - Episodio %s - Evaluación %s\n",
                    e.getSerie().getTitulo(), e.getTemporada(), e.getTitulo(), e.getEvaluacion()));

        }

    }
}

