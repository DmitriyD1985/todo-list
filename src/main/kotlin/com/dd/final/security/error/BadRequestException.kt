package com.dd.final.security.error

import java.lang.RuntimeException

class BadRequestException(override val message:String) : RuntimeException()

class NotAnyRoleExist(override val message:String) : RuntimeException()