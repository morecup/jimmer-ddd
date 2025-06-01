package org.morecup.jimmerddd.core.domain.order

//import org.morecup.jimmerddd.core.domain.order.dto.CreateOrderCmd
//import org.springframework.stereotype.Service
//
//@Service
//class CreateOrderCmdHandle(
//    private val orderRepository: OrderRepository,
//    private val orderFactory: OrderFactory,
//) {
//    fun handle(command: CreateOrderCmd): Long {
//        val order = orderFactory.create(command)
//        val updatedOrder = orderRepository.saveOrder(order)
//        return updatedOrder.id
//    }
//}