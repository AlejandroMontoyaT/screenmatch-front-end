package com.aluracursos.screenmatch.model;

public enum Categria {

    ACCION("Action" , "Acción"),

    ROMANCE("Romance", "Romance"),

    COMEDIA("Comedy", "Comedia"),

    DRAMA("Drama", "Drama"),

    CRIMEN("Crime", "Crimen");

    private String categoriaOmdb;

    private String categoriaEspanol;

    Categria (String categoriaOmdb, String categoriaEspanol) {
        this.categoriaOmdb = categoriaOmdb;
        this.categoriaEspanol = categoriaEspanol;
    }

    public static Categria fromString(String text) {
        for (Categria categoria : Categria.values()) {
            if (categoria.categoriaOmdb.equals(text)) {
                return  categoria;
            }
        }
        throw new IllegalArgumentException("No se encontró la categoría: " + text);
    }

    // Método para obtener la categoría en español
    public static Categria fromEspanol(String text) {
        for (Categria categoria : Categria.values()) {
            if (categoria.categoriaEspanol.equalsIgnoreCase(text)) {
                return  categoria;
            }
        }
        throw new IllegalArgumentException("No se encontró la categoría: " + text);
    }

    }

