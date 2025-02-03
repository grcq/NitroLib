package dev.grcq.nitrolib.core.tests.database;

import dev.grcq.nitrolib.core.annotations.orm.Column;
import dev.grcq.nitrolib.core.annotations.orm.Entity;
import dev.grcq.nitrolib.core.annotations.orm.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
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
