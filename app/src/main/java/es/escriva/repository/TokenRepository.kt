package es.escriva.repository

import es.escriva.dao.TokenDao
import es.escriva.domain.Token

class TokenRepository(private val tokenDao: TokenDao) {

    fun insert(token: Token): Long {
        return tokenDao.insert(token)
    }
}