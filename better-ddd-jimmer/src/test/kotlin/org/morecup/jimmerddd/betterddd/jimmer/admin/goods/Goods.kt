package org.morecup.jimmerddd.betterddd.jimmer.admin.goods

import org.morecup.jimmerddd.betterddd.jimmer.admin.BaseEntity

interface Goods : BaseEntity {
    val name: String
    val nowAddress: String
}