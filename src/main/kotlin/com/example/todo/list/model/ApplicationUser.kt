package com.example.todo.list.model

import javax.persistence.*

@Entity
@Table(name = "users")
data class ApplicationUser(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,

    val username: String,

    val password: String,

    @ManyToMany(cascade = [CascadeType.ALL])
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_role")],
        inverseJoinColumns = [JoinColumn(name = "id")]
    )
    var roles: Set<UserRole> = emptySet()
) {
    constructor(
        name: String,
        password: String,
    ) : this(
        username = name,
        password = password,
        roles = emptySet<UserRole>()
    )
}