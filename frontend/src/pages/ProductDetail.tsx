import { useParams } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { productService } from '../services/productService'
import { useCartStore } from '../stores/cartStore'
import { ShoppingCartIcon, StarIcon } from '@heroicons/react/24/outline'
import { StarIcon as StarIconSolid } from '@heroicons/react/24/solid'
import toast from 'react-hot-toast'

const ProductDetail = () => {
  const { id } = useParams<{ id: string }>()
  const { addItem } = useCartStore()

  const { data: product, isLoading, error } = useQuery({
    queryKey: ['product', id],
    queryFn: () => productService.getProduct(id!),
    enabled: !!id,
  })

  const handleAddToCart = (quantity: number) => {
    if (!product) return

    addItem({
      id: product.id,
      name: product.name,
      price: product.price,
      quantity,
      image: product.image,
      tenantId: product.tenantId,
    })
    toast.success(`Added ${quantity} ${quantity === 1 ? 'item' : 'items'} to cart!`)
  }

  if (isLoading) {
    return (
      <div className="text-center py-12">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto"></div>
        <p className="mt-4 text-gray-600">Loading product...</p>
      </div>
    )
  }

  if (error || !product) {
    return (
      <div className="text-center py-12">
        <p className="text-red-600">Product not found.</p>
      </div>
    )
  }

  return (
    <div className="max-w-7xl mx-auto">
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {/* Product Image */}
        <div>
          {product.image ? (
            <img
              src={product.image}
              alt={product.name}
              className="w-full h-96 object-cover rounded-lg"
            />
          ) : (
            <div className="w-full h-96 bg-gray-200 rounded-lg flex items-center justify-center">
              <span className="text-gray-400">No Image</span>
            </div>
          )}
        </div>

        {/* Product Info */}
        <div className="space-y-6">
          <div>
            <h1 className="text-3xl font-bold text-gray-900 mb-2">{product.name}</h1>
            <div className="flex items-center gap-2 mb-4">
              <div className="flex items-center">
                {[...Array(5)].map((_, i) => (
                  <StarIconSolid
                    key={i}
                    className="h-5 w-5 text-yellow-400"
                  />
                ))}
              </div>
              <span className="text-gray-600">(4.5)</span>
            </div>
            <p className="text-2xl font-bold text-primary-600">${product.price.toFixed(2)}</p>
          </div>

          <div>
            <h3 className="text-lg font-semibold text-gray-900 mb-2">Description</h3>
            <p className="text-gray-600 leading-relaxed">{product.description}</p>
          </div>

          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <span className="text-gray-700">Category:</span>
              <span className="font-medium">{product.category}</span>
            </div>
            <div className="flex items-center justify-between">
              <span className="text-gray-700">Stock:</span>
              <span className={`font-medium ${product.stock > 0 ? 'text-green-600' : 'text-red-600'}`}>
                {product.stock > 0 ? `${product.stock} available` : 'Out of stock'}
              </span>
            </div>
          </div>

          {/* Add to Cart */}
          {product.stock > 0 && (
            <div className="space-y-4">
              <div className="flex items-center gap-4">
                <label htmlFor="quantity" className="text-gray-700 font-medium">
                  Quantity:
                </label>
                <select
                  id="quantity"
                  className="input-field w-20"
                  defaultValue="1"
                  onChange={(e) => {
                    const quantity = parseInt(e.target.value)
                    handleAddToCart(quantity)
                  }}
                >
                  {[...Array(Math.min(10, product.stock))].map((_, i) => (
                    <option key={i + 1} value={i + 1}>
                      {i + 1}
                    </option>
                  ))}
                </select>
              </div>
              <button
                onClick={() => handleAddToCart(1)}
                className="btn-primary w-full flex items-center justify-center gap-2"
              >
                <ShoppingCartIcon className="h-5 w-5" />
                Add to Cart
              </button>
            </div>
          )}

          {product.stock === 0 && (
            <div className="bg-red-50 border border-red-200 rounded-lg p-4">
              <p className="text-red-600 font-medium">This product is currently out of stock.</p>
            </div>
          )}
        </div>
      </div>

      {/* Additional Info */}
      <div className="mt-12">
        <h2 className="text-2xl font-bold text-gray-900 mb-6">Product Details</h2>
        <div className="bg-white rounded-lg border p-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <h3 className="font-semibold text-gray-900 mb-2">Product Information</h3>
              <ul className="space-y-2 text-gray-600">
                <li><span className="font-medium">ID:</span> {product.id}</li>
                <li><span className="font-medium">Category:</span> {product.category}</li>
                <li><span className="font-medium">Price:</span> ${product.price.toFixed(2)}</li>
                <li><span className="font-medium">Stock:</span> {product.stock} units</li>
              </ul>
            </div>
            <div>
              <h3 className="font-semibold text-gray-900 mb-2">Timestamps</h3>
              <ul className="space-y-2 text-gray-600">
                <li><span className="font-medium">Created:</span> {new Date(product.createdAt).toLocaleDateString()}</li>
                <li><span className="font-medium">Updated:</span> {new Date(product.updatedAt).toLocaleDateString()}</li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default ProductDetail
