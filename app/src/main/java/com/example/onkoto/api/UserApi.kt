package com.example.onkoto.api

import com.example.onkoto.model.UserDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {
    @POST("http://10.0.2.2:8082/user") // Substitua pelo endpoint correto
    fun createUser(@Body userDto: UserDto): Call<Void>
}