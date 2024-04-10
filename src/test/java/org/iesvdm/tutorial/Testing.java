package org.iesvdm.tutorial;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.iesvdm.tutorial.domain.Actor;
import org.iesvdm.tutorial.domain.Categoria;
import org.iesvdm.tutorial.domain.Pelicula;
import org.iesvdm.tutorial.domain.Idioma;
import org.iesvdm.tutorial.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
class Ejercicio42ApplicationTests {


    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private TransactionTemplate transactionTemplate;
    @BeforeEach
    public void setUp() {
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Autowired
    PeliculaRepository peliculaRepository;

    @Autowired
    CategoriaRepository categoriaRepository;

    @Autowired
    ActorRepository actorRepository;

    @Autowired
    IdiomaRepository idiomaRepository;



    @Test
    @Order(1)
    void guardarManyToMany(){

        Idioma idioma =new Idioma(0,"espanglish");
        Pelicula pelicula1 = new Pelicula(0, "pelicula1",new HashSet<>(),new HashSet<>(),idioma);
        peliculaRepository.save(pelicula1);

        Categoria categoria1 = new Categoria(0, "categoria1", new HashSet<>());
        categoriaRepository.save(categoria1);

        Categoria categoria2 = new Categoria(0, "categoria2", new HashSet<>());
        categoriaRepository.save(categoria2);

        pelicula1.getCategorias().add(categoria1);
        pelicula1.getCategorias().add(categoria2);

        categoria1.getPeliculas().add(pelicula1);
        categoria2.getPeliculas().add(pelicula1);

        peliculaRepository.save(pelicula1);
        categoriaRepository.save(categoria1);
        categoriaRepository.save(categoria2);

    }

    @Test
    @Order(2)
    void guardarManyToManyActorPelicula() {
        transactionTemplate.execute(status -> {
            Idioma idioma = idiomaRepository.findById(1L).get();
            Pelicula pelicula1 = new Pelicula(0, "pelicula1",new HashSet<>(),new HashSet<>(),idioma);
            peliculaRepository.save(pelicula1);


        Actor actor1 = new Actor(0, "actor1");
        actorRepository.save(actor1);

        Actor actor2 = new Actor(1, "actor2");
        actorRepository.save(actor2);

        pelicula1.getActores().add(actor1);
        pelicula1.getActores().add(actor2);

        actor1.getPeliculas().add(pelicula1);
        actor2.getPeliculas().add(pelicula1);

        peliculaRepository.save(pelicula1);
        actorRepository.save(actor1);
        actorRepository.save(actor2);
            return  true;
        });




    }
@Test
@Order(3)
    void crearCategoriaPelicula(){
    transactionTemplate.execute(status -> {
        Categoria categoria1 = new Categoria(0, "miedo");
        Categoria categoria2 = new Categoria(0, "suspense");
        categoriaRepository.save(categoria1);
        categoriaRepository.save(categoria2);
        Idioma idioma = idiomaRepository.findById(1L).get();
        Set<Categoria> categoriaSet = new HashSet<>();
        categoriaRepository.save(categoria1);
        categoriaRepository.save(categoria2);
        categoriaSet.add(categoria1);
        categoriaSet.add(categoria2);
        Pelicula pelicula1 = new Pelicula(0, "pelicula1", new HashSet<>(), categoriaSet, idioma);
        peliculaRepository.save(pelicula1);
        Pelicula pelicula2 = new Pelicula(0, "pelicula2", new HashSet<>(), categoriaSet, idioma);
        peliculaRepository.save(pelicula2);
        List<Pelicula> peliculasList = Arrays.asList(pelicula1, pelicula2);

        // Llamada al método para leer las categorías asociadas a las películas
        leerCategoriasDePeliculasAsociadas(peliculasList);
   return true;
    });
    }




    void leerCategoriasDePeliculasAsociadas(List<Pelicula> peliculasList) {
       Map<Categoria, List<Pelicula>> categoriasConPeliculas = peliculasList.stream()
                .flatMap(pelicula -> pelicula.getCategorias().stream()
                        .map(categoria -> new AbstractMap.SimpleEntry<>(categoria, pelicula)))
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

        // Imprimimos las categorías y las películas asociadas
        System.out.println("Categorías de las películas asociadas:");
        categoriasConPeliculas.forEach((categoria, peliculas) -> {
            System.out.println("Categoría: " + categoria.getNombre());
            System.out.println("Películas:");
            peliculas.forEach(pelicula -> System.out.println("- " + pelicula.getTitulo()));
        });}
    @Test
    @Order(5)
    void modificarTablas() {
        Optional<Categoria> categoriaOptional = categoriaRepository.findById(1l);
        if (categoriaOptional.isPresent()) {
            Categoria categoriaModificar = categoriaOptional.get();

            categoriaModificar.setNombre("terror");

            categoriaRepository.save(categoriaModificar);
        }
        Optional<Pelicula> peliculaOptional = peliculaRepository.findById(1l);
        if (peliculaOptional.isPresent()) {
            Pelicula peliculaModificar = peliculaOptional.get();

            peliculaModificar.setTitulo("peliculaModificada");

            peliculaRepository.save(peliculaModificar);
        }
    }
    @Test
    @Order(6)
    void desasociarPeliculaDeIdioma() {

            Optional<Pelicula> peliculaOptional = peliculaRepository.findById(1L);
            if (peliculaOptional.isPresent()) {
                Pelicula pelicula = peliculaOptional.get();
                peliculaRepository.delete(pelicula);
            } else {
                System.out.println("No se encontró la película.");
            }


    }


}
