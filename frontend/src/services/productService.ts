import api from './api'

export interface Product {
  id: string
  name: string
  description: string
  price: number
  category: string
  image?: string
  stock: number
  tenantId: string
  createdAt: string
  updatedAt: string
}

export interface CreateProductRequest {
  name: string
  description: string
  price: number
  category: string
  stock: number
  image?: string
}

export interface UpdateProductRequest extends Partial<CreateProductRequest> {
  id: string
}

export interface ProductFilters {
  category?: string
  minPrice?: number
  maxPrice?: number
  search?: string
  tenantId?: string
}

export const productService = {
  async getProducts(filters?: ProductFilters): Promise<Product[]> {
    const response = await api.get('/products', { params: filters })
    return response.data
  },

  async getProduct(id: string): Promise<Product> {
    const response = await api.get(`/products/${id}`)
    return response.data
  },

  async createProduct(productData: CreateProductRequest): Promise<Product> {
    const response = await api.post('/products', productData)
    return response.data
  },

  async updateProduct(productData: UpdateProductRequest): Promise<Product> {
    const { id, ...data } = productData
    const response = await api.put(`/products/${id}`, data)
    return response.data
  },

  async deleteProduct(id: string): Promise<void> {
    await api.delete(`/products/${id}`)
  },

  async getCategories(): Promise<string[]> {
    const response = await api.get('/products/categories')
    return response.data
  },
}
