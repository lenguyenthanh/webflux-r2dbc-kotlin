package net.razvan.poc.springboot.webfluxr2dbckotlin.protocol

import arrow.core.right
import arrow.endpoint.*
import arrow.endpoint.server.ServerEndpoint
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.razvan.poc.springboot.webfluxr2dbckotlin.user.UserService
import net.razvan.poc.springboot.webfluxr2dbckotlin.user.User as UserDTO

val usersBase: EndpointInput<Unit> =
    ArrowEndpoint.fixedPath("api").and(ArrowEndpoint.fixedPath("users"))

@Serializable
data class User(
    val id: Long,
    val name: String,
    val login: String,
    val email: String,
    val avatar: String? = null
) {
    companion object {
        val schema: Schema<User> = Schema.product(
            User::id to Schema.long,
            User::name to Schema.string,
            User::login to Schema.string,
            User::email to Schema.string,
            User::avatar to Schema.string.asNullable(),
        )

        val jsonCodec: JsonCodec<User> = Codec.kotlinxJson(schema)
        val jsonCodecAsList: JsonCodec<List<User>> = Codec.kotlinxJson(schema.asList())
    }
}

private fun UserDTO.transform() =
    User(
        id ?: 0L, name, login, email, avatar
    )

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified A> Codec.Companion.kotlinxJson(schema: Schema<A>): JsonCodec<A> =
    json(schema, { DecodeResult.Value(Json.decodeFromString(it)) }) { Json.encodeToString(it) }

val findAll: Endpoint<Unit, Unit, List<User>> =
    Endpoint.get()
        .input(usersBase)
        .output(
            ArrowEndpoint.anyJsonBody(User.jsonCodecAsList)
                .description("Find all users")
                .default(emptyList())
                .example(emptyList())
        )

val usersEndpoint = listOf(findAll)

//fun UserService.endpoints() = listOf<ServerEndpoint<*, *, *>>(
//    ServerEndpoint(findAll) { findAll().map { it.transform() }.right() }
//)

fun UserService.endpoints() = listOf<ServerEndpoint<*, *, *>>(
    ServerEndpoint(findAll) { listOf(User(1L, "t", "t", "e", "a")).right() }
)
