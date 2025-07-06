package com.aluracursos.screenmatch.repository;

import com.aluracursos.screenmatch.model.Categria;
import com.aluracursos.screenmatch.model.Episodio;
import com.aluracursos.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    Optional findByTituloContainsIgnoreCase(String nombreSerie); // Método para buscar series por título, ignorando mayúsculas y minúsculas

    // traer el top 5 mejores series
    List<Serie> findTop5ByOrderByEvaluacionDesc(); // Método para obtener las 5 mejores series ordenadas por evaluación de forma descendente

    //Buscar series por categoría
    List<Serie> findByGenero(Categria categoria); // Método para buscar series por género

    // Buscar series por temporadas
    // List<Serie> finByTotalTemporadasLessThanEqualAndEvaluacionGreaterThanEqual(int totalTemporadas, double evaluacion); // Método para buscar series por temporadas y evaluación
    @Query("SELECT s FROM Serie s WHERE s.totalTemporadas <= :totalTemporadas AND s.evaluacion >= :evaluacion")
    List<Serie> seriesPorTemparadaYEvaluacion(int totalTemporadas, Double evaluacion);

    //buscar episodios por nombre
    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE e.titulo ILIKE %:nombreEpisodio%")
    List<Episodio> episodiosPorNombre(String nombreEpisodio); // Método para buscar episodios por nombre, ignorando mayúsculas y minúsculas

    //episodios por series
    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie ORDER BY e.evaluacion DESC LIMIT 5")
    List<Episodio> top5Episodios(Serie serie);
}
