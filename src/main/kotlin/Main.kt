package dev.tandosid.playground.kotlin

import dev.tandosid.playground.kotlin.EngineTypeDto.FOUR_STROKE
import dev.tandosid.playground.kotlin.EngineTypeDto.TWO_STROKE
import dev.tandosid.playground.kotlin.VehicleTypeDto.BIKE
import dev.tandosid.playground.kotlin.VehicleTypeDto.CAR
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

fun createRandomVehicleResponse(vehicleType: VehicleTypeDto, engineType: EngineTypeDto): String {
    val engine = when(engineType) {
        TWO_STROKE -> TwoStroke
        FOUR_STROKE -> FourStroke
    }

    fun mileage() = Random.nextLong(10, 1000)
    fun yearOfManufacturing() = Year.of(Random.nextInt(1999, Year.now(Clock.systemUTC()).value))
    val vehicle = when (vehicleType) {
        BIKE -> Bike(
            engine = engine,
            mileage = mileage(),
            yearOfManufacturing = yearOfManufacturing()
        )

        CAR -> Car(
            engine = engine,
            mileage = mileage(),
            yearOfManufacturing = yearOfManufacturing()
        )
    }

    // This casting is required for polymorphic encoding behaviour to work properly
    return Json.encodeToString(vehicle as Vehicle)
}

enum class EngineTypeDto {
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


enum class VehicleTypeDto {
    BIKE,
    CAR
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

@Serializable
@SerialName("car")
data class Car(
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