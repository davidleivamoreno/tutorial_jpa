package org.iesvdm.tutorial.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Idioma {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private long id;

    private String nombre;

    @OneToMany(mappedBy = "idioma",cascade=CascadeType.ALL, fetch = FetchType.EAGER,orphanRemoval = true)
    @JsonIgnore
    //@JsonManagedReference
    private Set<Pelicula> peliculas = new HashSet<>();

    public Idioma(int i, String idioma) {
        this.id=i;
        this.nombre=idioma;
    }

    public void addPelicula(Pelicula pelicula) {
        peliculas.add(pelicula);
        pelicula.setIdioma(this); // Establecer la asociación inversa
    }

}
