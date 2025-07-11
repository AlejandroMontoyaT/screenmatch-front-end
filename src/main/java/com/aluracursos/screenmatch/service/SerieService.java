package com.aluracursos.screenmatch.service;

import com.aluracursos.screenmatch.dto.EpisodioDTO;
import com.aluracursos.screenmatch.dto.SerieDTO;
import com.aluracursos.screenmatch.model.Categria;
import com.aluracursos.screenmatch.model.Serie;
import com.aluracursos.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//se identifica que es un servicio de la aplicacion
@Service
public class SerieService {

    @Autowired
    private SerieRepository repository;

    public List<SerieDTO> ObtenerTodasLasSeries() {
        return convierteDatos(repository.findTop5ByOrderByEvaluacionDesc());

    }

    public List<SerieDTO> ObtenerLanzamientosMasRecientes() {
        return convierteDatos(repository.lanzamientosMasRecientes());

    }

    public List<SerieDTO> obtenerTop5() {
        return convierteDatos(repository.findTop5ByOrderByEvaluacionDesc());
    }

    //metodo para convertir datos serieDTo y no se repitan
    public List<SerieDTO> convierteDatos(List<Serie> serie) {
       return serie.stream()
                .map(s -> new SerieDTO(
                        s.getId(),
                        s.getTitulo(),
                        s.getTotalTemporadas(),
                        s.getEvaluacion(),
                        s.getPoster(),
                        s.getGenero(),
                        s.getActores(),
                        s.getSinopsis()))
                .collect(Collectors.toList());

    }

    public SerieDTO obtenerPorId(long id) {
        Optional<Serie> serie = repository.findById(id);
        if (serie.isPresent()) {
            Serie s = serie.get();
            return new SerieDTO(
                    s.getId(),
                    s.getTitulo(),
                    s.getTotalTemporadas(),
                    s.getEvaluacion(),
                    s.getPoster(),
                    s.getGenero(),
                    s.getActores(),
                    s.getSinopsis());
        }
        return null; // o lanzar una excepción si no se encuentra la serie
    }

    public List<EpisodioDTO> ObtenerTodasLasTemporadas(long id) {
        Optional<Serie> serie = repository.findById(id);
        if (serie.isPresent()){
            Serie s = serie.get();
            return s.getEpisodios().stream()
                    .map(e -> new EpisodioDTO(e.getTemporada(),e.getTitulo(),e.getNumeroEpisodio()))
                    .collect(Collectors.toList());
        }
        return null;

    }

    public List<EpisodioDTO> ObtenerTemporadasPorNumero(long id, long numeroTemporada) {
        return repository.obtenerTemporadasPorNumero(id, numeroTemporada).stream()
                .map(e -> new EpisodioDTO(
                        e.getTemporada(),
                        e.getTitulo(),
                        e.getNumeroEpisodio()))
                .collect(Collectors.toList());
    }

    public List<SerieDTO> obtenerSeriesPorCategoria(String nombreGenero) {
        Categria categoria = Categria.fromEspanol(nombreGenero);
        return convierteDatos(repository.findByGenero(categoria));
    }
}
