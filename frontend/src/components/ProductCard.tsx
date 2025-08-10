import { Link } from 'react-router-dom'
import {
  Card,
  CardMedia,
  CardContent,
  CardActions,
  Typography,
  Button,
  IconButton,
  Box,
  Chip,
} from '@mui/material'
import { ShoppingCart } from '@mui/icons-material'
import { useCartStore } from '../stores/cartStore'
import { Product } from '../services/productService'
import { useSnackbar } from 'notistack'

interface ProductCardProps {
  product: Product
}

const ProductCard = ({ product }: ProductCardProps) => {
  const { addItem } = useCartStore()
  const { enqueueSnackbar } = useSnackbar()

  const handleAddToCart = () => {
    addItem({
      id: product.id,
      name: product.name,
      price: product.price,
      quantity: 1,
      image: product.image,
      tenantId: product.tenantId,
    })
    enqueueSnackbar('Added to cart!', { variant: 'success' })
  }

  return (
    <Card
      sx={{
        height: '100%',
        display: 'flex',
        flexDirection: 'column',
        transition: 'all 0.3s ease-in-out',
        '&:hover': {
          transform: 'translateY(-4px)',
          boxShadow: 4,
        },
      }}
    >
      <Box sx={{ position: 'relative' }}>
        {product.image ? (
          <CardMedia
            component={Link}
            to={`/products/${product.id}`}
            sx={{
              height: 200,
              display: 'block',
              textDecoration: 'none',
              cursor: 'pointer',
            }}
            image={product.image}
            title={product.name}
          />
        ) : (
          <Box
            component={Link}
            to={`/products/${product.id}`}
            sx={{
              height: 200,
              bgcolor: 'grey.200',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              textDecoration: 'none',
              cursor: 'pointer',
            }}
          >
            <Typography color="text.secondary">No Image</Typography>
          </Box>
        )}
        {product.stock === 0 && (
          <Chip
            label="Out of stock"
            color="error"
            size="small"
            sx={{
              position: 'absolute',
              top: 8,
              right: 8,
            }}
          />
        )}
      </Box>

      <CardContent sx={{ flexGrow: 1, pb: 1 }}>
        <Typography
          variant="h6"
          component={Link}
          to={`/products/${product.id}`}
          sx={{
            textDecoration: 'none',
            color: 'text.primary',
            fontWeight: 600,
            mb: 1,
            display: 'block',
            '&:hover': {
              color: 'primary.main',
            },
          }}
        >
          {product.name}
        </Typography>
        <Typography
          variant="body2"
          color="text.secondary"
          sx={{
            mb: 2,
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            display: '-webkit-box',
            WebkitLineClamp: 2,
            WebkitBoxOrient: 'vertical',
          }}
        >
          {product.description}
        </Typography>
        <Typography
          variant="h6"
          color="primary.main"
          fontWeight={700}
        >
          ${product.price.toFixed(2)}
        </Typography>
      </CardContent>

      <CardActions sx={{ justifyContent: 'space-between', px: 2, pb: 2 }}>
        <Button
          component={Link}
          to={`/products/${product.id}`}
          variant="outlined"
          size="small"
        >
          View Details
        </Button>
        <IconButton
          onClick={handleAddToCart}
          disabled={product.stock === 0}
          color="primary"
          sx={{
            bgcolor: product.stock === 0 ? 'grey.300' : 'primary.main',
            color: 'white',
            '&:hover': {
              bgcolor: product.stock === 0 ? 'grey.300' : 'primary.dark',
            },
            '&:disabled': {
              bgcolor: 'grey.300',
              color: 'grey.500',
            },
          }}
        >
          <ShoppingCart />
        </IconButton>
      </CardActions>
    </Card>
  )
}

export default ProductCard
