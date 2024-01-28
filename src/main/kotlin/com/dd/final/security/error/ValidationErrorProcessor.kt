package com.dd.final.security.error

import org.springframework.stereotype.Service
import org.springframework.validation.BindingResult

@Service
class ValidationErrorProcessor {
    fun process(result: BindingResult): String {
        val sb = StringBuilder()
        result.fieldErrors.forEach { sb.append("${it.field} ${it.defaultMessage} ,") }
        return sb.removeSuffix(",").toString()
    }
}