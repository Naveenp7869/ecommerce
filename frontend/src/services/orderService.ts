import api from './api'

export interface OrderItem {
  productId: string
  productName: string
  quantity: number
  price: number
}

export interface Order {
  id: string
  userId: string
  tenantId: string
  items: OrderItem[]
  total: number
  status: 'PENDING' | 'CONFIRMED' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED'
  shippingAddress: {
    street: string
    city: string
    state: string
    zipCode: string
    country: string
  }
  createdAt: string
  updatedAt: string
}

export interface CreateOrderRequest {
  items: OrderItem[]
  shippingAddress: {
    street: string
    city: string
    state: string
    zipCode: string
    country: string
  }
}

export const orderService = {
  async getOrders(): Promise<Order[]> {
    const response = await api.get('/orders')
    return response.data
  },

  async getOrder(id: string): Promise<Order> {
    const response = await api.get(`/orders/${id}`)
    return response.data
  },

  async createOrder(orderData: CreateOrderRequest): Promise<Order> {
    const response = await api.post('/orders', orderData)
    return response.data
  },

  async updateOrderStatus(id: string, status: Order['status']): Promise<Order> {
    const response = await api.put(`/orders/${id}/status`, { status })
    return response.data
  },

  async cancelOrder(id: string): Promise<void> {
    await api.put(`/orders/${id}/cancel`)
  },
}
