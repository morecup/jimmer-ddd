package org.morecup.jimmerddd.core.domain

import org.morecup.jimmerddd.core.domain.goods.GoodsFactory
import org.morecup.jimmerddd.core.domain.goods.GoodsRepository
import org.springframework.context.ApplicationContext

object DomainRegistry {
    lateinit var spring: ApplicationContext

    fun goodsRepository():GoodsRepository = spring.getBean(GoodsRepository::class.java)
    fun goodsFactory():GoodsFactory = spring.getBean(GoodsFactory::class.java)
}