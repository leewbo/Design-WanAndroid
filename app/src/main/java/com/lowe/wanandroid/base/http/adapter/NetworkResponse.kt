package com.lowe.wanandroid.base.http.adapter

import com.lowe.wanandroid.base.http.exception.ApiException

/**
 * 接口的返回类型包装类
 */
sealed class NetworkResponse<out T: Any> {
    /**
     * 成功
     */
    data class Success<T: Any>(val data: T) : NetworkResponse<T>()

    /**
     * 业务错误
     */
    data class BizError(val errorCode: Int = 0, val errorMessage: String = "") :
        NetworkResponse<Nothing>()

    /**
     * 其他错误
     */
    data class UnknownError(val throwable: Throwable) : NetworkResponse<Nothing>()
}

inline val NetworkResponse<*>.isSuccess: Boolean
    get() {
        return this is NetworkResponse.Success
    }

fun <T : Any> NetworkResponse<T>.getOrNull(): T? =
    when (this) {
        is NetworkResponse.Success -> data
        is NetworkResponse.BizError -> null
        is NetworkResponse.UnknownError -> null
    }

fun <T : Any> NetworkResponse<T>.getOrThrow(): T =
    when (this) {
        is NetworkResponse.Success -> data
        is NetworkResponse.BizError -> throw ApiException(errorCode, errorMessage)
        is NetworkResponse.UnknownError -> throw throwable
    }

inline fun <T : Any> NetworkResponse<T>.getOrElse(default: (NetworkResponse<T>) -> T): T =
    when (this) {
        is NetworkResponse.Success -> data
        else -> default(this)
    }

inline fun <T : Any> NetworkResponse<T>.whenSuccess(
    block: (NetworkResponse.Success<T>) -> Unit
) {
    (this as? NetworkResponse.Success)?.also(block)
}

inline fun <T : Any> NetworkResponse<T>.guardSuccess(
    block: () -> Nothing
): T {
    if (this !is NetworkResponse.Success) {
        block()
    }
    return this.data
}