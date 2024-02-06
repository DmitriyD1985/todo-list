package com.dd.final.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.Size

@Entity
@Table(name = "user")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(unique = true)
    @Size(min = 2, max = 20)
    val username: String,
    @JsonIgnore
    @Size(min = 2, max = 20)
    var password: String,
    val enabled : Boolean = true,
    val createdAt: LocalDateTime? = LocalDateTime.now(),
    val updatedAt: LocalDateTime? = LocalDateTime.now(),
    var token :String? = null,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "task_id")
    var tasks: MutableList<Task?> = arrayListOf(),

    @Fetch(FetchMode.JOIN)
    @Column var roles: HashSet<Role> = HashSet()
) {
    fun isAdmin(): Boolean {
        return roles.contains(Role.ROLE_ADMIN)
    }

}
