package minek.netty

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation

@Target(AnnotationTarget.PROPERTY)
@Retention
@MustBeDocumented
annotation class BufferSize(
    /**
     * -1 이면 dynamic
     */
    val byteLength: Int = -1
)

data class Pojo(
    val x: Int,
    @BufferSize
    val y: String,
    @BufferSize(byteLength = 5)
    val z: String
)

data class Pojo2(val x: Int, val subPojo: SubPojo)

data class SubPojo(val a: Int, val b: Int)

val stringType = String::class.createType()
val intType = Int::class.createType()

fun ByteBuf.writeString(value: String, length: Int) {
    val source = value.toByteArray()
    if (length == -1) {
        writeBytes(source)
    } else {
        val bytes = ByteArray(length)
        System.arraycopy(source, 0, bytes, 0, length)
        writeBytes(bytes)
    }
}

fun pojoToButeBuf(value: Any): ByteBuf {
    val buffer = Unpooled.buffer()
    for (declaredMemberProperty in value::class.declaredMemberProperties) {
        val get = declaredMemberProperty.getter.call(value)!!
        when (declaredMemberProperty.returnType) {
            stringType -> {
                val byteLength = declaredMemberProperty.findAnnotation<BufferSize>()?.byteLength ?: -1
                buffer.writeString(get as String, byteLength)
            }
            intType -> {
                buffer.writeInt(get as Int)
            }
            else -> throw UnsupportedOperationException()
        }
    }
    return buffer
}

@ExperimentalStdlibApi
@UseExperimental(ExperimentalUnsignedTypes::class)
fun main() {
    val pojo2 = Pojo2(1, SubPojo(1, 1))
    val pojoToButeBuf = pojoToButeBuf(pojo2)
    println(pojoToButeBuf)

//    val pojo = Pojo("429496729".toInt(), "1", "1234567890")
//    val pojoToButeBuf1= pojoToButeBuf(pojo)
//    val pojoToButeBuf2 = pojoToButeBuf(pojo)
//    println(pojoToButeBuf1)
//    val writeBytes = pojoToButeBuf1.writeBytes(pojoToButeBuf2)
//    println(writeBytes)
}
