package com.aluracursos.screenmatch.dto;

import com.aluracursos.screenmatch.model.Categria;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public record SerieDTO(

        String titulo,

         Integer totalTemporadas,

         Double evaluacion,

         String poster,

        Categria genero,

        String actores,

        String sinopsis
) {
}
