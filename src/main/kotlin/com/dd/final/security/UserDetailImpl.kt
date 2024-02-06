package com.dd.final.security

import com.dd.final.model.Task
import com.dd.final.model.User
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserDetailImpl(val user: User) : UserDetails {

    override fun getAuthorities(): MutableCollection<out SimpleGrantedAuthority> {
        return user.roles.map { SimpleGrantedAuthority(it.name) }.toMutableList()
    }

    fun getId(): Long? {
        return user.id
    }

    fun getTask(): MutableList<Task?> {
        return user.tasks
    }

    override fun getPassword(): String {
        return user.password
    }

    override fun getUsername(): String {
        return user.username
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return user.enabled
    }

}