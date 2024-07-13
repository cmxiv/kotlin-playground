package dev.tandosid.playground.kotlin

import dev.tandosid.playground.kotlin.EngineType.FOUR_STROKE
import dev.tandosid.playground.kotlin.EngineType.TWO_STROKE
import dev.tandosid.playground.kotlin.VehicleType.BIKE
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import java.time.Clock
import java.time.Year
import kotlin.random.Random

fun main() {
    println("Hello, Polymorphic Serialization!")

    val twoStrokeBikeJson = createRandomVehicleResponse(BIKE, TWO_STROKE)
    println(twoStrokeBikeJson)

    val fourStrokeBikeJson = createRandomVehicleResponse(BIKE, FOUR_STROKE)
    println(fourStrokeBikeJson)
}

fun createRandomVehicleResponse(vehicleType: VehicleType, engineType: EngineType): String {
    val engine = when(engineType) {
        TWO_STROKE -> TwoStroke
        FOUR_STROKE -> FourStroke
    }

    val vehicle = when (vehicleType) {
        BIKE -> Bike(
            engine = engine,
            mileage = Random.nextLong(10, 1000),
            yearOfManufacturing = Year.of(Random.nextInt(1999, Year.now(Clock.systemUTC()).value))
        )
    }

    return Json.encodeToString(vehicle)
}

enum class EngineType {
    TWO_STROKE,
    FOUR_STROKE
}

@Serializable
sealed class Engine(val stokes: Int)

@Serializable
@SerialName("two-stroke")
data object TwoStroke: Engine(2)

@Serializable
@SerialName("four-stroke")
data object FourStroke: Engine(4)


enum class VehicleType {
    BIKE
}

@Serializable
sealed interface Vehicle {
    val engine: Engine
}

@Serializable
@SerialName("bike")
data class Bike(
    val mileage: Long,
    override val engine: Engine,
    @Serializable(YearSerializer::class)
    val yearOfManufacturing: Year,
): Vehicle

class YearSerializer: KSerializer<Year> {
    override val descriptor = PrimitiveSerialDescriptor("year", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): Year = Year.of(decoder.decodeInt())

    override fun serialize(encoder: Encoder, value: Year) {
        encoder.encodeInt(value.value)
    }
}