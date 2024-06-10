package es.escriva.repository

import es.escriva.dao.TokenDao
import es.escriva.domain.Token

class TokenRepository(private val tokenDao: TokenDao) {

    fun upsert(token: Token): Long {
        return tokenDao.upsert(token)
    }

    fun findById(tokenId: Long): Token {
        return tokenDao.findById(tokenId)
    }

}