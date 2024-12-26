package com.example.onkoto.service

import android.util.Log
import com.example.onkoto.api.UserApi
import com.example.onkoto.model.UserDto
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class UserService () {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://example.com/api/") // Substitua pela URL base da sua API
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val userApi: UserApi = retrofit.create(UserApi::class.java)

    fun sendUser(userDto: UserDto) {
        val call: Call<Void> = userApi.createUser(userDto)

        call.enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(call: Call<Void>, response: retrofit2.Response<Void>) {
                if (response.isSuccessful) {
                    println("User successfully sent: ${userDto.name}")
                } else {
                    println("Failed to send user. Code: ${response.code()}")
                    Log.e("Erro:","1 - Failed to send user. Code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                println("Error sending user: ${t.message}")
                Log.e("Erro:","2 - Error sending user: ${t.message}")
            }
        })
    }
}