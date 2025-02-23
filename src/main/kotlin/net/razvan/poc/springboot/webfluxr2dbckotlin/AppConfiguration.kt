package net.razvan.poc.springboot.webfluxr2dbckotlin

import arrow.endpoint.spring.server.routerFunction
import io.r2dbc.spi.ConnectionFactory
import net.razvan.poc.springboot.webfluxr2dbckotlin.protocol.endpoints
import net.razvan.poc.springboot.webfluxr2dbckotlin.protocol.usersEndpoint
import net.razvan.poc.springboot.webfluxr2dbckotlin.user.UserHandler
import net.razvan.poc.springboot.webfluxr2dbckotlin.user.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator
import org.springframework.web.reactive.function.server.coRouter


@Configuration
@EnableR2dbcRepositories
class AppConfiguration {
    @Bean
    fun userRoute(userHandler: UserHandler) = coRouter {
        GET("/users", userHandler::findAll)
        GET("/users/search", userHandler::search)
        GET("/users/{id}", userHandler::findUser)
        POST("/users", userHandler::addUser)
        PUT("/users/{id}", userHandler::updateUser)
        DELETE("/users/{id}", userHandler::deleteUser)
    }

    @Bean
    fun usersRoute(service: UserService) = routerFunction(
        service.endpoints()
    )

    @Bean
    fun initializer(connectionFactory: ConnectionFactory): ConnectionFactoryInitializer {
        val initializer = ConnectionFactoryInitializer()
        initializer.setConnectionFactory(connectionFactory)
        val populator = CompositeDatabasePopulator()
        populator.addPopulators(ResourceDatabasePopulator(ClassPathResource("schema.sql")))
        populator.addPopulators(ResourceDatabasePopulator(ClassPathResource("data.sql")))
        initializer.setDatabasePopulator(populator)
        return initializer
    }
}