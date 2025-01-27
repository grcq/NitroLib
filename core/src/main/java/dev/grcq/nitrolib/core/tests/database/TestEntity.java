package dev.grcq.nitrolib.core.tests.database;

import dev.grcq.nitrolib.core.annotations.orm.Column;
import dev.grcq.nitrolib.core.annotations.orm.Entity;
import dev.grcq.nitrolib.core.annotations.orm.Id;

@Entity
public class TestEntity {

    @Id
    @Column
    private int id;

    @Column
    private String name;

    @Column
    private int age;

    @Column
    private boolean active;

    @Column
    private double balance;

}
