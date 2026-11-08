package com.example.restcontrollerdemo.repository

import com.example.restcontrollerdemo.domain.entity.Enterprise
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface EnterpriseRepository : CoroutineCrudRepository<Enterprise, Long>
