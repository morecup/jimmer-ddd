package org.morecup.jimmerddd.betterddd.admin.goods

import org.morecup.jimmerddd.betterddd.admin.BaseEntity

interface Goods : BaseEntity {
    val name: String
    val nowAddress: String
}