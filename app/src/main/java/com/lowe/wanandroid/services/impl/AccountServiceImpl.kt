package com.lowe.wanandroid.services.impl

import com.lowe.wanandroid.base.http.RetrofitManager
import com.lowe.wanandroid.services.AccountService
import javax.inject.Inject

class AccountServiceImpl @Inject constructor() : AccountService {

    private val service by lazy { RetrofitManager.getService(AccountService::class.java) }

    override suspend fun login(username: String, password: String) =
        service.login(username, password)

    override suspend fun logout() = service.logout()

    override suspend fun register(
        username: String,
        password: String,
        confirmPassword: String
    ) = service.register(username, password, confirmPassword)

    override suspend fun getUserInfo() = service.getUserInfo()
}