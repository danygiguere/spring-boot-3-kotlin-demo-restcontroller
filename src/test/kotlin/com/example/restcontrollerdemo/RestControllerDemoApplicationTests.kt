package com.example.restcontrollerdemo

import com.example.restcontrollerdemo.shared.config.SharedTestContainers
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class RestControllerDemoApplicationTests : SharedTestContainers() {
    @Test
    fun contextLoads() {
    }
}
