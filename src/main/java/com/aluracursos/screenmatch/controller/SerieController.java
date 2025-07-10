package com.aluracursos.screenmatch.controller;
import com.aluracursos.screenmatch.dto.EpisodioDTO;
import com.aluracursos.screenmatch.dto.SerieDTO;
import com.aluracursos.screenmatch.service.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/series")
public class SerieController {

    @Autowired
    private SerieService servicio;


    @GetMapping()
    public List<SerieDTO> ObtenerTodasLasSeries() {
        return servicio.ObtenerTodasLasSeries();

    }

    @GetMapping ("/top5")
    public List<SerieDTO> obtenerTop5() {
        return servicio.obtenerTop5();
    }
    @GetMapping("/lanzamientos")
    public List<SerieDTO> obtenerLanzamientosMasReciente (){
       return servicio.ObtenerLanzamientosMasRecientes();
    }
    //se mapea la serie que se quiere ver con unos parametro dinamicos
    @GetMapping("/{id}")
    public SerieDTO obtenerPorId(@PathVariable long id) {
        return servicio.obtenerPorId(id);

    }

    @GetMapping ("{id}/temporadas/todas")
    public List<EpisodioDTO> obtenerTodasLasTemporadas(@PathVariable long id){
        return servicio.ObtenerTodasLasTemporadas(id);
    }


}