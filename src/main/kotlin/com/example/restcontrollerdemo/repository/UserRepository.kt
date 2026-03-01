package com.example.restcontrollerdemo.repository

import com.example.restcontrollerdemo.domain.entity.User
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserRepository : CoroutineCrudRepository<User, Long>
