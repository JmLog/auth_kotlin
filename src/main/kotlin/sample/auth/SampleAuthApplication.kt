package sample.auth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SampleAuthApplication

fun main(args: Array<String>) {
    runApplication<SampleAuthApplication>(*args)
}
