package org.iesvdm.tutorial;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.iesvdm.tutorial.domain.Post;
import org.iesvdm.tutorial.domain.Tag;
import org.iesvdm.tutorial.repository.PostRepository;
import org.iesvdm.tutorial.repository.TagRepository;
import org.iesvdm.tutorial.util.UtilJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.HashSet;

@SpringBootTest
public class TagPostTests {

    @Autowired
    PostRepository postRepository;
    @Autowired
    TagRepository tagRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private PlatformTransactionManager transactionManager;
    private TransactionTemplate transactionTemplate;

    @BeforeEach
    public void setUp() {
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Test
    @Order(1)
    void grabarPorPropietarioDeManyToMany() {

        Tag tag1 = new Tag(null, "Programación", new HashSet<>());
        Tag tag2 = new Tag(null, "Base de datos", new HashSet<>());

        Post post1 = new Post(null, "Post1 - Programando fácil con JPA :P", new HashSet<>());

        post1.addTag(tag1);
        // RECUERDA QUE LA COLECCIÓN DE TAGS ES UN SET Y NO PUEDE HABER REPETIDOS CON EL MISMO ID
        postRepository.save(post1);

        post1.addTag(tag2);
        postRepository.save(post1);
    }

    @Test
    @Order(2)
    void grabarTagQueYaExiste() {

        Tag tag3 = new Tag(null, "EEEH Tag 3!!!!", new HashSet<>());
        tagRepository.save(tag3);

        Post post2 = new Post(null, "Post2 - NO programando tan fácilmente...", new HashSet<>());
        postRepository.save(post2);

        // Si utilizas un fetch LAZY, mejor estrategia realizar un join fetch en JPQL
        // y cargar en la colección. NOTA: si utilizas EAGER puedes prescindir de join fetch.
        UtilJPA.initializeLazyManyToManyByJoinFetch(entityManager,
                Tag.class,
                Post.class,
                post2.getId(),
                post2::setTags
        );

        Tag tag1 = tagRepository.findById(1L).orElse(null);

    }}