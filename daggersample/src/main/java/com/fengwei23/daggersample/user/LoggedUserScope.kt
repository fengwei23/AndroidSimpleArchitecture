package com.fengwei23.daggersample.user

import javax.inject.Scope
import kotlin.annotation.AnnotationRetention.RUNTIME

/**
 * @author w.feng
 * @date 2020/7/31
 */
@Scope
@MustBeDocumented
@Retention(value = RUNTIME)
annotation class LoggedUserScope