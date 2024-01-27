//package com.example.todo.list.model
//
//import org.springframework.security.core.GrantedAuthority
//import org.springframework.security.core.userdetails.UserDetails
//
//data class UserDetailsImpl(
//    val id: Long,
//    val name: String,
//    val email: String,
//    val password: String,
//    val authorities: Set<GrantedAuthority>,
//): UserDetails {
//    override fun getName(): String = name
//    override fun getPassword(): String = password
//    override fun isEnabled(): Boolean = isEnabled
//    override fun isCredentialsNonExpired(): Boolean = isCredentialsNonExpired
//    override fun isAccountNonExpired(): Boolean = isAccountNonExpired
//    override fun isAccountNonLocked(): Boolean = isAccountNonLocked
//    override fun getAuthorities(): Set<out GrantedAuthority> = authorities
//
//    constructor(user: ApplicationUser): this(
//        user.id,
//        user.name,
//        user.email,
//        user.password,
//        user.roles.map { it.name as GrantedAuthority}.toSet()
//    )
//}
