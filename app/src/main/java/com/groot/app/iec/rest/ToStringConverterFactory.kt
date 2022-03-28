package com.groot.app.iec.rest

import retrofit2.Retrofit
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import retrofit2.Converter
import java.lang.reflect.Type

class ToStringConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        return if (String::class.java == type) {
            Converter { value -> value.string() }
        } else null
    }

    override fun stringConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, String>? {
        return if (String::class.java == type) {
            Converter<String, String> { value -> value }
        } else null
    }
}